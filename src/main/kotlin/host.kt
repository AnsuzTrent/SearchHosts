import com.gargoylesoftware.htmlunit.BrowserVersion
import com.gargoylesoftware.htmlunit.WebClient
import com.gargoylesoftware.htmlunit.html.HtmlPage
import org.apache.commons.logging.LogFactory
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import java.awt.BorderLayout
import java.awt.Desktop
import java.awt.GridLayout
import java.awt.event.ActionEvent
import java.awt.event.ActionListener
import java.io.File
import java.io.IOException
import java.nio.file.Files
import java.util.*
import java.util.concurrent.Executors
import java.util.logging.Level
import javax.swing.*
import javax.swing.filechooser.FileSystemView

fun main() {
//	menu()
	GUI()
}

class GUI internal constructor() : JFrame(), ActionListener {
	private val hosts = JTextField()
	private val search = JButton("搜索")
	private val textA = JTextArea("请选择功能")
	private val backupHosts = JButton("备份")
	private val updateHosts = JButton("更新")

	private val etcPath = "C:\\Windows\\System32\\drivers\\etc"
	private val hostsPath = File("$etcPath\\hosts")
	private val editFile = File("${FileSystemView.getFileSystemView().homeDirectory.path}\\hosts")
	private val local = Vector<String>()

	init {
		//顶栏
		val append = JPanel()
		append.layout = GridLayout(1, 2)
		append.add(hosts)
		search.addActionListener(this)
		append.add(search)
		add(append, BorderLayout.NORTH)

		//中栏
		val recode = JPanel()
		recode.layout = GridLayout(1, 1)
		textA.isEditable = false//设置只读
		textA.lineWrap = true//设置自动换行
		recode.add(JScrollPane(textA))//创建滚动窗格
		add(recode, BorderLayout.CENTER)

		//底栏
		val backup = JPanel()
		backup.layout = GridLayout(1, 2)
		backupHosts.addActionListener(this)
		backup.add(backupHosts)
		updateHosts.addActionListener(this)
		backup.add(updateHosts)
		add(backup, BorderLayout.SOUTH)

		title = "test"
		setSize(500, 500)        //大小
		isResizable = false    //是否可改变大小
		setLocationRelativeTo(null)        //出现位置居中
//		setLocation(1200, 200)
		defaultCloseOperation = EXIT_ON_CLOSE    //关闭窗口按钮
		isVisible = true    //是否可见


		if (!System.getProperty("os.name").contains("indows")) {
			textA.text = "目前仅支持Windows 2000/XP 及以上版本"
			setButtonStatus(false, search, updateHosts, backupHosts)
		}
		//听说其在Win98,win me 中位于/Windows 下？

	}

	private fun setButtonStatus(f: Boolean, vararg buttons: JButton) {
		for (button in buttons)
			button.isEnabled = f
	}

	override fun actionPerformed(e: ActionEvent) {
		textA.text = ""
		when {
			e.source === search -> {
				setButtonStatus(false, backupHosts, updateHosts)
				appendNew(hosts.text)
				setButtonStatus(true, backupHosts, updateHosts)
			}
			e.source === backupHosts -> {
				setButtonStatus(false, search, updateHosts)
				backup()
				setButtonStatus(true, search, updateHosts)
			}
			else -> {
				setButtonStatus(false, search, backupHosts)
				update()
				setButtonStatus(true, search, backupHosts)
			}
		}
	}

	private fun backup(): Boolean {
		try {
			//备份hosts
			val backup = File("$editFile.bak")
			if (backup.exists())
				Files.delete(backup.toPath())
			Files.copy(hostsPath.toPath(), backup.toPath())
			textA.append("已备份hosts 文件至  ：  " + backup.toPath())
			return true
		} catch (e: IOException) {
			textA.append("\n\n${e.message}\n\n")
		}
		return false
	}

	private fun appendNew(str: String) {
		if (str == "") {
			textA.append("请在搜索栏中写入网址")
			return
		}
		if (editFile.exists())
			Files.delete(editFile.toPath())
		Files.copy(hostsPath.toPath(), editFile.toPath())
		val recode = readPage(str)
		if (!recode.isEmpty() && backup()) {
			append(recode)
			textA.append("\n 完成")
			openEtc()
		}
	}

	private fun update() {
		val urls = Objects.requireNonNull<Vector<String>>(readHosts())
		if (!urls.isEmpty() && backup()) {
			val fileWriter = editFile
			fileWriter.writeText(proString())
			if (local.size > 0)
				for (s in local)
					fileWriter.writeText(s)

			//设定线程池
			val pool = Executors.newFixedThreadPool(8)
			for (i in urls)
				pool.execute(Thread { append(readPage(i)) })
			pool.shutdown()
			while (true)
				if (pool.isTerminated)
					break
			textA.append("\n完成")
			openEtc()
			//移动，但目前不能获取管理员权限写入C 盘
//			Files.move(bak1.toPath(), hosts.toPath());
		}
	}

	private val openEtc: () -> Unit = { Desktop.getDesktop().open(File(etcPath)) }

	private fun append(recode: Vector<String>) {
		val fileWriter = editFile
		for (i in recode) {
			textA.append(i)
			fileWriter.appendText(i)
		}
	}

	private fun readPage(url: String): Vector<String> {
		val aimURL = "http://tool.chinaz.com/dns?type=1&host=$url&ip="
		//设置代理
//		System.setProperty("http.proxyHost", "127.0.0.1")
//		System.setProperty("http.proxyPort", "8090")

		val recode = Vector<String>()

		try {
			val doc = getDocumentFromPage(aimURL)

			val host = doc.getElementById("host").attr("value")

			val ipTmp =
				doc.getElementsByClass("w60-0 tl").text().split("\\[.*?]".toRegex()).dropLastWhile { it.isEmpty() }
					.toTypedArray()
			val ip = arrayOfNulls<String>(ipTmp.size)
			var i = 0
			var j = 0
			while (i < ipTmp.size) {
				ipTmp[i] = ipTmp[i].replace("([ \\-]|\\.\\.+)".toRegex(), "")
				if (ipTmp[i] == "") {
					i++
					continue
				}
				ip[j++] = ipTmp[i++]
			}

			for (s in ip)
				if (s != null)
					if ((recode.indexOf("\n$s $host") == -1) and (s != "-"))
						recode.addElement("\n$s $host")

			recode.sort()
		} catch (e: Exception) {
			textA.append("\nError in \n${e.message}\n")
		}
		if (!recode.isEmpty())
			recode.addElement("\n")
		else
			textA.append("输入的网址没有找到对应ip\n")
		return recode
	}

	private fun getDocumentFromPage(url: String): Document {
		//不打印日志
		LogFactory.getFactory()
			.setAttribute("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.NoOpLog")
		java.util.logging.Logger.getLogger("com.gargoylesoftware").level = Level.OFF
		java.util.logging.Logger.getLogger("org.apache.http.client").level = Level.OFF

		//模拟Chrome
		val webClient = WebClient(BrowserVersion.CHROME)
		val webClientOptions = webClient.options

		//禁用CSS
		webClientOptions.isCssEnabled = false
		//启用JS 解释器
		webClientOptions.isJavaScriptEnabled = true
		//JS 错误时不抛出异常
		webClientOptions.isThrowExceptionOnScriptError = false
		webClientOptions.isThrowExceptionOnFailingStatusCode = false
		//连接超时时间
		webClientOptions.timeout = 2 * 1000

		val page = webClient.getPage<HtmlPage>(url)
		//等待后台运行
		webClient.waitForBackgroundJavaScript((10 * 1000).toLong())

		return Jsoup.parse(page.asXml(), url)
	}

	private fun readHosts(): Vector<String>? {
		val recode = Vector<String>()

		val fileReader = File("$etcPath\\hosts").bufferedReader()
		var s = fileReader.readLine()
		//逐行读取文件记录
		while (s != null) {
			//过滤# 开头的注释以及空行
			if (s.startsWith("#") || s == "") {
				if (s.startsWith("127.0.0.1"))
					local.addElement(s)
				s = fileReader.readLine()
				continue
			}
			//以空格作为分割点
			val fromFile = s.split(" ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
			//过滤重复
			if (recode.indexOf(fromFile[1]) == -1)
				recode.addElement(fromFile[1])
			s = fileReader.readLine()
		}
		if (local.size > 0)
			local.addElement("\n")
		fileReader.close()
		recode.sort()
		return if (recode.isEmpty()) null else recode
	}

	private val proString: () -> String = {
		"""
# Copyright (c) 1993-2009 Microsoft Corp.
#
# This is getDocumentFromPage sample HOSTS file used by Microsoft TCP/IP for Windows.
#
# This file contains the mappings of IP addresses to host names. Each
# entry should be kept on an individual line. The IP address should
# be placed in the first column followed by the corresponding host name.
# The IP address and the host name should be separated by at least one
# space.
#
# Additionally, comments (such as these) may be inserted on individual
# lines or following the machine name denoted by getDocumentFromPage '#' symbol.
#
# For example:
#
#      102.54.94.97     rhino.acme.com          # source server
#       38.25.63.10     x.acme.com              # x client host

# localhost name resolution is handled within DNS itself.
#	127.0.0.1       localhost
#	::1             localhost

	""".trimIndent()
	}

	fun menu() {
		var flag = true
		//hosts 备份位于桌面
		while (flag) {
			flag = false
			println("1 更新hosts\n" + "2 新增URL\n" + "3 备份hosts\t" + "输入quit 退出")
			when (val s = readLine()) {
				"1" -> {
					update()
				}
				"2" -> {
					println("Input the URL:")
					readLine()?.let { appendNew(it) }
				}
				"3" -> flag = backup()
				else -> {
					if (!s.equals("quit")) {
						println("请重试")
						flag = true
					}
				}
			}
		}
	}

}

import com.gargoylesoftware.htmlunit.BrowserVersion
import com.gargoylesoftware.htmlunit.WebClient
import com.gargoylesoftware.htmlunit.html.HtmlPage
import org.apache.commons.logging.LogFactory
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import java.awt.BorderLayout
import java.awt.Desktop
import java.awt.EventQueue
import java.awt.GridLayout
import java.io.File
import java.io.IOException
import java.nio.file.Files
import java.util.*
import java.util.concurrent.Executors
import java.util.logging.Level
import javax.swing.*
import javax.swing.filechooser.FileSystemView

fun main() {
	EventQueue.invokeLater { GUI() }
}

class GUI : JFrame() {
	private val hosts = JTextField()
	private val search = JButton("搜索")
	private val textA = JTextArea("请选择功能\n")
	private val backupHosts = JButton("备份")
	private val updateHosts = JButton("更新")
	private val openFolder = JButton("打开hosts 所在文件夹")
	private var scrollBar: JScrollBar

	private val etcPath = "C:\\Windows\\System32\\drivers\\etc"
	private val hostsPath = File("$etcPath\\hosts")
	private val editFile = File("${FileSystemView.getFileSystemView().homeDirectory.path}\\hosts")
	private val local = Vector<String>()

	init {
		//顶栏
		val append = JPanel()
		append.layout = GridLayout(1, 2)
		append.add(hosts)
		search.addActionListener { Search().execute() }
		append.add(search)
		add(append, BorderLayout.NORTH)

		//中栏
		val recode = JPanel()
		recode.layout = GridLayout(1, 1)
		textA.isEditable = false//设置只读
		textA.lineWrap = true//设置自动换行
		val scrollPane = JScrollPane(textA)
		scrollBar = scrollPane.verticalScrollBar
		recode.add(scrollPane)//创建滚动窗格
		add(recode, BorderLayout.CENTER)

		//底栏
		val backup = JPanel()
		backup.layout = GridLayout(1, 3)
		backupHosts.addActionListener { backup() }
		backup.add(backupHosts)
		updateHosts.addActionListener { Update().execute() }
		backup.add(updateHosts)
		openFolder.addActionListener { openEtc() }
		backup.add(openFolder)
		add(backup, BorderLayout.SOUTH)

		title = "Search Hosts in Website"
		setSize(500, 500)        //大小
		isResizable = false    //是否可改变大小
		setLocationRelativeTo(null)        //出现位置居中
//		setLocation(1200, 200)
		defaultCloseOperation = EXIT_ON_CLOSE    //关闭窗口按钮
		isVisible = true    //是否可见

		if (!System.getProperty("os.name").contains("indows")) {
			textA.text = "目前仅支持Windows 2000/XP 及以上版本"
			setButtonStatus(false)
			openFolder.isEnabled = false
		}
		//听说其在Win98,win me 中位于/Windows 下？

	}

	private fun setButtonStatus(flag: Boolean) {
		backupHosts.isEnabled = flag
		updateHosts.isEnabled = flag
		search.isEnabled = flag
	}

	@Synchronized
	private fun appendString(str: String) {
		textA.append(str)
		try {
			Thread.sleep(25)
			scrollBar.value = scrollBar.maximum
		} catch (e: InterruptedException) {
			e.printStackTrace()
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

	private fun filterRules(str: String): Int {
		if (str.startsWith("#") || str == "")
			return 1
		else if (str.startsWith("10.") or
			str.startsWith("0.0.0.0") or
			str.startsWith("127.") or
//			str.startsWith("191.255.255.255") or
			str.startsWith("172.16.") or
			str.startsWith("172.17.") or
			str.startsWith("172.18.") or
			str.startsWith("172.19.") or
			str.startsWith("172.20.") or
			str.startsWith("172.21.") or
			str.startsWith("172.22.") or
			str.startsWith("172.23.") or
			str.startsWith("172.24.") or
			str.startsWith("172.25.") or
			str.startsWith("172.26.") or
			str.startsWith("172.27.") or
			str.startsWith("172.28.") or
			str.startsWith("172.29.") or
			str.startsWith("172.30.") or
			str.startsWith("172.31.") or
			str.startsWith("169.254.") or
			str.startsWith("192.168.")
		) {
			appendString("内网IP:\t$str\n")
			local.addElement("$str\n")
			return 2
		} else
			return 0
	}

	private fun readHosts(): Vector<String>? {
		val recode = Vector<String>()

		val fileReader = hostsPath.bufferedReader()
		var s = fileReader.readLine()
		//逐行读取文件记录
		while (s != null) {
			//过滤# 开头的注释以及空行
			if (filterRules(s) != 0) {
				s = fileReader.readLine()
				continue
			}
			//以空格作为分割点
			val fromFile = s.replace("\t", " ").split(" ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
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

	inner class Update : SwingWorker<Void, String>() {
		override fun doInBackground(): Void? {//后台任务
			setButtonStatus(false)
			textA.text = ""

			val urls = Objects.requireNonNull<Vector<String>>(readHosts())
			if (urls.isNotEmpty() && backup()) {
				val fileWriter = editFile
				fileWriter.writeText(proString())
				if (local.size > 0)
					for (s in local)
						fileWriter.appendText(s)
				local.clear()
				//设定线程池
				val pool = Executors.newFixedThreadPool(8)
				for (i in urls)
					pool.execute(Thread { append(readPage(i)) })
				pool.shutdown()
				while (true)
					if (pool.isTerminated)
						break
				publish("\n完成")
				//移动，但目前不能获取管理员权限写入C 盘
//				Files.move(editFile.toPath(), hostsPath.toPath());
			}
			return null
		}

		override fun process(chunks: List<String>) {//更新信息
			for (s in chunks)
				appendString(s)
		}

		override fun done() {//任务完成后恢复按钮状态
			scrollBar.value = scrollBar.maximum
			setButtonStatus(true)
		}

	}

	inner class Search : SwingWorker<Void, String>() {
		override fun doInBackground(): Void? {
			setButtonStatus(false)
			textA.text = ""

			val str = hosts.text
			if (str == "") {
				publish("请在搜索栏中写入网址")
				return null
			}
			Files.deleteIfExists(editFile.toPath())
			Files.copy(hostsPath.toPath(), editFile.toPath())
			val recode = readPage(str)
			if (!recode.isEmpty() && backup()) {
				append(recode)
				publish("\n 完成")
			} else
				Files.deleteIfExists(editFile.toPath())
			return null
		}

		override fun process(chunks: List<String>) {
			for (s in chunks)
				appendString(s)
		}

		override fun done() {
			setButtonStatus(true)
		}
	}

}

import com.gargoylesoftware.htmlunit.BrowserVersion
import com.gargoylesoftware.htmlunit.WebClient
import com.gargoylesoftware.htmlunit.html.HtmlPage
import org.apache.commons.logging.LogFactory
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import java.awt.Desktop
import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.nio.file.Files
import java.util.*
import java.util.logging.Level
import javax.swing.filechooser.FileSystemView

private val DesktopPath = FileSystemView.getFileSystemView().homeDirectory.path

private val proString: () -> String = {
	"# Copyright (c) 1993-2009 Microsoft Corp.\n" + "#\n" +
			"# This is getDocumentFromPage sample HOSTS file used by Microsoft TCP/IP for Windows.\n" + "#\n" +
			"# This file contains the mappings of IP addresses to host names. Each\n" +
			"# entry should be kept on an individual line. The IP address should\n" +
			"# be placed in the first column followed by the corresponding host name.\n" +
			"# The IP address and the host name should be separated by at least one\n" +
			"# space.\n" + "#\n" +
			"# Additionally, comments (such as these) may be inserted on individual\n" +
			"# lines or following the machine name denoted by getDocumentFromPage '#' symbol.\n" + "#\n" +
			"# For example:\n" + "#\n" +
			"#      102.54.94.97     rhino.acme.com          # source server\n" +
			"#       38.25.63.10     x.acme.com              # x client host\n" + "\n" +
			"# localhost name resolution is handled within DNS itself.\n" +
			"#\t127.0.0.1       localhost\n" +
			"#\t::1             localhost\n" + "\n"
}

private val openEtc: () -> Unit = { Desktop.getDesktop().open(File("C:\\Windows\\System32\\drivers\\etc\\")) }

private fun backup(): Boolean {
	val hosts = File("C:\\Windows\\System32\\drivers\\etc\\hosts")
	//备份hosts
	val backup = File("$DesktopPath\\hosts.bak")
	try {
		if (backup.exists())
			Files.delete(backup.toPath())
		Files.copy(hosts.toPath(), backup.toPath())
		return true
	} catch (e: IOException) {
		e.printStackTrace()
	}
	return false
}

private fun readPage(url: String): Vector<String> {
	val aimURL = "http://tool.chinaz.com/dns?type=1&host=$url&ip="
	//设置代理
//	System.setProperty("http.proxyHost", "127.0.0.1")
//	System.setProperty("http.proxyPort", "8090")

	val recode = Vector<String>()

	try {
		val doc = getDocumentFromPage(aimURL)

		println("The string is dealing...")
		val host = doc.getElementById("host").attr("value")

		val ipTmp =
			doc.getElementsByClass("w60-0 tl").text().split(" ".toRegex()).dropLastWhile { it.isEmpty() }
				.toTypedArray()
		val ip = arrayOfNulls<String>(ipTmp.size)
		run {
			var i = 0
			var j = 0
			while (i < ipTmp.size) {
				ipTmp[i] = ipTmp[i].replace("[^\\d{1,3}.]".toRegex(), "").replace("\\.\\.+".toRegex(), "")
				if (ipTmp[i] == "") {
					i++
					continue
				}
				ip[j] = ipTmp[i]
				j++
				i++
			}
		}

//		val str = doc.getElementsByClass("w14-0").text().split(" ".toRegex()).dropLastWhile { it.isEmpty() }
//			.toTypedArray()
//		val ttl = arrayOfNulls<Int>(str.size)
//		run {
//			var i = 0
//			var j = 0
//			while (i < str.size) {
//				if (str[i].contains("TTL")) {
//					i++
//					continue
//				}
//				ttl[j] = Integer.parseInt(str[i])
//				j++
//				i++
//			}
//		}


		val tmp = Vector<String>()
//		for (i in 0 until min(ttl.size, ip.size))
//			if (ip[i] != null)
//				if ((tmp.indexOf(ip[i]) == -1) and (ip[i] != "-"))
//					tmp.addElement(ip[i] + " " + ttl[i])
		for (s in ip) {
			if (s != null)
				if ((tmp.indexOf("$s $host") == -1) and (s != "-"))
					tmp.addElement("$s $host")
		}

		tmp.sort()
		for (i in tmp.indices)
			recode.addElement("\n" + tmp.elementAt(i))
	} catch (e: Exception) {
		e.printStackTrace()
	}

	println("Finish")
	recode.addElement("\n")
	return recode
}

private fun getDocumentFromPage(url: String): Document {
	println("Page loading...")
	//不打印日志
	LogFactory.getFactory().setAttribute("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.NoOpLog")
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

	println("Loaded.")
	return Jsoup.parse(page.asXml(), url)
}

private fun append(recode: Vector<String>): Boolean {
	if (!recode.isEmpty() && backup()) {
		try {
			val fileWriter1 = FileWriter("$DesktopPath\\hosts", true)
			for (i in recode.indices)
				fileWriter1.write(recode.elementAt(i))
			fileWriter1.close()
			return true
		} catch (e: IOException) {
			e.printStackTrace()
		}

	}
	return false
}

private fun updateHosts(urls: Vector<String>): Boolean {
	if (!urls.isEmpty() && backup()) {
		try {
			val fileWriter = FileWriter("$DesktopPath\\hosts")
			fileWriter.write(proString())
			fileWriter.flush()
			fileWriter.close()

			for (i in urls.indices)
				append(readPage(urls.elementAt(i)))

			//移动，但目前不能获取管理员权限写入C 盘
			//				Files.move(bak1.toPath(), hosts.toPath());

			return true
		} catch (e: IOException) {
			e.printStackTrace()
		}

	}
	return false
}

//private fun readHosts(): Vector<String>? {
//	val recode = Vector<String>()
//	try {
//		if (!System.getProperty("os.name").contains("indows")) {
//			println("目前仅支持Windows 2000/XP 及以上版本")
//			return null
//		}
//		//听说其在Win98,win me 中位于/Windows 下？
//		val dirPath = "C:\\Windows\\System32\\drivers\\etc\\hosts"
//		val fileReader = FileReader(dirPath)
//		val bufferedReader = BufferedReader(fileReader)
//		var s: String
//		//逐行读取文件记录
//		while ((s = bufferedReader.readLine()) != null) {
//			//过滤# 开头的注释以及空行
//			if (s.startsWith("#") || s == "")
//				continue
//			//以空格作为分割点
//			val fromFile = s.split(" ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
//			//过滤重复
//			if (recode.indexOf(fromFile[1]) == -1)
//				recode.addElement(fromFile[1])
//		}
//		fileReader.close()
//		bufferedReader.close()
//	} catch (e: IOException) {
//		e.printStackTrace()
//	}
//
//	recode.sort()
//	return if (recode.isEmpty()) null else recode
//}

private fun menu() {
	//hosts 备份位于桌面
	val sc = Scanner(System.`in`)
	while (true) {
		var flag: Boolean? = false
		println("1 更新hosts (暂停服务)\n" + "2 新增URL\n" + "3 备份hosts")
		when (sc.nextLine()) {
			"1" -> {
//				flag = updateHosts(Objects.requireNonNull<Vector<String>>(readHosts()))
//				openEtc()
			}
			"2" -> {
				println("Input the URL:")
				flag = append(readPage(sc.next()))
				openEtc()
			}
			"3" -> flag = backup()
		}
		if (flag!!)
			break
		else
			println("请重试")
	}
}

fun main() {
	menu()
}

import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import java.net.URL
import java.util.*

private fun selectDNSForURL(url: String, ia: Int): Vector<String> {
//	设置代理
//	setProperty("http.proxyHost", "127.0.0.1");
//	setProperty("http.proxyPort", "8090");

	val ip = Vector<String>()
	val recode = Vector<String>()

	val cAimURL = "http://tool.chinaz.com/dns?type=1&host=$url&ip="
	val mAimURL = "http://mtool.chinaz.com/dns?host=$url&ip=&accessmode=1"
	val au: (String) -> Document = { s -> Jsoup.parse(URL(s).openStream(), "UTF-8", s) }
	val doc = if (ia == 1) au(cAimURL) else au(mAimURL)
	println(doc.html() + ip)


//	for (i in ip.indices) recode.addElement("\n" + ip.elementAt(i) + " ")

	return recode
}

private fun save(recode: Vector<String>, path: String) {
	if (recode.isEmpty())
		return
	print(path)

	/*
	 * 如果没有hosts备份文件要拷贝
	 * "C:\\Windows\\System32\\drivers\\etc\\hosts"
	 * */

//	for (i in recode.indices) print(recode.elementAt(i) + path);

}

fun main(args: Array<String>) {
	val path = "D:\\My Document\\Desktop\\2"
	val i = 0
	if (args.isNotEmpty())
		save(selectDNSForURL(args[0], i), path)
	else {
		println("Input the URL:")
//		save(selectDNSForURL(Scanner(System.`in`).next(), i),path)

		save(selectDNSForURL("github.com", i), path)
	}

}

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebClientOptions;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import org.apache.commons.logging.LogFactory;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import java.awt.*;
import java.io.*;
import java.nio.file.Files;
import java.util.Collections;
import java.util.Objects;
import java.util.Scanner;
import java.util.Vector;
import java.util.logging.Level;

public class host {
	private static String DesktopPath = FileSystemView.getFileSystemView().getHomeDirectory().getPath();

	host() {
		/*
	Document中提供了丰富的方法来获取指定元素。

	◇使用DOM的方式来取得
		getElementById(String id)：通过id来获取
			getElementsByTag(String tagName)：通过标签名字来获取
　			getElementsByClass(String className)：通过类名来获取
　			getElementsByAttribute(String key)：通过属性名字来获取
　			getElementsByAttributeValue(String key, String value)：通过指定的属性名字，属性值来获取
　			getAllElements()：获取所有元素

	◇通过类似于css或jQuery的选择器来查找元素

		使用的是Element类的下记方法：

			public Elements select(String cssQuery)

		通过传入一个类似于CSS或jQuery的选择器字符串，来查找指定元素。

		例子：

			File input = new File("/tmp/input.html");
			Document doc = Jsoup.parse(input, "UTF-8", "http://example.com/");

			Elements links = doc.select("a[href]"); //带有href属性的a元素
			Elements pngs = doc.select("img[src$=.png]");
			//扩展名为.png的图片

			Element masthead = doc.select("div.masthead").first();
			//class等于masthead的div标签

			Elements resultLinks = doc.select("h3.r > a"); //在h3元素之后的a元素


	选择器的更多语法(可以在org.jsoup.select.Selector中查看到更多关于选择器的语法)：

	tagname: 通过标签查找元素，比如：a
	　　ns|tag: 通过标签在命名空间查找元素，比如：可以用 fb|name 语法来查找 <fb:name> 元素
	　　#id: 通过ID查找元素，比如：#logo
		.class: 通过class名称查找元素，比如：.masthead
		[attribute]: 利用属性查找元素，比如：[href]
		[^attr]: 利用属性名前缀来查找元素，比如：可以用[^data-] 来查找带有HTML5 Dataset属性的元素
		[attr=value]: 利用属性值来查找元素，比如：[width=500]
		[attr^=value], [attr$=value], [attr=value]: 利用匹配属性值开头、结尾或包含属性值来查找元素，比如：[href=/path/]
		[attr~=regex]: 利用属性值匹配正则表达式来查找元素，比如： img[src~=(?i).(png|jpe?g)]
		*: 这个符号将匹配所有元素

	Selector选择器组合使用
　		el#id: 元素+ID，比如： div#logo
		el.class: 元素+class，比如： div.masthead
		el[attr]: 元素+class，比如： a[href]
		任意组合，比如：a[href].highlight
　		ancestor child: 查找某个元素下子元素，比如：可以用.body p 查找在"body"元素下的所有 p元素
　		parent > child: 查找某个父元素下的直接子元素，比如：可以用div.content > p 查找 p 元素，也可以用body > * 查找body标签下所有直接子元素
　		siblingA + siblingB: 查找在A元素之前第一个同级元素B，比如：div.head + div
　		siblingA ~ siblingX: 查找A元素之前的同级X元素，比如：h1 ~ p
　		el, el, el:多个选择器组合，查找匹配任一选择器的唯一元素，例如：div.masthead, div.logo
	伪选择器selectors
　		:lt(n): 查找哪些元素的同级索引值（它的位置在DOM树中是相对于它的父节点）小于n，比如：td:lt(3) 表示小于三列的元素
　		:gt(n):查找哪些元素的同级索引值大于n，比如： div p:gt(2)表示哪些div中有包含2个以上的p元素
　		:eq(n): 查找哪些元素的同级索引值与n相等，比如：form input:eq(1)表示包含一个input标签的Form元素
　		:has(seletor): 查找匹配选择器包含元素的元素，比如：div:has(p)表示哪些div包含了p元素
　		:not(selector): 查找与选择器不匹配的元素，比如： div:not(.logo) 表示不包含 class="logo" 元素的所有 div 列表
　		:contains(text): 查找包含给定文本的元素，搜索不区分大不写，比如： p:contains(jsoup)
　		:containsOwn(text): 查找直接包含给定文本的元素
　		:matches(regex): 查找哪些元素的文本匹配指定的正则表达式，比如：div:matches((?i)login)
　		:matchesOwn(regex): 查找自身包含文本匹配指定正则表达式的元素
	注意　：上述伪选择器索引是从0开始的，也就是说第一个元素索引值为0，第二个元素index为1等

	◆通过上面的选择器，我们可以取得一个Elements对象，它继承了ArrayList对象，里面放的全是Element对象。
		接下来我们要做的就是从Element对象中，取出我们真正需要的内容。

		通常有下面几种方法：

			◇Element.text()
			这个方法用来取得一个元素中的文本。

			◇Element.html()或Node.outerHtml()
			这个方法用来取得一个元素中的html内容

			◇Node.attr(String key)
			获得一个属性的值，例如取得超链接<a href="">中href的值

*/
	}

	private static String proString() {
		return "# Copyright (c) 1993-2009 Microsoft Corp.\n" +
				"#\n" +
				"# This is getDocumentFromPage sample HOSTS file used by Microsoft TCP/IP for Windows.\n" +
				"#\n" +
				"# This file contains the mappings of IP addresses to host names. Each\n" +
				"# entry should be kept on an individual line. The IP address should\n" +
				"# be placed in the first column followed by the corresponding host name.\n" +
				"# The IP address and the host name should be separated by at least one\n" +
				"# space.\n" +
				"#\n" +
				"# Additionally, comments (such as these) may be inserted on individual\n" +
				"# lines or following the machine name denoted by getDocumentFromPage '#' symbol.\n" +
				"#\n" +
				"# For example:\n" +
				"#\n" +
				"#      102.54.94.97     rhino.acme.com          # source server\n" +
				"#       38.25.63.10     x.acme.com              # x client host\n" +
				"\n" +
				"# localhost name resolution is handled within DNS itself.\n" +
				"#\t127.0.0.1       localhost\n" +
				"#\t::1             localhost\n" +
				"\n";
	}

	private static void OpenEtc() {
		try {
			Desktop.getDesktop().open(new File("C:\\Windows\\System32\\drivers\\etc\\"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static Boolean Backup() {
		File hosts = new File("C:\\Windows\\System32\\drivers\\etc\\hosts");
		//备份hosts
		File backup = new File(DesktopPath + "\\hosts.bak");
		try {
			if (backup.exists())
				Files.delete(backup.toPath());
			Files.copy(hosts.toPath(), backup.toPath());
			return true;
		} catch (IOException e) {
			e.printStackTrace();
		}

		return false;
	}

	private static Document getDocumentFromPage(String url) throws IOException {
		System.out.println("Page loading...");
		//不打印日志
		LogFactory.getFactory().setAttribute("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.NoOpLog");
		java.util.logging.Logger.getLogger("com.gargoylesoftware").setLevel(Level.OFF);
		java.util.logging.Logger.getLogger("org.apache.http.client").setLevel(Level.OFF);

		//模拟Chrome
		WebClient webClient = new WebClient(BrowserVersion.CHROME);
		WebClientOptions webClientOptions = webClient.getOptions();

		//禁用CSS
		webClientOptions.setCssEnabled(false);
		//启用JS 解释器
		webClientOptions.setJavaScriptEnabled(true);
		//JS 错误时不抛出异常
		webClientOptions.setThrowExceptionOnScriptError(false);
		webClientOptions.setThrowExceptionOnFailingStatusCode(false);
		//连接超时时间
		webClientOptions.setTimeout(2 * 1000);

		HtmlPage page = webClient.getPage(url);
		//等待后台运行
		webClient.waitForBackgroundJavaScript(10 * 1000);

		System.out.println("Loaded.");
		return Jsoup.parse(page.asXml(), url);
	}

	private static Vector<String> ReadPage(String url) {
		String AimURL = "http://tool.chinaz.com/dns?type=1&host=" + url + "&ip=";
		//设置代理
//		System.setProperty("http.proxyHost", "127.0.0.1");
//		System.setProperty("http.proxyPort", "8090");

		Vector<String> recode = new Vector<>();

		try {
			Document doc = getDocumentFromPage(AimURL);

			System.out.println("The string is dealing...");
			String host = doc.getElementById("host").attr("value");

			String[] IPTmp = doc.getElementsByClass("w60-0 tl").text().split(" ");
			String[] IP = new String[IPTmp.length];
			for (int i = 0, j = 0; i < IPTmp.length; i++) {
				IPTmp[i] = IPTmp[i].replaceAll("[^\\d{1,3}.]", "").replaceAll("\\.\\.+", "");
				if (IPTmp[i].equals(""))
					continue;
				IP[j] = IPTmp[i];
				j++;
			}

//			String[] str = doc.getElementsByClass("w14-0").text().split(" ");
//			Integer[] TTL = new Integer[str.length];
//			for (int i = 0, j = 0; i < str.length; i++) {
//				if (str[i].contains("TTL"))
//					continue;
//				TTL[j] = Integer.parseInt(str[i]);
//				j++;
//			}

//			Vector<String> tmp = new Vector<>();
//			for (int i = 0; i < Math.min(TTL.length, IP.length); i++)
//				if (!(IP[i] == null))
//					if (tmp.indexOf(IP[i]) == -1 & !IP[i].equals("-"))
//						tmp.addElement(IP[i] + " " + TTL[i]);
			for (String s : IP) {
				if (!(s == null))
					if (recode.indexOf("\n" + s + " " + host) == -1 & !s.equals("-"))
						recode.addElement("\n" + s + " " + host);
			}

			Collections.sort(recode);
//			for (int i = 0; i < tmp.size(); i++)
//				recode.addElement("\n" + tmp.elementAt(i));
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("Finish");
		recode.addElement("\n");
		return recode;
	}

	private static Vector<String> ReadHosts() {
		Vector<String> recode = new Vector<>();
		try {
			if (!System.getProperty("os.name").contains("indows")) {
				System.out.println("目前仅支持Windows 2000/XP 及以上版本");
				return null;
			}
			//听说其在Win98,win me 中位于/Windows 下？
			String dirPath = "C:\\Windows\\System32\\drivers\\etc\\hosts";
			FileReader fileReader = new FileReader(dirPath);
			BufferedReader bufferedReader = new BufferedReader(fileReader);
			String s;
			//逐行读取文件记录
			while ((s = bufferedReader.readLine()) != null) {
				//过滤# 开头的注释以及空行
				if (s.startsWith("#") || s.equals(""))
					continue;
				//以空格作为分割点
				String[] fromFile = s.split(" ");
				//过滤重复
				if (recode.indexOf(fromFile[1]) == -1)
					recode.addElement(fromFile[1]);
			}
			fileReader.close();
			bufferedReader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		Collections.sort(recode);
		return recode.isEmpty() ? null : recode;
	}

	private static void Append(Vector<String> recode) {
		try {
			FileWriter fileWriter1 = new FileWriter(DesktopPath + "\\hosts", true);
			for (int i = 0; i < recode.size(); i++)
				fileWriter1.write(recode.elementAt(i));
			fileWriter1.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void AppendNew(Vector<String> recode) {
		if (!recode.isEmpty() && Backup())
			Append(recode);
		OpenEtc();
	}

	private static void UpdateHosts(Vector<String> urls) {
		if (!urls.isEmpty() && Backup()) {
			try {
				FileWriter fileWriter = new FileWriter(DesktopPath + "\\hosts");
				fileWriter.write(proString());
				fileWriter.flush();
				fileWriter.close();

				for (int i = 0; i < urls.size(); i++)
					Append(ReadPage(urls.elementAt(i)));

				OpenEtc();
				//移动，但目前不能获取管理员权限写入C 盘
//				Files.move(bak1.toPath(), hosts.toPath());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private static void Menu() {
		//hosts 备份位于桌面
		Scanner sc = new Scanner(System.in);
		boolean flag = true;
		while (flag) {
			flag = false;
			System.out.println("1 更新hosts\n" + "2 新增URL\n" + "3 备份hosts\t" + "输入quit 退出");
			String s = sc.nextLine();
			switch (s) {
				case "1":
					UpdateHosts(Objects.requireNonNull(ReadHosts()));
					break;
				case "2":
					System.out.println("Input the URL:");
					AppendNew(ReadPage(sc.next()));
					break;
				case "3":
					Backup();
					break;
				default:
					if (!s.equals("quit")) {
						System.out.println("请重试");
						flag = true;
					}
			}
		}
	}

	private static void GUI() {
		JFrame jf = new JFrame("test");
//		Container container = jf.getContentPane();

		String s = "随便写点什么避免尴尬";
		JPanel update = new JPanel();
		JButton updateHosts = new JButton("更新");
		update.setLayout(new GridLayout(1, 1));
		JTextArea textArea = new JTextArea(s);
		textArea.setEditable(false);
		update.add(textArea);

		JPanel add = new JPanel();
		JTextField hosts = new JTextField();
		JButton search = new JButton("搜索");
		String str = hosts.getText();
		add.setLayout(new GridLayout(1, 2));
		add.add(hosts);
		add.add(search);

		JPanel backup = new JPanel();
		JButton backupHosts = new JButton("备份");
		backup.setLayout(new GridLayout(1, 2));
		backup.add(backupHosts);
		backup.add(updateHosts);


		jf.add(update, BorderLayout.NORTH);
		jf.add(add, BorderLayout.CENTER);
		jf.add(backup, BorderLayout.SOUTH);

		//大小
		jf.setSize(500, 500);
		//是否可改变大小
		jf.setResizable(false);
		//出现位置居中
		jf.setLocationRelativeTo(null);
//		jf.setLocation(1200, 200);
		//关闭窗口按钮
		jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		//是否可见
		jf.setVisible(true);
	}

	public static void main(String[] args) {
//		Menu();
		GUI();
	}
}

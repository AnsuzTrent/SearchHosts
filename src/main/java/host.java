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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.nio.file.Files;
import java.util.Collections;
import java.util.Objects;
import java.util.Scanner;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;

public class host {

	private static String DesktopPath = FileSystemView.getFileSystemView().getHomeDirectory().getPath();

	public static void main(String[] args) {
//		Menu();
		new GUI();
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

	private static void AppendNew(String str) {
		Vector<String> recode = ReadPage(str);
		try {
			Files.copy(new File("C:\\Windows\\System32\\drivers\\etc\\hosts").toPath(), new File(DesktopPath + "\\hosts").toPath());
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (!recode.isEmpty() && Backup())
			Append(recode);
		OpenEtc();
	}

	private static void Update() {
		Vector<String> urls = Objects.requireNonNull(ReadHosts());
		if (!urls.isEmpty() && Backup()) {
			try {
				FileWriter fileWriter = new FileWriter(DesktopPath + "\\hosts");
				fileWriter.write(proString());
				fileWriter.flush();
				fileWriter.close();

				//设定线程池
				ExecutorService pool = Executors.newFixedThreadPool(8);
				for (int i = 0; i < urls.size(); i++) {
					int finalI = i;
					pool.execute(new Thread(() -> {
						System.out.println(urls.elementAt(finalI));
						Append(ReadPage(urls.elementAt(finalI)));
					}));
				}
				pool.shutdown();
				while (true)
					if (pool.isTerminated())
						break;

				OpenEtc();
				//移动，但目前不能获取管理员权限写入C 盘
//				Files.move(bak1.toPath(), hosts.toPath());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
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
			Desktop.getDesktop().open(new File("C:\\Windows\\System32\\drivers\\etc"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static Document getDocumentFromPage(String url) throws IOException {
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

			String host = doc.getElementById("host").attr("value");

			String[] IPTmp = doc.getElementsByClass("w60-0 tl").text().split("\\[.*?]");
			String[] IP = new String[IPTmp.length];
			for (int i = 0, j = 0; i < IPTmp.length; i++) {
				IPTmp[i] = IPTmp[i].replaceAll("([ \\-]|\\.\\.+)", "");
				if (IPTmp[i].equals(""))
					continue;
				IP[j++] = IPTmp[i];
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
					Update();
					break;
				case "2":
					System.out.println("Input the URL:");
					AppendNew(sc.next());
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

	static class GUI extends JFrame implements ActionListener {
		private JButton updateHosts, search, backupHosts;
		private JTextField hosts;

		GUI() {
			//左侧栏
			JTextArea textArea = new JTextArea("广告位招租");
			//设置只读
			textArea.setEditable(false);
			JPanel update = new JPanel();
			update.add(textArea);
			add(update, BorderLayout.WEST);

			//中栏
			JTextArea textA = new JTextArea("到时候输出记录");
			//设置只读
			textA.setEditable(false);
			JPanel recode = new JPanel();
			recode.setLayout(new GridLayout(1, 1));
			recode.add(textA);
			add(recode, BorderLayout.CENTER);

			//顶栏
			hosts = new JTextField();
			search = new JButton("搜索");
			search.addActionListener(this);
			JPanel append = new JPanel();
			append.setLayout(new GridLayout(1, 2));
			append.add(hosts);
			append.add(search);
			add(append, BorderLayout.NORTH);

			//底栏
			backupHosts = new JButton("备份");
			backupHosts.addActionListener(this);
			updateHosts = new JButton("更新");
			updateHosts.addActionListener(this);
			JPanel backup = new JPanel();
			backup.setLayout(new GridLayout(1, 2));
			backup.add(backupHosts);
			backup.add(updateHosts);
			add(backup, BorderLayout.SOUTH);

			setTitle("test");
			//大小
			setSize(500, 500);
			//是否可改变大小
//			setResizable(false);
			//出现位置居中
			setLocationRelativeTo(null);
//			setLocation(1200, 200);
			//关闭窗口按钮
			setDefaultCloseOperation(EXIT_ON_CLOSE);
			//是否可见
			setVisible(true);
		}

		private static void setButtonStatus(boolean f, JButton... a) {
			for (JButton button : a)
				button.setEnabled(f);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			if (e.getSource() == search) {
				setButtonStatus(false, backupHosts, updateHosts);
				host.AppendNew(hosts.getText());
				setButtonStatus(true, backupHosts, updateHosts);
			} else if (e.getSource() == backupHosts) {
				setButtonStatus(false, search, updateHosts);
				host.Backup();
				setButtonStatus(true, search, updateHosts);
			} else {
				setButtonStatus(false, search, backupHosts);
				host.Update();
				setButtonStatus(true, search, backupHosts);
			}
		}
	}

}

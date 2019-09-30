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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;

public class host {
	public static void main(String[] args) {
//		Menu();
		new SearchHosts();
	}
}

class SearchHosts extends JFrame {
	private static JTextField hosts = new JTextField();
	private static JButton search = new JButton("搜索");
	private static JTextArea textA = new JTextArea("请选择功能");
	private static JButton backupHosts = new JButton("备份");
	private static JButton updateHosts = new JButton("更新");
	private static JButton openFolder = new JButton("打开hosts 所在文件夹");

	SearchHosts() {
		//顶栏
		JPanel append = new JPanel();
		append.setLayout(new GridLayout(1, 2));
		append.add(hosts);
		search.addActionListener(e -> SearchUtil.AppendNew(hosts.getText()));
		append.add(search);
		add(append, BorderLayout.NORTH);

		//中栏
		JPanel recode = new JPanel();
		recode.setLayout(new GridLayout(1, 1));
		textA.setEditable(false);        //设置只读
		textA.setLineWrap(true);        //设置自动换行
		recode.add(new JScrollPane(textA));        //创建滚动窗格
		add(recode, BorderLayout.CENTER);

		//底栏
		JPanel backup = new JPanel();
		backup.setLayout(new GridLayout(1, 3));
		backupHosts.addActionListener(e -> SearchUtil.Backup());
		backup.add(backupHosts);
		updateHosts.addActionListener(e -> SearchUtil.Update());
		backup.add(updateHosts);
		openFolder.addActionListener(e -> SearchUtil.OpenEtc());
		backup.add(openFolder);
		add(backup, BorderLayout.SOUTH);

		setTitle("test");
		setSize(500, 500);        //大小
		setResizable(false);//是否可改变大小
		setLocationRelativeTo(null);        //出现位置居中
//		setLocation(1200, 200);
		setDefaultCloseOperation(EXIT_ON_CLOSE);        //关闭窗口按钮
		setVisible(true);    //是否可见

		if (!System.getProperty("os.name").contains("indows")) {
			textA.setText("\n目前仅支持Windows 2000/XP 及以上版本");
			setButtonStatus(false);
		}
		//听说其在Win98,win me 中位于/Windows 下？
	}

	private static void setButtonStatus(boolean flag) {
		openFolder.setEnabled(flag);
		backupHosts.setEnabled(flag);
		updateHosts.setEnabled(flag);
		search.setEnabled(flag);
	}

	static class SearchUtil {
		private static String EtcPath = "C:\\Windows\\System32\\drivers\\etc";
		private static File hostsPath = new File(EtcPath + "\\hosts");
		private static File editFile = new File(FileSystemView.getFileSystemView().getHomeDirectory().getPath() + "\\hosts");
		private static Vector<String> local = new Vector<>();

		static Boolean Backup() {
			try {
				File backup = new File(editFile + ".bak");
				if (backup.exists())
					Files.delete(backup.toPath());
				Files.copy(hostsPath.toPath(), backup.toPath());
				textA.append("已备份hosts 文件至  ：  " + backup.toPath());
				return true;
			} catch (IOException e) {
				textA.append("\nError in \n" + e.getMessage() + "\n");
			}

			return false;
		}

		static void AppendNew(String str) {
			if (str.equals("")) {
				textA.append("请在搜索栏中写入网址");
				return;
			}
			try {
				if (editFile.exists())
					Files.delete(editFile.toPath());
				Files.copy(hostsPath.toPath(), editFile.toPath());
			} catch (IOException e) {
				textA.append("\nError in \n" + e.getMessage() + "\n");
			}
			Vector<String> recode = ReadPage(str);
			if (!recode.isEmpty() && Backup()) {
				Append(recode);
				textA.append("\n 完成");
				OpenEtc();
			}
		}

		static void Update() {
			Vector<String> urls = Objects.requireNonNull(ReadHosts());
			if (!urls.isEmpty() && Backup()) {
				try {
					FileWriter fileWriter = new FileWriter(editFile);
					fileWriter.write(proString());
					if (local.size() > 0)
						for (String s : local)
							fileWriter.write(s);
					fileWriter.flush();
					fileWriter.close();

					//设定线程池
					ExecutorService pool = Executors.newFixedThreadPool(8);
					for (String str : urls)
						pool.execute(new Thread(() -> Append(ReadPage(str))));
					pool.shutdown();
					while (true)
						if (pool.isTerminated())
							break;

					textA.append("\n完成");
					OpenEtc();
					//移动，但目前不能获取管理员权限写入C 盘
//				Files.move(bak1.toPath(), hosts.toPath());
				} catch (IOException e) {
					textA.append("\nError in \n" + e.getMessage() + "\n");
				}
			}
		}

		static void OpenEtc() {
			try {
				Desktop.getDesktop().open(new File(EtcPath));
			} catch (IOException e) {
				textA.append("\nError in \n" + e.getMessage() + "\n");
			}
		}

		private static void Append(Vector<String> recode) {
			try {
				FileWriter fileWriter = new FileWriter(editFile, true);
				for (String str : recode) {
					textA.append(str);
					fileWriter.write(str);
				}
				fileWriter.close();
			} catch (IOException e) {
				textA.append("\nError in \n" + e.getMessage() + "\n\n");
			}
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
				int i = 0, j = 0;
				while (i < IPTmp.length) {
					IPTmp[i] = IPTmp[i].replaceAll("([ \\-]|\\.\\.+)", "");
					if (IPTmp[i].equals("")) {
						i++;
						continue;
					}
					IP[j++] = IPTmp[i++];
				}

				for (String s : IP)
					if (!(s == null))
						if (recode.indexOf("\n" + s + " " + host) == -1 & !s.equals("-"))
							recode.addElement("\n" + s + " " + host);

				Collections.sort(recode);
			} catch (Exception e) {
				textA.append("\nError in \n" + e.getMessage() + "\n");
			}
			if (!recode.isEmpty())
				recode.addElement("\n");
			else
				textA.append("输入的网址没有找到对应ip\n");
			return recode;
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

		private static Vector<String> ReadHosts() {
			Vector<String> recode = new Vector<>();
			try {
				FileReader fileReader = new FileReader(hostsPath);
				BufferedReader bufferedReader = new BufferedReader(fileReader);
				String s;
				//逐行读取文件记录
				while ((s = bufferedReader.readLine()) != null) {
					//过滤# 开头的注释以及空行
					if (s.startsWith("#") || s.equals("")) {
						if (s.startsWith("127.0.0.1"))
							local.addElement(s);
						continue;
					}
					//以空格作为分割点
					String[] fromFile = s.split(" ");
					//过滤重复
					if (recode.indexOf(fromFile[1]) == -1)
						recode.addElement(fromFile[1]);
				}
				if (local.size() > 0)
					local.addElement("\n");
				fileReader.close();
				bufferedReader.close();
			} catch (IOException e) {
				textA.append("\nError in \n" + e.getMessage() + "\n");
			}
			Collections.sort(recode);
			return recode.isEmpty() ? null : recode;
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
	}
}

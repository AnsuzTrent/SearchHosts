import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebClientOptions;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.LogFactory;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import java.awt.*;
import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;

public class host {
	public static void main(String[] args) {
		EventQueue.invokeLater(SearchUtil::new);
	}
}

class SearchUtil extends JFrame {
	private static JTextField hosts = new JTextField();
	private static JButton search = new JButton("搜索");
	private static JTextArea textA = new JTextArea("请选择功能");
	private static JButton backupHosts = new JButton("备份");
	private static JButton updateHosts = new JButton("更新");
	private static JButton openFolder = new JButton("打开hosts 所在文件夹");
	private static JButton flushDNS = new JButton("刷新DNS 配置");
	private static JScrollBar scrollBar;

	private static String EtcPath = "C:\\Windows\\System32\\drivers\\etc";
	private static File hostsPath = new File(EtcPath + "\\hosts");
	private static File editFile = new File(FileSystemView.getFileSystemView().getHomeDirectory().getPath() + "\\hosts");
	private static Vector<String> local = new Vector<>();

	SearchUtil() {
		//顶栏
		JPanel append = new JPanel();
		append.setLayout(new GridLayout(1, 2));
		append.add(hosts);
		search.addActionListener(e -> new search().execute());
		append.add(search);
		add(append, BorderLayout.NORTH);

		//中栏
		JPanel recode = new JPanel();
		recode.setLayout(new GridLayout(1, 1));
		textA.setEditable(false);        //设置只读
		textA.setLineWrap(true);        //设置自动换行
		JScrollPane scrollPane = new JScrollPane(textA);
		scrollBar = scrollPane.getVerticalScrollBar();
		recode.add(scrollPane);        //创建滚动窗格
		add(recode, BorderLayout.CENTER);

		//底栏
		JPanel backup = new JPanel();
		backup.setLayout(new GridLayout(1, 4));
		backupHosts.addActionListener(e -> Backup());
		backup.add(backupHosts);
		updateHosts.addActionListener(e -> new update().execute());
		backup.add(updateHosts);
		openFolder.addActionListener(e -> OpenEtc());
		backup.add(openFolder);
		flushDNS.addActionListener(e -> toFlushDNS());
		backup.add(flushDNS);
		add(backup, BorderLayout.SOUTH);

		setTitle("Search Hosts in Website");
		setSize(500, 500);        //大小
		setResizable(false);//是否可改变大小
		setLocationRelativeTo(null);        //出现位置居中
//		setLocation(1200, 200);
		setDefaultCloseOperation(EXIT_ON_CLOSE);        //关闭窗口按钮
		setVisible(true);    //是否可见

		if (!System.getProperty("os.name").contains("indows")) {
			textA.setText("\n目前仅支持Windows 2000/XP 及以上版本");
			setButtonStatus(false);
			openFolder.setEnabled(false);
			flushDNS.setEnabled(false);
		}
		//听说其在Win98,win me 中位于/Windows 下？
	}

	private static void setButtonStatus(boolean flag) {
		backupHosts.setEnabled(flag);
		updateHosts.setEnabled(flag);
		search.setEnabled(flag);
	}

	private synchronized static void appendString(String str) {
		textA.append(str);
		try {
			Thread.sleep(25);
			scrollBar.setValue(scrollBar.getMaximum());
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private static Boolean Backup() {
		textA.setText("");
		try {
			File backup = new File(editFile + ".bak");
			if (backup.exists())
				Files.delete(backup.toPath());
			Files.copy(hostsPath.toPath(), backup.toPath());
			appendString("已备份hosts 文件至  ：  " + backup.toPath());
			return true;
		} catch (IOException e) {
			appendString("\nError in \n" + e.getMessage() + "\n");
		}

		return false;
	}

	private static void OpenEtc() {
		try {
			Desktop.getDesktop().open(new File(EtcPath));
		} catch (IOException e) {
			appendString("\nError in \n" + e.getMessage() + "\n");
		}
	}

	private static void toFlushDNS() {
		textA.setText("");
		try {
			Process process = Runtime.getRuntime().exec("ipconfig /toFlushDNS");
			BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream(), Charset.forName("GBK")));
			String line;
			while ((line = br.readLine()) != null)
				if (!StringUtils.isEmpty(line))
					appendString(line + "\n");

			br.close();
		} catch (IOException e) {
			appendString("\nError in \n" + e.getMessage() + "\n");
		}
	}

	private static void Append(Vector<String> recode) {
		try {
			FileWriter fileWriter = new FileWriter(editFile, true);
			for (String str : recode) {
				appendString(str);
				fileWriter.write(str);
			}
			fileWriter.close();
		} catch (IOException e) {
			appendString("\nError in \n" + e.getMessage() + "\n\n");
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
			appendString("\nError in \n" + e.getMessage() + "\n");
		}
		if (!recode.isEmpty())
			recode.addElement("\n");
		else
			appendString("输入的网址没有找到对应ip\n");
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
				if (s.startsWith("#") || s.equals(""))
					continue;
				if (s.startsWith("127.0.0.1") || s.startsWith("0.0.0.0")) {
					local.addElement(s + "\n");
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
			appendString("\nError in \n" + e.getMessage() + "\n");
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

	static class update extends SwingWorker<Void, String> {
		@Override//后台任务
		protected Void doInBackground() {
			setButtonStatus(false);
			textA.setText("");

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
					local.clear();
					//设定线程池
					ExecutorService pool = Executors.newFixedThreadPool(8);
					for (String str : urls)
						pool.execute(() -> Append(ReadPage(str)));
					pool.shutdown();
					while (true)
						if (pool.isTerminated())
							break;

					publish("\n完成");
					//移动，但目前不能获取管理员权限写入C 盘
//					Files.move(editFile.toPath(), hostsPath.toPath());
				} catch (IOException e) {
					publish("\nError in \n" + e.getMessage() + "\n");
				}
			}


			return null;
		}

		@Override//更新信息
		protected void process(List<String> chunks) {
			for (String s : chunks)
				appendString(s);
		}

		@Override//任务完成后恢复按钮状态
		protected void done() {
			scrollBar.setValue(scrollBar.getMaximum());
			setButtonStatus(true);
		}

	}

	static class search extends SwingWorker<Void, String> {
		@Override
		protected Void doInBackground() {
			setButtonStatus(false);
			textA.setText("");

			String str = hosts.getText();
			if (str.equals("")) {
				publish("请在搜索栏中写入网址\n");
				return null;
			}
			try {
				Files.deleteIfExists(editFile.toPath());
				Files.copy(hostsPath.toPath(), editFile.toPath());
			} catch (IOException e) {
				publish("\nError in \n" + e.getMessage() + "\n");
			}
			Vector<String> recode = ReadPage(str);
			if (!recode.isEmpty() && Backup()) {
				Append(recode);
				publish("\n 完成");
			} else {
				try {
					Files.deleteIfExists(editFile.toPath());
				} catch (IOException e) {
					publish("\nError in \n" + e.getMessage() + "\n");

				}
			}
			return null;
		}

		@Override
		protected void process(List<String> chunks) {
			for (String s : chunks)
				appendString(s);
		}

		@Override
		protected void done() {
			setButtonStatus(true);
		}
	}

}

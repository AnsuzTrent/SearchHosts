package org.apache.fraud.search.common;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.io.FileUtils;
import org.apache.fraud.search.base.Data;
import org.apache.fraud.search.features.Common;
import org.apache.fraud.search.features.Search;
import org.apache.fraud.search.features.Update;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.List;

public class UserInterface extends JFrame {
	public static final JCheckBox enableTwice = new JCheckBox("开启二次搜索", false);
	private static final JTextField hostsTextField = new JTextField();
	private static final JButton searchButton = new JButton("搜索");
	private static final JTextArea textArea = new JTextArea("请选择功能\n");
	private static final JButton backupButton = new JButton("备份");
	private static final JButton updateButton = new JButton("更新");
	private static final JButton openFolderButton = new JButton("打开hosts 所在文件夹");
	private static final JButton flushButton = new JButton("刷新DNS 配置");
	public static List<Data> parserData = ThreadLocal.withInitial(() -> {
		String json;
		try {
			String s = System.getProperty("user.dir");
			File file = new File(s + "\\rules.json");
			json = FileUtils.readFileToString(file, "UTF-8");
			printToUserInterface("载入外附规则文件，使用");
		} catch (Exception e) {
			json = "[\n" +
					"  {\n" +
					"    \"name\": \"站长之家PC 版\",\n" +
					"    \"url\": \"http://tool.chinaz.com/dns?type=1&host=${website}&ip=\",\n" +
					"    \"cssQuery\": \"div.w60-0.tl\",\n" +
					"    \"replaceRegex\": \"(\\\\[(.+?)]|-)\"\n" +
					"  }\n" +
					"]";
			printToUserInterface("无外附规则文件，使用默认");
		}
		java.util.List<Data> data = new Gson().fromJson(json, new TypeToken<List<Data>>() {
		}.getType());
		printToUserInterface("规则：\n");
		for (Data d : data) {
			printToUserInterface("[" + d.name + "] ");
		}
		printToUserInterface("\n");
		return data;
	}).get();

	public UserInterface() {
		setTop();
		setMiddle();
		setBottle();

		setTitle("Search Hosts in Website");
		setSize(500, 500);
		//是否可改变大小
		setResizable(false);
		setLocationRelativeTo(null);
//		setLocation(1200, 200);
		//关闭窗口按钮
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setVisible(true);

		InfoPipe pipe = InfoPipe.getInstance();

		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				pipe.close();
			}
		});

		if (!System.getProperty("os.name").contains("indows")) {
			textArea.setText("目前仅支持Windows 2000/XP 及以上版本");
			setButtonStatusRunning(false);
			openFolderButton.setEnabled(false);
			flushButton.setEnabled(false);
		}
		//听说其在Win98,win me 中位于/Windows 下？

	}

//	private static List<Data> loadRules()

	private static void setButtonStatusRunning(boolean flag) {
		backupButton.setEnabled(flag);
		updateButton.setEnabled(flag);
		searchButton.setEnabled(flag);
		enableTwice.setEnabled(flag);
	}

	public static synchronized void printToUserInterface(String str) {
		textArea.append(str);
		try {
			Thread.sleep(10);
			textArea.setSelectionStart(textArea.getText().length());
		} catch (InterruptedException e) {
			printException(e);
		}
	}

	public static void initRun() {
		setButtonStatusRunning(false);
		textArea.setText("");
	}

	public static void end() {
		try {
			Thread.sleep(10);
			setButtonStatusRunning(true);
		} catch (InterruptedException e) {
			printException(e);
		}
		textArea.setSelectionStart(textArea.getText().length());
	}

	private static void printException(Exception e) {
		printToUserInterface("\nError in [" + e.getMessage() + "]\n");
	}

	private void setTop() {
		//顶栏
		JPanel append = new JPanel();
		append.setLayout(new GridLayout(1, 3));
		append.add(hostsTextField);
		searchButton.addActionListener(e -> {
			String s = hostsTextField.getText();
			String[] tmp = s.split("/");
			String uri = (s.startsWith("http:") | s.startsWith("https:")) ?
					tmp[2] : tmp[0];
			new Search(uri, parserData).execute();
			hostsTextField.setText(uri);
		});
		append.add(searchButton);
//		append.add(enableTwice);
		add(append, BorderLayout.NORTH);

	}

	private void setMiddle() {
		//中栏
		JPanel recodePanel = new JPanel();
		recodePanel.setLayout(new GridLayout(1, 1));
		//设置只读
		textArea.setEditable(false);
		//设置自动换行
		textArea.setLineWrap(true);
		JScrollPane scrollPane = new JScrollPane(textArea);
//		scrollBar = scrollPane.getVerticalScrollBar();
		//创建滚动窗格
		recodePanel.add(scrollPane);
		add(recodePanel, BorderLayout.CENTER);
	}

	private void setBottle() {
		//底栏
		JPanel backup = new JPanel();
		backup.setLayout(new GridLayout(1, 4));
		backupButton.addActionListener(e -> Common.backup());
		backup.add(backupButton);
		updateButton.addActionListener(e -> new Update(parserData).execute());
		backup.add(updateButton);
		openFolderButton.addActionListener(e -> Common.openEtc());
		backup.add(openFolderButton);
		flushButton.addActionListener(e -> Common.flushCache());
		backup.add(flushButton);
		add(backup, BorderLayout.SOUTH);
	}

}

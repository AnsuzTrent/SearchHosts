/*
 * Trent Inc.
 * Copyright (c) 2018- 2020.
 */
package org.apache.fraud.search;

import org.apache.commons.lang3.StringUtils;
import org.apache.fraud.search.rules.BaseParser;
import org.apache.fraud.search.rules.ChinaZ;

import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import java.awt.*;
import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.List;
import java.util.Objects;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class UI extends JFrame {
	/**
	 * 生成host
	 */
	static File editFile = new File(FileSystemView.getFileSystemView().getHomeDirectory().getPath() + "\\hosts");
	private static JTextField hosts = new JTextField();
	private static JButton search = new JButton("搜索");
	private static JTextArea textA = new JTextArea("请选择功能\n");
	private static JButton backupHosts = new JButton("备份");
	private static JButton updateHosts = new JButton("更新");
	private static JButton openFolder = new JButton("打开hosts 所在文件夹");
	private static JButton flushDNS = new JButton("刷新DNS 配置");
	private static JScrollBar scrollBar;
	private static String EtcPath = "C:\\Windows\\System32\\drivers\\etc";
	/**
	 * 系统host
	 */
	static File hostsPath = new File(EtcPath + "\\hosts");
	private static Vector<String> local = new Vector<>();

	UI() {
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
			Thread.sleep(10);
			scrollBar.setValue(scrollBar.getMaximum());
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	static void toFlushDNS() {
		try {
			Process process = Runtime.getRuntime().exec("ipconfig /flushDNS");
			BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream(), Charset.forName("GBK")));
			String line;
			while ((line = br.readLine()) != null)
				if (!StringUtils.isEmpty(line))
					appendString("\n" + line + "\n");

			br.close();
		} catch (IOException e) {
			appendString("\nError in \n" + e.getMessage() + "\n");
		}
	}

	private void setTop() {
		//顶栏
		JPanel append = new JPanel();
		append.setLayout(new GridLayout(1, 2));
		append.add(hosts);
		search.addActionListener(e -> new search().execute());
		append.add(search);
		add(append, BorderLayout.NORTH);

	}

	private void setMiddle() {
		//中栏
		JPanel recode = new JPanel();
		recode.setLayout(new GridLayout(1, 1));
		//设置只读
		textA.setEditable(false);
		//设置自动换行
		textA.setLineWrap(true);
		JScrollPane scrollPane = new JScrollPane(textA);
		scrollBar = scrollPane.getVerticalScrollBar();
		//创建滚动窗格
		recode.add(scrollPane);
		add(recode, BorderLayout.CENTER);
	}

	private void setBottle() {
		//底栏
		JPanel backup = new JPanel();
		backup.setLayout(new GridLayout(1, 4));
		backupHosts.addActionListener(e -> Backstage.Backup());
		backup.add(backupHosts);
		updateHosts.addActionListener(e -> new update().execute());
		backup.add(updateHosts);
		openFolder.addActionListener(e -> Backstage.OpenEtc());
		backup.add(openFolder);
		flushDNS.addActionListener(e -> toFlushDNS());
		backup.add(flushDNS);
		add(backup, BorderLayout.SOUTH);
	}

	static class update extends SwingWorker<Void, String> {
		@Override//后台任务
		protected Void doInBackground() {
			setButtonStatus(false);
			textA.setText("");

			BaseParser chinaz = new ChinaZ("");

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
					publish("\nError in \"" + e.getMessage() + "\"\n");
				}
			}


			return null;
		}

		@Override//更新信息
		protected void process(java.util.List<String> chunks) {
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
			if ("".equals(str)) {
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
					publish("\nError in \"" + e.getMessage() + "\"\n");
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
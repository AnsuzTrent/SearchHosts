/*
 * Trent Inc.
 * Copyright (c) 2018- 2020.
 */
package org.apache.fraud.search.common;

import org.apache.fraud.search.features.Common;
import org.apache.fraud.search.features.Search;
import org.apache.fraud.search.features.Update;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * @author trent
 */
public class UserInterface extends JFrame {

	private static final JTextField hostsTextField = new JTextField();
	private static final JButton searchButton = new JButton("搜索");
	private static final JTextArea textArea = new JTextArea("请选择功能\n");
	private static final JButton backupButton = new JButton("备份");
	private static final JButton updateButton = new JButton("更新");
	private static final JButton openFolderButton = new JButton("打开hosts 所在文件夹");
	private static final JButton flushButton = new JButton("刷新DNS 配置");
	private static JScrollBar scrollBar = null;
	public static JCheckBox enableTwice = new JCheckBox("开启二次搜索", false);

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

	private static void setButtonStatusRunning(boolean flag) {
		backupButton.setEnabled(flag);
		updateButton.setEnabled(flag);
		searchButton.setEnabled(flag);
		enableTwice.setEnabled(flag);
	}

	public static synchronized void appendString(String str) {
		textArea.append(str);
		try {
			Thread.sleep(10);
			scrollBar.setValue(scrollBar.getMaximum());
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
		scrollBar.setValue(scrollBar.getMaximum());
	}

	private void setTop() {
		//顶栏
		JPanel append = new JPanel();
		append.setLayout(new GridLayout(1, 3));
		append.add(hostsTextField);
		searchButton.addActionListener(e -> new Search(hostsTextField.getText()).execute());
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
		scrollBar = scrollPane.getVerticalScrollBar();
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
		updateButton.addActionListener(e -> new Update().execute());
		backup.add(updateButton);
		openFolderButton.addActionListener(e -> Common.openEtc());
		backup.add(openFolderButton);
		flushButton.addActionListener(e -> Common.flushCache());
		backup.add(flushButton);
		add(backup, BorderLayout.SOUTH);
	}

	private static void printException(Exception e) {
		appendString("\nError in [" + e.getMessage() + "]\n");
	}
}
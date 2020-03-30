/*
 * Trent Inc.
 * Copyright (c) 2018- 2020.
 */

package org.apache.fraud.search;

import javax.swing.filechooser.FileSystemView;
import java.awt.*;
import java.io.*;
import java.nio.file.Files;
import java.util.Collections;
import java.util.Vector;

/**
 * @author trent
 */
public class Backstage {
	/**
	 * 生成host
	 */
	static File editFile = new File(FileSystemView.getFileSystemView().getHomeDirectory().getPath() + "\\hosts");
	private static String EtcPath = "C:\\Windows\\System32\\drivers\\etc";
	/**
	 * 系统host
	 */
	static File hostsPath = new File(EtcPath + "\\hosts");
	private static Vector<String> local = new Vector<>();

	static String backup() {
		try {
			File backup = new File(editFile + ".bak");

			Files.deleteIfExists(backup.toPath());
			Files.copy(hostsPath.toPath(), backup.toPath());
//			appendString("已备份hosts 文件至  ：  " + backup.toPath() + "\n");
			return "已备份hosts 文件至 ：  " + backup.toPath();
		} catch (IOException e) {
//			appendString("\nError in \n" + e.getMessage() + "\n");
			return "Error in [" + e.getMessage() + "]";
		}
	}

	static String openEtc() {
		try {
			Desktop.getDesktop().open(new File(EtcPath));
			return "已打开";
		} catch (IOException e) {
			return "Error in [" + e.getMessage() + "]";
		}
	}

	private static String append(Vector<String> recode) {
		try {
			FileWriter fileWriter = new FileWriter(editFile, true);
			for (String str : recode) {
				appendString(str);
				fileWriter.write(str);
			}
			fileWriter.close();
		} catch (IOException e) {
			return "Error in [" + e.getMessage() + "]";
		}
	}

	private static int filterRules(String str) {
		if (str.startsWith("#") || "".equals(str)) {
			return 1;
		} else if (str.startsWith("10.") |
				str.startsWith("0.0.0.0") |
				str.startsWith("127.") |
//				str.startsWith("191.255.255.255") |
				str.startsWith("172.16.") |
				str.startsWith("172.17.") |
				str.startsWith("172.18.") |
				str.startsWith("172.19.") |
				str.startsWith("172.2") |
				str.startsWith("172.30.") |
				str.startsWith("172.31.") |
				str.startsWith("169.254.") |
				str.startsWith("192.168.")

		) {
			appendString("内网IP:\t" + str + "\n");
			local.addElement(str + "\n");
			return 2;
		} else {
			return 0;
		}
	}

	private static Vector<String> readHosts() {
		Vector<String> recode = new Vector<>();
		try {
			local.clear();
			FileReader fileReader = new FileReader(hostsPath);
			BufferedReader bufferedReader = new BufferedReader(fileReader);
			String s;
			//逐行读取文件记录
			while ((s = bufferedReader.readLine()) != null) {
				//过滤# 开头的注释以及空行
				if (filterRules(s) != 0) {
					continue;
				}
				//以空格作为分割点
				String[] fromFile = s.replace("\t", " ").split(" ");
				//过滤重复
				if (recode.indexOf(fromFile[1]) == -1) {
					recode.addElement(fromFile[1]);
				}
			}
			if (local.size() > 0) {
				local.addElement("\n");
			}
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

}

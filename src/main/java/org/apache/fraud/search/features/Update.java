/*
 * Trent Inc.
 * Copyright (c) 2018- 2020.
 */

package org.apache.fraud.search.features;

import org.apache.fraud.search.base.BaseData;
import org.apache.fraud.search.common.RulesChain;
import org.apache.fraud.search.common.UserInterface;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Vector;

import static org.apache.fraud.search.common.UserInterface.parserData;

/**
 * @author trent
 */
public class Update extends SwingWorker<Void, String> implements BaseData {

	private static final Vector<String> local = new Vector<>();

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

	@Override
	protected Void doInBackground() {
		UserInterface.initRun();
		Common.backup();

		Vector<String> urlsLocal;

		if ((urlsLocal = readHosts()) != null) {
			if (!urlsLocal.isEmpty()) {
				try {
					FileWriter fileWriter = new FileWriter(OBTAIN_FILE);
					fileWriter.write(proString());
					// 有内网ip 段
					if (local.size() > 0) {
						for (String s : local) {
							fileWriter.write(s);
						}
					}
					fileWriter.flush();
					fileWriter.close();
					local.clear();

					new RulesChain(parserData).exec(urlsLocal);

					//移动，但目前不能获取管理员权限写入C 盘
//					Files.move(editFile.toPath(), hostsPath.toPath());
				} catch (IOException e) {
					BaseData.printException(e);
				}
			}
		} else {
			publish("\n无记录，hosts 文件中没有需要更新的网址");
		}

		return null;
	}

	@Override
	protected void process(List<String> chunks) {
		for (String s : chunks) {
			BaseData.printToUserInterface(s);
		}
	}

	@Override
	protected void done() {
		UserInterface.end();
	}

	private Vector<String> readHosts() {
		Vector<String> recode = new Vector<>();
		try {
			local.clear();
			FileReader fileReader = new FileReader(HOSTS_PATH);
			BufferedReader bufferedReader = new BufferedReader(fileReader);
			String s;
			//逐行读取文件记录
			while ((s = bufferedReader.readLine()) != null) {
				String tmp = BaseData.filterRules(s);
				if (tmp.equals(s)) {    //过滤# 开头的注释以及空行
					//以空格作为分割点
					String[] fromFile = s.replace("\t", " ").split(" ");
					//过滤重复
					if (!recode.contains(fromFile[1])) {
						recode.addElement(fromFile[1]);
					}
				} else {
					if ("内网IP:".equals(tmp)) {
						publish("内网IP:\t" + s + "\n");
						local.addElement(s + "\n");
					}
				}
			}
			if (local.size() > 0) {
				local.addElement("\n");
			}
			fileReader.close();
			bufferedReader.close();
		} catch (IOException e) {
			BaseData.printException(e);
		}
		Collections.sort(recode);
		return recode.isEmpty() ? null : recode;
	}
}

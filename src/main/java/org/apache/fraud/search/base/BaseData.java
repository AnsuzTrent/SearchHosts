/*
 * Trent Inc.
 * Copyright (c) 2018- 2020.
 */

package org.apache.fraud.search.base;

import org.apache.fraud.search.common.InfoPipe;

import javax.swing.filechooser.FileSystemView;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * @author trent
 */
public interface BaseData {
	String ETC_PATH = "C:\\Windows\\System32\\drivers\\etc";
	/**
	 * 系统host
	 */
	File HOSTS_PATH = new File(ETC_PATH + "\\hosts");
	/**
	 * 生成host
	 */
	File OBTAIN_FILE = new File(FileSystemView.getFileSystemView().getHomeDirectory().getPath() + "\\hosts");
	/**
	 * 内网正则
	 */
	List<Pattern> internalIPFilter = ThreadLocal.withInitial(() -> {
		List<Pattern> ipFilterRegexList = new ArrayList<>();
		//A类地址范围：10.0.0.0—10.255.255.255
		ipFilterRegexList.add(Pattern.compile("^10\\.(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|[0-9])\\.(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|[0-9])\\.(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|[0-9])"));
		//B类地址范围: 172.16.0.0---172.31.255.255
		ipFilterRegexList.add(Pattern.compile("^172\\.(1[6789]|2[0-9]|3[01])\\.(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|[0-9])\\.(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|[0-9])"));
		//C类地址范围: 192.168.0.0---192.168.255.255
		ipFilterRegexList.add(Pattern.compile("^192\\.168\\.(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|[0-9])\\.(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|[0-9])"));
		ipFilterRegexList.add(Pattern.compile("127.0.0.1"));
		ipFilterRegexList.add(Pattern.compile("191.255.255.255"));

		return ipFilterRegexList;
	}).get();

	/**
	 * 显示到UI
	 *
	 * @param str 显示信息
	 */
	static void printToUserInterface(String str) {
		InfoPipe.getInstance().addInfo(str);
	}

	static void printException(Exception e) {
		printToUserInterface("\nError in [" + e.getMessage() + "]\n");
	}

	static String filterRules(String str) {
		//过滤空行，返回" "
		if (!str.equals("")) {
			//数字开头，否则返回" "
			if (Pattern.compile("[1-9]*").matcher(Character.toString(str.charAt(0))).matches()) {
				//过滤内网，返回"内网IP:"
				for (Pattern tmp : internalIPFilter) {
					if (tmp.matcher(str).find()) {
						return "内网IP:";
					}
				}
				//正常网址
				return str;
			}
		}
		return " ";
	}

}

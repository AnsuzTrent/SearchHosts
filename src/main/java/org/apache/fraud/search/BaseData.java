/*
 * Trent Inc.
 * Copyright (c) 2018- 2020.
 */

package org.apache.fraud.search;

import javax.swing.filechooser.FileSystemView;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Vector;

/**
 * @author trent
 */
public interface BaseData {
	/**
	 * 生成host
	 */
	File OBTAIN_FILE = new File(FileSystemView.getFileSystemView().getHomeDirectory().getPath() + "\\hosts");
	String ETC_PATH = "C:\\Windows\\System32\\drivers\\etc";
	/**
	 * 系统host
	 */
	File HOSTS_PATH = new File(ETC_PATH + "\\hosts");

	Method RETURN_STR_TO_USER_INTERFACE = ThreadLocal.withInitial(() -> {
		try {
			return UserInterface.class.getMethod("appendString");
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		}
		return null;
	}).get();

	Method INIT = ThreadLocal.withInitial(() -> {
		try {
			return UserInterface.class.getMethod("initRun");
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		}
		return null;
	}).get();

	Method END = ThreadLocal.withInitial(() -> {
		try {
			return UserInterface.class.getMethod("end");
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		}
		return null;
	}).get();

	/**
	 * 回调方法
	 *
	 * @param method 回调方法
	 */
	static void callFunc(Method method) {
		callFunc(method, null);
	}

	/**
	 * 回调方法
	 *
	 * @param method 方法
	 * @param str    参数
	 */
	static void callFunc(Method method, String str) {
		try {
			method.invoke(null, str == null ? null : str + "\n");
		} catch (IllegalAccessException | InvocationTargetException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 写入文件
	 *
	 * @param recode 搜索到的结果
	 */
	static void appendRecodeToFile(Vector<String> recode) {
		try {
			FileWriter fileWriter = new FileWriter(OBTAIN_FILE, true);
			for (String str : recode) {
				BaseData.callFunc(RETURN_STR_TO_USER_INTERFACE, str);
				fileWriter.write(str);
			}
			fileWriter.close();
		} catch (IOException e) {
			BaseData.callFunc(RETURN_STR_TO_USER_INTERFACE, "Error in [" + e.getMessage() + "]");
		}
	}


}

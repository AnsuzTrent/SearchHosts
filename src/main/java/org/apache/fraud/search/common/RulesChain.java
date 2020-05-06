/*
 * Trent Inc.
 * Copyright (c) 2018- 2020.
 */

package org.apache.fraud.search.common;

import org.apache.fraud.search.base.BaseData;
import org.apache.fraud.search.base.BaseParser;

import java.lang.reflect.Constructor;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RulesChain implements BaseData {
	private static final String rulesPath = "org.apache.fraud.search.rules";
	private static final List<Class<?>> clsList = ClassUtil.getClasses(rulesPath);
	private static final int maxFlag = clsList.size();
	private static final Vector<String> noResults = new Vector<>();

	private static int flag = 0;

	private static Vector<String> getVector(String url) {
		BaseParser parser;
		Vector<String> recode = null;

		try {
			Constructor<?> constructor = clsList.get(flag).getConstructor(String.class);
			parser = (BaseParser) constructor.newInstance(url);

//			parser.printName(flag);
			recode = parser.exec();
		} catch (Exception e) {
			printException(e);
		}

		return recode;
	}

	private static void moreTimes() {
		if (!noResults.isEmpty() & UserInterface.enableTwice.isSelected()) {
			printToUserInterface("等待下一次搜索\n");
			if (flag < maxFlag) {
				flag++;
			} else {
				return;
			}
			for (String url : noResults) {
				getVector(url);
			}
			moreTimes();
		}
	}

	private static void printToUserInterface(String str) {
		BaseData.printToUserInterface(str);
	}

	private static void printException(Exception e) {
		BaseData.printException(e);
	}

	public void exec(String url) {
		String[] tmp = url.split("/");

		String uri = (url.startsWith("http:") | url.startsWith("https:")) ?
				tmp[2] : tmp[0];

		Vector<String> recode = getVector(uri);

		if (!"none".equals(recode.get(0))) {
			BaseData.appendRecodeToFile(recode);
			printToUserInterface("\n 完成");
		} else {
			noResults.add(recode.get(1));
		}

		moreTimes();

	}

	public void exec(Vector<String> urls) {
		printToUserInterface("\n\n");

		//设定线程池，联网查询
		ExecutorService pool = Executors.newFixedThreadPool(8);
		for (String url : urls) {
			pool.execute(() -> {
				Vector<String> tmp = getVector(url);
				if (!"none".equals(tmp.get(0))) {
					BaseData.appendRecodeToFile(tmp);
				} else {
					noResults.add(tmp.get(1));
				}
			});
		}
		pool.shutdown();
		while (true) {
			if (pool.isTerminated()) {
				break;
			}
		}

		printToUserInterface("\n完成(" + (urls.size() - noResults.size()) + "/" + urls.size() + ")\n");

		moreTimes();

	}

}

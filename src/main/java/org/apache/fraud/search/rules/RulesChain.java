/*
 * Trent Inc.
 * Copyright (c) 2018- 2020.
 */

package org.apache.fraud.search.rules;

import org.apache.fraud.search.base.BaseData;
import org.apache.fraud.search.base.BaseParser;
import org.apache.fraud.search.common.ClassUtil;
import org.apache.fraud.search.common.UserInterface;

import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RulesChain implements BaseData {
	private final static int maxFlag = (ClassUtil.getClasses("org.apache.fraud.search.rules").size() - 1);
	private static int flag = 0;
	private static Vector<String> noResults = null;

	private static Vector<String> getVector(String url) {
		Vector<String> recode = null;
		BaseParser parser;
		switch (flag) {
			case 0:
				parser = new ChinaZP(url);
//				printParserName(parser.getName());
				recode = parser.exec();
				break;
			case 1:
				recode = new ChinaZP(url).exec();
				break;
			default:
				break;
		}

		return recode;
	}

	private static void moreTimes() {
		if (flag < maxFlag) {
			flag++;
		}

		if (!noResults.isEmpty() & UserInterface.enableTwice.isSelected()) {
			printToUserInterface("等待下一次搜索");
			for (String url : noResults)
				getVector(url);
		}
	}

	private static void printToUserInterface(String str) {
		BaseData.printToUserInterface(str);
	}

	private static void printParserName(String str) {
		printToUserInterface("正在使用[" + str + "] 进行第 " + flag + " 次查询\n");
	}

	public void exec(String url) {
		Vector<String> recode;

		recode = getVector(url);

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
		Vector<String> noRequest = new Vector<>();
		//设定线程池，联网查询
		ExecutorService pool = Executors.newFixedThreadPool(8);
		for (String url : urls) {
			pool.execute(() -> {
				Vector<String> tmp = getVector(url);
				if (!"none".equals(tmp.get(0))) {
					BaseData.appendRecodeToFile(tmp);
				} else {
					noRequest.add(tmp.get(1));
				}
			});
		}
		pool.shutdown();
		while (true) {
			if (pool.isTerminated()) {
				break;
			}
		}

		printToUserInterface("\n完成(" + (urls.size() - noRequest.size()) + "/" + urls.size() + ")\n");

		noResults = noRequest;

		moreTimes();

	}

}

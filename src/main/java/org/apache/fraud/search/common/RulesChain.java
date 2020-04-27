/*
 * Trent Inc.
 * Copyright (c) 2018- 2020.
 */

package org.apache.fraud.search.common;

import org.apache.fraud.search.base.BaseData;
import org.apache.fraud.search.base.BaseParser;
import org.apache.fraud.search.rules.ChinaZM;
import org.apache.fraud.search.rules.ChinaZP;
import org.apache.fraud.search.rules.IP138;

import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RulesChain implements BaseData {
	private final static int maxFlag = ClassUtil.getClasses("org.apache.fraud.search.rules").size();
	private static int flag = 0;
	private static final Vector<String> noResults = new Vector<>();

	private static Vector<String> getVector(String url) {
		Vector<String> recode = null;
		BaseParser parser;
		switch (flag) {
			case 0:
				parser = new ChinaZP(url);
//				parser.printName(flag);
				recode = parser.exec();
				break;
			case 1:
				parser = new ChinaZM(url);
//				parser.printName(flag);
				recode = parser.exec();
				break;

			case 2:
				parser = new IP138(url);
//				parser.printName(flag);
				recode = parser.exec();
				break;
			default:
				break;
		}

		return recode;
	}

	private static void moreTimes() {
		if (!noResults.isEmpty() & UserInterface.enableTwice.isSelected()) {
			printToUserInterface("等待下一次搜索");
			if (flag < maxFlag) {
				flag++;
			}
			for (String url : noResults)
				getVector(url);
		}
	}

	private static void printToUserInterface(String str) {
		BaseData.printToUserInterface(str);
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

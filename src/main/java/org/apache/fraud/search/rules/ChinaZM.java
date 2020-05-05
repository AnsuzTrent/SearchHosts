/*
 * Trent Inc.
 * Copyright (c) 2018- 2020.
 */

package org.apache.fraud.search.rules;

import org.apache.fraud.search.base.BaseParser;
import org.jsoup.nodes.Document;

import java.util.Vector;

public class ChinaZM extends BaseParser {

	public ChinaZM(String site) {
		super(site);
		this.name = "站长之家手机版";
	}

	@Override
	protected Vector<String> getResult() {
		String url = "https://mtool.chinaz.com/dns/?host=" + site + "&ip=&accessmode=1";
		Vector<String> recode;
		Vector<String> noResult = new Vector<>();
		noResult.add("none");

		try {
			Document doc = getDocumentFromPage(url);

			// 包括IP, "-", 其它奇怪的东西，需要考虑换正则式
			String[] ipTmp = doc.select("td.z-tc.c-39 > span.c-green").text()
					.split("(\\[.*?]| )");

			recode = makeRecode(ipTmp, site);

		} catch (Exception e) {
			printException(e, site);
			noResult.add(site);
			return noResult;
		}

		// 取得结果是否为空
		if (recode != null && !recode.isEmpty()) {
			recode.addElement("\n");
		} else {
			printToUserInterface("\n输入的网址:" + site + " 没有找到对应ip\n");
			noResult.add(site);
			return noResult;
		}

		return recode;
	}
}

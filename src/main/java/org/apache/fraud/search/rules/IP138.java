/*
 * Trent Inc.
 * Copyright (c) 2018- 2020.
 */

package org.apache.fraud.search.rules;

import org.apache.fraud.search.base.BaseParser;
import org.jsoup.nodes.Document;

import java.util.Vector;

public class IP138 extends BaseParser {

	public IP138(String site) {
		super(site);
		this.name = "IP138";
	}

	@Override
	protected Vector<String> getResult() {
		String url = "https://site.ip138.com/" + site;
		Vector<String> recode;
		Vector<String> noResult = new Vector<>();
		noResult.add("none");

		try {
			Document doc = getDocumentFromPage(url);

			// 包括IP, "-", 其它奇怪的东西，需要考虑换正则式
			String[] ipTmp = doc.select("div#curadress > p > a").text()
					.split(" ");

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

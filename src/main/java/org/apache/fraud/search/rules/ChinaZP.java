/*
 * Trent Inc.
 * Copyright (c) 2018- 2020.
 */

package org.apache.fraud.search.rules;

import org.apache.fraud.search.base.BaseParser;
import org.jsoup.nodes.Document;

import java.util.Collections;
import java.util.Vector;

/**
 * @author trent
 */
public class ChinaZP extends BaseParser {

	public ChinaZP(String site) {
		super(site);
		this.name = "站长之家PC 版";
	}

	@Override
	protected Vector<String> getResult() {
		String url = "http://tool.chinaz.com/dns?type=1&host=" + site + "&ip=";
		Vector<String> recode = new Vector<>();
		Vector<String> noResult = new Vector<>();
		noResult.add("none");

		try {
			Document doc = getDocumentFromPage(url);

			String host = doc.getElementById("host").attr("value");

			// 包括IP, "-", 其它奇怪的东西，需要考虑换正则式
			String[] ipTmp = doc.select("div.w60-0.tl").text()
					.split("\\[.*?]");

			String[] ip = new String[ipTmp.length];

			int i = 0, j = 0;
			while (i < ipTmp.length) {
				ipTmp[i] = ipTmp[i].replaceAll("([ \\-]|\\.\\.+)", "");
				if ("".equals(ipTmp[i])) {
					i++;
					continue;
				}
				ip[j++] = ipTmp[i++];
			}

			for (String s : ip) {
				if (s != null) {
					if (!recode.contains("\n" + s + " " + host) & !"-".equals(s)) {
						recode.addElement("\n" + s + " " + host);
					}
				}
			}

			Collections.sort(recode);

		} catch (Exception e) {
			// 可能联网超时
			printToUserInterface("\nError in [" + e.getMessage() + "]\nOf the \"" + site + "\"\n");
			noResult.add(site);
			return noResult;
		}

		// 取得结果是否为空
		if (!recode.isEmpty()) {
			recode.addElement("\n");
		} else {
			printToUserInterface("\n输入的网址:" + site + " 没有找到对应ip\n");
			noResult.add(site);
			return noResult;
		}

		return recode;
	}

}

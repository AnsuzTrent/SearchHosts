/*
 * Trent Inc.
 * Copyright (c) 2018- 2020.
 */

package org.apache.fraud.search.rules;

import org.apache.fraud.search.BaseData;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.Collections;
import java.util.Vector;

/**
 * @author trent
 */
public class ChinaZ extends BaseParser {

	public ChinaZ(String site) {
		super(site);
	}


	@Override
	Vector<String> getResult() {
		String url = "http://tool.chinaz.com/dns?type=1&host=" + site + "&ip=";
		Vector<String> recode = new Vector<>();

		try {
			Document doc = getDocumentFromPage(url);

			String host = doc.getElementById("host").attr("value");

			// 包括IP, "-", 其它奇怪的东西，需要考虑换正则式
			String[] ipTmp = doc.getElementsByClass("w60-0 tl").text()
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
					if (recode.indexOf("\n" + s + " " + host) == -1 & !"-".equals(s)) {
						recode.addElement("\n" + s + " " + host);
					}
				}
			}

			Collections.sort(recode);

		} catch (IOException e) {
			BaseData.printToUserInterface("\nError in [" + e.getMessage() + "]");
			return null;
		}

		// 取得结果是否为空
		if (!recode.isEmpty()) {
			recode.addElement("\n");
		} else {
			BaseData.printToUserInterface("\n输入的网址:" + site + " 没有找到对应ip");
			return null;
		}

		return recode;
	}

}
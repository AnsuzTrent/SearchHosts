/*
 * Trent Inc.
 * Copyright (c) 2018- 2020.
 */

package org.apache.fraud.search.base;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebClientOptions;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import org.apache.commons.logging.LogFactory;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.Collections;
import java.util.Vector;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Parser {
	protected String site;
	protected String name;
	protected String url;
	protected String cssQuery;
	protected String replaceRegex;

	public Parser(Data data, String site) {
		this.site = site;
		this.name = data.name;
		this.url = data.url.replace("${website}", site);
		this.cssQuery = data.cssQuery;
		this.replaceRegex = data.replaceRegex;
	}

	protected Document getDocumentFromPage(String url) throws IOException {
		//不打印日志
		LogFactory.getFactory().setAttribute("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.NoOpLog");
		java.util.logging.Logger.getLogger("com.gargoylesoftware").setLevel(Level.OFF);
		java.util.logging.Logger.getLogger("org.apache.http.client").setLevel(Level.OFF);

		//设置代理
//		System.setProperty("http.proxyHost", "127.0.0.1");
//		System.setProperty("http.proxyPort", "8090");

		//模拟
		WebClient webClient = new WebClient(BrowserVersion.CHROME);
		WebClientOptions webClientOptions = webClient.getOptions();

		webClientOptions.setUseInsecureSSL(true);
		//禁用CSS
		webClientOptions.setCssEnabled(false);
		webClientOptions.setThrowExceptionOnFailingStatusCode(false);
		//启用JS 解释器
		webClientOptions.setJavaScriptEnabled(true);
		//JS 错误时不抛出异常
		webClientOptions.setThrowExceptionOnScriptError(false);
		webClientOptions.setDoNotTrackEnabled(true);
		//连接超时时间
		webClientOptions.setTimeout(5 * 1000);

		HtmlPage page = webClient.getPage(url);

		//支持ajax
		webClient.setAjaxController(new NicelyResynchronizingAjaxController());
		//等待后台运行
		webClient.waitForBackgroundJavaScript(10 * 10000);
		webClient.setJavaScriptTimeout(5 * 1000);

		return Jsoup.parse(page.asXml());
	}

	protected Vector<String> makeRecode(String ipTmp, String host) {
		Vector<String> recode = new Vector<>();
		String ipRegex = "((2(5[0-5]|[0-4]\\d))|[0-1]?\\d{1,2})(\\.((2(5[0-5]|[0-4]\\d))|[0-1]?\\d{1,2})){3}";
		Matcher matcher = Pattern.compile(ipRegex).matcher(ipTmp);
		while (matcher.find()) {
			String s = matcher.group();
			String tmp = BaseData.filterRules(s);
			if (!tmp.equals(s)) {
				continue;
			}
			String s1 = "\n" + s + " " + host;
			if (!recode.contains(s1)) {
				recode.addElement(s1);
			}
		}

		Collections.sort(recode);

		return recode;
	}

	/**
	 * 获取结果
	 *
	 * @return 完整结果
	 */
	protected Vector<String> getResult() {
		Vector<String> recode;
		try {
			Document doc = getDocumentFromPage(url);

			String ipTmp = doc.select(cssQuery).text();
			if (!"".equals(replaceRegex)) {
				ipTmp = ipTmp.replaceAll(replaceRegex, "");
			}

			recode = makeRecode(ipTmp, site);

			// 取得结果是否为空
			if (recode != null && !recode.isEmpty()) {
				recode.addElement("\n");
			} else {
				recode = new Vector<>();
				recode.add("none");
				recode.add(site);
				recode.add("\n输入的网址:" + site + " 没有找到对应ip\n");
			}

		} catch (Exception e) {
			recode = new Vector<>();
			recode.add("none");
			recode.add(site);
			recode.add("\nError in [" + e.getMessage() + "]\n Of the \"" + site + "\"\n");
		}

		return recode;
	}

	public Vector<String> exec() {
		Vector<String> record = this.getResult();
		this.site = "";
		return record;
	}

	public void printName(int flag) {
		BaseData.printToUserInterface("正在使用[" + this.name + "] 进行第 " + flag + " 次查询\n");
	}

}

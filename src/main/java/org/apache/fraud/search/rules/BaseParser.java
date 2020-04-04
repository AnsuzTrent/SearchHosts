/*
 * Trent Inc.
 * Copyright (c) 2018- 2020.
 */

package org.apache.fraud.search.rules;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebClientOptions;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import org.apache.commons.logging.LogFactory;
import org.apache.fraud.search.BaseData;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.Vector;
import java.util.logging.Level;

/**
 * @author trent
 */
public abstract class BaseParser implements BaseData {

	String site;

	BaseParser(String site) {
		this.site = site;
	}

	Document getDocumentFromPage(String url) throws IOException {
		//不打印日志
		LogFactory.getFactory().setAttribute("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.NoOpLog");
		java.util.logging.Logger.getLogger("com.gargoylesoftware").setLevel(Level.OFF);
		java.util.logging.Logger.getLogger("org.apache.http.client").setLevel(Level.OFF);

		//模拟Chrome
		WebClient webClient = new WebClient(BrowserVersion.CHROME);
		WebClientOptions webClientOptions = webClient.getOptions();

		//禁用CSS
		webClientOptions.setCssEnabled(false);
		//启用JS 解释器
		webClientOptions.setJavaScriptEnabled(true);
		//JS 错误时不抛出异常
		webClientOptions.setThrowExceptionOnScriptError(false);
		webClientOptions.setThrowExceptionOnFailingStatusCode(false);
		//连接超时时间
		webClientOptions.setTimeout(2 * 1000);

		//设置代理
//		System.setProperty("http.proxyHost", "127.0.0.1");
//		System.setProperty("http.proxyPort", "8090");


		HtmlPage page = webClient.getPage(url);
		//等待后台运行
		webClient.waitForBackgroundJavaScript(10 * 1000);

		return Jsoup.parse(page.asXml(), url);
	}

	/**
	 * 获取结果
	 *
	 * @return 完整结果
	 */
	abstract Vector<String> getResult();

	public Vector<String> exec() {
		Vector<String> record = this.getResult();
		this.site = "";
		return record;
	}

}

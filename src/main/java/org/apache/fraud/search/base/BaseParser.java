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

/**
 * @author trent
 */
public abstract class BaseParser implements BaseData {

	protected String site;
	protected String name;

	public BaseParser(String site) {
		this.site = site;
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

	protected void printToUserInterface(String str) {
		BaseData.printToUserInterface(str);
	}

	protected static void printException(Exception e) {
		BaseData.printException(e);
	}

	protected static void printException(Exception e, String s) {
		BaseData.printToUserInterface("\nError in [" + e.getMessage() + "]\n Of the \"" + s + "\"");
	}

	protected Vector<String> makeRecode(String[] ipTmp, String host) {
		Vector<String> recode = new Vector<>();

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

		return recode;
	}

	/**
	 * 获取结果
	 *
	 * @return 完整结果
	 */
	protected abstract Vector<String> getResult();

	public Vector<String> exec() {
		Vector<String> record = this.getResult();
		this.site = "";
		return record;
	}

	public void printName(int flag) {
		printToUserInterface("正在使用[" + this.name + "] 进行第 " + flag + " 次查询\n");
	}

}

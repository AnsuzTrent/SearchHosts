/*
 * Trent Inc.
 * Copyright (c) 2018- 2020.
 */

package org.akvo.search.common;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebClientOptions;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import org.akvo.search.constant.PropertyConstant;
import org.akvo.search.constant.TextConstant;
import org.apache.commons.logging.LogFactory;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.Collections;
import java.util.Vector;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author trent
 */
public class Parser implements CommonFun {
    private final Rule rule;
    private final Pattern ipPattern = Pattern.compile("((2(5[0-5]|[0-4]\\d))|[0-1]?\\d{1,2})(\\.((2(5[0-5]|[0-4]\\d))|[0-1]?\\d{1,2})){3}");
    private String site;

    public Parser(Rule rule, String site) {
        this.site = site;
        this.rule = rule;

        String url = this.rule.getUrl().replace(PropertyConstant.REPLACE_SITE, site);
        this.rule.setUrl(url);
    }

    protected Document getDocumentFromPage(String url) throws IOException {
        // 不打印日志
        LogFactory.getFactory().setAttribute("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.NoOpLog");
        java.util.logging.Logger.getLogger("com.gargoylesoftware").setLevel(Level.OFF);
        java.util.logging.Logger.getLogger("org.apache.http.client").setLevel(Level.OFF);

        /// 设置代理
//		System.setProperty("http.proxyHost", "127.0.0.1");
//		System.setProperty("http.proxyPort", "8090");

        // 模拟 todo 这里会卡一下
        WebClient webClient = new WebClient(BrowserVersion.CHROME);
        WebClientOptions webClientOptions = webClient.getOptions();

        webClientOptions.setUseInsecureSSL(true);
        // 禁用CSS
        webClientOptions.setCssEnabled(false);
        webClientOptions.setThrowExceptionOnFailingStatusCode(false);
        // 启用JS 解释器
        webClientOptions.setJavaScriptEnabled(true);
        // JS 错误时不抛出异常
        webClientOptions.setThrowExceptionOnScriptError(false);
        webClientOptions.setDoNotTrackEnabled(true);
        // 连接超时时间
        webClientOptions.setTimeout(5 * 1000);
        // todo 这里卡很久
        HtmlPage page = webClient.getPage(url);

        // 支持ajax
        webClient.setAjaxController(new NicelyResynchronizingAjaxController());
        // 等待后台运行 todo 这里卡一下
        webClient.waitForBackgroundJavaScript(10 * 10000);
        webClient.setJavaScriptTimeout(5 * 1000);

        return Jsoup.parse(page.asXml());
    }

    protected Vector<String> makeRecode(String ipTmp, String host) {
        Vector<String> recode = new Vector<>();
        Matcher matcher = ipPattern.matcher(ipTmp);

        while (matcher.find()) {
            // 获得ip 型字符串
            String s = matcher.group();
            // 判断是否为内网ip
            String tmp = filterRules(s);
            if (!tmp.equals(s)) {
                continue;
            }
            String s1 = String.format(PropertyConstant.RECODE_FORMAT, s, host);
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
            Document doc = getDocumentFromPage(rule.getUrl());

            String ipTmp = doc.select(rule.getCssQuery()).text();

            String replaceRegex = rule.getReplaceRegex();
            if (!"".equals(replaceRegex)) {
                ipTmp = ipTmp.replaceAll(replaceRegex, "");
            }

            recode = makeRecode(ipTmp, site);

            // 取得结果是否为空
            if (recode != null && !recode.isEmpty()) {
                recode.addElement("\n");
            } else {
                recode = new Vector<>();
                recode.add(PropertyConstant.NONE_FLAG);
                recode.add(site);
                recode.add(String.format(TextConstant.NO_CORRESPOND_IP, site));
            }

        } catch (Exception e) {
            recode = new Vector<>();
            recode.add(PropertyConstant.NONE_FLAG);
            recode.add(site);
            recode.add(String.format(TextConstant.ERROR_FROM_SITE, e.getMessage(), site));
        }

        return recode;
    }

    public Vector<String> exec() {
        Vector<String> record = this.getResult();
        this.site = "";
        return record;
    }

//    public void printName(int flag) {
//        Factory.getFactory().getController()
//                .printInfo(String.format(TextConstant.NAME_INFO, rule.getName(), flag));
//    }

}

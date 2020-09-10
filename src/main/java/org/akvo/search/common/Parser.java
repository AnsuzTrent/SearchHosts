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
 * 解析器，用于获取源网页的信息并解析出结果集合
 *
 * @author trent
 */
public class Parser implements CommonFun {
    private final Rule rule;
    private final Pattern ipPattern = Pattern.compile("((2(5[0-5]|[0-4]\\d))|[0-1]?\\d{1,2})(\\.((2(5[0-5]|[0-4]\\d))|[0-1]?\\d{1,2})){3}");
    private String site;
    private final String url;

    /**
     * 解释器构造方法
     *
     * @param rule 解析规则
     * @param site 目标网址
     */
    public Parser(Rule rule, String site) {
        this.site = site;
        this.rule = rule;

        // 将通配${website} 换成真实网址
        this.url = this.rule.getUrl().replace(PropertyConstant.REPLACE_SITE, site);
    }

    /**
     * 获得JSoup 的Document 数据
     *
     * @param url 网址
     * @return Document数据
     * @throws IOException 获取网页可能的报错
     */
    protected Document getDocumentFromPage(String url) throws IOException {
        // 不打印日志
        LogFactory.getFactory().setAttribute("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.NoOpLog");
        java.util.logging.Logger.getLogger("com.gargoylesoftware").setLevel(Level.OFF);
        java.util.logging.Logger.getLogger("org.apache.http.client").setLevel(Level.OFF);

        /// 设置代理
//		System.setProperty("http.proxyHost", "127.0.0.1");
//		System.setProperty("http.proxyPort", "8090");

        // 模拟 todo 这里会卡一下
        WebClient webClient = new WebClient(BrowserVersion.FIREFOX);
        WebClientOptions options = webClient.getOptions();

        // 忽视ssl 证书错误
        options.setUseInsecureSSL(true);
        // 禁用CSS
        options.setCssEnabled(false);
        // 不丢状态码
        options.setThrowExceptionOnFailingStatusCode(false);
        // JS 错误时不抛出异常
        options.setThrowExceptionOnScriptError(false);
        // 不追踪请求
        options.setDoNotTrackEnabled(true);
        // 连接超时时间
        options.setTimeout(5 * 1000);
        // todo 这里卡很久
        HtmlPage page = webClient.getPage(url);

        // 支持ajax
        webClient.setAjaxController(new NicelyResynchronizingAjaxController());
        // 等待后台运行 todo 这里卡一下
        webClient.waitForBackgroundJavaScript(10 * 10000);
        // JS 脚本超时
        webClient.setJavaScriptTimeout(5 * 1000);

        return Jsoup.parse(page.asXml());
    }

    /**
     * 根据含有ip 和其他字符的字符串获得“ip 网址”集合
     *
     * @param ipTmp 含ip 的字符串
     * @param host  对应网址
     * @return 结果集
     */
    protected Vector<String> makeRecode(String ipTmp, String host) {
        Vector<String> recode = new Vector<>();
        Matcher matcher = ipPattern.matcher(ipTmp);

        while (matcher.find()) {
            // 匹配获得ip 字符串
            String s = matcher.group();
            // 判断是否为内网ip ，不是则返回值与本身相同
            String tmp = filterRules(s);
            if (!tmp.equals(s)) {
                continue;
            }
            // 格式化字符串
            String s1 = String.format(PropertyConstant.RECODE_FORMAT, s, host);
            if (!recode.contains(s1)) {
                // 没有与之前重复
                recode.addElement(s1);
            }
        }

        // 排序
        Collections.sort(recode);

        return recode;
    }

    /**
     * 获取结果
     *
     * @return 完整结果
     */
    protected Vector<String> getResult() {
        // 结果集合
        Vector<String> recode;
        try {
            // 获得网页节点
            Document doc = getDocumentFromPage(this.url);

            // 通过CSS 选择器获得包含所需结果的字符串化集合
            String ipTmp = doc.select(rule.getCssQuery()).text();

            // 依据规则中的正则式清除无关字符
            String replaceRegex = rule.getReplaceRegex();
            if (!"".equals(replaceRegex)) {
                ipTmp = ipTmp.replaceAll(replaceRegex, "");
            }

            // 获得结果集合
            recode = makeRecode(ipTmp, site);

            // 取得结果是否为空
            if (recode != null && !recode.isEmpty()) {
                // 结果集非空，在结尾加换行符
                recode.addElement("\n");
            } else {
                recode = new Vector<>();
                // 在结果集首位加空集合标识
                recode.add(PropertyConstant.NONE_FLAG);
                // 将当前网址加入集合，准备加入无结果集
                recode.add(site);
                // 将信息语句也放进集合
                recode.add(String.format(TextConstant.NO_CORRESPOND_IP, site));
            }

        } catch (Exception e) {
            recode = new Vector<>();
            // 在结果集首位加空集合标识
            recode.add(PropertyConstant.NONE_FLAG);
            // 将当前网址加入集合，准备加入无结果集
            recode.add(site);
            // 将报错信息语句也放进集合
            recode.add(String.format(TextConstant.ERROR_FROM_SITE, e.getMessage(), site));
        }

        return recode;
    }

    /**
     * 解释器执行入口
     *
     * @return 结果集，若为异常结果集则格式为{空集合标识, 无结果网址, 信息}
     */
    public Vector<String> exec() {
        // 获取结果集
        Vector<String> record = this.getResult();
        // 置空site，不过可能是没什么用处的句子
        this.site = "";
        return record;
    }

}

/*
 * Trent Inc.
 * Copyright (c) 2018- 2020.
 */

package org.akvo.search.common;

/**
 * 规则数据封装类
 *
 * @author trent
 */
public class Rule {
    private final String name;
    private final String cssQuery;
    private String replaceRegex;
    private String url;

    /**
     * 初始化规则
     *
     * @param name         源名称
     * @param url          源地址
     * @param cssQuery     CSS 选择器
     * @param replaceRegex 清理正则
     */
    public Rule(String name, String url, String cssQuery, String replaceRegex) {
        this.name = name;
        this.url = url;
        this.cssQuery = cssQuery;
        this.replaceRegex = replaceRegex;
    }

    public String getName() {
        return name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getCssQuery() {
        return cssQuery;
    }

    public String getReplaceRegex() {
        return replaceRegex;
    }

    public void setReplaceRegex(String replaceRegex) {
        this.replaceRegex = replaceRegex;
    }

    /**
     * 显示到下拉条的名字
     *
     * @return name
     */
    @Override
    public String toString() {
        return name;
    }
}

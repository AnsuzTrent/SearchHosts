/*
 * Trent Inc.
 * Copyright (c) 2018- 2020.
 */

package org.akvo.search.common;

/**
 * @author trent
 */
public class Rule {
    private String name;
    private String url;
    private String cssQuery;
    private String replaceRegex;

    public Rule(String name, String url, String cssQuery, String replaceRegex) {
        this.name = name;
        this.url = url;
        this.cssQuery = cssQuery;
        this.replaceRegex = replaceRegex;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public void setCssQuery(String cssQuery) {
        this.cssQuery = cssQuery;
    }

    public String getReplaceRegex() {
        return replaceRegex;
    }

    public void setReplaceRegex(String replaceRegex) {
        this.replaceRegex = replaceRegex;
    }

    @Override
    public String toString() {
        return name;
    }
}

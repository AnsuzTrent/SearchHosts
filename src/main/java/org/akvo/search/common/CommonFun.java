/*
 * Trent Inc.
 * Copyright (c) 2018- 2020.
 */

package org.akvo.search.common;

import org.akvo.search.constant.PropertyConstant;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * 一个常用方法的封装
 *
 * @author trent
 * @since JDK 1.8
 */
public interface CommonFun {
    Pattern COMPILE = Pattern.compile("[1-9]*");
    /**
     * A类地址范围：10.0.0.0—10.255.255.255
     */
    Pattern A_TYPE = Pattern.compile("^10\\.(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|[0-9])\\.(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|[0-9])\\.(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|[0-9])");
    /**
     * B类地址范围: 172.16.0.0---172.31.255.255
     */
    Pattern B_TYPE = Pattern.compile("^172\\.(1[6789]|2[0-9]|3[01])\\.(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|[0-9])\\.(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|[0-9])");
    Pattern B_TYPE_OTHER = Pattern.compile("191.255.255.255");
    /**
     * C类地址范围: 192.168.0.0---192.168.255.255
     */
    Pattern C_TYPE = Pattern.compile("^192\\.168\\.(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|[0-9])\\.(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|[0-9])");
    Pattern LOCALHOST = Pattern.compile("127.0.0.1");

    /**
     * 标记字符串
     *
     * @param str 源字符串
     * @return 结果，空行、非数字开头返回" "；内网IP 返回"内网IP:"；符合项返回自身
     */
    default String filterRules(String str) {
        List<Pattern> ipFilterRegexList = new ArrayList<>();
        ipFilterRegexList.add(A_TYPE);
        ipFilterRegexList.add(B_TYPE);
        ipFilterRegexList.add(C_TYPE);
        ipFilterRegexList.add(LOCALHOST);
        ipFilterRegexList.add(B_TYPE_OTHER);

        // 过滤空行，返回" "，其实返回啥都行反正用不上
        if (!"".equals(str)) {
            // 数字开头，否则返回" "
            if (COMPILE.matcher(Character.toString(str.charAt(0))).matches()) {
                // 过滤内网，返回"内网IP:"
                for (Pattern tmp : ipFilterRegexList) {
                    if (tmp.matcher(str).find()) {
                        return PropertyConstant.INTRANET;
                    }
                }
                // 正常网址，返回自身
                return str;
            }
        }
        return " ";
    }

}

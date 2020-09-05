/*
 * Trent Inc.
 * Copyright (c) 2018- 2020.
 */

package org.akvo.search.common;

import org.akvo.search.constant.TextConstant;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * @author trent
 * @ClassName: CommonFun
 * @Description:
 * @date 2020年09月05日
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
     * 过滤
     *
     * @param str 源字符串
     * @return 结果
     */
    default String filterRules(String str) {
        List<Pattern> ipFilterRegexList = new ArrayList<>();
        ipFilterRegexList.add(A_TYPE);
        ipFilterRegexList.add(B_TYPE);
        ipFilterRegexList.add(C_TYPE);
        ipFilterRegexList.add(LOCALHOST);
        ipFilterRegexList.add(B_TYPE_OTHER);

        // 过滤空行，返回" "
        if (!"".equals(str)) {
            // 数字开头，否则返回" "
            if (COMPILE.matcher(Character.toString(str.charAt(0))).matches()) {
                // 过滤内网，返回"内网IP:"
                for (Pattern tmp : ipFilterRegexList) {
                    if (tmp.matcher(str).find()) {
                        return TextConstant.INTRANET;
                    }
                }
                // 正常网址
                return str;
            }
        }
        return " ";
    }

}

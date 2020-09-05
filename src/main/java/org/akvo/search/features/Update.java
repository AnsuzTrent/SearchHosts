/*
 * Trent Inc.
 * Copyright (c) 2018- 2020.
 */

package org.akvo.search.features;

import org.akvo.search.common.*;
import org.akvo.search.constant.PropertyConstant;
import org.akvo.search.constant.TextConstant;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collections;
import java.util.Vector;

/**
 * 更新功能
 *
 * @author trent
 */
public class Update extends Base implements CommonFun {
    /**
     * 从hosts 文件中获得的内网映射集合
     */
    private final Vector<String> local = new Vector<>();

    /**
     * 更新操作
     *
     * @param rule 更新时所用规则
     */
    public Update(Rule rule) {
        this.rule = rule;
    }

    /**
     * 系统所使用的hosts 文件开头
     *
     * @return 字符串
     */
    private String proString() {
        return "# Copyright (c) 1993-2009 Microsoft Corp.\n" +
                "#\n" +
                "# This is getDocumentFromPage sample HOSTS file used by Microsoft TCP/IP for Windows.\n" +
                "#\n" +
                "# This file contains the mappings of IP addresses to host names. Each\n" +
                "# entry should be kept on an individual line. The IP address should\n" +
                "# be placed in the first column followed by the corresponding host name.\n" +
                "# The IP address and the host name should be separated by at least one\n" +
                "# space.\n" +
                "#\n" +
                "# Additionally, comments (such as these) may be inserted on individual\n" +
                "# lines or following the machine name denoted by getDocumentFromPage '#' symbol.\n" +
                "#\n" +
                "# For example:\n" +
                "#\n" +
                "#      102.54.94.97     rhino.acme.com          # source server\n" +
                "#       38.25.63.10     x.acme.com              # x client host\n" +
                "\n" +
                "# localhost name resolution is handled within DNS itself.\n" +
                "#\t127.0.0.1       localhost\n" +
                "#\t::1             localhost\n" +
                "\n";
    }

    @Override
    protected Void doInBackground() throws IOException {
        // 备份hosts 文件到桌面
        Factory.getFactory()
                .getController().backup();

        // 读取的hosts 文件字符集合
        Vector<String> urlsLocal = readHosts();

        if (urlsLocal != null) {
            // 非内网映射网址集合不空
            if (!urlsLocal.isEmpty()) {
                FileWriter fileWriter = new FileWriter(PropertyConstant.OBTAIN_FILE);
                // 在桌面上的hosts 文件里写入头字符串
                fileWriter.write(proString());
                // 有内网映射
                if (local.size() > 0) {
                    for (String s : local) {
                        // 直接写入桌面上的hosts 文件
                        fileWriter.write(s);
                    }
                }
                fileWriter.flush();
                fileWriter.close();

                // 查询
                new RulesChain().exec(rule, urlsLocal);

            }
        } else {
            // 输出hosts 为空无需要更新的信息
            Factory.getFactory()
                    .getController().printInfo(TextConstant.NO_RECORD_FROM_HOSTS);
        }

        // 清理内网映射集合
        local.clear();
        return null;
    }

    /**
     * 读取系统hosts 文件，并获得其中的网址
     *
     * @return 系统hosts 文件中的网址集合
     * @throws IOException 文件操作可能的错误
     */
    private Vector<String> readHosts() throws IOException {
        Vector<String> recode = new Vector<>();

        FileReader fileReader = new FileReader(PropertyConstant.HOSTS_PATH);
        BufferedReader bufferedReader = new BufferedReader(fileReader);
        String s;
        // 逐行读取文件记录
        while ((s = bufferedReader.readLine()) != null) {
            // 标记
            String tmp = filterRules(s);
            if (tmp.equals(s)) {
                // 过滤# 开头的注释以及空行
                // 以空格作为分割点，获得{ip, 网址}
                String[] fromFile = s.replace("\t", " ")
                        .split(" ");
                // 过滤重复
                if (!recode.contains(fromFile[1])) {
                    recode.addElement(fromFile[1]);
                }
            } else if (PropertyConstant.INTRANET.equals(tmp)) {
                Factory.getFactory()
                        .getController().printInfo(String.format(TextConstant.INTRANET, s));
                // 将当前记录加入集合
                if (!local.contains(s)) {
                    local.addElement(s + "\n");
                }
            }
        }
        if (local.size() > 0) {
            // 在结尾加个换行
            local.addElement("\n");
        }
        fileReader.close();
        bufferedReader.close();
        // 排序
        Collections.sort(recode);
        return recode.isEmpty() ? null : recode;
    }

}

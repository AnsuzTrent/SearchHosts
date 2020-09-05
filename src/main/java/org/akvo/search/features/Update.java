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
 * @author trent
 */
public class Update extends Base implements CommonFun {
    private static final Vector<String> LOCAL = new Vector<>();

    public Update(Rule rule) {
        this.rule = rule;
    }

    private static String proString() {
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
        Factory.getFactory()
                .getController().backup();

        Vector<String> urlsLocal;

        if ((urlsLocal = readHosts()) != null) {
            if (!urlsLocal.isEmpty()) {

                FileWriter fileWriter = new FileWriter(PropertyConstant.OBTAIN_FILE);
                fileWriter.write(proString());
                // 有内网ip 段
                if (LOCAL.size() > 0) {
                    for (String s : LOCAL) {
                        fileWriter.write(s);
                    }
                }
                fileWriter.flush();
                fileWriter.close();
                LOCAL.clear();

                new RulesChain().exec(rule, urlsLocal);

            }
        } else {
            Factory.getFactory()
                    .getController().printInfo(TextConstant.NO_RECORD_FROM_HOSTS);
        }

        return null;
    }

    private Vector<String> readHosts() throws IOException {
        Vector<String> recode = new Vector<>();

        LOCAL.clear();
        FileReader fileReader = new FileReader(PropertyConstant.HOSTS_PATH);
        BufferedReader bufferedReader = new BufferedReader(fileReader);
        String s;
        // 逐行读取文件记录
        while ((s = bufferedReader.readLine()) != null) {
            String tmp = filterRules(s);
            if (tmp.equals(s)) {
                // 过滤# 开头的注释以及空行
                // 以空格作为分割点
                String[] fromFile = s.replace("\t", " ").split(" ");
                // 过滤重复
                if (!recode.contains(fromFile[1])) {
                    recode.addElement(fromFile[1]);
                }
            } else {
                if (TextConstant.INTRANET.equals(tmp)) {
                    Factory.getFactory()
                            .getController().printInfo(TextConstant.INTRANET + s + "\n");

                    LOCAL.addElement(s + "\n");
                }
            }
        }
        if (LOCAL.size() > 0) {
            LOCAL.addElement("\n");
        }
        fileReader.close();
        bufferedReader.close();

        Collections.sort(recode);
        return recode.isEmpty() ? null : recode;
    }

}

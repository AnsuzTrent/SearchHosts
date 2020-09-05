/*
 * Trent Inc.
 * Copyright (c) 2018- 2020.
 */

package org.akvo.search.common;

import org.akvo.search.constant.PropertyConstant;
import org.akvo.search.constant.TextConstant;
import org.akvo.search.features.Backend;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author trent
 */
public class RulesChain {
    private final ArrayList<Rule> parserData;
    private final int maxFlag;
    private final Vector<String> noResults = new Vector<>();
    private final Backend controller = Factory.getFactory().getController();
    private int flag = -1;

    public RulesChain() {
        this.parserData = Factory.getFactory()
                .getController().getRules();
        this.maxFlag = parserData.size();
    }

    private void getVector(Rule rule, String url) throws IOException {
        Parser parser = new Parser(rule, url);
        Vector<String> recode = parser.exec();

        if (!PropertyConstant.NONE_FLAG.equals(recode.get(0))) {
            synchronized (this) {
                // 正常返回
                FileWriter fileWriter = new FileWriter(PropertyConstant.OBTAIN_FILE, true);
                for (String str : recode) {
                    // 显示到界面
                    controller.printResult(str);
                    // 写入文件
                    fileWriter.write(str);
                }
                fileWriter.close();
            }
        } else {
            // 无结果返回或错误返回
            noResults.add(recode.get(1));

            controller.printError(recode.get(2));
        }
    }

    private void moreTimes() throws IOException {
        if (!noResults.isEmpty() && controller.isSelect()) {
            if (flag < maxFlag) {
                ++flag;
            } else {
                return;
            }

            Rule rule = parserData.get(flag);
            controller.printInfo(TextConstant.NEXT_SEARCH);
            controller.printInfo(String.format(TextConstant.NAME_INFO, rule.getName(), flag + 2));

            Vector<String> none = new Vector<>(noResults);
            noResults.clear();
            exec(rule, none);
        }
    }

    public void exec(Rule rule, String url) throws IOException {
        getVector(rule, url);
        moreTimes();
    }

    public void exec(Rule rule, Vector<String> urls) throws IOException {
        // 设定线程池，联网查询
        // todo 改
        ExecutorService pool = Executors.newFixedThreadPool(8);
        for (String url : urls) {
            pool.execute(() -> {
                try {
                    getVector(rule, url);
                } catch (IOException e) {
                    controller.printError(e);
                }
            });
        }
        pool.shutdown();
        while (true) {
            if (pool.isTerminated()) {
                break;
            }
        }

        int i = urls.size() - noResults.size();
        controller.printResult(String.format(TextConstant.DONE, i, urls.size()));
        for (String s : noResults) {
            controller.printInfo(String.format(TextConstant.UNDONE, s));
        }
        moreTimes();
    }

}

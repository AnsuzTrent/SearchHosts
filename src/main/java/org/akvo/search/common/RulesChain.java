/*
 * Trent Inc.
 * Copyright (c) 2018- 2020.
 */

package org.akvo.search.common;

import org.akvo.search.constant.PropertyConstant;
import org.akvo.search.constant.TextConstant;
import org.akvo.search.controller.BackendController;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 规则集合
 *
 * @author trent
 * @since 1.8
 */
public class RulesChain {
    /**
     * 规则集合
     */
    private final ArrayList<Rule> parserData;
    /**
     * 规则数量
     */
    private final int maxFlag;
    /**
     * 未查到的地址集合
     */
    private final Vector<String> noResults = new Vector<>();
    /**
     * 控制器
     */
    private final BackendController controller = Factory.getFactory().getController();
    /**
     * 二次查找时用于标记当前下标
     */
    private int flag = -1;

    /**
     * 联网查询操作执行
     */
    public RulesChain() {
        this.parserData = Factory.getFactory()
                .getController().getRules();
        this.maxFlag = parserData.size();
    }

    /**
     * 获得结果并输出到界面、文件
     *
     * @param rule 规则
     * @param url  资源地址
     * @throws IOException 文件输出异常
     */
    private void getVector(Rule rule, String url) throws IOException {
        // 初始化解析器
        Parser parser = new Parser(rule, url);
        // 执行
        Vector<String> recode = parser.exec();

        // 判断首位是否为空集合标识
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
            // 无结果返回或错误返回，将无结果网址加入无结果集
            noResults.add(recode.get(1));
            // 在errorArea 上输出信息
            controller.printError(recode.get(2));
        }
    }

    /**
     * 二次查找功能
     *
     * @throws IOException 执行时的可能错误
     */
    private void moreTimes() throws IOException {
        if (!noResults.isEmpty() && controller.isSelected()) {
            // 判断是否遍历全部规则
            if (flag < maxFlag) {
                ++flag;
            } else {
                return;
            }

            // 获取当前规则
            Rule rule = parserData.get(flag);
            // 在infoArea 上输出相关信息
            controller.printInfo(TextConstant.NEXT_SEARCH);
            controller.printInfo(String.format(TextConstant.NAME_INFO, rule.getName(), flag + 2));

            // 获得内容与无结果集相同的集合
            Vector<String> none = new Vector<>(noResults);
            // 清空无结果集以避免出现重复
            noResults.clear();
            // 再查询无结果集
            exec(rule, none);
        }
    }

    /**
     * 单条查询执行入口
     *
     * @param rule 执行规则
     * @param url  目标网址
     * @throws IOException 可能出现的错误
     */
    public void exec(Rule rule, String url) throws IOException {
        getVector(rule, url);
        moreTimes();
    }

    /**
     * 多条查询执行入口
     *
     * @param rule 规则
     * @param urls 目标网址集合
     * @throws IOException 可能出现的错误
     */
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

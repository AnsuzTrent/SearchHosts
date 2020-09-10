/*
 * Trent Inc.
 * Copyright (c) 2018- 2020.
 */

package org.akvo.search.controller;

import org.akvo.search.common.Factory;
import org.akvo.search.common.Rule;
import org.akvo.search.constant.PropertyConstant;
import org.akvo.search.constant.TextConstant;
import org.akvo.search.features.Search;
import org.akvo.search.features.Update;
import org.akvo.search.view.MainView;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.ArrayList;

/**
 * 整体后台功能控制器
 *
 * @author trent
 * @since JDK 1.8
 */
public class BackendController {
    ArrayList<Rule> rules = null;
    boolean select;
    MainView view = null;

    /**
     * 初始化
     */
    public void init() {
        view = Factory.getFactory().getView();
        this.rules = view.getRules();
    }

    /**
     * 备份hosts 文件
     */
    public void backup() {
        try {
            File backup = new File(PropertyConstant.OBTAIN_FILE + ".bak");

            // 若存在同名文件则删除
            Files.deleteIfExists(backup.toPath());
            Files.copy(PropertyConstant.HOSTS_PATH.toPath(), backup.toPath());
            printInfo(String.format(TextConstant.BACKUP, backup.toPath().toString()));
        } catch (IOException e) {
            printError(e);
        }
    }

    /**
     * 搜索
     */
    public void search() {
        // 清理三个文本域
        clearArea();
        // 组件可操作性取消
        setComponentStatusRunning(false);

        JTextField hostsTextFiled = view.getHostsTextFiled();

        // 二次搜索功能开启标识
        this.select = view.getEnableTwice().isSelected();

        // 获取输入字符串
        String s = hostsTextFiled.getText().trim();
        // 分割，可能的组合如下：{http:/https:, , 网址, 其他的...},{网址, 其他的...}
        String[] tmp = s.split("/");
        String uri = (s.startsWith("http:") || s.startsWith("https:"))
                ? tmp[2]
                : tmp[0];

        // 获得下拉条中选中的规则
        Rule rule = (Rule) view.getRuleList().getSelectedItem();

        // 查询
        new Search(rule, uri).execute();

        // 将整理后的字符串显示到文本框
        hostsTextFiled.setText(uri);
    }

    /**
     * 更新
     */
    public void update() {
        // 清理文本域
        clearArea();
        // 设置组件可操作性
        setComponentStatusRunning(false);
        // 二次搜索功能开启标识
        this.select = view.getEnableTwice().isSelected();
        // 选中规则
        Rule rule = (Rule) view.getRuleList().getSelectedItem();

        // 更新
        new Update(rule).execute();
    }

    /**
     * 打开hosts 所在文件夹
     */
    public void openFolder() {
        try {
            Desktop.getDesktop()
                    .open(new File(PropertyConstant.ETC_PATH));
        } catch (IOException e) {
            printError(e);
        }
    }

    /**
     * 刷新DNS 缓存
     */
    public void flush() {
        try {
            // 执行命令
            Process process = Runtime.getRuntime().exec(PropertyConstant.DNS_FLUSH);
            // 以GBK 编码获取命令执行结果
            BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream(), Charset.forName("GBK")));
            String line;
            while ((line = br.readLine()) != null) {
                if (!StringUtils.isEmpty(line)) {
                    printInfo(line + "\n");
                }
            }

            br.close();
        } catch (IOException e) {
            printError(e);
        }
        printInfo("\n");
    }

    /**
     * 设置组件可操作性
     *
     * @param flag 是否可操作
     */
    public void setComponentStatusRunning(boolean flag) {
        view.getRuleList().setEnabled(flag);
        view.getBackupButton().setEnabled(flag);
        view.getUpdateButton().setEnabled(flag);
        view.getSearchButton().setEnabled(flag);
        view.getEnableTwice().setEnabled(flag);
    }

    /**
     * 清空文本域
     */
    public void clearArea() {
        view.getInfoArea().setText("");
        view.getErrorArea().setText("");
        view.getResultArea().setText("");
    }

    /**
     * 输出结果到resultArea
     *
     * @param s 字符串信息
     */
    public synchronized void printResult(String s) {
        JTextArea resultArea = Factory.getFactory()
                .getView().getResultArea();
        resultArea.append(s);
        resultArea.setSelectionStart(resultArea.getText().length());
    }

    /**
     * 输出结果到errorArea
     *
     * @param s 字符串化错误信息
     */
    public synchronized void printError(String s) {
        JTextArea errorArea = Factory.getFactory()
                .getView().getErrorArea();
        errorArea.append(s);
        errorArea.setSelectionStart(errorArea.getText().length());
    }

    /**
     * 输出结果到errorArea
     *
     * @param e 错误
     */
    public synchronized void printError(Exception e) {
        String s = String.format(TextConstant.ERROR_INFO_FORMATTER, e.getMessage());
        JTextArea errorArea = Factory.getFactory()
                .getView().getErrorArea();
        errorArea.append(s);
        errorArea.setSelectionStart(errorArea.getText().length());
    }

    /**
     * 输出结果到infoArea
     *
     * @param s 信息
     */
    public synchronized void printInfo(String s) {
        JTextArea infoArea = Factory.getFactory()
                .getView().getInfoArea();
        infoArea.append(s);
        infoArea.setSelectionStart(infoArea.getText().length());
    }

    /**
     * 获得规则集合
     *
     * @return 规则集合
     */
    public ArrayList<Rule> getRules() {
        return rules;
    }

    /**
     * 二次搜索是否被选中
     *
     * @return 是否选中
     */
    public boolean isSelected() {
        return select;
    }

}
/*
 * Trent Inc.
 * Copyright (c) 2018- 2020.
 */

package org.akvo.search.view;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.akvo.search.common.Factory;
import org.akvo.search.common.Rule;
import org.akvo.search.constant.CommandConstant;
import org.akvo.search.constant.PropertyConstant;
import org.akvo.search.controller.ButtonListener;
import org.apache.commons.io.FileUtils;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.ArrayList;

/**
 * 主界面
 *
 * @author trent
 */
public class MainView extends JFrame {
    private final JCheckBox enableTwice;
    private final JComboBox<Rule> ruleList;
    private final JTextField hostsTextFiled;
    private final JButton searchButton;

    private final JTextArea resultArea;
    private final JTextArea errorArea;
    private final JTextArea infoArea;

    private final JButton backupButton;
    private final JButton updateButton;
    private final JButton openFolderButton;
    private final JButton flushButton;

    ArrayList<Rule> rules;

    public MainView() {
        enableTwice = new JCheckBox("开启二次搜索", true);
        hostsTextFiled = new JTextField();
        searchButton = new JButton("搜索");
        ruleList = new JComboBox<>();

        resultArea = new JTextArea("请选择功能\n");
        errorArea = new JTextArea();
        infoArea = new JTextArea();

        backupButton = new JButton("备份");
        updateButton = new JButton("更新");
        openFolderButton = new JButton("打开hosts 所在文件夹");
        flushButton = new JButton("刷新DNS 配置");

        init();
    }

    /**
     * 初始化
     */
    public void init() {
        // 设置顶栏、中栏、底栏
        setTop();
        setMiddle();
        setBottle();

        // 设置标题
        setTitle(PropertyConstant.TITLE);

        // 原生风格顶部透明
        setUndecorated(true);
        // 设置当前风格顶部
        getRootPane().setWindowDecorationStyle(JRootPane.PLAIN_DIALOG);

        // 设置界面大小
        setSize(550, 550);
        // 设置是否可手动改变大小
        setResizable(false);
        // 居中
        setLocationRelativeTo(null);
        // 关闭窗口按钮可以关闭程序
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        // 查验是否为Windows
        isWindows();
        // 可见
        setVisible(true);
    }

    /**
     * 设置顶栏
     */
    private void setTop() {
        ButtonListener listener = Factory.getFactory().getListener();
        searchButton.setActionCommand(CommandConstant.SEARCH);
        searchButton.addActionListener(listener);

        setRules();

        // 顶栏
        JPanel searchPanel = new JPanel(new GridLayout(1, 0));

        searchPanel.add(hostsTextFiled);
        searchPanel.add(searchButton);
        searchPanel.add(ruleList);
        searchPanel.add(enableTwice);

        add(searchPanel, BorderLayout.NORTH);
    }

    /**
     * 设置中栏
     */
    private void setMiddle() {
        // 设置只读
        resultArea.setEditable(false);
        errorArea.setEditable(false);
        infoArea.setEditable(false);

        // 设置自动换行
        resultArea.setLineWrap(true);
        errorArea.setLineWrap(true);
        infoArea.setLineWrap(true);

        // 创建滚动窗格，将文本域套进去
        JScrollPane resultPane = new JScrollPane(resultArea);
        JScrollPane errorPane = new JScrollPane(errorArea);
        JScrollPane infoPane = new JScrollPane(infoArea);

        // 中栏整体
        JPanel middlePanel = new JPanel(new GridLayout(0, 2));

        // 中栏左
        JPanel majorPanel = new JPanel(new BorderLayout());
        majorPanel.add(resultPane);

        // 中栏右上
        JPanel infoPanel = new JPanel(new BorderLayout());
        infoPanel.add(infoPane);

        // 中栏右下
        JPanel errorPanel = new JPanel(new BorderLayout());
        errorPanel.add(errorPane);

        // 中栏右
        JPanel secondPanel = new JPanel(new GridLayout(2, 0));

        secondPanel.add(infoPanel);
        secondPanel.add(errorPanel);

        middlePanel.add(majorPanel);
        middlePanel.add(secondPanel);

        add(middlePanel, BorderLayout.CENTER);
    }

    /**
     * 设置底栏
     */
    private void setBottle() {
        ButtonListener listener = Factory.getFactory().getListener();

        backupButton.addActionListener(listener);
        updateButton.addActionListener(listener);
        openFolderButton.addActionListener(listener);
        flushButton.addActionListener(listener);

        backupButton.setActionCommand(CommandConstant.BACKUP);
        updateButton.setActionCommand(CommandConstant.UPDATE);
        openFolderButton.setActionCommand(CommandConstant.OPEN_FOLDER);
        flushButton.setActionCommand(CommandConstant.FLUSH);

        // 底栏
        JPanel backup = new JPanel(new GridLayout(1, 0));

        backup.add(backupButton);
        backup.add(updateButton);
        backup.add(openFolderButton);
        backup.add(flushButton);

        add(backup, BorderLayout.SOUTH);
    }

    /**
     * 检查是否为Windows 系统
     */
    public void isWindows() {
        // 听说其在Win98,win me 中位于/Windows 下？
        if (!System.getProperty(PropertyConstant.SYSTEM_PROPERTY)
                .contains(PropertyConstant.WINDOWS_PROPERTY)) {
            infoArea.setText("目前仅支持Windows 2000/XP 及以上版本");

            setComponentStatusRunning();
            openFolderButton.setEnabled(false);
            flushButton.setEnabled(false);
        }
    }

    /**
     * 设置部件可操作性
     */
    private void setComponentStatusRunning() {
        ruleList.setEnabled(false);
        backupButton.setEnabled(false);
        updateButton.setEnabled(false);
        searchButton.setEnabled(false);
        enableTwice.setEnabled(false);
    }

    /**
     * 设置下拉条内容
     */
    public void setRules() {
        String json;
        StringBuilder stringBuilder = new StringBuilder();
        try {
            // 获取运行位置
            String s = System.getProperty("user.dir");
            File file = new File(s + "\\rules.json");
            json = FileUtils.readFileToString(file, "UTF-8");
            stringBuilder.append("载入外附规则文件，使用");
        } catch (Exception e) {
            json = PropertyConstant.INNER_RULE_STR;
            stringBuilder.append("无外附规则文件，使用默认");
        }

        // 解析json
        rules = new Gson()
                .fromJson(json, new TypeToken<ArrayList<Rule>>() {
                }.getType());

        stringBuilder.append("规则：\n");
        for (Rule r : rules) {
            stringBuilder.append("[")
                    .append(r.getName())
                    .append("]");
            if (r.getReplaceRegex() == null) {
                stringBuilder.append(" (无清理正则)");
                r.setReplaceRegex("");
            }
            stringBuilder.append("\n");

            // 加入下拉条
            ruleList.addItem(r);
        }
        stringBuilder.append("\n");

        infoArea.append(stringBuilder.toString());
    }

    public JCheckBox getEnableTwice() {
        return enableTwice;
    }

    public JTextField getHostsTextFiled() {
        return hostsTextFiled;
    }

    public JButton getSearchButton() {
        return searchButton;
    }

    public JTextArea getResultArea() {
        return resultArea;
    }

    public JTextArea getErrorArea() {
        return errorArea;
    }

    public JTextArea getInfoArea() {
        return infoArea;
    }

    public JButton getBackupButton() {
        return backupButton;
    }

    public JButton getUpdateButton() {
        return updateButton;
    }

    public ArrayList<Rule> getRules() {
        return rules;
    }

    public JComboBox<Rule> getRuleList() {
        return ruleList;
    }

}

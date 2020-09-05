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
import org.akvo.search.controller.ActionController;
import org.apache.commons.io.FileUtils;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.ArrayList;

/**
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
        enableTwice = new JCheckBox("开启二次搜索", false);
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

    public void init() {
        setTop();
        setMiddle();
        setBottle();

        setTitle(PropertyConstant.TITLE);

        setUndecorated(true);
        getRootPane().setWindowDecorationStyle(JRootPane.PLAIN_DIALOG);

        setSize(550, 550);
        // 是否可改变大小
        setResizable(false);
        setLocationRelativeTo(null);
        // 关闭窗口按钮
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        isWindows();
        setVisible(true);
    }

    private void setTop() {
        ActionController controller = Factory.getFactory().getListener();
        searchButton.setActionCommand(CommandConstant.SEARCH);
        searchButton.addActionListener(controller);

        setRules();

        // 顶栏
        JPanel searchPanel = new JPanel(new GridLayout(1, 0));

        searchPanel.add(hostsTextFiled);
        searchPanel.add(searchButton);
        searchPanel.add(ruleList);
        searchPanel.add(enableTwice);

        add(searchPanel, BorderLayout.NORTH);
    }

    private void setMiddle() {
        // 设置只读
        resultArea.setEditable(false);
        errorArea.setEditable(false);
        infoArea.setEditable(false);

        // 设置自动换行
        resultArea.setLineWrap(true);
        errorArea.setLineWrap(true);
        infoArea.setLineWrap(true);

        // 创建滚动窗格
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

    private void setBottle() {
        ActionController controller = Factory.getFactory().getListener();

        backupButton.addActionListener(controller);
        updateButton.addActionListener(controller);
        openFolderButton.addActionListener(controller);
        flushButton.addActionListener(controller);

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

    public void isWindows() {
        // 听说其在Win98,win me 中位于/Windows 下？
        if (!System.getProperty(PropertyConstant.SYSTEM_PROPERTY)
                .contains(PropertyConstant.WINDOWS_PROPERTY)) {
            getInfoArea().setText("目前仅支持Windows 2000/XP 及以上版本");

            setComponentStatusRunning();
            getOpenFolderButton().setEnabled(false);
            getFlushButton().setEnabled(false);
        }
    }

    private void setComponentStatusRunning() {
        getRuleList().setEnabled(false);
        getBackupButton().setEnabled(false);
        getUpdateButton().setEnabled(false);
        getSearchButton().setEnabled(false);
        getEnableTwice().setEnabled(false);
    }

    public void setRules() {
        String json;
        StringBuilder stringBuilder = new StringBuilder();
        try {
            String s = System.getProperty("user.dir");
            File file = new File(s + "\\rules.json");
            json = FileUtils.readFileToString(file, "UTF-8");
            stringBuilder.append("载入外附规则文件，使用");
        } catch (Exception e) {
            json = PropertyConstant.INNER_RULE_STR;
            stringBuilder.append("无外附规则文件，使用默认");
        }


        rules = new Gson().fromJson(json, new TypeToken<ArrayList<Rule>>() {
        }.getType());

        stringBuilder.append("规则：\n");
        for (Rule d : rules) {
            stringBuilder.append("[")
                    .append(d.getName())
                    .append("] ");
            ruleList.addItem(d);
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

    public JButton getOpenFolderButton() {
        return openFolderButton;
    }

    public JButton getFlushButton() {
        return flushButton;
    }

    public ArrayList<Rule> getRules() {
        return rules;
    }

    public JComboBox<Rule> getRuleList() {
        return ruleList;
    }

}

/*
 * Trent Inc.
 * Copyright (c) 2018- 2020.
 */

/*
 * @Title: Backend
 * @Package org.akvo.search.features
 * @author trent
 * @date 2020年09月05日
 * @version V1.0
 */
package org.akvo.search.features;

import org.akvo.search.common.Factory;
import org.akvo.search.common.Rule;
import org.akvo.search.constant.PropertyConstant;
import org.akvo.search.constant.TextConstant;
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
 * @author trent
 * @ClassName: Backend
 * @Description:
 * @date 2020年09月05日
 * @since JDK 1.8
 */
public class Backend {
    ArrayList<Rule> rules = null;
    boolean select;
    MainView view = null;

    public void init() {
        view = Factory.getFactory().getView();
        this.rules = view.getRules();
    }

    public void backup() {
        try {
            File backup = new File(PropertyConstant.OBTAIN_FILE + ".bak");

            Files.deleteIfExists(backup.toPath());
            Files.copy(PropertyConstant.HOSTS_PATH.toPath(), backup.toPath());
            printInfo(String.format(TextConstant.BACKUP, backup.toPath().toString()));
        } catch (IOException e) {
            printError(e);
        }
    }

    public void search() {
        clearArea();
        setComponentStatusRunning(false);

        JTextField hostsTextFiled = view.getHostsTextFiled();

        this.select = view.getEnableTwice().isSelected();

        String s = hostsTextFiled.getText().trim();
        String[] tmp = s.split("/");
        String uri = (s.startsWith("http:") | s.startsWith("https:"))
                ? tmp[2]
                : tmp[0];

        Rule item = (Rule) view.getRuleList().getSelectedItem();

        new Search(item, uri).execute();

        hostsTextFiled.setText(uri);
    }

    public void update() {
        clearArea();
        setComponentStatusRunning(false);
        this.select = view.getEnableTwice().isSelected();

        Rule item = (Rule) view.getRuleList().getSelectedItem();

        new Update(item).execute();
    }

    public void openFolder() {
        try {
            Desktop.getDesktop()
                    .open(new File(PropertyConstant.ETC_PATH));
        } catch (IOException e) {
            printError(e);
        }
    }

    public void flush() {
        try {
            Process process = Runtime.getRuntime().exec(PropertyConstant.DNS_FLUSH);
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

    public void setComponentStatusRunning(boolean flag) {
        view.getRuleList().setEnabled(flag);
        view.getBackupButton().setEnabled(flag);
        view.getUpdateButton().setEnabled(flag);
        view.getSearchButton().setEnabled(flag);
        view.getEnableTwice().setEnabled(flag);
    }

    public void clearArea() {
        view.getInfoArea().setText("");
        view.getErrorArea().setText("");
        view.getResultArea().setText("");
    }

    public synchronized void printResult(String s) {
        JTextArea resultArea = Factory.getFactory()
                .getView().getResultArea();
        resultArea.append(s);
        resultArea.setSelectionStart(resultArea.getText().length());
    }

    public synchronized void printError(String s) {
        JTextArea errorArea = Factory.getFactory()
                .getView().getErrorArea();
        errorArea.append(s);
        errorArea.setSelectionStart(errorArea.getText().length());
    }

    public synchronized void printError(Exception e) {
        String s = String.format(TextConstant.ERROR_INFO_FORMATTER, e.getMessage());
        JTextArea errorArea = Factory.getFactory()
                .getView().getErrorArea();
        errorArea.append(s);
        errorArea.setSelectionStart(errorArea.getText().length());
    }

    public synchronized void printInfo(String s) {
        JTextArea infoArea = Factory.getFactory()
                .getView().getInfoArea();
        infoArea.append(s);
        infoArea.setSelectionStart(infoArea.getText().length());
    }

    public ArrayList<Rule> getRules() {
        return rules;
    }

    public boolean isSelect() {
        return select;
    }

}
package org.apache.fraud.search.common;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.io.FileUtils;
import org.apache.fraud.search.base.Data;
import org.apache.fraud.search.features.Common;
import org.apache.fraud.search.features.Search;
import org.apache.fraud.search.features.Update;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.List;

/**
 * @author trent
 */
public class UserInterface extends JFrame {
    public static final JCheckBox ENABLE_TWICE = new JCheckBox("开启二次搜索", false);
    private static final JTextField HOSTS_TEXT_FIELD = new JTextField();
    private static final JButton SEARCH_BUTTON = new JButton("搜索");
    private static final JTextArea TEXT_AREA = new JTextArea("请选择功能\n");
    private static final JButton BACKUP_BUTTON = new JButton("备份");
    private static final JButton UPDATE_BUTTON = new JButton("更新");
    private static final JButton OPEN_FOLDER_BUTTON = new JButton("打开hosts 所在文件夹");
    private static final JButton FLUSH_BUTTON = new JButton("刷新DNS 配置");
    public static List<Data> parserData = ThreadLocal.withInitial(() -> {
        String json;
        try {
            String s = System.getProperty("user.dir");
            File file = new File(s + "\\rules.json");
            json = FileUtils.readFileToString(file, "UTF-8");
            printToUserInterface("载入外附规则文件，使用");
        } catch (Exception e) {
            json = "[\n" +
                    "  {\n" +
                    "    \"name\": \"站长之家PC 版\",\n" +
                    "    \"url\": \"http://tool.chinaz.com/dns/?type=1&host=${website}&ip=\",\n" +
                    "    \"cssQuery\": \"div.w60-0.tl\",\n" +
                    "    \"replaceRegex\": \"(\\\\[(.+?)]|-)\"\n" +
                    "  }\n" +
                    "]";
            printToUserInterface("无外附规则文件，使用默认");
        }
        java.util.List<Data> data = new Gson().fromJson(json, new TypeToken<List<Data>>() {
        }.getType());
        printToUserInterface("规则：\n");
        for (Data d : data) {
            printToUserInterface("[" + d.name + "] ");
        }
        printToUserInterface("\n");
        return data;
    }).get();

    public UserInterface() {
        setTop();
        setMiddle();
        setBottle();

        setTitle("Search Hosts in Website");
        setUndecorated(true);
        getRootPane().setWindowDecorationStyle(JRootPane.PLAIN_DIALOG);

        setSize(500, 500);
        //是否可改变大小
        setResizable(false);
        setLocationRelativeTo(null);
//		setLocation(1200, 200);
        //关闭窗口按钮
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setVisible(true);

        InfoPipe pipe = InfoPipe.getInstance();

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                pipe.close();
            }
        });

        if (!System.getProperty(Constant.SYSTEM_PROPERTY).contains(Constant.WINDOWS_PROPERTY)) {
            TEXT_AREA.setText("目前仅支持Windows 2000/XP 及以上版本");
            setButtonStatusRunning(false);
            OPEN_FOLDER_BUTTON.setEnabled(false);
            FLUSH_BUTTON.setEnabled(false);
        }
        //听说其在Win98,win me 中位于/Windows 下？

    }

    private static void setButtonStatusRunning(boolean flag) {
        BACKUP_BUTTON.setEnabled(flag);
        UPDATE_BUTTON.setEnabled(flag);
        SEARCH_BUTTON.setEnabled(flag);
        ENABLE_TWICE.setEnabled(flag);
    }

    public static synchronized void printToUserInterface(String str) {
        TEXT_AREA.append(str);
        try {
            Thread.sleep(10);
            TEXT_AREA.setSelectionStart(TEXT_AREA.getText().length());
        } catch (InterruptedException e) {
            printException(e);
        }
    }

    public static void initRun() {
        setButtonStatusRunning(false);
        TEXT_AREA.setText("");
    }

    public static void end() {
        try {
            Thread.sleep(10);
            setButtonStatusRunning(true);
        } catch (InterruptedException e) {
            printException(e);
        }
        TEXT_AREA.setSelectionStart(TEXT_AREA.getText().length());
    }

    private static void printException(Exception e) {
        printToUserInterface("\nError in [" + e.getMessage() + "]\n");
    }

    private void setTop() {
        //顶栏
        JPanel append = new JPanel();
        append.setLayout(new GridLayout(1, 3));
        append.add(HOSTS_TEXT_FIELD);
        SEARCH_BUTTON.addActionListener(e -> {
            String s = HOSTS_TEXT_FIELD.getText();
            String[] tmp = s.split("/");
            String uri = (s.startsWith("http:") | s.startsWith("https:")) ?
                    tmp[2] : tmp[0];
            new Search(uri, parserData).execute();
            HOSTS_TEXT_FIELD.setText(uri);
        });
        append.add(SEARCH_BUTTON);
//		append.add(ENABLE_TWICE);
        add(append, BorderLayout.NORTH);

    }

    private void setMiddle() {
        //中栏
        JPanel recodePanel = new JPanel();
        recodePanel.setLayout(new GridLayout(1, 1));
        //设置只读
        TEXT_AREA.setEditable(false);
        //设置自动换行
        TEXT_AREA.setLineWrap(true);
        JScrollPane scrollPane = new JScrollPane(TEXT_AREA);
//		scrollBar = scrollPane.getVerticalScrollBar();
        //创建滚动窗格
        recodePanel.add(scrollPane);
        add(recodePanel, BorderLayout.CENTER);
    }

    private void setBottle() {
        //底栏
        JPanel backup = new JPanel();
        backup.setLayout(new GridLayout(1, 4));
        BACKUP_BUTTON.addActionListener(e -> Common.backup());
        backup.add(BACKUP_BUTTON);
        UPDATE_BUTTON.addActionListener(e -> new Update(parserData).execute());
        backup.add(UPDATE_BUTTON);
        OPEN_FOLDER_BUTTON.addActionListener(e -> Common.openEtc());
        backup.add(OPEN_FOLDER_BUTTON);
        FLUSH_BUTTON.addActionListener(e -> Common.flushCache());
        backup.add(FLUSH_BUTTON);
        add(backup, BorderLayout.SOUTH);
    }

}

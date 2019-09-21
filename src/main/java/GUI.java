/*
 * Trent Inc.
 * Copyright (c) 2018- 2019.
 */

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GUI extends JFrame implements ActionListener {
	private JButton jb0, jb1, jb2;//定义3个按钮
	private boolean flag = false;//设定一个标记.用于确定是否禁止使用jb1和jb2按钮

	//构造器--对窗口组件进行初始化
	public GUI() {
		//1.顶部面板 :  包含按钮 jb1和jb2
		JPanel jp1 = new JPanel();
		jb1 = new JButton("按钮1:背景变红");
		jb1.addActionListener(this);//给按钮添加事件响应,点按钮被点击时,执行本类的actionPerformed方法
		jb2 = new JButton("按钮2:背景变蓝");
		jb2.addActionListener(this);
		jp1.add(jb1);
		jp1.add(jb2);
		add(jp1, BorderLayout.NORTH);//把面板添加到窗口的顶部(北面)

		//2.顶部的面板:  包含jb0
		JPanel jp2 = new JPanel();
		jb0 = new JButton(flag ? "让按钮恢复使用" : "禁用其他按钮");//改变按钮的文字,如果flag为true就显示 恢复...flag为false就显示 禁用

		jb0.addActionListener(this);
		jp2.add(jb0);
		add(jp2, BorderLayout.SOUTH);//把面板添加到窗口的顶部(南面)

		//3. 窗口属性的设置
		setTitle("主窗口");//标题
		setSize(300, 260);//大小
		setLocationRelativeTo(null);//居中
		setDefaultCloseOperation(EXIT_ON_CLOSE);//退出窗口后关闭程序
		setVisible(true);//窗口可见
	}

	private static void Activity() {
		JOptionPane.showInputDialog(null, "这是一个可供用户输入信息的对话框");

	}

	@Override
	public void actionPerformed(ActionEvent e) {
		//首先判断是哪个按钮被点击了,然后执行相应的事件

		if (e.getSource() == jb0) {//如果是jb0按钮被点击
			jb1.setEnabled(flag);//让jb1不能使用(点击无效)
			jb2.setEnabled(flag);
			flag = !flag;//让标记取反
			jb0.setText(flag ? "让按钮恢复使用" : "禁用其他按钮");//改变按钮的文字

		} else if (e.getSource() == jb1) {//如果是按钮jb1被点击
			this.getContentPane().setBackground(Color.RED);//窗体的内容面板的背景色修改为红色
		} else if (e.getSource() == jb2) {//如果是jb2被点击
			this.getContentPane().setBackground(Color.BLUE);//窗体的内容面板的背景色修改为蓝色
		}

	}

}

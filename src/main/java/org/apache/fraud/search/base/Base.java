package org.apache.fraud.search.base;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.io.FileUtils;
import org.apache.fraud.search.common.InfoPipe;
import org.apache.fraud.search.common.UserInterface;

import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public abstract class Base extends SwingWorker<Void, String> {
	public static String ETC_PATH = "C:\\Windows\\System32\\drivers\\etc";
	/**
	 * 系统host
	 */
	public static File HOSTS_PATH = new File(ETC_PATH + "\\hosts");
	/**
	 * 生成host
	 */
	public static File OBTAIN_FILE = new File(FileSystemView.getFileSystemView().getHomeDirectory().getPath() + "\\hosts");
	/**
	 * 内网正则
	 */
	public static List<Pattern> internalIPFilter = ThreadLocal.withInitial(() -> {
		List<Pattern> ipFilterRegexList = new ArrayList<>();
		//A类地址范围：10.0.0.0—10.255.255.255
		ipFilterRegexList.add(Pattern.compile("^10\\.(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|[0-9])\\.(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|[0-9])\\.(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|[0-9])"));
		//B类地址范围: 172.16.0.0---172.31.255.255
		ipFilterRegexList.add(Pattern.compile("^172\\.(1[6789]|2[0-9]|3[01])\\.(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|[0-9])\\.(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|[0-9])"));
		//C类地址范围: 192.168.0.0---192.168.255.255
		ipFilterRegexList.add(Pattern.compile("^192\\.168\\.(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|[0-9])\\.(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|[0-9])"));
		ipFilterRegexList.add(Pattern.compile("127.0.0.1"));
		ipFilterRegexList.add(Pattern.compile("191.255.255.255"));

		return ipFilterRegexList;
	}).get();
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
					"    \"url\": \"http://tool.chinaz.com/dns?type=1&host=${website}&ip=\",\n" +
					"    \"cssQuery\": \"div.w60-0.tl\",\n" +
					"    \"replaceRegex\": \"(\\\\[(.+?)]|-)\"\n" +
					"  }\n" +
					"]";
			printToUserInterface("无外附规则文件，使用默认");
		}
		List<Data> data = new Gson().fromJson(json, new TypeToken<List<Data>>() {
		}.getType());
		printToUserInterface("规则：\n");
		for (Data d : data) {
			printToUserInterface("[" + d.name + "] ");
		}

		return data;
	}).get();

	/**
	 * 显示到UI
	 *
	 * @param str 显示信息
	 */
	public static void printToUserInterface(String str) {
		InfoPipe.getInstance().addInfo(str);
	}

	public static void printException(Exception e) {
		printToUserInterface("\nError in [" + e.getMessage() + "]\n");
	}

	public static String filterRules(String str) {
		//过滤空行，返回" "
		if (!str.equals("")) {
			//数字开头，否则返回" "
			if (Pattern.compile("[1-9]*").matcher(Character.toString(str.charAt(0))).matches()) {
				//过滤内网，返回"内网IP:"
				for (Pattern tmp : internalIPFilter) {
					if (tmp.matcher(str).find()) {
						return "内网IP:";
					}
				}
				//正常网址
				return str;
			}
		}
		return " ";
	}

	@Override
	protected void process(List<String> chunks) {
		for (String s : chunks) {
			printToUserInterface(s);
		}
	}

	@Override
	protected void done() {
		UserInterface.end();
	}

}

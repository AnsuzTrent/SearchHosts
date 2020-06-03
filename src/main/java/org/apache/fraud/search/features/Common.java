package org.apache.fraud.search.features;

import org.apache.commons.lang3.StringUtils;
import org.apache.fraud.search.base.BaseData;

import java.awt.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;

public class Common implements BaseData {

	public static void backup() {
		try {
			File backup = new File(OBTAIN_FILE + ".bak");

			Files.deleteIfExists(backup.toPath());
			Files.copy(HOSTS_PATH.toPath(), backup.toPath());
			BaseData.printToUserInterface("已备份hosts 文件至 ：  " + backup.toPath() + "\n\n");
		} catch (IOException e) {
			BaseData.printException(e);
		}
	}

	public static void flushCache() {
		try {
			Process process = Runtime.getRuntime().exec("ipconfig /flushDNS");
			BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream(), Charset.forName("GBK")));
			String line;
			while ((line = br.readLine()) != null) {
				if (!StringUtils.isEmpty(line)) {
					BaseData.printToUserInterface(line + "\n");
				}
			}

			br.close();
		} catch (IOException e) {
			BaseData.printException(e);
		}
	}

	public static void openEtc() {
		try {
			Desktop.getDesktop().open(new File(ETC_PATH));
		} catch (IOException e) {
			BaseData.printException(e);
		}
	}

}

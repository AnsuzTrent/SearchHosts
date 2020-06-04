package org.apache.fraud.search.features;

import org.apache.commons.lang3.StringUtils;
import org.apache.fraud.search.base.Base;

import java.awt.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;

import static org.apache.fraud.search.base.Base.*;

public class Common {

	public static void backup() {
		try {
			File backup = new File(OBTAIN_FILE + ".bak");

			Files.deleteIfExists(backup.toPath());
			Files.copy(HOSTS_PATH.toPath(), backup.toPath());
			Base.printToUserInterface("已备份hosts 文件至 ：  " + backup.toPath() + "\n\n");
		} catch (IOException e) {
			Base.printException(e);
		}
	}

	public static void flushCache() {
		try {
			Process process = Runtime.getRuntime().exec("ipconfig /flushDNS");
			BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream(), Charset.forName("GBK")));
			String line;
			while ((line = br.readLine()) != null) {
				if (!StringUtils.isEmpty(line)) {
					Base.printToUserInterface(line + "\n");
				}
			}

			br.close();
		} catch (IOException e) {
			Base.printException(e);
		}
	}

	public static void openEtc() {
		try {
			Desktop.getDesktop().open(new File(ETC_PATH));
		} catch (IOException e) {
			Base.printException(e);
		}
	}

}

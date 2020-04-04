/*
 * Trent Inc.
 * Copyright (c) 2018- 2020.
 */

package org.apache.fraud.search;

import org.apache.commons.lang3.StringUtils;

import java.awt.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;

/**
 * @author trent
 */
public class Backstage implements BaseData {

	void backup() {
		try {
			File backup = new File(OBTAIN_FILE + ".bak");

			Files.deleteIfExists(backup.toPath());
			Files.copy(HOSTS_PATH.toPath(), backup.toPath());
			BaseData.callFunc(RETURN_STR_TO_USER_INTERFACE, "已备份hosts 文件至 ：  " + backup.toPath());
		} catch (IOException e) {
			BaseData.callFunc(RETURN_STR_TO_USER_INTERFACE, "Error in [" + e.getMessage() + "]");
		}
	}

	void flushCache() {
		try {
			Process process = Runtime.getRuntime().exec("ipconfig /flushDNS");
			BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream(), Charset.forName("GBK")));
			String line;
			while ((line = br.readLine()) != null) {
				if (!StringUtils.isEmpty(line)) {
					BaseData.callFunc(RETURN_STR_TO_USER_INTERFACE, "\n" + line + "\n");
				}
			}

			br.close();
		} catch (IOException e) {
			BaseData.callFunc(RETURN_STR_TO_USER_INTERFACE, "Error in [" + e.getMessage() + "]");
		}
	}

	void openEtc() {
		try {
			Desktop.getDesktop().open(new File(ETC_PATH));
		} catch (IOException e) {
			e.getMessage();
		}
	}

}

/*
 * Trent Inc.
 * Copyright (c) 2018- 2020.
 */

package org.apache.fraud.search.features;

import org.apache.fraud.search.BaseData;
import org.apache.fraud.search.rules.ChinaZ;

import javax.swing.*;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Vector;

/**
 * @author trent
 */
public class Search extends SwingWorker<Void, String> implements BaseData {
	String url;

	public Search(String url) {
		this.url = url;
	}

	@Override
	protected Void doInBackground() {
		BaseData.callFunc(INIT_RUN);

		if ("".equals(url)) {
			publish("请在搜索栏中写入网址\n");
			return null;
		}
		try {
			Files.deleteIfExists(OBTAIN_FILE.toPath());
			Files.copy(HOSTS_PATH.toPath(), OBTAIN_FILE.toPath());
		} catch (IOException e) {
			publish("Error in [" + e.getMessage() + "]");
		}

		Vector<String> recode = new ChinaZ(url).exec();
		if (!recode.isEmpty()) {
			BaseData.appendRecodeToFile(recode);
			publish("\n 完成");
		} else {
			try {
				Files.deleteIfExists(OBTAIN_FILE.toPath());
			} catch (IOException e) {
				publish("Error in [" + e.getMessage() + "]");
			}
		}

		return null;
	}

	@Override
	protected void process(List<String> chunks) {
		for (String s : chunks) {
			BaseData.printToUserInterface(s);
		}
	}

	@Override
	protected void done() {
		BaseData.callFunc(END);
	}
}
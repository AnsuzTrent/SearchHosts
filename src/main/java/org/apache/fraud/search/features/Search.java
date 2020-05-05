/*
 * Trent Inc.
 * Copyright (c) 2018- 2020.
 */

package org.apache.fraud.search.features;

import org.apache.fraud.search.base.BaseData;
import org.apache.fraud.search.common.RulesChain;

import javax.swing.*;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

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
			try {
				publish("请在搜索栏中写入网址\n");
				Files.deleteIfExists(OBTAIN_FILE.toPath());
			} catch (IOException e) {
				BaseData.printException(e);
			}
			return null;
		}
		try {
			Files.deleteIfExists(OBTAIN_FILE.toPath());
			Files.copy(HOSTS_PATH.toPath(), OBTAIN_FILE.toPath());
		} catch (IOException e) {
			BaseData.printException(e);
		}

		//获取结果
		new RulesChain().exec(url);

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
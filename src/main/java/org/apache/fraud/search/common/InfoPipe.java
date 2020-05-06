/*
 * Trent Inc.
 * Copyright (c) 2018- 2020.
 */

package org.apache.fraud.search.common;

import org.apache.fraud.search.base.BaseData;

import java.util.Vector;

public class InfoPipe implements BaseData {
	private static InfoPipe instance = null;
	private static boolean flag = false;
	private final Vector<String> strings = new Vector<>();

	private InfoPipe() {
	}

	public static InfoPipe getInstance() {
		if (instance == null) {
			synchronized (InfoPipe.class) {
				if (instance == null) {
					instance = new InfoPipe();
					flag = true;
					instance.getThread().start();
				}
			}
		}
		return instance;
	}

	public synchronized void addInfo(Vector<String> recode) {
		this.strings.addAll(recode);
	}

	public synchronized void addInfo(String s) {
		this.strings.add(s);
	}

	public void close() {
		try {
			Thread.sleep(50);
			if (strings.size() > 0) {
				for (String s : strings) {
					BaseData.printToUserInterface(s);
				}
			}
			flag = false;
		} catch (InterruptedException e) {
			BaseData.printException(e);
		}
	}

	private Thread getThread() {
		return new IterateThread();
	}

	private class IterateThread extends Thread {
		public synchronized void run() {
			while (flag) {
				while (strings.size() > 0) {
					printToUI(strings.get(0));
					strings.remove(0);
				}
			}
		}

		private void printToUI(String str) {
			BaseData.callFunc(RETURN_STR_TO_USER_INTERFACE, str);
		}
	}
}
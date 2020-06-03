package org.apache.fraud.search.common;

import org.apache.fraud.search.base.BaseData;

import java.lang.reflect.Method;
import java.util.Vector;

public class InfoPipe implements BaseData {
	private static InfoPipe instance = null;
	private static boolean flag = false;
	private final Vector<String> strings = new Vector<>();

	private static final Method RETURN_STR_TO_USER_INTERFACE = ThreadLocal.withInitial(() -> {
		try {
			return UserInterface.class.getMethod("appendString", String.class);
		} catch (NoSuchMethodException e) {
			BaseData.printException(e);
		}
		return null;
	}).get();

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
					UserInterface.appendString(strings.get(0));
					strings.remove(0);
				}
			}
		}

	}
}
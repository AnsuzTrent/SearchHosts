package org.apache.fraud.search.common;

import org.apache.fraud.search.base.Base;

import java.util.Vector;

/**
 * @author trent
 */
public class InfoPipe {
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

    public synchronized void addInfo(String s) {
        this.strings.add(s);
    }

    public void close() {
        try {
            Thread.sleep(50);
            if (strings.size() > 0) {
                for (String s : strings) {
                    Base.printToUserInterface(s);
                }
            }
            flag = false;
        } catch (InterruptedException e) {
            Base.printException(e);
        }
    }

    private Thread getThread() {
        return new IterateThread();
    }

    private class IterateThread extends Thread {
        @Override
        public synchronized void run() {
            while (flag) {
                while (strings.size() > 0) {
                    UserInterface.printToUserInterface(strings.get(0));
                    strings.remove(0);
                }
            }
        }

    }
}
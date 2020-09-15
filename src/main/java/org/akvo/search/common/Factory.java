/*
 * Trent Inc.
 * Copyright (c) 2018- 2020.
 */

package org.akvo.search.common;

import org.akvo.search.controller.BackendController;
import org.akvo.search.controller.ButtonListener;
import org.akvo.search.view.MainView;

import java.awt.*;

/**
 * 单例工厂
 *
 * @author trent
 * @since JDK 1.8
 */
public class Factory {
    private static Factory factory = null;

    private MainView view = null;
    private ButtonListener listener = null;
    private BackendController controller = null;

    private Factory() {
    }

    /**
     * 获得工厂单例
     *
     * @return 单例对象
     */
    public static Factory getFactory() {
        if (factory == null) {
            synchronized (Factory.class) {
                if (factory == null) {
                    factory = new Factory();
                }
            }
        }
        return factory;
    }

    /**
     * 获得主界面单例
     *
     * @return 单例对象
     */
    public MainView getView() {
        if (view == null) {
            synchronized (Factory.class) {
                if (view == null) {
                    EventQueue.invokeLater(() -> view = new MainView());
                }
            }
        }
        return view;
    }

    /**
     * 获得按钮监听器单例
     *
     * @return 单例对象
     */
    public ButtonListener getListener() {
        if (listener == null) {
            synchronized (Factory.class) {
                if (listener == null) {
                    listener = new ButtonListener();
                }
            }
        }
        return listener;
    }

    /**
     * 获得控制器单例
     *
     * @return 单例对象
     */
    public BackendController getController() {
        if (controller == null) {
            synchronized (Factory.class) {
                if (controller == null) {
                    controller = new BackendController();
                }
            }
        }
        return controller;
    }

}
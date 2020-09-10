/*
 * Trent Inc.
 * Copyright (c) 2018- 2020.
 */

package org.akvo.search.controller;

import org.akvo.search.common.Factory;
import org.akvo.search.constant.CommandConstant;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * 监听器
 *
 * @author trent
 * @date 2020年09月03日
 * @since JDK 1.8
 */
public class ButtonListener implements ActionListener {
    @Override
    public void actionPerformed(ActionEvent e) {
        BackendController backendController = Factory.getFactory().getController();
        // 初始化控制器
        backendController.init();

        Factory.getFactory()
                .getView()
                .getResultArea().requestFocus();

        switch (e.getActionCommand()) {
            case CommandConstant.SEARCH:
                backendController.search();
                break;
            case CommandConstant.BACKUP:
                backendController.backup();
                break;
            case CommandConstant.UPDATE:
                backendController.update();
                break;
            case CommandConstant.OPEN_FOLDER:
                backendController.openFolder();
                break;
            case CommandConstant.FLUSH:
                backendController.flush();
                break;
            default:
                break;
        }
    }
}
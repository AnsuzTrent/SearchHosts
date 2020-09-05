/*
 * Trent Inc.
 * Copyright (c) 2018- 2020.
 */

/*
 * @Title: ActionController
 * @Package org.akvo.search.controller
 * @author trent
 * @date 2020年09月03日
 * @version V1.0
 */
package org.akvo.search.controller;

import org.akvo.search.common.Factory;
import org.akvo.search.constant.CommandConstant;
import org.akvo.search.features.Backend;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * @author trent
 * @ClassName: ActionController
 * @Description:
 * @date 2020年09月03日
 * @since JDK 1.8
 */
public class ActionController implements ActionListener {
    @Override
    public void actionPerformed(ActionEvent e) {
        Backend backend = Factory.getFactory().getController();
        backend.init();

        Factory.getFactory()
                .getView()
                .getResultArea().requestFocus();

        switch (e.getActionCommand()) {
            case CommandConstant.SEARCH:
                backend.search();
                break;
            case CommandConstant.BACKUP:
                backend.backup();
                break;
            case CommandConstant.UPDATE:
                backend.update();
                break;
            case CommandConstant.OPEN_FOLDER:
                backend.openFolder();
                break;
            case CommandConstant.FLUSH:
                backend.flush();
                break;
            default:
                break;
        }
    }
}
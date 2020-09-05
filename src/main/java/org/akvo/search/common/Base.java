/*
 * Trent Inc.
 * Copyright (c) 2018- 2020.
 */

package org.akvo.search.common;

import javax.swing.*;
import java.util.List;

/**
 * SwingWork 基类，用于简略公共代码
 *
 * @author trent
 */
public abstract class Base extends SwingWorker<Void, String> {
    /**
     * 所选规则
     */
    protected Rule rule = null;

    /**
     * 接收publish 方法数据，这里用不到
     *
     * @param chunks publish 传的数据
     */
    @Override
    protected void process(List<String> chunks) {
    }

    /**
     * 任务完毕执行方法
     */
    @Override
    protected void done() {
        Factory.getFactory().getController()
                .setComponentStatusRunning(true);
    }

}

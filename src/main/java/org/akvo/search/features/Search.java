/*
 * Trent Inc.
 * Copyright (c) 2018- 2020.
 */

package org.akvo.search.features;

import org.akvo.search.common.Base;
import org.akvo.search.common.Factory;
import org.akvo.search.common.Rule;
import org.akvo.search.common.RulesChain;
import org.akvo.search.constant.PropertyConstant;
import org.akvo.search.constant.TextConstant;

import java.io.IOException;
import java.nio.file.Files;

/**
 * 查询功能
 *
 * @author trent
 */
public class Search extends Base {
    String url;

    /**
     * 查询
     *
     * @param rule 查询规则
     * @param url  查询地址
     */
    public Search(Rule rule, String url) {
        this.url = url;
        this.rule = rule;
    }

    /**
     * 后台执行查询
     *
     * @return 当前线程的返回值，这里没有用上所以在设置为void 的包装型Void
     * @throws IOException 文件操作可能出现的错误
     */
    @Override
    protected Void doInBackground() throws IOException {
        if ("".equals(url)) {
            // 输入为空则提示检查
            Factory.getFactory().getController()
                    .printInfo(TextConstant.INPUT_SITE_INFO);

            return null;
        }
        // 桌面上有hosts 文件则删除
        Files.deleteIfExists(PropertyConstant.OBTAIN_FILE.toPath());
        // 将系统hosts 文件复制到桌面
        Files.copy(PropertyConstant.HOSTS_PATH.toPath(), PropertyConstant.OBTAIN_FILE.toPath());

        // 执行查询
        new RulesChain().exec(rule, url);

        return null;
    }

}
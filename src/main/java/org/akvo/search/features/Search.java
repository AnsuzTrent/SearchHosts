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
 * @author trent
 */
public class Search extends Base {
    String url;

    public Search(Rule rule, String url) {
        this.url = url;
        this.rule = rule;
    }

    @Override
    protected Void doInBackground() throws IOException {
        Files.deleteIfExists(PropertyConstant.OBTAIN_FILE.toPath());

        if ("".equals(url)) {
            Factory.getFactory().getController()
                    .printInfo(TextConstant.INPUT_SITE_INFO);

            return null;
        }
        Files.copy(PropertyConstant.HOSTS_PATH.toPath(), PropertyConstant.OBTAIN_FILE.toPath());

        // 获取结果
        new RulesChain().exec(rule, url);

        return null;
    }

}
package org.apache.fraud.search.features;

import org.apache.fraud.search.base.Base;
import org.apache.fraud.search.base.Data;
import org.apache.fraud.search.common.RulesChain;
import org.apache.fraud.search.common.UserInterface;

import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

/**
 * @author trent
 */
public class Search extends Base {
    String url;

    public Search(String url, List<Data> rules) {
        super(rules);
        this.url = url;
    }

    @Override
    protected Void doInBackground() {
        UserInterface.initRun();
        try {
            if ("".equals(url)) {
                publish("请在搜索栏中写入网址\n");
                Files.deleteIfExists(OBTAIN_FILE.toPath());
                return null;
            }
            Files.deleteIfExists(OBTAIN_FILE.toPath());
            Files.copy(HOSTS_PATH.toPath(), OBTAIN_FILE.toPath());
        } catch (IOException e) {
            Base.printException(e);
        }

        //获取结果
        new RulesChain(parserData).exec(url);

        return null;
    }

}
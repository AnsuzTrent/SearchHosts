package org.apache.fraud.search.common;

import org.apache.fraud.search.base.Base;
import org.apache.fraud.search.base.Data;
import org.apache.fraud.search.base.Parser;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.apache.fraud.search.base.Base.OBTAIN_FILE;

/**
 * @author trent
 */
public class RulesChain {
    private final List<Data> parserData;
    private final int maxFlag;
    private final Vector<String> noResults = new Vector<>();

    private int flag = 2;

    public RulesChain(List<Data> parserData) {
        this.parserData = parserData;
        this.maxFlag = parserData.size();
    }

    private void getVector(String url) {
        Parser parser = new Parser(parserData.get(0), url);
        Vector<String> recode = parser.exec();

        if (!Constant.NONE_FLAG.equals(recode.get(0))) {
            synchronized (this) {
                try {
                    FileWriter fileWriter = new FileWriter(OBTAIN_FILE, true);
                    for (String str : recode) {
                        Base.printToUserInterface(str);
                        fileWriter.write(str);
                    }
                    fileWriter.close();
                } catch (IOException e) {
                    Base.printException(e);
                }
            }
        } else {
            noResults.add(recode.get(1));
            Base.printToUserInterface(recode.get(2));
        }
    }

    private void moreTimes() {
        if (!noResults.isEmpty() & UserInterface.ENABLE_TWICE.isSelected()) {
            if (flag < maxFlag) {
                flag++;
            } else {
                return;
            }
            Base.printToUserInterface("等待下一次搜索\n");
            for (String url : noResults) {
                getVector(url);
            }
            moreTimes();
        }
    }

    public void exec(String url) {
        getVector(url);
        Base.printToUserInterface("\n 完成");
        moreTimes();
    }

    public void exec(Vector<String> urls) {
        Base.printToUserInterface("\n\n");

        //设定线程池，联网查询
        ExecutorService pool = Executors.newFixedThreadPool(8);
        for (String url : urls) {
            pool.execute(() -> getVector(url));
        }
        pool.shutdown();
        while (true) {
            if (pool.isTerminated()) {
                break;
            }
        }

        Base.printToUserInterface("\n完成(" + (urls.size() - noResults.size()) + "/" + urls.size() + ")\n");
        for (String s : noResults) {
            Base.printToUserInterface("\n未完成" + s + "\n");
        }
        moreTimes();

    }

}

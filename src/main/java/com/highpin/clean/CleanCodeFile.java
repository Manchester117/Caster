package com.highpin.clean;

import com.highpin.tools.Utility;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;

/**
 * Created by Peng.Zhao on 2015/12/3.
 */
public class CleanCodeFile {
    public static Logger logger = LogManager.getLogger(CleanCodeFile.class.getName());
    // 清理报告和截图
    public void cleanReportAndScreenShot() {
        File reports = new File("reports");
        Utility.deleteFiles(reports);
    }

    // 删除代码文件/报告/截图
    public static void main(String[] args) {
        CleanCodeFile ccf = new CleanCodeFile();
        ccf.cleanReportAndScreenShot();
        Utility.cleanCodeFile();
        logger.info("所有文件清理完毕!");
    }
}

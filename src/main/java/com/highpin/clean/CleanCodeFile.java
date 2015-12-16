package com.highpin.clean;

import com.highpin.tools.Utility;

import java.io.File;

/**
 * Created by Administrator on 2015/12/3.
 */
public class CleanCodeFile {
    public void cleanReportAndScreenShot() {
        String reportPath = "report";
        String screenShotPath = "screenshot";

        this.deleteFiles(reportPath);
        this.deleteFiles(screenShotPath);
    }

    private void deleteFiles(String path) {
        File folder = new File(path);
        File [] fileList = folder.listFiles();
        if (fileList != null) {
            for (File file: fileList) {
                file.delete();
            }
        }
    }
    // 删除代码文件/报告/截图
    public static void main(String[] args) {
        CleanCodeFile ccf = new CleanCodeFile();
        ccf.cleanReportAndScreenShot();
        Utility.cleanCodeFile();
    }
}

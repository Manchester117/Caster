package com.highpin.clean;

import com.highpin.tools.Utility;

import java.io.File;

/**
 * Created by Administrator on 2015/12/3.
 */
public class CleanCodeFile {
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
        System.out.println("清理完毕!");
    }
}

package com.highpin.tools;

import com.google.gson.Gson;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import java.io.*;

/**
 * Created by Administrator on 2015/12/3.
 */
public class Utility {
    /**
     * @Description: 复制字节码文件到target当中的com.highpin.test包中
     * @return 如果target文件夹存在以及复制成功则返回true
     */
    public static boolean copyByteCode() {
        File codeDest = new File("target");
        String codeDestPath = "target/classes/com/highpin/test";
        boolean createFlag = false;
        if (codeDest.exists() && codeDest.isDirectory()) {
            File testByteFolder = new File(codeDestPath);
            createFlag = testByteFolder.mkdirs();
        }

        File fileSources = new File("./src/main/java/com/highpin/test");
        File [] codeFileList = fileSources.listFiles();
        boolean moveFlag = false;
        if (codeFileList != null) {
            for (File codeFile: codeFileList) {
                // 过滤Java文件
                if (codeFile.getName().endsWith(".class")) {
                    moveFlag = codeFile.renameTo(new File(codeDestPath + File.separator + codeFile.getName()));
                }
            }
        } else {
            moveFlag = false;
        }
        System.out.println("************************************************复制代码************************************************");
        return createFlag && moveFlag;
    }

    /**
     * @Description: 屏幕截图方法
     * @param driver -- 浏览器对象
     * @param screenShotName -- 截图的文件名
     * @return destImagePath -- 截图的存放路径
     */
    public static String captureScreenShot(WebDriver driver, String screenShotName) {
        TakesScreenshot ts = (TakesScreenshot) driver;
        File sourceImage = ts.getScreenshotAs(OutputType.FILE);
        String destImagePath = "screenshot/" + screenShotName + ".png";
        File destImage = new File(destImagePath);
        try {
            FileUtils.copyFile(sourceImage, destImage);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(destImage.getAbsolutePath());
        // 图片存放路径转换为相对路径
        destImagePath = "../" + destImagePath;
        return destImagePath;
    }

    /**
     * @Description: 替换报告中的JS引用
     */
    public static void replaceReportJS() {
        File reportFolder = new File("report");
        File [] reportList = reportFolder.listFiles();
        FileInputStream fis = null;
        FileOutputStream fos = null;
        byte [] reportCodeByte = null;
        String reportStr = null;

        if (reportList != null) {
            for (File report : reportList) {
                try {
                    fis = new FileInputStream(report);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                try {
                    if (fis != null) {
                        reportCodeByte = new byte[fis.available()];
                        while (fis.read(reportCodeByte) != -1) {
                            reportStr = new String(reportCodeByte);
                        }
                        fis.close();
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }

                if (reportStr != null) {
                    reportStr = reportStr.replace("https://ajax.googleapis.com/ajax/libs/jquery/1.11.3/jquery.min.js",
                            "http://libs.baidu.com/jquery/1.11.1/jquery.min.js");
//                    System.out.println(reportStr);
                }

                try {
                    fos = new FileOutputStream(report);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

                try {
                    if (fos != null && reportStr != null) {
                        fos.write(reportStr.getBytes(), 0, reportStr.getBytes().length);
                        fos.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * @Description: 清理代码--测试执行完成后进行代码删除
     */
    public static void cleanCodeFile() {
        String testPackagePath = "./src/main/java/com/highpin/test";
        String testNGFilePath = "./testng.xml";
        File testPackage = new File(testPackagePath);
        File testNGFile = new File(testNGFilePath);
        File [] codeFileList = testPackage.listFiles();
        int codeFileNum = 0;
        int deleteNum = 0;

        // 删除字节码&代码文件
        if (codeFileList != null) {
            codeFileNum = codeFileList.length;
            for (File codeFile: codeFileList) {
                if (codeFile.delete()) {
                    deleteNum++;
                }
            }
        }
        boolean codeDeleteFlag = (deleteNum == codeFileNum);
        // 删除testng.xml
        boolean deleteFlag = testNGFile.delete();
        if (codeDeleteFlag || deleteFlag) {
            System.out.println("清理完毕!");
        }
    }

    /**
     * @Description: 将Java数据结构转为JSON...方便调试...
     * @param obj -- 传入的Java数据结构
     * @return json -- 返回的JSON
     */
    public static String dataStructConvertJSON(Object obj) {
        Gson gson = new Gson();
        String json = gson.toJson(obj);
        return json;
    }
}

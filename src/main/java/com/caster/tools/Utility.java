package com.caster.tools;

import com.google.gson.Gson;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Created by Peng.Zhao on 2015/12/3.
 */
public class Utility {
    public static Logger logger = LogManager.getLogger(Utility.class.getName());

    /**
     * @param driver         -- 浏览器对象
     * @param screenShotName -- 截图的文件名
     * @return destImagePath -- 截图的存放路径
     * @Description: 屏幕截图方法
     */
    public static String captureScreenShot(WebDriver driver, String reportDir, String screenShotName) {
        TakesScreenshot ts = (TakesScreenshot) driver;
        File sourceImage = ts.getScreenshotAs(OutputType.FILE);
        String destImagePath = "reports/" + reportDir + "/" + screenShotName + ".png";
        File destImage = new File(destImagePath);
        try {
            FileUtils.copyFile(sourceImage, destImage);
        } catch (IOException e) {
            e.printStackTrace();
        }
        destImagePath = screenShotName + ".png";
        return destImagePath;
    }

    /**
     * @Description: 替换报告中的JS引用
     */
    public static void replaceReportJS() {
        File reportFolder = new File("reports");
        File[] reportPackList = reportFolder.listFiles();
        String reportStr = null;

        if (reportPackList != null) {
            for (File reportPack : reportPackList) {
                File[] reportList = reportPack.listFiles();
                if (reportList != null) {
                    for (File singleReport : reportList) {
                        if (singleReport.getName().endsWith(".html")) {
                            reportStr = Utility.fileInput(singleReport);
                            reportStr = reportStr.replace("https://ajax.googleapis.com/ajax/libs/jquery/1.11.3/jquery.min.js",
                                    "http://libs.baidu.com/jquery/1.11.3/jquery.min.js");
                            Utility.fileOutput(singleReport, reportStr);
                        }
                    }
                }
            }
        }
        logger.info("报告JS替换完毕");
    }

    /**
     * @Description: 创建存放TestSuiteXML的文件夹
     */
    public static File createXMLFolder() {
        // 创建存放TestSuiteXML的文件夹(当前项目文件下的xml文件夹)
        File xmlFolder = new File("xml");
        if (!xmlFolder.exists())
            xmlFolder.mkdirs();
        logger.info("**************************TestSuiteXml文件夹创建完毕**************************");
        return xmlFolder;
    }

    /**
     * @Description: 清理代码--测试执行完成后进行代码删除
     */
    public static void cleanCodeFile() {
        String testPackagePath = "src/main/java/com/caster/test";
        String testOutputPath = "test-output";
        File testPackage = new File(testPackagePath);
        File testOutput = new File(testOutputPath);

        Utility.deleteFiles(testPackage);
        Utility.deleteFiles(testOutput);
        Utility.deleteTestNGXML();
        logger.info("**************************代码清理完毕**************************");
    }

    /**
     * @Description: 清空文件夹中的文件,并保留根文件夹
     * @param file  --  文件根路径
     */
    public static void deleteFiles(File file) {
        File [] fileList = file.listFiles();
        if (fileList != null) {
            for (File delFile : fileList) {
                if (delFile.isDirectory()) {
                    deleteFiles(delFile);
                }
                if (delFile.delete()) {
                    logger.info("已删除文件: " + delFile.getName());
                }
            }
        } else {
            logger.info("待清空目标文件夹不存在!");
        }
    }

    // 删除testng.xml文件
    public static void deleteTestNGXML() {
        String testNGFilePrefix = "testng_test";
//        File root = new File(".");
        File root = new File("xml");
        boolean flag = false;
        for (String name : Objects.requireNonNull(root.list())) {
            if (name.startsWith(testNGFilePrefix)) {
                flag = new File("xml/" + name).delete();
                if (flag) {
                    logger.info("删除TestNG.xml文件: " + name);
                } else {
                    logger.info("删除TestNG.xml文件失败: " + name);
                }
            }
        }
    }

    /**
     * @return testNGxmlList   --  返回文件列表
     * @Description: 在项目目录中查找TestNG.xml
     */
    public static List<String> searchTestNGXML() {
        List<String> testNGxmlList = new ArrayList<>();
        String testNGFilePrefix = "testng_test";
//        File root = new File(".");
        File root = new File("xml");
        for (String name : Objects.requireNonNull(root.list())) {
            if (name.startsWith(testNGFilePrefix)) {
                testNGxmlList.add(StringUtils.join("xml/", name));
            }
        }
        return testNGxmlList;
    }

    /**
     * @param obj -- 传入的Java数据结构
     * @return json -- 返回的JSON
     * @Description: 将Java数据结构转为JSON...方便调试...
     */
    public static String dataStructConvertJSON(Object obj) {
        Gson gson = new Gson();
        String json = gson.toJson(obj);
        return json;
    }

    /**
     * @Description: 文件输入流  --  注意!报告替换使用字节流(持续集成环境)
     * @Description: 在个人开发环境中,代码/报告读入写出都可用此方法,因为代码生成是在开发环境中,所以文件生成时的编码格式就是UTF-8
     * @param file  --  文件对象
     * @return  -- 文件正文
     */
    public static String fileInput(File file) {
        FileInputStream fis = null;
        byte [] fileCodeByte = null;
        String strContent = null;

        try {
            fis = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        try {
            if (fis != null) {
                fileCodeByte = new byte[fis.available()];
                while (fis.read(fileCodeByte) != -1) {
                    strContent = new String(fileCodeByte);
                }
                fis.close();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return strContent;
    }

    /**
     * @Description: 文件输出流  --  注意!报告替换使用字节流(持续集成环境)
     * @Description: 在个人开发环境中,代码/报告读入写出都可用此方法,因为代码生成是在开发环境中,所以文件生成时的编码格式就是UTF-8
     * @param file          --          操作的文件
     * @param strContent    --          文件正文
     */
    public static void fileOutput(File file, String strContent) {
        FileOutputStream fos = null;

        try {
            fos = new FileOutputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        try {
            if (fos != null && strContent != null) {
                fos.write(strContent.getBytes(), 0, strContent.getBytes().length);
                fos.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

//    public static void main(String[] args) {
//        Utility.cleanCodeFile();
//    }
}

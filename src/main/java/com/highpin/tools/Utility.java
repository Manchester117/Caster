package com.highpin.tools;

import com.google.gson.Gson;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2015/12/3.
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
     * @Description: 清理代码--测试执行完成后进行代码删除
     */
    public static void cleanCodeFile() {
        String testPackagePath = "src/main/java/com/highpin/test";
        String testOutputPath = "test-output";
        File testPackage = new File(testPackagePath);
        File testOutput = new File(testOutputPath);

        Utility.deleteFiles(testPackage);
        Utility.deleteFiles(testOutput);
        Utility.deleteTestNGXML();
        logger.info("代码清理完毕");
    }

    // 清理方法
    public static boolean deleteFiles(File file) {
        if (file.isDirectory()) {
            String[] childrenArray = file.list();
            for (String children : childrenArray) {
                boolean flag = deleteFiles(new File(file, children));
                if (!flag) {
                    return false;
                }
            }
        }
        return file.delete();
    }

    // 删除testng.xml文件
    public static void deleteTestNGXML() {
        String testNGFilePrefix = "testng_test";
        File root = new File(".");
        boolean flag = false;
        for (String name : root.list()) {
            if (name.startsWith(testNGFilePrefix)) {
                flag = new File("./" + name).delete();
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
        File root = new File(".");
        for (String name : root.list()) {
            if (name.startsWith(testNGFilePrefix)) {
                testNGxmlList.add(name);
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
     * @Description: 文件输入流  --  指定字符编码
     * @param file  --  文件对象
     * @return  -- 文件正文
     */
    public static String fileInput(File file) {
        String strCode = null;
        String strContent = "";
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        InputStreamReader isr = null;
        if (fis != null) {
            try {
                isr = new InputStreamReader(fis, "GBK");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        BufferedReader bufReader = null;
        if (isr != null) {
            bufReader = new BufferedReader(isr);
        }
        try {
            if (bufReader != null) {
                while ((strCode = bufReader.readLine()) != null) {
                    strContent += strCode;
                }
                bufReader.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return strContent;
    }

    /**
     * @Description: 文件输出流  --  指定字符编码
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
        OutputStreamWriter osw = null;
        try {
            if (fos != null) {
                osw = new OutputStreamWriter(fos, "UTF-8");
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        BufferedWriter bufWriter = null;
        if (osw != null) {
            bufWriter = new BufferedWriter(osw);
        }
        try {
            if (bufWriter != null) {
                bufWriter.write(strContent);
                bufWriter.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

//    /**
//     * @param file  --   文件对象
//     * @return strContent   --  文件正文
//     * @Description: 文件输入流
//     */
//    public static String fileInputOriginal(File file) {
//        FileInputStream fis = null;
//        byte[] fileCodeByte = null;
//        String strContent = null;
//
//        try {
//            fis = new FileInputStream(file);
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        }
//        try {
//            if (fis != null) {
//                fileCodeByte = new byte[fis.available()];
//                while (fis.read(fileCodeByte) != -1) {
//                    strContent = new String(fileCodeByte);
//                }
//                fis.close();
//            }
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return strContent;
//    }
//
//    /**
//     * @param file       --          操作的文件
//     * @param strContent --          文件正文
//     * @Description: 文件输出流
//     */
//    public static void fileOutputOriginal(File file, String strContent) {
//        FileOutputStream fos = null;
//
//        try {
//            fos = new FileOutputStream(file);
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        }
//        try {
//            if (fos != null && strContent != null) {
//                fos.write(strContent.getBytes(), 0, strContent.getBytes().length);
//                fos.close();
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

//    public static void main(String[] args) {
//        Utility.cleanCodeFile();
//    }
}

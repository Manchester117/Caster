package com.highpin.generator.core;

import com.highpin.tools.Utility;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Administrator on 2015/12/4.
 */
public class ConvertCodeFile {
    public static Logger logger = LogManager.getLogger(ConvertCodeFile.class.getName());

    /**
     * @Description: 对Java代码中unicode编码的中文进行转码
     */
    public void convertUnicodeFile() {
        File file = new File("./src/main/java/com/highpin/test");
        File [] packageList = file.listFiles();
        if (packageList != null) {
            for (File singlePackage : packageList) {
                File [] codeList = singlePackage.listFiles();
                if (codeList != null) {
                    for (File codeFile : codeList) {
                        // 过滤Java文件
                        if (codeFile.getName().endsWith(".java")) {
                            this.convertUnicodeSingleCodeFile(codeFile);
                        }
                    }
                }
            }
        }
        logger.info("Java文件转码完成");
    }

    /**
     * @Description: 代码文件读取写入
     * @param codeFile -- 代码文件对象
     */
    public void convertUnicodeSingleCodeFile(File codeFile) {
        FileInputStream fis = null;
        FileOutputStream fos = null;
        String codeStr = null;
        byte [] buff = null;

        if (codeFile != null && codeFile.isFile()) {
            codeStr = Utility.fileInput(codeFile);
            // 进行字符转换
            if (codeStr != null) {
                codeStr = this.unicodeToString(codeStr);
            }
            // 将代码写回到Java文件当中
            Utility.fileOutput(codeFile, codeStr);
        }
    }

    /**
     * @Description: 转码方法
     * @param code -- 代码字符串
     * @return code -- 返回转码后的代码
     */
    private String unicodeToString(String code) {
        Pattern pattern = Pattern.compile("(\\\\u(\\p{XDigit}{4}))");
        Matcher matcher = pattern.matcher(code);
        char ch;
        while (matcher.find()) {
            ch = (char)Integer.parseInt(matcher.group(2), 16);
            code = code.replace(matcher.group(1), ch + "");
        }
        return code;
    }


    // 清空Java文件
    private void clearCodeFileContent(File codeFile) {
        FileWriter clear = null;
        try {
            clear = new FileWriter(codeFile);
            // 清空文件
            clear.write("");
            clear.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        ConvertCodeFile rcf = new ConvertCodeFile();
        rcf.convertUnicodeFile();
    }
}

package com.highpin.generator.core;

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
        File [] codeFileList = file.listFiles();
        if (codeFileList != null) {
            for (File codeFile: codeFileList) {
                // 过滤Java文件
                if (codeFile.getName().endsWith(".java")) {
                    this.convertUnicodeSingleCodeFile(codeFile);
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
        String code = null;
        byte [] buff = null;

        if (codeFile != null && codeFile.isFile()) {
            try {
                fis = new FileInputStream(codeFile);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            try {
                if (fis != null) {
                    buff = new byte[fis.available()];
                    while (fis.read(buff) != -1) {
                        code = new String(buff);
                    }
                    fis.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            // 进行字符转换
            if (code != null) {
                code = this.unicodeToString(code);
                System.out.println(code);
            }
            // 将代码写回到Java文件当中
            try {
                fos = new FileOutputStream(codeFile);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            try {
                if (fos != null && code != null) {
                    fos.write(code.getBytes(), 0, code.getBytes().length);
                    fos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
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

package com.highpin.generator.core;

import com.strobel.decompiler.Decompiler;
import com.strobel.decompiler.PlainTextOutput;
import javassist.CannotCompileException;
import javassist.CtClass;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2015/12/2.
 */
public class ClassDecompiler {
    private ClassGenerator cg = null;
    private List<List<CtClass>> ctList = null;
    private List<String> classPackageFullNameList = null;
    public static Logger logger = LogManager.getLogger(ClassDecompiler.class.getName());

    /**
     * @Description: 创建测试类,并且以列表的形式获取所有测试类
     * @throws Exception
     */
    public ClassDecompiler() throws Exception {
        this.cg = new ClassGenerator();
        this.cg.createClass();
        this.cg.insertField();
        this.cg.suiteInsertMethod();
        this.ctList = this.cg.getAllClassList();
        this.classPackageFullNameList = new ArrayList<>();
    }

    /**
     * @Description: 将class文件写入到当前文件路径的test包中
     */
    public void writeClassToPackage() {
        for (List<CtClass> suiteClassList : this.ctList) {
            for (CtClass ct : suiteClassList) {
                try {
                    ct.writeFile("./src/main/java");
                } catch (IOException | CannotCompileException e) {
                    logger.error("写入字节码文件失败");
                    e.printStackTrace();
                }
            }
        }
        logger.info("写入字节码文件完成");
    }

    /**
     * @Description: 直接获取类的字节码字符串
     */
    public void getClassByteCode() {
        byte [] classByteCode = null;
        for (List<CtClass> suiteClassList : this.ctList) {
            for (CtClass aCtList : suiteClassList) {
                try {
                    classByteCode = aCtList.toBytecode();
                    String classStatement = new String(classByteCode, "UTF-8");
                    System.out.println(classStatement);
                } catch (IOException | CannotCompileException e) {
                    logger.error("获取字节码字符串失败");
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * @Description: 获取字节码文件的类名称
     * @return classPackageFullNameList -- 类名称
     */
    public List<String> getTestClassFileName() {
        String testClassPath = "./src/main/java/com/highpin/test";
        File pack = new File(testClassPath);
        File [] fileList = pack.listFiles();
        String fileClassName = null;
        if (fileList != null) {
            for (File codePackage : fileList) {
                for (String codeFile : codePackage.list()) {
                    // 去掉.class后缀
                    codeFile = codeFile.substring(0, codeFile.lastIndexOf("."));
                    classPackageFullNameList.add("com/highpin/test/" + codeFile);
                }
            }
        }
        logger.info("返回所有的类全名");
        logger.info(classPackageFullNameList);
        return classPackageFullNameList;
    }

    /**
     * @Description: 将字节码文件反编译为Java文件
     */
    public void decompilerClass() {
        String prefixPath = "./src/main/java/";
        String javaFullPath = null;
        String classFullPath = null;
        FileOutputStream stream = null;
        OutputStreamWriter writer = null;

        logger.info("************************************************类反编译开始************************************************");
        for (String classPackageFullName : this.classPackageFullNameList) {
            javaFullPath = prefixPath + classPackageFullName + ".java";
            classFullPath = prefixPath + classPackageFullName + ".class";
            logger.info(javaFullPath);
            logger.info(classFullPath);
            try {
                stream = new FileOutputStream(javaFullPath);
            } catch (FileNotFoundException e) {
                logger.error("字节码文件不存在: " + javaFullPath);
                e.printStackTrace();
            }
            try {
                if (stream != null) {
                    writer = new OutputStreamWriter(stream, "UTF-8");
                }
            } catch (UnsupportedEncodingException e) {
                logger.error("不支持的编码格式");
                e.printStackTrace();
            }
            if (writer != null) {
                // 进行反编译
                Decompiler.decompile(classFullPath, new PlainTextOutput(writer));
            }
            try {
                if (writer != null) {
                    writer.flush();
                    writer.close();
                }
                if (stream != null) {
                    stream.flush();
                    stream.close();
                }
            } catch (IOException e) {
                logger.error("文件流不能正常关闭");
                e.printStackTrace();
            }
        }
        logger.info("************************************************类反编译完成************************************************");
    }

    // 测试--main方法
    public static void main(String[] args) throws Exception {
        ClassDecompiler cd = new ClassDecompiler();
        cd.writeClassToPackage();
        cd.getTestClassFileName();
        cd.decompilerClass();
    }
}

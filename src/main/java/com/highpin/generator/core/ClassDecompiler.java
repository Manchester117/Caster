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
 * Created by Peng.Zhao on 2015/12/2.
 */
public class ClassDecompiler {
    private ClassGenerator cg = null;
    private List<List<CtClass>> ctList = null;
    private List<String> suitePackageList = null;
    private List<List<String>> classNameList = null;
    public static Logger logger = LogManager.getLogger(ClassDecompiler.class.getName());

    /**
     * @Description: 创建测试类,并且以列表的形式获取所有测试类
     * @throws Exception
     */
    public ClassDecompiler() throws Exception {
        this.cg = new ClassGenerator();
        this.cg.createTestClass();
        this.cg.insertField();
        this.cg.suiteInsertMethod();
        this.ctList = this.cg.getAllClassList();
        this.suitePackageList = new ArrayList<>();
        this.classNameList = new ArrayList<>();
    }

    /**
     * @Description: 将class文件写入到target的test包中
     */
    public void writeClassToPackage() {
        for (List<CtClass> suiteClassList : this.ctList) {
            for (CtClass ct : suiteClassList) {
                try {
                    ct.writeFile("target/classes");
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
    public void getTestClassFileName() {
        String testClassPath = "target/classes/com/highpin/test";
        File pack = new File(testClassPath);
        File [] fileList = pack.listFiles();
        String subPackageName = null;
        List<String> classList = null;

        if (fileList != null) {
            for (File codePackage : fileList) {
                subPackageName = codePackage.getName();
                // 获取全部子包名
                this.suitePackageList.add(subPackageName);
                classList = new ArrayList<>();
                for (String codeFile : codePackage.list()) {
                    // 去掉.class后缀
                    codeFile = codeFile.substring(0, codeFile.lastIndexOf("."));
                    classList.add(codeFile);
                }
                this.classNameList.add(classList);
            }
        }
        logger.info("获取所有的类全名: " + this.classNameList);
    }

    /**
     * @Description: 将字节码文件反编译为Java文件
     */
    public void decompilerClass() {
        String javaPrefixPath = "src/main/java/com/highpin/test/";
        String classPrefixPath = "target/classes/com/highpin/test/";
        String javaFullPath = null;
        String classFullPath = null;
        FileOutputStream stream = null;
        OutputStreamWriter writer = null;

        logger.info("**************************类反编译开始**************************");
        for (int suiteIndex = 0; suiteIndex < this.suitePackageList.size(); ++suiteIndex) {
            String packageName = this.suitePackageList.get(suiteIndex);
            for (String className : this.classNameList.get(suiteIndex)) {
                javaFullPath = javaPrefixPath + packageName + "/" + className + ".java";
                classFullPath = classPrefixPath + packageName + "/" + className + ".class";
                logger.info(javaFullPath);
                logger.info(classFullPath);
                File codePackage = new File(javaFullPath.substring(0, javaFullPath.lastIndexOf("/")));
                if (codePackage.mkdirs()) {
                    logger.info("Test Package创建成功");
                }
                try {
                    stream = new FileOutputStream(javaFullPath);
                } catch (FileNotFoundException e) {
                    logger.error("Java文件不存在: " + javaFullPath);
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
        }
        logger.info("**************************类反编译完成**************************");
    }

    // 测试--main方法
//    public static void main(String[] args) throws Exception {
//        ClassDecompiler cd = new ClassDecompiler();
//        cd.writeClassToPackage();
//        cd.getTestClassFileName();
//        cd.decompilerClass();
//        ConvertCodeFile rcf = new ConvertCodeFile();
//        rcf.convertUnicodeFile();
//    }
}

package com.highpin.runner;

import com.highpin.generator.core.ClassDecompiler;
import com.highpin.generator.core.ReplaceCodeFile;
import com.highpin.generator.xml.XMLFileOperator;
import com.highpin.tools.Utility;
import org.testng.TestNG;
import org.testng.xml.XmlSuite;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2015/12/3.
 */
public class UIKRunner {
    private XMLFileOperator xfo = null;
    private ClassDecompiler cd = null;
    private ReplaceCodeFile rcf = null;

    public void createTestJavaClass() throws Exception {
        this.cd = new ClassDecompiler();
        this.cd.writeClassToPackage();
        this.cd.getTestClassFileName();
        this.cd.decompilerClass();
    }

    public void createTestDriverXML() throws Exception{
        this.xfo = new XMLFileOperator();
        this.xfo.createXML("testng.xml");
    }

    public void replaceCode() {
        rcf = new ReplaceCodeFile();
        rcf.convertUnicodeFile();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void copyTargetCode() {
        Utility.copyByteCode();
    }

    public void testRunner() {
        // 设定TestNG.xml的列表
        List<String> suitesStrList = new ArrayList<String>();
        suitesStrList.add("testng.xml");

        // 将TestNG.xml文件装载到XmlSuite里
        XmlSuite suite = new XmlSuite();
        suite.setSuiteFiles(suitesStrList);

        // 创建一个Suite的List
        List<XmlSuite> suitesXmlList = new ArrayList<>();
        suitesXmlList.add(suite);

        // 执行用例
        TestNG testng = new TestNG();
        testng.setXmlSuites(suitesXmlList);
        System.out.println("************************************************测试开始执行************************************************");
        testng.run();
        System.out.println("************************************************测试执行结束************************************************");
    }

    public void replaceReport() {
        Utility.replaceReportJS();
    }

    public void cleanCodeFile() {
        Utility.cleanCodeFile();
    }

    // 测试--main方法
    public static void main(String[] args) throws Exception {
        UIKRunner uik = new UIKRunner();
        uik.createTestJavaClass();
        uik.createTestDriverXML();
        uik.replaceCode();
        uik.copyTargetCode();
        uik.testRunner();
        uik.replaceReport();
//        uik.cleanCodeFile();
    }
}

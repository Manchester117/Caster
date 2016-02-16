package com.highpin.runner;

import com.highpin.generator.core.ClassDecompiler;
import com.highpin.generator.core.ConvertCodeFile;
import com.highpin.generator.xml.XMLFileOperator;
import com.highpin.tools.Utility;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.TestNG;
import org.testng.xml.XmlSuite;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Peng.Zhao on 2015/12/3.
 */
public class UIKRunner {
    private XMLFileOperator xfo = null;
    private ClassDecompiler cd = null;
    private ConvertCodeFile rcf = null;
    public static Logger logger = LogManager.getLogger(UIKRunner.class.getName());

    public void createTestJavaClass() throws Exception {
        this.cd = new ClassDecompiler();
        this.cd.writeClassToPackage();
        this.cd.getTestClassFileName();
        this.cd.decompilerClass();
    }

    public void createTestDriverXML() throws Exception{
        this.xfo = new XMLFileOperator();
        this.xfo.createMultiXML();
    }

    public void ConvertCode() {
        rcf = new ConvertCodeFile();
        rcf.convertUnicodeFile();
    }

    public void testRunner() {
        // 设定TestNG.xml的列表
        List<String> suitesStrList = Utility.searchTestNGXML();

        // 将TestNG.xml文件装载到XmlSuite里
        XmlSuite suite = new XmlSuite();
        suite.setSuiteFiles(suitesStrList);

        // 创建一个Suite的List
        List<XmlSuite> suitesXmlList = new ArrayList<>();
        suitesXmlList.add(suite);

        // 执行用例
        TestNG testng = new TestNG();
        testng.setXmlSuites(suitesXmlList);
        logger.info("**************************测试开始执行**************************");
        testng.run();
        logger.info("**************************测试执行结束**************************");
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
        uik.ConvertCode();
        uik.testRunner();
        uik.replaceReport();
        uik.cleanCodeFile();
    }
}

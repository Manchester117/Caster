package com.caster.generator.xml;

import com.caster.operatordata.ReadAllTestSuiteFile;
import com.caster.operatordata.ReadStruct;
import com.caster.tools.Utility;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

/**
 * Created by Peng.Zhao on 2015/11/22.
 */
public class XMLFileOperator {
    private Document document;
    private List<String> allSuiteList = null;
    private List<List<String>> allClassNameList = null;
    private List<List<List<Object>>> allMethodNameList = null;
    public static Logger logger = LogManager.getLogger(XMLFileOperator.class.getName());

    /**
     * @Description: 构造方法 -- 从Excel中读取数据,生成数据结构,并获取所有的类名和关键字(方法名)
     * @throws Exception -- 如果遇到无法识别的字段则抛出NotFoundExcelColException
     */
    public XMLFileOperator() throws Exception {
        ReadAllTestSuiteFile rf = new ReadAllTestSuiteFile();
        ReadStruct rs = new ReadStruct(rf.readTestSuite());

        this.allSuiteList = rs.getTestSuiteName();
        this.allClassNameList = rs.getAllClassName();
        this.allMethodNameList = rs.getSheetField("Action_Keyword");
        logger.info("读取Excel获取所有关键字");
        logger.info(this.allSuiteList);
        logger.info(this.allClassNameList);
        logger.info(this.allMethodNameList);
    }

    public void createMultiXML() {
        // 创建testSuiteXML文件
        String xmlFolderPath = null;
        try {
            xmlFolderPath = Utility.createXMLFolder().getCanonicalPath();
            String testngFileName = null;
            for (int i = 0; i < this.allSuiteList.size(); ++i) {
                testngFileName = StringUtils.join(xmlFolderPath, File.separator, "testng_", this.allSuiteList.get(i), ".xml");
                this.createSingleXML(testngFileName, this.allSuiteList.get(i), this.allClassNameList.get(i), this.allMethodNameList.get(i));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * @Description: 根据数据结构创建XML对象,并调用writerXML2File方法将XML对象写入到文件中
     * @param filePath -- XML文件路径
     */
    public void createSingleXML(String filePath, String suiteName, List<String> classNameList, List<List<Object>> methodNameList) {
        this.document = DocumentHelper.createDocument();
        // sheet页索引
        int sheetIndex = 0;
        // 创建根节点(suite)
        Element rootSuite = this.document.addElement("suite");
        rootSuite.addAttribute("name", "Caster UI Test");
        rootSuite.addAttribute("preserve-order", "true");
        // 此处可以再加入参数
        // 创建子节点(test)
        for (String className: classNameList) {
            Element leafTest = rootSuite.addElement("test");
            leafTest.addAttribute("name", className);
            leafTest.addAttribute("verbose", "10");
            leafTest.addAttribute("preserve-order", "true");
            // 创建所有测试类
            Element testClasses = leafTest.addElement("classes");
            Element testClass = testClasses.addElement("class");
            testClass.addAttribute("name", "com.caster.test." + suiteName + "." + className);
            // 向测试类中添加方法节点
            Element testMethod = testClasses.addElement("methods");
            for (Object methodName : methodNameList.get(sheetIndex)) {
                // 添加方法
                Element method = testMethod.addElement("include");
                method.addAttribute("name", methodName.toString());
            }
            sheetIndex++;
        }
        logger.info("创建testng的XML对象");
        // 写出XML到文件
        this.writerXML2File(filePath);
    }

    /**
     * @Description: 将XML对象写入到文件
     * @param filePath -- 文件路径
     */
    private void writerXML2File(String filePath) {
        XMLWriter output = null;
        OutputFormat format = OutputFormat.createPrettyPrint();
        try {
            output = new XMLWriter(new FileWriter(filePath), format);
            output.write(this.document);
            output.close();
            logger.info("将XML对象写入到文件");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

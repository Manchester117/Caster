package com.highpin.generator.xml;

import com.highpin.operatordata.ExcelOperator;
import com.highpin.operatordata.ReadStruct;
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
 * Created by Administrator on 2015/11/22.
 */
public class XMLFileOperator {
    private Document document;
    private List<String> classNameList = null;
    private List<List<String>> methodNameList = null;
    public static Logger logger = LogManager.getLogger(XMLFileOperator.class.getName());

    /**
     * @Description: 构造方法 -- 从Excel中读取数据,生成数据结构,并获取所有的类名和关键字(方法名)
     * @throws Exception -- 如果遇到无法识别的字段则抛出NotFoundExcelColException
     */
    public XMLFileOperator() throws Exception {
        ExcelOperator eo = new ExcelOperator("case/DataEngine.xlsx");
        ReadStruct rs = new ReadStruct(eo.traverseTestSteps());
        this.classNameList = rs.getAllClassName();
        this.methodNameList = rs.getSheetField("Action_Keyword");
        logger.info("读取Excel获取所有关键字");
    }

    /**
     * @Description: 根据数据结构创建XML对象,并调用writerXML2File方法将XML对象写入到文件中
     * @param filePath -- XML文件路径
     */
    public void createXML(String filePath) {
        this.document = DocumentHelper.createDocument();
        // sheet页索引
        int sheetIndex = 0;
        // 创建根节点(suite)
        Element rootSuite = this.document.addElement("suite");
        rootSuite.addAttribute("name", "HighPin UI Test");
        rootSuite.addAttribute("preserve-order", "true");
        // 此处可以再加入参数
        // 创建子节点(test)
        for (String className: this.classNameList) {
            Element leafTest = rootSuite.addElement("test");
            leafTest.addAttribute("name", className);
            leafTest.addAttribute("verbose", "10");
            leafTest.addAttribute("preserve-order", "true");
            // 创建所有测试类
            Element testClasses = leafTest.addElement("classes");
            Element testClass = testClasses.addElement("class");
            testClass.addAttribute("name", "com.highpin.test." + className);
            // 向测试类中添加方法节点
            Element testMethod = testClasses.addElement("methods");
            for (String methodName : this.methodNameList.get(sheetIndex)) {
                // 添加方法
                Element method = testMethod.addElement("include");
                method.addAttribute("name", methodName);
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

    public static void main(String[] args) throws Exception {
        XMLFileOperator xfo = new XMLFileOperator();
        xfo.createXML("testng.xml");
    }
}

package com.caster.operatordata;

import com.caster.tools.Utility;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.util.*;

/**
 * Created by Peng.Zhao on 2015/11/22.
 */
public class ReadStruct {
    private SortedMap<String, SortedMap<String, SortedMap<String, Map<String, Object>>>> suiteMap = null;
    public static Logger logger = LogManager.getLogger(ReadStruct.class.getName());

    /**
     * @Description: 构造方法 -- 从测试数据结构中获取数据
     * @param suiteMap -- 测试数据结构
     */
    public ReadStruct(SortedMap<String, SortedMap<String, SortedMap<String, Map<String, Object>>>> suiteMap) {
        this.suiteMap = suiteMap;
    }

    /**
     * @Description: 从数据结构中获取所有的Excel文件名称
     * @return suiteNameList -- 返回以List形式存放的文件名称
     */
    public List<String> getTestSuiteName() {
        List<String> suiteNameList = new ArrayList<>();
        for (Object step : this.suiteMap.entrySet()) {
            Map.Entry entry = (Map.Entry) step;
            suiteNameList.add(entry.getKey().toString());
        }
        logger.info("文件列表: " + Utility.dataStructConvertJSON(suiteNameList));
        return suiteNameList;
    }

    /**
     * @Description: 从数据结构中获取所有的类名称
     * @return classNameList -- 返回以List形式存放的类名称
     */
    public List<List<String>> getAllClassName() {
        List<String> classNameList = null;
        List<List<String>> allClassNameList = new ArrayList<>();

        SortedMap classMap = null;

        for (Object testSuite : this.suiteMap.entrySet()) {
            classNameList = new ArrayList<>();
            Map.Entry entrySuite = (Map.Entry) testSuite;
            classMap = (SortedMap)entrySuite.getValue();
            for (Object subClass : classMap.entrySet()) {
                Map.Entry entryClass = (Map.Entry)subClass;
                classNameList.add(entryClass.getKey().toString());
            }
            allClassNameList.add(classNameList);
        }
        logger.info("类名列表: " + Utility.dataStructConvertJSON(allClassNameList));
        return allClassNameList;
    }

    /**
     * @Description: 从数据结构中获取字段值
     * @param sheetField -- 字段名称
     * @return allFieldList -- 返回的字段值
     */
    public List<List<List<Object>>> getSheetField(String sheetField) {
        SortedMap testClasses = null;
        SortedMap testSteps = null;
        HashMap testItem = null;
        List<Object> fieldList = null;
        List<List<Object>> testFieldList = null;
        List<List<List<Object>>> suiteFieldList = new ArrayList<>();

        try {
            for (Object testSuite : this.suiteMap.entrySet()) {
                Map.Entry entryTestSuite = (Map.Entry) testSuite;
                testClasses = (SortedMap) entryTestSuite.getValue();
                testFieldList = new ArrayList<>();
                for (Object testClass : testClasses.entrySet()) {
                    Map.Entry entryTestClass = (Map.Entry) testClass;
                    testSteps = (SortedMap) entryTestClass.getValue();
                    fieldList = new ArrayList<>();
                    for (Object item : testSteps.entrySet()) {
                        Map.Entry entryItem = (Map.Entry) item;
                        testItem = (HashMap) entryItem.getValue();
                        fieldList.add(testItem.get(sheetField));
                    }
                    testFieldList.add(fieldList);
                }
                suiteFieldList.add(testFieldList);
            }
        } catch (NullPointerException e) {
            logger.error("字段传递错误");
            e.printStackTrace();
        }
        logger.info("字段列表: " + Utility.dataStructConvertJSON(suiteFieldList));
        return suiteFieldList;
    }

//    public static void main(String[] args) throws Exception {
//        ReadAllTestSuiteFile rf = new ReadAllTestSuiteFile();
//        ReadStruct rs = new ReadStruct(rf.readTestSuite());
//        rs.getAllClassName();
//        rs.getSheetField("Action_Keyword");
//        rs.getTestSuiteName();
//    }
}

package com.highpin.operatordata;

import com.highpin.tools.Utility;

import java.io.File;
import java.util.*;

/**
 * Created by Peng.Zhao on 2015/12/21.
 */
public class ReadAllTestSuiteFile {
    private File testFolder = null;
    private SortedMap<String, SortedMap<String, SortedMap<String, Map<String, Object>>>> allTestDataMap = null;

    /**
     * @Description: 获取用例目录并且建立数据结构
     */
    public ReadAllTestSuiteFile() {
        this.testFolder = new File("cases");
        this.allTestDataMap = new TreeMap<>();
    }

    /**
     * @Description: 将用例中的数据读取出来,放置在数据结构当中
     * @return  allTestDataMap  --  测试数据结构
     * @throws Exception
     */
    public SortedMap<String, SortedMap<String, SortedMap<String, Map<String, Object>>>> readTestSuite() throws Exception {
        String testSuiteName = null;
        File [] testSuiteList = this.testFolder.listFiles();
        SortedMap<String, SortedMap<String, Map<String, Object>>> testMap = null;

        if (testSuiteList != null && testSuiteList.length > 0) {
            for (File file : testSuiteList) {
                testSuiteName = file.getName();
                // 只读取前缀为test_和后缀为.xlsx的文件
                if (testSuiteName.startsWith("test_") && testSuiteName.endsWith(".xlsx")) {
                    // 实例化Excel数据读取
                    ExcelOperator eo = new ExcelOperator("cases/" + testSuiteName);
                    // 获取单个Excel数据结构
                    testMap = eo.traverseTestSteps();
                    // 获取文件名(不带后缀)
                    testSuiteName = testSuiteName.substring(0, testSuiteName.indexOf("."));
                    // 将每个Excel生成的数据结构放置在SortedMap当中
                    this.allTestDataMap.put(testSuiteName, testMap);
                }
            }
            System.out.println(Utility.dataStructConvertJSON(this.allTestDataMap));
        }
        return this.allTestDataMap;
    }

//    public static void main(String[] args) throws Exception {
//        ReadAllTestSuiteFile rf = new ReadAllTestSuiteFile();
//        rf.readTestSuite();
//    }
}

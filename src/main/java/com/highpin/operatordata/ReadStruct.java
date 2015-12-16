package com.highpin.operatordata;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.util.*;

/**
 * Created by Administrator on 2015/11/22.
 */
public class ReadStruct {
    private SortedMap caseMap = null;
    public static Logger logger = LogManager.getLogger(ReadStruct.class.getName());

    /**
     * @Description: 构造方法 -- 从测试数据结构中获取数据
     * @param caseMap -- 测试数据结构
     */
    public ReadStruct(SortedMap caseMap) {
        this.caseMap = caseMap;
    }

    /**
     * @Description: 从数据结构中获取素有的类名称
     * @return classNameList -- 返回以List形式存放的类名称
     */
    public List<String> getAllClassName() {
        List<String> classNameList = new ArrayList<>();

        for (Object step : this.caseMap.entrySet()) {
            Map.Entry entry = (Map.Entry) step;
            classNameList.add(entry.getKey().toString());
        }
//        System.out.println("类名列表: " + classNameList);
        logger.info("返回数据结构的类名称列表");
        return classNameList;
    }

    /**
     * @Description: 从数据结构中获取字段值
     * @param sheetField -- 字段名称
     * @return allFieldList -- 返回的字段值
     */
    public List<List<String>> getSheetField(String sheetField) {
        SortedMap testStep = null;
        HashMap testItem = null;
        List<String> fieldList = null;
        List<List<String>> allFieldList = new ArrayList<>();

        try {
            for (Object step : this.caseMap.entrySet()) {
                Map.Entry entryStep = (Map.Entry) step;
                testStep = (SortedMap) entryStep.getValue();
                fieldList = new ArrayList<>();
                for (Object item : testStep.entrySet()) {
                    Map.Entry entryItem = (Map.Entry) item;
                    testItem = (HashMap) entryItem.getValue();
                    fieldList.add(testItem.get(sheetField).toString());
                }
                allFieldList.add(fieldList);
            }
        } catch (NullPointerException e) {
            logger.error("字段传递错误");
            e.printStackTrace();
        }
//        System.out.println("字段列表: " + allFieldList);
        logger.info("返回数据结构的方法名称列表");
        return allFieldList;
    }

    public static void main(String[] args) throws Exception {
        ExcelOperator eo = new ExcelOperator("case/DataEngine.xlsx");
        ReadStruct rs = new ReadStruct(eo.traverseTestSteps());
        rs.getAllClassName();
        rs.getSheetField("Action_Keyword");
    }
}

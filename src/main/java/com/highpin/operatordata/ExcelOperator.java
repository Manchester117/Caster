package com.highpin.operatordata;

import com.highpin.except.NotFoundExcelColException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * Created by Peng.Zhao on 2015/11/16.
 */
public class ExcelOperator {
    private XSSFWorkbook excelBook = null;
    private List<XSSFSheet> readyToRunTestStepList = null;
    public static Logger logger = LogManager.getLogger(ExcelOperator.class.getName());

    /**
     * @Description: 读取Excel
     * @param excelPath -- Excel文件所在路径
     */
    public ExcelOperator(String excelPath) {
        InputStream is = null;
        try {
            is = new FileInputStream(excelPath);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        if (is != null) {
            try {
                this.excelBook = new XSSFWorkbook(is);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        logger.info("创建Excel文件对象成功");
    }

    /**
     * @Description: 获取可以运行的测试用例
     */
    public void getRunTestSteps() {
        XSSFSheet excelSheet = this.excelBook.getSheet("Test Suite");
        XSSFCell cell = null;
        // 用getPhysicalNumberOfRows方法获取实际的Sheet行数
        int rowNum = excelSheet.getPhysicalNumberOfRows();
        // 用List存放可以运行的TestSteps
        this.readyToRunTestStepList = new ArrayList<>();

        for (int r = 1; r < rowNum; ++r) {
            cell = excelSheet.getRow(r).getCell(0);
            if ("Yes".equals(excelSheet.getRow(r).getCell(2).getStringCellValue())) {
                this.readyToRunTestStepList.add(this.excelBook.getSheet(cell.getStringCellValue()));
            }
        }
        logger.info("获取测试用例成功");
    }

    /**
     * @Description: 从Excel中读取数据生成数据结构
     * @return -- 返回一个SortMap的数据结构
     * @throws Exception -- 如果Excel中出现无法识别的字段则抛出NotFoundExcelColException
     */
    public SortedMap<String, SortedMap<String, Map<String, Object>>> traverseTestSteps() throws Exception {
        int rowNum = 0;
        int colNum = 0;
        String title = null;
        String value = null;

        // 验证类型列表
        List<String> verifyTypeList = null;
        // 验证目标列表
        List<String> verifyTargetList = null;
        // 验证值列表
        List<String> verifyValueList = null;

        // 每个测试步骤中的子项
        Map<String, Object> stepItem = null;
        // 每个测试步骤
        SortedMap<String, Map<String, Object>> testStep = null;
        // 每条测试用例
        SortedMap<String, SortedMap<String, Map<String, Object>>> caseMap = new TreeMap<>();
        // 获取可以运行的测试步骤
        this.getRunTestSteps();

        for (XSSFSheet stepSheet : this.readyToRunTestStepList) {
            try {
                rowNum = stepSheet.getPhysicalNumberOfRows();
                colNum = stepSheet.getRow(0).getPhysicalNumberOfCells();
            } catch (NullPointerException e) {
                logger.error("Test_Case_Name与Sheet名不符");
                e.printStackTrace();
            }
            testStep = new TreeMap<>();
            for (int r = 1; r < rowNum; ++r) {
                try {
                    // 如果第一列的Test_Step_ID不是空的.则会初始化验证列表(主要是判断是不是合并单元格),并且读取单元格中的数据
                    if (!stepSheet.getRow(r).getCell(0).getStringCellValue().isEmpty()) {
                        verifyTypeList = new ArrayList<>();
                        verifyTargetList = new ArrayList<>();
                        verifyValueList = new ArrayList<>();
                        stepItem = new HashMap<>();
                        //此处创建基本的测试数据结构(未加入验证点)
                        this.createTestDataStruct(stepSheet, colNum, r, testStep, stepItem);
                    }
                } catch (NullPointerException e) {
                    logger.error("读取Excel数据出现错误!Excel中存在用例范围外的数据.");
                    e.getMessage();
                }
                // 向数据结构中加入验证点
                this.insertVerifyData(stepSheet, colNum, r, verifyTypeList, verifyTargetList, verifyValueList, stepItem);
            }
            caseMap.put(stepSheet.getSheetName(), testStep);
        }
        logger.info("返回测试数据结构");
        return caseMap;
    }

    /**
     * @Description: 创建基本的测试数据结构(未加验证点)
     * @param stepSheet --  当前的Sheet
     * @param colNum    --  Sheet页的列数
     * @param rowNum    --  Sheet页的当前行数
     * @param testStep  --  测试步骤map
     * @param stepItem  --  单个测试步骤的map
     * @throws Exception
     */
    private void createTestDataStruct(XSSFSheet stepSheet, int colNum, int rowNum, SortedMap<String, Map<String, Object>> testStep, Map<String, Object> stepItem) throws Exception {
        String title = null;
        String value = null;
        for (int c = 0; c < colNum; ++c) {
            title = stepSheet.getRow(0).getCell(c).getStringCellValue();
            value = stepSheet.getRow(rowNum).getCell(c).getStringCellValue();
            // 去掉收字段值得首尾空格,避免方法无法执行.
            value = value.trim();
            switch (title) {
                case "Test_Step_ID":
                    testStep.put(value, stepItem);
                    break;
                case "Description":
                    stepItem.put(title, value);
                    break;
                case "Action_Keyword":
                    stepItem.put(title, value);
                    break;
                case "Element_Type":
                    stepItem.put(title, value);
                    break;
                case "Locator_Type":
                    stepItem.put(title, value);
                    break;
                case "Locator_Value":
                    stepItem.put(title, value);
                    break;
                case "Data_Set":
                    stepItem.put(title, value);
                    break;
                case "Screen_Capture":
                    stepItem.put(title, value);
                    break;
                case "Verify_Type":
                    break;
                case "Verify_Target":
                    break;
                case "Verify_Value":
                    break;
                default:
                    logger.error("无法识别Excel中的字段: " + title);
                    throw new NotFoundExcelColException("无法识别Excel中的字段: " + title);
            }
        }
    }

    /**
     * @Description: 向数据结构中加入验证点
     * @param stepSheet --  当前的Sheet
     * @param colNum    --  Sheet页的列数
     * @param rowNum    --  Sheet页的当前行数
     * @param verifyTypeList    --  验证类型列表
     * @param verifyTargetList  --  验证目标列表
     * @param verifyValueList   --  验证值列表
     * @param stepItem  --  单个操作步骤map
     * @throws Exception
     */
    private void insertVerifyData(XSSFSheet stepSheet, int colNum, int rowNum, List<String> verifyTypeList, List<String> verifyTargetList, List<String> verifyValueList, Map<String, Object> stepItem) throws Exception {
        String title = null;
        String value = null;
        for (int c = 0; c < colNum; ++c) {
            title = stepSheet.getRow(0).getCell(c).getStringCellValue();
            value = stepSheet.getRow(rowNum).getCell(c).getStringCellValue();
            switch (title) {
                case "Verify_Type":
                    if (verifyTypeList != null) {
                        verifyTypeList.add(value);
                    } else {
                        logger.error("Test_Step_ID为空,无法将验证类型加入列表.");
                    }
                    break;
                case "Verify_Target":
                    if (verifyTargetList != null) {
                        verifyTargetList.add(value);
                    } else {
                        logger.error("Test_Step_ID为空,无法将验证目标加入列表.");
                    }
                    break;
                case "Verify_Value":
                    if (verifyValueList != null) {
                        verifyValueList.add(value);
                    } else {
                        logger.error("Test_Step_ID为空,无法将验证值加入列表.");
                    }
                    break;
                case "Test_Step_ID":
                    break;
                case "Description":
                    break;
                case "Action_Keyword":
                    break;
                case "Element_Type":
                    break;
                case "Locator_Type":
                    break;
                case "Locator_Value":
                    break;
                case "Data_Set":
                    break;
                case "Screen_Capture":
                    break;
                default:
                    logger.error("无法识别Excel中的字段: " + title);
                    throw new NotFoundExcelColException("无法识别Excel中的字段: " + title);
            }
        }
        if (stepItem != null) {
            stepItem.put("Verify_Type", verifyTypeList);
            stepItem.put("Verify_Target", verifyTargetList);
            stepItem.put("Verify_Value", verifyValueList);
        } else {
            logger.error("Test_Step_ID为空");
            throw new NotFoundExcelColException("Test_Step_ID为空");
        }
    }

//    public static void main(String[] args) throws Exception {
//        ExcelOperator eo = new ExcelOperator("case/test_dataengine_1.xlsx");
//        SortedMap<String, SortedMap<String, Map<String, Object>>> testDataMap = eo.traverseTestSteps();
//        String dataStruct = Utility.dataStructConvertJSON(testDataMap);
//        System.out.println(dataStruct);
//    }
}

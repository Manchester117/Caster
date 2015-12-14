package com.highpin.operatordata;

import com.highpin.except.NotFoundExcelColException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.util.*;

/**
 * Created by Administrator on 2015/11/16.
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
        XSSFSheet excelSheet = this.excelBook.getSheet("Test Cases");
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
    public SortedMap<String, SortedMap<String, Map<String, String>>> traverseTestSteps() throws Exception {
        int rowNum = 0;
        int colNum = 0;
        String title = null;
        String value = null;
        // 每个测试步骤中的子项
        Map<String, String> stepItem = null;
        // 每个测试步骤
        SortedMap<String, Map<String, String>> testStep = null;
        // 每条测试用例
        SortedMap<String, SortedMap<String, Map<String, String>>> caseMap = new TreeMap<>();
        // 获取可以运行的测试步骤
        this.getRunTestSteps();

        for (XSSFSheet stepSheet: this.readyToRunTestStepList) {
            rowNum = stepSheet.getPhysicalNumberOfRows();
            colNum = stepSheet.getRow(0).getPhysicalNumberOfCells();
            testStep = new TreeMap<>();
            for (int r = 1; r < rowNum; ++r) {
                stepItem = new HashMap<>();
                for (int c = 0; c < colNum; ++c) {
                    title = stepSheet.getRow(0).getCell(c).getStringCellValue();
                    value = stepSheet.getRow(r).getCell(c).getStringCellValue();
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
                        case "Verify_Type":
                            stepItem.put(title, value);
                            break;
                        case "Verify_Target":
                            stepItem.put(title, value);
                            break;
                        case "Verify_Value":
                            stepItem.put(title, value);
                            break;
                        default:
                            System.out.print("无法识别Excel中的字段: " + title);
                            throw new NotFoundExcelColException("无法识别Excel中的字段: " + title);
                    }
                }
            }
            caseMap.put(stepSheet.getSheetName(), testStep);
        }
//        for (Object step : caseMap.entrySet()) {
//            Map.Entry entry = (Map.Entry) step;
//            System.out.println(entry.getKey().toString() + ": " + entry.getValue().toString());
//        }
        logger.info("返回测试数据结构");
        return caseMap;
    }

    public static void main(String[] args) throws Exception {
        ExcelOperator eo = new ExcelOperator("case/DataEngine.xlsx");
        SortedMap<String, SortedMap<String, Map<String, String>>> testDataMap = eo.traverseTestSteps();
        System.out.println(testDataMap);
    }
}

package com.highpin.generator.core;

import com.highpin.except.NotFoundTestException;
import com.highpin.operatordata.ExcelOperator;
import com.highpin.operatordata.ReadStruct;
import javassist.*;
import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.ClassFile;
import javassist.bytecode.ConstPool;
import javassist.bytecode.annotation.Annotation;
import javassist.bytecode.annotation.BooleanMemberValue;
import javassist.bytecode.annotation.StringMemberValue;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2015/11/28.
 */
public class ClassGenerator {
    private ClassPool cPool = null;
    private MethodTemplate mt = null;
    private List<String> classNameList = null;
    private List<CtClass> ctList = null;

    private List<List<Object>> methodNameList = null;
    private List<List<Object>> methodDescriptionList = null;
    private List<List<Object>> methodElementTypeList = null;
    private List<List<Object>> methodLocatorTypeList = null;
    private List<List<Object>> methodLocatorValueList = null;
    private List<List<Object>> methodDataSetList = null;

    private List<List<Object>> methodVerifyTypeList = null;
    private List<List<Object>> methodVerifyTargetList = null;
    private List<List<Object>> methodVerifyValueList = null;

    private List<List<Object>> methodScreenCaptureList = null;

    public static Logger logger = LogManager.getLogger(ClassGenerator.class.getName());

    /**
     * @Description: 构造方法--通过对Excel的操作获取测试用例的数据结构.
     * @throws Exception -- 如果在读取Excel时出现不正确的字段则抛出NotFoundExcelColException
     */
    public ClassGenerator() throws Exception {
        this.cPool = ClassPool.getDefault();
        this.mt = new MethodTemplate();
        ExcelOperator eo = new ExcelOperator("case/DataEngine.xlsx");
        ReadStruct rs = new ReadStruct(eo.traverseTestSteps());
        this.classNameList = rs.getAllClassName();
        this.methodNameList = rs.getSheetField("Action_Keyword");
        this.methodDescriptionList = rs.getSheetField("Description");
        this.methodElementTypeList = rs.getSheetField("Element_Type");
        this.methodLocatorTypeList = rs.getSheetField("Locator_Type");
        this.methodLocatorValueList = rs.getSheetField("Locator_Value");
        this.methodDataSetList = rs.getSheetField("Data_Set");
        this.methodVerifyTypeList = rs.getSheetField("Verify_Type");
        this.methodVerifyTargetList = rs.getSheetField("Verify_Target");
        this.methodVerifyValueList = rs.getSheetField("Verify_Value");
        this.methodScreenCaptureList = rs.getSheetField("Screen_Capture");

        logger.info("获取测试数据结构,从数据结构中取出所有测试数据,以列表形式存储");
    }

    /**
     * @Description: 按照类名称列表生成类
     */
    public void createClass() {
        this.ctList = new ArrayList<>();
        for (String ClassName : this.classNameList) {
            ctList.add(cPool.makeClass("com.highpin.test." + ClassName));
        }
        logger.info("创建所有可运行的测试类");
    }

    /**
     * @Description: 向类中加入属性
     */
    public void insertField() {
        CtField ctFieldDriver = null;
        CtField ctFieldExtentReports = null;
        CtField ctFieldExtentTest = null;
        for (CtClass ct: this.ctList) {
            try {
                // 加入WebDriver成员
                ctFieldDriver = new CtField(this.cPool.getCtClass("org.openqa.selenium.WebDriver"), "driver", ct);
                ctFieldDriver.setModifiers(Modifier.PRIVATE);
                // 加入ExtentReports成员
                ctFieldExtentReports = new CtField(this.cPool.getCtClass("com.relevantcodes.extentreports.ExtentReports"), "extent", ct);
                ctFieldExtentReports.setModifiers(Modifier.PRIVATE);
                // 加入ExtentTest成员
                ctFieldExtentTest = new CtField(this.cPool.getCtClass("com.relevantcodes.extentreports.ExtentTest"), "test", ct);
                ctFieldExtentTest.setModifiers(Modifier.PRIVATE);

                ct.addField(ctFieldDriver);
                ct.addField(ctFieldExtentReports);
                ct.addField(ctFieldExtentTest);
                logger.info("向类当中添加属性");
            } catch (CannotCompileException | NotFoundException e) {
                logger.error("添加属性失败");
                e.printStackTrace();
            }
        }
    }

    /**
     * @Description: 向类当中加入方法
     * @throws Exception -- 如果出现错误的元素类型则抛出NotFoundTestException
     */
    public void insertMethod() throws Exception {
        // 参数对象
        StepParameters sp = null;
        // 获取对应方法的语句
        String methodStatement = null;

        String beforeTestAnnotation = "org.testng.annotations.BeforeClass";
        String afterTestAnnotation = "org.testng.annotations.AfterClass";
        String testAnnotation = "org.testng.annotations.Test";

        for (int ctIndex = 0; ctIndex < this.ctList.size(); ++ctIndex) {
            // 创建参数对象
            sp = new StepParameters();
            // 获取类
            CtClass ctClass = this.ctList.get(ctIndex);
            // 显示测试类名称
            logger.info("类名称:" + ctClass.getName());
            logger.info("************************************************开始创建一个类************************************************");
            for (int methodIndex = 0; methodIndex < this.methodNameList.get(ctIndex).size(); ++methodIndex) {
                // 获取类名称
                sp.setClassName(this.ctList.get(ctIndex).getName());
                // 获取方法名称
                sp.setMethodName(this.methodNameList.get(ctIndex).get(methodIndex).toString());
                // 获取元素类型
                sp.setEleType(this.methodElementTypeList.get(ctIndex).get(methodIndex).toString());
                // 获取每个步骤对应的元素定位类型
                sp.setLocType(this.methodLocatorTypeList.get(ctIndex).get(methodIndex).toString());
                // 获取每个步骤对应的元素定位方式
                sp.setLocValue(this.methodLocatorValueList.get(ctIndex).get(methodIndex).toString());
                // 获取每个步骤对应的操作数据
                sp.setEleData(this.methodDataSetList.get(ctIndex).get(methodIndex).toString());
                // 获取每个步骤的描述
                sp.setDescription(this.methodDescriptionList.get(ctIndex).get(methodIndex).toString());
                // 获取每个步骤的验证类型(注意:传递过去的类型是List<String>)
                sp.setVerifyType(this.methodVerifyTypeList.get(ctIndex).get(methodIndex));
                // 获取每个步骤的验证页面路径(注意:传递过去的类型是List<String>)
                sp.setVerifyTarget(this.methodVerifyTargetList.get(ctIndex).get(methodIndex));
                // 获取每个步骤的验证值(注意:传递过去的类型是List<String>)
                sp.setVerifyValue(this.methodVerifyValueList.get(ctIndex).get(methodIndex));
                // 获取每个步骤是否截图
                sp.setScreenCapture(this.methodScreenCaptureList.get(ctIndex).get(methodIndex).toString());

                // 根据元素类型判断对应的操作
                if (sp.getMethodName().equals("openBrowser")) {
                    // 插入初始化浏览器方法
                    methodStatement = this.mt.openBrowser(sp);
                    this.addMethod(ctClass, sp.getMethodName(), methodStatement, beforeTestAnnotation, sp.getDescription());
                    logger.info("已加入方法--" + sp.getMethodName());
                } else if (sp.getMethodName().equals("closeBrowser")) {
                    // 插入关闭浏览器方法
                    methodStatement = this.mt.closeBrowser(sp);
                    this.addMethod(ctClass, sp.getMethodName(), methodStatement, afterTestAnnotation, sp.getDescription());
                    logger.info("已加入方法--" + sp.getMethodName());
                    // 插入等待方法--根据测试用例中的方法名前缀是否存在wait来判断
                } else if (sp.getMethodName().startsWith("wait")) {
                    methodStatement = this.mt.waitFor(sp);
                    this.addMethod(ctClass, sp.getMethodName(), methodStatement, testAnnotation, sp.getDescription());
                    logger.info("已加入方法--" + sp.getMethodName());
                } else if (!sp.getMethodName().equals("openBrowser") && !sp.getMethodName().equals("closeBrowser")) {
                    switch (sp.getEleType()) {
                        case "text":
                            methodStatement = this.mt.inputText(sp);
                            this.addMethod(ctClass, sp.getMethodName(), methodStatement, testAnnotation, sp.getDescription());
                            logger.info("已加入方法--" + sp.getMethodName());
                            break;
                        case "link":
                            methodStatement = this.mt.linkForward(sp);
                            this.addMethod(ctClass, sp.getMethodName(), methodStatement, testAnnotation, sp.getDescription());
                            logger.info("已加入方法--" + sp.getMethodName());
                            break;
                        case "button":
                            methodStatement = this.mt.buttonClick(sp);
                            this.addMethod(ctClass, sp.getMethodName(), methodStatement, testAnnotation, sp.getDescription());
                            logger.info("已加入方法--" + sp.getMethodName());
                            break;
                        case "submit":
                            methodStatement = this.mt.submitClick(sp);
                            this.addMethod(ctClass, sp.getMethodName(), methodStatement, testAnnotation, sp.getDescription());
                            logger.info("已加入方法--" + sp.getMethodName());
                            break;
                        case "select":
                            methodStatement = this.mt.selectOption(sp);
                            this.addMethod(ctClass, sp.getMethodName(), methodStatement, testAnnotation, sp.getDescription());
                            logger.info("已插入方法--" + sp.getMethodName());
                            break;
                        case "file":
                            methodStatement = this.mt.uploadFile(sp);
                            this.addMethod(ctClass, sp.getMethodName(), methodStatement, testAnnotation, sp.getDescription());
                            logger.info("已加入方法--" + sp.getMethodName());
                            break;
                        case "radioButton":
                            methodStatement = this.mt.radioButtonOper(sp);
                            this.addMethod(ctClass, sp.getMethodName(), methodStatement, testAnnotation, sp.getDescription());
                            logger.info("已加入方法--" + sp.getMethodName());
                            break;
                        case "checkBox":
                            methodStatement = this.mt.checkBoxOper(sp);
                            this.addMethod(ctClass, sp.getMethodName(), methodStatement, testAnnotation, sp.getDescription());
                            logger.info("已加入方法--" + sp.getMethodName());
                            break;
                        case "alert":
                            methodStatement = this.mt.popupAlert(sp);
                            this.addMethod(ctClass, sp.getMethodName(), methodStatement, testAnnotation, sp.getDescription());
                            logger.info("已加入方法--" + sp.getMethodName());
                            break;
                        case "mouseHold":
                            methodStatement = this.mt.mouseHold(sp);
                            this.addMethod(ctClass, sp.getMethodName(), methodStatement, testAnnotation, sp.getDescription());
                            logger.info("已加入方法--" + sp.getMethodName());
                            break;
                        case "mouseClick":
                            methodStatement = this.mt.mouseClick(sp);
                            this.addMethod(ctClass, sp.getMethodName(), methodStatement, testAnnotation, sp.getDescription());
                            logger.info("已加入方法--" + sp.getMethodName());
                            break;
                        default:
                            logger.error("未知的操作方法--" + sp.getMethodName());
                            // 需要加入异常处理
                            throw new NotFoundTestException("未知的操作方法: " + sp.getMethodName());
                    }
                }
            }
            logger.info("************************************************类已经创建完成************************************************");
        }
    }

    /**
     * @Description: 向类中加入方法
     * @param ctClass       --  当前类
     * @param methodName    --  当前add的方法名
     * @param methodBody    --  方法语句
     * @param annotTitle    --  方法注解名称
     * @param annotValue    --  方法注解描述
     */
    private void addMethod(CtClass ctClass, String methodName, String methodBody, String annotTitle, String annotValue) {
        CtMethod ctMethod = null;
        try {
            logger.info(methodBody);
            ctMethod = CtNewMethod.make(methodBody, ctClass);
            ctClass.addMethod(ctMethod);
        } catch (CannotCompileException | NullPointerException e) {
            e.printStackTrace();
        }

        if (methodName.equals("openBrowser") || methodName.equals("closeBrowser")) {
            this.addAnnotation(ctClass, ctMethod, annotTitle, "alwaysRun", true);
        } else {
            this.addAnnotation(ctClass, ctMethod, annotTitle, "description", annotValue);
        }
    }

    /**
     * @Description: 给方法加入Annotation
     * @param ctClass       -- 当前类
     * @param ctMethod      -- 当前add的方法
     * @param annotTitle    -- 当前方法的Annotation
     * @param annotKey      -- Annotation的键
     * @param annotValue    -- Annotation的值
     */
    private void addAnnotation(CtClass ctClass, CtMethod ctMethod, String annotTitle, String annotKey, Object annotValue) {
        // 给方法添加Annotation
        ClassFile classFile = ctClass.getClassFile();
        ConstPool cPool = classFile.getConstPool();
        String methodName = ctMethod.getName();
        AnnotationsAttribute attr = new AnnotationsAttribute(cPool, AnnotationsAttribute.visibleTag);
        Annotation annotation = new Annotation(annotTitle, cPool);
        if (methodName.equals("openBrowser") || methodName.equals("closeBrowser")) {
            // 如果Annotation的属性值是boolean类型
            annotation.addMemberValue(annotKey, new BooleanMemberValue((boolean)annotValue, cPool));
        } else {
            // 如果Annotation的属性值是String类型
            annotation.addMemberValue(annotKey, new StringMemberValue((String)annotValue, cPool));
        }
        attr.addAnnotation(annotation);
        ctMethod.getMethodInfo().addAttribute(attr);
    }

    /**
     * @Description: 获取一个类的所有方法 -- ClassRunner类测试使用
     * @param className -- 类名称
     * @return methodNameList -- 类当中所有方法(以List结构返回)
     */
    public List<String> getMethod(String className) {
        CtClass ct = null;
        List<String> methodNameList = new ArrayList<>();
        try {
            ct = this.cPool.get(className);
        } catch (NotFoundException e) {
            e.printStackTrace();
        }
        CtMethod [] ctMethodList = null;
        if (ct != null) {
            ctMethodList = ct.getDeclaredMethods();
            for (CtMethod aCtMethodList : ctMethodList) {
                System.out.println(aCtMethodList.getName());
                methodNameList.add(aCtMethodList.getName());
            }
        }
        return methodNameList;
    }

    /**
     * @Description: 获取所有类的方法 -- ClassRunner类测试使用
     * @return -- methodAllList 返回所有的方法(用List结构返回)
     */
    public List<List<String>> getAllClassMethodList() {
        List<List<String>> methodAllList = new ArrayList<>();
        for (CtClass aCtList : this.ctList) {
            logger.info(aCtList.getName());
            methodAllList.add(this.getMethod(aCtList.getName()));
            logger.info("************************************************************************************************");
        }
        return methodAllList;
    }

    /**
     * 获取所有要运行的类 -- ClassRunner类测试使用
     * @return -- this.ctList 成员变量this.ctList
     */
    public List<CtClass> getAllClassList() {
        return this.ctList;
    }

    // 测试--main方法
    public static void main(String[] args) throws Exception {
        ClassGenerator cg = new ClassGenerator();
        cg.createClass();
        cg.insertField();
        cg.insertMethod();
    }
}

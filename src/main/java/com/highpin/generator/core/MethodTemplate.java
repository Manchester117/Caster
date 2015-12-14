package com.highpin.generator.core;

import com.highpin.check.VerifyModule;
import com.highpin.except.NotFoundLocatorException;

/**
 * Created by Administrator on 2015/11/28.
 */
public class MethodTemplate {
    /**
     * @Description: 根据定位类型和定位值返回对应的元素选择语句
     * @param locatorType -- 元素定位类型
     * @param locatorValue -- 元素定位值
     * @return by -- 返回对应的定位语句
     * @throws Exception -- 如果出现定位类型/定位值不正确则抛出NotFoundLocatorException
     */
    public static String chooseLocator(String locatorType, String locatorValue) throws Exception {
        String by = null;
        switch (locatorType) {
            case "id":
                by = "org.openqa.selenium.By.id(\"" + locatorValue + "\")";
                break;
            case "name":
                by = "org.openqa.selenium.By.name(\"" + locatorValue + "\")";
                break;
            case "xpath":
                by = "org.openqa.selenium.By.xpath(\"" + locatorValue + "\")";
                break;
            case "linkText":
                by = "org.openqa.selenium.By.linkText(\"" + locatorValue + "\")";
                break;
            case "partialLinkText":
                by = "org.openqa.selenium.By.partialLinkText(\"" + locatorValue + "\")";
                break;
            case "tagName":
                by = "org.openqa.selenium.By.tagName(\"" + locatorValue + "\")";
                break;
            case "className":
                by = "org.openqa.selenium.By.className(\"" + locatorValue + "\")";
                break;
            case "cssSelector":
                by = "org.openqa.selenium.By.cssSelector(\"" + locatorValue + "\")";
                break;
            default:
                // 后续加入异常处理
                System.out.println("无法识别定位选择器");
                throw new NotFoundLocatorException("无法识别定位选择器: " + locatorValue);
        }
        return by;
    }

    // 初始化浏览器
    public String openBrowser(StepParameters sp) {
        String methodDefine = "public void " + sp.getMethodName() + "() {" +
                                    "String browser = \"" + sp.getEleData() + "\";" +
                                    "this.extent = new com.relevantcodes.extentreports.ExtentReports(\"report/HighPin UIAutomation Test Report.html\", java.lang.Boolean.FALSE);" +
                                    "this.extent.addSystemInfo(\"Selenium Version\", \"2.48.2\");" +
                                    "this.extent.addSystemInfo(\"Environment\", \"QA\");" +
                                    "this.test = this.extent.startTest(\"" + sp.getClassName() + "\", \"Login Flow\");" +
                                    "try {" +
                                        "if (browser.equals(\"Firefox\")) {" +
                                            "this.driver = new org.openqa.selenium.firefox.FirefoxDriver();" +
                                        "} else if (browser.equals(\"Chrome\")) {" +
                                            "System.setProperty(\"webdriver.chrome.driver\", \"browserdriver/chromedriver.exe\");" +
                                            "this.driver = new org.openqa.selenium.chrome.ChromeDriver();" +
                                        "} else if (browser.equals(\"IE\")) {" +
                                            "System.setProperty(\"webdriver.ie.driver\", \"browserdriver/IEDriverServer.exe\");" +
                                            "this.driver = new org.openqa.selenium.ie.InternetExplorerDriver();" +
                                        "}" +
                                        "this.driver.manage().timeouts().implicitlyWait(10L, java.util.concurrent.TimeUnit.SECONDS);" +
                                        "this.driver.manage().window().maximize();" +
                                        "this.test.log(com.relevantcodes.extentreports.LogStatus.PASS, \"" + sp.getDescription() + "\");" +
                                    "} catch (java.lang.Exception e) {" +
                                        "e.printStackTrace();" +
                                        "this.test.log(com.relevantcodes.extentreports.LogStatus.FAIL, e.getMessage());" +
                                    "}" +
                                    "java.lang.String imgPath = com.highpin.tools.Utility.captureScreenShot(this.driver, \"" + sp.getClassName() + "." + sp.getMethodName() + "\");" +
                                    "this.test.log(com.relevantcodes.extentreports.LogStatus.INFO, \"Snapshot below: \" + this.test.addScreenCapture(imgPath));" +
                              "}";
        System.out.println(methodDefine);
        return methodDefine;
    }

    // 关闭浏览器
    public String closeBrowser(StepParameters sp) {
        String methodDefine = "public void " + sp.getMethodName() + "() {" +
                                    "try {" +
                                        "this.driver.quit();" +
                                        "this.test.log(com.relevantcodes.extentreports.LogStatus.PASS, \"" + sp.getDescription() + "\");" +
                                    "} catch (java.lang.Exception e) {" +
                                        "e.printStackTrace();" +
                                        "this.test.log(com.relevantcodes.extentreports.LogStatus.FAIL, e.getMessage());" +
                                    "}" +
                                    "this.extent.endTest(this.test);" +
                                    "this.extent.flush();" +
                                    "this.extent.close();" +
                              "}";
        return methodDefine;
    }

    // 静态等待
    public String waitFor(StepParameters sp) {
        long millis = Long.parseLong(sp.getEleData()) * 1000L;
        String methodDefine = "public void " + sp.getMethodName() + "() {" +
                                    "try {" +
                                        "java.lang.Thread.sleep(" + millis + "L);" +
                                        "this.test.log(com.relevantcodes.extentreports.LogStatus.PASS, \"" + sp.getDescription() + "\");" +
                                    "} catch (java.lang.InterruptedException e) {" +
                                        "e.printStackTrace();" +
                                    "}" +
                              "}";
        return methodDefine;
    }

    // 输入文本框操作
    public String inputText(StepParameters sp) throws Exception {
        // 获取验证语句
        String verifyStatement = VerifyModule.addVerifyContentStatement(sp.getVerifyType(), sp.getVerifyTarget(), sp.getVerifyValue());
        String by = MethodTemplate.chooseLocator(sp.getLocType(), sp.getLocValue());
        // 这里要注意的是:sendkeys的参数类型是CharSequence...,而实际传入的是String类型,所以需要将String类型参数转换为CharSequence...
        // CharSequence是可变字符序列,String是不可变字符串
        // 转换方法:使用new String[]{"xxxxx"}
        String methodDefine = "public void " + sp.getMethodName() + "() {" +
                                    "try {" +
                                        "this.driver.manage().timeouts().implicitlyWait(10L, java.util.concurrent.TimeUnit.SECONDS);" +
                                        "org.openqa.selenium.WebElement element = this.driver.findElement(" + by + ");" +
                                        "element.clear();" +
                                        "element.sendKeys(new String[]{\"" + sp.getEleData() + "\"});" +
                                        verifyStatement +
                                        "this.test.log(com.relevantcodes.extentreports.LogStatus.PASS, \"" + sp.getDescription() + "\");" +
                                    "} catch (java.lang.Exception e) {" +
                                        "e.printStackTrace();" +
                                        "this.test.log(com.relevantcodes.extentreports.LogStatus.FAIL, e.getMessage());" +
                                    "}" +
                                    "java.lang.String imgPath = com.highpin.tools.Utility.captureScreenShot(this.driver, \"" + sp.getClassName() + "." + sp.getMethodName() + "\");" +
                                    "this.test.log(com.relevantcodes.extentreports.LogStatus.INFO, \"Snapshot below: \" + this.test.addScreenCapture(imgPath));" +
                              "}";
        return methodDefine;
    }

    // 选择下拉菜单操作
    public String selectOption(StepParameters sp) throws Exception {
        // 获取验证语句
        String verifyStatement = VerifyModule.addVerifyContentStatement(sp.getVerifyType(), sp.getVerifyTarget(), sp.getVerifyValue());
        String by = MethodTemplate.chooseLocator(sp.getLocType(), sp.getLocValue());
        String methodDefine = "public void " + sp.getMethodName() + "() {" +
                                    "try {" +
                                        "this.driver.manage().timeouts().implicitlyWait(10L, java.util.concurrent.TimeUnit.SECONDS);" +
                                        "org.openqa.selenium.WebElement element = this.driver.findElement(" + by + ");" +
                                        "org.openqa.selenium.support.ui.Select select = new org.openqa.selenium.support.ui.Select(element);" +
                                        "select.selectByValue(\"" + sp.getEleData() + "\");" +
                                        verifyStatement +
                                        "this.test.log(com.relevantcodes.extentreports.LogStatus.PASS, \"" + sp.getDescription() + "\");" +
                                    "} catch (java.lang.Exception e) {" +
                                        "e.printStackTrace();" +
                                        "this.test.log(com.relevantcodes.extentreports.LogStatus.FAIL, e.getMessage());" +
                                    "}" +
                                    "java.lang.String imgPath = com.highpin.tools.Utility.captureScreenShot(this.driver, \"" + sp.getClassName() + "." + sp.getMethodName() + "\");" +
                                    "this.test.log(com.relevantcodes.extentreports.LogStatus.INFO, \"Snapshot below: \" + this.test.addScreenCapture(imgPath));" +
                              "}";
        return methodDefine;
    }

    // 单选项操作
    public String radioButtonOper(StepParameters sp) throws Exception {
        // 获取验证语句
        String verifyStatement = VerifyModule.addVerifyContentStatement(sp.getVerifyType(), sp.getVerifyTarget(), sp.getVerifyValue());
        String by = MethodTemplate.chooseLocator(sp.getLocType(), sp.getLocValue());
        String methodDefine = "public void " + sp.getMethodName() + "() {" +
                                    "try {" +
                                        "this.driver.manage().timeouts().implicitlyWait(10L, java.util.concurrent.TimeUnit.SECONDS);" +
                                        "org.openqa.selenium.WebElement element = this.driver.findElement(" + by + ");" +
                                        "element.click();" +
                                        verifyStatement +
                                        "this.test.log(com.relevantcodes.extentreports.LogStatus.PASS, \"" + sp.getDescription() + "\");" +
                                    "} catch (java.lang.Exception e) {" +
                                        "e.printStackTrace();" +
                                        "this.test.log(com.relevantcodes.extentreports.LogStatus.FAIL, e.getMessage());" +
                                    "}" +
                                    "java.lang.String imgPath = com.highpin.tools.Utility.captureScreenShot(this.driver, \"" + sp.getClassName() + "." + sp.getMethodName() + "\");" +
                                    "this.test.log(com.relevantcodes.extentreports.LogStatus.INFO, \"Snapshot below: \" + this.test.addScreenCapture(imgPath));" +
                              "}";
        return methodDefine;
    }

    // 多选项操作
    public String checkBoxOper(StepParameters sp) throws Exception {
        // 获取验证语句
        String verifyStatement = VerifyModule.addVerifyContentStatement(sp.getVerifyType(), sp.getVerifyTarget(), sp.getVerifyValue());
        String by = MethodTemplate.chooseLocator(sp.getLocType(), sp.getLocValue());
        String methodDefine = "public void " + sp.getMethodName() + "() {" +
                                    "try {" +
                                        "this.driver.manage().timeouts().implicitlyWait(10L, java.util.concurrent.TimeUnit.SECONDS);" +
                                        "org.openqa.selenium.WebElement element = this.driver.findElement(" + by + ");" +
                                        "element.click();" +
                                        verifyStatement +
                                        "this.test.log(com.relevantcodes.extentreports.LogStatus.PASS, \"" + sp.getDescription() + "\");" +
                                    "} catch (java.lang.Exception e) {" +
                                        "e.printStackTrace();" +
                                        "this.test.log(com.relevantcodes.extentreports.LogStatus.FAIL, e.getMessage());" +
                                    "}" +
                                    "java.lang.String imgPath = com.highpin.tools.Utility.captureScreenShot(this.driver, \"" + sp.getClassName() + "." + sp.getMethodName() + "\");" +
                                    "this.test.log(com.relevantcodes.extentreports.LogStatus.INFO, \"Snapshot below: \" + this.test.addScreenCapture(imgPath));" +
                              "}";
        return methodDefine;
    }

    // 按钮操作
    public String buttonClick(StepParameters sp) throws Exception {
        // 获取验证语句
        String verifyStatement = VerifyModule.addVerifyContentStatement(sp.getVerifyType(), sp.getVerifyTarget(), sp.getVerifyValue());
        String by = MethodTemplate.chooseLocator(sp.getLocType(), sp.getLocValue());
        String methodDefine = "public void " + sp.getMethodName() + "() {" +
                                    "try {" +
                                        "this.driver.manage().timeouts().implicitlyWait(10L, java.util.concurrent.TimeUnit.SECONDS);" +
                                        "org.openqa.selenium.WebElement element = this.driver.findElement(" + by + ");" +
                                        "element.click();" +
                                        verifyStatement +
                                        "this.test.log(com.relevantcodes.extentreports.LogStatus.PASS, \"" + sp.getDescription() + "\");" +
                                    "} catch (java.lang.Exception e) {" +
                                        "e.printStackTrace();" +
                                        "this.test.log(com.relevantcodes.extentreports.LogStatus.FAIL, e.getMessage());" +
                                    "}" +
                                    "java.lang.String imgPath = com.highpin.tools.Utility.captureScreenShot(this.driver, \"" + sp.getClassName() + "." + sp.getMethodName() + "\");" +
                                    "this.test.log(com.relevantcodes.extentreports.LogStatus.INFO, \"Snapshot below: \" + this.test.addScreenCapture(imgPath));" +
                              "}";
        return methodDefine;
    }

    // 提交操作
    public String submitClick(StepParameters sp) throws Exception {
        // 获取验证语句
        String verifyStatement = VerifyModule.addVerifyContentStatement(sp.getVerifyType(), sp.getVerifyTarget(), sp.getVerifyValue());
        String by = MethodTemplate.chooseLocator(sp.getLocType(), sp.getLocValue());
        String methodDefine = "public void " + sp.getMethodName() + "() {" +
                                    "try {" +
                                        "this.driver.manage().timeouts().implicitlyWait(10L, java.util.concurrent.TimeUnit.SECONDS);" +
                                        "org.openqa.selenium.WebElement element = this.driver.findElement(" + by + ");" +
                                        "element.submit();" +
                                        verifyStatement +
                                        "this.test.log(com.relevantcodes.extentreports.LogStatus.PASS, \"" + sp.getDescription() + "\");" +
                                    "} catch (java.lang.Exception e) {" +
                                        "e.printStackTrace();" +
                                        "this.test.log(com.relevantcodes.extentreports.LogStatus.FAIL, e.getMessage());" +
                                    "}" +
                                    "java.lang.String imgPath = com.highpin.tools.Utility.captureScreenShot(this.driver, \"" + sp.getClassName() + "." + sp.getMethodName() + "\");" +
                                    "this.test.log(com.relevantcodes.extentreports.LogStatus.INFO, \"Snapshot below: \" + this.test.addScreenCapture(imgPath));" +
                              "}";
        return methodDefine;
    }

    // 上传文件
    public String uploadFile(StepParameters sp) throws Exception {
        // 获取验证语句
        String verifyStatement = VerifyModule.addVerifyContentStatement(sp.getVerifyType(), sp.getVerifyTarget(), sp.getVerifyValue());
        String by = MethodTemplate.chooseLocator(sp.getLocType(), sp.getLocValue());
        String methodDefine = "public void " + sp.getMethodName() + "() {" +
                                    "try {" +
                                        "this.driver.manage().timeouts().implicitlyWait(10L, java.util.concurrent.TimeUnit.SECONDS);" +
                                        "org.openqa.selenium.WebElement element = this.driver.findElement(" + by + ");" +
                                        "element.sendKeys(new String[]{\"" + sp.getEleData() + "\"});" +
                                        verifyStatement +
                                        "this.test.log(com.relevantcodes.extentreports.LogStatus.PASS, \"" + sp.getDescription() + "\");" +
                                    "} catch (java.lang.Exception e) {" +
                                        "e.printStackTrace();" +
                                        "this.test.log(com.relevantcodes.extentreports.LogStatus.FAIL, e.getMessage());" +
                                    "}" +
                                    "java.lang.String imgPath = com.highpin.tools.Utility.captureScreenShot(this.driver, \"" + sp.getClassName() + "." + sp.getMethodName() + "\");" +
                                    "this.test.log(com.relevantcodes.extentreports.LogStatus.INFO, \"Snapshot below: \" + this.test.addScreenCapture(imgPath));" +
                              "}";
        return methodDefine;
    }

    // 对话框操作
    public String popupAlert(StepParameters sp) throws Exception {
        // 获取验证语句
        String verifyStatement = VerifyModule.addVerifyContentStatement(sp.getVerifyType(), sp.getVerifyTarget(), sp.getVerifyValue());
        String methodDefine = "public void " + sp.getMethodName() + "() {" +
                                    "try {" +
                                        "this.driver.manage().timeouts().implicitlyWait(10L, java.util.concurrent.TimeUnit.SECONDS);" +
                                        "org.openqa.selenium.Alert alert = this.driver.switchTo().alert();" +
                                        "alert.accept();" +
                                        verifyStatement +
                                        "this.test.log(com.relevantcodes.extentreports.LogStatus.PASS, \"" + sp.getDescription() + "\");" +
                                    "} catch (java.lang.Exception e) {" +
                                        "e.printStackTrace();" +
                                        "this.test.log(com.relevantcodes.extentreports.LogStatus.FAIL, e.getMessage());" +
                                    "}" +
                                    "java.lang.String imgPath = com.highpin.tools.Utility.captureScreenShot(this.driver, \"" + sp.getClassName() + "." + sp.getMethodName() + "\");" +
                                    "this.test.log(com.relevantcodes.extentreports.LogStatus.INFO, \"Snapshot below: \" + this.test.addScreenCapture(imgPath));" +
                              "}";
        return methodDefine;
    }

    // 链接跳转
    public String linkForward(StepParameters sp) throws Exception {
        // 获取验证语句
        String verifyStatement = VerifyModule.addVerifyContentStatement(sp.getVerifyType(), sp.getVerifyTarget(), sp.getVerifyValue());
        String methodDefine = "public void " + sp.getMethodName() + "() {" +
                                    "try {" +
                                        "this.driver.manage().timeouts().implicitlyWait(10L, java.util.concurrent.TimeUnit.SECONDS);" +
                                        "this.driver.get(\"" + sp.getEleData() + "\");" +
                                        verifyStatement +
                                        "this.test.log(com.relevantcodes.extentreports.LogStatus.PASS, \"" + sp.getDescription() + "\");" +
                                    "} catch (java.lang.Exception e) {" +
                                        "e.printStackTrace();" +
                                        "this.test.log(com.relevantcodes.extentreports.LogStatus.FAIL, e.getMessage());" +
                                    "}" +
                                    "java.lang.String imgPath = com.highpin.tools.Utility.captureScreenShot(this.driver, \"" + sp.getClassName() + "." + sp.getMethodName() + "\");" +
                                    "this.test.log(com.relevantcodes.extentreports.LogStatus.INFO, \"Snapshot below: \" + this.test.addScreenCapture(imgPath));" +
                              "}";
        return methodDefine;
    }
}

package com.highpin.generator.core;

import com.highpin.check.VerifyModule;
import org.openqa.selenium.support.ui.WebDriverWait;
/**
 * Created by Peng.Zhao on 2015/11/28.
 */
public class MethodTemplate {

    // 初始化浏览器
    public String openBrowser(StepParameters sp) {
        String methodDefine = "public void " + sp.getMethodName() + "() {" +
                                    "String browser = \"" + sp.getDataSet() + "\";" +
                                    "this.extent = new com.relevantcodes.extentreports.ExtentReports(\"reports/" + sp.getSuiteName() + "/Automation Test Report-HighPin UI.html\", java.lang.Boolean.FALSE);" +
                                    "this.extent.addSystemInfo(\"Selenium Version\", \"2.48.2\");" +
                                    "this.extent.addSystemInfo(\"Environment\", \"QA\");" +
                                    "this.test = this.extent.startTest(\"" + sp.getClassName() + "\", \"HighPin Automation Test\");" +
                                    "try {" +
                                        "if (browser.equals(\"Firefox\")) {" +
                                            "this.driver = new org.openqa.selenium.firefox.FirefoxDriver();" +
                                        "} else if (browser.equals(\"Chrome\")) {" +
                                            "this.service = new org.openqa.selenium.chrome.ChromeDriverService.Builder().usingDriverExecutable(new java.io.File(\"browserdriver/chromedriver.exe\")).usingAnyFreePort().build();" +
                                            "this.service.start();" +
                                            "this.driver = new org.openqa.selenium.chrome.ChromeDriver((org.openqa.selenium.chrome.ChromeDriverService)this.service);" +
                                        "} else if (browser.equals(\"IE\")) {" +
                                            "this.service = new org.openqa.selenium.ie.InternetExplorerDriverService.Builder().usingDriverExecutable(new java.io.File(\"browserdriver/IEDriverServer.exe\")).usingAnyFreePort().build();" +
                                            "this.service.start();" +
                                            "this.driver = new org.openqa.selenium.ie.InternetExplorerDriver((org.openqa.selenium.ie.InternetExplorerDriverService)this.service);" +
                                        "}" +
                                        "this.driver.manage().timeouts().implicitlyWait(10L, java.util.concurrent.TimeUnit.SECONDS);" +
                                        "this.driver.manage().window().maximize();" +
                                        "this.test.log(com.relevantcodes.extentreports.LogStatus.PASS, \"" + sp.getDescription() + "\");" +
                                    "} catch (java.lang.Exception e) {" +
                                        "e.printStackTrace();" +
                                        "this.test.log(com.relevantcodes.extentreports.LogStatus.FAIL, \"" + sp.getDescription() + "\" + \":  \" + e.getMessage());" +
                                    "}" +
                                    "if (\"Yes\".equals(\"" + sp.getScreenCapture() + "\")) {" +
                                        "java.lang.String imgPath = com.highpin.tools.Utility.captureScreenShot(this.driver, \"" + sp.getSuiteName() + "\", \"" + sp.getClassName() + "." + sp.getMethodName() + "\");" +
                                        "this.test.log(com.relevantcodes.extentreports.LogStatus.INFO, \"截图 -- " + sp.getDescription() + ": \" + this.test.addScreenCapture(imgPath));" +
                                    "}" +
                              "}";
        return methodDefine;
    }

    // 关闭浏览器
    public String closeBrowser(StepParameters sp) {
        String methodDefine = "public void " + sp.getMethodName() + "() {" +
                                    "try {" +
                                        "this.driver.close();" +
                                        "this.driver.quit();" +
                                        "this.test.log(com.relevantcodes.extentreports.LogStatus.PASS, \"" + sp.getDescription() + "\");" +
                                    "} catch (java.lang.Exception e) {" +
                                        "e.printStackTrace();" +
                                        "this.test.log(com.relevantcodes.extentreports.LogStatus.FAIL, \"" + sp.getDescription() + "\" + \":  \" + e.getMessage());" +
                                    "} finally {" +
                                        "this.extent.endTest(this.test);" +
                                        "this.extent.flush();" +
                                        "this.extent.close();" +
                                        "if (this.service != null) {" +
                                            "this.service.stop();" +
                                        "}" +
                                    "}" +
                              "}";
        return methodDefine;
    }

    // 静态等待
    public String waitFor(StepParameters sp) throws Exception {
        String verifyStatement = VerifyModule.appendVerifyContentStatement(sp);
        long millis = Long.parseLong(sp.getDataSet()) * 1000L;
        String methodDefine = "public void " + sp.getMethodName() + "() {" +
                                    "try {" +
                                        "java.lang.Thread.sleep(" + millis + "L);" +
                                        "this.test.log(com.relevantcodes.extentreports.LogStatus.INFO, \"" + sp.getDescription() + "\");" +
                                    "} catch (java.lang.InterruptedException e) {" +
                                        "e.printStackTrace();" +
                                    "}" +
                                    verifyStatement +
                                    "if (\"Yes\".equals(\"" + sp.getScreenCapture() + "\")) {" +
                                        "java.lang.String imgPath = com.highpin.tools.Utility.captureScreenShot(this.driver, \"" + sp.getSuiteName() + "\", \"" + sp.getClassName() + "." + sp.getMethodName() + "\");" +
                                        "this.test.log(com.relevantcodes.extentreports.LogStatus.INFO, \"截图 -- " + sp.getDescription() + ": \" + this.test.addScreenCapture(imgPath));" +
                                    "}" +
                              "}";
        return methodDefine;
    }

    // 输入文本框操作
    public String inputText(StepParameters sp) throws Exception {
        // 获取验证语句
        String verifyStatement = VerifyModule.appendVerifyContentStatement(sp);
        String by = LocatorTemplate.chooseLocator(sp.getLocType(), sp.getLocValue());
        // 这里要注意的是:sendkeys的参数类型是CharSequence...,而实际传入的是String类型,所以需要将String类型参数转换为CharSequence...
        // CharSequence是可变字符序列,String是不可变字符串
        // 转换方法:使用new String[]{"xxxxx"}
        String methodDefine = "public void " + sp.getMethodName() + "() {" +
                                    "try {" +
                                        "org.openqa.selenium.support.ui.WebDriverWait wait = new org.openqa.selenium.support.ui.WebDriverWait(this.driver, 30L);" +
//                                        "this.driver.manage().timeouts().implicitlyWait(10L, java.util.concurrent.TimeUnit.SECONDS);" +
                                        "org.openqa.selenium.WebElement element = wait.until(org.openqa.selenium.support.ui.ExpectedConditions.presenceOfElementLocated(" + by + "));" +
                                        "element.sendKeys(new String[]{\"" + sp.getDataSet() + "\"});" +
                                        verifyStatement +
                                        "this.test.log(com.relevantcodes.extentreports.LogStatus.PASS, \"" + sp.getDescription() + "\");" +
                                    "} catch (java.lang.Exception e) {" +
                                        "e.printStackTrace();" +
                                        "this.test.log(com.relevantcodes.extentreports.LogStatus.FAIL, \"" + sp.getDescription() + "\" + \":  \" + e.getMessage());" +
                                    "}" +
                                    "if (\"Yes\".equals(\"" + sp.getScreenCapture() + "\")) {" +
                                        "java.lang.String imgPath = com.highpin.tools.Utility.captureScreenShot(this.driver, \"" + sp.getSuiteName() + "\", \"" + sp.getClassName() + "." + sp.getMethodName() + "\");" +
                                        "this.test.log(com.relevantcodes.extentreports.LogStatus.INFO, \"截图 -- " + sp.getDescription() + ": \" + this.test.addScreenCapture(imgPath));" +
                                    "}" +
                              "}";
        return methodDefine;
    }

    // 选择下拉菜单操作
    public String selectOption(StepParameters sp) throws Exception {
        // 获取验证语句
        String verifyStatement = VerifyModule.appendVerifyContentStatement(sp);
        String by = LocatorTemplate.chooseLocator(sp.getLocType(), sp.getLocValue());
        String methodDefine = "public void " + sp.getMethodName() + "() {" +
                                    "try {" +
                                        "org.openqa.selenium.support.ui.WebDriverWait wait = new org.openqa.selenium.support.ui.WebDriverWait(this.driver, 30L);" +
//                                        "this.driver.manage().timeouts().implicitlyWait(10L, java.util.concurrent.TimeUnit.SECONDS);" +
                                        "org.openqa.selenium.WebElement element = wait.until(org.openqa.selenium.support.ui.ExpectedConditions.presenceOfElementLocated(" + by + "));" +
                                        "org.openqa.selenium.support.ui.Select select = new org.openqa.selenium.support.ui.Select(element);" +
                                        "select.selectByVisibleText(\"" + sp.getDataSet() + "\");" +
                                        verifyStatement +
                                        "this.test.log(com.relevantcodes.extentreports.LogStatus.PASS, \"" + sp.getDescription() + "\");" +
                                    "} catch (java.lang.Exception e) {" +
                                        "e.printStackTrace();" +
                                        "this.test.log(com.relevantcodes.extentreports.LogStatus.FAIL, \"" + sp.getDescription() + "\" + \":  \" + e.getMessage());" +
                                    "}" +
                                    "if (\"Yes\".equals(\"" + sp.getScreenCapture() + "\")) {" +
                                        "java.lang.String imgPath = com.highpin.tools.Utility.captureScreenShot(this.driver, \"" + sp.getSuiteName() + "\", \"" + sp.getClassName() + "." + sp.getMethodName() + "\");" +
                                        "this.test.log(com.relevantcodes.extentreports.LogStatus.INFO, \"截图 -- " + sp.getDescription() + ": \" + this.test.addScreenCapture(imgPath));" +
                                    "}" +
                              "}";
        return methodDefine;
    }

    // 单选项操作
    public String radioButtonOper(StepParameters sp) throws Exception {
        // 获取验证语句
        String verifyStatement = VerifyModule.appendVerifyContentStatement(sp);
        String by = LocatorTemplate.chooseLocator(sp.getLocType(), sp.getLocValue());
        String methodDefine = "public void " + sp.getMethodName() + "() {" +
                                    "try {" +
                                        "org.openqa.selenium.support.ui.WebDriverWait wait = new org.openqa.selenium.support.ui.WebDriverWait(this.driver, 30L);" +
//                                        "this.driver.manage().timeouts().implicitlyWait(10L, java.util.concurrent.TimeUnit.SECONDS);" +
                                        "org.openqa.selenium.WebElement element = wait.until(org.openqa.selenium.support.ui.ExpectedConditions.presenceOfElementLocated(" + by + "));" +
                                        "element.click();" +
                                        verifyStatement +
                                        "this.test.log(com.relevantcodes.extentreports.LogStatus.PASS, \"" + sp.getDescription() + "\");" +
                                    "} catch (java.lang.Exception e) {" +
                                        "e.printStackTrace();" +
                                        "this.test.log(com.relevantcodes.extentreports.LogStatus.FAIL, \"" + sp.getDescription() + "\" + \":  \" + e.getMessage());" +
                                    "}" +
                                    "if (\"Yes\".equals(\"" + sp.getScreenCapture() + "\")) {" +
                                        "java.lang.String imgPath = com.highpin.tools.Utility.captureScreenShot(this.driver, \"" + sp.getSuiteName() + "\", \"" + sp.getClassName() + "." + sp.getMethodName() + "\");" +
                                        "this.test.log(com.relevantcodes.extentreports.LogStatus.INFO, \"截图--" + sp.getDescription() + ": \" + this.test.addScreenCapture(imgPath));" +
                                    "}" +
                              "}";
        return methodDefine;
    }

    // 多选项操作
    public String checkBoxOper(StepParameters sp) throws Exception {
        // 获取验证语句
        String verifyStatement = VerifyModule.appendVerifyContentStatement(sp);
        String by = LocatorTemplate.chooseLocator(sp.getLocType(), sp.getLocValue());
        String methodDefine = "public void " + sp.getMethodName() + "() {" +
                                    "try {" +
                                        "org.openqa.selenium.support.ui.WebDriverWait wait = new org.openqa.selenium.support.ui.WebDriverWait(this.driver, 30L);" +
//                                        "this.driver.manage().timeouts().implicitlyWait(10L, java.util.concurrent.TimeUnit.SECONDS);" +
                                        "org.openqa.selenium.WebElement element = wait.until(org.openqa.selenium.support.ui.ExpectedConditions.presenceOfElementLocated(" + by + "));" +
                                        "element.click();" +
                                        verifyStatement +
                                        "this.test.log(com.relevantcodes.extentreports.LogStatus.PASS, \"" + sp.getDescription() + "\");" +
                                    "} catch (java.lang.Exception e) {" +
                                        "e.printStackTrace();" +
                                        "this.test.log(com.relevantcodes.extentreports.LogStatus.FAIL, \"" + sp.getDescription() + "\" + \":  \" + e.getMessage());" +
                                    "}" +
                                    "if (\"Yes\".equals(\"" + sp.getScreenCapture() + "\")) {" +
                                        "java.lang.String imgPath = com.highpin.tools.Utility.captureScreenShot(this.driver, \"" + sp.getSuiteName() + "\", \"" + sp.getClassName() + "." + sp.getMethodName() + "\");" +
                                        "this.test.log(com.relevantcodes.extentreports.LogStatus.INFO, \"截图 -- " + sp.getDescription() + ": \" + this.test.addScreenCapture(imgPath));" +
                                    "}" +
                              "}";
        return methodDefine;
    }

    // 按钮操作
    public String buttonClick(StepParameters sp) throws Exception {
        // 获取验证语句
        String verifyStatement = VerifyModule.appendVerifyContentStatement(sp);
        String by = LocatorTemplate.chooseLocator(sp.getLocType(), sp.getLocValue());
        String methodDefine = "public void " + sp.getMethodName() + "() {" +
                                    "try {" +
                                        "org.openqa.selenium.support.ui.WebDriverWait wait = new org.openqa.selenium.support.ui.WebDriverWait(this.driver, 30L);" +
//                                        "this.driver.manage().timeouts().implicitlyWait(10L, java.util.concurrent.TimeUnit.SECONDS);" +
                                        "org.openqa.selenium.WebElement element = wait.until(org.openqa.selenium.support.ui.ExpectedConditions.presenceOfElementLocated(" + by + "));" +
                                        "element.click();" +
                                        verifyStatement +
                                        "this.test.log(com.relevantcodes.extentreports.LogStatus.PASS, \"" + sp.getDescription() + "\");" +
                                    "} catch (java.lang.Exception e) {" +
                                        "e.printStackTrace();" +
                                        "this.test.log(com.relevantcodes.extentreports.LogStatus.FAIL, \"" + sp.getDescription() + "\" + \":  \" + e.getMessage());" +
                                    "}" +
                                    "if (\"Yes\".equals(\"" + sp.getScreenCapture() + "\")) {" +
                                        "java.lang.String imgPath = com.highpin.tools.Utility.captureScreenShot(this.driver, \"" + sp.getSuiteName() + "\", \"" + sp.getClassName() + "." + sp.getMethodName() + "\");" +
                                        "this.test.log(com.relevantcodes.extentreports.LogStatus.INFO, \"截图 -- " + sp.getDescription() + ": \" + this.test.addScreenCapture(imgPath));" +
                                    "}" +
                              "}";
        return methodDefine;
    }

    // 提交操作
    public String submitClick(StepParameters sp) throws Exception {
        // 获取验证语句
        String verifyStatement = VerifyModule.appendVerifyContentStatement(sp);
        String by = LocatorTemplate.chooseLocator(sp.getLocType(), sp.getLocValue());
        String methodDefine = "public void " + sp.getMethodName() + "() {" +
                                    "try {" +
                                        "org.openqa.selenium.support.ui.WebDriverWait wait = new org.openqa.selenium.support.ui.WebDriverWait(this.driver, 30L);" +
//                                        "this.driver.manage().timeouts().implicitlyWait(10L, java.util.concurrent.TimeUnit.SECONDS);" +
                                        "org.openqa.selenium.WebElement element = wait.until(org.openqa.selenium.support.ui.ExpectedConditions.presenceOfElementLocated(" + by + "));" +
                                        "element.submit();" +
                                        verifyStatement +
                                        "this.test.log(com.relevantcodes.extentreports.LogStatus.PASS, \"" + sp.getDescription() + "\");" +
                                    "} catch (java.lang.Exception e) {" +
                                        "e.printStackTrace();" +
                                        "this.test.log(com.relevantcodes.extentreports.LogStatus.FAIL, \"" + sp.getDescription() + "\" + \":  \" + e.getMessage());" +
                                    "}" +
                                    "if (\"Yes\".equals(\"" + sp.getScreenCapture() + "\")) {" +
                                        "java.lang.String imgPath = com.highpin.tools.Utility.captureScreenShot(this.driver, \"" + sp.getSuiteName() + "\", \"" + sp.getClassName() + "." + sp.getMethodName() + "\");" +
                                        "this.test.log(com.relevantcodes.extentreports.LogStatus.INFO, \"截图 -- " + sp.getDescription() + ": \" + this.test.addScreenCapture(imgPath));" +
                                    "}" +
                              "}";
        return methodDefine;
    }

    // 上传文件
    public String uploadFile(StepParameters sp) throws Exception {
        // 获取验证语句
        String verifyStatement = VerifyModule.appendVerifyContentStatement(sp);
        String by = LocatorTemplate.chooseLocator(sp.getLocType(), sp.getLocValue());
        String methodDefine = "public void " + sp.getMethodName() + "() {" +
                                    "try {" +
                                        "org.openqa.selenium.support.ui.WebDriverWait wait = new org.openqa.selenium.support.ui.WebDriverWait(this.driver, 30L);" +
//                                        "this.driver.manage().timeouts().implicitlyWait(10L, java.util.concurrent.TimeUnit.SECONDS);" +
                                        "org.openqa.selenium.WebElement element = wait.until(org.openqa.selenium.support.ui.ExpectedConditions.presenceOfElementLocated(" + by + "));" +
                                        "element.sendKeys(new String[]{\"" + sp.getDataSet() + "\"});" +
                                        verifyStatement +
                                        "this.test.log(com.relevantcodes.extentreports.LogStatus.PASS, \"" + sp.getDescription() + "\");" +
                                    "} catch (java.lang.Exception e) {" +
                                        "e.printStackTrace();" +
                                        "this.test.log(com.relevantcodes.extentreports.LogStatus.FAIL, \"" + sp.getDescription() + "\" + \":  \" + e.getMessage());" +
                                    "}" +
                                    "if (\"Yes\".equals(\"" + sp.getScreenCapture() + "\")) {" +
                                        "java.lang.String imgPath = com.highpin.tools.Utility.captureScreenShot(this.driver, \"" + sp.getSuiteName() + "\", \"" + sp.getClassName() + "." + sp.getMethodName() + "\");" +
                                        "this.test.log(com.relevantcodes.extentreports.LogStatus.INFO, \"截图 -- " + sp.getDescription() + ": \" + this.test.addScreenCapture(imgPath));" +
                                    "}" +
                              "}";
        return methodDefine;
    }

    // 对话框操作
    public String popupAlert(StepParameters sp) throws Exception {
        // 获取验证语句
        String verifyStatement = VerifyModule.appendVerifyContentStatement(sp);
        String methodDefine = "public void " + sp.getMethodName() + "() {" +
                                    "try {" +
                                        "this.driver.manage().timeouts().implicitlyWait(10L, java.util.concurrent.TimeUnit.SECONDS);" +
                                        "org.openqa.selenium.Alert alert = this.driver.switchTo().alert();" +
                                        "alert.accept();" +
                                        verifyStatement +
                                        "this.test.log(com.relevantcodes.extentreports.LogStatus.PASS, \"" + sp.getDescription() + "\");" +
                                    "} catch (java.lang.Exception e) {" +
                                        "e.printStackTrace();" +
                                        "this.test.log(com.relevantcodes.extentreports.LogStatus.FAIL, \"" + sp.getDescription() + "\" + \":  \" + e.getMessage());" +
                                    "}" +
                                    "if (\"Yes\".equals(\"" + sp.getScreenCapture() + "\")) {" +
                                        "java.lang.String imgPath = com.highpin.tools.Utility.captureScreenShot(this.driver, \"" + sp.getSuiteName() + "\", \"" + sp.getClassName() + "." + sp.getMethodName() + "\");" +
                                        "this.test.log(com.relevantcodes.extentreports.LogStatus.INFO, \"截图 -- " + sp.getDescription() + ": \" + this.test.addScreenCapture(imgPath));" +
                                    "}" +
                              "}";
        return methodDefine;
    }

    // 链接跳转
    public String linkForward(StepParameters sp) throws Exception {
        // 获取验证语句
        String verifyStatement = VerifyModule.appendVerifyContentStatement(sp);
        String methodDefine = "public void " + sp.getMethodName() + "() {" +
                                    "try {" +
                                        "this.driver.manage().timeouts().implicitlyWait(10L, java.util.concurrent.TimeUnit.SECONDS);" +
                                        "this.driver.get(\"" + sp.getDataSet() + "\");" +
                                        verifyStatement +
                                        "this.test.log(com.relevantcodes.extentreports.LogStatus.PASS, \"" + sp.getDescription() + "\");" +
                                    "} catch (java.lang.Exception e) {" +
                                        "e.printStackTrace();" +
                                        "this.test.log(com.relevantcodes.extentreports.LogStatus.FAIL, \"" + sp.getDescription() + "\" + \":  \" + e.getMessage());" +
                                    "}" +
                                    "if (\"Yes\".equals(\"" + sp.getScreenCapture() + "\")) {" +
                                        "java.lang.String imgPath = com.highpin.tools.Utility.captureScreenShot(this.driver, \"" + sp.getSuiteName() + "\", \"" + sp.getClassName() + "." + sp.getMethodName() + "\");" +
                                        "this.test.log(com.relevantcodes.extentreports.LogStatus.INFO, \"截图 -- " + sp.getDescription() + ": \" + this.test.addScreenCapture(imgPath));" +
                                    "}" +
                              "}";
        return methodDefine;
    }

    public String javaScriptClick(StepParameters sp) throws Exception {
        String verifyStatement = VerifyModule.appendVerifyContentStatement(sp);
        String by = LocatorTemplate.chooseLocator(sp.getLocType(), sp.getLocValue());
        String methodDefine = "public void " + sp.getMethodName() + "() {" +
                                    "try {" +
                                        "this.driver.manage().timeouts().implicitlyWait(10L, java.util.concurrent.TimeUnit.SECONDS);" +
                                        "org.openqa.selenium.JavascriptExecutor jsExecutor = (org.openqa.selenium.JavascriptExecutor)this.driver;" +
                                        "java.lang.String jsCode = \"" + by + ".click();\";" +
                                        "jsExecutor.executeScript(jsCode, new java.lang.Object[]{\"\"});" +
                                        verifyStatement +
                                        "this.test.log(com.relevantcodes.extentreports.LogStatus.PASS, \"" + sp.getDescription() + "\");" +
                                    "} catch (java.lang.Exception e) {" +
                                        "e.printStackTrace();" +
                                        "this.test.log(com.relevantcodes.extentreports.LogStatus.FAIL, \"" + sp.getDescription() + "\" + \":  \" + e.getMessage());" +
                                    "}" +
                                    "if (\"Yes\".equals(\"" + sp.getScreenCapture() + "\")) {" +
                                        "java.lang.String imgPath = com.highpin.tools.Utility.captureScreenShot(this.driver, \"" + sp.getSuiteName() + "\", \"" + sp.getClassName() + "." + sp.getMethodName() + "\");" +
                                        "this.test.log(com.relevantcodes.extentreports.LogStatus.INFO, \"截图 -- " + sp.getDescription() + ": \" + this.test.addScreenCapture(imgPath));" +
                                    "}" +
                              "}";
        return methodDefine;
    }

    // 鼠标悬停
    public String mouseHold(StepParameters sp) throws Exception {
        String verifyStatement = VerifyModule.appendVerifyContentStatement(sp);
        String by = LocatorTemplate.chooseLocator(sp.getLocType(), sp.getLocValue());
        String methodDefine = "public void " + sp.getMethodName() + "() {" +
                                    "try {" +
                                        "org.openqa.selenium.support.ui.WebDriverWait wait = new org.openqa.selenium.support.ui.WebDriverWait(this.driver, 30L);" +
//                                        "this.driver.manage().timeouts().implicitlyWait(10L, java.util.concurrent.TimeUnit.SECONDS);" +
                                        "org.openqa.selenium.WebElement element = wait.until(org.openqa.selenium.support.ui.ExpectedConditions.presenceOfElementLocated(" + by + "));" +
                                        "org.openqa.selenium.interactions.Actions actions = new org.openqa.selenium.interactions.Actions(this.driver);" +
                                        "actions.moveToElement(element).perform();" +
                                        verifyStatement +
                                        "this.test.log(com.relevantcodes.extentreports.LogStatus.PASS, \"" + sp.getDescription() + "\");" +
                                    "} catch (java.lang.Exception e) {" +
                                        "e.printStackTrace();" +
                                        "this.test.log(com.relevantcodes.extentreports.LogStatus.FAIL, \"" + sp.getDescription() + "\" + \":  \" + e.getMessage());" +
                                    "}" +
                                    "if (\"Yes\".equals(\"" + sp.getScreenCapture() + "\")) {" +
                                        "java.lang.String imgPath = com.highpin.tools.Utility.captureScreenShot(this.driver, \"" + sp.getSuiteName() + "\", \"" + sp.getClassName() + "." + sp.getMethodName() + "\");" +
                                        "this.test.log(com.relevantcodes.extentreports.LogStatus.INFO, \"截图 -- " + sp.getDescription() + ": \" + this.test.addScreenCapture(imgPath));" +
                                    "}" +
                              "}";
        return methodDefine;
    }

    // 鼠标点击
    public String mouseClick(StepParameters sp) throws Exception {
        String verifyStatement = VerifyModule.appendVerifyContentStatement(sp);
        String by = LocatorTemplate.chooseLocator(sp.getLocType(), sp.getLocValue());
        String methodDefine = "public void " + sp.getMethodName() + "() {" +
                                    "try {" +
                                        "org.openqa.selenium.support.ui.WebDriverWait wait = new org.openqa.selenium.support.ui.WebDriverWait(this.driver, 30L);" +
//                                        "this.driver.manage().timeouts().implicitlyWait(10L, java.util.concurrent.TimeUnit.SECONDS);" +
                                        "org.openqa.selenium.WebElement element = wait.until(org.openqa.selenium.support.ui.ExpectedConditions.presenceOfElementLocated(" + by + "));" +
                                        "org.openqa.selenium.interactions.Actions actions = new org.openqa.selenium.interactions.Actions(this.driver);" +
                                        "actions.click(element).perform();" +
                                        verifyStatement +
                                        "this.test.log(com.relevantcodes.extentreports.LogStatus.PASS, \"" + sp.getDescription() + "\");" +
                                    "} catch (java.lang.Exception e) {" +
                                        "e.printStackTrace();" +
                                        "this.test.log(com.relevantcodes.extentreports.LogStatus.FAIL, \"" + sp.getDescription() + "\" + \":  \" + e.getMessage());" +
                                    "}" +
                                    "if (\"Yes\".equals(\"" + sp.getScreenCapture() + "\")) {" +
                                        "java.lang.String imgPath = com.highpin.tools.Utility.captureScreenShot(this.driver, \"" + sp.getSuiteName() + "\", \"" + sp.getClassName() + "." + sp.getMethodName() + "\");" +
                                        "this.test.log(com.relevantcodes.extentreports.LogStatus.INFO, \"截图 -- " + sp.getDescription() + ": \" + this.test.addScreenCapture(imgPath));" +
                                    "}" +
                              "}";
        return methodDefine;
    }
}

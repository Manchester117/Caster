package com.highpin.check;

import com.highpin.generator.core.LocatorTemplate;
import com.highpin.generator.core.StepParameters;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Created by Administrator on 2015/12/6.
 */
public class VerifyModule {
    public static Logger logger = LogManager.getLogger(VerifyModule.class.getName());

    /**
     * @Description: 向方法中加入验证语句,当前是对文本进行验证
     * @param verifyType    --  验证类型
     * @param verifyTarget  --  验证路径
     * @param verifyValue   --  验证值
     * @return  -- 返回验证语句
     * @throws Exception    --  如果验证类型/验证路径不正确则抛出NotFoundLocatorException
     */
    public static String createVerifyContentStatement(String verifyType, String verifyTarget, String verifyValue) throws Exception {
        String verifyStatement = "";
        if (!verifyType.isEmpty() && !verifyTarget.isEmpty() && !verifyValue.isEmpty()) {
            verifyStatement = "try {" +
                                    "java.lang.Thread.sleep(250L);" +
                                    "org.openqa.selenium.WebElement verifyElem = this.driver.findElement(" + LocatorTemplate.chooseLocator(verifyType, verifyTarget) + ");" +
                                    "java.lang.String targetText = verifyElem.getText();" +
                                    "if (targetText.contains(\"" + verifyValue + "\")) {" +
                                        "this.test.log(com.relevantcodes.extentreports.LogStatus.PASS, \"" + verifyValue + "\" + \" ---- 存在\");" +
                                    "} else {" +
                                        "this.test.log(com.relevantcodes.extentreports.LogStatus.FAIL, \"" + verifyValue + "\" + \" ---- 不存在\");" +
                                    "}" +
                              "} catch (java.lang.Exception e) {" +
                                    "this.test.log(com.relevantcodes.extentreports.LogStatus.FAIL, \"" + verifyTarget + "\" + \" ---- 未找到: \" + e.getMessage());" +
                              "}";
            logger.info("添加一个测试验证");
        }
        return verifyStatement;
    }

    /**
     * @Description: 将多个验证语句进行拼接
     * @param sp       --       测试数据结构
     * @return verifyStatement  --  返回拼接的验证语句
     * @throws Exception 如果验证类型/验证路径不正确则抛出NotFoundLocatorException
     */
    public static String appendVerifyContentStatement(StepParameters sp) throws Exception {
        String verifyTypeString = null;
        String verifyTargetString = null;
        String verifyValueString = null;
        // 获取验证语句
        String verifyStatement = "";
        // 验证点遍历
        for (int v = 0; v < sp.getVerifyValue().size(); ++v) {
            verifyTypeString = sp.getVerifyType().get(v).toString();
            verifyTargetString = sp.getVerifyTarget().get(v).toString();
            verifyValueString = sp.getVerifyValue().get(v).toString();
            verifyStatement += VerifyModule.createVerifyContentStatement(verifyTypeString, verifyTargetString, verifyValueString);
        }
        logger.info("添加当前步骤的全部测试验证");
        return verifyStatement;
    }
}

package com.highpin.generator.core;

/**
 * Created by Administrator on 2015/12/12.
 */
public class StepParameters {
    /**
     * @Description: POJO类 -- 用于封装方法参数
     */
    private String className;
    private String methodName;
    private String eleType;
    private String locType;
    private String locValue;
    private String eleData;
    private String description;
    private String verifyType;
    private String verifyTarget;
    private String verifyValue;
    private String screenCapture;

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public String getEleType() {
        return eleType;
    }

    public void setEleType(String eleType) {
        this.eleType = eleType;
    }

    public String getLocType() {
        return locType;
    }

    public void setLocType(String locType) {
        this.locType = locType;
    }

    public String getLocValue() {
        return locValue;
    }

    public void setLocValue(String locValue) {
        this.locValue = locValue;
    }

    public String getEleData() {
        return eleData;
    }

    public void setEleData(String eleData) {
        this.eleData = eleData;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getVerifyType() {
        return verifyType;
    }

    public void setVerifyType(String verifyType) {
        this.verifyType = verifyType;
    }

    public String getVerifyTarget() {
        return verifyTarget;
    }

    public void setVerifyTarget(String verifyTarget) {
        this.verifyTarget = verifyTarget;
    }

    public String getVerifyValue() {
        return verifyValue;
    }

    public void setVerifyValue(String verifyValue) {
        this.verifyValue = verifyValue;
    }

    public String getScreenCapture() {
        return screenCapture;
    }

    public void setScreenCapture(String screenCapture) {
        this.screenCapture = screenCapture;
    }
}

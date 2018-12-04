package com.caster.generator.core;

import javassist.CannotCompileException;
import javassist.CtClass;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

/**
 * Created by Peng.Zhao on 2015/12/1.
 */
public class ClassRunner {
    public void reflectRunClass(List<List<CtClass>> suiteClassList, List<List<String>> methodNameList) {
        Class<?> clazz = null;
        for (List<CtClass> classList : suiteClassList) {
            for (int cListIndex = 0; cListIndex < classList.size(); ++cListIndex) {
                try {
                    clazz = classList.get(cListIndex).toClass();
                    this.instanceClass(clazz, methodNameList.get(cListIndex));
                } catch (CannotCompileException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void instanceClass(Class<?> clazz, List<String> methodNameList) {
        Object obj = null;
        try {
            obj = clazz.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
        for (String methodName: methodNameList) {
            this.methodPerform(obj, methodName);
        }
    }

    private void methodPerform(Object obj, String methodName) {
        Method method = null;
        try {
            method = obj.getClass().getMethod(methodName);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        try {
            if (method != null) {
                method.invoke(obj);
            }
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    // 测试--main方法
//    public static void main(String[] args) throws Exception{
//        ClassGenerator cg = new ClassGenerator();
//        cg.createTestClass();
//        cg.insertField();
//        cg.suiteInsertMethod();
//
//        List<List<String>> methodList = cg.getAllClassMethodList();
//        List<List<CtClass>> classList = cg.getAllClassList();
//
//        ClassRunner cr = new ClassRunner();
//        cr.reflectRunClass(classList, methodList);
//    }
}

package com.example.elasticsearch.config;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class EmptyTool {
    public static <E> boolean containsEmptyField(Class<E> clz, E source) {
        int limitCount = 0;
        int emptyResult = 0;
        Field[] declaredFields = clz.getDeclaredFields();
        for (Field field : declaredFields) {
            EmptyMark annotation = field.getAnnotation(EmptyMark.class);
            if (annotation != null) {
                limitCount++;
                try {
                    Method method = source.getClass().getMethod("get" + upperCaseFirst(field.getName()));
                    Object invoke = method.invoke(source);
                    if (invoke == null) {
                        continue;
                    }
                    if (invoke instanceof Double) {
                        emptyResult = (Double) invoke == 0d ? emptyResult + 1 : emptyResult;
                    }
                    if (invoke instanceof Float) {
                        emptyResult = (Float) invoke == 0f ? emptyResult + 1 : emptyResult;
                    }
                    if (invoke instanceof String) {
                        emptyResult = "".equals(invoke) ? emptyResult + 1 : emptyResult;
                    }
                } catch (Exception exception) {
                    System.out.println("exception");
                }
            }
        }
        return limitCount == emptyResult;
    }

    private static String upperCaseFirst(String val) {
        char[] arr = val.toCharArray();
        arr[0] = Character.toUpperCase(arr[0]);
        return new String(arr);
    }
}

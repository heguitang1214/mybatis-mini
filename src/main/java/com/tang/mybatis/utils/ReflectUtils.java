package com.tang.mybatis.utils;

public class ReflectUtils {
    public static Class<?> resolveType(String resultType) {
        try {
            return Class.forName(resultType);
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
}

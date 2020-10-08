package com.tang.mybatis.handler;


import com.tang.mybatis.config.MappedStatement;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.List;

public class DefaultResultSetHandler implements ResultSetHandler {
    @Override
    public List<Object> handleResultSet(PreparedStatement preparedStatement, ResultSet rs, MappedStatement mappedStatement) throws Exception {
        // 遍历查询结果集
        Class clazz = mappedStatement.getResultTypeClass();

        // 一般都是通过构造器去创建对象
        Constructor<?> constructor = clazz.getDeclaredConstructor();

        List<Object> results = new ArrayList<>();
        Object result;
        while (rs.next()) {
            result = constructor.newInstance();

            // 获取结果集中列的信息
            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();
            for (int i = 0; i < columnCount; i++) {
                String columnName = metaData.getColumnName(i + 1);

                // 通过反射给指点列对应的属性名称赋值
                // 列名和属性名要一致
                Field field = clazz.getDeclaredField(columnName);
                // 暴力破解，破坏封装，可以访问私有成员
                field.setAccessible(true);
                field.set(result, rs.getObject(columnName));
            }
            results.add(result);
        }

        return results;
    }
}

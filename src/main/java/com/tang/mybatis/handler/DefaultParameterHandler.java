package com.tang.mybatis.handler;


import com.tang.mybatis.config.BoundSql;
import com.tang.mybatis.config.ParameterMapping;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.Map;

public class DefaultParameterHandler implements ParameterHandler {
    @Override
    public void setParameters(Object param, PreparedStatement preparedStatement, BoundSql boundSql) throws Exception {
        // 设置参数，第一个参数为 sql 语句中参数的序号（从 1 开始），第二个参数为设置的
        // preparedStatement.setObject(1, param);
        // 如果入参是简单类型，那么我们不关心参数名称
        if (param instanceof Integer || param instanceof String) {
            preparedStatement.setObject(1, param);
        } else if (param instanceof Map) {
            Map<String, Object> map = (Map<String, Object>) param;

            // TODO 需要解析#{}之后封装的参数集合List<ParameterMapping>
            List<ParameterMapping> parameterMappings = boundSql.getParameterMappings();
            for (int i = 0; i < parameterMappings.size(); i++) {
                ParameterMapping parameterMapping = parameterMappings.get(i);
                String name = parameterMapping.getName();
                Object value = map.get(name);
                // 给map集合中的参数赋值
                preparedStatement.setObject(i + 1, value);
            }

            // map集合中的key和要映射的参数名称要一致
        }
    }
}

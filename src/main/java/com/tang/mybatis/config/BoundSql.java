package com.tang.mybatis.config;

import java.util.ArrayList;
import java.util.List;

/**
 * 封装SQL语句和#{}解析出来的参数信息集合
 */
public class BoundSql {

    /**
     * 已经解析完成的SQL语句
     */
    private String sql;

    private List<ParameterMapping> parameterMappings = new ArrayList<>();

    public BoundSql(String sql, List<ParameterMapping> parameterMappings) {
        this.sql = sql;
        this.parameterMappings = parameterMappings;
    }

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    public List<ParameterMapping> getParameterMappings() {
        return parameterMappings;
    }

    public void addParameterMapping(ParameterMapping parameterMapping) {
        this.parameterMappings.add(parameterMapping);
    }
}

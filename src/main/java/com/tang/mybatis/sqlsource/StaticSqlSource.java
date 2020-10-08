package com.tang.mybatis.sqlsource;



import com.tang.mybatis.config.BoundSql;
import com.tang.mybatis.config.ParameterMapping;

import java.util.List;

/**
 * 静态的SqlSource
 */
public class StaticSqlSource implements SqlSource{

    private String sql;

    private List<ParameterMapping> parameterMappings;

    public StaticSqlSource(String sql, List<ParameterMapping> parameterMappings) {
        this.sql = sql;
        this.parameterMappings = parameterMappings;
    }

    @Override
    public BoundSql getBoundSql(Object param) {
        return new BoundSql(sql,parameterMappings);
    }
}

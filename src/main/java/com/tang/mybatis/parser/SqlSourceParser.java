package com.tang.mybatis.parser;


import com.tang.mybatis.sqlsource.SqlSource;
import com.tang.mybatis.sqlsource.StaticSqlSource;
import com.tang.mybatis.utils.GenericTokenParser;
import com.tang.mybatis.utils.ParameterMappingTokenHandler;

/**
 * 用来处理#{}之后，获取StaticSqlSource
 */
public class SqlSourceParser {

    public SqlSource parse(String sqlText) {
        ParameterMappingTokenHandler tokenHandler = new ParameterMappingTokenHandler();
        GenericTokenParser tokenParser = new GenericTokenParser("#{", "}", tokenHandler);
        String sql = tokenParser.parse(sqlText);

        return new StaticSqlSource(sql, tokenHandler.getParameterMappings());
    }
}

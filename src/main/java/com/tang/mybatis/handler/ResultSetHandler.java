package com.tang.mybatis.handler;


import com.tang.mybatis.config.MappedStatement;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;

/**
 * 专门处理结果集的
 */
public interface ResultSetHandler {

    List<Object> handleResultSet(PreparedStatement preparedStatement, ResultSet rs, MappedStatement mappedStatement) throws Exception;
}

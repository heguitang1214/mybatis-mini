package com.tang.mybatis.handler;


import com.tang.mybatis.config.BoundSql;

import java.sql.PreparedStatement;

/**
 * 专门用来处理JDBC执行过程中参数设置的
 */
public interface ParameterHandler {

    void setParameters(Object param, PreparedStatement preparedStatement, BoundSql boundSql) throws Exception;
}

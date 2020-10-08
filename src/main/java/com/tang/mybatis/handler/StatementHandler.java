package com.tang.mybatis.handler;


import com.tang.mybatis.config.BoundSql;
import com.tang.mybatis.config.MappedStatement;

import java.sql.Connection;
import java.sql.Statement;
import java.util.List;

public interface StatementHandler {

    /**
     * 创建Statement
     *
     * @param sql        sql语句
     * @param connection 连接
     * @return Statement
     * @throws Exception 异常
     */
    Statement prepare(String sql, Connection connection) throws Exception;

    /**
     * 参数处理
     *
     * @param param     请求参数
     * @param statement statement
     * @param boundSql  boundSql
     * @throws Exception 异常信息
     */
    void parameterize(Object param, Statement statement, BoundSql boundSql) throws Exception;

    /**
     * 结果查询
     *
     * @param statement       statement
     * @param mappedStatement mappedStatement
     * @return 结果集
     * @throws Exception 异常信息
     */
    List<Object> query(Statement statement, MappedStatement mappedStatement) throws Exception;
}

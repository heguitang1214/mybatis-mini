package com.tang.mybatis.handler;


import com.tang.mybatis.config.BoundSql;
import com.tang.mybatis.config.MappedStatement;

import java.sql.Connection;
import java.sql.Statement;
import java.util.List;

/**
 * 专门处理PreparedStatement对象的
 */
public class SimpleStatementHandler implements StatementHandler {
    @Override
    public Statement prepare(String sql, Connection connection) {
        return null;
    }

    @Override
    public void parameterize(Object param, Statement statement, BoundSql boundSql) {

    }

    @Override
    public List<Object> query(Statement statement, MappedStatement mappedStatement) {
        return null;
    }
}

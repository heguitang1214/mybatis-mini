package com.tang.mybatis.handler;


import com.tang.mybatis.common.Constant;
import com.tang.mybatis.config.BoundSql;
import com.tang.mybatis.config.Configuration;
import com.tang.mybatis.config.MappedStatement;

import java.sql.Connection;
import java.sql.Statement;
import java.util.List;

public class RoutingStatementHandler implements StatementHandler {
    private StatementHandler statementHandler;

    public RoutingStatementHandler(String statementType, Configuration configuration) {
        if (Constant.STATEMENT_TYPE_PREPARED.equals(statementType)) {
            statementHandler = new PreparedStatementHandler(configuration);
        }
    }

    @Override
    public Statement prepare(String sql, Connection connection) throws Exception {
        return statementHandler.prepare(sql, connection);
    }

    @Override
    public void parameterize(Object param, Statement statement, BoundSql boundSql) throws Exception {
        statementHandler.parameterize(param, statement, boundSql);
    }

    @Override
    public List<Object> query(Statement statement, MappedStatement mappedStatement) throws Exception {
        return statementHandler.query(statement, mappedStatement);
    }
}

package com.tang.mybatis.executor;

import com.tang.mybatis.config.BoundSql;
import com.tang.mybatis.config.Configuration;
import com.tang.mybatis.config.MappedStatement;
import com.tang.mybatis.handler.StatementHandler;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * 处理普通的JDBC执行操作，默认也是这种执行器去处理
 */
public class SimpleExecutor extends BaseExecutor {
    @Override
    protected List<Object> queryFromDataBase(Configuration configuration, MappedStatement mappedStatement, BoundSql boundSql, Object param) {
        List<Object> results = new ArrayList<>();

        Connection connection = null;
        Statement statement = null;
        ResultSet rs = null;
        try {
            // 连接的获取
            connection = getConnection(configuration);
            // SQL的获取(SqlSource和SqlNode的处理流程)
            // 触发SqlSource和SqlNode的解析处理流程
            String sql = boundSql.getSql();
            // 创建statement
            // StatementHandler用于处理Statement操作（statement、prepared、callable）
            // 通过StatementHandler去屏蔽不同Statement的处理逻辑
            StatementHandler statementHandler = configuration.newStatementHandler(mappedStatement.getStatementType());
            statement = statementHandler.prepare(sql, connection);
            // 设置参数（使用的是BoundSql中的参数集合）
            statementHandler.parameterize(param, statement, boundSql);
            // 执行statement
            results = statementHandler.query(statement, mappedStatement);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // 释放资源
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }

        return results;
    }

    private Connection getConnection(Configuration configuration) throws Exception {
        DataSource dataSource = configuration.getDataSource();
        return dataSource.getConnection();
    }
}

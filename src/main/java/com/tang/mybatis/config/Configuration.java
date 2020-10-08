package com.tang.mybatis.config;


import com.tang.mybatis.common.Constant;
import com.tang.mybatis.executor.CachingExecutor;
import com.tang.mybatis.executor.Executor;
import com.tang.mybatis.executor.SimpleExecutor;
import com.tang.mybatis.handler.*;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

/**
 * 封装了mybatis中xml文件的所有配置信息
 */
public class Configuration {
    private DataSource dataSource;

    private final boolean useCache = true;

    private Map<String, MappedStatement> mappedStatements = new HashMap<>();

    public DataSource getDataSource() {
        return dataSource;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public MappedStatement getMappedStatementById(String statementId) {
        return mappedStatements.get(statementId);
    }

    public void addMappedStatement(String statementId, MappedStatement mappedStatement) {
        this.mappedStatements.put(statementId, mappedStatement);
    }

    public Executor newExecutor(String executorType) {
        // 如果没有传参数，就选择默认的
        executorType = executorType == null || executorType.equals("") ? Constant.SIMPLE_EXECUTOR_TYPE : executorType;

        Executor executor = null;
        if (executorType.equals(Constant.SIMPLE_EXECUTOR_TYPE)) {
            executor = new SimpleExecutor();
        }

        // 针对真正的执行器，进行二级缓存保证
        if (useCache) {
            executor = new CachingExecutor(executor);
        }
        return executor;
    }

    public StatementHandler newStatementHandler(String statementType) {
        statementType = statementType == null || statementType.equals("") ? Constant.STATEMENT_TYPE_PREPARED : statementType;
        // RoutingStatementHandler
        return new RoutingStatementHandler(statementType, this);
    }

    public ParameterHandler newParameterHandler() {
        return new DefaultParameterHandler();
    }

    public ResultSetHandler newResultSetHandler() {
        return new DefaultResultSetHandler();
    }
}

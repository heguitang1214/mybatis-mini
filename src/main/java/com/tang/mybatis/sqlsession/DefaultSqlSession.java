package com.tang.mybatis.sqlsession;


import com.tang.mybatis.config.Configuration;
import com.tang.mybatis.config.MappedStatement;
import com.tang.mybatis.executor.Executor;

import java.util.List;
import java.util.Map;

public class DefaultSqlSession implements SqlSession {

    private Configuration configuration;

    public DefaultSqlSession(Configuration configuration) {
        this.configuration = configuration;
    }

    @Override
    public <T> List<T> selectList(String statementId, Map param) {
        MappedStatement mappedStatement = configuration.getMappedStatementById(statementId);

        // Executor有多种类型，比如SimpleExecutor、ReuseExecutor、BatchExecutor
        // 可以通过全局配置文件的settings去指定（该信息存储到Configuration对象中）
        // 可以通过创建SqlSession时指定executorType为以上三种类型之一
        Executor executor = configuration.newExecutor(null);

        // 通过Executor去处理JDBC操作
        return executor.query(configuration, mappedStatement, param);
    }

    @Override
    public <T> T selectOne(String statementId, Map param) {
        List<Object> list = this.selectList(statementId, param);
        if (list != null && list.size() == 1) {
            return (T) list.get(0);
        }
        return null;
    }
}

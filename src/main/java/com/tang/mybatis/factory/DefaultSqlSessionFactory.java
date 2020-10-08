package com.tang.mybatis.factory;


import com.tang.mybatis.config.Configuration;
import com.tang.mybatis.sqlsession.DefaultSqlSession;
import com.tang.mybatis.sqlsession.SqlSession;

public class DefaultSqlSessionFactory implements SqlSessionFactory {

    private Configuration configuration;

    public DefaultSqlSessionFactory(Configuration configuration) {
        this.configuration = configuration;
    }

    @Override
    public SqlSession openSession() {
        return new DefaultSqlSession(configuration);
    }
}

package com.tang.mybatis.executor;


import com.tang.mybatis.config.BoundSql;
import com.tang.mybatis.config.Configuration;
import com.tang.mybatis.config.MappedStatement;

import java.util.List;

public class BatchExecutor extends BaseExecutor {
    @Override
    protected List<Object> queryFromDataBase(Configuration configuration, MappedStatement mappedStatement, BoundSql boundSql, Object param) {
        return null;
    }
}

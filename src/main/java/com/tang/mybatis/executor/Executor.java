package com.tang.mybatis.executor;


import com.tang.mybatis.config.Configuration;
import com.tang.mybatis.config.MappedStatement;

import java.util.List;

/**
 * 用来执行JDBC逻辑
 */
public interface Executor {
    <T> List<T> query(Configuration configuration, MappedStatement mappedStatement, Object param);
}

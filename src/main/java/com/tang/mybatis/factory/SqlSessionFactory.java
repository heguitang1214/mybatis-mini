package com.tang.mybatis.factory;


import com.tang.mybatis.sqlsession.SqlSession;

public interface SqlSessionFactory {
    SqlSession openSession();
}

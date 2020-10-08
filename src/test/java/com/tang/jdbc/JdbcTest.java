package com.tang.jdbc;

import com.tang.mybatis.test.JdbcDemo;
import org.junit.Test;

/**
 * 测试使用JDBC连接数据库
 */
public class JdbcTest {

    @Test
    public void test() {
        JdbcDemo demo = new JdbcDemo();
        demo.jdbcTest();
    }

}

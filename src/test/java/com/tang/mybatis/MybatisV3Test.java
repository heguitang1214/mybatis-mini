package com.tang.mybatis;

import com.tang.mybatis.builder.SqlSessionFactoryBuilder;
import com.tang.mybatis.entity.User;
import com.tang.mybatis.factory.SqlSessionFactory;
import com.tang.mybatis.io.Resources;
import com.tang.mybatis.sqlsession.SqlSession;
import com.tang.mybatis.test.MybatisV2;
import org.junit.Before;
import org.junit.Test;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MybatisV3Test {

    private SqlSessionFactory sqlSessionFactory;

    @Before
    public void before() {
        // 全局配置文件的路径
        String location = "mybatis-config.xml";
        InputStream inputStream = Resources.getResourceAsStream(location);
        // 创建SqlSessionFactory
        sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
    }

    @Test
    public void test() {
        // 规定selectUserList方法的参数只有 两个。
        Map<String, String> param = new HashMap<>();
        param.put("username", "恶鬼缠身");
        param.put("sex", "男");

        // 调用公共方法，查询用户信息
        SqlSession sqlSession = sqlSessionFactory.openSession();
        List<User> users = sqlSession.selectList("test.queryUserByParams", param);
        System.out.println(users);
    }


}

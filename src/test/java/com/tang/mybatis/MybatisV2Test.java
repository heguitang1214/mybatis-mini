package com.tang.mybatis;

import com.tang.mybatis.entity.User;
import com.tang.mybatis.test.MybatisV2;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MybatisV2Test {

    @Test
    public void test() {
        MybatisV2 mybatisV2 = new MybatisV2();

        // 加载XML文件（全局配置文件和映射文件）
        mybatisV2.loadXml("mybatis-config.xml");

        // 执行查询
        Map<String, Object> param = new HashMap<>();
        param.put("username", "恶鬼缠身");
        param.put("sex", "男");
        List<User> users = mybatisV2.selectList("test.queryUserByParams", param);
        System.out.println(users);
    }


}

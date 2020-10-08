package com.tang.mybatis;

import com.tang.mybatis.entity.User;
import com.tang.mybatis.test.MybatisV1;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MybatisV1Test {

    @Test
    public void test() {
        MybatisV1 mybatisV1 = new MybatisV1();

        // 加载properties文件中的内容
        mybatisV1.loadProperties("jdbc.properties");

        List<User> users = mybatisV1.selectList("queryUserById", 1);
        print(users);

        List<User> users1 = mybatisV1.selectList("queryUserByName", "一刀修罗");
        print(users1);


        Map<String, Object> param = new HashMap<>(16);
        param.put("username", "恶鬼缠身");
        param.put("sex", "男");
        List<User> users2 = mybatisV1.selectList("queryUserByParams", param);
        print(users2);

    }

    private void print(List<User> users) {
        System.out.println("========================start=======================");
        for (User user : users) {
            System.out.println(user.toString());
        }
        System.out.println("========================end=======================");
    }

}

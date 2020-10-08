package com.tang.mybatis.test;

import org.apache.commons.dbcp.BasicDataSource;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;


/**
 * 对JDBC的初步封装
 * 解决硬编码问题（properties文件），properties文件中的内容，最终会被【加载】到Properties集合中
 */
public class MybatisV1 {

    /**
     * 抽取的配置文件
     */
    private final Properties properties = new Properties();

    /**
     * 加载jdbc配置文件
     *
     * @param location 资源路径
     */
    public void loadProperties(String location) {
        InputStream inputStream = null;
        try {
            inputStream = this.getClass().getClassLoader().getResourceAsStream(location);
            properties.load(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    /**
     * 抽取一个通用的查询方法
     *
     * @param statementId 查询的SQL，从配置文件中读取
     * @param param       SQL查询的参数
     * @param <T>         返回的实体泛型
     * @return 集合
     */
    public <T> List<T> selectList(String statementId, Object param) {

        List<T> results = new ArrayList<>();

        Connection connection;
        PreparedStatement preparedStatement = null;
        ResultSet rs = null;

        try {
            // 加载数据库驱动
            // 解决了jdbc连接获取时的硬编码问题和频繁连接的问题
            BasicDataSource dataSource = new BasicDataSource();
            dataSource.setDriverClassName(properties.getProperty("db.driver"));
            dataSource.setUrl(properties.getProperty("db.url"));
            dataSource.setUsername(properties.getProperty("db.username"));
            dataSource.setPassword(properties.getProperty("db.password"));

            connection = dataSource.getConnection();

            // 定义sql语句 ?表示占位符
            String sql = properties.getProperty("db.sql." + statementId);

            // 获取预处理 statement
            preparedStatement = connection.prepareStatement(sql);

            // 设置参数，第一个参数为 sql 语句中参数的序号（从 1 开始），第二个参数为设置的
            // preparedStatement.setObject(1, param);
            // 如果入参是简单类型，那么我们不关心参数名称
            if (param instanceof Integer) {
                preparedStatement.setInt(1, (Integer) param);
            } else if (param instanceof String) {
                preparedStatement.setString(1, param.toString());
            } else if (param instanceof Map) {
                Map<String, Object> map = (Map<String, Object>) param;

                String columnnames = properties.getProperty("db.sql." + statementId + ".columnnames");
                String[] nameArray = columnnames.split(",");
                if (nameArray.length > 0) {
                    for (int i = 0; i < nameArray.length; i++) {
                        String name = nameArray[i];
                        Object value = map.get(name);
                        // 给map集合中的参数赋值
                        preparedStatement.setObject(i + 1, value);
                    }
                }
                // map集合中的key和要映射的参数名称要一致
            } else {
                // 其他具体的类型的处理
                preparedStatement.setObject(1, param);
            }

            // 向数据库发出 sql 执行查询，查询出结果集
            rs = preparedStatement.executeQuery();

            // 遍历查询结果集
            String resultType = properties.getProperty("db.sql." + statementId + ".resulttype");
            Class<?> clazz = Class.forName(resultType);

            // 一般都是通过构造器去创建对象，无参、有参构造器
            Constructor<?> constructor = clazz.getDeclaredConstructor();
            Object result;
            while (rs.next()) {
                result = constructor.newInstance();

                // 获取结果集中列的信息
                ResultSetMetaData metaData = rs.getMetaData();
                int columnCount = metaData.getColumnCount();
                for (int i = 0; i < columnCount; i++) {
                    String columnName = metaData.getColumnName(i + 1);

                    // 通过反射给指点列对应的属性名称赋值
                    // 列名和属性名要一致
                    Field field = clazz.getDeclaredField(columnName);
                    // 暴力破解，破坏封装，可以访问私有成员
                    field.setAccessible(true);
                    field.set(result, rs.getObject(columnName));
                }
                results.add((T) result);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // 释放资源
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (preparedStatement != null) {
                try {
                    preparedStatement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return results;
    }
}

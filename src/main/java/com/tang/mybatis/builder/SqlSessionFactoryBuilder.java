package com.tang.mybatis.builder;

import com.tang.mybatis.config.Configuration;
import com.tang.mybatis.factory.DefaultSqlSessionFactory;
import com.tang.mybatis.factory.SqlSessionFactory;
import com.tang.mybatis.utils.DocumentUtils;
import org.dom4j.Document;

import java.io.InputStream;
import java.io.Reader;

public class SqlSessionFactoryBuilder {
    public SqlSessionFactory build(InputStream inputStream) {
        // 获取Configuration对象（XMLConfigBuilder）
        Document document = DocumentUtils.getDocument(inputStream);

        XMLConfigBuilder configBuilder = new XMLConfigBuilder();
        Configuration configuration = configBuilder.parseConfiguration(document.getRootElement());
        // 创建SqlSessionFactory对象
        return build(configuration);
    }

    public SqlSessionFactory build(Reader reader) {
        return null;
    }

    private SqlSessionFactory build(Configuration configuration) {
        return new DefaultSqlSessionFactory(configuration);
    }
}

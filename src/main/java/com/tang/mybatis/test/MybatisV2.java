package com.tang.mybatis.test;

import com.tang.mybatis.common.Constant;
import com.tang.mybatis.config.BoundSql;
import com.tang.mybatis.config.Configuration;
import com.tang.mybatis.config.MappedStatement;
import com.tang.mybatis.config.ParameterMapping;
import com.tang.mybatis.sqlnode.*;
import com.tang.mybatis.sqlsource.DynamicSqlSource;
import com.tang.mybatis.sqlsource.RawSqlSource;
import com.tang.mybatis.sqlsource.SqlSource;
import org.apache.commons.dbcp.BasicDataSource;
import org.dom4j.*;
import org.dom4j.io.SAXReader;

import javax.sql.DataSource;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.sql.*;
import java.util.*;

/**
 * 对mybatisV1的再次封装
 * 1.properties配置文件升级为XML配置文件
 * 2.使用面向过程思维去优化代码
 * 3.使用面向对象思维去理解配置文件封装的类的作用
 */
public class MybatisV2 {

    /**
     * mybatis中xml文件的所有配置信息
     */
    private Configuration configuration;

    /**
     * 命名空间
     */
    private String namespace;

    private boolean isDynamic = false;


    public <T> List<T> selectList(String statementId, Object param) {
        List<T> results = new ArrayList<>();

        Connection connection;
        Statement statement = null;
        ResultSet rs = null;
        try {
            // 获取statement相关的信息MappedStatement
            MappedStatement mappedStatement = configuration.getMappedStatementById(statementId);
            // 连接的获取
            connection = getConnection();
            // SQL的获取(SqlSource和SqlNode的处理流程)
            SqlSource sqlSource = mappedStatement.getSqlSource();
            // 触发SqlSource和SqlNode的解析处理流程
            BoundSql boundSql = sqlSource.getBoundSql(param);
            String sql = boundSql.getSql();
            // 创建statement
            statement = createStatement(mappedStatement, sql, connection);
            // 设置参数
            setParameters(param, statement, boundSql);
            // 执行statement
            rs = executeQuery(statement);

            // 处理结果
            handleResult(rs, mappedStatement, results);
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
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }

        return results;
    }
    //================================xml解析开始=====================

    /**
     * 解析XML文件，最终将信息封装到Configuration对象中
     *
     * @param location 文件路径
     */
    public void loadXml(String location) {
        configuration = new Configuration();
        // 获取全局配置文件对应的流对象
        InputStream is = getResourceAsStream(location);
        // 获取Document对象
        Document document = getDocument(is);
        // 根据xml语义进行解析
        parseConfiguration(document.getRootElement());
    }

    private InputStream getResourceAsStream(String location) {
        return this.getClass().getClassLoader().getResourceAsStream(location);
    }

    private Document getDocument(InputStream is) {
        SAXReader reader = new SAXReader();
        try {
            return reader.read(is);
        } catch (DocumentException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 解析Mybatis配置文件
     *
     * @param rootElement 根元素
     */
    private void parseConfiguration(Element rootElement) {
        // 解析mybatis-config.xml 下 <configuration/>标签下<environments/>标签的信息
        Element environments = rootElement.element(Constant.ROOT_ELEMENT);
        parseEnvironments(environments);

        // 解析mybatis-config.xml 下 <configuration/>标签下<mappers/>标签的信息
        Element mappers = rootElement.element(Constant.MAPPERS);
        parseMappers(mappers);
    }


    private void parseEnvironments(Element environments) {
        String aDefault = environments.attributeValue(Constant.DEF);
        List<Element> elements = environments.elements(Constant.ENVIRONMENT);
        for (Element element : elements) {
            String id = element.attributeValue(Constant.ID);
            if (aDefault.equals(id)) {
                parseDataSource(element.element(Constant.DATA_SOURCE));
            }
        }
    }


    private void parseDataSource(Element dataSource) {
        String type = dataSource.attributeValue(Constant.TYPE);
        if (type.equals(Constant.DATA_SOURCE_TYPE)) {
            BasicDataSource ds = new BasicDataSource();
            Properties properties = parseProperties(dataSource);
            ds.setDriverClassName(properties.getProperty(Constant.DB_DRIVER));
            ds.setUrl(properties.getProperty(Constant.DB_URL));
            ds.setUsername(properties.getProperty(Constant.DB_USERNAME));
            ds.setPassword(properties.getProperty(Constant.DB_PASSWORD));

            configuration.setDataSource(ds);
        }
    }

    private Properties parseProperties(Element dataSource) {
        Properties properties = new Properties();
        List<Element> list = dataSource.elements(Constant.PROPERTY);
        for (Element element : list) {
            String name = element.attributeValue(Constant.NAME);
            String value = element.attributeValue(Constant.VALUE);
            properties.put(name, value);
        }
        return properties;
    }


    /**
     * 解析全局配置文件中的mappers标签
     *
     * @param mappers <mappers></mappers>
     */
    private void parseMappers(Element mappers) {
        List<Element> list = mappers.elements(Constant.MAPPER);
        for (Element element : list) {
            String resource = element.attributeValue(Constant.RESOURCE);
            // 根据xml的路径，获取对应的输入流
            InputStream inputStream = getResourceAsStream(resource);
            // 将流对象，转换成Document对象
            Document document = getDocument(inputStream);
            // 针对Document对象，按照Mybatis的语义去解析Document
            parseMapper(document.getRootElement());
        }
    }


    /**
     * 解析映射文件的mapper信息
     *
     * @param rootElement <mapper></mapper>
     */
    private void parseMapper(Element rootElement) {
        // statementid是由namespace+statement标签的id值组成的。
        namespace = rootElement.attributeValue(Constant.NAMESPACE);
        //  获取动态SQL标签，比如<sql>
        //  获取其他标签
        List<Element> selectElements = rootElement.elements(Constant.SELECT);
        for (Element selectElement : selectElements) {
            parseStatementElement(selectElement);
        }
    }

    /**
     * 解析映射文件中的select标签
     *
     * @param selectElement <select></select>
     */
    private void parseStatementElement(Element selectElement) {
        String statementId = selectElement.attributeValue(Constant.ID);

        if (statementId == null || "".equals(statementId)) {
            return;
        }
        // 一个CURD标签对应一个MappedStatement对象
        // 一个MappedStatement对象由一个statementId来标识，所以保证唯一性
        // statementId = namespace + "." + CRUD标签的id属性
        statementId = namespace + "." + statementId;

        // 注意：parameterType参数可以不设置也可以不解析
        String resultType = selectElement.attributeValue(Constant.RESULT_TYPE);
        Class<?> resultClass = resolveType(resultType);

        String statementType = selectElement.attributeValue(Constant.STATEMENT_TYPE);
        statementType = statementType == null || "".equals(statementType) ? Constant.STATEMENT_TYPE_PREPARED : statementType;

        // SqlSource和SqlNode的封装过程
        SqlSource sqlSource = createSqlSource(selectElement);

        // 建议使用构建者模式去优化
        MappedStatement mappedStatement = new MappedStatement(statementId, resultClass, statementType,
                sqlSource);
        configuration.addMappedStatement(statementId, mappedStatement);
    }


    /**
     * 根据全限定名获取Class对象
     *
     * @param parameterType 参数类型
     * @return 对象类型
     */
    private Class<?> resolveType(String parameterType) {
        try {
            return Class.forName(parameterType);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }


    //================================xml解析结束=====================

    /**
     * 创建一个SqlSource，根据<select></select>标签中的内容
     *
     * @param selectElement 查询元素
     * @return SqlSource
     */
    private SqlSource createSqlSource(Element selectElement) {
        // 其他子标签的解析处理
        return parseScriptNode(selectElement);
    }

    /**
     * 解析SQL脚本
     *
     * @param selectElement 查询元素
     * @return SqlSource
     */
    private SqlSource parseScriptNode(Element selectElement) {
        //解析所有SQL节点，最终封装到MixedSqlNode中
        SqlNode mixedSqlNode = parseDynamicTags(selectElement);

        SqlSource sqlSource;
        //如果带有${}或者动态SQL标签
        if (isDynamic) {
            sqlSource = new DynamicSqlSource(mixedSqlNode);
        } else {
            sqlSource = new RawSqlSource(mixedSqlNode);
        }
        return sqlSource;
    }


    private SqlNode parseDynamicTags(Element selectElement) {
        List<SqlNode> sqlNodes = new ArrayList<>();

        //获取select标签的子元素 ：文本类型或者Element类型
        int nodeCount = selectElement.nodeCount();
        for (int i = 0; i < nodeCount; i++) {
            Node node = selectElement.node(i);
            if (node instanceof Text) {
                String text = node.getText();
                if (text == null) {
                    continue;
                }
                if ("".equals(text.trim())) {
                    continue;
                }
                // 先将sql文本封装到TextSqlNode中
                TextSqlNode textSqlNode = new TextSqlNode(text.trim());
                if (textSqlNode.isDynamic()) {
                    sqlNodes.add(textSqlNode);
                    isDynamic = true;
                } else {
                    sqlNodes.add(new StaticTextSqlNode(text.trim()));
                }

            } else if (node instanceof Element) {
                isDynamic = true;
                Element element = (Element) node;
                String name = element.getName();

                if (Constant.IF_LABEL.equals(name)) {
                    String test = element.attributeValue(Constant.TEST);
                    //递归去解析子元素
                    SqlNode sqlNode = parseDynamicTags(element);

                    IfSqlNode ifSqlNode = new IfSqlNode(test, sqlNode);
                    sqlNodes.add(ifSqlNode);
                } else {
                    // TODO
                }
            } else {
                //TODO
            }
        }
        return new MixedSqlNode(sqlNodes);
    }


    private Connection getConnection() throws Exception {
        DataSource dataSource = configuration.getDataSource();
        return dataSource.getConnection();
    }


    private Statement createStatement(MappedStatement mappedStatement, String sql, Connection connection) throws Exception {
        String statementType = mappedStatement.getStatementType();
        if (Constant.STATEMENT_TYPE_PREPARED.equals(statementType)) {
            return connection.prepareStatement(sql);
        } else {
            // TODO 其他类型的处理
        }
        return null;
    }


    private <T> void handleResult(ResultSet rs, MappedStatement mappedStatement, List<T> results) throws Exception {
        // 遍历查询结果集
        Class clazz = mappedStatement.getResultTypeClass();

        // 一般都是通过构造器去创建对象
        Constructor<?> constructor = clazz.getDeclaredConstructor();

        Object result = null;
        while (rs.next()) {
            // result = clazz.newInstance();

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
    }

    private ResultSet executeQuery(Statement statement) throws Exception {
        ResultSet rs = null;
        if (statement instanceof PreparedStatement) {
            PreparedStatement preparedStatement = (PreparedStatement) statement;
            // 向数据库发出 sql 执行查询，查询出结果集
            rs = preparedStatement.executeQuery();
        }

        return rs;
    }


    private void setParameters(Object param, Statement statement, BoundSql boundSql) throws Exception {
        if (statement instanceof PreparedStatement) {
            PreparedStatement preparedStatement = (PreparedStatement) statement;

            // 设置参数，第一个参数为 sql 语句中参数的序号（从 1 开始），第二个参数为设置的
            // preparedStatement.setObject(1, param);
            // 如果入参是简单类型，那么我们不关心参数名称
            if (param instanceof Integer || param instanceof String) {
                preparedStatement.setObject(1, param);
            } else if (param instanceof Map) {
                Map<String, Object> map = (Map<String, Object>) param;

                // TODO 需要解析#{}之后封装的参数集合List<ParameterMapping>
                List<ParameterMapping> parameterMappings = boundSql.getParameterMappings();
                for (int i = 0; i < parameterMappings.size(); i++) {
                    ParameterMapping parameterMapping = parameterMappings.get(i);
                    String name = parameterMapping.getName();
                    Object value = map.get(name);
                    // 给map集合中的参数赋值
                    preparedStatement.setObject(i + 1, value);
                }

                // map集合中的key和要映射的参数名称要一致
            } else {
                //TODO
            }
        }
    }

}

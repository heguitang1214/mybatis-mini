package com.tang.mybatis.builder;

import com.tang.mybatis.common.Constant;
import com.tang.mybatis.config.Configuration;
import com.tang.mybatis.io.Resources;
import com.tang.mybatis.utils.DocumentUtils;
import org.apache.commons.dbcp.BasicDataSource;
import org.dom4j.Document;
import org.dom4j.Element;

import java.io.InputStream;
import java.util.List;
import java.util.Properties;

/**
 * 该类就是用来解析全局配置文件的
 */
public class XMLConfigBuilder {
    private Configuration configuration;

    public XMLConfigBuilder() {
        configuration = new Configuration();
    }

    public Configuration parseConfiguration(Element rootElement) {
        Element environments = rootElement.element(Constant.ROOT_ELEMENT);
        parseEnvironments(environments);
        Element mappers = rootElement.element(Constant.MAPPERS);
        parseMappers(mappers);

        return configuration;
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
            InputStream inputStream = Resources.getResourceAsStream(resource);
            // 将流对象，转换成Document对象
            Document document = DocumentUtils.getDocument(inputStream);
            // 针对Document对象，按照Mybatis的语义去解析Document
            XMLMapperBuilder mapperBuilder = new XMLMapperBuilder(configuration);
            mapperBuilder.parseMapper(document.getRootElement());
        }
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
}

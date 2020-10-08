package com.tang.mybatis.builder;

import com.tang.mybatis.common.Constant;
import com.tang.mybatis.config.Configuration;
import com.tang.mybatis.config.MappedStatement;
import com.tang.mybatis.sqlsource.SqlSource;
import com.tang.mybatis.utils.ReflectUtils;
import org.dom4j.Element;

/**
 * 专门解析select/insert等statement标签
 */
public class XMLStatementBuilder {

    private Configuration configuration;

    public XMLStatementBuilder(Configuration configuration) {
        this.configuration = configuration;
    }

    public void parseStatementElement(Element selectElement, String namespace) {
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
        Class<?> resultClass = ReflectUtils.resolveType(resultType);

        String statementType = selectElement.attributeValue(Constant.STATEMENT_TYPE);
        statementType = statementType == null || "".equals(statementType) ? Constant.STATEMENT_TYPE_PREPARED : statementType;

        // SqlSource和SqlNode的封装过程
        SqlSource sqlSource = createSqlSource(selectElement);

        // 建议使用构建者模式去优化
        MappedStatement mappedStatement = new MappedStatement(statementId, resultClass, statementType,
                sqlSource);
        configuration.addMappedStatement(statementId, mappedStatement);
    }

    private SqlSource createSqlSource(Element selectElement) {
        XMLScriptBuilder scriptBuilder = new XMLScriptBuilder();

        return scriptBuilder.parseScriptNode(selectElement);
    }

}

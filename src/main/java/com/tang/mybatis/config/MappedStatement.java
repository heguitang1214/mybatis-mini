package com.tang.mybatis.config;


import com.tang.mybatis.sqlsource.SqlSource;

/**
 * 封装了statement标签中的信息
 */
public class MappedStatement {

    private String statementId;

    private String statementType;

    private String resultType;

    private Class resultTypeClass;

    private SqlSource sqlSource;

    public MappedStatement(String statementId, Class resultTypeClass, String statementType, SqlSource sqlSource) {
        this.statementId = statementId;
        this.statementType = statementType;
        this.resultTypeClass = resultTypeClass;
        this.sqlSource = sqlSource;
    }

    public String getStatementId() {
        return statementId;
    }

    public void setStatementId(String statementId) {
        this.statementId = statementId;
    }

    public String getStatementType() {
        return statementType;
    }

    public void setStatementType(String statementType) {
        this.statementType = statementType;
    }

    public String getResultType() {
        return resultType;
    }

    public void setResultType(String resultType) {
        this.resultType = resultType;
    }

    public Class getResultTypeClass() {
        return resultTypeClass;
    }

    public void setResultTypeClass(Class resultTypeClass) {
        this.resultTypeClass = resultTypeClass;
    }

    public SqlSource getSqlSource() {
        return sqlSource;
    }

    public void setSqlSource(SqlSource sqlSource) {
        this.sqlSource = sqlSource;
    }
}

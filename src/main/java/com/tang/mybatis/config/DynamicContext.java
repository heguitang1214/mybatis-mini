package com.tang.mybatis.config;

import java.util.HashMap;
import java.util.Map;

/**
 * 动态上下文
 */
public class DynamicContext {

    private StringBuffer sb = new StringBuffer();

    private Map<String, Object> bindings = new HashMap<>();

    public DynamicContext(Object param) {
        // 为了处理${}时，需要用到入参对象
        this.bindings.put("_parameter", param);
    }

    public String getSql() {
        return sb.toString();
    }

    public void appendSql(String sqlText) {
        this.sb.append(sqlText);
        this.sb.append(" ");
    }

    public Map<String, Object> getBindings() {
        return bindings;
    }
}

package com.tang.mybatis.sqlnode;

import com.tang.mybatis.config.DynamicContext;
import com.tang.mybatis.utils.GenericTokenParser;
import com.tang.mybatis.utils.OgnlUtils;
import com.tang.mybatis.utils.SimpleTypeRegistry;
import com.tang.mybatis.utils.TokenHandler;

/**
 * 封装了带有${}的 SQL文本
 */
public class TextSqlNode implements SqlNode {

    private String sqlText;

    public TextSqlNode(String sqlText) {
        this.sqlText = sqlText;
    }

    public boolean isDynamic() {
        return sqlText.indexOf("${") > -1;
    }

    @Override
    public void apply(DynamicContext context) {
        // 处理${}
        GenericTokenParser tokenParser = new GenericTokenParser("${", "}", new BindingTokenHandler(context));
        String sql = tokenParser.parse(sqlText);
        context.appendSql(sql);
    }

    /**
     * 处理${}中的内容
     */
    class BindingTokenHandler implements TokenHandler {
        // 为了获取入参对象
        private DynamicContext context;

        public BindingTokenHandler(DynamicContext context) {
            this.context = context;
        }

        /**
         * @param content 这就是${}中的参数名称
         * @return
         */
        @Override
        public String handleToken(String content) {
            Object parameter = context.getBindings().get("_parameter");
            if (SimpleTypeRegistry.isSimpleType(parameter.getClass())) {
                return parameter.toString();
            }
            // 使用ONGL表达式获取值
            Object value = OgnlUtils.getValue(content, parameter);
            return value == null ? "" : value.toString();
        }
    }
}

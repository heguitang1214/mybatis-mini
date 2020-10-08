package com.tang.mybatis.sqlnode;


import com.tang.mybatis.config.DynamicContext;
import com.tang.mybatis.utils.OgnlUtils;

/**
 * 封装了带有if标签的动态标签
 */
public class IfSqlNode implements SqlNode{
    private String test;

    private SqlNode mixedSqlNode;

    public IfSqlNode(String test, SqlNode mixedSqlNode) {
        this.test = test;
        this.mixedSqlNode = mixedSqlNode;
    }

    @Override
    public void apply(DynamicContext context) {
        // 使用OGNL对test中的内容进行判断（test属性值写的就是ONGL表达式的语法）
        Object parameter = context.getBindings().get("_parameter");
        boolean b = OgnlUtils.evaluateBoolean(test, parameter);
        if (b){
            mixedSqlNode.apply(context);
        }
    }
}

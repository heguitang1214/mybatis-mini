package com.tang.mybatis.sqlnode;


import com.tang.mybatis.config.DynamicContext;

public interface SqlNode {

    /**
     * SqlNodede 的执行方法
     *
     * @param context 动态上下文
     */
    void apply(DynamicContext context);
}

package com.tang.mybatis.io;

import java.io.InputStream;
import java.io.Reader;

public class Resources {

    public static InputStream getResourceAsStream(String location) {
        return Resources.class.getClassLoader().getResourceAsStream(location);
    }

    public static Reader getResourceAsReader(String location) {
        // TODO: 2020/10/2 需要具体实现
        return null;
    }
}

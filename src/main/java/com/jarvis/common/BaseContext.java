package com.jarvis.common;

/**
 * 基于ThreadLocal封装工具类，用户保存和获取当前登录用户的ID
 */
public class BaseContext {

    private static ThreadLocal<Long> threadLocal = new ThreadLocal<Long>();

    public static void setCurrentId(Long id) {
        threadLocal.set(id);
//        threadLocal.remove();
    }

    public static Long getCurrentId() {
       return threadLocal.get();
//        threadLocal.remove();

    }


}

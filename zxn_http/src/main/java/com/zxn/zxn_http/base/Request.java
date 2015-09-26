package com.zxn.zxn_http.base;

/**
 *
 * 网络请求类，GET和DELETE不能传递参数，因为其请求的性质所致，用户可以将参数构建到url后传递到Request中
 *
 * Created by ZXN on 2015/9/26 0026.
 */
public abstract class Request<T> implements Comparable<T> {

    /**
     * http request method enum
     * 
     * @author ZXN
     * created at 2015/9/26 0026 23:15
     */
    public static enum HttpMethod {
        GET("GET"),
        POST("POST"),
        PUT("PUT"),
        DELETE("DELETE");


        /**
         * http request type
         */
        private String httpMethod = "";

        /**
         * @param method
         */
        private HttpMethod(String method) {
            httpMethod = method;
        }

        @Override
        public String toString() {
            return httpMethod;
        }

    }

    /**
     * 优先级枚举
     * 
     * @author ZXN
     * created at 2015/9/26 0026 23:16
     */
    public static enum Priority {
        LOW,
        NORMAL,
        HIGH,
        IMMEDIATE
    }



}

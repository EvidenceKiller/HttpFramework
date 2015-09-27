package com.zxn.zxn_http.base;


import java.util.HashMap;
import java.util.Map;

/**
 *
 * 网络请求类，GET和DELETE不能传递参数，因为其请求的性质所致，用户可以将参数构建到url后传递到Request中
 *
 * Created by ZXN on 2015/9/26 0026.
 * @param <T>
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

    /**
     * Default encoding for POST or GET parameters.
     */
    public static final String DEFAULT_PARAMS_ENCODING = "UTF-8";

    /**
     * Default Content-Type
     */
    public static final String HEADER_CONTENT_TYPE = "Content-Type";

    /**
     * 请求序列号
     */
    protected int serialNum = 0;

    /**
     * 优先级设置为normal
     */
    protected Priority priority = Priority.NORMAL;

    /**
     * 是否取消请求
     */
    protected boolean isCancel = false;

    /**
     * 是否缓存
     */
    private boolean shouldCache = true;

    /**
     * 请求Listener
     */
    protected RequestListener<T> requestListener;

    private String url = "";

    /**
     * 请求方法
     */
    HttpMethod httpMethod = HttpMethod.GET;

    /**
     * 请求的headers
     */
    private Map<String, String> headers = new HashMap<String, String>();

    /**
     * 请求参数
     */
    private Map<String, String> bodyParams = new HashMap<String, String>();

    /**
     * 添加头
     *
     * @param name
     * @param value
     */
    public void addHeader(String name, String value) {
        headers.put(name, value);
    }

    /**
     * 从网络中解析返回的数据
     *
     * @param response
     * @return
     */
    public abstract T parseResponse(Response response);






    /**
     * 网络请求监听
     *
     * @author ZXN
     * created at 2015/9/27 0027 21:07
     */
    public static interface RequestListener<T> {
        /**
         * 请求完成回调
         *
         * @param stCode
         * @param response
         * @param errorMsg
         */
        public void onComplete(int stCode, T response, String errorMsg);
    }

}

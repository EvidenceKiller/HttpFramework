package com.zxn.zxn_http.base;


import android.util.Log;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * 网络请求类，GET和DELETE不能传递参数，因为其请求的性质所致，用户可以将参数构建到url后传递到Request中
 *
 * Created by ZXN on 2015/9/26 0026.
 * @param <T>
 */
public abstract class Request<T> implements Comparable<Request<T>> {

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

    public Request(HttpMethod method, String url, RequestListener<T> listener) {
        httpMethod = method;
        this.url = url;
        requestListener = listener;
    }

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
     * 处理response，该方法在UI线程执行
     *
     * @param response
     */
    public final void deliveryResponse(Response response) {
        T result = parseResponse(response);
        if (requestListener != null) {
            int stCode = response != null ? response.getStatusCode() : -1;
            String msg = response != null ? response.getMessage() : "unknow error";
            Log.e("Request", "### perform listener : stCode = " + stCode + ", result = " + result.toString() + ", err = " + msg);
            requestListener.onComplete(stCode, result, msg);
        }
    }

    public String getUrl() {
        return url;
    }

    public RequestListener<T> getRequestListener() {
        return requestListener;
    }

    public int getSerialNum() {
        return serialNum;
    }

    public void setSerialNum(int serialNum) {
        this.serialNum = serialNum;
    }

    public Priority getPriority() {
        return priority;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }

    protected String getDefaultParamsEncoding() {
        return DEFAULT_PARAMS_ENCODING;
    }

    public String getBodyContentType() {
        return "application/x-www-form-urlencoded; charset=" + getDefaultParamsEncoding();
    }

    public HttpMethod getHttpMethod() {
        return httpMethod;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public Map<String, String> getParams() {
        return bodyParams;
    }

    public boolean isHttps() {
        return url.startsWith("https");
    }

    public void setShouldCache(boolean shouldCache) {
        this.shouldCache = shouldCache;
    }

    public void cancel() {
        this.isCancel = true;
    }

    public boolean isCanceled() {
        return isCancel;
    }

    /**
     * return the raw POST or PUT body to be sent
     *
     * @return
     */
    public byte[] getBody() {
        Map<String, String> params = getParams();
        if (params != null && params.size() > 0) {
            return encodeParameters(params, getDefaultParamsEncoding());
        }
        return null;
    }

    /**
     * Converts <code>params</code> into an application/x-www-form-urlencoded
     *
     * @param params
     * @param paramsEncoding
     * @return
     */
    private byte[] encodeParameters(Map<String, String> params, String paramsEncoding) {
        StringBuilder encodedParams = new StringBuilder();
        try {
            for (Map.Entry<String, String> entry : params.entrySet()) {
                encodedParams.append(URLEncoder.encode(entry.getKey(), paramsEncoding));
                encodedParams.append("=");
                encodedParams.append(URLEncoder.encode(entry.getValue(), paramsEncoding));
                encodedParams.append("&");
            }
            return encodedParams.toString().getBytes(paramsEncoding);
        } catch (UnsupportedEncodingException uee) {
            throw new RuntimeException("Encoding not supported : " + paramsEncoding, uee);
        }
    }

    /**
     * @param anthor
     * @return
     */
    @Override
    public int compareTo(Request<T> anthor) {
        Priority myPriority = this.getPriority();
        Priority anthorPriority = anthor.getPriority();
        return myPriority.equals(anthorPriority) ? this.getSerialNum() - anthor.getSerialNum() : myPriority.ordinal() - anthorPriority.ordinal();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((headers == null) ? 0 : headers.hashCode());
        result = prime * result + ((httpMethod == null) ? 0 : httpMethod.hashCode());
        result = prime * result + ((bodyParams == null) ? 0 : bodyParams.hashCode());
        result = prime * result + ((priority == null) ? 0 : priority.hashCode());
        result = prime * result + (shouldCache ? 1231 : 1237);
        result = prime * result + ((url == null) ? 0 : url.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        Request<?> other = (Request<?>) obj;
        if (headers == null) {
            if(other.getHeaders() != null)
                return false;
        } else if (!headers.equals(other.getHeaders())) {
            return false;
        }
        if (httpMethod != other.httpMethod) {
            return false;
        }
        if (bodyParams == null) {
            if (other.bodyParams != null) {
                return false;
            }
        } else if (!bodyParams.equals(other.bodyParams)) {
            return false;
        }
        if (priority != other.priority) {
            return false;
        }
        if (shouldCache != other.shouldCache) {
            return false;
        }
        if (url == null) {
            if (other.getUrl() != null) {
                return false;
            }
        } else if (!url.equals(other.getUrl())) {
            return false;
        }
        return true;
    }

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

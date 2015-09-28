package com.zxn.zxn_http.config;

import org.apache.http.conn.ssl.SSLSocketFactory;

/**
 * Created by Administrator on 2015/9/28.
 */
public class HttpClientConfig extends HttpConfig {

    private static HttpClientConfig config = new HttpClientConfig();
    private SSLSocketFactory sslSocketFactory;

    private HttpClientConfig() {

    }

    public static HttpClientConfig getConfig() {
        return config;
    }

    /**
     * 配置https请求的SSLSocketFactory
     *
     * @param sslSocketFactory
     */
    public void setHttpsConfig(SSLSocketFactory sslSocketFactory) {
        this.sslSocketFactory = sslSocketFactory;
    }

    public SSLSocketFactory getSSLSocketFactory() {
        return sslSocketFactory;
    }

}

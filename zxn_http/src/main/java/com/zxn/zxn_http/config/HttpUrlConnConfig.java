package com.zxn.zxn_http.config;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSocketFactory;

/**
 * Created by Administrator on 2015/9/28.
 */
public class HttpUrlConnConfig extends HttpConfig {

    private static HttpUrlConnConfig config = new HttpUrlConnConfig();
    private SSLSocketFactory sslSocketFactory;
    private HostnameVerifier hostnameVerifier;

    private HttpUrlConnConfig() {

    }

    public static HttpUrlConnConfig getConfig() {
        return config;
    }

    /**
     * 配置https请求的SSLSocketFactory和HostnameVerifier
     *
     * @param sslSocketFactory
     */
    public void setHttpsConfig(SSLSocketFactory sslSocketFactory, HostnameVerifier hostnameVerifier) {
        this.sslSocketFactory = sslSocketFactory;
        this.hostnameVerifier = hostnameVerifier;
    }

    public SSLSocketFactory getSSLSocketFactory() {
        return sslSocketFactory;
    }

    public HostnameVerifier getHostnameVerifier() {
        return hostnameVerifier;
    }

}

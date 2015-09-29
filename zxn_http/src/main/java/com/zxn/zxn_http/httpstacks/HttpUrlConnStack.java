package com.zxn.zxn_http.httpstacks;

import com.zxn.zxn_http.base.Request;
import com.zxn.zxn_http.base.Response;
import com.zxn.zxn_http.config.HttpUrlConnConfig;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

/**
 * 使用HttpURLConnection执行网络请求的HttpStack
 *
 * Created by Administrator on 2015/9/28.
 */
public class HttpUrlConnStack implements HttpStack {

    /**
     * 配置Https
     */
    HttpUrlConnConfig config = HttpUrlConnConfig.getConfig();

    /**
     * 执行Http请求
     *
     * @param request
     * @return
     */
    @Override
    public Response performRequest(Request<?> request) {

        HttpURLConnection httpURLConnection = null;
        try {
            // 构建HttpURLConnection
            httpURLConnection = createUrlConnection(request.getUrl());
            // 设置Headers
            setRequestHeaders(httpURLConnection, request);
        }

        return null;
    }

    private HttpURLConnection createUrlConnection(String url) throws IOException {
        URL newUrl = new URL(url);
        URLConnection urlConnection = newUrl.openConnection();
        urlConnection.setConnectTimeout(config.connTimeout);
        urlConnection.setReadTimeout(config.soTimeout);
        urlConnection.setDoInput(true);
        urlConnection.setUseCaches(false);
        return (HttpURLConnection) urlConnection;
    }
}

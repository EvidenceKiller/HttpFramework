package com.zxn.zxn_http.httpstacks;

import com.zxn.zxn_http.base.Request;
import com.zxn.zxn_http.base.Response;
import com.zxn.zxn_http.config.HttpUrlConnConfig;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Set;

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
            // 设置body参数
            setRequestParams(httpURLConnection, request);
        }

        return null;
    }

    /**
     * 创建HttpURLConnection
     *
     * @param url
     * @return
     * @throws IOException
     */
    private HttpURLConnection createUrlConnection(String url) throws IOException {
        URL newUrl = new URL(url);
        URLConnection urlConnection = newUrl.openConnection();
        urlConnection.setConnectTimeout(config.connTimeout);
        urlConnection.setReadTimeout(config.soTimeout);
        urlConnection.setDoInput(true);
        urlConnection.setUseCaches(false);
        return (HttpURLConnection) urlConnection;
    }

    /**
     * 设置请求头
     *
     * @param connection
     * @param request
     */
    private void setRequestHeaders(HttpURLConnection connection, Request<?> request) {
        Set<String> headersKeys = request.getHeaders().keySet();
        for (String headerName : headersKeys) {
            connection.addRequestProperty(headerName, request.getHeaders().get(headerName));
        }
    }

    /**
     * 设置请求参数
     *
     * @param connection
     * @param request
     * @throws IOException
     */
    private void setRequestParams(HttpURLConnection connection, Request<?> request) throws IOException {
        Request.HttpMethod method = request.getHttpMethod();
        connection.setRequestMethod(method.toString());
        // add params
        byte[] body = request.getBody();
        if (body != null) {
            // enable output
            connection.setDoOutput(true);
            // set content type
            connection.addRequestProperty(Request.HEADER_CONTENT_TYPE, request.getBodyContentType());
            // write params data to connection
            DataOutputStream dataOutputStream = new DataOutputStream(connection.getOutputStream());
            dataOutputStream.write(body);
            dataOutputStream.close();
        }
    }

}

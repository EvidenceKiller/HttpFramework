package com.zxn.zxn_http.httpstacks;

import android.net.http.AndroidHttpClient;

import com.zxn.zxn_http.base.Request;
import com.zxn.zxn_http.base.Response;
import com.zxn.zxn_http.config.HttpClientConfig;

import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import java.util.Map;

/**
 * api9以下使用HttpClient执行网络请求
 *
 * Created by Administrator on 2015/9/28.
 */
public class HttpClientStack implements HttpStack {

    /**
     * 使用HttpClient执行网络请求时的Https配置
     */
    HttpClientConfig config = HttpClientConfig.getConfig();

    HttpClient httpClient = AndroidHttpClient.newInstance(config.userAgent);

    /**
     * 执行Http请求
     *
     * @param request
     * @return
     */
    @Override
    public Response performRequest(Request<?> request) {
        try {
            // 创建http请求
            HttpUriRequest httpRequest = createHttpRequest(request);
            // 添加连接参数
            setConnectionParams(httpRequest);
            // 添加头
            addHeaders(httpRequest, request.getHeaders());
            // https配置
            configHttps(request);
            // 执行请求
            HttpResponse response = httpClient.execute(httpRequest);
            // 构建response
            Response rawResponse = new Response(response.getStatusLine());
            // 设置Entity
            rawResponse.setEntity(response.getEntity());
            return rawResponse;
        } catch (Exception e) {
        }

        return null;
    }

    /**
     * 根据请求类型创建不同的http请求
     *
     * @param request
     * @return
     */
    private HttpUriRequest createHttpRequest(Request<?> request) {
        HttpUriRequest httpUriRequest = null;
        switch(request.getHttpMethod()) {
            case GET:
                httpUriRequest = new HttpGet(request.getUrl());
                break;
            case POST:
                httpUriRequest = new HttpPost(request.getUrl());
                httpUriRequest.addHeader(Request.HEADER_CONTENT_TYPE, request.getBodyContentType());
                setEntityIfNonEmptyBody((HttpPost) httpUriRequest, request);
                break;
            case PUT:
                httpUriRequest = new HttpPut(request.getUrl());
                httpUriRequest.addHeader(Request.HEADER_CONTENT_TYPE, request.getBodyContentType());
                setEntityIfNonEmptyBody((HttpPost) httpUriRequest, request);
                break;
            case DELETE:
                httpUriRequest = new HttpDelete(request.getUrl());
                break;
            default:
                throw new IllegalStateException("Unknow request method");
        }
        return httpUriRequest;
    }

    /**
     * 将请求参数设置到HttpEntity中
     *
     * @param httpRequest
     * @param request
     */
    private void setEntityIfNonEmptyBody(HttpEntityEnclosingRequestBase httpRequest, Request<?> request) {
        byte[] body = request.getBody();
        if (body != null) {
            HttpEntity entity = new ByteArrayEntity(body);
            httpRequest.setEntity(entity);
        }
    }

    /**
     * 设置连接参数
     *
     * @param httpUriRequest
     */
    private void setConnectionParams(HttpUriRequest httpUriRequest) {
        HttpParams httpParams = httpUriRequest.getParams();
        HttpConnectionParams.setConnectionTimeout(httpParams, config.connTimeout);
        HttpConnectionParams.setSoTimeout(httpParams, config.soTimeout);
    }

    /**
     * 添加头
     *
     * @param httpUriRequest
     * @param headers
     */
    private void addHeaders(HttpUriRequest httpUriRequest, Map<String, String> headers) {
        for (String key : headers.keySet()) {
            httpUriRequest.setHeader(key, headers.get(key));
        }
    }

    /**
     *
     * 如果是https请求，则使用用户配置的SSLSocketFactory进行配置
     * @param request
     */
    private void configHttps(Request<?> request) {
        SSLSocketFactory sslSocketFactory = config.getSSLSocketFactory();
        if (request.isHttps() && sslSocketFactory != null) {
            Scheme scheme = new Scheme("https", sslSocketFactory, 443);
            httpClient.getConnectionManager().getSchemeRegistry().register(scheme);
        }
    }

}

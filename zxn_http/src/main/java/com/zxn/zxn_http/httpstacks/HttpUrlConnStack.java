package com.zxn.zxn_http.httpstacks;

import com.zxn.zxn_http.base.Request;
import com.zxn.zxn_http.base.Response;
import com.zxn.zxn_http.config.HttpUrlConnConfig;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.ProtocolVersion;
import org.apache.http.StatusLine;
import org.apache.http.entity.BasicHttpEntity;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicStatusLine;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSocketFactory;

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
            // https配置
            configHttps(request);
            return fetchResponse(httpURLConnection);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (httpURLConnection != null) {
                httpURLConnection.disconnect();
            }
        }
        return null;
    }

    /**
     * 创建URLConnection
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
     * @param httpURLConnection
     * @param request
     */
    private void setRequestHeaders(HttpURLConnection httpURLConnection, Request<?> request) {
        Set<String> headersKeys = request.getHeaders().keySet();
        for (String headerName : headersKeys) {
            httpURLConnection.addRequestProperty(headerName, request.getHeaders().get(headerName));
        }
    }

    /**
     * 设置请求body
     *
     * @param httpURLConnection
     * @param request
     * @throws ProtocolException
     * @throws IOException
     */
    private void setRequestParams(HttpURLConnection httpURLConnection, Request<?> request) throws ProtocolException, IOException {
        Request.HttpMethod httpMethod = request.getHttpMethod();
        httpURLConnection.setRequestMethod(httpMethod.toString());
        // add params
        byte[] body = request.getBody();
        if (body != null) {
            // enable output
            httpURLConnection.setDoOutput(true);
            // set content type
            httpURLConnection.addRequestProperty(Request.HEADER_CONTENT_TYPE, request.getBodyContentType());
            // write params data to connection
            DataOutputStream dataOutputStream = new DataOutputStream(httpURLConnection.getOutputStream());
            dataOutputStream.write(body);
            dataOutputStream.close();
        }
    }

    /**
     * 配置Https
     *
     * @param request
     */
    private void configHttps(Request<?> request) {
        if (request.isHttps()) {
            SSLSocketFactory sslSocketFactory = config.getSSLSocketFactory();
            // 配置Https
            if (sslSocketFactory != null) {
                HttpsURLConnection.setDefaultSSLSocketFactory(sslSocketFactory);
                HttpsURLConnection.setDefaultHostnameVerifier(config.getHostnameVerifier());
            }
        }
    }

    /**
     * 取响应
     *
     * @param httpURLConnection
     * @return
     * @throws IOException
     */
    private Response fetchResponse(HttpURLConnection httpURLConnection) throws IOException {
        // Initialize HttpResponse with data from the HttpURLConnection
        ProtocolVersion protocolVersion = new ProtocolVersion("HTTP", 1, 1);
        int responseCode = httpURLConnection.getResponseCode();
        if (responseCode == -1) {
            throw new IOException("Could not retrieve response code from HttpURLConnection");
        }
        StatusLine responseStatus = new BasicStatusLine(protocolVersion, httpURLConnection.getResponseCode(), httpURLConnection.getResponseMessage());
        // 构建response
        Response response = new Response(responseStatus);
        // 设置response数据
        response.setEntity(entityFromURLConnection(httpURLConnection));
        addHeadersToResponse(response, httpURLConnection);
        return response;
    }

    /**
     * 执行http请求之后返回的数据流，返回请求的结果
     *
     * @param httpURLConnection
     * @return
     */
    private HttpEntity entityFromURLConnection(HttpURLConnection httpURLConnection) {
        BasicHttpEntity entity = new BasicHttpEntity();
        InputStream inputStream = null;
        try {
            httpURLConnection.getInputStream();
        } catch (IOException e) {
            e.printStackTrace();
            inputStream = httpURLConnection.getErrorStream();
        }

        entity.setContent(inputStream);
        entity.setContentLength(httpURLConnection.getContentLength());
        entity.setContentEncoding(httpURLConnection.getContentEncoding());
        entity.setContentType(httpURLConnection.getContentType());

        return entity;
    }

    /**
     * 为响应添加headers
     *
     * @param response
     * @param httpURLConnection
     */
    private void addHeadersToResponse(Response response, HttpURLConnection httpURLConnection) {
        for (Map.Entry<String, List<String>> header : httpURLConnection.getHeaderFields().entrySet()) {
            if (header.getKey() != null) {
                Header h = new BasicHeader(header.getKey(), header.getValue().get(0));
                response.addHeader(h);
            }
        }
    }
}

package com.zxn.zxn_http.requests;

import com.zxn.zxn_http.base.Request;
import com.zxn.zxn_http.base.Response;

/**
 * User : Administrator
 * Date : 2015-10-13
 * Time : 17:00
 */
public class StringRequest extends Request<String> {

    public StringRequest(HttpMethod method, String url, RequestListener<String> listener) {
        super(method, url, listener);
    }

    @Override
    public String parseResponse(Response response) {
        return new String(response.getRawData());
    }
}

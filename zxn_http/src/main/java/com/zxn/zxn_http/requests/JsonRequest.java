package com.zxn.zxn_http.requests;

import com.zxn.zxn_http.base.Request;
import com.zxn.zxn_http.base.Response;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * User : Administrator
 * Date : 2015-10-13
 * Time : 17:05
 */
public class JsonRequest extends Request<JSONObject> {

    public JsonRequest(HttpMethod method, String url, RequestListener<JSONObject> listener) {
        super(method, url, listener);
    }

    @Override
    public JSONObject parseResponse(Response response) {
        String jsonString = new String(response.getRawData());
        try {
            return new JSONObject(jsonString);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}

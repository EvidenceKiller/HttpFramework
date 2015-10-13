package com.zxn.zxn_http.requests;

import android.util.Log;

import com.zxn.zxn_http.base.Request;
import com.zxn.zxn_http.base.Response;
import com.zxn.zxn_http.entity.MultipartEntity;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Multipart请求 ( 只能为POST请求 ),该请求可以搭载多种类型参数,比如文本、文件等,但是文件仅限于小文件,否则会出现OOM异常.
 *
 * User : Administrator
 * Date : 2015-10-13
 * Time : 17:09
 */
public class MultipartRequest extends Request<String> {

    MultipartEntity multipartEntity = new MultipartEntity();

    public MultipartRequest(String url, RequestListener<String> listener) {
        super(HttpMethod.POST, url, listener);
    }

    public MultipartEntity getMultipartEntity() {
        return multipartEntity;
    }

    @Override
    public String getBodyContentType() {
        return multipartEntity.getContentType().getValue();
    }

    @Override
    public byte[] getBody() {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            multipartEntity.writeTo(bos);
        } catch (IOException e) {
            Log.e("MultipartRequest", "IOException writing to ByteArrayOutputStream");
        }
        return bos.toByteArray();
    }

    @Override
    public String parseResponse(Response response) {
        if (response != null && response.getRawData() != null) {
            return new String(response.getRawData());
        }
        return "";
    }
}

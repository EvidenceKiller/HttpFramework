package com.zxn.zxn_http.base;

import android.net.wifi.WifiConfiguration;

import org.apache.http.HttpEntity;
import org.apache.http.ProtocolVersion;
import org.apache.http.StatusLine;
import org.apache.http.message.BasicHttpResponse;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

/**
 * User : ZXN
 * Date : 2015-09-27
 * Time : 21:21
 */
public class Response extends BasicHttpResponse {

    public byte[] rawData = new byte[0];

    public Response(StatusLine statusLine) {
        super(statusLine);
    }

    public Response(ProtocolVersion version, int code, String reason) {
        super(version, code, reason);
    }

    @Override
    public void setEntity(HttpEntity httpEntity) {
        super.setEntity(httpEntity);
        rawData = entityToBytes(getEntity());
    }

    public byte[] getRawData() {
        return rawData;
    }

    public int getStatusCode() {
        return getStatusLine().getStatusCode();
    }

    public String getMessage() {
        return getStatusLine().getReasonPhrase();
    }

    /**
     * Reads the contents of HttpEntity into a byte[]
     *
     * @param httpEntity
     * @return
     */
    private byte[] entityToBytes(HttpEntity httpEntity) {
        try {
            return EntityUtils.toByteArray(httpEntity);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new byte[0];
    }

}

package com.zxn.zxn_http.httpstacks;

import android.os.Build;

/**
 * 根据API版本选择HttpClient或者HttpURLConnection
 *
 * User : ZXN
 * Date : 2015-10-09
 * Time : 20:51
 */
public final class HttpStackFactory {

    private static final int GINGERBREAD_SDK_NUM = 9;

    public static HttpStack createHttpStack() {
        int runtimeSDKApi = Build.VERSION.SDK_INT;
        if (runtimeSDKApi >= GINGERBREAD_SDK_NUM) {
            return new HttpUrlConnStack();
        }

        return new HttpClientStack();
    }

}

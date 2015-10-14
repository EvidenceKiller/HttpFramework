package com.zxn.zxn_http.core;

/**
 * User : Administrator
 * Date : 2015-10-14
 * Time : 08:54
 */
public final class SimpleNet {

    public static RequestQueue newRequestQueue() {
        return newRequestQueue(RequestQueue.DEFAULT_CORE_NUMS);
    }

}

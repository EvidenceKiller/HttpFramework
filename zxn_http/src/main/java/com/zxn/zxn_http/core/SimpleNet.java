package com.zxn.zxn_http.core;

import com.zxn.zxn_http.httpstacks.HttpStack;

/**
 * User : Administrator
 * Date : 2015-10-14
 * Time : 08:54
 */
public final class SimpleNet {

    /**
     * 创建一个请求队列，NetworkExecutor数量为默认的数量
     *
     * @return
     */
    public static RequestQueue newRequestQueue() {
        return newRequestQueue(RequestQueue.DEFAULT_CORE_NUMS);
    }

    /**
     * 创建一个请求队列，NetworkExecutor数量为coreNums
     *
     * @param coreNums
     * @return
     */
    public static RequestQueue newRequestQueue(int coreNums) {
        return newRequestQueue(coreNums, null);
    }

    /**
     * 创建一个请求队列，NetworkExecutor数量为coreNums
     *
     * @param coreNums
     * @param httpStack
     * @return
     */
    public static RequestQueue newRequestQueue(int coreNums, HttpStack httpStack) {
        RequestQueue queue = new RequestQueue(Math.max(0, coreNums), httpStack);
        queue.start();
        return queue;
    }


}

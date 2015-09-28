package com.zxn.zxn_http.httpstacks;

import com.zxn.zxn_http.base.Request;
import com.zxn.zxn_http.base.Response;

/**
 * 执行网络请求的接口
 *
 * Created by Administrator on 2015/9/28.
 */
public interface HttpStack {

    /**
     * 执行Http请求
     *
     * @param request
     * @return
     */
    public Response performRequest(Request<?> request);

}

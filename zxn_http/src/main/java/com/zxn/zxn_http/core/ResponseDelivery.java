package com.zxn.zxn_http.core;

import android.os.Handler;
import android.os.Looper;

import com.zxn.zxn_http.base.Request;
import com.zxn.zxn_http.base.Response;

import java.util.concurrent.Executor;

/**
 * 请求结果投递类，将请求结果投递给UI线程
 *
 * User : Administrator
 * Date : 2015-10-14
 * Time : 10:19
 */
public class ResponseDelivery implements Executor {

    /**
     * 主线程Handler
     */
    Handler responseHandler = new Handler(Looper.getMainLooper());

    public void deliveryResponse(final Request<?> request, final Response response) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                request.deliveryResponse(response);
            }
        };

        execute(runnable);
    }

    @Override
    public void execute(Runnable command) {
        responseHandler.post(command);
    }
}

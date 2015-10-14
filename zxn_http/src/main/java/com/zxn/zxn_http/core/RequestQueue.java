package com.zxn.zxn_http.core;

import android.util.Log;

import com.zxn.zxn_http.base.Request;
import com.zxn.zxn_http.httpstacks.HttpStack;
import com.zxn.zxn_http.httpstacks.HttpStackFactory;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 请求队列，使用优先队列，使得请求可以按照优先级进行处理
 *
 * User : Administrator
 * Date : 2015-10-14
 * Time : 08:56
 */
public final class RequestQueue {

    /**
     * 请求队列，线程安全
     */
    private BlockingQueue<Request<?>> requestQueue = new PriorityBlockingQueue<Request<?>>();

    /**
     * 请求的序列化生成器
     */
    private AtomicInteger serialNumGenerator = new AtomicInteger(0);

    /**
     * 默认核心数
     */
    public static int DEFAULT_CORE_NUMS = Runtime.getRuntime().availableProcessors() + 1;

    /**
     * CPU核心数 + 1个分发线程数
     */
    private int dispatcherNums = DEFAULT_CORE_NUMS;

    /**
     * NetworkExecutor,执行网络请求的线程
     */
    private NetworkExecutor[] dispatchers = null;

    /**
     * Http请求的真正执行者
     */
    private HttpStack httpStack;

    /**
     * @param coreNums
     * @param httpStack
     */
    protected RequestQueue(int coreNums, HttpStack httpStack) {
        dispatcherNums = coreNums;
        this.httpStack = httpStack != null ? httpStack : HttpStackFactory.createHttpStack();
    }

    private final void startNetWorkExecutor() {
        dispatchers = new NetworkExecutor[dispatcherNums];
        for (int i = 0; i < dispatcherNums; i++) {
            dispatchers[i] = new NetworkExecutor(requestQueue, httpStack);
            dispatchers[i].start();
        }
    }

    public void start() {
        stop();
        startNetWorkExecutor();
    }

    /**
     * 停止NetworkExecutor
     */
    public void stop() {
        if (dispatchers != null && dispatchers.length > 0) {
            for (int i = 0; i < dispatchers.length; i++) {
                dispatchers[i].quit();
            }
        }
    }

    /**
     * 添加request，不能重复添加
     *
     * @param request
     */
    public void addRequest(Request<?> request) {
        if (!requestQueue.contains(request)) {
            request.setSerialNum(this.generateSerialNumber());
            requestQueue.add(request);
        } else {
            Log.i("###", "Request queue has contain request");
        }
    }

    public void clear() {
        requestQueue.clear();
    }

    public BlockingQueue<Request<?>> getAllRequests() {
        return requestQueue;
    }

    /**
     * 为每个请求生成一个序列号
     *
     * @return
     */
    private int generateSerialNumber() {
        return serialNumGenerator.incrementAndGet();
    }


}

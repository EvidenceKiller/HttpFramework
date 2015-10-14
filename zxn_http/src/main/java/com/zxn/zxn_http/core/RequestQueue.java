package com.zxn.zxn_http.core;

import com.zxn.zxn_http.base.Request;

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



}

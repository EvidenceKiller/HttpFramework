package com.zxn.zxn_http.core;

import com.zxn.zxn_http.base.Request;
import com.zxn.zxn_http.base.Response;
import com.zxn.zxn_http.cache.Cache;
import com.zxn.zxn_http.cache.LruMemCache;
import com.zxn.zxn_http.httpstacks.HttpStack;

import java.util.concurrent.BlockingQueue;

/**
 * 网络请求Executor，继承自Thread，从网络请求队列中循环读取请求并且执行
 *
 * User : Administrator
 * Date : 2015-10-14
 * Time : 10:07
 */
public class NetworkExecutor extends Thread {

   /**
    * 网络请求队列
    */
   private BlockingQueue<Request<?>> requestQueue;

   /**
    * 网络请求栈
    */
   private HttpStack httpStack;

   /**
    * 结果分发器，将结果返回到主线程
    */
   private static ResponseDelivery responseDelivery = new ResponseDelivery();

   /**
    * 请求缓存
    */
   private static Cache<String, Response> cache = new LruMemCache();

   /**
    * 是否停止
    */
   private boolean isStop = false;


}

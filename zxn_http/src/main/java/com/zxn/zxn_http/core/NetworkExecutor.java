package com.zxn.zxn_http.core;

import android.util.Log;

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

   public NetworkExecutor(BlockingQueue<Request<?>> queue, HttpStack httpStack) {
      this.requestQueue = queue;
      this.httpStack = httpStack;
   }

   /**
    *
    */
   @Override
   public void run() {
      try {
         while (!isStop) {
            final Request<?> request = requestQueue.take();
            if (request.isCanceled()) {
               Log.d("###", "### request is canceled");
               continue;
            }

            Response response = null;
            if (isUseCache(request)) {
               // 从缓存中读取
               response = cache.get(request.getUrl());
            } else {
               // 从网络上获取
               response = httpStack.performRequest(request);
               // 如果该请求需要缓存，那么请求成功则缓存到responseCache中
               if (request.shouldCache() && isSuccess(response)) {
                  cache.put(request.getUrl(), response);
               }
            }

            // 分发请求结果
            responseDelivery.deliveryResponse(request, response);
         }
      } catch (InterruptedException e) {
         Log.e("", "### delivery response exit");
      }
   }

   /**
    * 判断请求是否成功
    *
    * @param response
    * @return
    */
   private boolean isSuccess(Response response) {
      return response != null && response.getStatusCode() == 200;
   }

   /**
    * 判断本地是否存在缓存
    *
    * @param request
    * @return
    */
   private boolean isUseCache(Request<?> request) {
      return request.shouldCache() && cache.get(request.getUrl()) != null;
   }

   public void quit() {
      isStop = true;
      interrupt();
   }


}

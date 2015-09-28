package com.zxn.zxn_http.cache;

import android.support.v4.util.LruCache;

import com.zxn.zxn_http.base.Response;

/**
 * 内存缓存，即一级缓存
 *
 * Created by Administrator on 2015/9/28.
 */
public class LruMemCache implements Cache<String, Response> {


    /**
     * Response缓存
     */
    private LruCache<String, Response> responseCache;

    public LruMemCache() {
        // 计算可以使用的最大内存
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);

        // 取八分之一用作缓存
        final int cacheSize = maxMemory / 8;
        responseCache = new LruCache<String, Response>(cacheSize) {

            @Override
            protected int sizeOf(String key, Response response) {
                return response.rawData.length / 1024;
            }

        };
    }

    @Override
    public Response get(String key) {
        return responseCache.get(key);
    }

    @Override
    public void put(String key, Response value) {
        responseCache.put(key, value);
    }

    @Override
    public void remove(String key) {
        responseCache.remove(key);
    }

}

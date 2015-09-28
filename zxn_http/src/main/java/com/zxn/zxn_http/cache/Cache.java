package com.zxn.zxn_http.cache;

/**
 * Created by Administrator on 2015/9/28.
 */
public interface Cache<K, V> {

    public V get(K key);

    public void put(K key, V value);

    public void remove(K key);

}

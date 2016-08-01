package com.feed.jedis;

import java.util.Map;

/**
 * Created by lz on 2016/6/16.
 */
public interface JedisClient {
    String get(String key);
    String set(String key, String value);
    String hget(String hkey, String key);
    long hset(String hkey, String key, String value);
    long incr(String key);
    long expire(String key, int second);
    long ttl(String key);
    long del(String key);
    long hdel(String hkey, String key);

    Map hgetAll(String hkey);
    long hlen(String hkey);
    Boolean hexists(String key,String field);
}

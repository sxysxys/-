package com.shen.shengeunion.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.shen.shengeunion.base.BaseApplication;
import com.shen.shengeunion.model.domain.CacheWithTime;

import java.util.List;

/**
 * 将json字符串缓存
 */
public class JsonCacheUtils {

    private static final String JSON_CACHE_SP_NAME = "json_cache_sp_name";
    private final SharedPreferences mSharedPreferences;
    private final Gson mGson;

    private JsonCacheUtils() {
        mSharedPreferences = BaseApplication.getContext().getSharedPreferences(JSON_CACHE_SP_NAME, Context.MODE_PRIVATE);
        mGson = new Gson();
    }

    private static JsonCacheUtils jsonCacheUtils = null;

    public static JsonCacheUtils getInstance() {
        if (jsonCacheUtils == null) {
            jsonCacheUtils = new JsonCacheUtils();
        }
        return jsonCacheUtils;
    }

    /**
     * 添加
     * @param key
     * @param val
     */
    public void saveCache(String key, Object val) {
        this.saveCache(key, val, -1L);
    }

    public void saveCache(String key, Object val, long duration) {
        SharedPreferences.Editor edit = mSharedPreferences.edit();
        String jsonVal = mGson.toJson(val);
        if (duration != -1L) {
            duration = System.currentTimeMillis() + duration;
        }
        CacheWithTime cacheWithTime = new CacheWithTime(duration, jsonVal);
        String jsonCacheTime = mGson.toJson(cacheWithTime);
        edit.putString(key, jsonCacheTime);
        edit.apply();
    }

    /**
     * 删除键
     * @param key
     */
    public void delCache(String key) {
        mSharedPreferences.edit().remove(key).apply();
    }

    public <T> T getVal(String key, Class<T> clazz) {
        String jsonCacheTime = mSharedPreferences.getString(key, null);
        if (jsonCacheTime == null) {
            return null;
        }
        CacheWithTime cacheWithTime = mGson.fromJson(jsonCacheTime, CacheWithTime.class);
        long duration = cacheWithTime.getDuration();
        if (duration != -1L && System.currentTimeMillis() - duration > 0) {
            return null;
        } else {
            String jsonVal = cacheWithTime.getJsonVal();
            T t = mGson.fromJson(jsonVal, clazz);
            return t;
        }
    }
}

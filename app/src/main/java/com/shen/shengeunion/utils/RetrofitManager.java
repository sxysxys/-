package com.shen.shengeunion.utils;

import com.shen.shengeunion.model.Api;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * retrofit请求
 */
public class RetrofitManager {
    private static RetrofitManager retrofitManager = new RetrofitManager();
    private final Retrofit mRetrofit;


    public static RetrofitManager getInstance() {
        return retrofitManager;
    }

    private RetrofitManager() {
        //创建retrofit
        mRetrofit = new Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    public Retrofit getRetrofit() {
        return mRetrofit;
    }

    public Api getApi() {
        return mRetrofit.create(Api.class);
    }
}

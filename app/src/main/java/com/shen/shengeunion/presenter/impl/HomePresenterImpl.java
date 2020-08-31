package com.shen.shengeunion.presenter.impl;

import com.shen.shengeunion.model.Api;
import com.shen.shengeunion.model.domain.Categories;
import com.shen.shengeunion.presenter.IHomePresenter;
import com.shen.shengeunion.utils.LogUtils;
import com.shen.shengeunion.utils.RetrofitManager;
import com.shen.shengeunion.view.IHomeCallback;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

import static java.net.HttpURLConnection.HTTP_OK;

public class HomePresenterImpl implements IHomePresenter {

    List<IHomeCallback> callbacks = new ArrayList<>();

    @Override
    public void getCategories() {
        if (callbacks.size() > 0) {
            for (IHomeCallback callback : callbacks) {
                callback.Loading();
            }
        }
        // 加载分类数据
        Retrofit retrofit = RetrofitManager.getInstance().getRetrofit();
        Api api = retrofit.create(Api.class);
        Call<Categories> task = api.getCategories();
        task.enqueue(new Callback<Categories>() {
            @Override
            public void onResponse(Call<Categories> call, Response<Categories> response) {
                // 数据结果
                LogUtils.d(HomePresenterImpl.this,"response code is -> " + response.code());
                if (response.code() == HTTP_OK) {
                    Categories body = response.body();
                    LogUtils.d(HomePresenterImpl.this,body.toString());
                    // 此时将数据广播给所有的监听者
                    if (callbacks.size() != 0) {
                        // 如果此时返回数据为空
                        if (body == null || body.getData().size() == 0) {
                            for (IHomeCallback callback : callbacks) {
                                callback.onEmpty();
                            }
                        } else {
                            for (IHomeCallback callback : callbacks){
                                callback.onCategoriesLoaded(body);
                            }
                        }
                    }
                } else {
                    LogUtils.w(HomePresenterImpl.this,"请求失败");
                    for (IHomeCallback callback : callbacks) {
                        callback.onNetError();
                    }
                }
            }

            @Override
            public void onFailure(Call<Categories> call, Throwable t) {
                LogUtils.w(HomePresenterImpl.this,"请求错误:" + t.getMessage());
                for (IHomeCallback callback : callbacks) {
                    callback.onNetError();
                }
            }
        });
    }

    @Override
    public void registerCallback(IHomeCallback callback) {
        callbacks.add(callback);
    }

    @Override
    public void unregisterCallback(IHomeCallback callback) {
        callbacks.remove(callback);
    }
}

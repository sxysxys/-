package com.shen.shengeunion.presenter.impl;

import com.shen.shengeunion.model.Api;
import com.shen.shengeunion.model.domain.OnSellContent;
import com.shen.shengeunion.presenter.IOnSellPagePresenter;
import com.shen.shengeunion.utils.LogUtils;
import com.shen.shengeunion.utils.RetrofitManager;
import com.shen.shengeunion.utils.UrlUtils;
import com.shen.shengeunion.view.IOnSellPageCallback;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static java.net.HttpURLConnection.HTTP_OK;

public class OnSellPagePresenterImpl implements IOnSellPagePresenter {

    private IOnSellPageCallback callback;

    private static IOnSellPagePresenter onSellPagePresenter = new OnSellPagePresenterImpl();

    private int curPage = 0;

    private boolean isLoading = false;

    private OnSellPagePresenterImpl(){}

    public static IOnSellPagePresenter getInstance() {
        return onSellPagePresenter;
    }

    /**
     * 拿到第一页的数据
     */
    @Override
    public void getOnSellContent() {
        if (isLoading) {
            return;
        }
        isLoading = true;
        if (curPage == 0) {
            callback.Loading();
        }
        Api api = RetrofitManager.getInstance().getApi();
        Call<OnSellContent> task = api.getOnSellContent(UrlUtils.getOnSellContent(curPage));
        task.enqueue(new Callback<OnSellContent>() {
            @Override
            public void onResponse(Call<OnSellContent> call, Response<OnSellContent> response) {
                isLoading = false;
                LogUtils.d(this, "response -> " + response.code());
                if (response.code() == HTTP_OK) {
                    OnSellContent body = response.body();
                    if (body == null || body.getData() == null
                            || body.getData().getTbk_dg_optimus_material_response().getResult_list().getMap_data().size() == 0) {
                        responseOnEmpty();
                    } else {
                        responseSuccess(body);
                    }
                } else {
                    responseOnError();
                }
            }

            @Override
            public void onFailure(Call<OnSellContent> call, Throwable t) {
                responseOnError();
            }
        });
    }

    private void responseOnError() {
        isLoading = false;
        if (curPage == 0) {
            callback.onNetError();
        } else {
            curPage --;
            callback.onMoreLoadedError();
        }
    }

    private void responseOnEmpty() {
        if (curPage == 0) {
            callback.onEmpty();
        } else {
            curPage --;
            callback.onMoreLoadedEmpty();
        }
    }

    private void responseSuccess(OnSellContent body) {
        if (curPage == 0) {
            callback.onContentLoaded(body.getData());
        } else {
            callback.onMoreLoader(body.getData());
        }
    }

    @Override
    public void reload() {
        this.getOnSellContent();
    }


    @Override
    public void loaderMore() {
        curPage ++;
        this.getOnSellContent();
    }

    @Override
    public void registerCallback(IOnSellPageCallback callback) {
        this.callback = callback;
    }

    @Override
    public void unregisterCallback(IOnSellPageCallback callback) {
        this.callback = null;
    }
}

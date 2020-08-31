package com.shen.shengeunion.presenter.impl;

import com.shen.shengeunion.model.Api;
import com.shen.shengeunion.model.domain.SelectedContent;
import com.shen.shengeunion.model.domain.SelectedPageCategories;
import com.shen.shengeunion.presenter.ISelectedPagePresenter;
import com.shen.shengeunion.utils.LogUtils;
import com.shen.shengeunion.utils.RetrofitManager;
import com.shen.shengeunion.utils.UrlUtils;
import com.shen.shengeunion.view.ISelectedPageCallback;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static java.net.HttpURLConnection.HTTP_OK;

public class SelectedPagePresenterImpl implements ISelectedPagePresenter {

    private List<ISelectedPageCallback> callbacks = new ArrayList<>();
    private SelectedPageCategories.DataBean mDataBean;

    private SelectedPagePresenterImpl(){}

    private static final ISelectedPagePresenter selectedPagePresenter = new SelectedPagePresenterImpl();

    public static ISelectedPagePresenter getInstance() {
        return selectedPagePresenter;
    }

    @Override
    public void getCategories() {
        for (ISelectedPageCallback callback : callbacks) {
            callback.Loading();
        }
        Api api = RetrofitManager.getInstance().getApi();
        Call<SelectedPageCategories> task = api.getSelectedPageCategories();
        task.enqueue(new Callback<SelectedPageCategories>() {
            @Override
            public void onResponse(Call<SelectedPageCategories> call, Response<SelectedPageCategories> response) {
                if (response.code() == HTTP_OK) {
                    SelectedPageCategories body = response.body();
                    if (body.getData() == null || body.getData().size() == 0) {
                        responseEmpty();
                    }
                    successLoadedCategories(body);
                } else {
                    errorResponse();
                }
            }

            @Override
            public void onFailure(Call<SelectedPageCategories> call, Throwable t) {
                t.printStackTrace();
                errorResponse();
            }
        });
    }

    private void responseEmpty() {
        for (ISelectedPageCallback callback : callbacks) {
            callback.onEmpty();
        }
    }

    private void errorResponse() {
        for (ISelectedPageCallback callback : callbacks) {
            callback.onNetError();
        }
    }

    private void successLoadedCategories(SelectedPageCategories body) {
        for (ISelectedPageCallback callback : callbacks) {
            callback.onCategoriesLoad(body);
        }
    }

    @Override
    public void getContentById(SelectedPageCategories.DataBean dataBean) {
        mDataBean = dataBean;
        int id = mDataBean.getFavorites_id();
        String url = UrlUtils.getSelectedPageUrl(id);
        Call<SelectedContent> task = RetrofitManager.getInstance().getApi().getContent(url);
        task.enqueue(new Callback<SelectedContent>() {
            @Override
            public void onResponse(Call<SelectedContent> call, Response<SelectedContent> response) {
                LogUtils.d(SelectedPagePresenterImpl.this, "response code -> " + response.code());
                if (response.code() == HTTP_OK) {
                    SelectedContent body = response.body();
                    successLoadedContent(body);
                } else {
                    errorResponse();
                }
            }

            @Override
            public void onFailure(Call<SelectedContent> call, Throwable t) {
                t.printStackTrace();
                errorResponse();
            }
        });
    }

    private void successLoadedContent(SelectedContent body) {
        for (ISelectedPageCallback callback : callbacks) {
            callback.onContentLoad(body);
        }
    }

    @Override
    public void reloadContent() {
        this.getCategories();
    }

    @Override
    public void registerCallback(ISelectedPageCallback callback) {
        callbacks.add(callback);
    }

    @Override
    public void unregisterCallback(ISelectedPageCallback callback) {
        if (callbacks.contains(callback)) {
            callbacks.remove(callback);
        }
    }
}

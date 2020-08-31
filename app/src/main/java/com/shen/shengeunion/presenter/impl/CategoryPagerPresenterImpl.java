package com.shen.shengeunion.presenter.impl;

import com.shen.shengeunion.model.Api;
import com.shen.shengeunion.model.domain.HomePagerContent;
import com.shen.shengeunion.model.domain.IItemInfo;
import com.shen.shengeunion.presenter.ICategoryPagerPresenter;
import com.shen.shengeunion.utils.LogUtils;
import com.shen.shengeunion.utils.RetrofitManager;
import com.shen.shengeunion.utils.UrlUtils;
import com.shen.shengeunion.view.ICategoryPagerCallBack;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class CategoryPagerPresenterImpl implements ICategoryPagerPresenter {

    private Map<Integer,Integer> pagesInfo = new HashMap<>();

    private List<ICategoryPagerCallBack> callbacks = new ArrayList<>();

    public static final int DEFAULT_PAGE = 1;
    private Integer curPage;

    private CategoryPagerPresenterImpl(){}

    private static ICategoryPagerPresenter presenter;

    public static ICategoryPagerPresenter getInstance() {
        if (presenter == null) {
            presenter = new CategoryPagerPresenterImpl();
        }
        return presenter;
    }

    /**
     * 通过id拿到相应的值
     * @param categoryId
     */
    @Override
    public void getContentByCategoryId(int categoryId) {
        for (ICategoryPagerCallBack callback : callbacks) {
            if (callback.getCategoryId() == categoryId) {
                callback.Loading();
            }
        }
        Integer integer = pagesInfo.get(categoryId);
        if (integer == null) {
            integer = DEFAULT_PAGE;
            pagesInfo.put(categoryId,integer);
        }
        Call<HomePagerContent> task = getHomePagerContentCall(categoryId, integer);
        task.enqueue(new Callback<HomePagerContent>() {
            @Override
            public void onResponse(Call<HomePagerContent> call, Response<HomePagerContent> response) {
                if (response.code() == HttpURLConnection.HTTP_OK) {
                    HomePagerContent body = response.body();
                    handResult(categoryId,body);
                } else {
                    handError(categoryId);
                }
            }

            @Override
            public void onFailure(Call<HomePagerContent> call, Throwable t) {
                t.printStackTrace();
                handError(categoryId);
            }
        });
    }

    /**
     * 拿到相应的task
     * @param categoryId
     * @param curPage
     * @return
     */
    private Call<HomePagerContent> getHomePagerContentCall(int categoryId, Integer curPage) {
        Retrofit retrofit = RetrofitManager.getInstance().getRetrofit();
        Api api = retrofit.create(Api.class);
        return api.getHomePagerContent(UrlUtils.getCategoryUrl(categoryId, curPage));
    }

    /**
     * 加载更多数据
     * 拿到当前页面
     * 页码++
     * 加载数据
     * 处理数据结果
     */
    @Override
    public void loadMore(int categoryId) {
        curPage = pagesInfo.get(categoryId);
        if (curPage == null) {
            curPage = 1;
        } else {
            curPage++;
        }
        Call<HomePagerContent> task = getHomePagerContentCall(categoryId, curPage);
        task.enqueue(new Callback<HomePagerContent>() {
            @Override
            public void onResponse(Call<HomePagerContent> call, Response<HomePagerContent> response) {
                if (response.code() == HttpURLConnection.HTTP_OK) {
                    HomePagerContent body = response.body();
                    LogUtils.d(CategoryPagerPresenterImpl.this, body.toString());
                    handLoadMoreResult(categoryId, body);
                } else {
                    handLoadMoreError(categoryId);
                }
            }

            @Override
            public void onFailure(Call<HomePagerContent> call, Throwable t) {
                t.printStackTrace();
                handLoadMoreError(categoryId);
            }
        });
    }

    /**
     * 当加载更多成功的的时候
     * @param categoryId
     * @param body
     */
    private void handLoadMoreResult(int categoryId, HomePagerContent body) {
        // 将此时的page++
        pagesInfo.put(categoryId,curPage);
        List<HomePagerContent.DataBean> data = body.getData();
        for (ICategoryPagerCallBack callback : callbacks) {
            if (callback.getCategoryId() == categoryId) {
                if (data == null || data.size() == 0) {
                    callback.onLoadMoreEmpty();
                } else {
                    callback.onLoadMoreLoaded(data);
                }
            }
        }
    }

    /**
     * 处理加载更多时的错误
     * @param categoryId
     */
    private void handLoadMoreError(int categoryId) {
        // 当错误的时候，将page减少
        curPage --;
        pagesInfo.put(categoryId, curPage);
        for (ICategoryPagerCallBack callback : callbacks) {
            if (callback.getCategoryId() == categoryId) {
                callback.onLoadMoreError();
            }
        }
    }

    private void handError(int categoryId) {
        for (ICategoryPagerCallBack callback : callbacks) {
            if (callback.getCategoryId() == categoryId) {
                callback.onNetError();
            }
        }
    }

    /**
     * 对送回的结果进行处理，包括轮播图和list
     * @param categoryId
     * @param body
     */
    private void handResult(int categoryId, HomePagerContent body) {
        List<HomePagerContent.DataBean> data = body.getData();
        List<HomePagerContent.DataBean> loopList = data.subList(data.size() - 5, data.size());

        for (ICategoryPagerCallBack callback : callbacks) {
            if (callback.getCategoryId() == categoryId) {
                if (data == null || data.size() == 0) {
                    callback.onEmpty();
                } else {
                    callback.onContentLoad(data);
                    callback.onLoopListLoad(loopList);
                }
            }
        }
    }

    @Override
    public void registerCallback(ICategoryPagerCallBack callback) {
        if (!callbacks.contains(callback)) {
            callbacks.add(callback);
        }
    }

    @Override
    public void unregisterCallback(ICategoryPagerCallBack callback) {
        if (callbacks.contains(callback)) {
            callbacks.remove(callback);
        }
    }
}

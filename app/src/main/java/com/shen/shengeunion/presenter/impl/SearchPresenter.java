package com.shen.shengeunion.presenter.impl;

import com.shen.shengeunion.model.Api;
import com.shen.shengeunion.model.domain.SearchResult;
import com.shen.shengeunion.model.domain.Histories;
import com.shen.shengeunion.model.domain.RecommendResult;
import com.shen.shengeunion.presenter.ISearchPresenter;
import com.shen.shengeunion.utils.JsonCacheUtils;
import com.shen.shengeunion.utils.LogUtils;
import com.shen.shengeunion.utils.RetrofitManager;
import com.shen.shengeunion.view.ISearchViewCallBack;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static java.net.HttpURLConnection.HTTP_OK;

public class SearchPresenter implements ISearchPresenter {

    private ISearchViewCallBack searchViewCallBack;

    private static final ISearchPresenter searchPresenter = new SearchPresenter();
    private final Api mApi;

    private static final int DEFAULT_PAGE = 0;

    private int mPage = DEFAULT_PAGE;
    private String mCurKeyword;
    private JsonCacheUtils mJsonCacheUtils;

    private SearchPresenter() {
        mApi = RetrofitManager.getInstance().getApi();
        mJsonCacheUtils = JsonCacheUtils.getInstance();
    }

    public static ISearchPresenter getInstance() {
        return searchPresenter;
    }

    private static int MAX_SEARCH_COUNT = 10;

    @Override
    public void getHistories() {
        Histories histories = mJsonCacheUtils.getVal(KEY_HISTORIES, Histories.class);
        // 此时成功拿到数据
        searchViewCallBack.onHistoryLoad(histories);
    }

    @Override
    public void delHistories() {
        if (mJsonCacheUtils != null) {
            mJsonCacheUtils.delCache(KEY_HISTORIES);
            // 回调
            searchViewCallBack.onHistoriesDel();
        }
    }

    public static final String KEY_HISTORIES = "key_histories";

    /**
     * 将history进行更新
     * 1. 不存在
     * 2. 存在，但是没有这条记录
     * 3. 存在，也存在这条记录
     * @param history
     */
    private void saveHistories(String history) {
        Histories histories = mJsonCacheUtils.getVal(KEY_HISTORIES, Histories.class);
        List<String> list = new ArrayList<>();
        // 如果还没添加
        if (histories == null || histories.getHistories() == null || histories.getHistories().size() == 0) {
            list.add(history);
        } else {
            // 此时需要判断历史记录里有没有
            List<String> curHistories = histories.getHistories();
            // 如果不包含，直接放到第一个
            if (curHistories.contains(history)) {
                curHistories.remove(history);
            }
            curHistories.add(0,history);
            list.addAll(curHistories.subList(0, Math.min(curHistories.size(), MAX_SEARCH_COUNT)));
        }
        Histories curHis = new Histories();
        curHis.setHistories(list);
        mJsonCacheUtils.saveCache(KEY_HISTORIES, curHis);
    }



    @Override
    public void doSearch(String keyword) {
        if (mCurKeyword == null || !mCurKeyword.equals(keyword)) {
            this.mCurKeyword = keyword;
        }
        if (searchViewCallBack != null && mPage == 0) {
            searchViewCallBack.Loading();
        }
        // 保存历史
        saveHistories(keyword);
        Call<SearchResult> task = mApi.getSearchResult(mPage, this.mCurKeyword);
        task.enqueue(new Callback<SearchResult>() {
            @Override
            public void onResponse(Call<SearchResult> call, Response<SearchResult> response) {
                LogUtils.d(this, "do search response -> " + response.code());
                if (response.code() == HTTP_OK) {
                    handleResult(response.body());
                } else {
                    responseNetError();
                }
            }

            @Override
            public void onFailure(Call<SearchResult> call, Throwable t) {
                responseNetError();
            }
        });
    }

    private void handleResult(SearchResult body) {
        if (isResultEmpty(body)) {
            responseEmpty();
        } else {
            searchResponseSuccess(body);
        }
    }

    private boolean isResultEmpty(SearchResult body) {
        try {
            return body == null || body.getData().getTbk_dg_material_optional_response().getResult_list().getMap_data().size() == 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 当获取成功的时候
     * @param body
     */
    private void searchResponseSuccess(SearchResult body) {
        if (searchViewCallBack != null) {
            if (mPage > 0) {
                searchViewCallBack.onMoreLoaded(body);
            } else {
                searchViewCallBack.onSearchSuccess(body);
            }
        }
    }

    @Override
    public void reSearch() {
        load();
    }

    @Override
    public void loadMore() {
        mPage ++;
        load();
    }

    private void load() {
        if (mCurKeyword == null) {
            searchViewCallBack.onEmpty();
        } else {
            this.doSearch(this.mCurKeyword);
        }
    }



    @Override
    public void getRecommend() {
        Call<RecommendResult> task = mApi.getRecommendResult();
        task.enqueue(new Callback<RecommendResult>() {
            @Override
            public void onResponse(Call<RecommendResult> call, Response<RecommendResult> response) {
                LogUtils.d(this, "recommend response -> " + response.code());
                if (response.code() == HTTP_OK) {
                    RecommendResult body = response.body();
                    if (body.getData() == null || body.getData().size() == 0) {
                        responseEmpty();
                    } else {
                        recommendSuccess(body.getData());
                    }
                } else {
                    responseEmpty();
                }
            }

            @Override
            public void onFailure(Call<RecommendResult> call, Throwable t) {
                responseNetError();
            }
        });
    }

    private void responseEmpty() {
        if (searchViewCallBack != null) {
            if (mPage > 0) {
                mPage --;
                searchViewCallBack.onMoreLoadedEmpty();
            } else {
                searchViewCallBack.onEmpty();
            }
        }
    }

    private void responseNetError() {
        if (searchViewCallBack != null) {
            if (mPage > 0) {
                mPage --;
                searchViewCallBack.onMoreLoadedError();
            } else {
                searchViewCallBack.onNetError();
            }
        }
    }

    /**
     *
     * @param body
     */
    private void recommendSuccess(List<RecommendResult.DataBean> body) {
        searchViewCallBack.onRecommendLoaded(body);
    }

    @Override
    public void registerCallback(ISearchViewCallBack callback) {
        this.searchViewCallBack = callback;
    }

    @Override
    public void unregisterCallback(ISearchViewCallBack callback) {
        if (this.searchViewCallBack == callback) {
            this.searchViewCallBack = null;
        }
    }
}

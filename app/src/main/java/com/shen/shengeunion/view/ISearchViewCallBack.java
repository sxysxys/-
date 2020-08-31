package com.shen.shengeunion.view;

import com.shen.shengeunion.base.IBaseCallback;
import com.shen.shengeunion.model.domain.SearchResult;
import com.shen.shengeunion.model.domain.Histories;
import com.shen.shengeunion.model.domain.RecommendResult;

import java.util.List;

public interface ISearchViewCallBack extends IBaseCallback {

    /**
     * 拿到相应的历史数据
     */
    void onHistoryLoad(Histories histories);

    /**
     * 删除历史
     */
    void onHistoriesDel();

    /**
     * 搜索成功
     */
    void onSearchSuccess(SearchResult result);

    /**
     * 重新搜索成功
     */
    void onReSearchSuccess(SearchResult result);

    /**
     * 加载更多
     * @param result
     */
    void onMoreLoaded(SearchResult result);

    /**
     * 加载更多失败
     */
    void onMoreLoadedError();

    /**
     * 加载更多为空
     */
    void onMoreLoadedEmpty();


    /**
     * 推荐加载成功
     */
    void onRecommendLoaded(List<RecommendResult.DataBean> recommends);

}

package com.shen.shengeunion.presenter;

import com.shen.shengeunion.base.IBasePresenter;
import com.shen.shengeunion.view.ISearchViewCallBack;

public interface ISearchPresenter extends IBasePresenter<ISearchViewCallBack> {

    /**
     * 拿到搜索历史
     */
    void getHistories();

    /**
     * 删除搜索历史
     */
    void delHistories();

    /**
     * 进行搜索
     * @param keyword
     */
    void doSearch(String keyword);

    /**
     * 重新搜索
     */
    void reSearch();

    /**
     * 获取更多
     */
    void loadMore();

    /**
     * 获取热门搜索
     */
    void getRecommend();

}

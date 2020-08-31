package com.shen.shengeunion.presenter;

import com.shen.shengeunion.base.IBasePresenter;
import com.shen.shengeunion.view.IOnSellPageCallback;

public interface IOnSellPagePresenter extends IBasePresenter<IOnSellPageCallback> {
    /**
     * 去拿到相应的列表
     */
    void getOnSellContent();

    /**
     * 重新加载
     */
    void reload();

    /**
     * 加载更多
     */
    void loaderMore();
}

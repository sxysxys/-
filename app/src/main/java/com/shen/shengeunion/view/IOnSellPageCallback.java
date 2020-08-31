package com.shen.shengeunion.view;

import com.shen.shengeunion.base.IBaseCallback;
import com.shen.shengeunion.model.domain.OnSellContent;

public interface IOnSellPageCallback extends IBaseCallback {

    /**
     * 成功加载数据
     * @param dataBean
     */
    void onContentLoaded(OnSellContent.DataBean dataBean);

    /**
     * 加载更多的数据返回
     * @param dataBean
     */
    void onMoreLoader(OnSellContent.DataBean dataBean);

    /**
     * 加载更多的时候出错
     */
    void onMoreLoadedError();

    /**
     * 加载更多的时候数据为空
     */
    void onMoreLoadedEmpty();
}

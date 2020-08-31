package com.shen.shengeunion.view;

import com.shen.shengeunion.base.IBaseCallback;
import com.shen.shengeunion.model.domain.HomePagerContent;
import com.shen.shengeunion.model.domain.IItemInfo;

import java.util.List;

public interface ICategoryPagerCallBack extends IBaseCallback {
    /**
     * 当加载好的时候
     * @param contents
     */
    void onContentLoad(List<HomePagerContent.DataBean> contents);

    /**
     * 拿到相应的id
     * @return
     */
    int getCategoryId();

    /**
     * 当轮播图加载好的时候
     * @param contents
     */
    void onLoopListLoad(List<HomePagerContent.DataBean> contents);

    /**
     * 加载更多时候出现错误
     */
    void onLoadMoreError();

    void onLoadMoreEmpty();

    void onLoadMoreLoaded(List<HomePagerContent.DataBean> contents);

}

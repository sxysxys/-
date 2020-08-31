package com.shen.shengeunion.presenter;

import com.shen.shengeunion.base.IBasePresenter;
import com.shen.shengeunion.model.domain.SelectedPageCategories;
import com.shen.shengeunion.view.ISelectedPageCallback;

public interface ISelectedPagePresenter extends IBasePresenter<ISelectedPageCallback> {
    /**
     * 获取精选分类
     */
    void getCategories();

    /**
     * 通过分类id获取相应的内容
     */
    void getContentById(SelectedPageCategories.DataBean dataBean);

    /**
     * 重新加载内容
     */
    void reloadContent();
}

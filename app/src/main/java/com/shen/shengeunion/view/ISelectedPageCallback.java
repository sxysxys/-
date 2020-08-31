package com.shen.shengeunion.view;

import com.shen.shengeunion.base.IBaseCallback;
import com.shen.shengeunion.model.domain.SelectedContent;
import com.shen.shengeunion.model.domain.SelectedPageCategories;

public interface ISelectedPageCallback extends IBaseCallback {
    /**
     * 当数据请求回来的回调接口
     * @param categories
     */
    void onCategoriesLoad(SelectedPageCategories categories);


    /**
     * 当通过id请求返回的内容
     */
    void onContentLoad(SelectedContent content);
}

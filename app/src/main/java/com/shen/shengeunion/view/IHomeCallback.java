package com.shen.shengeunion.view;

import com.shen.shengeunion.base.IBaseCallback;
import com.shen.shengeunion.model.domain.Categories;

/**
 * 观察者模式回调接口
 */
public interface IHomeCallback extends IBaseCallback {
    void onCategoriesLoaded(Categories categories);
}

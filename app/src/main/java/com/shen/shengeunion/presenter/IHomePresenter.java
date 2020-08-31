package com.shen.shengeunion.presenter;

import com.shen.shengeunion.base.IBasePresenter;
import com.shen.shengeunion.view.IHomeCallback;

/**
 * 使用观察者模式
 */
public interface IHomePresenter extends IBasePresenter<IHomeCallback> {
    void getCategories();
}

package com.shen.shengeunion.presenter;

import com.shen.shengeunion.base.IBasePresenter;
import com.shen.shengeunion.view.ICategoryPagerCallBack;

public interface ICategoryPagerPresenter extends IBasePresenter<ICategoryPagerCallBack> {
    void getContentByCategoryId(int categoryId);

    void loadMore(int categoryId);
}

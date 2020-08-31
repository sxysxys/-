package com.shen.shengeunion.presenter;

import com.shen.shengeunion.base.IBasePresenter;
import com.shen.shengeunion.view.ITicketPagerCallBack;

public interface ITicketPresenter extends IBasePresenter<ITicketPagerCallBack> {
    /**
     * 去获取相应的淘口令
     * @param title
     * @param url
     * @param cover
     */
     void getTicket(String title, String url, String cover);
}

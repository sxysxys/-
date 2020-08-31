package com.shen.shengeunion.view;

import com.shen.shengeunion.base.IBaseCallback;
import com.shen.shengeunion.model.domain.TicketResult;

public interface ITicketPagerCallBack extends IBaseCallback {

    void onTicketLoaded(String cover, TicketResult ticketResult);
}

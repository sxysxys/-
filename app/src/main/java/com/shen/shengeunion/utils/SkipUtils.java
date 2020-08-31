package com.shen.shengeunion.utils;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.shen.shengeunion.presenter.ISkipInfo;
import com.shen.shengeunion.presenter.ITicketPresenter;
import com.shen.shengeunion.presenter.impl.TicketPresenterImpl;
import com.shen.shengeunion.ui.activity.TicketActivity;

import static com.shen.shengeunion.base.BaseApplication.getContext;

public class SkipUtils {
    public static void skipPage(Context context, ISkipInfo info) {
        String title = info.getTitle();
        String clickUrl = info.getClick_url();
        String pictUrl = info.getPict_url();
        String couponUrl = info.getCoupon_click_url();
        // 如果存在领劵码，就拿到相应的劵。
        if (!TextUtils.isEmpty(couponUrl)) {
            clickUrl = couponUrl;
        }
        ITicketPresenter ticketPresenter = TicketPresenterImpl.getInstance();
        ticketPresenter.getTicket(title,clickUrl,pictUrl);
        context.startActivity(new Intent(getContext(), TicketActivity.class));
    }
}

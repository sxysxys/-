package com.shen.shengeunion.presenter;

/**
 * 实现页面跳转的接口
 */
public interface ISkipInfo {
    String getTitle();

    String getClick_url();

    String getPict_url();

    /**
     * 看是否有优惠券
     * @return
     */
    String getCoupon_click_url();
}

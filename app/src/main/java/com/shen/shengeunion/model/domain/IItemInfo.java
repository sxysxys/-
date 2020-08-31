package com.shen.shengeunion.model.domain;

import com.shen.shengeunion.presenter.ISkipInfo;

public interface IItemInfo extends ISkipInfo {

    /**
     * 获取原价
     * @return
     */
    String getZk_final_price();

    /**
     * 获取优惠价格
     * @return
     */
    long getCoupon_amount();

    /**
     * 获取销量
     * @return
     */
    long getVolume();
}

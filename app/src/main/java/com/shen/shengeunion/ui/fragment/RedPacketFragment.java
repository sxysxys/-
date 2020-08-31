package com.shen.shengeunion.ui.fragment;

import android.content.Intent;
import android.graphics.Rect;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.lcodecore.tkrefreshlayout.RefreshListenerAdapter;
import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout;
import com.shen.shengeunion.R;
import com.shen.shengeunion.base.BaseFragment;
import com.shen.shengeunion.model.domain.OnSellContent;
import com.shen.shengeunion.presenter.IOnSellPagePresenter;
import com.shen.shengeunion.presenter.ISkipInfo;
import com.shen.shengeunion.presenter.ITicketPresenter;
import com.shen.shengeunion.presenter.impl.OnSellPagePresenterImpl;
import com.shen.shengeunion.presenter.impl.TicketPresenterImpl;
import com.shen.shengeunion.ui.activity.TicketActivity;
import com.shen.shengeunion.ui.adapter.OnSellAdapter;
import com.shen.shengeunion.utils.LogUtils;
import com.shen.shengeunion.utils.SizeUtils;
import com.shen.shengeunion.utils.SkipUtils;
import com.shen.shengeunion.utils.ToastUtils;
import com.shen.shengeunion.view.IOnSellPageCallback;

import java.util.List;

import butterknife.BindView;

public class RedPacketFragment extends BaseFragment implements IOnSellPageCallback, OnSellAdapter.OnSellItemListener {

    private IOnSellPagePresenter onSellPagePresenter;

    private static final int DEFAULT_ROW = 2;

    @BindView(R.id.on_sell_content_list)
    public RecyclerView sellList;

    @BindView(R.id.on_sell_refresh)
    public TwinklingRefreshLayout refreshLayout;

    @BindView(R.id.bar_text)
    public TextView barText;

    private OnSellAdapter mOnSellAdapter;
    private ITicketPresenter ticketPresenter;

    @Override
    protected int getViewId() {
        return R.layout.red_packet_fragment;
    }

    @Override
    protected int getBaseId() {
        return R.layout.base_fragment_onsell_layout;
    }

    @Override
    protected void initView(View view) {
        barText.setText("特惠宝贝");
        sellList.setLayoutManager(new GridLayoutManager(getContext(),DEFAULT_ROW));
        mOnSellAdapter = new OnSellAdapter();
        sellList.setAdapter(mOnSellAdapter);
        sellList.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
//                super.getItemOffsets(outRect, view, parent, state);
                outRect.top = SizeUtils.dip2px(getContext(), 2.5f);
                outRect.bottom = SizeUtils.dip2px(getContext(), 2.5f);
                outRect.right = SizeUtils.dip2px(getContext(), 2.5f);
                outRect.left = SizeUtils.dip2px(getContext(), 2.5f);
            }
        });

        refreshLayout.setEnableLoadmore(true);
        refreshLayout.setEnableRefresh(false);
        refreshLayout.setEnableOverScroll(true);
    }

    @Override
    protected void initListener() {
        super.initListener();
        refreshLayout.setOnRefreshListener(new RefreshListenerAdapter() {
            @Override
            public void onLoadMore(TwinklingRefreshLayout refreshLayout) {
            if (onSellPagePresenter != null) {
                onSellPagePresenter.loaderMore();
            }
            }
        });
        mOnSellAdapter.setListener(this);
    }

    @Override
    protected void initPresent() {
        super.initPresent();
        onSellPagePresenter = OnSellPagePresenterImpl.getInstance();
        onSellPagePresenter.registerCallback(this);
    }

    @Override
    protected void loadData() {
        super.loadData();
        onSellPagePresenter.getOnSellContent();
    }

    @Override
    protected void onRetryClick() {
        super.onRetryClick();
        onSellPagePresenter.getOnSellContent();
    }

    /**
     * 第一次页面回调
     * @param dataBean
     */
    @Override
    public void onContentLoaded(OnSellContent.DataBean dataBean) {
        List<OnSellContent.DataBean.TbkDgOptimusMaterialResponseBean.ResultListBean.MapDataBean> data = dataBean.getTbk_dg_optimus_material_response().getResult_list().getMap_data();
        setCurState(State.SUCCESS);
        LogUtils.d(this, "content loaded -> " + data);
        mOnSellAdapter.setData(data);
    }

    @Override
    public void onMoreLoader(OnSellContent.DataBean dataBean) {
        refreshLayout.finishLoadmore();
        List<OnSellContent.DataBean.TbkDgOptimusMaterialResponseBean.ResultListBean.MapDataBean> map_data = dataBean.getTbk_dg_optimus_material_response().getResult_list().getMap_data();
        mOnSellAdapter.addData(map_data);
        ToastUtils.showToast("加载了" + map_data.size() + "条数据");
    }

    @Override
    public void onMoreLoadedError() {
        refreshLayout.finishLoadmore();
        ToastUtils.showToast("加载数据失败，请重试..");
    }

    @Override
    public void onMoreLoadedEmpty() {
        refreshLayout.finishLoadmore();
        ToastUtils.showToast("已经到底了..");
    }

    @Override
    public void onNetError() {
        setCurState(State.NET_ERROR);
    }

    @Override
    public void onEmpty() {
        setCurState(State.EMPTY);
    }

    @Override
    public void Loading() {
        setCurState(State.LOADING);
    }

    @Override
    public void onSellItemClick(ISkipInfo mapDataBean) {
        SkipUtils.skipPage(getContext(), mapDataBean);
    }
}

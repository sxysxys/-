package com.shen.shengeunion.ui.fragment;

import android.graphics.Rect;
import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.lcodecore.tkrefreshlayout.RefreshListenerAdapter;
import com.lcodecore.tkrefreshlayout.TbNestedScrollView;
import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout;
import com.shen.shengeunion.R;
import com.shen.shengeunion.base.BaseFragment;
import com.shen.shengeunion.model.domain.Categories;
import com.shen.shengeunion.model.domain.HomePagerContent;
import com.shen.shengeunion.model.domain.IItemInfo;
import com.shen.shengeunion.presenter.ICategoryPagerPresenter;
import com.shen.shengeunion.presenter.ISkipInfo;
import com.shen.shengeunion.presenter.ITicketPresenter;
import com.shen.shengeunion.presenter.impl.CategoryPagerPresenterImpl;
import com.shen.shengeunion.ui.adapter.LinearItemAdapter;
import com.shen.shengeunion.ui.adapter.LoopPagerAdapter;
import com.shen.shengeunion.ui.custom.LoopViewPager;
import com.shen.shengeunion.utils.Constants;
import com.shen.shengeunion.utils.LogUtils;
import com.shen.shengeunion.utils.SkipUtils;
import com.shen.shengeunion.utils.ToastUtils;
import com.shen.shengeunion.view.ICategoryPagerCallBack;

import java.util.List;

import butterknife.BindView;

/**
 * 首页切换不同分类对应的fragment，FragmentPagerAdapter返回
 */
public class HomePagerFragment extends BaseFragment implements ICategoryPagerCallBack, LinearItemAdapter.OnItemClickListener, LoopPagerAdapter.OnImageClickListener {

    private ICategoryPagerPresenter mpresenter;
    private int id;

    @BindView(R.id.home_pager_content_list)
    public RecyclerView mrecyclerView;

    @BindView(R.id.home_view_pager)
    public LoopViewPager mViewPager;

    @BindView(R.id.text_recommend)
    public TextView textView;

    @BindView(R.id.looper_point_container)
    public LinearLayout linearLayout;

    @BindView(R.id.twk_layout)
    public TwinklingRefreshLayout refreshLayout;

    @BindView(R.id.home_pager_parent)
    public LinearLayout parentLayout;

    @BindView(R.id.home_pager_header_container)
    public LinearLayout homeHeaderContainer;

    @BindView(R.id.tb_nested_view)
    public TbNestedScrollView nestedScrollView;


    private LinearItemAdapter contentAdapter;
    private LoopPagerAdapter loopPagerAdapter;
    private ITicketPresenter ticketPresenter;

    /**
     * 返回相应的带有数据的fragment
     * @return
     */
    public static HomePagerFragment newInstance(Categories.DataBean dataBean) {
        HomePagerFragment homePagerFragment = new HomePagerFragment();
        Bundle bundle = new Bundle();
        bundle.putString(Constants.KEY_HOME_PAGER_TITLE,dataBean.getTitle());
        bundle.putInt(Constants.KEY_HOME_PAGER_MATERIAL_ID,dataBean.getId());
        homePagerFragment.setArguments(bundle);
        return homePagerFragment;
    }

    @Override
    protected int getViewId() {
        return R.layout.fragment_home_pager;
    }


    @Override
    protected void initView(View view) {
        // 设置布局管理器
        mrecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mrecyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                outRect.top = 8;
                outRect.bottom = 8;
            }
        });
        // 设置适配器
        contentAdapter = new LinearItemAdapter();
        // 添加适配器
        mrecyclerView.setAdapter(contentAdapter);
        // 设置轮播图适配器
        loopPagerAdapter = new LoopPagerAdapter();
        mViewPager.setAdapter(loopPagerAdapter);
        // 设置刷新
        refreshLayout.setAutoLoadMore(true);
        refreshLayout.setEnableRefresh(false);
    }

    /**
     * 在页面可见的时候
     */
    @Override
    public void onResume() {
        super.onResume();
        mViewPager.startLoop();
    }

    @Override
    public void onPause() {
        super.onPause();
        mViewPager.stopLoop();
    }

    @Override
    protected void initPresent() {
        mpresenter = CategoryPagerPresenterImpl.getInstance();
        mpresenter.registerCallback(this);
    }

    @Override
    protected void initListener() {
        loopPagerAdapter.setOnImageClickListener(this);
        contentAdapter.setOnItemClickListener(this);
        // 设置一下布局时候的监听器，如果布局的时候就会调用这个方法
        parentLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (homeHeaderContainer == null) {
                    return;
                }
                int parentHeight = parentLayout.getMeasuredHeight();
                int headerHeight = homeHeaderContainer.getMeasuredHeight();
                LogUtils.d(HomePagerFragment.this, "parentHeight -> " + parentHeight);
                nestedScrollView.setHeaderHeight(headerHeight);

                LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) mrecyclerView.getLayoutParams();
                layoutParams.height = parentHeight;
                mrecyclerView.setLayoutParams(layoutParams);

                if (parentHeight != 0) {
                    parentLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }
            }
        });

        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (loopPagerAdapter.getSize() == 0) {
                    return;
                }
                position = position % loopPagerAdapter.getSize();
                updateLoopIndicator(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        // 当加载更多的时候触发
        refreshLayout.setOnRefreshListener(new RefreshListenerAdapter() {
            @Override
            public void onLoadMore(TwinklingRefreshLayout refreshLayout) {
                LogUtils.d(HomePagerFragment.this, "加载更多中");
                // 延迟加载
                // 3s后结束刷新
//                refreshLayout.postDelayed(refreshLayout::finishLoadmore,3000);
                if (mpresenter != null) {
                    mpresenter.loadMore(id);
                }
            }
        });
    }

    private void updateLoopIndicator(int position) {
        for (int i = 0; i < linearLayout.getChildCount(); i++) {
            View point = linearLayout.getChildAt(i);
            if (i == position) {
                point.setBackgroundResource(R.drawable.item_recommend_point);
            } else {
                point.setBackgroundResource(R.drawable.item_recommend_normal_point);
            }
        }
    }

    /**
     * 初始化的时候加载数据
     */
    @Override
    protected void loadData() {
        Bundle arguments = getArguments();
        String title = arguments.getString(Constants.KEY_HOME_PAGER_TITLE);
        id = arguments.getInt(Constants.KEY_HOME_PAGER_MATERIAL_ID);
        LogUtils.i(this,"title" + title);
        LogUtils.i(this,"id" + id);
        textView.setText(title);
        // 加载数据
        if (mpresenter != null) {
            mpresenter.getContentByCategoryId(id);
        }
    }

    /**
     * 当加载成功的时候回调
     * @param contents
     */
    @Override
    public void onContentLoad(List<HomePagerContent.DataBean> contents) {
        for (IItemInfo content : contents) {
            LogUtils.i(this,content.toString());
        }
        contentAdapter.setData(contents);
        setCurState(State.SUCCESS);
    }

    @Override
    public int getCategoryId() {
        return this.id;
    }

    /**
     * 轮播图回调
     * @param contents
     */
    @Override
    public void onLoopListLoad(List<HomePagerContent.DataBean> contents) {
        LogUtils.d(this,"loopList ----->  " + contents.size());
        // 设置成中间位置，可以无限轮播
        if (mViewPager == null) {
            return;
        }
        mViewPager.setCurrentItem((Integer.MAX_VALUE / 2) - (Integer.MAX_VALUE / 2) % contents.size());
        // 轮播图数据的set
        loopPagerAdapter.setData(contents);
        // 设置小圆点
        linearLayout.removeAllViews();
//        GradientDrawable selectPoint = (GradientDrawable)getContext().getDrawable(R.drawable.item_recommend_point);
//        GradientDrawable normalPoint = (GradientDrawable)getContext().getDrawable(R.drawable.item_recommend_point);
//        normalPoint.setColor(getResources().getColor(R.color.white));
        for (int i = 0; i < contents.size(); i++) {
            View point = new View(getContext());
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(8, 8);
            layoutParams.leftMargin = 5;
            layoutParams.rightMargin = 5;
            point.setLayoutParams(layoutParams);
            if (i == 0) {
                point.setBackgroundResource(R.drawable.item_recommend_point);
            } else {
                point.setBackgroundResource(R.drawable.item_recommend_normal_point);
            }
            linearLayout.addView(point);
        }
    }

    /**
     * 网络失败回调
     */
    @Override
    public void onLoadMoreError() {
        ToastUtils.showToast("网络异常，请稍后重试...");
        refreshLayout.finishLoadmore();
    }

    /**
     * 加载为空回调
     */
    @Override
    public void onLoadMoreEmpty() {
        ToastUtils.showToast("已经加载到底部了...");
        refreshLayout.finishLoadmore();
    }

    /**
     * 加载更多成功时回调
     * @param contents
     */
    @Override
    public void onLoadMoreLoaded(List<HomePagerContent.DataBean> contents) {
        LogUtils.d(this, "onLoadMoreLoaded -->  回来了");
        // 此时将请求的下一批数据添加到此时底边栏的底部
        contentAdapter.addData(contents);
        refreshLayout.finishLoadmore();
//        Toast.makeText(getContext(), "加载了" + contents.size() + "条记录",Toast.LENGTH_SHORT).show();
        ToastUtils.showToast("加载了" + contents.size() + "条数据");
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
    protected void release() {
        if (mpresenter != null) {
            mpresenter.unregisterCallback(this);
        }
    }

    /**
     * 回调
     * @param dataBean
     */
    @Override
    public void onItemClick(ISkipInfo dataBean) {
        LogUtils.d(this,"data ->" + dataBean.getTitle());
        handleItemClick(dataBean);
    }

    private void handleItemClick(ISkipInfo dataBean) {
        SkipUtils.skipPage(getContext(), dataBean);
    }

    @Override
    public void onImageClick(IItemInfo dataBean) {
        LogUtils.d(this,"image -> " + dataBean.getTitle());
    }
}

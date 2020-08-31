package com.shen.shengeunion.ui.fragment;

import android.content.Intent;
import android.graphics.Rect;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Size;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.shen.shengeunion.R;
import com.shen.shengeunion.base.BaseFragment;
import com.shen.shengeunion.model.domain.SelectedContent;
import com.shen.shengeunion.model.domain.SelectedPageCategories;
import com.shen.shengeunion.presenter.ISelectedPagePresenter;
import com.shen.shengeunion.presenter.ISkipInfo;
import com.shen.shengeunion.presenter.ITicketPresenter;
import com.shen.shengeunion.presenter.impl.SelectedPagePresenterImpl;
import com.shen.shengeunion.presenter.impl.TicketPresenterImpl;
import com.shen.shengeunion.ui.activity.TicketActivity;
import com.shen.shengeunion.ui.adapter.LeftListAdapter;
import com.shen.shengeunion.ui.adapter.RightListContentAdapter;
import com.shen.shengeunion.utils.LogUtils;
import com.shen.shengeunion.utils.SizeUtils;
import com.shen.shengeunion.utils.SkipUtils;
import com.shen.shengeunion.view.ISelectedPageCallback;

import java.util.List;

import butterknife.BindView;

public class SelectedFragment extends BaseFragment implements ISelectedPageCallback, LeftListAdapter.OnClickLeftListener, RightListContentAdapter.OnRightClickListener {

    @BindView(R.id.recycle_category)
    public RecyclerView leftList;

    @BindView(R.id.recycle_content)
    public RecyclerView rightContentList;

    @BindView(R.id.bar_text)
    public TextView barText;

    private ISelectedPagePresenter mSelectPresenter;
    private LeftListAdapter mLeftListAdapter;
    private RightListContentAdapter mRightListContentAdapter;


    @Override
    protected int getViewId() {
        return R.layout.selected_fragment;
    }

    @Override
    protected int getBaseId() {
        return R.layout.base_fragment_onsell_layout;
    }

    @Override
    protected void initView(View view) {
        barText.setText("精选宝贝");
        leftList.setLayoutManager(new LinearLayoutManager(getContext()));
        mLeftListAdapter = new LeftListAdapter();
        leftList.setAdapter(mLeftListAdapter);

        rightContentList.setLayoutManager(new LinearLayoutManager(getContext()));
        mRightListContentAdapter = new RightListContentAdapter();
        rightContentList.setAdapter(mRightListContentAdapter);
        rightContentList.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                outRect.top = SizeUtils.dip2px(getContext(), 4);;
                outRect.bottom = SizeUtils.dip2px(getContext(), 4);;
                outRect.left = SizeUtils.dip2px(getContext(), 6);;
                outRect.right = SizeUtils.dip2px(getContext(), 6);;
            }
        });
    }

    @Override
    protected void release() {
        super.release();
        if (mSelectPresenter != null) {
            mSelectPresenter.unregisterCallback(this);
        }
    }

    @Override
    protected void initPresent() {
        super.initPresent();
        mSelectPresenter = SelectedPagePresenterImpl.getInstance();
        mSelectPresenter.registerCallback(this);
        mSelectPresenter.getCategories();
    }

    @Override
    protected void initListener() {
        super.initListener();
        if (mLeftListAdapter != null) {
            mLeftListAdapter.setOnClickLeftListener(this);
        }
        if (mRightListContentAdapter != null) {
            mRightListContentAdapter.setRightClickListener(this);
        }
    }

    @Override
    public void onCategoriesLoad(SelectedPageCategories categories) {
        LogUtils.d(this,"categories -> " + categories);
        mLeftListAdapter.setData(categories);
        setCurState(State.SUCCESS);
        List<SelectedPageCategories.DataBean> data = categories.getData();
        // 去请求数据
        mSelectPresenter.getContentById(data.get(0));
    }

    /**
     * 拿到了内容相关的数据
     * @param content
     */
    @Override
    public void onContentLoad(SelectedContent content) {
        LogUtils.d(this, "content -> " + content);
        mRightListContentAdapter.setData(content);
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
    protected void onRetryClick() {
        if (mSelectPresenter != null) {
            mSelectPresenter.reloadContent();
        }
    }

    /**
     * 左边回调
     * @param dataBean
     */
    @Override
    public void onItemChanged(SelectedPageCategories.DataBean dataBean) {
        LogUtils.d(this, "title -> " + dataBean.getFavorites_title());
        mSelectPresenter.getContentById(dataBean);
    }

    /**
     * 右边回调
     * @param dataBean
     */
    @Override
    public void onClickItem(ISkipInfo dataBean) {
        SkipUtils.skipPage(getContext(), dataBean);
    }
}

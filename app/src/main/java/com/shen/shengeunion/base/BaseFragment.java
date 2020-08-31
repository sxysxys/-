package com.shen.shengeunion.base;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.shen.shengeunion.R;

import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public abstract class BaseFragment extends Fragment {

    private Unbinder mbind;
    private FrameLayout baseLayout;

    private State curState = State.NONE;
    private View memptyView;
    private View mloadingView;
    private View merrorView;
    private View msuccessView;

    public enum State {
        NONE,NET_ERROR,EMPTY,SUCCESS,LOADING;
    }

    // 设置点击事件
    @OnClick(R.id.net_work_error)
    public void retry() {
        // 子类知道应该做什么
        onRetryClick();
    }

    protected void onRetryClick() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // 这里必须为false，因为frameLayout后面会自动将这个view add进去，如果为true那么会add两次，会报错。
        View baseView = getLayout(inflater, container);
        baseLayout = baseView.findViewById(R.id.base_layout);
        // 在原先的fragment上加上一层状态层。
        initViewState(inflater,container);
        // 需要将页面和fragment进行绑定。
        mbind = ButterKnife.bind(this, baseView);
        initView(baseView);
        // 加载数据
        initPresent();
        initListener();
        loadData();
        return baseView;
    }

    protected void initListener() {

    }

    protected View getLayout(@NonNull LayoutInflater inflater, @Nullable ViewGroup container) {
        return inflater.inflate(getBaseId(), container, false);
    }

    /**
     * 将几个view添加进坑中
     * TODO:为何不能使用inflater.inflate(R.layout.base_empty_layout, baseLayout, true);
     */
    protected void initViewState(LayoutInflater inflater, ViewGroup container) {
        memptyView = inflater.inflate(R.layout.base_empty_layout, container,false);
        mloadingView = inflater.inflate(R.layout.base_loading_layout, container,false);
        merrorView = inflater.inflate(R.layout.net_error_layout, container,false);
        msuccessView = inflater.inflate(getViewId(), container,false);
        baseLayout.addView(memptyView);
        baseLayout.addView(mloadingView);
        baseLayout.addView(merrorView);
        baseLayout.addView(msuccessView);
        // 设置此时的state为空
        setCurState(State.NONE);
    }

    protected void setCurState(State state) {
        this.curState = state;
        memptyView.setVisibility(this.curState == State.EMPTY ? View.VISIBLE : View.GONE);
        msuccessView.setVisibility(this.curState == State.SUCCESS ? View.VISIBLE : View.GONE);
        merrorView.setVisibility(this.curState == State.NET_ERROR ? View.VISIBLE : View.GONE);
        mloadingView.setVisibility(this.curState == State.LOADING ? View.VISIBLE : View.GONE);
    }

    protected void initView(View view) {
    }

    /**
     * 由于不是每个fragment都有数据提供者，所以不定义成抽象方法
     */
    protected void initPresent() {

    }

    /**
     * 由于不是每个页面都要加载数据，所以不定义成抽象方法
     */
    protected void loadData() {

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mbind != null) {
            mbind.unbind();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        release();
    }

    /**
     * release也不是所有都需要
     */
    protected void release() {

    }

//    public void loadStateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        View successView = loadSuccessView(inflater, container, savedInstanceState);
//        baseLayout.addView(successView);
//    }
//
//    public View loadSuccessView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        return inflater.inflate(getViewId(),container,false);
//    }

    protected abstract int getViewId();

    protected int getBaseId() {
        return R.layout.base_fragment_layout;
    }
}

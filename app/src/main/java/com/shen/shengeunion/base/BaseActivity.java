package com.shen.shengeunion.base;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import butterknife.ButterKnife;
import butterknife.Unbinder;

public abstract class BaseActivity extends AppCompatActivity {

    private Unbinder bind;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getResId());
        bind = ButterKnife.bind(this);
        initFragments();
        initView();
        initPresenter();
        initListener();
    }

    protected abstract void initPresenter();

    protected void initListener() {

    }

    protected void initView() {

    }

    protected void initFragments() {

    }



    /**
     * 子类实现，拿到相应的资源id
     * @return
     */
    protected abstract int getResId();

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (bind != null) {
            bind.unbind();
        }
        this.release();
    }

    protected void release() {
    }
}

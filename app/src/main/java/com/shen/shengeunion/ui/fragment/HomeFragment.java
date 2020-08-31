package com.shen.shengeunion.ui.fragment;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.shen.shengeunion.R;
import com.shen.shengeunion.base.BaseFragment;
import com.shen.shengeunion.model.domain.Categories;
import com.shen.shengeunion.presenter.IHomePresenter;
import com.shen.shengeunion.presenter.impl.HomePresenterImpl;
import com.shen.shengeunion.ui.activity.IMainActivity;
import com.shen.shengeunion.ui.activity.MainActivity;
import com.shen.shengeunion.ui.activity.ScanCodeActivity;
import com.shen.shengeunion.ui.adapter.HomePagerAdapter;
import com.shen.shengeunion.view.IHomeCallback;
import com.vondear.rxfeature.activity.ActivityScanerCode;

import butterknife.BindView;

public class HomeFragment extends BaseFragment implements IHomeCallback {

    @BindView(R.id.home_indicator)
    public TabLayout mtabLayout;

    @BindView(R.id.search)
    EditText searchBox;

    private IHomePresenter homePresenter;

    @BindView(R.id.home_pager)
    public ViewPager mHomePager;

    @BindView(R.id.scan)
    public ImageView scanImage;

    private HomePagerAdapter mHomePagerAdapter;

    @Override
    protected int getViewId() {
        return R.layout.main_fragment;
    }

    /**
     * 初始化成功的界面
     * @param view
     */
    @Override
    protected void initView(View view) {
        // 关联上pager
        mtabLayout.setupWithViewPager(mHomePager);
        // 给viewPager设置适配器，getChildFragmentManager是拿到fragment子容器中的管理器。
        mHomePagerAdapter = new HomePagerAdapter(getChildFragmentManager());
        mHomePager.setAdapter(mHomePagerAdapter);
    }

    @Override
    protected void initPresent() {
        homePresenter = new HomePresenterImpl();
        homePresenter.registerCallback(this);
    }

    @Override
    protected View getLayout(@NonNull LayoutInflater inflater, @Nullable ViewGroup container) {
        return inflater.inflate(R.layout.base_main_fragment_layout, container, false);
    }

    @Override
    protected void initListener() {
        super.initListener();
        searchBox.setOnClickListener(v -> {
            FragmentActivity activity = getActivity();
            if (activity instanceof IMainActivity) {
                IMainActivity ac = (IMainActivity) activity;
                ac.switch2SearchPage();
            }
        });
        scanImage.setOnClickListener(v -> {
            // 跳转到扫描界面
            startActivity(new Intent(getContext(), ScanCodeActivity.class));
        });
    }

    @Override
    protected void loadData() {
        homePresenter.getCategories();
    }

    @Override
    public void onCategoriesLoaded(Categories categories) {
        // 加载的数据从这里回来
//        mHomePager.setOffscreenPageLimit(categories.getData().size());  // 如果设置了这个，会自动将所有pager页面都加载，正常默认是只加载两个
        mHomePagerAdapter.setCategories(categories);
        setCurState(State.SUCCESS);
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
        if (homePresenter != null) {
            homePresenter.unregisterCallback(this);
        }
    }

    /**
     * 网络错误的时候重新加载
     */
    @Override
    protected void onRetryClick() {
        if (homePresenter != null) {
            homePresenter.getCategories();
        }
    }
}

package com.shen.shengeunion.ui.activity;

import android.widget.FrameLayout;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.shen.shengeunion.R;
import com.shen.shengeunion.base.BaseActivity;
import com.shen.shengeunion.ui.fragment.HomeFragment;
import com.shen.shengeunion.ui.fragment.RedPacketFragment;
import com.shen.shengeunion.ui.fragment.SearchFragment;
import com.shen.shengeunion.ui.fragment.SelectedFragment;
import com.shen.shengeunion.utils.LogUtils;

import butterknife.BindView;

public class MainActivity extends BaseActivity implements IMainActivity{

    private static final String TAG = "MainActivity";

    @BindView(R.id.main_navigation)
    public BottomNavigationView nav;

    private HomeFragment homeFragment;
    private RedPacketFragment redPacketFragment;
    private SearchFragment searchFragment;
    private SelectedFragment selectedFragment;
    private FragmentManager fragmentManager;

    @Override
    protected void initFragments() {
        homeFragment = new HomeFragment();
        redPacketFragment = new RedPacketFragment();
        searchFragment = new SearchFragment();
        selectedFragment = new SelectedFragment();
        // 添加初始的fragment
        lastFragment = homeFragment;
    }

    @Override
    protected int getResId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initPresenter() {

    }

    @Override
    public void switch2SearchPage() {
//        switchFragment(searchFragment);
        // 使得底下的颜色也变化，这句话模拟了点击
        nav.setSelectedItemId(R.id.search);
    }

    @Override
    protected void initListener() {
        nav.setOnNavigationItemSelectedListener(item -> {
            if (item.getItemId() == R.id.home) {
                LogUtils.d(this, "切换到首页");
                switchFragment(homeFragment);
            } else if (item.getItemId() == R.id.selected) {
                LogUtils.d(this, "切换到精选");
                switchFragment(selectedFragment);
            } else if (item.getItemId() == R.id.red_packet) {
                LogUtils.d(this, "切换到特惠");
                switchFragment(redPacketFragment);
            } else if (item.getItemId() == R.id.search) {
                LogUtils.d(this, "切换到搜索");
                switchFragment(searchFragment);
            }
            return true;
        });
    }

    /**
     * 记录上一个页面
     */
    private Fragment lastFragment;

    private void switchFragment(Fragment homeFragment) {
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        if (!homeFragment.isAdded()) {
            transaction.add(R.id.main_page_container,homeFragment);
        } else {
            transaction.show(homeFragment);
        }
        if (lastFragment != null && lastFragment != homeFragment) {
            transaction.hide(lastFragment);
        }
        lastFragment = homeFragment;
        transaction.commit();
        // 采用简单暴力直接替换
//        fragmentManager.beginTransaction().replace(R.id.main_page_container, homeFragment).commit();
    }

    @Override
    protected void initView() {
        fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().add(R.id.main_page_container, homeFragment).commit();
    }

}
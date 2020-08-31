package com.shen.shengeunion.ui.adapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.shen.shengeunion.model.domain.Categories;
import com.shen.shengeunion.ui.fragment.HomePagerFragment;

import java.util.ArrayList;
import java.util.List;

public class HomePagerAdapter extends FragmentPagerAdapter {


    List<Categories.DataBean> categoriesList = new ArrayList<>();

    public HomePagerAdapter(@NonNull FragmentManager fm) {
        super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        HomePagerFragment homePagerFragment = HomePagerFragment.newInstance(categoriesList.get(position));
        return homePagerFragment;
    }

    @Override
    public int getCount() {
        return categoriesList.size();
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return categoriesList.get(position).getTitle();
    }

    public void setCategories(Categories categories) {
        categoriesList.clear();
        categoriesList.addAll(categories.getData());
        notifyDataSetChanged();
    }
}

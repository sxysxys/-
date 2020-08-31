package com.shen.shengeunion.ui.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.bumptech.glide.Glide;
import com.shen.shengeunion.model.domain.HomePagerContent;
import com.shen.shengeunion.model.domain.IItemInfo;
import com.shen.shengeunion.utils.UrlUtils;

import java.util.ArrayList;
import java.util.List;

public class LoopPagerAdapter extends PagerAdapter {

    List<IItemInfo> loopList = new ArrayList<>();
    private OnImageClickListener onImageClickListener;

    /**
     * 创建view送入container中返回
     * @param container
     * @param position
     * @return
     */
    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {

        int realPosition = position % loopList.size();

        // 拿到元素
        int measuredHeight = container.getMeasuredHeight();
        int measuredWidth = container.getMeasuredWidth();
        int ivSize = (measuredHeight > measuredWidth ? measuredHeight : measuredWidth) / 2;
        IItemInfo cur = loopList.get(realPosition);
        String path = UrlUtils.getCoverPath(cur.getPict_url(),ivSize);

        // 自定义控件
        ImageView imageView = new ImageView(container.getContext());
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        imageView.setLayoutParams(layoutParams);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        Glide.with(container.getContext()).load(path).into(imageView);
        // 添加事件
        imageView.setOnClickListener(v -> {
            if (this.onImageClickListener != null) {
                onImageClickListener.onImageClick(loopList.get(realPosition));
            }
        });

        // 将view添加进container中
        container.addView(imageView);
        return imageView;
    }

    public int getSize() {
        return loopList.size();
    }

    /**
     * 销毁的时候，将view从contain中移除
     * @param container
     * @param position
     * @param object
     */
    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }

    /**
     * 无限轮播
     * @return
     */
    @Override
    public int getCount() {
        return Integer.MAX_VALUE;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    public void setData(List<HomePagerContent.DataBean> contents) {
        loopList.clear();
        loopList.addAll(contents);
        notifyDataSetChanged();
    }

    public void setOnImageClickListener(OnImageClickListener listener) {
        this.onImageClickListener = listener;
    }

    public interface OnImageClickListener {
        void onImageClick(IItemInfo dataBean);
    }
}

package com.shen.shengeunion.ui.custom;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;

public class LoopViewPager extends ViewPager {

    private boolean isLoop = false;

    private static final int DEFAULT_DURATION = 3000;

    private int mDuration = DEFAULT_DURATION;

    public LoopViewPager(@NonNull Context context) {
        super(context);
    }

    public LoopViewPager(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * 开始循环
     */
    public void startLoop() {
        isLoop = true;
        // 用来执行方法
        post(mask);
    }

    Runnable mask = new Runnable() {
        @Override
        public void run() {
            int currentItem = getCurrentItem();
            currentItem ++;
            setCurrentItem(currentItem);
            if (isLoop) {
                postDelayed(this,mDuration);
            }
        }
    };

    public void setmDuration(int mDuration) {
        this.mDuration = mDuration;
    }

    public void stopLoop() {
        isLoop = false;
        removeCallbacks(mask);
    }
}

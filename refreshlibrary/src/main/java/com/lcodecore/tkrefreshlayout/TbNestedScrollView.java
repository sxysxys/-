package com.lcodecore.tkrefreshlayout;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.RecyclerView;


/**
 * 继承NestScrollView，实现其中的方法，解决滑动冲突问题
 */
public class TbNestedScrollView extends NestedScrollView {

    private int originScroll = 0;
    private int headerHeight = 0;
    private RecyclerView recyclerView;

    public TbNestedScrollView(@NonNull Context context) {
        super(context);
    }

    public TbNestedScrollView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public TbNestedScrollView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void onNestedPreScroll(@NonNull View target, int dx, int dy, @NonNull int[] consumed, int type) {
//        LogUtils.d(this,"dyConsumed ->" + dy);
        if (this.originScroll < this.headerHeight) {
            // 没必要
            if (target instanceof RecyclerView) {
                recyclerView = (RecyclerView) target;
            }
            scrollBy(dx,dy);
            // 配置这个说明已经被消费了，也就是不会再让子view去滑动了。
            consumed[0] = dx;
            consumed[1] = dy;
        }
        super.onNestedPreScroll(target, dx, dy, consumed, type);
    }


    /**
     * 当本view进行滑动的时候会触发
     * @param l
     * @param t
     * @param oldl
     * @param oldt
     */
    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
//        LogUtils.d(this,"originScroll -> " + t);
        this.originScroll = t;
        super.onScrollChanged(l, t, oldl, oldt);
    }

    public void setHeaderHeight(int headerHeight) {
        this.headerHeight = headerHeight;
    }

    /**
     * 当判断的时候进行拦截，需要判断此时的recycleView有没有到底
     * @return
     */
    public boolean isBottom() {
        if (recyclerView != null) {
             return !recyclerView.canScrollVertically(1);
        }
        return false;
    }
}

package com.shen.shengeunion.ui.custom;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatImageView;

import com.shen.shengeunion.R;
import com.shen.shengeunion.utils.LogUtils;

/**
 * 自定义加载框图片
 */
public class LoadingView extends AppCompatImageView {

    private float mDegrees = 0f;

    private boolean mNeedRotate = true;

    public LoadingView(Context context) {
        this(context, null, 0);
    }

    public LoadingView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LoadingView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        // 添加动画，使得没加载出来的时候一直转圈
        setImageResource(R.mipmap.loading);
    }

    /**
     * 如果这个控件和窗口取消了绑定
     */
    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        stopRotate();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        mNeedRotate = true;
        startRotate();
    }

    private void stopRotate() {
        mNeedRotate = false;
    }

    private void startRotate() {
        post(new Runnable() {
            @Override
            public void run() {
                mDegrees += 10;
                if (mDegrees >= 360) {
                    mDegrees = 0;
                }
                // 重置一次view
                invalidate();
                if (getVisibility() != VISIBLE || !mNeedRotate) {
                    // 如果这个窗口变的不可见就不在转动了。
                    LogUtils.d(this, "run -> stop...");
                    removeCallbacks(this);
                } else {
//                    LogUtils.d(this, "run -> start...");
                    postDelayed(this, 10);
                }
            }
        });
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // 以半径来转。
        canvas.rotate(mDegrees, getWidth() / 2, getHeight() / 2);
        super.onDraw(canvas);
    }
}

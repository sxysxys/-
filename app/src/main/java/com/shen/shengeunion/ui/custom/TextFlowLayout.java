package com.shen.shengeunion.ui.custom;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.shen.shengeunion.R;
import com.shen.shengeunion.utils.LogUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 自定义搜索界面的控件
 */
public class TextFlowLayout extends ViewGroup {

    private static final int DEFAULT_DP = 10;

    private List<String> mHistories = new ArrayList<>();

    private int CUR_DP = DEFAULT_DP;
    private float mItemVerticalSpace = DEFAULT_DP;

    private int mSelfWidth;
    private int mItemHeight;
    private float mItemHorSpace = DEFAULT_DP;

    public int getCUR_DP() {
        return CUR_DP;
    }

    public void setCUR_DP(int CUR_DP) {
        this.CUR_DP = CUR_DP;
    }

    public TextFlowLayout(Context context) {
        this(context,null,0);
    }

    public TextFlowLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TextFlowLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        /**
         * 去拿相关的属性
         */
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.FlowTextStyle);
        mItemHorSpace = ta.getDimension(R.styleable.FlowTextStyle_horsize, CUR_DP);
        mItemVerticalSpace = ta.getDimension(R.styleable.FlowTextStyle_versize, CUR_DP);
        ta.recycle();
//        LogUtils.d(this, "mItemHorSpace -> " + mItemHorSpace);
//        LogUtils.d(this, "mItemVerSpace -> " + mItemVerSpace);
    }

    public void setItemViews(List<String> histories) {
        removeAllViews();
        this.mHistories = histories;
        for (String history : mHistories) {
            TextView historyText = (TextView) LayoutInflater.from(getContext()).inflate(R.layout.flow_text_view, this, false);
            historyText.setText(history);
            historyText.setOnClickListener(v -> {
                onItemClick.onFlowClick(history);
            });
            addView(historyText);
        }
    }

    public int getContentCount() {
        return mHistories.size();
    }

    /**
     * 摆放
     * @param changed
     * @param l
     * @param t
     * @param r
     * @param b
     */
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int topOffset = (int)mItemVerticalSpace;
        for (List<View> view : listViews) {
            int leftOffset = (int)mItemHorSpace;
            for (View v : view) {
                v.layout(leftOffset, topOffset, leftOffset + v.getMeasuredWidth(), topOffset + v.getMeasuredHeight());
                leftOffset += (mItemHorSpace + v.getMeasuredWidth());
            }
            topOffset += (getChildAt(0).getMeasuredHeight() + mItemVerticalSpace);
        }
    }

    private List<List<View>> listViews = new ArrayList<>();
    /**
     * 测量
     * @param widthMeasureSpec
     * @param heightMeasureSpec
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (getChildCount() == 0) {
            return;
        }
        listViews.clear();
        mSelfWidth = MeasureSpec.getSize(widthMeasureSpec) - getPaddingLeft() - getPaddingRight();
        LogUtils.d(this, "width -> " + mSelfWidth);
        int childCount = getChildCount();
        // 对每一个孩子进行测量
        List<View> line = null;
        for (int i = 0; i < childCount; i++) {
            View childView = getChildAt(i);
            if (childView.getVisibility() != VISIBLE) {
                continue;
            }
            // 测量孩子
            measureChild(childView, widthMeasureSpec, heightMeasureSpec);
            LogUtils.d(this,"after height -- > " + childView.getMeasuredHeight());
            if (listViews.size() == 0) {
                line = createLine(childView);
            } else {
                // 判断是否能添加进这一行了
                if (canAdd(childView,line)) {
                    line.add(childView);
                } else {
                    line = createLine(childView);
                }
            }
        }
        mItemHeight = getChildAt(0).getMeasuredHeight();
        // 测量自己
        int parentHeight = (int)(listViews.size() * mItemHeight + mItemVerticalSpace * (listViews.size() + 1) + 0.5f);
        setMeasuredDimension(mSelfWidth, parentHeight);
    }

    /**
     * 判断能不能添加进去这个line中了
     * @param childView
     * @param line
     * @return
     */
    private boolean canAdd(View childView, List<View> line) {
        int total = childView.getMeasuredWidth();
        for (View view : line) {
            total += view.getMeasuredWidth();
        }
        total += mItemHorSpace * (line.size() + 1);
        return !(total > mSelfWidth);
    }

    private List<View> createLine(View childView) {
        List<View> line = new ArrayList<>();
        line.add(childView);
        listViews.add(line);
        return line;
    }

    private OnItemClick onItemClick;

    public OnItemClick getOnItemClick() {
        return onItemClick;
    }

    public void setOnItemClick(OnItemClick onItemClick) {
        this.onItemClick = onItemClick;
    }

    public interface OnItemClick {
        void onFlowClick(String tag);
    }
}

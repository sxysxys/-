package com.shen.shengeunion.ui.adapter;

import android.content.Context;
import android.graphics.Paint;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.shen.shengeunion.R;
import com.shen.shengeunion.model.domain.HomePagerContent;
import com.shen.shengeunion.model.domain.IItemInfo;
import com.shen.shengeunion.presenter.ISkipInfo;
import com.shen.shengeunion.utils.LogUtils;
import com.shen.shengeunion.utils.UrlUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 页面内容的适配器
 */
public class LinearItemAdapter extends RecyclerView.Adapter<LinearItemAdapter.InnerHolder> {

    private List<IItemInfo> dataList = new ArrayList<>();
    private OnItemClickListener onItemClickListener;

    @NonNull
    @Override
    public InnerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_home_pager_content, parent, false);
        LogUtils.d(this,"onCreateViewHolder 执行了");
        return new InnerHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull InnerHolder holder, int position) {
        LogUtils.d(this,"onBindViewHolder ->" + position);
        // 设置数据
        holder.setData(dataList.get(position));
        // 拿到相应的
        holder.itemView.setOnClickListener(v -> {
            if (this.onItemClickListener != null) {
                onItemClickListener.onItemClick(dataList.get(position));
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    // ? extends 代表你可以传，但是我不让你用
    public void setData(List<? extends IItemInfo> contents) {
        dataList.clear();
        dataList.addAll(contents);
        notifyDataSetChanged();
    }

    /**
     * 当加载更多的时候添加数据
     * @param contents
     */
    public void addData(List<? extends IItemInfo> contents) {
        int countPre = dataList.size();
        dataList.addAll(contents);
        notifyItemRangeChanged(countPre - 1, contents.size());
    }

    public class InnerHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.goods_cover)
        public ImageView goodsCover;

        @BindView(R.id.goods_title)
        public TextView goodsTitle;

        @BindView(R.id.goods_off_price)
        public TextView goodsOff;

        @BindView(R.id.goods_after_off_price)
        public TextView goodsAfterOff;

        @BindView(R.id.goods_origin_price)
        public TextView goodsOriginPrice;

        @BindView(R.id.sell_count)
        public TextView sellCount;

        public InnerHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }

        public void setData(IItemInfo dataBean) {
            Context context = itemView.getContext();
            long coupon_amount = dataBean.getCoupon_amount();
            String originPrice = dataBean.getZk_final_price();
            float finalResult = Float.parseFloat(originPrice) - coupon_amount;

            // 设置请求图片路径的图片大小宽度
//            ViewGroup.LayoutParams params = goodsCover.getLayoutParams();
//            int width = params.width;
//            int height = params.height;
//            int coverSize = (width > height ? width : height) / 2;

            // 设置属性
            goodsTitle.setText(dataBean.getTitle());
            String pict_url = dataBean.getPict_url();
            if (!TextUtils.isEmpty(pict_url)) {
                Glide.with(context).load(UrlUtils.getTicketUrl(pict_url)).into(goodsCover);
            } else {
                goodsCover.setImageResource(R.mipmap.ic_launcher);
            }
            goodsOff.setText(String.format(context.getString(R.string.goods_off_price_text), coupon_amount));
            goodsOriginPrice.setText(String.format(context.getString(R.string.goods_original_text),originPrice));
            // 设置划线
            goodsOriginPrice.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
            goodsAfterOff.setText(String.format(context.getString(R.string.goods_final_text),finalResult));
            sellCount.setText(String.format(context.getString(R.string.goods_sell_text),dataBean.getVolume()));
        }
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    public interface OnItemClickListener {
        void onItemClick(ISkipInfo dataBean);
    }
}

package com.shen.shengeunion.ui.adapter;

import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.shen.shengeunion.R;
import com.shen.shengeunion.model.domain.OnSellContent;
import com.shen.shengeunion.presenter.ISkipInfo;
import com.shen.shengeunion.utils.UrlUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class OnSellAdapter extends RecyclerView.Adapter<OnSellAdapter.InnerHolder> {

    private List<OnSellContent.DataBean.TbkDgOptimusMaterialResponseBean.ResultListBean.MapDataBean> dataBeans = new ArrayList<>();

    private OnSellItemListener listener;

    @NonNull
    @Override
    public OnSellAdapter.InnerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_on_sale_content, parent, false);
        return new InnerHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OnSellAdapter.InnerHolder holder, int position) {
        OnSellContent.DataBean.TbkDgOptimusMaterialResponseBean.ResultListBean.MapDataBean data = dataBeans.get(position);
        holder.setData(data);
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onSellItemClick(data);
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataBeans.size();
    }

    public void setData(List<OnSellContent.DataBean.TbkDgOptimusMaterialResponseBean.ResultListBean.MapDataBean> data) {
        dataBeans.clear();
        dataBeans.addAll(data);
        notifyDataSetChanged();
    }

    public void addData(List<OnSellContent.DataBean.TbkDgOptimusMaterialResponseBean.ResultListBean.MapDataBean> data) {
        int preSize = this.dataBeans.size();
        dataBeans.addAll(data);
        notifyItemRangeChanged(preSize - 1,data.size());
    }

    public class InnerHolder extends RecyclerView.ViewHolder{

        @BindView(R.id.image_content)
        ImageView imageContent;

        @BindView(R.id.on_sell_content)
        TextView onSellContent;

        @BindView(R.id.origin_price)
        TextView originPrice;

        @BindView(R.id.off_price)
        TextView offPrice;


        public InnerHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void setData(OnSellContent.DataBean.TbkDgOptimusMaterialResponseBean.ResultListBean.MapDataBean mapDataBean) {
            Glide.with(imageContent.getContext()).load(UrlUtils.getTicketUrl(mapDataBean.getPict_url())).into(imageContent);
            onSellContent.setText(mapDataBean.getTitle());
            String origin = mapDataBean.getZk_final_price();
            this.originPrice.setText("￥" + origin + " ");
            this.originPrice.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
            int couponAmount = mapDataBean.getCoupon_amount();
            float v = Float.parseFloat(origin);
            float finalPrice = v - couponAmount;
            this.offPrice.setText("劵后价: " + String.format("%.2f",finalPrice));
        }
    }

    public void setListener(OnSellItemListener listener) {
        this.listener = listener;
    }

    public interface OnSellItemListener {
        void onSellItemClick(ISkipInfo mapDataBean);
    }
}

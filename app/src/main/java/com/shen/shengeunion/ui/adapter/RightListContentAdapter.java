package com.shen.shengeunion.ui.adapter;

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
import com.shen.shengeunion.model.domain.SelectedContent;
import com.shen.shengeunion.presenter.ISkipInfo;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.shen.shengeunion.utils.Constants.SUCCESS_CODE;

public class RightListContentAdapter extends RecyclerView.Adapter<RightListContentAdapter.InnerHolder> {

    private List<SelectedContent.DataBean.TbkUatmFavoritesItemGetResponseBean.ResultsBean.UatmTbkItemBean> item = new ArrayList<>();

    private OnRightClickListener onRightClickListener;

    public void setRightClickListener(OnRightClickListener rightClickListener) {
        this.onRightClickListener = rightClickListener;
    }

    @NonNull
    @Override
    public RightListContentAdapter.InnerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_selected_page_content, parent, false);
        return new InnerHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RightListContentAdapter.InnerHolder holder, int position) {
        SelectedContent.DataBean.TbkUatmFavoritesItemGetResponseBean.ResultsBean.UatmTbkItemBean data = item.get(position);
        holder.setData(data);
        holder.itemView.setOnClickListener(v -> {
            if (onRightClickListener != null) {
                onRightClickListener.onClickItem(data);
            }
        });
    }

    @Override
    public int getItemCount() {
        return item.size();
    }

    public void setData(SelectedContent content) {
        if (content.getCode() == SUCCESS_CODE) {
            item.clear();
            item.addAll( content.getData().getTbk_uatm_favorites_item_get_response().getResults().getUatm_tbk_item());
        }
        notifyDataSetChanged();
    }

    public class InnerHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.image_content)
        ImageView imageContent;

        @BindView(R.id.off_price)
        TextView offPrice;

        @BindView(R.id.text_content)
        TextView textContent;

        @BindView(R.id.origin_price)
        TextView originPrice;

        @BindView(R.id.get_ticket_btn)
        TextView ticketBtn;

        public InnerHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void setData(SelectedContent.DataBean.TbkUatmFavoritesItemGetResponseBean.ResultsBean.UatmTbkItemBean data) {
            Glide.with(imageContent.getContext()).load(data.getPict_url()).into(imageContent);
            textContent.setText(data.getTitle());
            if (TextUtils.isEmpty(data.getCoupon_click_url())) {
                originPrice.setText("晚咯，没有优惠券了");
                ticketBtn.setVisibility(View.GONE);
            } else {
                originPrice.setText("原价：" + data.getZk_final_price());
            }
            if (TextUtils.isEmpty(data.getCoupon_info())) {
                offPrice.setVisibility(View.GONE);
            } else {
                offPrice.setText(data.getCoupon_info());
            }
        }
    }



    public interface OnRightClickListener {
        void onClickItem(ISkipInfo data);
    }
}

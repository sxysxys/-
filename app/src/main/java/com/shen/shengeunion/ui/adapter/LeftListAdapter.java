package com.shen.shengeunion.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.shen.shengeunion.R;
import com.shen.shengeunion.model.domain.SelectedPageCategories;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LeftListAdapter extends RecyclerView.Adapter<LeftListAdapter.InnerHolder> {
    private List<SelectedPageCategories.DataBean> mData = new ArrayList<>();

    // 设置默认的颜色，默认选中第0个
    private int currentSelectedPosition = 0;

    private OnClickLeftListener onClickLeftListener;


    @NonNull
    @Override
    public LeftListAdapter.InnerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_selected_left, parent, false);
        return new InnerHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LeftListAdapter.InnerHolder holder, int position) {
        SelectedPageCategories.DataBean dataBean = mData.get(position);
        if (position == currentSelectedPosition) {
            holder.itemView.setBackgroundColor(holder.itemView.getResources().getColor(R.color.selected_background_color));
        } else {
            holder.itemView.setBackgroundColor(holder.itemView.getResources().getColor(R.color.no_selected_background_color));
        }
        holder.setData(dataBean);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onClickLeftListener != null && currentSelectedPosition != position) {
                    currentSelectedPosition = position;
                    onClickLeftListener.onItemChanged(mData.get(position));
                    // 更新一下左边的状态
                    notifyDataSetChanged();
                }
            }
        });
    }

    public void setOnClickLeftListener(OnClickLeftListener onClickLeftListener) {
        this.onClickLeftListener = onClickLeftListener;
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public void setData(SelectedPageCategories categories) {
        List<SelectedPageCategories.DataBean> data = categories.getData();
        if (data != null) {
            this.mData.clear();
            this.mData.addAll(data);
        }
        if (onClickLeftListener != null) {
            onClickLeftListener.onItemChanged(mData.get(currentSelectedPosition));
        }
        notifyDataSetChanged();
    }

    public class InnerHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.text_selected_page)
        public TextView textView;

        public InnerHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void setData(SelectedPageCategories.DataBean data) {
            String title = data.getFavorites_title();
            textView.setText(title);
        }
    }

    public interface OnClickLeftListener {
        void onItemChanged(SelectedPageCategories.DataBean dataBean);
    }
}

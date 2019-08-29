package com.zxjk.duoduo.ui.walletpage.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.zxjk.duoduo.R;
import com.zxjk.duoduo.ui.walletpage.model.MyOrdersData;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class MyOrdersAdapter extends RecyclerView.Adapter<MyOrdersAdapter.ViewHolder> {

    List<MyOrdersData> data;

    public MyOrdersAdapter(List<MyOrdersData> data) {
        this.data = data;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_myorders, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tvTime;
        private TextView tvState;
        private TextView tvCountDown;
        private TextView tvCount;
        private TextView tvCointype;
        private TextView tvMoney;
        private ImageView ivState;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTime = itemView.findViewById(R.id.tvTime);
            tvState = itemView.findViewById(R.id.tvState);
            tvCountDown = itemView.findViewById(R.id.tvCountDown);
            tvCount = itemView.findViewById(R.id.tvCount);
            tvCointype = itemView.findViewById(R.id.tvCointype);
            tvMoney = itemView.findViewById(R.id.tvMoney);
            ivState = itemView.findViewById(R.id.ivState);
        }

        void bindData(MyOrdersData bean) {

        }
    }
}

package com.zxjk.moneyspace.ui.msgpage.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.zxjk.moneyspace.R;
import com.zxjk.moneyspace.bean.response.GetGroupRedPackageInfoResponse;
import com.zxjk.moneyspace.utils.GlideUtil;

import java.text.SimpleDateFormat;
import java.util.List;

public class RedPackageAdapter extends RecyclerView.Adapter<RedPackageAdapter.ViewHolder> {

    private List<GetGroupRedPackageInfoResponse.CustomerInfoBean> data;
    private String symbol;

    //群组红包时，手气最佳item position
    private int bestPosition = -1;

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public void setData(List<GetGroupRedPackageInfoResponse.CustomerInfoBean> data) {
        this.data = data;
        notifyDataSetChanged();
    }

    public void setBest(int bestPosition) {
        if (bestPosition < 0) return;
        this.bestPosition = bestPosition;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_red_packet, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bindData(data.get(position));
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        private ImageView ivHead;
        private TextView tvNick;
        private TextView tvTime;
        private TextView tvMoney;
        private TextView tvSymbol;
        private TextView tvBest;

        ViewHolder(@NonNull View itemView) {
            super(itemView);

            ivHead = itemView.findViewById(R.id.ivHead);
            tvNick = itemView.findViewById(R.id.tvNick);
            tvTime = itemView.findViewById(R.id.tvTime);
            tvMoney = itemView.findViewById(R.id.tvMoney);
            tvSymbol = itemView.findViewById(R.id.tv_symbol);
            tvBest = itemView.findViewById(R.id.tvBest);
        }

        void bindData(GetGroupRedPackageInfoResponse.CustomerInfoBean bean) {
            GlideUtil.loadCircleImg(ivHead, bean.getHeadPortrait());
            tvNick.setText(bean.getNick());
            tvTime.setText(sd.format(Long.parseLong(bean.getCreateTime())));
            tvMoney.setText(bean.getMoney());
            tvSymbol.setText(symbol);
            if (bestPosition != -1 && getAdapterPosition() == bestPosition) {
                tvBest.setVisibility(View.VISIBLE);
            } else {
                tvBest.setVisibility(View.INVISIBLE);
            }
        }
    }
}

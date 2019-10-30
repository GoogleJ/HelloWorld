package com.zxjk.duoduo.ui.msgpage.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.zxjk.duoduo.R;
import com.zxjk.duoduo.bean.response.GetGroupRedPackageInfoResponse;
import com.zxjk.duoduo.utils.GlideUtil;
import java.text.SimpleDateFormat;
import java.util.List;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class RedPackageAdapter extends RecyclerView.Adapter<RedPackageAdapter.ViewHolder> {

    private List<GetGroupRedPackageInfoResponse.CustomerInfoBean> data;

    public void setData(List<GetGroupRedPackageInfoResponse.CustomerInfoBean> data) {
        this.data = data;
        notifyDataSetChanged();
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
        private ImageView ivHead;
        private TextView tvNick;
        private TextView tvTime;
        private TextView tvMoney;
        SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        ViewHolder(@NonNull View itemView) {
            super(itemView);

            ivHead = itemView.findViewById(R.id.ivHead);
            tvNick = itemView.findViewById(R.id.tvNick);
            tvTime = itemView.findViewById(R.id.tvTime);
            tvMoney = itemView.findViewById(R.id.tvMoney);
        }

        void bindData(GetGroupRedPackageInfoResponse.CustomerInfoBean bean) {
            GlideUtil.loadCircleImg(ivHead, bean.getHeadPortrait());
            tvNick.setText(bean.getNick());
            tvTime.setText(sd.format(Long.parseLong(bean.getCreateTime())));
            tvMoney.setText(bean.getMoney() + bean.getSymbol());
        }
    }
}

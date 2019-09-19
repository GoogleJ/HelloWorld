package com.zxjk.duoduo.ui.minepage.adapter;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.zxjk.duoduo.R;
import com.zxjk.duoduo.bean.response.DetailListResposne;
import com.zxjk.duoduo.ui.minepage.DetailInfoActivity;
import com.zxjk.duoduo.utils.DataUtils;

import java.util.ArrayList;
import java.util.List;

public class DetailListAdapter extends RecyclerView.Adapter<DetailListAdapter.ViewHolder> {

    private List<DetailListResposne> data = new ArrayList<>();

    public void setData(List<DetailListResposne> data) {
        this.data = data;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_detaillist, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bindData(data.get(position));
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(holder.itemView.getContext(), DetailInfoActivity.class);
            intent.putExtra("data", data.get(position));
            intent.putExtra("type", holder.tvItemDetaillistName.getText());
            intent.putExtra("remarks", data.get(position).getRemarks());
            holder.itemView.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView ivItemDetaillist;
        private TextView tvItemDetaillistName;
        private TextView tvItemDetaillistTime;
        private TextView tvItemDetaillistMoney;

        ViewHolder(@NonNull View itemView) {
            super(itemView);

            ivItemDetaillist = itemView.findViewById(R.id.ivItemDetaillist);
            tvItemDetaillistName = itemView.findViewById(R.id.tvItemDetaillistName);
            tvItemDetaillistTime = itemView.findViewById(R.id.tvItemDetaillistTime);
            tvItemDetaillistMoney = itemView.findViewById(R.id.tvItemDetaillistMoney);
        }

        @SuppressLint("SetTextI18n")
        private void bindData(DetailListResposne bean) {
            tvItemDetaillistTime.setText(DataUtils.timeStamp2Date(Long.parseLong(bean.getCreateTime()), "yyyy-MM-dd HH:mm:ss"));
            tvItemDetaillistMoney.setText(DataUtils.getTwoDecimals(bean.getMot()));
            switch (bean.getSource()) {
                case 0:
                    ivItemDetaillist.setImageResource(R.drawable.ic_type0);
                    tvItemDetaillistName.setText("红包");
                    break;
                case 1:
                    ivItemDetaillist.setImageResource(R.drawable.ic_type1);
                    tvItemDetaillistName.setText("转账");
                    break;
                case 2:
                    ivItemDetaillist.setImageResource(R.drawable.ic_type2);
                    tvItemDetaillistName.setText("红包退还");
                    break;
                case 3:
                    ivItemDetaillist.setImageResource(R.drawable.ic_type3);
                    tvItemDetaillistName.setText("游戏上分");
                    break;
                case 4:
                    ivItemDetaillist.setImageResource(R.drawable.ic_type4);
                    tvItemDetaillistName.setText("挂单出售");
                    break;
                case 5:
                    ivItemDetaillist.setImageResource(R.drawable.ic_type5);
                    tvItemDetaillistName.setText("撤销订单");
                    break;
                case 6:
                    ivItemDetaillist.setImageResource(R.drawable.ic_type6);
                    tvItemDetaillistName.setText("买入MoT");
                    break;
                case -3:
                    ivItemDetaillist.setImageResource(R.drawable.ic_type03);
                    tvItemDetaillistName.setText("游戏下分");
                    break;
                case 7:
                    ivItemDetaillist.setImageResource(R.drawable.ic_type7_12_13);
                    tvItemDetaillistName.setText("群主下分手续费收取");
                    break;
                case 8:
                    ivItemDetaillist.setImageResource(R.drawable.ic_type8_9_09);
                    tvItemDetaillistName.setText("MoT转MoT余额");
                    break;
                case 9:
                    ivItemDetaillist.setImageResource(R.drawable.ic_type8_9_09);
                    tvItemDetaillistName.setText("MoT余额转MoT");
                    break;
                case -9:
                    ivItemDetaillist.setImageResource(R.drawable.ic_type8_9_09);
                    tvItemDetaillistName.setText("MoT余额转MoT失败");
                    break;
                case 10:
                    ivItemDetaillist.setImageResource(R.drawable.ic_type10);
                    tvItemDetaillistName.setText("申诉退回");
                    break;
                case 11:
                    ivItemDetaillist.setImageResource(R.drawable.ic_type11);
                    tvItemDetaillistName.setText("注册赠送");
                    break;
                case 12:
                    ivItemDetaillist.setImageResource(R.drawable.ic_type7_12_13);
                    tvItemDetaillistName.setText("MoT余额转MoT手续费");
                    break;
                case 13:
                    ivItemDetaillist.setImageResource(R.drawable.ic_type7_12_13);
                    tvItemDetaillistName.setText("交易所手续费收取");
                    break;
                case 14:
                    ivItemDetaillist.setImageResource(R.drawable.ic_type12);
                    tvItemDetaillistName.setText("签到");
                    break;
                case 15:
                    ivItemDetaillist.setImageResource(R.drawable.ic_type13);
                    tvItemDetaillistName.setText("实名认证");
                    break;
                case 16:
                    ivItemDetaillist.setImageResource(R.drawable.ic_type14);
                    tvItemDetaillistName.setText("注册奖励");
                    break;
                case 17:
                    ivItemDetaillist.setImageResource(R.drawable.ic_type14);
                    tvItemDetaillistName.setText("活动奖励");
                    break;
                case 18:
                    ivItemDetaillist.setImageResource(R.drawable.ic_type14);
                    tvItemDetaillistName.setText("活动奖励");
                    break;
                case 19:
                    ivItemDetaillist.setImageResource(R.drawable.ic_type14);
                    tvItemDetaillistName.setText("活动奖励");
                    break;
                default:
                    ivItemDetaillist.setImageResource(R.drawable.ic_detail_item_type5);
                    tvItemDetaillistName.setText("其他");
                    break;
            }
        }
    }
}

package com.zxjk.duoduo.ui.walletpage.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.zxjk.duoduo.R;
import com.zxjk.duoduo.bean.response.GetTransferEthResponse;
import com.zxjk.duoduo.ui.walletpage.BlockOrderDetailActivity;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class ETHOrdersAdapter extends RecyclerView.Adapter<ETHOrdersAdapter.MyViewHolder> {

    private List<GetTransferEthResponse.ListBean> data;
    private Context context;

    public ETHOrdersAdapter(List<GetTransferEthResponse.ListBean> data) {
        this.data = data;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        return new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_eth_orders, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        GetTransferEthResponse.ListBean listBean = data.get(position);

        String inOrOut = listBean.getInOrOut();

        String address = "";
        if (inOrOut.equals("0")) {
            //转入
            holder.ivItemEthOrdersType.setImageResource(R.drawable.ic_ethorders_icon_in);
            String fromAddress = listBean.getFromAddress();
            address = fromAddress.substring(0, 7) + "..." + fromAddress.substring(fromAddress.length() - 5);
            holder.tvItemEthOrdersMoney.setTextColor(ContextCompat.getColor(context, R.color.red_eth_in));
            holder.tvItemEthOrdersMoney.setText("+" + listBean.getBalance() + "ETH");
        }
        if (inOrOut.equals("1")) {
            //转出
            holder.ivItemEthOrdersType.setImageResource(R.drawable.ic_ethorders_icon_out);
            String toAddress = listBean.getToAddress();
            address = toAddress.substring(0, 7) + "..." + toAddress.substring(toAddress.length() - 5);
            holder.tvItemEthOrdersMoney.setTextColor(ContextCompat.getColor(context, R.color.themecolor));
            holder.tvItemEthOrdersMoney.setText("-" + listBean.getBalance() + "ETH");
        }
        holder.tvItemEthOrdersWalletAddress.setText(address);


        holder.llItemEthOrdersJump.setOnClickListener(v -> {
            Intent intent = new Intent(context, BlockOrderDetailActivity.class);
            intent.putExtra("type", "0");
            intent.putExtra("data1", listBean);
            context.startActivity(intent);
        });

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        holder.tvItemEthOrdersTime.setText(simpleDateFormat.format(Long.parseLong(listBean.getCreateTime())));
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView tvItemEthOrdersWalletAddress;
        private TextView tvItemEthOrdersTime;
        private TextView tvItemEthOrdersMoney;
        private ImageView ivItemEthOrdersType;
        private LinearLayout llItemEthOrdersJump;

        MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tvItemEthOrdersWalletAddress = itemView.findViewById(R.id.tvItemEthOrdersWalletAddress);
            tvItemEthOrdersTime = itemView.findViewById(R.id.tvItemEthOrdersTime);
            tvItemEthOrdersMoney = itemView.findViewById(R.id.tvItemEthOrdersMoney);
            ivItemEthOrdersType = itemView.findViewById(R.id.ivItemEthOrdersType);
            llItemEthOrdersJump = itemView.findViewById(R.id.llItemEthOrdersJump);
        }
    }
}

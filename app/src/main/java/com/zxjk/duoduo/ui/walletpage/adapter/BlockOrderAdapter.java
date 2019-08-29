package com.zxjk.duoduo.ui.walletpage.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zxjk.duoduo.R;
import com.zxjk.duoduo.bean.response.GetTransferAllResponse;

import java.text.SimpleDateFormat;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

public class BlockOrderAdapter extends RecyclerView.Adapter<BlockOrderAdapter.ViewHoder> {
    private Context context;
    private List<GetTransferAllResponse.ListBean> data;

    private OnClickListener onClickListener;

    public interface OnClickListener {
        void onClick(GetTransferAllResponse.ListBean data);
    }

    public void setData(List<GetTransferAllResponse.ListBean> data) {
        this.data = data;
    }

    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    @NonNull
    @Override
    public ViewHoder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (context == null) {
            context = parent.getContext();
        }
        return new ViewHoder(LayoutInflater.from(context).inflate(R.layout.item_blockorder, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHoder holder, int position) {
        holder.bindData(data.get(position));
        holder.llItemClick.setOnClickListener(v -> {
            if (null != onClickListener) {
                onClickListener.onClick(data.get(holder.getAdapterPosition()));
            }
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class ViewHoder extends RecyclerView.ViewHolder {

        private ImageView ivItemBlockOrderImgLeft;
        private ImageView ivItemArrow;
        private TextView tvWalletAddress;
        private TextView tvItemFirst;
        private TextView tvItemSecond;
        private TextView tvItemTime;
        private TextView tvItemState;
        private TextView tvItemMoney;
        private LinearLayout llItemClick;

        ViewHoder(@NonNull View itemView) {
            super(itemView);
            ivItemBlockOrderImgLeft = itemView.findViewById(R.id.ivItemBlockOrderImgLeft);
            ivItemArrow = itemView.findViewById(R.id.ivItemArrow);
            tvWalletAddress = itemView.findViewById(R.id.tvWalletAddress);
            tvItemFirst = itemView.findViewById(R.id.tvItemFirst);
            tvItemSecond = itemView.findViewById(R.id.tvItemSecond);
            tvItemTime = itemView.findViewById(R.id.tvItemTime);
            tvItemMoney = itemView.findViewById(R.id.tvItemMoney);
            tvItemState = itemView.findViewById(R.id.tvItemState);
            llItemClick = itemView.findViewById(R.id.llItemClick);
        }

        @SuppressLint("ResourceType")
        void bindData(GetTransferAllResponse.ListBean bean) {
            tvItemTime.setText(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Long.parseLong(bean.getCreateTime())));
            tvItemState.setText((bean.getTxreceiptStatus().equals("0")) ? R.string.failed : (bean.getTxreceiptStatus().equals("1") ? R.string.success : R.string.procssing));

            if ("2".equals(bean.getSerialType())||"3".equals(bean.getSerialType())){
                tvItemMoney.setTextColor(ContextCompat.getColor(context,R.color.red_eth_in));
                tvItemMoney.setText("+"+bean.getBalance()+"MoT");
            }else if ("1".equals(bean.getSerialType())&&"0".equals(bean.getInOrOut())){
                tvItemMoney.setTextColor(ContextCompat.getColor(context,R.color.themecolor));
                tvItemMoney.setText("+"+bean.getBalance()+"MoT");

            }else if ("1".equals(bean.getSerialType())&&"1".equals(bean.getInOrOut())){
                tvItemMoney.setTextColor(ContextCompat.getColor(context,R.color.themecolor));
                tvItemMoney.setText("-"+bean.getBalance()+"MoT");
            }else if ("0".equals(bean.getSerialType())&&"0".equals(bean.getInOrOut())){
                tvItemMoney.setTextColor(ContextCompat.getColor(context,R.color.themecolor));
                tvItemMoney.setText("+"+bean.getBalance()+"ETH");
            }else{
                tvItemMoney.setTextColor(ContextCompat.getColor(context,R.color.themecolor));
                tvItemMoney.setText("-"+bean.getBalance()+"ETH");
            }



            if (bean.getSerialType().equals("0") || bean.getSerialType().equals("1")) {
                tvWalletAddress.setVisibility(View.VISIBLE);
                tvItemFirst.setVisibility(View.GONE);
                tvItemSecond.setVisibility(View.GONE);
                ivItemArrow.setVisibility(View.GONE);
                if (bean.getInOrOut().equals("0")) {
                    tvWalletAddress.setText(bean.getFromAddress());
                    ivItemBlockOrderImgLeft.setImageResource(R.drawable.ic_ethorders_icon_in);
                } else {
                    tvWalletAddress.setText(bean.getToAddress());
                    ivItemBlockOrderImgLeft.setImageResource(R.drawable.ic_ethorders_icon_out);
                }
            } else {
                ivItemBlockOrderImgLeft.setImageResource(R.drawable.ic_huazhuan_order);
                tvWalletAddress.setVisibility(View.GONE);
                tvItemFirst.setVisibility(View.VISIBLE);
                tvItemSecond.setVisibility(View.VISIBLE);
                ivItemArrow.setVisibility(View.VISIBLE);
                if (bean.getSerialType().equals("2")) {
                    tvItemFirst.setText("MoT");
                    tvItemSecond.setText("MoT");
                } else {
                    tvItemFirst.setText("MoT");
                    tvItemSecond.setText("MoT");
                }
            }
        }
    }
}

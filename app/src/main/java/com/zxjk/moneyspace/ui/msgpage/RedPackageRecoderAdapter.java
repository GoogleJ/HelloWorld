package com.zxjk.moneyspace.ui.msgpage;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.zxjk.moneyspace.bean.response.GetRedPackageRecordResponse;
import com.zxjk.moneyspace.R;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.List;

import static com.blankj.utilcode.util.ActivityUtils.startActivity;

public class RedPackageRecoderAdapter extends RecyclerView.Adapter<RedPackageRecoderAdapter.ViewHolder> {

    DecimalFormat df = new DecimalFormat("0.00");

    private Context context;
    private List<GetRedPackageRecordResponse.RedpackageListBean> data;

    public RedPackageRecoderAdapter(List<GetRedPackageRecordResponse.RedpackageListBean> data) {
        this.data = data;
    }

    public void setData(List<GetRedPackageRecordResponse.RedpackageListBean> data) {
        this.data = data;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (context == null) {
            context = parent.getContext();
        }
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_red_packet_record, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        GetRedPackageRecordResponse.RedpackageListBean redpackageListBean = data.get(position);
        holder.bindData(redpackageListBean);
        holder.itemView.setOnClickListener(v -> {
            Intent intent1 = new Intent(context, PeopleUnaccalimedActivity.class);
            intent1.putExtra("id", String.valueOf(data.get(holder.getAdapterPosition()).getRedPackageId()));
            intent1.putExtra("fromList", true);
            startActivity(intent1);
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvRedPackageNick;
        TextView tvRedPackageTime;
        TextView tvRedPackageMoney;
        ImageView iv;

        ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvRedPackageNick = itemView.findViewById(R.id.tvRedPackageNick);
            tvRedPackageTime = itemView.findViewById(R.id.tvRedPackageTime);
            tvRedPackageMoney = itemView.findViewById(R.id.tvRedPackageMoney);
            iv = itemView.findViewById(R.id.iv);
        }

        void bindData(GetRedPackageRecordResponse.RedpackageListBean bean) {
            tvRedPackageNick.setText(bean.getNick());
            tvRedPackageTime.setText(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Long.parseLong(bean.getCreateTime())));
            tvRedPackageMoney.setText(df.format(bean.getMoney()) + "MoT");
            if (TextUtils.isEmpty(bean.getTYPE())) {
                iv.setVisibility(View.GONE);
                return;
            }
            if (bean.getTYPE().equals("1")) {
                iv.setVisibility(View.VISIBLE);
            } else {
                iv.setVisibility(View.GONE);
            }
        }
    }
}

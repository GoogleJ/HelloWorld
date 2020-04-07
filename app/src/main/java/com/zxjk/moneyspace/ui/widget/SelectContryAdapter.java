package com.zxjk.moneyspace.ui.widget;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zxjk.moneyspace.R;
import com.zxjk.moneyspace.bean.CountryEntity;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class SelectContryAdapter extends RecyclerView.Adapter<SelectContryAdapter.ViewHolder> {

    private List<CountryEntity> data = new ArrayList<>();

    public void setData(List<CountryEntity> data) {
        this.data = data;
        notifyDataSetChanged();
    }

    private OnClickListener onClickListener;

    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public interface OnClickListener {
        void onClick(CountryEntity entity);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_select_contry, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bindData(data.get(position));
        if (position != 0) {
            if (data.get(position - 1).pinyin.equals(data.get(position).pinyin)) {
                holder.tvTitle.setVisibility(View.GONE);
            } else {
                holder.tvTitle.setVisibility(View.VISIBLE);
            }
        } else {
            holder.tvTitle.setVisibility(View.VISIBLE);
        }

        holder.llContent.setOnClickListener(v -> {
            if (onClickListener != null) {
                onClickListener.onClick(data.get(position));
            }
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvContry;
        TextView tvCode;
        TextView tvTitle;
        LinearLayout llContent;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvContry = itemView.findViewById(R.id.tvContry);
            tvCode = itemView.findViewById(R.id.tvCode);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            llContent = itemView.findViewById(R.id.llContent);
        }

        void bindData(CountryEntity bean) {
            tvContry.setText(bean.countryName);
            tvCode.setText(bean.countryCode);
            tvTitle.setText(bean.pinyin);
        }
    }
}

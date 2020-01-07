package com.zxjk.duoduo.ui.msgpage.adapter;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.zxjk.duoduo.R;
import com.zxjk.duoduo.bean.response.FriendInfoResponse;
import com.zxjk.duoduo.bean.response.GroupResponse;
import com.zxjk.duoduo.utils.GlideUtil;

import java.util.ArrayList;
import java.util.List;

public class CreateGroupAdapter extends RecyclerView.Adapter<CreateGroupAdapter.ViewHolder> {
    private boolean fromSocial;
    //好友列表
    private List<FriendInfoResponse> data = new ArrayList<>();
    //当前群成员
    private List<GroupResponse.CustomersBean> data1 = new ArrayList<>();

    private OnClickListener onClickListener;
    private OnTitleClickListener onTitleClickListener;

    //是否添加成员
    private boolean isAdd;

    public CreateGroupAdapter() {
    }

    public CreateGroupAdapter(boolean isAdd) {
        this.isAdd = isAdd;
    }

    public void setFromSocial(boolean fromSocial) {
        this.fromSocial = fromSocial;
    }

    public void setData1(List<GroupResponse.CustomersBean> data1) {
        this.data1 = data1;
    }

    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public void setOnTitleClickListener(OnTitleClickListener onTitleClickListener) {
        this.onTitleClickListener = onTitleClickListener;
    }

    public void setData(List<FriendInfoResponse> data) {
        this.data = data;
        if (data1.size() != 0) {
            flag:
            for (FriendInfoResponse f : data) {
                for (GroupResponse.CustomersBean c : data1) {
                    if (f.getId().equals(c.getId())) {
                        f.setCanCheck(false);
                        continue flag;
                    }
                }
            }
        }
        notifyDataSetChanged();
    }

    public interface OnClickListener {
        void onclick(FriendInfoResponse item, boolean check, int position);
    }

    public interface OnTitleClickListener {
        void onTitleClick();
    }

    @NonNull
    @Override
    public CreateGroupAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_add_group, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull CreateGroupAdapter.ViewHolder holder, int position) {
        holder.llTitle.setVisibility(View.GONE);
        FriendInfoResponse bean = data.get(position);

        holder.bindData(bean);

        if (bean.isCanCheck()) {
            holder.user_name.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.black));
        } else {
            holder.user_name.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.textcolor3));
        }

        holder.add_del_group_layout.setOnClickListener(v -> {
            if (!bean.isCanCheck()) {
                return;
            }
            if (!isAdd) {
                bean.setChecked(!bean.isChecked());
                notifyItemChanged(position);
            }
            if (null != onClickListener) onClickListener.onclick(bean, bean.isChecked(), position);
        });

        if (position != 0) {
            if (data.get(position - 1).getFirstLeter().equals(data.get(position).getFirstLeter())) {
                holder.tvLetter.setVisibility(View.GONE);
            } else {
                holder.tvLetter.setVisibility(View.VISIBLE);
            }
        } else {
            if (fromSocial) {
                holder.llTitle.setVisibility(View.VISIBLE);
            }
            holder.tvLetter.setVisibility(View.VISIBLE);
        }
        holder.llTitle.setOnClickListener(v -> {
            if (onTitleClickListener != null) onTitleClickListener.onTitleClick();
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tvLetter;
        private ImageView remove_headers;
        private TextView user_name;
        private CheckBox selected_delete;
        private LinearLayout add_del_group_layout;
        private LinearLayout llTitle;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            remove_headers = itemView.findViewById(R.id.remove_headers);
            user_name = itemView.findViewById(R.id.user_name);
            selected_delete = itemView.findViewById(R.id.selected_delete);
            tvLetter = itemView.findViewById(R.id.tvLetter);
            add_del_group_layout = itemView.findViewById(R.id.add_del_group_layout);
            llTitle = itemView.findViewById(R.id.llTitle);
        }

        private void bindData(FriendInfoResponse bean) {
            GlideUtil.loadCircleImg(remove_headers, bean.getHeadPortrait());
            user_name.setText(TextUtils.isEmpty(bean.getRemark()) ? bean.getNick() : bean.getRemark());
            selected_delete.setChecked(bean.isChecked());
            tvLetter.setText(bean.getFirstLeter());
        }
    }
}

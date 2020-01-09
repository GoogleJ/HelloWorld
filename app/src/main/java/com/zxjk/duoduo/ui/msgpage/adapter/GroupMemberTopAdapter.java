package com.zxjk.duoduo.ui.msgpage.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.zxjk.duoduo.R;
import com.zxjk.duoduo.bean.response.AllGroupMembersResponse;
import com.zxjk.duoduo.utils.GlideUtil;

import java.util.ArrayList;
import java.util.List;

public class GroupMemberTopAdapter extends RecyclerView.Adapter<GroupMemberTopAdapter.ViewHolder> {

    private List<AllGroupMembersResponse> data = new ArrayList<>();

    public void setData(List<AllGroupMembersResponse> data) {
        this.data = data;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_select_contact, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        GlideUtil.loadCircleImg((holder.item_header), data.get(position).getHeadPortrait());
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView item_header;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            item_header = itemView.findViewById(R.id.item_header);
        }
    }
}

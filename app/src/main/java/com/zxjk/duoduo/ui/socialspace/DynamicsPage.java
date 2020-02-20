package com.zxjk.duoduo.ui.socialspace;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.zxjk.duoduo.R;
import com.zxjk.duoduo.ui.base.BaseFragment;

public class DynamicsPage extends BaseFragment {

    private RecyclerView recycler;

    private BaseQuickAdapter<String, BaseViewHolder> adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.dynamicspager, container, false);

        recycler = rootView.findViewById(R.id.recycler);

        recycler.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new BaseQuickAdapter<String, BaseViewHolder>(android.R.layout.activity_list_item) {
            @Override
            protected void convert(BaseViewHolder helper, String item) {
            }
        };

        View emptyView = LayoutInflater.from(getContext()).inflate(R.layout.empty_recycler_social_calture, container, false);
        ImageView iv = emptyView.findViewById(R.id.iv);
        TextView tv = emptyView.findViewById(R.id.tv);
        iv.setImageResource(R.drawable.ic_emptyview_nofriend);
        tv.setText("社群动态暂未开放，敬请期待！");

        adapter.setEmptyView(emptyView);
        recycler.setAdapter(adapter);

        return rootView;
    }

}

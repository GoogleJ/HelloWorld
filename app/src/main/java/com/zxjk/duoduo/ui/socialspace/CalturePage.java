package com.zxjk.duoduo.ui.socialspace;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.zxjk.duoduo.R;
import com.zxjk.duoduo.ui.base.BaseFragment;

import java.util.ArrayList;

public class CalturePage extends BaseFragment {

    private RecyclerView recycler;
    private BaseQuickAdapter<String, BaseViewHolder> adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.calturepager, container, false);

        recycler = rootView.findViewById(R.id.recycler);
        recycler.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new BaseQuickAdapter<String, BaseViewHolder>(android.R.layout.activity_list_item) {
            @Override
            protected void convert(BaseViewHolder helper, String item) {
                helper.setText(android.R.id.text1, "asdasdasdsadas"+ helper.getAdapterPosition());
            }
        };
        recycler.setAdapter(adapter);

        ArrayList<String> strings = new ArrayList<>(100);
        for (int i = 0; i < 100; i++) {
            strings.add("");
        }
        adapter.setNewData(strings);

        return rootView;
    }

}

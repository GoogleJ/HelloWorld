package com.zxjk.duoduo.ui.socialspace;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.zxjk.duoduo.R;
import com.zxjk.duoduo.ui.base.BaseFragment;

public class DynamicsPage extends BaseFragment {

    private TextView tvTips;
    private ImageView ivTop;
    private boolean isSocial2 = true;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.dynamicspager, container, false);
        tvTips = rootView.findViewById(R.id.tvTips);
        ivTop = rootView.findViewById(R.id.ivTop);
        if (isSocial2) {
            tvTips.setText("暂未开放，尽情期待！");
            ivTop.setImageResource(R.drawable.ic_emptyview_nofriend);
        }
        return rootView;
    }

}

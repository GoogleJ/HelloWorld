package com.zxjk.duoduo.ui.socialspace;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.zxjk.duoduo.R;
import com.zxjk.duoduo.ui.base.BaseFragment;

public class CalturePage extends BaseFragment {

    private TextView tvTips;
    private boolean isSocial2;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.calturepager, container, false);
        tvTips = rootView.findViewById(R.id.tvTips);
        if (isSocial2) {
            tvTips.setText("暂未开放，尽情期待！");
        }
        return rootView;
    }

    public void social2() {
        isSocial2 = true;
    }
}

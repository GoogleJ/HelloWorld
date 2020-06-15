package com.zxjk.moneyspace.ui.minepage.wallet;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.blankj.utilcode.util.ToastUtils;
import com.zxjk.moneyspace.R;
import com.zxjk.moneyspace.ui.WebActivity;
import com.zxjk.moneyspace.ui.base.BaseFragment;

public class BalancePage1 extends BaseFragment implements View.OnClickListener {
    private TextView tv1;
    private TextView tv2;
    private LinearLayout tv3;
    private TextView tv4;
    private TextView tv5;
    private TextView tv6;
    private TextView tv7;
    private TextView tv8;
    private TextView tv9;
    private TextView tv12;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.balancepage1, container, false);

        tv1 = rootView.findViewById(R.id.tv1);
        tv2 = rootView.findViewById(R.id.tv2);
        tv3 = rootView.findViewById(R.id.tv3);
        tv4 = rootView.findViewById(R.id.tv4);
        tv5 = rootView.findViewById(R.id.tv5);
        tv6 = rootView.findViewById(R.id.tv6);
        tv7 = rootView.findViewById(R.id.tv7);
        tv8 = rootView.findViewById(R.id.tv8);
        tv9 = rootView.findViewById(R.id.tv9);
        tv12 = rootView.findViewById(R.id.tv12);

        tv1.setOnClickListener(this);
        tv2.setOnClickListener(this);
        tv3.setOnClickListener(this);
        tv4.setOnClickListener(this);
        tv5.setOnClickListener(this);
        tv6.setOnClickListener(this);
        tv7.setOnClickListener(this);
        tv8.setOnClickListener(this);
        tv9.setOnClickListener(this);
        tv12.setOnClickListener(this);

        return rootView;
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(getContext(), WebActivity.class);
        Bundle bundle = new Bundle();
        switch (v.getId()) {
            case R.id.tv1:
                bundle.putString("url", "https://www.huobi.me/zh-cn/markets/");
                intent.putExtras(bundle);
                getActivity().startActivity(intent);
                break;
            case R.id.tv2:
                bundle.putString("url", "https://www.binancezh.com/cn");
                intent.putExtras(bundle);
                getActivity().startActivity(intent);
                break;
            case R.id.tv3:
                bundle.putString("url", "https://www.chb.plus");
                intent.putExtras(bundle);
                getActivity().startActivity(intent);
                break;
            case R.id.tv4:
                bundle.putString("url", "https://www.okex.me");
                intent.putExtras(bundle);
                getActivity().startActivity(intent);
                break;
            case R.id.tv5:
                bundle.putString("url", "https://m.zb.live/");
                intent.putExtras(bundle);
                getActivity().startActivity(intent);
                break;
            case R.id.tv6:
                bundle.putString("url", "https://www.bitmex.com/");
                intent.putExtras(bundle);
                getActivity().startActivity(intent);
                break;
            case R.id.tv7:
            case R.id.tv8:
            case R.id.tv9:
            case R.id.tv12:
                ToastUtils.showShort(R.string.developing);
                break;
        }

    }
}

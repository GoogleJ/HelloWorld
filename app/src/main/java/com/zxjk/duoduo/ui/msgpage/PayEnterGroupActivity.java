package com.zxjk.duoduo.ui.msgpage;

import android.os.Bundle;
import android.view.View;
import android.widget.Switch;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.zxjk.duoduo.R;
import com.zxjk.duoduo.ui.base.BaseActivity;

public class PayEnterGroupActivity extends BaseActivity {

    private String groupId;

    private TextView tvTitle;
    private Switch switchOpen;
    private TextView tvMoney;
    private TextView tvTotalMoney;
    private TextView tvCount;
    private RecyclerView recycler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_enter_group);

        initView();
        initData();
    }

    private void initView() {
        tvTitle = findViewById(R.id.tv_title);
        switchOpen = findViewById(R.id.switchOpen);
        tvMoney = findViewById(R.id.tvMoney);
        tvTotalMoney = findViewById(R.id.tvTotalMoney);
        tvCount = findViewById(R.id.tvCount);
        recycler = findViewById(R.id.recycler);
    }

    private void initData() {
        groupId = getIntent().getStringExtra("groupId");

        findViewById(R.id.rl_back).setOnClickListener(v -> finish());
        tvTitle.setText(R.string.pay_enter_group);
    }
    
    public void setMoney(View view) {

    }
}

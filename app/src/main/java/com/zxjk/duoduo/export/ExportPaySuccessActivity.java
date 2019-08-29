package com.zxjk.duoduo.export;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.zxjk.duoduo.R;
import com.zxjk.duoduo.ui.base.BaseActivity;

public class ExportPaySuccessActivity extends BaseActivity {

    private TextView tvMoney;
    private TextView tvBussinessName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_export_pay_success);

        TextView tv_title = findViewById(R.id.tv_title);
        tv_title.setText(R.string.pay_success1);

        tvMoney = findViewById(R.id.tvMoney);
        tvBussinessName = findViewById(R.id.tvBussinessName);
        tvBussinessName.setText(getIntent().getStringExtra("bussinessName"));
        tvMoney.setText(getIntent().getStringExtra("hk"));

        findViewById(R.id.rl_back).setOnClickListener(v -> finish());
    }

    public void done(View view) {
        finish();
    }

}

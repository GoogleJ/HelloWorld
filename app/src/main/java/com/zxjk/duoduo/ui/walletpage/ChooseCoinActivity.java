package com.zxjk.duoduo.ui.walletpage;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.zxjk.duoduo.R;
import com.zxjk.duoduo.ui.base.BaseActivity;

public class ChooseCoinActivity extends BaseActivity {

    private TextView tv_currency;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_coin);
        initView();
        initData();
    }

    private void initView() {
        String coin = getIntent().getStringExtra("coin");
        TextView tv_title = findViewById(R.id.tv_title);
        tv_title.setText(getString(R.string.currency_option));
        ImageView iv_currency = findViewById(R.id.iv_currency);
        tv_currency = findViewById(R.id.tv_currency);

        if (tv_currency.getText().toString().equals(coin)) {
            tv_currency.setText("ETH");
            iv_currency.setImageResource(R.drawable.ic_eth);
        }
    }

    private void initData() {
        findViewById(R.id.rl_back).setOnClickListener(v -> finish());
        findViewById(R.id.rl_currency).setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.putExtra("coin", tv_currency.getText().toString());
            setResult(2, intent);
            finish();
        });
    }
}

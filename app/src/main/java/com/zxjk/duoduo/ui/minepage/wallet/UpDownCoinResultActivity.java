package com.zxjk.duoduo.ui.minepage.wallet;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.zxjk.duoduo.R;
import com.zxjk.duoduo.ui.base.BaseActivity;
import com.zxjk.duoduo.utils.GlideUtil;

public class UpDownCoinResultActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_up_down_coin_result);

        ImageView ivLogo = findViewById(R.id.ivLogo);

        String type = getIntent().getStringExtra("type");
        String logo = getIntent().getStringExtra("logo");

        TextView title = findViewById(R.id.tv_title);
        title.setText(type + "详情");
        findViewById(R.id.rl_back).setOnClickListener(v -> finish());

        switch (logo) {
            case "BTC":
                Glide.with(this).load(R.drawable.ic_updowncoin_result_btc).into(ivLogo);
                break;
            case "ETH":
                Glide.with(this).load(R.drawable.ic_updowncoin_result_eth).into(ivLogo);
                break;
            case "USDT":
                Glide.with(this).load(R.drawable.ic_updowncoin_result_usdt).into(ivLogo);
                break;
        }
    }

    public void back2Detail(View view) {
        Intent intent = new Intent(this, BalanceDetailActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    public void back2Home(View view) {
        Intent intent = new Intent(this, BalanceLeftActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
}

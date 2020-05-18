package com.zxjk.duoduo.ui.externalfunc;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.zxjk.duoduo.R;
import com.zxjk.duoduo.bean.response.ThirdPartyPaymentOrderResponse;
import com.zxjk.duoduo.ui.base.BaseActivity;
import com.zxjk.duoduo.utils.GlideUtil;

public class ThirdPartPayResultActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_third_part_pay_result);

        ThirdPartyPaymentOrderResponse r = getIntent().getParcelableExtra("data");
        if (r == null) {
            finish();
            return;
        }

        TextView tvMoney = findViewById(R.id.tvMoney);
        TextView tvSymbol = findViewById(R.id.tvSymbol);
        ImageView ivSymbol = findViewById(R.id.ivSymbol);

        tvMoney.setText(r.getAmount());
        tvSymbol.setText(r.getSymbol());
        GlideUtil.loadCircleImg(ivSymbol, r.getSymbolLogo());
    }

    public void back(View view) {
        finish();
    }

}

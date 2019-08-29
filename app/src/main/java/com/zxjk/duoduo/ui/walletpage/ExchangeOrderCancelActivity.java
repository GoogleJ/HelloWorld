package com.zxjk.duoduo.ui.walletpage;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.zxjk.duoduo.R;
import com.zxjk.duoduo.bean.response.GetOverOrderResponse;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ExchangeOrderCancelActivity extends AppCompatActivity {

    @BindView(R.id.tv_title)
    TextView tvTitle;
    private TextView tvConfirmSaleOrderId;
    private TextView tvConfirmSaleCoinType;
    private TextView tvConfirmSalePriceReference;
    private TextView tvConfirmSaleCount;
    private TextView tvConfirmSaleTotalPrice;
    private ImageView iv_wechat, iv_alipay, iv_bank;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exchange_order_cancel);
        ButterKnife.bind(this);
        tvTitle.setText(getString(R.string.order));

        tvConfirmSaleOrderId = findViewById(R.id.tvConfirmSaleOrderId);
        tvConfirmSaleCoinType = findViewById(R.id.tvConfirmSaleCoinType);
        tvConfirmSalePriceReference = findViewById(R.id.tvConfirmSalePriceReference);
        tvConfirmSaleCount = findViewById(R.id.tvConfirmSaleCount);
        tvConfirmSaleTotalPrice = findViewById(R.id.tvConfirmSaleTotalPrice);
        iv_wechat = findViewById(R.id.iv_wechat);
        iv_alipay = findViewById(R.id.iv_alipay);
        iv_bank = findViewById(R.id.iv_bank);

        GetOverOrderResponse data = (GetOverOrderResponse) getIntent().getSerializableExtra("data");
        tvConfirmSaleOrderId.setText(TextUtils.isEmpty(data.getBothOrderId()) ? (TextUtils.isEmpty(data.getSellOrderId()) ?
                data.getBuyOrderId() : data.getSellOrderId()) : data.getBothOrderId());
        tvConfirmSaleCoinType.setText(data.getCurrency().equals("1") ? "MoT" : "Other");
        tvConfirmSalePriceReference.setText(getIntent().getStringExtra("rate"));
        tvConfirmSaleCount.setText(data.getNumber());
        tvConfirmSaleTotalPrice.setText(data.getMoney());

        if (data.getPayType().equals("1")) {

            iv_wechat.setVisibility(View.VISIBLE);
        } else if (data.getPayType().equals("2")) {
            iv_alipay.setVisibility(View.VISIBLE);
        } else {
            iv_bank.setVisibility(View.VISIBLE);
        }
    }

    @OnClick(R.id.rl_back)
    public void onClick() {
        finish();
    }
}

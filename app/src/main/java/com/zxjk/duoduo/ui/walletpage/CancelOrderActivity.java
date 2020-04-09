package com.zxjk.moneyspace.ui.walletpage;

import android.os.Bundle;
import android.widget.TextView;

import com.zxjk.moneyspace.bean.response.ReleasePurchaseResponse;
import com.zxjk.moneyspace.R;
import com.zxjk.moneyspace.ui.base.BaseActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CancelOrderActivity extends BaseActivity {

    @BindView(R.id.tv_title)
    TextView tvTitle;
    private ReleasePurchaseResponse data;

    private TextView tvConfirmSaleOrderId;
    private TextView tvConfirmSaleCoinType;
    private TextView tvConfirmSalePriceReference;
    private TextView tvConfirmSaleCount;
    private TextView tvConfirmSaleTotalPrice;
    private TextView tvConfirmSalePayType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cancel_order);
        ButterKnife.bind(this);
        tvTitle.setText(getString(R.string.sale_cancel));

        data = (ReleasePurchaseResponse) getIntent().getSerializableExtra("data");
        String rate = getIntent().getStringExtra("rate");

        tvConfirmSaleOrderId = findViewById(R.id.tvConfirmSaleOrderId);
        tvConfirmSaleCoinType = findViewById(R.id.tvConfirmSaleCoinType);
        tvConfirmSalePriceReference = findViewById(R.id.tvConfirmSalePriceReference);
        tvConfirmSaleCount = findViewById(R.id.tvConfirmSaleCount);
        tvConfirmSaleTotalPrice = findViewById(R.id.tvConfirmSaleTotalPrice);
        tvConfirmSalePayType = findViewById(R.id.tvConfirmSalePayType);

        tvConfirmSaleOrderId.setText(data.getSellOrderId());
        tvConfirmSaleCoinType.setText(data.getCurrency().equals("1") ? "MoT" : "Other");
        tvConfirmSalePriceReference.setText(rate);
        tvConfirmSaleCount.setText(data.getNumber());
        tvConfirmSaleTotalPrice.setText(data.getMoney());

        StringBuilder sb = new StringBuilder();
        if (data.getPayType().contains(",")) {
            String[] split = data.getPayType().split(",");
            for (String str : split) {
                switch (str) {
                    case "1":
                        sb.append("微信" + ",");
                        break;
                    case "2":
                        sb.append("支付宝" + ",");
                        break;
                    case "3":
                        sb.append("银行卡" + ",");
                        break;
                }
            }
            sb.deleteCharAt(sb.length() - 1);
        } else {
            switch (data.getPayType()) {
                case "1":
                    sb.append("微信");
                    break;
                case "2":
                    sb.append("支付宝");
                    break;
                case "3":
                    sb.append("银行卡");
                    break;
            }
        }
        tvConfirmSalePayType.setText(sb.toString());
    }


    @OnClick(R.id.rl_back)
    public void onClick() {
        finish();
    }
}

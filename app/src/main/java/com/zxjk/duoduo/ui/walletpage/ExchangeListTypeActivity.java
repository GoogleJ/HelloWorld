package com.zxjk.duoduo.ui.walletpage;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.zxjk.duoduo.R;
import com.zxjk.duoduo.bean.response.GetOverOrderResponse;
import com.zxjk.duoduo.ui.base.BaseActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ExchangeListTypeActivity extends BaseActivity {
    ImageView icon;
    TextView description;
    TextView single_number;
    TextView currency_of_sale;
    TextView proposed_price;
    TextView quantity_to_sale;
    TextView sale_amount;
    TextView payment_method;
    String wechat;
    String alipay;
    String bank;
    @BindView(R.id.tv_title)
    TextView tvTitle;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exchange_list_type);
        ButterKnife.bind(this);
        initView();
        initData();


    }

    private void initData() {
        GetOverOrderResponse data = (GetOverOrderResponse) getIntent().getSerializableExtra("exchangeType");
        currency_of_sale.setText("MoT");
        if ("0".equals(data.getStatus()) || "1".equals(data.getStatus()) || "2".equals(data.getStatus())) {
            icon.setImageResource(R.drawable.icon_transfer_successful);
            description.setText(getString(R.string.exchange_list_successful));
        } else {
            if ("0".equals(data.getIsBuyPay())) {
                icon.setImageResource(R.drawable.icon_pending);
                description.setText(getString(R.string.exchange_list_wait_friend));
            } else {
                icon.setImageResource(R.drawable.icon_caveat);
                description.setText(getString(R.string.exchange_list_not_pay));
            }
        }
        if ("0".equals(data.getPayType())) {
            wechat = getString(R.string.wechat) + ",";
        } else if ("1".equals(data.getPayType())) {
            alipay = getString(R.string.alipay) + ",";
        } else {
            bank = getString(R.string.bank);
        }

        payment_method.setText(wechat + alipay + bank);
        single_number.setText(data.getBothOrderId());
        quantity_to_sale.setText(data.getNumber());
        sale_amount.setText(data.getMoney() + "CNY");
    }

    private void initView() {
        tvTitle.setText(getString(R.string.order));
        icon = findViewById(R.id.icon);
        description = findViewById(R.id.description);
        single_number = findViewById(R.id.single_number);
        currency_of_sale = findViewById(R.id.currency_of_sale);
        proposed_price = findViewById(R.id.proposed_price);
        quantity_to_sale = findViewById(R.id.quantity_to_sale);
        sale_amount = findViewById(R.id.sale_amount);
        payment_method = findViewById(R.id.payment_method);
    }

    @OnClick(R.id.rl_back)
    public void onClick() {
        finish();
    }
}

package com.zxjk.moneyspace.ui.msgpage;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.zxjk.moneyspace.R;
import com.zxjk.moneyspace.ui.base.BaseActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class TransferSuccessActivity extends BaseActivity {

    @BindView(R.id.tv_title)
    TextView tvTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transfer_success);
        ButterKnife.bind(this);
        tvTitle.setText(getString(R.string.m_successful_transfer_title_bar));
        TextView tvTransferSuccessFriend = findViewById(R.id.tvTransferSuccessFriend);
        TextView tvTransferSuccessMoney = findViewById(R.id.tvTransferSuccessMoney);
        tvTransferSuccessFriend.setText(getString(R.string.wait_for_xxx_confirm_receive, getIntent().getStringExtra("name")));
        String money = getIntent().getStringExtra("betMoney") + getIntent().getStringExtra("symbol");
        tvTransferSuccessMoney.setText(money);
    }

    public void submit(View view) {
        finish();
    }

    @OnClick(R.id.rl_back)
    public void onClick() {
        finish();
    }
}

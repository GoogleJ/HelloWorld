package com.zxjk.duoduo.ui.msgpage;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.zxjk.duoduo.R;
import com.zxjk.duoduo.ui.base.BaseActivity;

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
        tvTransferSuccessFriend.setText("待好友" + getIntent().getStringExtra("name") + "确认收款");
        tvTransferSuccessMoney.setText(getIntent().getStringExtra("betMoney") + "MoT");
    }

    public void submit(View view) {
        finish();
    }

    @OnClick(R.id.rl_back)
    public void onClick() {
        finish();
    }
}

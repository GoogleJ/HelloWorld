package com.zxjk.moneyspace.ui.walletpage;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.blankj.utilcode.util.ToastUtils;
import com.blankj.utilcode.util.Utils;
import com.zxjk.moneyspace.R;
import com.zxjk.moneyspace.bean.response.CnyRechargeResponse;
import com.zxjk.moneyspace.ui.base.BaseActivity;

public class CNYUpConfirm1Activity extends BaseActivity {

    private TextView tvBank;
    private TextView tvBankCard;
    private TextView tvBankAddress;
    private TextView tvName;
    private TextView tvMoney;

    private CnyRechargeResponse r;
    private String money;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cnyup_confirm1);

        money = getIntent().getStringExtra("money");
        r = getIntent().getParcelableExtra("bank");

        TextView title = findViewById(R.id.tv_title);
        title.setText(R.string.chongzhi);
        findViewById(R.id.rl_back).setOnClickListener(v -> finish());

        tvBank = findViewById(R.id.tvBank);
        tvBankCard = findViewById(R.id.tvBankCard);
        tvBankAddress = findViewById(R.id.tvBankAddress);
        tvName = findViewById(R.id.tvName);
        tvMoney = findViewById(R.id.tvMoney);

        tvBank.setText(r.getSuperBankInfo().getBank());
        tvBankCard.setText(r.getSuperBankInfo().getBankNum());
        tvBankAddress.setText(r.getSuperBankInfo().getSubbranch());
        tvName.setText(r.getSuperBankInfo().getCardholderName());
        tvMoney.setText(money);
    }

    public void copy1(View view) {
        ToastUtils.showShort(R.string.duplicated_to_clipboard);
        ClipboardManager cm = (ClipboardManager) Utils.getApp().getSystemService(Context.CLIPBOARD_SERVICE);
        if (cm != null) {
            cm.setPrimaryClip(ClipData.newPlainText("text", tvBank.getText()));
        }
    }

    public void copy2(View view) {
        ToastUtils.showShort(R.string.duplicated_to_clipboard);
        ClipboardManager cm = (ClipboardManager) Utils.getApp().getSystemService(Context.CLIPBOARD_SERVICE);
        if (cm != null) {
            cm.setPrimaryClip(ClipData.newPlainText("text", tvBankCard.getText()));
        }
    }

    public void copy3(View view) {
        ToastUtils.showShort(R.string.duplicated_to_clipboard);
        ClipboardManager cm = (ClipboardManager) Utils.getApp().getSystemService(Context.CLIPBOARD_SERVICE);
        if (cm != null) {
            cm.setPrimaryClip(ClipData.newPlainText("text", tvBankAddress.getText()));
        }
    }

    public void copy4(View view) {
        ToastUtils.showShort(R.string.duplicated_to_clipboard);
        ClipboardManager cm = (ClipboardManager) Utils.getApp().getSystemService(Context.CLIPBOARD_SERVICE);
        if (cm != null) {
            cm.setPrimaryClip(ClipData.newPlainText("text", tvName.getText()));
        }
    }

    public void copy5(View view) {
        ToastUtils.showShort(R.string.duplicated_to_clipboard);
        ClipboardManager cm = (ClipboardManager) Utils.getApp().getSystemService(Context.CLIPBOARD_SERVICE);
        if (cm != null) {
            cm.setPrimaryClip(ClipData.newPlainText("text", tvMoney.getText()));
        }
    }

    public void next(View view) {
        Intent intent = new Intent(this, CNYUpConfirm2Activity.class);
        intent.putExtra("money", money);
        startActivity(intent);
    }

}

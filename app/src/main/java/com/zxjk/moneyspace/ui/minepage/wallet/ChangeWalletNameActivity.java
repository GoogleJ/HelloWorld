package com.zxjk.moneyspace.ui.minepage.wallet;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.blankj.utilcode.util.ToastUtils;
import com.zxjk.moneyspace.R;
import com.zxjk.moneyspace.network.Api;
import com.zxjk.moneyspace.network.ServiceFactory;
import com.zxjk.moneyspace.network.rx.RxSchedulers;
import com.zxjk.moneyspace.ui.base.BaseActivity;
import com.zxjk.moneyspace.utils.CommonUtils;

public class ChangeWalletNameActivity extends BaseActivity {

    private EditText etInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_wallet_name);

        TextView title = findViewById(R.id.tv_title);
        title.setText(R.string.change_walletname);
        findViewById(R.id.rl_back).setOnClickListener(v -> finish());

        etInput = findViewById(R.id.etInput);

        String name = getIntent().getStringExtra("name");
        etInput.setText(name);
    }

    @SuppressLint("CheckResult")
    public void commit(View view) {
        String str = etInput.getText().toString().trim();
        if (TextUtils.isEmpty(str)) {
            ToastUtils.showShort(R.string.input_empty);
            return;
        }
        if (str.equals(getIntent().getStringExtra("name"))) {
            finish();
            return;
        }

        ServiceFactory.getInstance().getBaseService(Api.class)
                .updateWalletChainName(str, getIntent().getStringExtra("address"))
                .compose(bindToLifecycle())
                .compose(RxSchedulers.normalTrans())
                .compose(RxSchedulers.ioObserver(CommonUtils.initDialog(this)))
                .subscribe(s -> {
                    ToastUtils.showShort(R.string.successfully_modified);
                    Intent intent = new Intent();
                    intent.putExtra("name", str);
                    setResult(1, intent);
                    finish();
                }, this::handleApiError);
    }
}

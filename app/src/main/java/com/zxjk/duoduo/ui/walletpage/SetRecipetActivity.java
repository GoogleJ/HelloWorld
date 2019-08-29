package com.zxjk.duoduo.ui.walletpage;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.blankj.utilcode.util.ToastUtils;
import com.zxjk.duoduo.R;
import com.zxjk.duoduo.ui.base.BaseActivity;
import com.zxjk.duoduo.utils.MoneyValueFilter;

public class SetRecipetActivity extends BaseActivity {
    private EditText etMoney;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_recipet);

        findViewById(R.id.rl_back).setOnClickListener(v -> finish());

        TextView textView = findViewById(R.id.tv_title);
        textView.setText(R.string.setmoney);

        etMoney = findViewById(R.id.etMoney);

        etMoney.setFilters(new InputFilter[]{new MoneyValueFilter()});
    }

    public void commit(View view) {
        String trim = etMoney.getText().toString().trim();
        if (TextUtils.isEmpty(trim)) {
            ToastUtils.showShort(getString(R.string.please_set_money));
            return;
        }
        int i = Integer.parseInt(trim);
        if (i == 0) {
            ToastUtils.showShort(getString(R.string.please_set_money1));
            return;
        }
        Intent intent = new Intent();
        intent.putExtra("money", trim);
        setResult(1, intent);
        finish();
    }
}

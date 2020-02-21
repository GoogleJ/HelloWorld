package com.zxjk.duoduo.ui.walletpage;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.blankj.utilcode.util.ToastUtils;
import com.zxjk.duoduo.R;
import com.zxjk.duoduo.ui.base.BaseActivity;
import com.zxjk.duoduo.utils.DecimalInputTextWatcher;
import com.zxjk.duoduo.utils.MoneyValueFilter;

import kotlin.text.Regex;

public class SetRecipetActivity extends BaseActivity {
    private EditText etMoney;
    private TextView tvUnit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_recipet);

        findViewById(R.id.rl_back).setOnClickListener(v -> finish());

        TextView textView = findViewById(R.id.tv_title);
        tvUnit = findViewById(R.id.tvUnit);
        textView.setText(R.string.setmoney);
        tvUnit.setText(getIntent().getStringExtra("symbol"));

        etMoney = findViewById(R.id.etMoney);


        etMoney.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        etMoney.addTextChangedListener(new DecimalInputTextWatcher(etMoney,10,5));
    }

    public void commit(View view) {
        String trim = etMoney.getText().toString().trim();
        if (TextUtils.isEmpty(trim)) {
            ToastUtils.showShort(getString(R.string.please_set_money));
            return;
        }
        double i = Double.parseDouble(trim);
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

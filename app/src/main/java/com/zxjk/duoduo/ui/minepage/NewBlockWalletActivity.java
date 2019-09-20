package com.zxjk.duoduo.ui.minepage;

import android.os.Bundle;
import android.view.View;

import com.blankj.utilcode.util.ToastUtils;
import com.zxjk.duoduo.R;
import com.zxjk.duoduo.ui.base.BaseActivity;

public class NewBlockWalletActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_block_wallet);
        setTrasnferStatusBar(true);

        ToastUtils.showShort(R.string.developing);
    }

    public void back(View view) {
        finish();
    }

    public void list(View view) {
        ToastUtils.showShort(R.string.developing);
    }

    public void add(View view) {
        ToastUtils.showShort(R.string.developing);
    }
}

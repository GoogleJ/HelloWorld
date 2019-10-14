package com.zxjk.duoduo.ui.minepage.wallet;

import android.os.Bundle;
import android.view.View;

import com.blankj.utilcode.util.ToastUtils;
import com.zxjk.duoduo.R;
import com.zxjk.duoduo.bean.response.WalletChainInfoBean;
import com.zxjk.duoduo.ui.base.BaseActivity;

import java.util.ArrayList;

public class NewBlockWalletActivity extends BaseActivity {

    private ArrayList<WalletChainInfoBean> list = new ArrayList<>(0);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_block_wallet);
        setTrasnferStatusBar(true);

        list = getIntent().getParcelableArrayListExtra("list");
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

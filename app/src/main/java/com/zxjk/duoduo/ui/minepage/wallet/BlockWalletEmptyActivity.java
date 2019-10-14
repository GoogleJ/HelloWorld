package com.zxjk.duoduo.ui.minepage.wallet;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.zxjk.duoduo.R;
import com.zxjk.duoduo.ui.base.BaseActivity;

public class BlockWalletEmptyActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_block_wallet_empty);

        TextView tvtitle = findViewById(R.id.tv_title);
        tvtitle.setText(R.string.blockWallet);
        findViewById(R.id.rl_back).setOnClickListener(v -> finish());
    }

    /**
     * 创建钱包
     *
     * @param view
     */
    public void createW(View view) {
        startActivity(new Intent(this, CreateWalletActivity.class));
        finish();
    }

    /**
     * 导入钱包
     *
     * @param view
     */
    public void importW(View view) {

    }
}

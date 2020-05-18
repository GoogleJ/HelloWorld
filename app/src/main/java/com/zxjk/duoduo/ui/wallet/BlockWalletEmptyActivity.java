package com.zxjk.duoduo.ui.wallet;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.zxjk.duoduo.Constant;
import com.zxjk.duoduo.R;
import com.zxjk.duoduo.ui.base.BaseActivity;

public class BlockWalletEmptyActivity extends BaseActivity {
    private BroadcastReceiver broadcastReceiver;
    private boolean hasCreated;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_block_wallet_empty);

        TextView tvtitle = findViewById(R.id.tv_title);
        tvtitle.setText(R.string.blockWallet);
        findViewById(R.id.rl_back).setOnClickListener(v -> finish());

        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                hasCreated = true;
            }
        };
        IntentFilter intentFilter = new IntentFilter(Constant.ACTION_BROADCAST1);

        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver, intentFilter);
    }

    /**
     * 创建钱包
     *
     * @param view
     */
    public void createW(View view) {
        startActivity(new Intent(this, CreateWalletActivity.class));
    }

    /**
     * 导入钱包
     *
     * @param view
     */
    public void importW(View view) {
        Intent intent = new Intent(this, CreateWalletActivity.class);
        intent.putExtra("isImport", true);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        if (broadcastReceiver != null) {
            LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver);
        }
        super.onDestroy();
    }

    @Override
    protected void onRestart() {
        if (hasCreated) {
            Intent intent = new Intent(this, NewBlockWalletActivity.class);
            startActivity(intent);
            finish();
        }
        super.onRestart();
    }
}

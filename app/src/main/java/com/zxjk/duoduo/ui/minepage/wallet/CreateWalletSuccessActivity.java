package com.zxjk.duoduo.ui.minepage.wallet;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.zxjk.duoduo.R;
import com.zxjk.duoduo.bean.response.GenerateMnemonicResponse;
import com.zxjk.duoduo.ui.base.BaseActivity;

public class CreateWalletSuccessActivity extends BaseActivity {

    private GenerateMnemonicResponse response;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_wallet_success);

        response = getIntent().getParcelableExtra("response");

        TextView title = findViewById(R.id.tv_title);
        title.setText(R.string.backup_wallet);
        findViewById(R.id.rl_back).setOnClickListener(v -> finish());
    }

    public void backup(View view) {
        Intent intent = new Intent(this, BackupWordsActivity.class);
        intent.putExtra("words", response.getWalletMnemonic());
        startActivity(intent);
        finish();
    }

    public void backupLater(View view) {
        finish();
    }
}

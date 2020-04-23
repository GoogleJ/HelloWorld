package com.zxjk.moneyspace.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.zxjk.moneyspace.R;
import com.zxjk.moneyspace.ui.base.BaseActivity;

public class SuccessActivity1 extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_success1);

        TextView title = findViewById(R.id.tv_title);
        title.setText(R.string.commit);
        findViewById(R.id.rl_back).setOnClickListener(v -> finish());

    }

    public void done(View view) {
        finish();
    }

}

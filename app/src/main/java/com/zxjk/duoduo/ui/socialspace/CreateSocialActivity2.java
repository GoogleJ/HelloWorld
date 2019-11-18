package com.zxjk.duoduo.ui.socialspace;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.zxjk.duoduo.R;
import com.zxjk.duoduo.ui.base.BaseActivity;

public class CreateSocialActivity2 extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_social2);

        TextView title = findViewById(R.id.tv_title);
        title.setText(R.string.social_type);
        findViewById(R.id.rl_back).setOnClickListener(v -> finish());

    }

    public void paySocial(View view) {
        startActivity(new Intent(this, CreateSocialActivity3.class));
    }

    public void freeSocial(View view) {
        startActivity(new Intent(this, CreateSocialActivity4.class));
    }
}

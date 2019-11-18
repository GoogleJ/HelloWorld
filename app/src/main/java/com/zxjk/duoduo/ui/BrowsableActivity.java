package com.zxjk.duoduo.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import androidx.appcompat.app.AppCompatActivity;

import com.blankj.utilcode.util.AppUtils;
import com.zxjk.duoduo.Constant;
import com.zxjk.duoduo.ui.findpage.NewsDetailActivity;

public class BrowsableActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getIntent().getData() != null) {
            String action = getIntent().getData().getQueryParameter("action");
            if (!TextUtils.isEmpty(action)) {
                switch (action) {
                    case "default":
                        if (Constant.currentUser != null) {
                            Intent intent = new Intent(this, HomeActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                        } else {
                            startActivity(new Intent(this, WelcomeActivity.class));
                        }
                        break;
                    case "sharenews":
                        String url = getIntent().getData().getQueryParameter("url");
                        if (Constant.currentUser != null) {
                            if (!TextUtils.isEmpty(url)) {
                                Intent intent = new Intent(this, NewsDetailActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                intent.putExtra("url", url);
                                intent.putExtra("title", "新闻");
                                startActivity(intent);
                            }
                        } else {
                            startActivity(new Intent(this, WelcomeActivity.class));
                        }
                        break;
                }
            }
            finish();
        }

    }
}

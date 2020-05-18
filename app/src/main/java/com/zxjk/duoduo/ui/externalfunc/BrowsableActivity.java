package com.zxjk.duoduo.ui.externalfunc;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.blankj.utilcode.util.ToastUtils;
import com.zxjk.duoduo.Constant;
import com.zxjk.duoduo.R;
import com.zxjk.duoduo.ui.HomeActivity;
import com.zxjk.duoduo.ui.WelcomeActivity;
import com.zxjk.duoduo.ui.base.BaseActivity;
import com.zxjk.duoduo.ui.findpage.NewsDetailActivity;

import static com.zxjk.duoduo.ui.externalfunc.ThirdPartLoginActivity.ACTION_PAY;
import static com.zxjk.duoduo.ui.externalfunc.ThirdPartLoginActivity.ACTION_THIRDPARTLOGINACCESS;

public class BrowsableActivity extends BaseActivity {

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
                    case "authorizationLogin":
                        String appid = getIntent().getData().getQueryParameter("appId");
                        String randomStr = getIntent().getData().getQueryParameter("randomStr");
                        String sign = getIntent().getData().getQueryParameter("sign");
                        if (!TextUtils.isEmpty(appid) && !TextUtils.isEmpty(randomStr) && !TextUtils.isEmpty(sign)) {
                            if (Constant.currentUser.getId() == null) {
                                Intent intent = new Intent(this, ThirdPartLoginActivity.class);
                                intent.putExtra("action", ACTION_THIRDPARTLOGINACCESS);
                                intent.putExtra("appId", appid);
                                intent.putExtra("randomStr", randomStr);
                                intent.putExtra("sign", sign);
                                startActivity(intent);
                            } else {
                                Intent intent = new Intent(this, LoginAuthorizationActivity.class);
                                intent.putExtra("action", ACTION_THIRDPARTLOGINACCESS);
                                intent.putExtra("appId", appid);
                                intent.putExtra("randomStr", randomStr);
                                intent.putExtra("sign", sign);
                                startActivity(intent);
                            }
                        } else {
                            ToastUtils.showShort(R.string.wrong_param_data);
                        }

                        break;
                    case "pay":
                        Intent intent = new Intent(this, ThirdPartLoginActivity.class);
                        intent.putExtra("action", ACTION_PAY);
                        intent.putExtra("orderId", getIntent().getData().getQueryParameter("orderId"));
                        startActivity(intent);
                        break;
                }
            }
            finish();
        }
    }
}

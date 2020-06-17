package com.zxjk.duoduo.ui.externalfunc;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.zxjk.duoduo.Constant;
import com.zxjk.duoduo.bean.response.LoginResponse;
import com.zxjk.duoduo.ui.HomeActivity;
import com.zxjk.duoduo.ui.NewLoginActivity;
import com.zxjk.duoduo.ui.WelcomeActivity;
import com.zxjk.duoduo.ui.base.BaseActivity;
import com.zxjk.duoduo.ui.msgpage.AddFriendDetailsActivity;
import com.zxjk.duoduo.ui.socialspace.SocialHomeActivity;
import com.zxjk.duoduo.utils.MMKVUtils;

import static com.zxjk.duoduo.ui.externalfunc.ThirdPartLoginActivity.ACTION_PAY;
import static com.zxjk.duoduo.ui.externalfunc.ThirdPartLoginActivity.ACTION_THIRDPARTLOGINACCESS;

public class BrowsableActivity extends BaseActivity {

    @SuppressLint("CheckResult")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setEnableCheckConstant(false);
        super.onCreate(savedInstanceState);

        if (Constant.currentUser == null || TextUtils.isEmpty(Constant.token) || TextUtils.isEmpty(Constant.userId)) {
            LoginResponse loginResponse = MMKVUtils.getInstance().decodeParcelable("login");
            if (loginResponse != null) {
                Constant.currentUser = loginResponse;
                Constant.token = loginResponse.getToken();
                Constant.userId = loginResponse.getId();
            } else {
                Constant.currentUser = null;
            }
        }

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
                    case "authorizationLogin":
                        String appid = getIntent().getData().getQueryParameter("appId");
                        String randomStr = getIntent().getData().getQueryParameter("randomStr");
                        String sign = getIntent().getData().getQueryParameter("sign");
                        if (!TextUtils.isEmpty(appid) && !TextUtils.isEmpty(randomStr) && !TextUtils.isEmpty(sign)) {
                            if (Constant.currentUser == null) {
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
                        }
                        break;
                    case "pay":
                        if (null == Constant.currentUser) {
                            Intent intent = new Intent(this, ThirdPartLoginActivity.class);
                            intent.putExtra("action", ACTION_PAY);
                            intent.putExtra("orderId", getIntent().getData().getQueryParameter("orderId"));
                            startActivity(intent);
                        } else {
                            Intent intent = new Intent(this, PayConfirmActivity.class);
                            intent.putExtra("orderId", getIntent().getData().getQueryParameter("orderId"));
                            startActivity(intent);
                        }
                        break;
                    case "addFriend":
                        if (Constant.currentUser != null) {
                            String id = getIntent().getData().getQueryParameter("id");
                            if (!TextUtils.isEmpty(id)) {
                                try {
                                    ClipboardManager clipboardManager = (ClipboardManager) this.getSystemService(Context.CLIPBOARD_SERVICE);
                                    clipboardManager.setPrimaryClip(ClipData.newPlainText(null, ""));
                                } catch (Exception e) {
                                }

                                Intent intent = new Intent(this, AddFriendDetailsActivity.class);
                                intent.putExtra("friendId", id);
                                intent.putExtra("isQR", true);
                                startActivity(intent);
                            }
                        } else {
                            startActivity(new Intent(this, NewLoginActivity.class));
                        }
                        break;
                    case "joinGroup":
                        break;
                    case "joinCommunity":
                        if (Constant.currentUser != null) {
                            String id = getIntent().getData().getQueryParameter("id");
                            String groupId = getIntent().getData().getQueryParameter("groupId");
                            try {
                                ClipboardManager clipboardManager = (ClipboardManager) this.getSystemService(Context.CLIPBOARD_SERVICE);
                                clipboardManager.setPrimaryClip(ClipData.newPlainText(null, ""));
                            } catch (Exception e) {
                            }
                            if (!TextUtils.isEmpty(id) && !TextUtils.isEmpty(groupId)) {
                                Intent intent = new Intent(this, SocialHomeActivity.class);
                                intent.putExtra("id", groupId);
                                startActivity(intent);
                            }
                        } else {
                            startActivity(new Intent(this, NewLoginActivity.class));
                        }
                        break;
                }
            }
            finish();
        }
    }

}
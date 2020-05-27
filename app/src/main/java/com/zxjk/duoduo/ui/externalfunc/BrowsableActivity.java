package com.zxjk.duoduo.ui.externalfunc;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;

import com.blankj.utilcode.util.ToastUtils;
import com.zxjk.duoduo.Constant;
import com.zxjk.duoduo.R;
import com.zxjk.duoduo.bean.response.LoginResponse;
import com.zxjk.duoduo.network.Api;
import com.zxjk.duoduo.network.ServiceFactory;
import com.zxjk.duoduo.network.rx.RxSchedulers;
import com.zxjk.duoduo.ui.HomeActivity;
import com.zxjk.duoduo.ui.NewLoginActivity;
import com.zxjk.duoduo.ui.WelcomeActivity;
import com.zxjk.duoduo.ui.base.BaseActivity;
import com.zxjk.duoduo.ui.findpage.NewsDetailActivity;
import com.zxjk.duoduo.ui.msgpage.AddFriendDetailsActivity;
import com.zxjk.duoduo.ui.msgpage.FriendDetailsActivity;
import com.zxjk.duoduo.ui.socialspace.SocialHomeActivity;
import com.zxjk.duoduo.utils.CommonUtils;
import com.zxjk.duoduo.utils.MMKVUtils;

import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.UserInfo;

import static com.zxjk.duoduo.ui.externalfunc.ThirdPartLoginActivity.ACTION_PAY;
import static com.zxjk.duoduo.ui.externalfunc.ThirdPartLoginActivity.ACTION_THIRDPARTLOGINACCESS;

public class BrowsableActivity extends BaseActivity {

    private String id;
    private String groupId;

    @SuppressLint("CheckResult")
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
                        if (null == Constant.currentUser || TextUtils.isEmpty(Constant.token)) {
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
                        if (MMKVUtils.getInstance().decodeBool("isLogin")) {
                            id = getIntent().getData().getQueryParameter("id");
                            if (!TextUtils.isEmpty(id)) {
                                ServiceFactory.getInstance().getBaseService(Api.class)
                                        .getFriendInfoById(id)
                                        .compose(bindToLifecycle())
                                        .compose(RxSchedulers.ioObserver(CommonUtils.initDialog(this)))
                                        .compose(RxSchedulers.normalTrans())
                                        .subscribe(r -> {
                                            if (r.getIsFriend().equals("0")) {
                                                Intent intent = new Intent(this, AddFriendDetailsActivity.class);
                                                intent.putExtra("friendId", id);
                                                intent.putExtra("isQR","1");
                                                startActivity(intent);
                                                ClipboardManager clipboardManager = (ClipboardManager) this.getSystemService(Context.CLIPBOARD_SERVICE);
                                                clipboardManager.setPrimaryClip(ClipData.newPlainText(null, ""));
                                            }else {
                                                Intent intent = new Intent(this, FriendDetailsActivity.class);
                                                intent.putExtra("friendId", id);
                                                intent.putExtra("isQR","1");
                                                startActivity(intent);
                                            }
                                        }, this::handleApiError);
                            }
                        } else {
                            startActivity(new Intent(this, NewLoginActivity.class));
                            finish();
                        }
                        break;
                    case "joinGroup":
                        id = getIntent().getData().getQueryParameter("id");
                        groupId = getIntent().getData().getQueryParameter("groupId");
                        break;
                    case "joinCommunity":
                        if (MMKVUtils.getInstance().decodeBool("isLogin")) {
                            id = getIntent().getData().getQueryParameter("id");
                            groupId = getIntent().getData().getQueryParameter("groupId");
                            if (!TextUtils.isEmpty(id) && !TextUtils.isEmpty(groupId)) {
                                Intent intent = new Intent(this, SocialHomeActivity.class);
                                intent.putExtra("id", groupId);
                                intent.putExtra("isQR","1");
                                startActivity(intent);
                                ClipboardManager clipboardManager = (ClipboardManager) this.getSystemService(Context.CLIPBOARD_SERVICE);
                                clipboardManager.setPrimaryClip(ClipData.newPlainText(null, ""));
                            }
                        } else {
                            startActivity(new Intent(this, NewLoginActivity.class));
                            finish();
                        }
                        break;
                }
            }
            finish();
        }
    }

    private void goLoginByServer() {
        LoginResponse login = MMKVUtils.getInstance().decodeParcelable("login");
        if (login != null) {
            Constant.currentUser = login;
            Constant.token = login.getToken();
            Constant.userId = login.getId();
            // 连接融云
            RongIM.connect(login.getRongToken(), new RongIMClient.ConnectCallback() {

                @Override
                public void onTokenIncorrect() {
                    MMKVUtils.getInstance().enCode("isLogin", false);
                    Constant.clear();
                    ToastUtils.showShort(getString(R.string.login_again));
                    Intent intent = new Intent(BrowsableActivity.this, NewLoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                }

                @Override
                public void onSuccess(String userid) {
                    UserInfo userInfo = new UserInfo(userid, Constant.currentUser.getNick(), Uri.parse(Constant.currentUser.getHeadPortrait()));
                    RongIM.getInstance().setCurrentUserInfo(userInfo);
                    startActivity(new Intent(BrowsableActivity.this, HomeActivity.class));
                    finish();
                }

                @Override
                public void onError(RongIMClient.ErrorCode errorCode) {
                    MMKVUtils.getInstance().enCode("isLogin", false);
                    Constant.clear();
                    ToastUtils.showShort(getString(R.string.login_again));
                    Intent intent = new Intent(BrowsableActivity.this, NewLoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                }
            });
        } else {
            ToastUtils.showShort(getString(R.string.login_again));
            MMKVUtils.getInstance().enCode("isLogin", false);
            startActivity(new Intent(this, NewLoginActivity.class));
            finish();
        }
    }
}
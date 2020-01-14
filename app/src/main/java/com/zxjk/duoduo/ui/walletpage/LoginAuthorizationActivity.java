package com.zxjk.duoduo.ui.walletpage;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.blankj.utilcode.util.ToastUtils;
import com.bumptech.glide.Glide;
import com.shehuan.nicedialog.BaseNiceDialog;
import com.shehuan.nicedialog.NiceDialog;
import com.shehuan.nicedialog.ViewConvertListener;
import com.shehuan.nicedialog.ViewHolder;
import com.zxjk.duoduo.Application;
import com.zxjk.duoduo.Constant;
import com.zxjk.duoduo.R;
import com.zxjk.duoduo.network.Api;
import com.zxjk.duoduo.network.ServiceFactory;
import com.zxjk.duoduo.network.rx.RxSchedulers;
import com.zxjk.duoduo.ui.base.BaseActivity;
import com.zxjk.duoduo.utils.CommonUtils;
import com.zxjk.duoduo.utils.MMKVUtils;

import io.rong.imkit.RongIM;

import static com.zxjk.duoduo.ui.walletpage.ThirdPartLoginActivity.ACTION_LOGINAUTHORIZATIONSWICH;
import static com.zxjk.duoduo.ui.walletpage.ThirdPartLoginActivity.ACTION_THIRDPARTLOGINACCESS;

public class LoginAuthorizationActivity extends BaseActivity implements View.OnClickListener {
    private TextView mNickTv;
    private ImageView mLoginHeadPortrait;
    private String mAppId = "";
    private String mRandomStr = "";
    private String mSign = "";
    private String action = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_authorization);

        initView();

        initData();
    }

    public void initView() {
        mNickTv = findViewById(R.id.tv_login_rick);
        mLoginHeadPortrait = findViewById(R.id.iv_login_head_portrait);
        findViewById(R.id.btn_login_agreed).setOnClickListener(this);
        findViewById(R.id.btn_login_refused).setOnClickListener(this);
        findViewById(R.id.btn_login_switchid).setOnClickListener(this);
    }

    public void initData() {
        action = getIntent().getStringExtra("action");
        mAppId = getIntent().getStringExtra("appId");
        mRandomStr = getIntent().getStringExtra("randomStr");
        mSign = getIntent().getStringExtra("sign");
        Glide.with(this).load(Constant.currentUser.getHeadPortrait()).into(mLoginHeadPortrait);
        mNickTv.setText(Constant.currentUser.getNick());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_login_agreed:
                getAuthorizationTokenResponse();
                break;
            case R.id.btn_login_refused:
                finish();
                break;
            case R.id.btn_login_switchid:
                switchId();
                break;
        }
    }

    public void switchId() {
        NiceDialog.init().setLayoutId(R.layout.layout_general_dialog).setConvertListener(new ViewConvertListener() {
            @SuppressLint("CheckResult")
            @Override
            protected void convertView(ViewHolder holder, BaseNiceDialog dialog) {
                holder.setText(R.id.tv_title, "提示");
                holder.setText(R.id.tv_content, "您将退出登录");
                holder.setText(R.id.tv_cancel, "取消");
                holder.setText(R.id.tv_notarize, "确认");
                holder.setOnClickListener(R.id.tv_cancel, v1 -> dialog.dismiss());
                holder.setOnClickListener(R.id.tv_notarize, v12 -> {
                    dialog.dismiss();
                    ServiceFactory.getInstance().getBaseService(Api.class)
                            .loginOut()
                            .compose(RxSchedulers.ioObserver(CommonUtils.initDialog(LoginAuthorizationActivity.this)))
                            .compose(RxSchedulers.normalTrans())
                            .subscribe(s -> {
                                RongIM.getInstance().logout();
                                MMKVUtils.getInstance().enCode("isLogin", false);
                                Constant.clear();
                                ToastUtils.showShort(R.string.login_out);
                                Intent intent = new Intent(LoginAuthorizationActivity.this, ThirdPartLoginActivity.class);
                                intent.putExtra("action", ACTION_LOGINAUTHORIZATIONSWICH);
                                intent.putExtra("appId", mAppId);
                                intent.putExtra("randomStr", mRandomStr);
                                intent.putExtra("sign", mSign);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                finish();
                            }, LoginAuthorizationActivity.this::handleApiError);
                });
            }
        }).setDimAmount(0.5f).setOutCancel(false).show(getSupportFragmentManager());
    }

    @SuppressLint("CheckResult")
    public void getAuthorizationTokenResponse() {
        ServiceFactory.getInstance().getBaseService(Api.class)
                .htmlLogin(mAppId, mRandomStr, mSign)
                .compose(bindToLifecycle())
                .compose(RxSchedulers.normalTrans())
                .compose(RxSchedulers.ioObserver(CommonUtils.initDialog(LoginAuthorizationActivity.this)))
                .subscribe(s -> {
                    ToastUtils.showShort("授权成功！");
                    switch (action){
                        case ACTION_THIRDPARTLOGINACCESS:
                            ((Application) getApplication()).GetWebDataUtils().webToLogin(s);
                    }
                    finish();
                }, this::handleApiError);
    }
}
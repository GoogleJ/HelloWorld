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
import com.zxjk.duoduo.ui.NewLoginActivity;
import com.zxjk.duoduo.ui.base.BaseActivity;
import com.zxjk.duoduo.utils.CommonUtils;
import com.zxjk.duoduo.utils.MMKVUtils;
import io.rong.imkit.RongIM;

public class LoginAuthorizationActivity extends BaseActivity implements View.OnClickListener{

    private TextView tv_Sign;
    private TextView tv_Nick;
    private ImageView img_HeadPortrait;
    private String appId = "";
    private String randomStr = "";
    private String sign = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_authorization);

        initView();
        initDate();
        getAuthorization();
    }


    public void initView(){
        tv_Nick = findViewById(R.id.tv_Nick);
        tv_Sign = findViewById(R.id.tv_Sign);
        img_HeadPortrait = findViewById(R.id.img_HeadPortrait);
        findViewById(R.id.btn_agreedTo).setOnClickListener(this);
        findViewById(R.id.btn_refusedTo).setOnClickListener(this);
        findViewById(R.id.btn_switchid).setOnClickListener(this);

    }


    public void initDate(){
        appId = getIntent().getStringExtra("appId");
        randomStr = getIntent().getStringExtra("randomStr");
        sign = getIntent().getStringExtra("sign");
        Glide.with(this).load(Constant.currentUser.getHeadPortrait()).into(img_HeadPortrait);
        tv_Nick.setText(Constant.currentUser.getNick());
    }

    @SuppressLint("CheckResult")
    public void getAuthorization(){
        ServiceFactory.getInstance().getBaseService(Api.class)
                .getAuthorization(appId)
                .compose(bindToLifecycle())
                .compose(RxSchedulers.normalTrans())
                .compose(RxSchedulers.ioObserver(CommonUtils.initDialog(LoginAuthorizationActivity.this)))
                .subscribe(s -> {
                }, this::handleApiError);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_agreedTo:
                GetAuthorizationTokenResponse();
                break;
            case R.id.btn_refusedTo:
                finish();
                break;
            case R.id.btn_switchid:
                SwitchId();
                break;
        }
    }


    public void SwitchId(){
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
                                Intent intent = new Intent(LoginAuthorizationActivity.this, NewLoginActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }, LoginAuthorizationActivity.this::handleApiError);
                });
            }
        }).setDimAmount(0.5f).setOutCancel(false).show(getSupportFragmentManager());
    }

    @SuppressLint("CheckResult")
    public void GetAuthorizationTokenResponse(){
        ServiceFactory.getInstance().getBaseService(Api.class)
                .htmlLogin(appId,randomStr,sign)
                .compose(bindToLifecycle())
                .compose(RxSchedulers.normalTrans())
                .compose(RxSchedulers.ioObserver(CommonUtils.initDialog(LoginAuthorizationActivity.this)))
                .subscribe(s -> {
                    ((Application)getApplication()).GetWebDataUtils().webToLogin(s);
                    finish();
                }, this::handleApiError);
    }
}

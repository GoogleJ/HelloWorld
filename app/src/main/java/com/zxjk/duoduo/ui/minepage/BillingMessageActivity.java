package com.zxjk.duoduo.ui.minepage;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.blankj.utilcode.util.KeyboardUtils;
import com.blankj.utilcode.util.SPUtils;
import com.zxjk.duoduo.Constant;
import com.zxjk.duoduo.R;
import com.zxjk.duoduo.network.Api;
import com.zxjk.duoduo.network.ServiceFactory;
import com.zxjk.duoduo.network.rx.RxSchedulers;
import com.zxjk.duoduo.ui.base.BaseActivity;
import com.zxjk.duoduo.ui.widget.NewPayBoard;
import com.zxjk.duoduo.ui.widget.dialog.BaseAddTitleDialog;
import com.zxjk.duoduo.utils.CommonUtils;
import com.zxjk.duoduo.utils.MD5Utils;

@SuppressLint("CheckResult")
public class BillingMessageActivity extends BaseActivity implements View.OnClickListener {
    TextView tv_perfection_wechat, tv_perfection_alipay, tv_perfection_bank;
    ImageView iv_perfection_wechat, iv_perfection_alipay, iv_perfection_bank;
    RelativeLayout rl_weChat, rl_aliPay, rl_bank;

    String wechat = "1";
    String alipay = "2";
    String bank = "3";
    String type = "type";
    String isTag;
    BaseAddTitleDialog dialog;

    Intent intent;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_billing_message);
        intent = new Intent(this, ReceiptTypeActivity.class);

        initView();
        getPayInfo();
    }

    private void initView() {
        rl_weChat = findViewById(R.id.rl_weChat);
        rl_aliPay = findViewById(R.id.rl_aliPay);
        rl_bank = findViewById(R.id.rl_bank);
        tv_perfection_wechat = findViewById(R.id.tv_perfection_wechat);
        tv_perfection_alipay = findViewById(R.id.tv_perfection_alipay);
        tv_perfection_bank = findViewById(R.id.tv_perfection_bank);
        iv_perfection_wechat = findViewById(R.id.iv_perfection_wechat);
        iv_perfection_alipay = findViewById(R.id.iv_perfection_alipay);
        iv_perfection_bank = findViewById(R.id.iv_perfection_bank);
        rl_weChat.setOnClickListener(this);
        rl_aliPay.setOnClickListener(this);
        rl_bank.setOnClickListener(this);

        TextView tv_title = findViewById(R.id.tv_title);
        tv_title.setText(getString(R.string.collection_information));
        findViewById(R.id.rl_back).setOnClickListener(v -> finish());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_weChat:
                //跳转到微信信息页面
                inoutPsw();
                isTag = "1";
                break;
            case R.id.rl_aliPay:
                //跳转到支付宝信息页面
                inoutPsw();
                isTag = "2";
                break;
            case R.id.rl_bank:
                //跳转到银行卡信息页面
                inoutPsw();
                isTag = "3";
                break;
            default:
        }
    }

    public void updatePayInfo(String payType) {
        ServiceFactory.getInstance().getBaseService(Api.class)
                .updatePayInfo(payType)
                .compose(bindToLifecycle())
                .compose(RxSchedulers.ioObserver(CommonUtils.initDialog(this)))
                .compose(RxSchedulers.normalTrans())
                .subscribe(s -> startActivityForResult(intent, 2000), throwable -> {
                    dialog = new BaseAddTitleDialog(BillingMessageActivity.this);
                    dialog.setOnClickListener(() -> dialog.dismiss());
                    dialog.show(throwable.getMessage());
                });
    }

    //打开输入密码的对话框
    public void inoutPsw() {
        KeyboardUtils.hideSoftInput(BillingMessageActivity.this);
        new NewPayBoard(this).show(psw -> ServiceFactory.getInstance().getBaseService(Api.class)
                .verifyPayPwd(MD5Utils.getMD5(psw))
                .compose(bindToLifecycle())
                .compose(RxSchedulers.ioObserver(CommonUtils.initDialog(BillingMessageActivity.this)))
                .compose(RxSchedulers.normalTrans())
                .subscribe(s -> {
                    intent.putExtra("payPwd", psw);
                    if (isTag.equals(wechat)) {
                        intent.putExtra(type, isTag);
                        updatePayInfo(wechat);
                    } else if (isTag.equals(alipay)) {
                        intent.putExtra(type, alipay);
                        updatePayInfo(alipay);
                    } else {
                        intent.putExtra(type, bank);
                        updatePayInfo(bank);
                    }
                }, BillingMessageActivity.this::handleApiError));
    }

    public void getPayInfo() {
        ServiceFactory.getInstance().getBaseService(Api.class)
                .getPayInfo()
                .compose(bindToLifecycle())
                .compose(RxSchedulers.ioObserver(CommonUtils.initDialog(this)))
                .compose(RxSchedulers.normalTrans())
                .subscribe(payInfoResponses -> {
                    for (int i = 0; i < payInfoResponses.size(); i++) {
                        if (payInfoResponses.get(i).getPayType().equals("1")) {
                            tv_perfection_wechat.setText(getString(R.string.pay_type_update_successful));
                            iv_perfection_wechat.setVisibility(View.VISIBLE);
                        } else if (payInfoResponses.get(i).getPayType().equals("2")) {
                            tv_perfection_alipay.setText(getString(R.string.pay_type_update_successful));
                            iv_perfection_alipay.setVisibility(View.VISIBLE);
                        } else if (payInfoResponses.get(i).getPayType().equals("3")) {
                            tv_perfection_bank.setText(getString(R.string.pay_type_update_successful));
                            iv_perfection_bank.setVisibility(View.VISIBLE);
                        }

                        if (tv_perfection_wechat.getText().toString().equals(getString(R.string.pay_type_update_successful))
                                && tv_perfection_alipay.getText().toString().equals(getString(R.string.pay_type_update_successful))
                                && tv_perfection_bank.getText().toString().equals(getString(R.string.pay_type_update_successful))) {
                            SPUtils.getInstance().put(Constant.currentUser.getId(), true);
                        }
                    }
                }, this::handleApiError);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            if (resultCode == 1000 && requestCode == 2000) {
                if (data.getStringExtra("pay").equals("1")) {
                    tv_perfection_wechat.setText(getString(R.string.pay_type_update_successful));
                    iv_perfection_wechat.setVisibility(View.VISIBLE);
                } else if (data.getStringExtra("pay").equals("2")) {
                    tv_perfection_alipay.setText(getString(R.string.pay_type_update_successful));
                    iv_perfection_alipay.setVisibility(View.VISIBLE);
                } else {
                    tv_perfection_bank.setText(getString(R.string.pay_type_update_successful));
                    iv_perfection_bank.setVisibility(View.VISIBLE);
                }
                if ((tv_perfection_wechat.getText().toString().equals(getString(R.string.pay_type_update_successful)))
                        && (tv_perfection_alipay.getText().toString().equals(getString(R.string.pay_type_update_successful)))
                        && (tv_perfection_bank.getText().toString().equals(getString(R.string.pay_type_update_successful)))) {
                    SPUtils.getInstance().put(Constant.currentUser.getId(), true);
                }

            }
        }

    }
}

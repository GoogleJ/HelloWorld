package com.zxjk.duoduo.ui.wallet;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.blankj.utilcode.util.GsonUtils;
import com.blankj.utilcode.util.KeyboardUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.shehuan.nicedialog.BaseNiceDialog;
import com.shehuan.nicedialog.NiceDialog;
import com.shehuan.nicedialog.ViewConvertListener;
import com.shehuan.nicedialog.ViewHolder;
import com.zxjk.duoduo.Constant;
import com.zxjk.duoduo.R;
import com.zxjk.duoduo.bean.CountryEntity;
import com.zxjk.duoduo.bean.request.AddPayInfoBean;
import com.zxjk.duoduo.bean.response.GetPaymentInformationResponse;
import com.zxjk.duoduo.network.Api;
import com.zxjk.duoduo.network.ServiceFactory;
import com.zxjk.duoduo.network.rx.RxSchedulers;
import com.zxjk.duoduo.ui.CountrySelectActivity;
import com.zxjk.duoduo.ui.base.BaseActivity;
import com.zxjk.duoduo.ui.widget.dialog.PaymentTypeDialog;
import com.zxjk.duoduo.utils.CommonUtils;
import com.zxjk.duoduo.utils.OssUtils;
import com.zxjk.duoduo.utils.TakePicUtil;

import java.io.File;
import java.util.Collections;


/**
 * 完善信息
 * 微信、支付宝、银行卡
 */
@SuppressLint("CheckResult")
public class ReceiptTypeActivity extends BaseActivity implements View.OnClickListener {
    public GetPaymentInformationResponse paymentInformation;
    private RelativeLayout nickName, realName, accountIdCard;
    private TextView receiptTypeName, receiptTypeCard, receiptTypePaymentName;
    private TextView receiptTypeRealName, receiptTypeRealCardName, receiptTypePayment;
    private ImageView receiptTypeGo, receiptTypeCardGo, receiptTypePaymentGo;
    private TextView commitBtn;
    private TextView tv_title;
    private LinearLayout llContrary;
    private TextView tvContrary;
    private PaymentTypeDialog dialog;
    private String wechat = "WEIXIN";
    private String alipay = "ALIPAY";
    private String bank = "EBANK";
    private String mobile = "MOBILE";

    private String types;
    private String url;
    private String contrary;
    private int paymentinformation;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receipt_type);
        initView();
        initData();
        initClick();
    }

    private void initClick() {
        nickName.setOnClickListener(this);
        realName.setOnClickListener(this);
        commitBtn.setOnClickListener(this);
    }

    private void initView() {
        tv_title = findViewById(R.id.tv_title);
        findViewById(R.id.rl_back).setOnClickListener(v -> finish());
        nickName = findViewById(R.id.nick_name);
        realName = findViewById(R.id.real_name);
        accountIdCard = findViewById(R.id.account_id_card);
        receiptTypeName = findViewById(R.id.receipt_type_name);
        receiptTypeCard = findViewById(R.id.receipt_type_card);
        receiptTypePaymentName = findViewById(R.id.receipt_type_payment_name);
        receiptTypeRealName = findViewById(R.id.receipt_type_real_name);
        receiptTypeRealCardName = findViewById(R.id.receipt_type_real_card_name);
        receiptTypePayment = findViewById(R.id.receipt_type_payment);
        receiptTypeGo = findViewById(R.id.receipt_type_go);
        receiptTypeCardGo = findViewById(R.id.receipt_type_card_go);
        receiptTypePaymentGo = findViewById(R.id.receipt_type_payment_go);
        commitBtn = findViewById(R.id.commit_btn);
    }

    private void initData() {

        dialog = new PaymentTypeDialog(this);

        dialog.setOnClickListener((editContent, s) -> {
            if (s == 1) {
                receiptTypeRealName.setText(editContent);
                receiptTypeRealName.setVisibility(View.VISIBLE);
            } else if (s == 2) {
                receiptTypeRealCardName.setText(editContent);
                receiptTypeRealCardName.setVisibility(View.VISIBLE);
            } else {
                receiptTypePayment.setText(editContent);
                receiptTypePayment.setVisibility(View.VISIBLE);
            }

            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
            dialog.dismiss();
        });

        Intent intent = getIntent();
        types = intent.getStringExtra("type");
        paymentinformation = intent.getIntExtra("paymentinformation", -1);

        if (wechat.equals(types)) {
            //微信信息，提交按钮已隐藏
            if (1 == paymentinformation) {
                improvePaymentInformationByType("WEIXIN");
            }
            tv_title.setText(getString(R.string.wechat_info));
            nickName.setVisibility(View.GONE);
            receiptTypeCard.setText("微信账号");
            receiptTypePaymentName.setText(R.string.collection_code);
            receiptTypeRealCardName.setHint("请填写微信账号");
            receiptTypePayment.setText(R.string.not_uploaded);
        } else if (alipay.equals(types)) {
            //支付宝信息，提交按钮已隐藏
            if (1 == paymentinformation) {
                improvePaymentInformationByType("ALIPAY");
            }

            tv_title.setText(getString(R.string.alipy_info));
            nickName.setVisibility(View.GONE);
            receiptTypeCard.setText("支付宝账号");
            receiptTypePaymentName.setText(R.string.collection_code);
            receiptTypeRealCardName.setHint("请填写支付宝账号");
            receiptTypePayment.setText(R.string.not_uploaded);
        } else if (bank.equals(types)) {
            //银行卡信息，提交按钮已隐藏
            if (1 == paymentinformation) {
                improvePaymentInformationByType("EBANK");
            }

            tv_title.setText(getString(R.string.bank_info));
            receiptTypeName.setText(R.string.account_name);
            receiptTypeCard.setText(R.string.bank_id_card);
            receiptTypePaymentName.setText(R.string.bank);
            receiptTypeRealName.setHint("请填写持卡人姓名");
            receiptTypeRealCardName.setHint("请填写银行卡号");
            receiptTypePayment.setHint("请填写开户行");
        } else {
            if (1 == paymentinformation) {
                improvePaymentInformationByType("MOBILE");
            }
            nickName.setVisibility(View.GONE);
            realName.setVisibility(View.GONE);
            findViewById(R.id.tv1).setVisibility(View.VISIBLE);
            receiptTypePaymentName.setText("手机号码");
            receiptTypePayment.setHint("请填写手机号码");
        }

        if (types.equals(wechat) || types.equals(alipay)) {
            getPermisson(accountIdCard, result -> {
                if (result) {
                    dialogType();
                }
            }, Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        } else if (types.equals(bank)) {
            accountIdCard.setOnClickListener(v -> {
                dialog.show(getString(R.string.open_bank), getString(R.string.please_upload_selector_open_bank), "", 3);
            });
        } else {
            accountIdCard.setOnClickListener(v -> {
                dialog.setVisibilitys();
                dialog.setOnStartActivity(() -> {
                    startActivityForResult(new Intent(this, CountrySelectActivity.class), 200);
                });
                dialog.show("请填写您的手机号码", "请填写手机号码", mobile, 3);
            });
        }
    }


    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.nick_name:
                if (wechat.equals(types)) {
                    dialog.show(getString(R.string.wechat_nick), getString(R.string.hint_nick), wechat, 1);
                    return;
                } else if (alipay.equals(types)) {
                    dialog.show(getString(R.string.alipay_number), getString(R.string.hint_alipay), alipay, 1);
                    return;
                } else {
                    dialog.show("持卡人姓名", "请填写持卡人姓名", "", 1);
                    return;
                }
            case R.id.real_name:
                if (wechat.equals(types)) {
                    dialog.show(getString(R.string.wechat_nick), getString(R.string.hint_nick), wechat, 2);
                    return;
                } else if (alipay.equals(types)) {
                    dialog.show(getString(R.string.alipay_number), getString(R.string.hint_alipay), alipay, 2);
                    return;
                } else {
                    dialog.show(getString(R.string.bankcard), getString(R.string.input_bank_number), bank, 2);
                    return;
                }
            case R.id.commit_btn:
                AddPayInfoBean addPayInfoBean = new AddPayInfoBean();
                if (wechat.equals(types)) {
                    if (TextUtils.isEmpty(receiptTypeRealCardName.getText().toString())) {
                        ToastUtils.showShort("请填写微信账号");
                        return;
                    } else if (TextUtils.isEmpty(url)) {
                        ToastUtils.showShort("请上传收款二维码");
                        return;
                    }
                    addPayInfoBean.setWeixinId(receiptTypeRealCardName.getText().toString());
                    addPayInfoBean.setPayInfoType("WEIXIN");
                    addPayInfoBean.setWeixinUrl(url);
                } else if (alipay.equals(types)) {
                    if (TextUtils.isEmpty(receiptTypeRealCardName.getText().toString())) {
                        ToastUtils.showShort("请填写支付宝账号");
                        return;
                    } else if (TextUtils.isEmpty(url)) {
                        ToastUtils.showShort("请上传收款二维码");
                        return;
                    }
                    addPayInfoBean.setAlipayId(receiptTypeRealCardName.getText().toString());
                    addPayInfoBean.setPayInfoType("ALIPAY");
                    addPayInfoBean.setAlipayUrl(url);
                } else if (bank.equals(types)) {
                    if (TextUtils.isEmpty(receiptTypeRealCardName.getText().toString())) {
                        ToastUtils.showShort("请填写银行卡号");
                        return;
                    } else if (TextUtils.isEmpty(receiptTypeRealName.getText().toString())) {
                        ToastUtils.showShort("请填写持卡人姓名");
                        return;
                    } else if (TextUtils.isEmpty(receiptTypePayment.getText().toString())) {
                        ToastUtils.showShort("请填写开户行");
                        return;
                    }
                    addPayInfoBean.setPayInfoType("EBANK");
                    addPayInfoBean.setCardCode(receiptTypeRealCardName.getText().toString());
                    addPayInfoBean.setCardUserName(receiptTypeRealName.getText().toString());
                    addPayInfoBean.setCardAddress(receiptTypePayment.getText().toString());
                } else {
                    if (TextUtils.isEmpty(receiptTypePayment.getText().toString())) {
                        ToastUtils.showShort("请填写手机号");
                        return;
                    }
                    addPayInfoBean.setMobile(receiptTypePayment.getText().toString());
                    addPayInfoBean.setPayInfoType("MOBILE");
                    addPayInfoBean.setCountryCode(dialog.getContrary());
                }
                addPayInfo(GsonUtils.toJson(addPayInfoBean));
                break;
            default:
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (corpFile != null) {
            zipFile(Collections.singletonList(corpFile.getPath()), files -> {
                File file = files.get(0);
                OssUtils.uploadFile(file.getAbsolutePath(), url -> {
                    this.url = url;
                    receiptTypePayment.setText(R.string.uploaded);
                    ToastUtils.showShort(R.string.uploaded);
                });
            });
        }

        if (requestCode == 200 && resultCode == Activity.RESULT_OK && data != null) {
            CountryEntity countryEntity = (CountryEntity) data.getSerializableExtra("country");
            if (dialog != null) {
                dialog.setContrary(getString(R.string.country_phone, (countryEntity != null ? countryEntity.countryCode : "86")));
            }
            if (countryEntity != null) {
                Constant.HEAD_LOCATION = countryEntity.countryCode;
            }
        }
    }

    private void improvePaymentInformationByType(String data) {
        ServiceFactory.getInstance().getBaseService(Api.class)
                .improvePaymentInformationByType(data)
                .compose(bindToLifecycle())
                .compose(RxSchedulers.ioObserver(CommonUtils.initDialog(this)))
                .compose(RxSchedulers.normalTrans())
                .subscribe(s -> {
                    this.paymentInformation = s;
                    if (wechat.equals(types)) {
                        //微信信息，提交按钮已隐藏
                        tv_title.setText(getString(R.string.wechat_info));
                        nickName.setVisibility(View.GONE);
                        receiptTypeCard.setText("微信账号");
                        receiptTypePaymentName.setText(R.string.collection_code);
                        receiptTypeRealCardName.setText(s.getWeixinId());
                        if (TextUtils.isEmpty(s.getWeixinUrl())) {
                            receiptTypePayment.setText(R.string.not_uploaded);
                        } else {
                            receiptTypePayment.setText("已上传");
                        }
                    } else if (alipay.equals(types)) {
                        //支付宝信息，提交按钮已隐藏
                        tv_title.setText(getString(R.string.alipy_info));
                        nickName.setVisibility(View.GONE);
                        receiptTypeCard.setText("支付宝账号");
                        receiptTypePaymentName.setText(R.string.collection_code);
                        receiptTypeRealCardName.setText(s.getAlipayId());
                        if (TextUtils.isEmpty(s.getAlipayUrl())) {
                            receiptTypePayment.setText(R.string.not_uploaded);
                        } else {
                            receiptTypePayment.setText("已上传");
                        }
                    } else if (bank.equals(types)) {
                        //银行卡信息，提交按钮已隐藏
                        tv_title.setText(getString(R.string.bank_info));
                        receiptTypeName.setText(R.string.account_name);
                        receiptTypeCard.setText(R.string.bank_id_card);
                        receiptTypePaymentName.setText(R.string.bank);
                        receiptTypeRealName.setText(s.getCardUserName());
                        receiptTypeRealCardName.setText(s.getCardCode());
                        receiptTypePayment.setText(s.getCardAddress());
                    } else {
                        nickName.setVisibility(View.GONE);
                        realName.setVisibility(View.GONE);
                        receiptTypePaymentName.setText("手机号码");
                        receiptTypePayment.setText(s.getMobile());
                        dialog.setContrary(s.getCountryCode());
                    }
                }, this::handleApiError);
    }

    private void addPayInfo(String data) {
        ServiceFactory.getInstance().getBaseService(Api.class)
                .updateOTCPayInfo(data)
                .compose(bindToLifecycle())
                .compose(RxSchedulers.ioObserver(CommonUtils.initDialog(this)))
                .compose(RxSchedulers.normalTrans())
                .subscribe(s -> {
                    commitBtn.setEnabled(false);
                    ToastUtils.showShort(getString(R.string.pay_type_successful));
                    Intent intent = new Intent();
                    intent.putExtra("pay", types);
                    setResult(1000, intent);
                    finish();
                }, this::handleApiError);
    }

    private void dialogType() {
        KeyboardUtils.hideSoftInput(ReceiptTypeActivity.this);

        NiceDialog.init().setLayoutId(R.layout.layout_general_dialog6).setConvertListener(new ViewConvertListener() {
            @Override
            protected void convertView(ViewHolder holder, BaseNiceDialog dialog) {
                holder.setOnClickListener(R.id.tv_photograph, v -> {
                    dialog.dismiss();
                    TakePicUtil.takePicture(ReceiptTypeActivity.this);
                });
                holder.setOnClickListener(R.id.tv_photo_select, v -> {
                    dialog.dismiss();
                    TakePicUtil.albumPhoto(ReceiptTypeActivity.this);
                });
                holder.setOnClickListener(R.id.tv_cancel, v -> dialog.dismiss());

            }
        }).setShowBottom(true)
                .setOutCancel(true)
                .setDimAmount(0.5f)
                .show(getSupportFragmentManager());
    }
}

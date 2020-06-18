package com.zxjk.duoduo.ui.wallet;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
    private RelativeLayout nickName, realName, accountIdCard;
    private TextView receiptTypeName, receiptTypeCard, receiptTypePaymentName;
    private TextView receiptTypeRealName, receiptTypeRealCardName, receiptTypePayment;
    private ImageView receiptTypeGo, receiptTypeCardGo, receiptTypePaymentGo;
    private TextView commitBtn;
    private String wechat = "WEIXIN";
    private String alipay = "ALIPAY";
    private String bank = "EBANK";
    private String mobile = "MOBILE ";
    private PaymentTypeDialog dialog;
    private String types;
    private String url;
    private TextView tv_title;
    private LinearLayout llContrary;
    private TextView tvContrary;

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
        Intent intent = getIntent();
        types = intent.getStringExtra("type");
        if (wechat.equals(types)) {
            //微信信息，提交按钮已隐藏
            tv_title.setText(getString(R.string.wechat_info));
            nickName.setVisibility(View.GONE);
            receiptTypeCard.setText("微信账号");
            receiptTypePaymentName.setText(R.string.collection_code);
            receiptTypeRealCardName.setText("请填写微信账号");
            receiptTypePayment.setText(R.string.not_uploaded);
        } else if (alipay.equals(types)) {
            //支付宝信息，提交按钮已隐藏
            tv_title.setText(getString(R.string.alipy_info));
            nickName.setVisibility(View.GONE);
            receiptTypeCard.setText("支付宝账号");
            receiptTypePaymentName.setText(R.string.collection_code);
            receiptTypeRealCardName.setText("请填写支付宝账号");
            receiptTypePayment.setText(R.string.not_uploaded);
        } else if (bank.equals(types)) {
            //银行卡信息，提交按钮已隐藏
            tv_title.setText(getString(R.string.bank_info));
            receiptTypeName.setText(R.string.account_name);
            receiptTypeCard.setText(R.string.bank_id_card);
            receiptTypePaymentName.setText(R.string.bank);
            receiptTypeRealName.setText("请填写持卡人姓名");
            receiptTypeRealCardName.setText("请填写银行卡号");
            receiptTypePayment.setText("请填写开户行");
        } else {
            nickName.setVisibility(View.GONE);
            realName.setVisibility(View.GONE);
            receiptTypePaymentName.setText("手机号码");
            receiptTypePayment.setText("请填写手机号码");
        }

        if (types.equals(wechat) || types.equals(alipay)) {
            getPermisson(accountIdCard, result -> {
                if (result) {
                    dialogType();
                }
            }, Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        } else if (types.equals(bank)) {
            accountIdCard.setOnClickListener(v -> {
                dialog = new PaymentTypeDialog(ReceiptTypeActivity.this);
                dialog.setOnClickListener(editContent -> {
                    receiptTypePayment.setText(editContent);
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
                    dialog.dismiss();
                });
                dialog.show(getString(R.string.open_bank), getString(R.string.please_upload_selector_open_bank), bank);
            });
        } else {
            accountIdCard.setOnClickListener(v -> {
                dialog = new PaymentTypeDialog(ReceiptTypeActivity.this);

                llContrary = dialog.findViewById(R.id.llContrary);
                tvContrary = dialog.findViewById(R.id.tvContrary);

//                llContrary.setOnClickListener(v1 -> ReceiptTypeActivity.this.startActivityForResult(new Intent(ReceiptTypeActivity.this, CountrySelectActivity.class), 200));

                dialog.setOnClickListener(editContent -> {
                    receiptTypePayment.setText(editContent);
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
                    dialog.dismiss();
                });
                dialog.show("请填写您的手机号码", "请填写手机号码", mobile);
            });
        }
//        getPayInfo();
    }

//    private void getPayInfo() {
//        ServiceFactory.getInstance().getBaseService(Api.class).getPayInfo()
//                .compose(bindToLifecycle())
//                .compose(RxSchedulers.ioObserver(CommonUtils.initDialog(this)))
//                .compose(RxSchedulers.normalTrans())
//                .subscribe(payInfoResponses -> {
//
//                    for (int i = 0; i < payInfoResponses.size(); i++) {
//                        if (payInfoResponses.get(i).getPayType().equals("1") && types.equals("1")) {
//                            receiptTypeRealName.setText(payInfoResponses.get(i).getWechatNick());
//                            if (!TextUtils.isEmpty(payInfoResponses.get(i).getPayPicture())) {
//                                receiptTypePayment.setText("已上传");
//                            }
//                            receiptTypeRealCardName.setText(Constant.currentUser.getRealname());
//                        } else if (payInfoResponses.get(i).getPayType().equals("2") && types.equals("2")) {
//                            receiptTypeRealName.setText(payInfoResponses.get(i).getZhifubaoNumber());
//                            if (!TextUtils.isEmpty(payInfoResponses.get(i).getPayPicture())) {
//                                receiptTypePayment.setText("已上传");
//                            }
//                            receiptTypeRealCardName.setText(Constant.currentUser.getRealname());
//                        } else if (payInfoResponses.get(i).getPayType().equals("3") && types.equals("3")) {
//                            receiptTypeRealName.setText(Constant.currentUser.getRealname());
//                            receiptTypeRealCardName.setText(payInfoResponses.get(i).getPayNumber());
//                            receiptTypePayment.setText(payInfoResponses.get(i).getOpenBank());
//                        }
//                    }
//                });
//    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.nick_name:
                if (wechat.equals(types)) {
                    dialog = new PaymentTypeDialog(this);
                    dialog.setOnClickListener(editContent -> {
                        receiptTypeRealName.setText(editContent);
                        receiptTypeRealName.setVisibility(View.VISIBLE);
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
                        dialog.dismiss();
                    });
                    dialog.show(getString(R.string.wechat_nick), getString(R.string.hint_nick), wechat);
                    return;
                } else if (alipay.equals(types)) {
                    dialog = new PaymentTypeDialog(this);
                    dialog.setOnClickListener(editContent -> {
                        receiptTypeRealName.setText(editContent);
                        receiptTypeRealName.setVisibility(View.VISIBLE);
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
                        dialog.dismiss();
                    });
                    dialog.show(getString(R.string.alipay_number), getString(R.string.hint_alipay), alipay);
                    return;
                }
                break;
            case R.id.real_name:
                if (wechat.equals(types)) {
                    dialog = new PaymentTypeDialog(this);
                    dialog.setOnClickListener(editContent -> {
                        receiptTypeRealCardName.setText(editContent);
                        receiptTypeRealCardName.setVisibility(View.VISIBLE);
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
                        dialog.dismiss();
                    });
                    dialog.show(getString(R.string.wechat_nick), getString(R.string.hint_nick), wechat);
                    return;
                } else if (alipay.equals(types)) {
                    dialog = new PaymentTypeDialog(this);
                    dialog.setOnClickListener(editContent -> {
                        receiptTypeRealCardName.setText(editContent);
                        receiptTypeRealCardName.setVisibility(View.VISIBLE);
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
                        dialog.dismiss();
                    });
                    dialog.show(getString(R.string.alipay_number), getString(R.string.hint_alipay), alipay);
                    return;
                } else {
                    dialog = new PaymentTypeDialog(ReceiptTypeActivity.this);
                    dialog.setOnClickListener(editContent -> {
                        receiptTypeRealCardName.setText(editContent);
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
                        dialog.dismiss();
                    });
                    dialog.show(getString(R.string.bankcard), getString(R.string.input_bank_number), bank);
                }
                break;
            case R.id.commit_btn:
                String pwd = getIntent().getStringExtra("payPwd");
                AddPayInfoBean addPayInfoBean = new AddPayInfoBean();
                if (wechat.equals(types)) {
                    addPayInfoBean.setWeixinId(receiptTypeRealCardName.getText().toString());
                    addPayInfoBean.setPayInfoType("WEIXIN");
                    addPayInfoBean.setWeixinUrl(url);
                } else if (alipay.equals(types)) {
                    addPayInfoBean.setAlipayId(receiptTypeRealCardName.getText().toString());
                    addPayInfoBean.setPayInfoType("ALIPAY");
                    addPayInfoBean.setAlipayUrl(url);
                } else if (bank.equals(types)) {
                    addPayInfoBean.setPayInfoType("EBANK");
                    addPayInfoBean.setCardCode(receiptTypeCard.getText().toString());
                    addPayInfoBean.setCardUserName(receiptTypeName.getText().toString());
                    addPayInfoBean.setCardAddress(receiptTypePaymentName.getText().toString());
                } else {
                    addPayInfoBean.setMobile(receiptTypeRealCardName.getText().toString());
                    addPayInfoBean.setPayInfoType("MOBILE");
                    addPayInfoBean.setCountryCode(tvContrary.getText().toString());
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
            Log.i("tag", "onActivityResult: "+countryEntity != null ? countryEntity.countryCode : "86");
            if (countryEntity != null) {
                Constant.HEAD_LOCATION = countryEntity.countryCode;
            }
        }
    }

    public void addPayInfo(String data) {
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

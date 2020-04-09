package com.zxjk.moneyspace.ui.minepage;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
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
import com.zxjk.moneyspace.bean.AddPayInfoBean;
import com.zxjk.moneyspace.Constant;
import com.zxjk.moneyspace.R;
import com.zxjk.moneyspace.network.Api;
import com.zxjk.moneyspace.network.ServiceFactory;
import com.zxjk.moneyspace.network.rx.RxSchedulers;
import com.zxjk.moneyspace.ui.base.BaseActivity;
import com.zxjk.moneyspace.ui.widget.dialog.PaymentTypeDialog;
import com.zxjk.moneyspace.utils.AesUtil;
import com.zxjk.moneyspace.utils.CommonUtils;
import com.zxjk.moneyspace.utils.MD5Utils;
import com.zxjk.moneyspace.utils.OssUtils;
import com.zxjk.moneyspace.utils.TakePicUtil;

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
    private String wechat = "1";
    private String alipay = "2";
    private String bank = "3";
    private PaymentTypeDialog dialog;
    private String types;
    private String url;
    private TextView tv_title;

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
            receiptTypeName.setText(R.string.nick);
            receiptTypeCard.setText(R.string.receipt_type_real_name);
            receiptTypePaymentName.setText(R.string.collection_code);
            receiptTypeRealCardName.setText(Constant.currentUser.getRealname());
            receiptTypePayment.setText(R.string.not_uploaded);
        } else if (alipay.equals(types)) {
            //支付宝信息，提交按钮已隐藏
            tv_title.setText(getString(R.string.alipy_info));
            receiptTypeName.setText(R.string.account_id);
            receiptTypeCard.setText(R.string.receipt_type_real_name);
            receiptTypePaymentName.setText(R.string.collection_code);
            receiptTypeRealCardName.setText(Constant.currentUser.getRealname());
            receiptTypePayment.setText(R.string.not_uploaded);
        } else {
            //银行卡信息，提交按钮已隐藏
            tv_title.setText(getString(R.string.bank_info));
            receiptTypeName.setText(R.string.account_name);
            receiptTypeCard.setText(R.string.bank_id_card);
            receiptTypePaymentName.setText(R.string.bank);
            receiptTypeRealName.setText(Constant.currentUser.getRealname());
        }

        if (types.equals(wechat) || types.equals(alipay)) {
            getPermisson(accountIdCard, result -> {
                if (result) {
                    dialogType();
                }
            }, Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        } else {
            accountIdCard.setOnClickListener(v -> {
                dialog = new PaymentTypeDialog(ReceiptTypeActivity.this);
                dialog.setOnClickListener(editContent -> {
                    receiptTypePayment.setText(editContent);
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
                    dialog.dismiss();
                });
                dialog.show(getString(R.string.open_bank), getString(R.string.please_upload_selector_open_bank), "4");
            });
        }

        getPayInfo();
    }

    private void getPayInfo() {
        ServiceFactory.getInstance().getBaseService(Api.class).getPayInfo()
                .compose(bindToLifecycle())
                .compose(RxSchedulers.ioObserver(CommonUtils.initDialog(this)))
                .compose(RxSchedulers.normalTrans())
                .subscribe(payInfoResponses -> {

                    for (int i = 0; i < payInfoResponses.size(); i++) {
                        if (payInfoResponses.get(i).getPayType().equals("1") && types.equals("1")) {
                            receiptTypeRealName.setText(payInfoResponses.get(i).getWechatNick());
                            if (!TextUtils.isEmpty(payInfoResponses.get(i).getPayPicture())) {
                                receiptTypePayment.setText("已上传");
                            }
                            receiptTypeRealCardName.setText(Constant.currentUser.getRealname());
                        } else if (payInfoResponses.get(i).getPayType().equals("2") && types.equals("2")) {
                            receiptTypeRealName.setText(payInfoResponses.get(i).getZhifubaoNumber());
                            if (!TextUtils.isEmpty(payInfoResponses.get(i).getPayPicture())) {
                                receiptTypePayment.setText("已上传");
                            }
                            receiptTypeRealCardName.setText(Constant.currentUser.getRealname());
                        } else if (payInfoResponses.get(i).getPayType().equals("3") && types.equals("3")) {
                            receiptTypeRealName.setText(Constant.currentUser.getRealname());
                            receiptTypeRealCardName.setText(payInfoResponses.get(i).getPayNumber());
                            receiptTypePayment.setText(payInfoResponses.get(i).getOpenBank());
                        }
                    }
                });
    }

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
                    dialog.show(getString(R.string.alipay_number), getString(R.string.alipay_number), alipay);
                    return;
                }
                break;
            case R.id.real_name:
                if (wechat.equals(types)) {
                    return;
                } else if (alipay.equals(types)) {
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
                    addPayInfoBean.setPayType(wechat);
                    addPayInfoBean.setWechatNick(receiptTypeRealName.getText().toString());
                    addPayInfoBean.setPayPicture(url);
                    addPayInfoBean.setPayPwd(MD5Utils.getMD5(pwd));
                } else if (alipay.equals(types)) {
                    addPayInfoBean.setPayType(alipay);
                    addPayInfoBean.setZhifubaoNumber(receiptTypeRealName.getText().toString());
                    addPayInfoBean.setPayPicture(url);
                    addPayInfoBean.setPayPwd(MD5Utils.getMD5(pwd));
                } else {
                    addPayInfoBean.setPayType(bank);
                    addPayInfoBean.setPayNumber(receiptTypeRealCardName.getText().toString());
                    addPayInfoBean.setOpenBank(receiptTypePayment.getText().toString());
                    addPayInfoBean.setPayPwd(MD5Utils.getMD5(pwd));
                }
                addPayInfo(AesUtil.getInstance().encrypt(GsonUtils.toJson(addPayInfoBean)));
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
    }

    public void addPayInfo(String data) {
        ServiceFactory.getInstance().getBaseService(Api.class)
                .addPayInfo(data)
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
                //拍照
                holder.setOnClickListener(R.id.tv_photograph, v -> {
                    dialog.dismiss();
                    TakePicUtil.takePicture(ReceiptTypeActivity.this);
                });
                //相册选择
                holder.setOnClickListener(R.id.tv_photo_select, v -> {
                    dialog.dismiss();
                    TakePicUtil.albumPhoto(ReceiptTypeActivity.this);
                });
                //取消
                holder.setOnClickListener(R.id.tv_cancel, v -> dialog.dismiss());
            }
        }).setShowBottom(true)
                .setOutCancel(true)
                .setDimAmount(0.5f)
                .show(getSupportFragmentManager());
    }
}

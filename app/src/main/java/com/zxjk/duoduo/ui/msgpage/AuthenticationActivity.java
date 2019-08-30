package com.zxjk.duoduo.ui.msgpage;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.blankj.utilcode.util.GsonUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.google.gson.Gson;
import com.shehuan.nicedialog.BaseNiceDialog;
import com.shehuan.nicedialog.NiceDialog;
import com.shehuan.nicedialog.ViewConvertListener;
import com.shehuan.nicedialog.ViewHolder;
import com.zxjk.duoduo.Constant;
import com.zxjk.duoduo.R;
import com.zxjk.duoduo.bean.AuditCertificationBean;
import com.zxjk.duoduo.bean.BitMapBean;
import com.zxjk.duoduo.bean.VerifiedBean;
import com.zxjk.duoduo.network.Api;
import com.zxjk.duoduo.network.ServiceFactory;
import com.zxjk.duoduo.network.rx.RxException;
import com.zxjk.duoduo.network.rx.RxSchedulers;
import com.zxjk.duoduo.ui.base.BaseActivity;
import com.zxjk.duoduo.utils.AesUtil;
import com.zxjk.duoduo.utils.CommonUtils;
import com.zxjk.duoduo.utils.GlideUtil;
import com.zxjk.duoduo.utils.MMKVUtils;
import com.zxjk.duoduo.utils.OssUtils;
import com.zxjk.duoduo.utils.TakePicUtil;

import java.io.File;
import java.util.Collections;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.functions.Function;
import okhttp3.RequestBody;

public class AuthenticationActivity extends BaseActivity {
    //标题
    @BindView(R.id.tv_title)
    TextView tvTitle;
    //身份证正面
    @BindView(R.id.iv_cardFront)
    ImageView ivCardFront;
    //身份证正面默认图
    @BindView(R.id.iv_default1)
    ImageView ivDefault1;
    //姓名
    @BindView(R.id.et_name)
    EditText etName;
    //身份证号码
    @BindView(R.id.et_idCard)
    EditText etIdCard;
    //正面信息可隐藏
    @BindView(R.id.rl_front)
    RelativeLayout rlFront;
    //身份证反面
    @BindView(R.id.iv_cardReverse)
    ImageView ivCardReverse;
    //身份证反面默认图
    @BindView(R.id.iv_default2)
    ImageView ivDefault2;
    //签发机关
    @BindView(R.id.et_issuingAuthority)
    EditText etIssuingAuthority;
    //有效期
    @BindView(R.id.et_validTerm)
    EditText etValidTerm;
    //反面信息可隐藏
    @BindView(R.id.rl_reverse)
    RelativeLayout rlReverse;
    //正面
    String frontUrl = "";
    //反面
    String reverseUrl = "";
    String body = "";
    public static final int REQUEST_TAKE = 1;

    public static final int REQUEST_ALBUM = 2;

    private int currentPictureFlag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authentication);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        tvTitle.setText("实名认证");
        initData();
    }

    private void initData() {
        getPermisson(ivCardFront, result -> {
            if (result) {
                currentPictureFlag = 1;
                dialogType();

            }
        }, Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE);

        getPermisson(ivCardReverse, result -> {
            if (result) {
                currentPictureFlag = 2;
                dialogType();
            }
        }, Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (corpFile != null) {
            zipFile(Collections.singletonList(corpFile.getPath()), files -> {
                File file = files.get(0);
                OssUtils.uploadFile(file.getAbsolutePath(), url -> {
                    if (currentPictureFlag == 1) {
                        frontUrl = url;
                        data("face", url);
                        cardFront();
                    }
                    if (currentPictureFlag == 2) {
                        reverseUrl = url;
                        data("back", url);
                        cardBack();
                    }
                });
            });
        }
    }

    private void data(String s, String url) {
        BitMapBean bitMapBean = new BitMapBean();
        BitMapBean.ConfigBean configBean = new BitMapBean.ConfigBean();
        bitMapBean.setImage(url);
        configBean.setSide(s);
        bitMapBean.setConfigure(configBean);
        body = new Gson().toJson(bitMapBean);
    }

    //身份证背面识别
    @SuppressLint({"CheckResult", "SetTextI18n"})
    private void cardBack() {
        RequestBody requestBody = RequestBody.create(okhttp3.MediaType.parse("application/json;charset=UTF-8"), body);
        Api api = ServiceFactory.getInstance().getNormalService("https://dm-51.data.aliyun.com/", Api.class);
        api.getOCRBackResult(requestBody)
                .compose(RxSchedulers.ioObserver(CommonUtils.initDialog(this)))
                .compose(bindToLifecycle())
                .subscribe(cardBackBean -> {
                    if (cardBackBean.isSuccess()) {
                        ivDefault2.setVisibility(View.GONE);
                        GlideUtil.loadCornerImg(ivCardReverse, reverseUrl, 5);
                        etIssuingAuthority.setText(cardBackBean.getIssue());
                        etValidTerm.setText(cardBackBean.getStart_date() + " - " + cardBackBean.getEnd_date());
                        rlReverse.setVisibility(View.VISIBLE);
                    }
                }, t -> ToastUtils.showShort(R.string.unknowerror));
    }

    //身份证正面识别
    @SuppressLint("CheckResult")
    private void cardFront() {
        RequestBody requestBody = RequestBody.create(okhttp3.MediaType.parse("application/json;charset=UTF-8"), body);
        Api api = ServiceFactory.getInstance().getNormalService("https://dm-51.data.aliyun.com/", Api.class);
        api.getOCRResult(requestBody)
                .compose(RxSchedulers.ioObserver(CommonUtils.initDialog(this)))
                .compose(bindToLifecycle())
                .subscribe(cardFaceBean -> {
                    if (cardFaceBean.isSuccess()) {
                        ivDefault1.setVisibility(View.GONE);
                        GlideUtil.loadCornerImg(ivCardFront, frontUrl, 5);
                        etName.setText(cardFaceBean.getName());
                        etIdCard.setText(cardFaceBean.getNum());
                        rlFront.setVisibility(View.VISIBLE);
                    }
                }, t -> ToastUtils.showShort(R.string.unknowerror));
    }

    @OnClick({R.id.rl_back, R.id.tv_submit})
    public void onClick(View view) {
        switch (view.getId()) {
            //返回
            case R.id.rl_back:
                finish();
                break;
            //提交
            case R.id.tv_submit:
                commit();
                break;
        }
    }

    @SuppressLint("CheckResult")
    private void commit() {
        if (TextUtils.isEmpty(etIdCard.getText().toString()) ||
                TextUtils.isEmpty(etName.getText().toString()) ||
                TextUtils.isEmpty(frontUrl) ||
                TextUtils.isEmpty(reverseUrl) ||
                TextUtils.isEmpty(etIssuingAuthority.getText().toString()) ||
                TextUtils.isEmpty(etValidTerm.getText().toString())) {
            ToastUtils.showShort("请检查信息是否完整");
            return;
        }
        VerifiedBean bean = new VerifiedBean();
        bean.setNumber(etIdCard.getText().toString());
        bean.setRealName(etName.getText().toString());
        bean.setType("1");
        bean.setPictureFront(frontUrl);
        bean.setPictureReverse(reverseUrl);
        bean.setPictureHand("");
        String dataStr = AesUtil.getInstance().encrypt(GsonUtils.toJson(bean));
        ServiceFactory.getInstance().getNormalService("http://phonethird.market.alicloudapi.com/", Api.class)
                .getCertification(etIdCard.getText().toString(), Constant.currentUser.getMobile(), etName.getText().toString())
                .flatMap((Function<AuditCertificationBean, Observable<String>>) auditCertificationBean -> {
                    if (auditCertificationBean.getStatus().equals("01")) {
                        return ServiceFactory.getInstance().getBaseService(Api.class)
                                .certification(dataStr)
                                .compose(RxSchedulers.normalTrans());
                    } else {
                        RxException.ParamsException exception = new RxException.ParamsException();
                        exception.setMsg(getString(R.string.cantverify));
                        throw exception;
                    }
                })
                .compose(RxSchedulers.ioObserver(CommonUtils.initDialog(this)))
                .compose(bindToLifecycle())
                .subscribe(response -> {
                    Constant.currentUser.setIsAuthentication("2");
                    Constant.currentUser.setRealname(etName.getText().toString());
                    MMKVUtils.getInstance().enCode("login", Constant.currentUser);
                    ToastUtils.showShort(R.string.verified_success);
                    finish();
                }, this::handleApiError);
    }

    //底部弹窗dialog 拍照、选择相册、取消
    private void dialogType() {
        NiceDialog.init().setLayoutId(R.layout.layout_general_dialog6).setConvertListener(new ViewConvertListener() {
            @Override
            protected void convertView(ViewHolder holder, BaseNiceDialog dialog) {
                //拍照
                holder.setOnClickListener(R.id.tv_photograph, v -> {
                    dialog.dismiss();
                    TakePicUtil.takePicture(AuthenticationActivity.this);
                });
                //相册选择
                holder.setOnClickListener(R.id.tv_photo_select, v -> {
                    dialog.dismiss();
                    TakePicUtil.albumPhoto(AuthenticationActivity.this);
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

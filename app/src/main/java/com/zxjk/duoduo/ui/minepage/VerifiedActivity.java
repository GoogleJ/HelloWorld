package com.zxjk.duoduo.ui.minepage;

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
import com.shehuan.nicedialog.BaseNiceDialog;
import com.shehuan.nicedialog.NiceDialog;
import com.shehuan.nicedialog.ViewConvertListener;
import com.shehuan.nicedialog.ViewHolder;
import com.zxjk.duoduo.Constant;
import com.zxjk.duoduo.R;
import com.zxjk.duoduo.bean.VerifiedBean;
import com.zxjk.duoduo.network.Api;
import com.zxjk.duoduo.network.ServiceFactory;
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


@SuppressLint("CheckResult")
public class VerifiedActivity extends BaseActivity {
    public static final int REQUEST_TAKE = 1;
    public static final int REQUEST_ALBUM = 2;
    String url1;
    String url2;
    String url3;
    //提交
    @BindView(R.id.tv_commit)
    TextView tvCommit;
    //标题
    @BindView(R.id.tv_title)
    TextView tvTitle;
    //证件类型
    @BindView(R.id.tv_certificateType)
    TextView tvCertificateType;
    //真实姓名
    @BindView(R.id.et_realName)
    EditText etRealName;
    //证件号码
    @BindView(R.id.et_idCard)
    EditText etIdCard;
    //正面照片
    @BindView(R.id.iv_frontPhoto)
    ImageView ivFrontPhoto;
    //正面照片右上角编辑
    @BindView(R.id.iv_frontPhotoEdit)
    ImageView ivFrontPhotoEdit;
    //正面照片
    @BindView(R.id.rl_front)
    RelativeLayout rlFront;
    //反面照片
    @BindView(R.id.iv_reversePhoto)
    ImageView ivReversePhoto;
    @BindView(R.id.tv_reverse)
    TextView tvReverse;
    //反面照片右上角编辑
    @BindView(R.id.iv_reversePhotoEdit)
    ImageView ivReversePhotoEdit;
    //反面照片
    @BindView(R.id.rl_reverse)
    RelativeLayout rlReverse;
    //手持照
    @BindView(R.id.iv_heldPhoto)
    ImageView ivHeldPhoto;
    @BindView(R.id.tv_heldPhoto)
    TextView tvHeldPhoto;
    //手持照右上角编辑
    @BindView(R.id.iv_heldPhotoEdit)
    ImageView ivHeldPhotoEdit;
    //手持照
    @BindView(R.id.rl_heldPhoto)
    RelativeLayout rlHeldPhoto;

    private int currentPictureFlag;
    private String otherIdCardType = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verified);
        ButterKnife.bind(this);
        initView();
    }


    @SuppressLint("WrongViewCast")
    private void initView() {
        otherIdCardType = getIntent().getStringExtra("otherIdCardType");
        tvTitle.setText(getString(R.string.verified));
        tvCommit.setVisibility(View.VISIBLE);
        tvCommit.setText(getString(R.string.commit));
        if (otherIdCardType.equals("2")) {
            tvCertificateType.setText("护照");
            tvReverse.setText("手持证件照");
            ivReversePhoto.setImageResource(R.drawable.icon_hand_held_passport_photo);
            rlHeldPhoto.setVisibility(View.GONE);
        } else if (otherIdCardType.equals("3")) {
            tvCertificateType.setText("其他国家或地区身份证");
        }

        getPermisson(rlFront, result -> {
            if (result) {
                currentPictureFlag = 1;
                dialogType();

            }
        }, Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE);

        getPermisson(rlReverse, result -> {
            if (result) {
                currentPictureFlag = 2;
                dialogType();
            }
        }, Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE);

        getPermisson(rlHeldPhoto, result -> {
            if (result) {
                currentPictureFlag = 3;
                dialogType();

            }
        }, Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE);
    }

    private void commit() {

        if (TextUtils.isEmpty(etRealName.getText().toString())) {
            ToastUtils.showShort(R.string.edit_real_name);
            return;
        }

        if (TextUtils.isEmpty(etIdCard.getText().toString())) {
            ToastUtils.showShort(R.string.edit_id_cards);
            return;
        }

        if (TextUtils.isEmpty(url1)) {
            ToastUtils.showShort(R.string.verified_realname1);
            return;
        }

        if (TextUtils.isEmpty(url2)) {
            ToastUtils.showShort(R.string.verified_realname2);
            return;
        }

        if (otherIdCardType.equals("3")) {
            if (TextUtils.isEmpty(url3)) {
                ToastUtils.showShort(R.string.verified_realname3);
                return;
            }
        }
        VerifiedBean bean = new VerifiedBean();
        bean.setNumber(etIdCard.getText().toString());
        bean.setRealName(etRealName.getText().toString());
        bean.setType(otherIdCardType);
        if (otherIdCardType.equals("2")) {
            bean.setPictureFront(url1);
            bean.setPictureReverse("");
            bean.setPictureHand(url2);
        } else if (otherIdCardType.equals("3")) {
            bean.setPictureFront(url1);
            bean.setPictureReverse(url2);
            bean.setPictureHand(url3);
        }
        certification(AesUtil.getInstance().encrypt(GsonUtils.toJson(bean)));
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        String filePath = "";

        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case REQUEST_TAKE:
                    filePath = TakePicUtil.file.getAbsolutePath();
                    break;
                case REQUEST_ALBUM:
                    filePath = TakePicUtil.getPath(this, data.getData());
                    break;
            }
        }

        if (!TextUtils.isEmpty(filePath)) {
            zipFile(Collections.singletonList(filePath), files -> {
                File file = files.get(0);
                OssUtils.uploadFile(file.getAbsolutePath(), url -> {
                    if (currentPictureFlag == 1) {
                        url1 = url;
                        ivFrontPhotoEdit.setVisibility(View.VISIBLE);
                        GlideUtil.loadCircleImg(ivFrontPhoto, url);
                        return;
                    }
                    if (currentPictureFlag == 2) {
                        url2 = url;
                        ivReversePhotoEdit.setVisibility(View.VISIBLE);
                        GlideUtil.loadCircleImg(ivReversePhoto, url);
                        return;
                    }
                    if (currentPictureFlag == 3) {
                        url3 = url;
                        ivHeldPhotoEdit.setVisibility(View.VISIBLE);
                        GlideUtil.loadCircleImg(ivHeldPhoto, url);
                    }
                });
            });
        }
    }

    public void certification(String data) {
        ServiceFactory.getInstance().getBaseService(Api.class)
                .certification(data)
                .compose(bindToLifecycle())
                .compose(RxSchedulers.ioObserver(CommonUtils.initDialog(this)))
                .compose(RxSchedulers.normalTrans())
                .subscribe(s -> {
                    Constant.currentUser.setIsAuthentication("2");
                    Constant.currentUser.setRealname(etRealName.getText().toString());
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
                    TakePicUtil.takePicture(VerifiedActivity.this, REQUEST_TAKE);
                });
                //相册选择
                holder.setOnClickListener(R.id.tv_photo_select, v -> {
                    dialog.dismiss();
                    TakePicUtil.albumPhoto(VerifiedActivity.this, REQUEST_ALBUM);
                });
                //取消
                holder.setOnClickListener(R.id.tv_cancel, v -> dialog.dismiss());

            }
        }).setShowBottom(true)
                .setOutCancel(true)
                .setDimAmount(0.5f)
                .show(getSupportFragmentManager());
    }

    @OnClick({R.id.rl_back, R.id.tv_commit})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rl_back:
                finish();
                break;
            case R.id.tv_commit:
                commit();
                break;
        }
    }
}

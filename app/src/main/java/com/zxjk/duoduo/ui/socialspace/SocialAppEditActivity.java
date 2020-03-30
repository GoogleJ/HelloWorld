package com.zxjk.duoduo.ui.socialspace;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import com.blankj.utilcode.util.GsonUtils;
import com.blankj.utilcode.util.KeyboardUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.shehuan.nicedialog.BaseNiceDialog;
import com.shehuan.nicedialog.NiceDialog;
import com.shehuan.nicedialog.ViewConvertListener;
import com.shehuan.nicedialog.ViewHolder;
import com.zxjk.duoduo.R;
import com.zxjk.duoduo.bean.request.EditCommunityApplicationRequest;
import com.zxjk.duoduo.bean.response.BaseResponse;
import com.zxjk.duoduo.network.Api;
import com.zxjk.duoduo.network.ServiceFactory;
import com.zxjk.duoduo.network.rx.RxSchedulers;
import com.zxjk.duoduo.ui.base.BaseActivity;
import com.zxjk.duoduo.utils.CommonUtils;
import com.zxjk.duoduo.utils.GlideUtil;
import com.zxjk.duoduo.utils.OssUtils;
import com.zxjk.duoduo.utils.TakePicUtil;

import java.io.File;
import java.util.Collections;

import io.reactivex.ObservableSource;
import io.reactivex.functions.Function;

public class SocialAppEditActivity extends BaseActivity {

    private String groupId;
    private String applicationId;
    private String applicationAddress;
    private String applicationLogo;
    private String applicationName;

    private boolean isAdd;

    private ImageView ivLogo;
    private EditText etName;
    private EditText etAddress;
    private LinearLayout llBottom;

    private String modifyLogoAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_social_app_edit);

        isAdd = getIntent().getBooleanExtra("isAdd", false);

        ivLogo = findViewById(R.id.ivLogo);
        etName = findViewById(R.id.etName);
        etAddress = findViewById(R.id.etAddress);
        llBottom = findViewById(R.id.llBottom);

        groupId = getIntent().getStringExtra("groupId");

        if (!isAdd) {
            applicationId = getIntent().getStringExtra("applicationId");
            applicationAddress = getIntent().getStringExtra("applicationAddress");
            applicationLogo = getIntent().getStringExtra("applicationLogo");
            applicationName = getIntent().getStringExtra("applicationName");

            GlideUtil.loadCornerImg(ivLogo, applicationLogo, 3);
            etAddress.setText(applicationAddress);
            etName.setText(applicationName);

            llBottom.setVisibility(View.VISIBLE);
        }

        getPermisson(findViewById(R.id.ivLogo), result -> {
            if (result) {
                dialogType();
            }
        }, Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE);
    }

    private void dialogType() {
        KeyboardUtils.hideSoftInput(this);
        NiceDialog.init().setLayoutId(R.layout.layout_general_dialog6).setConvertListener(new ViewConvertListener() {
            @Override
            protected void convertView(ViewHolder holder, BaseNiceDialog dialog) {
                //拍照
                holder.setOnClickListener(R.id.tv_photograph, v -> {
                    dialog.dismiss();
                    TakePicUtil.takePicture(SocialAppEditActivity.this);
                });
                //相册选择
                holder.setOnClickListener(R.id.tv_photo_select, v -> {
                    dialog.dismiss();
                    TakePicUtil.albumPhoto(SocialAppEditActivity.this);
                });
                //取消
                holder.setOnClickListener(R.id.tv_cancel, v -> dialog.dismiss());
            }
        }).setShowBottom(true)
                .setOutCancel(true)
                .setDimAmount(0.5f)
                .show(getSupportFragmentManager());
    }

    @SuppressLint("CheckResult")
    public void save(View view) {
        if (isAdd && TextUtils.isEmpty(modifyLogoAddress)) {
            ToastUtils.showShort(R.string.set_socialapp_logo);
            return;
        }

        String name = etName.getText().toString().trim();
        if (name.isEmpty()) {
            ToastUtils.showShort(R.string.input_socialapp_name);
            return;
        }

        String address = etAddress.getText().toString().trim();
        if (address.isEmpty()) {
            ToastUtils.showShort(R.string.input_socialapp_address);
            return;
        }

        EditCommunityApplicationRequest request = new EditCommunityApplicationRequest();
        request.setGroupId(groupId);

        if (isAdd) {
            request.setType("add");
        } else {
            request.setType("update");

        }

        if (!TextUtils.isEmpty(modifyLogoAddress)) {
            request.setApplicationLogo(modifyLogoAddress);
        }

        request.setApplicationId(applicationId);
        request.setApplicationAddress(address);
        request.setApplicationName(name);

        ServiceFactory.getInstance().getBaseService(Api.class)
                .editCommunityApplication(GsonUtils.toJson(request))
                .flatMap((Function<BaseResponse<String>, ObservableSource<BaseResponse<String>>>) stringBaseResponse -> {
                    request.setType("openOrClose");
                    request.setApplicationOpen("1");
                    return ServiceFactory.getInstance().getBaseService(Api.class)
                            .editCommunityApplication(GsonUtils.toJson(request));
                })
                .compose(bindToLifecycle())
                .compose(RxSchedulers.normalTrans())
                .compose(RxSchedulers.ioObserver(CommonUtils.initDialog(this)))
                .subscribe(s -> {
                    if (isAdd) {
                        Intent intent = new Intent();
                        setResult(1, intent);
                        finish();
                    } else {
                        Intent intent = new Intent();
                        setResult(1, intent);
                        finish();
                    }
                }, this::handleApiError);
    }

    @SuppressLint("CheckResult")
    public void deleteApp(View view) {
        EditCommunityApplicationRequest request = new EditCommunityApplicationRequest();
        request.setGroupId(groupId);
        request.setType("del");
        request.setApplicationId(applicationId);

        ServiceFactory.getInstance().getBaseService(Api.class)
                .editCommunityApplication(GsonUtils.toJson(request))
                .compose(bindToLifecycle())
                .compose(RxSchedulers.normalTrans())
                .compose(RxSchedulers.ioObserver(CommonUtils.initDialog(this)))
                .subscribe(s -> {
                    Intent intent = new Intent();
                    setResult(1, intent);
                    finish();
                }, this::handleApiError);
    }

    public void back(View view) {
        finish();
    }

    @SuppressLint("CheckResult")
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (corpFile != null) {
            zipFile(Collections.singletonList(corpFile.getPath()), files -> {
                File file = files.get(0);
                OssUtils.uploadFile(file.getAbsolutePath(), url -> {
                    modifyLogoAddress = url;
                    GlideUtil.loadCornerImg(ivLogo, url, 3);
                });
            });
        }
    }
}

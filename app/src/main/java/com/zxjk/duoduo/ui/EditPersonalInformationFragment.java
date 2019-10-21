package com.zxjk.duoduo.ui;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.zxjk.duoduo.bean.response.LoginResponse;
import com.zxjk.duoduo.network.Api;
import com.zxjk.duoduo.network.ServiceFactory;
import com.zxjk.duoduo.network.rx.RxSchedulers;
import com.zxjk.duoduo.ui.base.BaseActivity;
import com.zxjk.duoduo.ui.widget.provinces.Interface.OnCityItemClickListener;
import com.zxjk.duoduo.ui.widget.provinces.JDCityPicker;
import com.zxjk.duoduo.ui.widget.provinces.bean.CityBean;
import com.zxjk.duoduo.ui.widget.provinces.bean.DistrictBean;
import com.zxjk.duoduo.ui.widget.provinces.bean.ProvinceBean;
import com.zxjk.duoduo.utils.GlideUtil;
import com.zxjk.duoduo.utils.OssUtils;
import com.zxjk.duoduo.utils.TakePicUtil;

import java.io.File;
import java.util.Collections;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class EditPersonalInformationFragment extends BaseActivity {

    private JDCityPicker cityPicker;
    public static final int REQUEST_TAKE = 1;
    public static final int REQUEST_ALBUM = 2;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.iv_head)
    ImageView ivHead;
    @BindView(R.id.et_nickName)
    EditText etNickName;
    @BindView(R.id.tv_district)
    TextView tvDistrict;
    @BindView(R.id.iv_location)
    ImageView ivLocation;
    private String url;
    private LoginResponse update;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_personal_information);
        ButterKnife.bind(this);
        cityPicker = new JDCityPicker();
        cityPicker.init(this);
        initView();

    }

    @SuppressLint("SetTextI18n")
    private void initView() {
        tvTitle.setText(getString(R.string.m_edit_information_title_bar));
        getPermisson(ivHead, granted -> dialogType(),
                Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE);

        getPermisson(ivLocation, granted -> locationAMap(),
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.READ_PHONE_STATE);

        cityPicker.setOnCityItemClickListener(new OnCityItemClickListener() {

            @Override
            public void onSelected(ProvinceBean province, CityBean city, DistrictBean district) {
                tvDistrict.setText(province.getName() + city.getName() + district.getName());
            }

            @Override
            public void onCancel() {
            }
        });
    }

    private void showJD() {
        cityPicker.showCityPicker();
    }

    @OnClick({R.id.rl_back, R.id.tv_district, R.id.tv_complete})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rl_back:
                finish();
                break;
            case R.id.tv_district:
                showJD();
                break;

            case R.id.tv_complete:
                if (TextUtils.isEmpty(url)) {
                    ToastUtils.showShort(getString(R.string.please_upload_an_avatar));
                    return;
                }
                if (TextUtils.isEmpty(etNickName.getText().toString())) {
                    ToastUtils.showShort(getString(R.string.username_can_not_be_blank));
                    return;
                }
                if (TextUtils.isEmpty(tvDistrict.getText().toString())) {
                    ToastUtils.showShort(getString(R.string.area_can_not_be_empty));
                    return;
                }
                update = new LoginResponse(Constant.userId);
                update.setHeadPortrait(url);
                update.setNick(etNickName.getText().toString());
                update.setAddress(tvDistrict.getText().toString());
                updateCustomerInfo(GsonUtils.toJson(update));
                break;
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (corpFile != null) {
            zipFile(Collections.singletonList(corpFile.getPath()), files -> {
                File file = files.get(0);
                OssUtils.uploadFile(file.getAbsolutePath(), url -> {
                    LoginResponse update = new LoginResponse(Constant.userId);
                    update.setHeadPortrait(url);
                    GlideUtil.loadCircleImg(ivHead, url);
                    this.url = url;
                });
            });
        }

        if (data != null && resultCode == 1001 && requestCode == 1002) {
            tvDistrict.setText(data.getStringExtra("address"));
        }

    }

    //底部弹窗dialog 拍照、选择相册、取消
    private void dialogType() {
        NiceDialog.init().setLayoutId(R.layout.layout_general_dialog6).setConvertListener(new ViewConvertListener() {
            @Override
            protected void convertView(ViewHolder holder, BaseNiceDialog dialog) {
                //拍照
                holder.setOnClickListener(R.id.tv_photograph, v -> {
                    dialog.dismiss();
                    TakePicUtil.takePicture(EditPersonalInformationFragment.this);
                });
                //相册选择
                holder.setOnClickListener(R.id.tv_photo_select, v -> {
                    dialog.dismiss();
                    TakePicUtil.albumPhoto(EditPersonalInformationFragment.this);
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
    public void updateCustomerInfo(String customerInfo) {
        ServiceFactory.getInstance().getBaseService(Api.class)
                .updateUserInfo(customerInfo)
                .compose(bindToLifecycle())
                .compose(RxSchedulers.ioObserver())
                .compose(RxSchedulers.normalTrans())
                .subscribe(response -> {
                    GlideUtil.loadCircleImg(ivHead, url);
                    Constant.currentUser.setHeadPortrait(update.getHeadPortrait());
                    Constant.currentUser.setNick(update.getNick());
                    Constant.currentUser.setAddress(update.getAddress());
                    Intent intent = new Intent(EditPersonalInformationFragment.this, SetUpPaymentPwdActivity.class);
                    intent.putExtra("firstLogin", true);
                    startActivity(intent);
                    finish();
                }, this::handleApiError);
    }

    //高德地图
    private void locationAMap() {
        Intent intent = new Intent(this, AMapActivity.class);
        startActivityForResult(intent, 1002);
    }

}

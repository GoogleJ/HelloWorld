package com.zxjk.moneyspace.ui.minepage;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
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
import com.zxjk.moneyspace.Constant;
import com.zxjk.moneyspace.R;
import com.zxjk.moneyspace.bean.response.LoginResponse;
import com.zxjk.moneyspace.network.Api;
import com.zxjk.moneyspace.network.ServiceFactory;
import com.zxjk.moneyspace.network.rx.RxSchedulers;
import com.zxjk.moneyspace.ui.base.BaseActivity;
import com.zxjk.moneyspace.ui.msgpage.MyQrCodeActivity;
import com.zxjk.moneyspace.ui.widget.dialog.ChooseSexDialog;
import com.zxjk.moneyspace.ui.widget.provinces.Interface.OnCityItemClickListener;
import com.zxjk.moneyspace.ui.widget.provinces.JDCityPicker;
import com.zxjk.moneyspace.ui.widget.provinces.bean.CityBean;
import com.zxjk.moneyspace.ui.widget.provinces.bean.DistrictBean;
import com.zxjk.moneyspace.ui.widget.provinces.bean.ProvinceBean;
import com.zxjk.moneyspace.utils.CommonUtils;
import com.zxjk.moneyspace.utils.GlideUtil;
import com.zxjk.moneyspace.utils.MMKVUtils;
import com.zxjk.moneyspace.utils.OssUtils;
import com.zxjk.moneyspace.utils.TakePicUtil;

import java.io.File;
import java.util.Collections;

import io.rong.imkit.RongIM;
import io.rong.imlib.model.UserInfo;

@SuppressLint("CheckResult")
public class UserInfoActivity extends BaseActivity {
    String type = "type";
    int changeNick = 2;
    int changeSign = 1;
    int changeEmail = 3;
    private RelativeLayout rlEmial;
    private RelativeLayout rlPhone;
    private TextView tvUserInfoSex;
    private TextView tv_DuoDuoNumber;
    private TextView tv_realName;
    private TextView tv_phoneNumber;
    private TextView tv_personalizedSignature;
    private TextView tv_email;
    private ImageView iv_headPortrait;
    private TextView tv_nickname;
    private TextView tvArea;
    private JDCityPicker cityPicker;
    private ChooseSexDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);

        initPicker();

        initView();

        initDialog();
    }

    private void initPicker() {
        cityPicker = new JDCityPicker();
        cityPicker.init(this);
        cityPicker.setOnCityItemClickListener(new OnCityItemClickListener() {
            @Override
            public void onSelected(ProvinceBean province, CityBean city, DistrictBean district) {
                super.onSelected(province, city, district);
                LoginResponse update = new LoginResponse(Constant.userId);
                update.setAddress(province.getName() + city.getName() + district.getName());
                ServiceFactory.getInstance().getBaseService(Api.class)
                        .updateUserInfo(GsonUtils.toJson(update))
                        .compose(bindToLifecycle())
                        .compose(RxSchedulers.normalTrans())
                        .compose(RxSchedulers.ioObserver(CommonUtils.initDialog(UserInfoActivity.this)))
                        .subscribe(s -> {
                            String cityName = city.getName();
                            if (city.getName().equals("省直辖县级行政单位")) {
                                cityName = "";
                            }
                            Constant.currentUser.setAddress(province.getName() + cityName + district.getName());
                            tvArea.setText(province.getName() + cityName + district.getName());
                            MMKVUtils.getInstance().enCode("login", Constant.currentUser);
                            ToastUtils.showShort(R.string.update_success);
                        }, UserInfoActivity.this::handleApiError);
            }
        });
    }

    private void initDialog() {
        dialog = new ChooseSexDialog(this, sex -> {
            if (sex.equals(Constant.currentUser.getSex())) {
                return;
            }
            LoginResponse update = new LoginResponse(Constant.currentUser.getId());
            update.setSex(sex);
            ServiceFactory.getInstance().getBaseService(Api.class)
                    .updateUserInfo(GsonUtils.toJson(update))
                    .compose(bindToLifecycle())
                    .compose(RxSchedulers.normalTrans())
                    .compose(RxSchedulers.ioObserver(CommonUtils.initDialog(this)))
                    .subscribe(s -> {
                        Constant.currentUser.setSex(sex);
                        MMKVUtils.getInstance().enCode("login", Constant.currentUser);
                        tvUserInfoSex.setText(CommonUtils.getSex(Constant.currentUser.getSex()));
                    }, this::handleApiError);
        });
    }

    @SuppressLint("SetTextI18n")
    private void bindData() {
        GlideUtil.loadCircleImg(iv_headPortrait, Constant.currentUser.getHeadPortrait());
        tv_nickname.setText(Constant.currentUser.getNick());
        tvUserInfoSex.setText(CommonUtils.getSex(Constant.currentUser.getSex()));
        tv_DuoDuoNumber.setText(Constant.currentUser.getDuoduoId());
        tv_realName.setText(TextUtils.isEmpty(Constant.currentUser.getRealname()) ? getString(R.string.authen_false) : Constant.currentUser.getRealname());
        tv_personalizedSignature.setText(TextUtils.isEmpty(Constant.currentUser.getSignature()) ? getString(R.string.none) : Constant.currentUser.getSignature());
        tvArea.setText(Constant.currentUser.getAddress());

        String mobile = Constant.currentUser.getMobile();
        String email = Constant.currentUser.getEmail();

        if (!TextUtils.isEmpty(mobile)) {
            rlPhone.setVisibility(View.VISIBLE);

            try {
                tv_phoneNumber.setText(mobile.substring(0, 3) + "****" + mobile.substring(7));
            } catch (Exception e) {
                tv_phoneNumber.setText(mobile);
            }
        }

        if (!TextUtils.isEmpty(email)) {
            rlEmial.setVisibility(View.VISIBLE);
            tv_email.setText(email);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        isAuthentication();
        bindData();
    }

    private void isAuthentication() {
        if (!Constant.currentUser.getIsAuthentication().equals("0")) {
            ServiceFactory.getInstance().getBaseService(Api.class)
                    .getCustomerAuth()
                    .compose(bindToLifecycle())
                    .compose(RxSchedulers.normalTrans())
                    .compose(RxSchedulers.ioObserver(CommonUtils.initDialog(this)))
                    .subscribe(s -> {
                        Constant.currentUser.setIsAuthentication(s);
                        MMKVUtils.getInstance().enCode("login", Constant.currentUser);
                        switch (s) {
                            case "0":
                                tv_realName.setText(R.string.authen_true);
                                break;
                            case "2":
                                tv_realName.setText(R.string.authening);
                                break;
                            case "1":
                                tv_realName.setText(R.string.authenfail);
                                break;
                            default:
                                tv_realName.setText(R.string.authen_false);
                                break;
                        }
                    }, this::handleApiError);
        }
    }

    private void initView() {
        TextView tv_title = findViewById(R.id.tv_title);
        tv_title.setText(getString(R.string.userinfo));
        findViewById(R.id.rl_back).setOnClickListener(v -> finish());
        iv_headPortrait = findViewById(R.id.iv_headPortrait);
        RelativeLayout rl_headPortrait = findViewById(R.id.rl_headPortrait);
        tv_nickname = findViewById(R.id.tv_nickname);
        tvUserInfoSex = findViewById(R.id.tvUserInfoSex);
        tv_DuoDuoNumber = findViewById(R.id.tv_DuoDuoNumber);
        tv_realName = findViewById(R.id.tv_realName);
        tv_phoneNumber = findViewById(R.id.tv_phoneNumber);
        tv_personalizedSignature = findViewById(R.id.tv_personalizedSignature);
        tv_email = findViewById(R.id.tv_email);
        tvArea = findViewById(R.id.tvArea);

        rlEmial = findViewById(R.id.rlEmail);
        rlPhone = findViewById(R.id.rlPhone);

        getPermisson(rl_headPortrait, result -> {
            if (result) {
                dialogType();
            }
        }, Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE);
    }

    //修改昵称
    public void changeNick(View view) {
        Intent intent = new Intent(this, UpdateUserInfoActivity.class);
        intent.putExtra(type, changeNick);
        startActivity(intent);
    }

    //修改手机
    public void changeMobile(View view) {
        startActivity(new Intent(this, ChangePhoneActivity.class));
    }

    //修改性别
    public void changeSex(View view) {
        dialog.show();
    }

    //我的二维码
    public void QRCode(View view) {
        startActivity(new Intent(this, MyQrCodeActivity.class));
    }

    //修改个性签名
    public void changeSign(View view) {
        Intent intent = new Intent(this, UpdateUserInfoActivity.class);
        intent.putExtra(type, changeSign);
        startActivity(intent);
    }

    //修改Email
    public void changeEmail(View view) {
//        Intent intent = new Intent(this, UpdateUserInfoActivity.class);
//        intent.putExtra(type, changeEmail);
//        startActivity(intent);
    }

    //修改地区
    public void chooseArea(View view) {
        cityPicker.showCityPicker();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (corpFile != null) {
            zipFile(Collections.singletonList(corpFile.getPath()), files -> {
                File file = files.get(0);
                OssUtils.uploadFile(file.getAbsolutePath(), url -> {
                    LoginResponse update = new LoginResponse(Constant.userId);
                    update.setHeadPortrait(url);
                    ServiceFactory.getInstance().getBaseService(Api.class)
                            .updateUserInfo(GsonUtils.toJson(update))
                            .compose(bindToLifecycle())
                            .compose(RxSchedulers.ioObserver(CommonUtils.initDialog(UserInfoActivity.this)))
                            .compose(RxSchedulers.normalTrans())
                            .subscribe(response -> {
                                Constant.currentUser.setHeadPortrait(url);
                                MMKVUtils.getInstance().enCode("login", Constant.currentUser);
                                GlideUtil.loadCircleImg(iv_headPortrait, url);

                                UserInfo userInfo = new UserInfo(Constant.userId, Constant.currentUser.getNick(), Uri.parse(url));
                                RongIM.getInstance().refreshUserInfoCache(userInfo);
                                ToastUtils.showShort(R.string.update_head_portrail);
                            }, UserInfoActivity.this::handleApiError);
                });
            });
        }
    }

    private void dialogType() {
        KeyboardUtils.hideSoftInput(UserInfoActivity.this);
        NiceDialog.init().setLayoutId(R.layout.layout_general_dialog6).setConvertListener(new ViewConvertListener() {
            @Override
            protected void convertView(ViewHolder holder, BaseNiceDialog dialog) {
                holder.setOnClickListener(R.id.tv_photograph, v -> {
                    dialog.dismiss();
                    TakePicUtil.takePicture(UserInfoActivity.this);
                });
                holder.setOnClickListener(R.id.tv_photo_select, v -> {
                    dialog.dismiss();
                    TakePicUtil.albumPhoto(UserInfoActivity.this);
                });
                holder.setOnClickListener(R.id.tv_cancel, v -> dialog.dismiss());

            }
        }).setShowBottom(true)
                .setOutCancel(true)
                .setDimAmount(0.5f)
                .show(getSupportFragmentManager());
    }
}

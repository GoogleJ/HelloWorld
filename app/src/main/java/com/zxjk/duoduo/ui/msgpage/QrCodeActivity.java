package com.zxjk.duoduo.ui.msgpage;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.blankj.utilcode.util.RegexUtils;
import com.blankj.utilcode.util.ScreenUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.zxjk.duoduo.Constant;
import com.zxjk.duoduo.R;
import com.zxjk.duoduo.ui.WebActivity;
import com.zxjk.duoduo.ui.base.BaseActivity;
import com.zxjk.duoduo.ui.minepage.scanuri.Action1;
import com.zxjk.duoduo.ui.minepage.scanuri.BaseUri;
import com.zxjk.duoduo.ui.socialspace.SocialHomeActivity;
import com.zxjk.duoduo.ui.socialspace.SocialQRCodeActivity;
import com.zxjk.duoduo.utils.CommonUtils;
import com.zxjk.duoduo.utils.TakePicUtil;

import org.json.JSONObject;

import java.util.concurrent.TimeUnit;

import cn.bingoogolapple.qrcode.core.QRCodeView;
import cn.bingoogolapple.qrcode.zxing.ZXingView;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;

@SuppressLint("CheckResult")
public class QrCodeActivity extends BaseActivity implements QRCodeView.Delegate {
    public static final String ACTION_IMPORT_WALLET = "import_wallet";
    private String actionType;

    private ZXingView zxingview;
    private TextView tv_end;
    private TextView tv_title;

    protected void initUI() {
        tv_end = findViewById(R.id.tv_end);
        zxingview = findViewById(R.id.m_qr_code_zxing_view);
        zxingview.setDelegate(this);
        tv_title = findViewById(R.id.tv_title);
        tv_title.setText(getString(R.string.m_add_friend_scan_it_label_1));
        findViewById(R.id.rl_back).setOnClickListener(v -> finish());
        tv_end.setVisibility(View.VISIBLE);
        tv_end.setText(R.string.album);
        tv_end.setTextColor(ContextCompat.getColor(this, R.color.white));
        tv_title.setTextColor(ContextCompat.getColor(this, R.color.white));
        ImageView imageView = findViewById(R.id.ivBack);
        imageView.setImageResource(R.drawable.ico_back_white);
        findViewById(R.id.rlTitle).setBackgroundColor(ContextCompat.getColor(this, R.color.black));
        getPermisson(tv_end, granted -> TakePicUtil.albumPhoto(QrCodeActivity.this, false),
                Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ScreenUtils.setFullScreen(this);
        setContentView(R.layout.activity_qr_code);

        actionType = getIntent().getStringExtra("actionType");

        initUI();
    }

    @Override
    public void onScanQRCodeSuccess(String result) {
        Ringtone rt = RingtoneManager.getRingtone(getApplicationContext(), RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
        rt.play();

        if (!TextUtils.isEmpty(actionType)) {
            if (actionType.equals(ACTION_IMPORT_WALLET)) {
                Intent resultIntent = new Intent();
                resultIntent.putExtra("result", result);
                setResult(1, resultIntent);
                finish();
                return;
            }
        }

        String regexUrl = "^https?:/{2}\\w.+$";
        if (RegexUtils.isMatch(regexUrl, result)) {
            Intent intent = new Intent(this, WebActivity.class);
            intent.putExtra("url", result);
            startActivity(intent);
            finish();
            return;
        }

        try {
            JSONObject jsonObject = new JSONObject(result);
            Object schem = jsonObject.opt("schem");
            if (!schem.equals("com.zxjk.duoduo")) {
                throw new RuntimeException();
            }

            Object action = jsonObject.opt("action");

            if (action.equals("action1")) {
                BaseUri<Action1> uri = new Gson().fromJson(result, new TypeToken<BaseUri<Action1>>() {
                }.getType());
                Intent intent = new Intent(this, TransferActivity.class);
                intent.putExtra("fromScan", true);
                intent.putExtra("betMoney", uri.data.money);
                intent.putExtra("userId", uri.data.userId);
                intent.putExtra("symbol", uri.data.symbol);
                intent.putExtra("logo", uri.data.logo);
                startActivity(intent);
                finish();
            } else if (action.equals("action2")) {
                BaseUri<String> uri = new Gson().fromJson(result, new TypeToken<BaseUri<String>>() {
                }.getType());

                String userId = uri.data;

                if (userId.equals(Constant.userId)) {
                    //扫到了自己的二维码
                    finish();
                    return;
                }

                CommonUtils.resolveFriendList(this, userId, true);
            } else if (action.equals("action3")) {
                ToastUtils.showShort("群组二维码已过期，请使用社群二维码");
            } else if (action.equals("action4")) {
                BaseUri<SocialQRCodeActivity.QRCodeData> uri = new Gson().fromJson(result, new TypeToken<BaseUri<SocialQRCodeActivity.QRCodeData>>() {
                }.getType());
                Intent intent = new Intent(this, SocialHomeActivity.class);
                intent.putExtra("id", uri.data.groupId);
                startActivity(intent);
                finish();
            }
        } catch (Exception e) {
            ToastUtils.showShort(R.string.decode_qr_failure);
            Observable.timer(3, TimeUnit.SECONDS)
                    .compose(bindToLifecycle())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(l -> zxingview.startSpot());
        }
    }

    @Override
    public void onCameraAmbientBrightnessChanged(boolean isDark) {
        if (isDark) {
            zxingview.openFlashlight();
        }
    }

    @Override
    public void onScanQRCodeOpenCameraError() {

    }

    /**
     * 开始扫描
     */
    private void startScan() {
        zxingview.startCamera();
        zxingview.startSpotAndShowRect();
        zxingview.startSpot(); // 开始识别
    }

    /**
     * 关闭扫描，同时关灯 关摄像头
     */
    private void stopScan() {
        zxingview.stopCamera();
        zxingview.stopSpotAndHiddenRect();
        zxingview.closeFlashlight();
    }

    @Override
    protected void onStart() {
        super.onStart();
        startScan();
    }

    @Override
    protected void onStop() {
        stopScan();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        zxingview.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        zxingview.startSpotAndShowRect(); // 显示扫描框，并开始识别
        if (resultCode == Activity.RESULT_OK) {
            zxingview.decodeQRCode(TakePicUtil.getPath(this, data.getData()));
        }
    }
}

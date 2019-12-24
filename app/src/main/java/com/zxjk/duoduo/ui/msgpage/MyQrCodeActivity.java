package com.zxjk.duoduo.ui.msgpage;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.blankj.utilcode.util.BarUtils;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.zxjk.duoduo.Constant;
import com.zxjk.duoduo.R;
import com.zxjk.duoduo.network.rx.RxSchedulers;
import com.zxjk.duoduo.ui.base.BaseActivity;
import com.zxjk.duoduo.ui.minepage.scanuri.BaseUri;
import com.zxjk.duoduo.utils.GlideUtil;

import net.lucode.hackware.magicindicator.buildins.UIUtil;

import cn.bingoogolapple.qrcode.zxing.QRCodeEncoder;
import de.hdodenhof.circleimageview.CircleImageView;
import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;

public class MyQrCodeActivity extends BaseActivity {
    private ImageView ivQRImg;
    private ImageView ivSex;
    private TextView tv_title;
    private TextView tvUserName;
    private TextView tvLocation;
    private CircleImageView ivHead;
    private CircleImageView ivHeadQR;

    private BaseUri uri = new BaseUri("action2");
    private String uri2Code;

    public static void start(AppCompatActivity activity) {
        Intent intent = new Intent(activity, MyQrCodeActivity.class);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BarUtils.setStatusBarColor(this, Color.parseColor("#F2F3F5"));

        setContentView(R.layout.activity_my_qr_code);

        tv_title = findViewById(R.id.tv_title);
        ivQRImg = findViewById(R.id.ivQRImg);
        ivSex = findViewById(R.id.ivSex);
        ivHead = findViewById(R.id.ivHead);
        ivHeadQR = findViewById(R.id.ivHeadQR);
        tvUserName = findViewById(R.id.tvUserName);
        tvLocation = findViewById(R.id.tvLocation);

        Glide.with(this).load(Constant.currentUser.getHeadPortrait()).into(ivHeadQR);
        Glide.with(this).load(Constant.currentUser.getHeadPortrait()).into(ivHead);

        findViewById(R.id.rl_back).setOnClickListener(v -> finish());

        tv_title.setText(getString(R.string.qr_code));
        uri.data = Constant.userId;
        uri2Code = new Gson().toJson(uri);

        tvUserName.setText(Constant.currentUser.getNick());
        tvLocation.setText(Constant.currentUser.getAddress());

        if (Constant.currentUser.getSex().equals("0")) {
            ivSex.setImageResource(R.drawable.ic_myqr_male);
        } else {
            ivSex.setImageResource(R.drawable.ic_myqr_female);
        }

        getCodeBitmap();
    }

    @SuppressLint("CheckResult")
    private void getCodeBitmap() {
        Observable.create((ObservableOnSubscribe<Bitmap>) e -> {
            Bitmap bitmap = QRCodeEncoder.syncEncodeQRCode(uri2Code, UIUtil.dip2px(this, 200), Color.BLACK);
            e.onNext(bitmap);
        })
                .compose(RxSchedulers.ioObserver())
                .compose(bindToLifecycle())
                .subscribe(b -> ivQRImg.setImageBitmap(b));
    }
}

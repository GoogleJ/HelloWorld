package com.zxjk.moneyspace.ui.msgpage;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.blankj.utilcode.util.BarUtils;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.zxjk.moneyspace.Constant;
import com.zxjk.moneyspace.R;
import com.zxjk.moneyspace.network.rx.RxSchedulers;
import com.zxjk.moneyspace.ui.base.BaseActivity;
import com.zxjk.moneyspace.ui.minepage.scanuri.BaseUri;

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
    private BaseUri uri = new BaseUri("action2");
    private String uri2Code;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BarUtils.setStatusBarColor(this, Color.parseColor("#272E3F"));

        setContentView(R.layout.activity_my_qr_code);

        tv_title = findViewById(R.id.tv_title);
        ivQRImg = findViewById(R.id.ivQRImg);
        ivSex = findViewById(R.id.ivSex);
        ivHead = findViewById(R.id.ivHead);
        tvUserName = findViewById(R.id.tvUserName);
        tvLocation = findViewById(R.id.tvLocation);

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
            Bitmap bitmap = QRCodeEncoder.syncEncodeQRCode(uri2Code, UIUtil.dip2px(this, 180), Color.BLACK);
            e.onNext(bitmap);
        })
                .compose(RxSchedulers.ioObserver())
                .compose(bindToLifecycle())
                .subscribe(b -> ivQRImg.setImageBitmap(b));
    }
}

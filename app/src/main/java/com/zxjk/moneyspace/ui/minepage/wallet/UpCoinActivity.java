package com.zxjk.moneyspace.ui.minepage.wallet;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.blankj.utilcode.util.ToastUtils;
import com.blankj.utilcode.util.Utils;
import com.zxjk.moneyspace.R;
import com.zxjk.moneyspace.bean.response.GetBalanceInfoResponse;
import com.zxjk.moneyspace.network.rx.RxSchedulers;
import com.zxjk.moneyspace.ui.base.BaseActivity;
import com.zxjk.moneyspace.utils.GlideUtil;

import net.lucode.hackware.magicindicator.buildins.UIUtil;

import cn.bingoogolapple.qrcode.zxing.QRCodeEncoder;
import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;

public class UpCoinActivity extends BaseActivity {

    private GetBalanceInfoResponse.BalanceListBean data;

    private ImageView ivLogo;
    private ImageView ivQR;
    private TextView tvSymbol;
    private TextView tvAddress;
    private TextView tvTips;

    @SuppressLint("CheckResult")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_up_coin);

        data = getIntent().getParcelableExtra("data");

        TextView title = findViewById(R.id.tv_title);
        title.setText(R.string.collection_code);
        findViewById(R.id.rl_back).setOnClickListener(v -> finish());

        ivLogo = findViewById(R.id.ivLogo);
        ivQR = findViewById(R.id.ivQR);
        tvSymbol = findViewById(R.id.tvSymbol);
        tvAddress = findViewById(R.id.tvAddress);
        tvTips = findViewById(R.id.tvTips);

        GlideUtil.loadNormalImg(ivLogo, data.getLogo());
        tvAddress.setText(data.getBalanceAddress());
        tvSymbol.setText(data.getCurrencyName());

        Observable.create((ObservableOnSubscribe<Bitmap>) e -> e.onNext(QRCodeEncoder.syncEncodeQRCode(data.getBalanceAddress(), UIUtil.dip2px(this, 160), Color.BLACK)))
                .compose(bindToLifecycle())
                .compose(RxSchedulers.ioObserver())
                .subscribe(ivQR::setImageBitmap);

        tvTips.setText(getString(R.string.tips_upcoin, data.getCurrencyName()));
    }

    public void copyAddress(View view) {
        ToastUtils.showShort(R.string.duplicated_to_clipboard);
        ClipboardManager cm = (ClipboardManager) Utils.getApp().getSystemService(Context.CLIPBOARD_SERVICE);
        if (cm != null) {
            cm.setPrimaryClip(ClipData.newPlainText("text", data.getBalanceAddress()));
        }
    }
}

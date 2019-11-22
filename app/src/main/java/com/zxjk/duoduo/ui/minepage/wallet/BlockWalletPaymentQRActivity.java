package com.zxjk.duoduo.ui.minepage.wallet;

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
import com.zxjk.duoduo.R;
import com.zxjk.duoduo.network.rx.RxSchedulers;
import com.zxjk.duoduo.ui.base.BaseActivity;
import com.zxjk.duoduo.utils.GlideUtil;

import net.lucode.hackware.magicindicator.buildins.UIUtil;

import cn.bingoogolapple.qrcode.zxing.QRCodeEncoder;
import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;

public class BlockWalletPaymentQRActivity extends BaseActivity {

    private String symbol;
    private String address;
    private String logo;

    private ImageView ivLogo;
    private ImageView ivQr;
    private TextView tvAddress;
    private TextView tvTips;

    @SuppressLint("CheckResult")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_block_wallet_payment_qr);

        symbol = getIntent().getStringExtra("symbol");
        address = getIntent().getStringExtra("address");
        logo = getIntent().getStringExtra("logo");

        TextView title = findViewById(R.id.tv_title);
        title.setText(R.string.receiptCode);
        findViewById(R.id.rl_back).setOnClickListener(v -> finish());

        ivLogo = findViewById(R.id.ivLogo);
        ivQr = findViewById(R.id.ivQr);
        tvAddress = findViewById(R.id.tvAddress);
        tvTips = findViewById(R.id.tvTips);

        tvTips.setText("注意：该地址仅用于" + symbol + "钱包收款，请勿用于其他币种");
        GlideUtil.loadNormalImg(ivLogo, logo);
        tvAddress.setText(address);

        Observable.create((ObservableOnSubscribe<Bitmap>) e ->
                e.onNext(QRCodeEncoder.syncEncodeQRCode(address, UIUtil.dip2px(this, 160), Color.BLACK)))
                .compose(RxSchedulers.ioObserver())
                .compose(bindToLifecycle())
                .subscribe(b -> ivQr.setImageBitmap(b));
    }

    public void copyAddress(View view) {
        ToastUtils.showShort(R.string.duplicated_to_clipboard);
        ClipboardManager cm = (ClipboardManager) Utils.getApp().getSystemService(Context.CLIPBOARD_SERVICE);
        if (cm != null) {
            cm.setPrimaryClip(ClipData.newPlainText("text", address));
        }
    }
}

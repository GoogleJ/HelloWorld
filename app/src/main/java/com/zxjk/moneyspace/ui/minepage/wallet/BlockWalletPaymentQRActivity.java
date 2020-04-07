package com.zxjk.moneyspace.ui.minepage.wallet;

import android.Manifest;
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
import com.zxjk.moneyspace.network.rx.RxSchedulers;
import com.zxjk.moneyspace.ui.base.BaseActivity;
import com.zxjk.moneyspace.utils.GlideUtil;
import com.zxjk.moneyspace.utils.SaveImageUtil;

import net.lucode.hackware.magicindicator.buildins.UIUtil;

import cn.bingoogolapple.qrcode.zxing.QRCodeEncoder;
import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;

@SuppressLint("CheckResult")
public class BlockWalletPaymentQRActivity extends BaseActivity {

    private String symbol;
    private String address;
    private String logo;

    private ImageView ivLogo;
    private ImageView ivQr;
    private TextView tvAddress;
    private TextView tvTips;
    private TextView mTvCurrency;

    @SuppressLint("CheckResult")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_block_wallet_payment_qr);

        initView();

        initData();
    }

    private void initView() {
        TextView title = findViewById(R.id.tv_title);
        title.setText(R.string.receiptCode);

        ivLogo = findViewById(R.id.ivLogo);
        ivQr = findViewById(R.id.iv_block_Qr);
        tvAddress = findViewById(R.id.tvAddress);
        tvTips = findViewById(R.id.tvTips);
        mTvCurrency = findViewById(R.id.iv_currency);
    }

    private Bitmap bitmap;

    private void initData() {
        symbol = getIntent().getStringExtra("symbol");
        address = getIntent().getStringExtra("address");
        logo = getIntent().getStringExtra("logo");
        findViewById(R.id.rl_back).setOnClickListener(v -> finish());
        tvTips.setText(getString(R.string.tips_blockwallet_qr, symbol));
        GlideUtil.loadNormalImg(ivLogo, logo);
        tvAddress.setText(address);
        mTvCurrency.setText(symbol);

        Observable.create((ObservableOnSubscribe<Bitmap>) e ->
                e.onNext(QRCodeEncoder.syncEncodeQRCode(address, UIUtil.dip2px(this, 160), Color.BLACK)))
                .compose(RxSchedulers.ioObserver())
                .compose(bindToLifecycle())
                .subscribe(b -> {
                    bitmap = b;
                    ivQr.setImageBitmap(b);
                });

        getPermisson(findViewById(R.id.tv_save_qr), g -> {
            if (bitmap == null) {
                return;
            }
            ivQr.buildDrawingCache();

            SaveImageUtil.get().savePic(ivQr.getDrawingCache(), success -> {
                if (success) {
                    ToastUtils.showShort(R.string.savesucceed);
                    return;
                }
                ToastUtils.showShort(R.string.savefailed);
            });
        }, Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE);
    }


    public void copyAddress(View view) {
        ToastUtils.showShort(R.string.duplicated_to_clipboard);
        ClipboardManager cm = (ClipboardManager) Utils.getApp().getSystemService(Context.CLIPBOARD_SERVICE);
        if (cm != null) {
            cm.setPrimaryClip(ClipData.newPlainText("text", address));
        }
    }

}

package com.zxjk.moneyspace.ui.minepage.wallet;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.blankj.utilcode.util.ToastUtils;
import com.blankj.utilcode.util.Utils;
import com.zxjk.moneyspace.R;
import com.zxjk.moneyspace.network.rx.RxSchedulers;
import com.zxjk.moneyspace.ui.base.BaseActivity;
import com.zxjk.moneyspace.utils.AesUtil;

import net.lucode.hackware.magicindicator.buildins.UIUtil;

import cn.bingoogolapple.qrcode.zxing.QRCodeEncoder;
import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import razerdp.basepopup.QuickPopupBuilder;
import razerdp.basepopup.QuickPopupConfig;
import razerdp.widget.QuickPopup;

public class BackupKeystoreActivity extends BaseActivity {
    private QuickPopup popup;

    private String keystore;

    private TextView tvKeystore;
    private TextView tvQr;
    private View line1;
    private View line2;

    private LinearLayout llKeystore;
    private TextView tvKeystoreContent;

    private LinearLayout llQr;
    private LinearLayout llWarn;
    private FrameLayout flQr;
    private ImageView ivQr;
    private Bitmap bitmapQr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_backup_keystore);

        keystore = getIntent().getStringExtra("keystore");
        if (TextUtils.isEmpty(keystore)) {
            finish();
            return;
        }
        keystore = AesUtil.getInstance().decrypt(keystore);

        initView();

        initWarnPop();
    }

    private void initView() {
        TextView title = findViewById(R.id.tv_title);
        title.setText(R.string.wallet_manage_detail_tips3);
        findViewById(R.id.rl_back).setOnClickListener(v -> finish());

        tvKeystore = findViewById(R.id.tvKeystore);
        tvQr = findViewById(R.id.tvQr);
        line1 = findViewById(R.id.line1);
        line2 = findViewById(R.id.line2);

        llKeystore = findViewById(R.id.llKeystore);
        tvKeystoreContent = findViewById(R.id.tvKeystoreContent);

        llQr = findViewById(R.id.llQr);
        llWarn = findViewById(R.id.llWarn);
        flQr = findViewById(R.id.flQr);
        ivQr = findViewById(R.id.ivQr);
    }

    private void initWarnPop() {
        if (popup == null) {
            ScaleAnimation showAnim = new ScaleAnimation(0f, 1f, 0f, 1f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            ScaleAnimation hideAnim = new ScaleAnimation(1f, 0f, 1f, 0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            hideAnim.setDuration(250);
            showAnim.setDuration(250);
            popup = QuickPopupBuilder.with(this).contentView(R.layout.popup_backupkeystore)
                    .config(new QuickPopupConfig()
                            .withClick(R.id.btnConfirm, v -> tvKeystoreContent.setText(keystore), true)
                            .withShowAnimation(showAnim)
                            .withDismissAnimation(hideAnim)
                            .dismissOnOutSideTouch(false)
                            .blurBackground(true)
                    ).show();
        } else {
            popup.showPopupWindow();
        }
    }

    public void qr(View view) {
        if (llQr.getVisibility() == View.VISIBLE) {
            return;
        }
        tvQr.setTextColor(ContextCompat.getColor(this, R.color.colorTheme));
        tvKeystore.setTextColor(ContextCompat.getColor(this, R.color.black));
        line2.setVisibility(View.VISIBLE);
        line1.setVisibility(View.INVISIBLE);
        llKeystore.setVisibility(View.GONE);
        llQr.setVisibility(View.VISIBLE);
        tvKeystoreContent.setText("");
        llWarn.setVisibility(View.VISIBLE);
        flQr.setVisibility(View.GONE);
    }

    public void keystore(View view) {
        if (llKeystore.getVisibility() == View.VISIBLE) {
            return;
        }
        tvKeystore.setTextColor(ContextCompat.getColor(this, R.color.colorTheme));
        tvQr.setTextColor(ContextCompat.getColor(this, R.color.black));
        line1.setVisibility(View.VISIBLE);
        line2.setVisibility(View.INVISIBLE);
        llKeystore.setVisibility(View.VISIBLE);
        llQr.setVisibility(View.GONE);
        initWarnPop();
    }

    public void copyKeystore(View view) {
        ToastUtils.showShort(R.string.duplicated_to_clipboard);
        ClipboardManager cm = (ClipboardManager) Utils.getApp().getSystemService(Context.CLIPBOARD_SERVICE);
        if (cm != null) {
            cm.setPrimaryClip(ClipData.newPlainText("text", keystore));
        }
    }

    @SuppressLint("CheckResult")
    public void showQr(View view) {
        llWarn.setVisibility(View.GONE);
        flQr.setVisibility(View.VISIBLE);

        if (bitmapQr == null) {
            Observable.create((ObservableOnSubscribe<Bitmap>) e -> {
                bitmapQr = QRCodeEncoder.syncEncodeQRCode(keystore, UIUtil.dip2px(this, 216), Color.BLACK, Color.parseColor("#f1f1f1"), null);
                e.onNext(bitmapQr);
            })
                    .compose(RxSchedulers.ioObserver())
                    .compose(bindToLifecycle())
                    .subscribe(b -> ivQr.setImageBitmap(b));
        } else {
            ivQr.setImageBitmap(bitmapQr);
        }
    }
}

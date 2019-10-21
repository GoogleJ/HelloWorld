package com.zxjk.duoduo.ui.minepage.wallet;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.blankj.utilcode.util.ToastUtils;
import com.blankj.utilcode.util.Utils;
import com.zxjk.duoduo.R;
import com.zxjk.duoduo.bean.response.GetMainSymbolByCustomerIdBean;
import com.zxjk.duoduo.network.Api;
import com.zxjk.duoduo.network.ServiceFactory;
import com.zxjk.duoduo.network.rx.RxSchedulers;
import com.zxjk.duoduo.ui.base.BaseActivity;
import com.zxjk.duoduo.ui.widget.NewPayBoard;
import com.zxjk.duoduo.utils.AesUtil;
import com.zxjk.duoduo.utils.CommonUtils;
import com.zxjk.duoduo.utils.GlideUtil;
import com.zxjk.duoduo.utils.MD5Utils;

import razerdp.basepopup.QuickPopupBuilder;
import razerdp.basepopup.QuickPopupConfig;
import razerdp.widget.QuickPopup;

public class BlockWalletManageDetailActivity extends BaseActivity {
    private final int CHANGE_NAME = 1;
    private final int DELETE_WALLET = 2;

    private GetMainSymbolByCustomerIdBean data;
    private boolean nameChanged;

    private ImageView ivLogo;
    private TextView tvMoney;
    private TextView tvAddress;
    private TextView title;
    private LinearLayout llWords;

    @SuppressLint("CheckResult")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_block_wallet_manage_detail);
        data = getIntent().getParcelableExtra("data");

        title = findViewById(R.id.tv_title);
        title.setText(data.getWalletName());
        findViewById(R.id.rl_back).setOnClickListener(v -> finish());

        ivLogo = findViewById(R.id.ivLogo);
        tvMoney = findViewById(R.id.tvMoney);
        tvAddress = findViewById(R.id.tvAddress);

        GlideUtil.loadNormalImg(ivLogo, data.getLogo());
        tvAddress.setText(data.getWalletAddress());

        ServiceFactory.getInstance().getBaseService(Api.class)
                .getBalanceByAddress(data.getSymbol(), data.getWalletAddress())
                .compose(bindToLifecycle())
                .compose(RxSchedulers.normalTrans())
                .compose(RxSchedulers.ioObserver(CommonUtils.initDialog(this)))
                .subscribe(s -> tvMoney.setText(s), t -> {
                    handleApiError(t);
                    finish();
                });

        llWords = findViewById(R.id.llWords);
        if (!data.getImportMethod().equals("3")) {
            llWords.setVisibility(View.GONE);
        }
    }

    //修改钱包名称
    public void changeName(View view) {
        if (data == null) {
            return;
        }
        Intent intent = new Intent(this, ChangeWalletNameActivity.class);
        intent.putExtra("name", data.getWalletName());
        intent.putExtra("address", data.getWalletAddress());
        startActivityForResult(intent, CHANGE_NAME);
    }

    //导出助记词
    @SuppressLint("CheckResult")
    public void exportWords(View view) {
        if (data == null) {
            return;
        }
        new NewPayBoard(this).show(pwd -> ServiceFactory.getInstance().getBaseService(Api.class)
                .exportWalletInfo(data.getWalletAddress(), MD5Utils.getMD5(pwd), "0")
                .compose(bindToLifecycle())
                .compose(RxSchedulers.normalTrans())
                .compose(RxSchedulers.ioObserver(CommonUtils.initDialog(this)))
                .subscribe(s -> {
                    Intent intent = new Intent(this, BackupWordsActivity.class);
                    intent.putExtra("words", s);
                    startActivity(intent);
                }, this::handleApiError));
    }

    //导出keystore
    public void exportKeystore(View view) {
        if (data == null) {
            return;
        }
        new NewPayBoard(this).show(pwd -> ServiceFactory.getInstance().getBaseService(Api.class)
                .exportWalletInfo(data.getWalletAddress(), MD5Utils.getMD5(pwd), "2")
                .compose(bindToLifecycle())
                .compose(RxSchedulers.normalTrans())
                .compose(RxSchedulers.ioObserver(CommonUtils.initDialog(this)))
                .subscribe(s -> {
                    Intent intent = new Intent(this, BackupKeystoreActivity.class);
                    intent.putExtra("keystore", s);
                    startActivity(intent);
                }, this::handleApiError));
    }

    //导出私钥
    public void exportKey(View view) {
        if (data == null) {
            return;
        }
        new NewPayBoard(this).show(pwd -> ServiceFactory.getInstance().getBaseService(Api.class)
                .exportWalletInfo(data.getWalletAddress(), MD5Utils.getMD5(pwd), "1")
                .compose(bindToLifecycle())
                .compose(RxSchedulers.normalTrans())
                .compose(RxSchedulers.ioObserver(CommonUtils.initDialog(this)))
                .subscribe(s -> {
                    //弹出对话框
                    ScaleAnimation showAnim = new ScaleAnimation(0f, 1f, 0f, 1f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                    ScaleAnimation hideAnim = new ScaleAnimation(1f, 0f, 1f, 0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                    hideAnim.setDuration(250);
                    showAnim.setDuration(250);
                    String key = AesUtil.getInstance().decrypt(s);
                    QuickPopup popup = QuickPopupBuilder.with(this).contentView(R.layout.popup_backupkey)
                            .config(new QuickPopupConfig()
                                    .withClick(R.id.btnCopy, v -> {
                                        ToastUtils.showShort(R.string.duplicated_to_clipboard);
                                        ClipboardManager cm = (ClipboardManager) Utils.getApp().getSystemService(Context.CLIPBOARD_SERVICE);
                                        cm.setPrimaryClip(ClipData.newPlainText("text", key));
                                    }, true)
                                    .blurBackground(true)
                                    .withShowAnimation(showAnim)
                                    .withDismissAnimation(hideAnim).dismissOnOutSideTouch(false))
                            .show();
                    TextView tvKey = popup.getContentView().findViewById(R.id.tvKey);
                    tvKey.setText(key);
                }, this::handleApiError));
    }

    //删除钱包
    @SuppressLint("CheckResult")
    public void deleteWallet(View view) {
        if (data == null) {
            return;
        }

        new NewPayBoard(this).show(pwd -> {
            if (data.getImportMethod().equals("3")) {
                ServiceFactory.getInstance().getBaseService(Api.class)
                        .exportWalletInfo(data.getWalletAddress(), MD5Utils.getMD5(pwd), "2")
                        .compose(bindToLifecycle())
                        .compose(RxSchedulers.normalTrans())
                        .compose(RxSchedulers.ioObserver(CommonUtils.initDialog(this)))
                        .subscribe(s -> {
                            Intent intent = new Intent(this, DeleteBlockWalletActivity.class);
                            intent.putExtra("data", data);
                            startActivityForResult(intent, DELETE_WALLET);
                        }, this::handleApiError);
            } else {
                ServiceFactory.getInstance().getBaseService(Api.class)
                        .deleteWalletByKey(data.getWalletAddress(), data.getImportMethod(), data.getSymbol(), MD5Utils.getMD5(pwd))
                        .compose(bindToLifecycle())
                        .compose(RxSchedulers.normalTrans())
                        .compose(RxSchedulers.ioObserver(CommonUtils.initDialog(this)))
                        .subscribe(s -> {
                            setResult(2);
                            finish();
                        }, this::handleApiError);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CHANGE_NAME && resultCode == 1) {
            nameChanged = true;
            this.data.setWalletName(data.getStringExtra("name"));
            title.setText(this.data.getWalletName());
        }

        if (requestCode == DELETE_WALLET && resultCode == 1) {
            setResult(2);
            finish();
        }
    }

    @Override
    public void finish() {
        if (nameChanged) {
            setResult(1);
        }
        super.finish();
    }
}

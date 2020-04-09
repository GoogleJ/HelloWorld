package com.zxjk.moneyspace.ui.walletpage;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityOptionsCompat;

import com.blankj.utilcode.util.ToastUtils;
import com.shehuan.nicedialog.BaseNiceDialog;
import com.shehuan.nicedialog.NiceDialog;
import com.shehuan.nicedialog.ViewConvertListener;
import com.shehuan.nicedialog.ViewHolder;
import com.zxjk.moneyspace.R;
import com.zxjk.moneyspace.bean.response.ReleasePurchaseResponse;
import com.zxjk.moneyspace.bean.response.ReleaseSaleResponse;
import com.zxjk.moneyspace.network.Api;
import com.zxjk.moneyspace.network.ServiceFactory;
import com.zxjk.moneyspace.network.rx.RxSchedulers;
import com.zxjk.moneyspace.ui.ZoomActivity;
import com.zxjk.moneyspace.ui.base.BaseActivity;
import com.zxjk.moneyspace.utils.CommonUtils;
import com.zxjk.moneyspace.utils.OssUtils;
import com.zxjk.moneyspace.utils.TakePicUtil;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;

@SuppressLint("CheckResult")
public class ConfirmBuyActivity extends BaseActivity {

    @BindView(R.id.tv_title)
    TextView tvTitle;
    private String pictureUrl = ""; //支付凭证

    private static final int REQUEST_TAKE = 1;
    private static final int REQUEST_ALBUM = 2;

    private ReleaseSaleResponse data;
    private int limitNum = 3;

    private TextView tvConfirmBuyPayType; //支付类型
    private TextView tvConfirmBuyMoney; //需支付
    private TextView tvConfirmBuyReceiver; //收款人
    private TextView tvConfirmBuyReceiverAccount; //收款账号
    private TextView tvConfirmBuyReceiverQR; //收款二维码
    private TextView tvConfirmBuyOrderId; //订单号
    private TextView tvConfirmBuyReceiverCount; //购买数量
    private TextView tvConfirmBuyReceiverSinglePrice; //单价
    private TextView tvConfirmBuyOrderTime; //订单时间
    private TextView flagConfirmBuy; //支付凭证是否已上传？
    private TextView tvConfirmBuyCountDown; //倒计时

    private LinearLayout llConfirmBuyBank;
    private LinearLayout llConfirmBuyQR;
    private TextView tvConfirmBuyReceiverBank;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_buy);

        initView();

        initData();
    }

    private void initView() {
        ButterKnife.bind(this);
        tvTitle.setText(getString(R.string.buy));
        getPermisson(findViewById(R.id.llUploadSign), granted -> dialogType(),
                Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        tvConfirmBuyPayType = findViewById(R.id.tvConfirmBuyPayType);
        tvConfirmBuyMoney = findViewById(R.id.tvConfirmBuyMoney);
        tvConfirmBuyReceiver = findViewById(R.id.tvConfirmBuyReceiver);
        tvConfirmBuyReceiverAccount = findViewById(R.id.tvConfirmBuyReceiverAccount);
        tvConfirmBuyReceiverQR = findViewById(R.id.tvConfirmBuyReceiverQR);
        tvConfirmBuyOrderId = findViewById(R.id.tvConfirmBuyOrderId);
        tvConfirmBuyReceiverCount = findViewById(R.id.tvConfirmBuyReceiverCount);
        tvConfirmBuyReceiverSinglePrice = findViewById(R.id.tvConfirmBuyReceiverSinglePrice);
        tvConfirmBuyOrderTime = findViewById(R.id.tvConfirmBuyOrderTime);
        flagConfirmBuy = findViewById(R.id.flagConfirmBuy);
        tvConfirmBuyCountDown = findViewById(R.id.tvConfirmBuyCountDown);
        llConfirmBuyBank = findViewById(R.id.llConfirmBuyBank);
        llConfirmBuyQR = findViewById(R.id.llConfirmBuyQR);
        tvConfirmBuyReceiverBank = findViewById(R.id.tvConfirmBuyReceiverBank);
    }

    private void initData() {
        data = (ReleaseSaleResponse) getIntent().getSerializableExtra("data");
        String buytype = getIntent().getStringExtra("buytype");

        long l1 = (System.currentTimeMillis() - Long.parseLong(data.getCreateTime())) / 1000;
        long total = (900 - l1) <= 0 ? 0 : (900 - l1);
        Observable.interval(0, 1, TimeUnit.SECONDS)
                .take(total)
                .observeOn(AndroidSchedulers.mainThread())
                .compose(bindToLifecycle())
                .subscribe(l -> {
                    long minute = (total - l) / 60;
                    long second = (total - l) % 60;
                    tvConfirmBuyCountDown.setText(minute + ":" + (second == 60 ? "00" : ((second < 10 ? ("0" + second) : second))));
                    if (total == 0 || l == total - 1) {
                        ToastUtils.showShort(R.string.timeup);
                        finish();
                    }
                }, t -> {
                });

        Drawable drawable = getDrawable(R.drawable.ic_exchange_wechat);
        switch (buytype) {
            case "1":
                tvConfirmBuyPayType.setText(R.string.wechatPay);
                drawable = getDrawable(R.drawable.ic_exchange_wechat);
                break;
            case "2":
                tvConfirmBuyPayType.setText(R.string.alipay);
                drawable = getDrawable(R.drawable.ic_exchange_alipay);
                break;
            case "3":
                tvConfirmBuyPayType.setText(R.string.bankcard);
                drawable = getDrawable(R.drawable.ic_exchange_bankpay);
                llConfirmBuyQR.setVisibility(View.GONE);
                llConfirmBuyBank.setVisibility(View.VISIBLE);
                tvConfirmBuyReceiverBank.setText(data.getOpenBank());
                break;
            default:
        }
        tvConfirmBuyPayType.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null);
        tvConfirmBuyReceiver.setText(data.getNick());
        tvConfirmBuyReceiverAccount.setText(data.getReceiptNumber());
        tvConfirmBuyOrderId.setText(data.getBothOrderId());
        tvConfirmBuyReceiverCount.setText(data.getNumber());
        tvConfirmBuyReceiverSinglePrice.setText(getIntent().getStringExtra("rate"));
        tvConfirmBuyOrderTime.setText(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Long.parseLong(data.getCreateTime())));
        tvConfirmBuyMoney.setText(data.getMoney() + "CNY");

        if (!TextUtils.isEmpty(data.getDefaultLimitTransactions())) {
            try {
                limitNum = Integer.parseInt(data.getDefaultLimitTransactions());
            } catch (Exception e) {
            }
        }
    }

    //我已完成支付
    public void confirm(View view) {
        if (TextUtils.isEmpty(pictureUrl)) {
            ToastUtils.showShort(getString(R.string.please_upload_a_payment_voucher));
            return;
        }
        NiceDialog.init().setLayoutId(R.layout.layout_general_dialog).setConvertListener(new ViewConvertListener() {
            @Override
            protected void convertView(ViewHolder holder, BaseNiceDialog dialog) {
                holder.setText(R.id.tv_title, getString(R.string.payment_confirmation));
                holder.setText(R.id.tv_content, getString(R.string.please_confirm_that_you_have_paid_the_seller));
                holder.setText(R.id.tv_cancel, "取消");
                holder.setText(R.id.tv_notarize, "确认");
                holder.setOnClickListener(R.id.tv_cancel, v -> dialog.dismiss());
                holder.setOnClickListener(R.id.tv_notarize, v -> {
                    dialog.dismiss();
                    ServiceFactory.getInstance().getBaseService(Api.class)
                            .updateBuyPayState(data.getBothOrderId(), pictureUrl)
                            .compose(RxSchedulers.normalTrans())
                            .compose(bindToLifecycle())
                            .compose(RxSchedulers.ioObserver(CommonUtils.initDialog(ConfirmBuyActivity.this)))
                            .subscribe(s -> {
                                Intent intent = new Intent(ConfirmBuyActivity.this, com.zxjk.moneyspace.ui.walletpage.WaitForJudgeActivity.class);
                                data.setPayTime(String.valueOf(System.currentTimeMillis()));
                                intent.putExtra("data", data);
                                intent.putExtra("buytype", getIntent().getStringExtra("buytype"));
                                intent.putExtra("rate", getIntent().getStringExtra("rate"));
                                startActivity(intent);
                                finish();
                            }, ConfirmBuyActivity.this::handleApiError);
                });

            }
        }).setDimAmount(0.5f).setOutCancel(false).show(getSupportFragmentManager());
    }

    //取消订单
    public void cancelOrder(View view) {
        NiceDialog.init().setLayoutId(R.layout.layout_general_dialog).setConvertListener(new ViewConvertListener() {
            @Override
            protected void convertView(ViewHolder holder, BaseNiceDialog dialog) {
                holder.setText(R.id.tv_title, "取消订单");
                holder.setText(R.id.tv_content, "取消订单不会退款，一天内取消" + limitNum + "笔交易会限制买入功能。");
                holder.setText(R.id.tv_cancel, "取消");
                holder.setText(R.id.tv_notarize, "确认");
                holder.setOnClickListener(R.id.tv_cancel, v -> dialog.dismiss());
                holder.setOnClickListener(R.id.tv_notarize, v -> {
                    dialog.dismiss();
                    ServiceFactory.getInstance().getBaseService(Api.class)
                            .cancelled(data.getBuyOrderId(), data.getBothOrderId(), data.getSellOrderId())
                            .compose(RxSchedulers.normalTrans())
                            .compose(bindToLifecycle())
                            .compose(RxSchedulers.ioObserver(CommonUtils.initDialog(ConfirmBuyActivity.this)))
                            .subscribe(s -> {
                                ReleasePurchaseResponse result = new ReleasePurchaseResponse();
                                result.setSellOrderId(data.getBothOrderId());
                                result.setCurrency(data.getCurrency());
                                result.setNumber(data.getNumber());
                                result.setMoney(data.getMoney());
                                result.setPayType(getIntent().getStringExtra("buytype"));
                                Intent intent = new Intent(ConfirmBuyActivity.this, com.zxjk.moneyspace.ui.walletpage.CancelOrderActivity.class);
                                intent.putExtra("data", result);
                                intent.putExtra("rate", getIntent().getStringExtra("rate"));
                                startActivity(intent);
                                finish();
                            }, ConfirmBuyActivity.this::handleApiError);


                });

            }
        }).setDimAmount(0.5f).setOutCancel(false).show(getSupportFragmentManager());
    }

    public void showQR(View view) {
        if (llConfirmBuyBank.getVisibility() == View.VISIBLE) {
            return;
        }
        Intent intent5 = new Intent(this, ZoomActivity.class);
        intent5.putExtra("image", data.getReceiptPicture());
        startActivity(intent5,
                ActivityOptionsCompat.makeSceneTransitionAnimation(this,
                        tvConfirmBuyReceiverQR, "12").toBundle());
    }

    public void copyNick(View view) {
        ClipboardManager clipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        clipboardManager.setText(tvConfirmBuyReceiver.getText().toString());
        ToastUtils.showShort(getString(R.string.duplicated_to_clipboard));
    }

    public void copyAccount(View view) {
        ClipboardManager clipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        clipboardManager.setText(tvConfirmBuyReceiverAccount.getText().toString());
        ToastUtils.showShort(getString(R.string.duplicated_to_clipboard));
    }

    public void back(View view) {
        finish();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (corpFile != null) {
            CommonUtils.initDialog(this).show();
            zipFile(Collections.singletonList(corpFile.getPath()), files -> {
                File file = files.get(0);
                OssUtils.uploadFile(file.getAbsolutePath(), url -> {
                    CommonUtils.destoryDialog();
                    flagConfirmBuy.setText(R.string.upload_done);
                    ToastUtils.showShort(R.string.upload_done);
                    pictureUrl = url;
                });
            });
        }
    }

    @OnClick(R.id.rl_back)
    public void onViewClicked() {
        finish();
    }

    //底部弹窗dialog 拍照、选择相册、取消
    private void dialogType() {
        NiceDialog.init().setLayoutId(R.layout.layout_general_dialog6).setConvertListener(new ViewConvertListener() {
            @Override
            protected void convertView(ViewHolder holder, BaseNiceDialog dialog) {
                //拍照
                holder.setOnClickListener(R.id.tv_photograph, v -> {
                    dialog.dismiss();
                    TakePicUtil.takePicture(ConfirmBuyActivity.this);
                });
                //相册选择
                holder.setOnClickListener(R.id.tv_photo_select, v -> {
                    dialog.dismiss();
                    TakePicUtil.albumPhoto(ConfirmBuyActivity.this);
                });
                //取消
                holder.setOnClickListener(R.id.tv_cancel, v -> dialog.dismiss());

            }
        }).setShowBottom(true)
                .setOutCancel(true)
                .setDimAmount(0.5f)
                .show(getSupportFragmentManager());
    }

}

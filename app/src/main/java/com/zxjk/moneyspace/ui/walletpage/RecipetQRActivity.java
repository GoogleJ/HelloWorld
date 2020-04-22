package com.zxjk.moneyspace.ui.walletpage;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.blankj.utilcode.util.BarUtils;
import com.google.gson.Gson;
import com.zxjk.moneyspace.R;
import com.zxjk.moneyspace.bean.response.GetPaymentListBean;
import com.zxjk.moneyspace.network.Api;
import com.zxjk.moneyspace.network.ServiceFactory;
import com.zxjk.moneyspace.network.rx.RxSchedulers;
import com.zxjk.moneyspace.ui.base.BaseActivity;
import com.zxjk.moneyspace.ui.minepage.DetailListActivity;
import com.zxjk.moneyspace.ui.minepage.scanuri.Action1;
import com.zxjk.moneyspace.ui.minepage.scanuri.BaseUri;
import com.zxjk.moneyspace.ui.minepage.wallet.ChooseCoinActivity;
import com.zxjk.moneyspace.utils.CommonUtils;
import com.zxjk.moneyspace.utils.GlideUtil;
import com.zxjk.moneyspace.utils.QRCodeEncoder;

import net.lucode.hackware.magicindicator.buildins.UIUtil;

import java.util.ArrayList;

import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;

public class RecipetQRActivity extends BaseActivity {
    private static final int QR_SIZE = 192;
    private GetPaymentListBean result;
    private ArrayList<GetPaymentListBean> list = new ArrayList<>();
    private ImageView ivRecipetImg;
    private ImageView ivHead;
    private FrameLayout flTop;
    private TextView tvMoney, tv_setMoney;
    private BaseUri uri;
    private String uri2Code;
    private String money = "";
    private int imgSize;
    private boolean isSet;

    @SuppressLint("CheckResult")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipet_qr);
        BarUtils.setStatusBarColor(this, Color.parseColor("#272E3F"));

        ivRecipetImg = findViewById(R.id.ivRecipetImg);
        ivHead = findViewById(R.id.ivHead);
        tvMoney = findViewById(R.id.tvMoney);
        tv_setMoney = findViewById(R.id.tv_setMoney);
        flTop = findViewById(R.id.flTop);

        BarUtils.addMarginTopEqualStatusBarHeight(flTop);

        Api api = ServiceFactory.getInstance().getBaseService(Api.class);
        api.getPaymentList()
                .compose(bindToLifecycle())
                .compose(RxSchedulers.normalTrans())
                .compose(RxSchedulers.ioObserver(CommonUtils.initDialog(this)))
                .subscribe(list -> {
                    this.list.addAll(list);
                    result = list.get(0);
                    GlideUtil.loadCircleImg(ivHead, result.getLogo());
                    initUri();
                    createBitmap();
                    tvMoney.setText(result.getSymbol() + ">");
                }, t -> {
                    handleApiError(t);
                    finish();
                });

    }

    private void createBitmap() {
        imgSize = UIUtil.dip2px(this, QR_SIZE);
        getCodeBitmap();
    }

    @SuppressLint("CheckResult")
    private void getCodeBitmap() {
        Observable.create((ObservableOnSubscribe<Bitmap>) e -> {
            Bitmap bitmap = QRCodeEncoder.syncEncodeQRCode(uri2Code, imgSize, Color.BLACK);
            e.onNext(bitmap);
        })
                .compose(RxSchedulers.ioObserver())
                .compose(bindToLifecycle())
                .subscribe(b -> ivRecipetImg.setImageBitmap(b));
    }

    private void initUri() {
        uri = new BaseUri("action1");
        Action1 action1 = new Action1();
        action1.money = money;
        action1.logo = result.getLogo();
        action1.symbol = result.getSymbol();
        uri.data = action1;
        uri2Code = new Gson().toJson(uri);
    }

    // 设置金额
    @SuppressLint("SetTextI18n")
    public void setMoney(View view) {
        if (!isSet) {
            Intent intent = new Intent(this, SetRecipetActivity.class);
            intent.putExtra("symbol", result.getSymbol());
            startActivityForResult(intent, 1);
        } else {
            money = "";
            tvMoney.setText(money + " " + result.getSymbol() + ">");
            initUri();
            getCodeBitmap();
            tv_setMoney.setText(getString(R.string.set_money));
            isSet = false;
        }
    }

    // 收款详情
    public void jump2Detail(View view) {
        startActivity(new Intent(this, DetailListActivity.class));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (data == null) {
            return;
        }

        if (requestCode == 2 && resultCode == 1) {
            result = data.getParcelableExtra("result");
            if (result == null) return;

            GlideUtil.loadCircleImg(ivHead, result.getLogo());

            tvMoney.setText(money + " " + result.getSymbol() + ">");
            initUri();
            getCodeBitmap();
            return;
        }

        if (requestCode == 1 && resultCode == 1) {
            money = data.getStringExtra("money");
            tvMoney.setText(money + " " + result.getSymbol() + ">");
            initUri();
            getCodeBitmap();
            tv_setMoney.setText(getString(R.string.clear_money));
            isSet = true;
        }
    }

    public void chooseCoin(View view) {
        Intent intent = new Intent(this, ChooseCoinActivity.class);
        intent.putExtra("data", list);
        startActivityForResult(intent, 2);
    }

    public void back(View view) {
        finish();
    }

}

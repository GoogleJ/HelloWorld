package com.zxjk.duoduo.ui.walletpage;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.Nullable;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.zxjk.duoduo.Constant;
import com.zxjk.duoduo.R;
import com.zxjk.duoduo.network.rx.RxSchedulers;
import com.zxjk.duoduo.ui.base.BaseActivity;
import com.zxjk.duoduo.ui.minepage.BalanceLeftActivity;
import com.zxjk.duoduo.ui.minepage.DetailListActivity;
import com.zxjk.duoduo.ui.minepage.scanuri.Action1;
import com.zxjk.duoduo.ui.minepage.scanuri.BaseUri;
import com.zxjk.duoduo.utils.QRCodeEncoder;
import net.lucode.hackware.magicindicator.buildins.UIUtil;
import de.hdodenhof.circleimageview.CircleImageView;
import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;

public class RecipetQRActivity extends BaseActivity {

    private static final int QR_SIZE = 192;

    private ImageView ivRecipetImg;
    private CircleImageView ivHead;
    private TextView tvMoney, tv_setMoney, tvRecipetTips;
    private BaseUri uri;
    private String uri2Code;
    private int imgSize;
    private boolean isSet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipet_qr);
        ivRecipetImg = findViewById(R.id.ivRecipetImg);
        ivHead = findViewById(R.id.ivHead);
        tvMoney = findViewById(R.id.tvMoney);
        tv_setMoney = findViewById(R.id.tv_setMoney);
        tvRecipetTips = findViewById(R.id.tvRecipetTips);
        TextView tv_title = findViewById(R.id.tv_title);
        tv_title.setText(getString(R.string.receiptCode));

        Glide.with(this).load(Constant.currentUser.getHeadPortrait()).into(ivHead);

        findViewById(R.id.rl_back).setOnClickListener(v -> finish());

        initUri();

        initImgSize();

    }

    private void initImgSize() {
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
        action1.money = "";
        uri.data = action1;
        uri2Code = new Gson().toJson(uri);
    }

    // 设置金额
    @SuppressLint("SetTextI18n")
    public void setMoney(View view) {
        if (!isSet) {
            startActivityForResult(new Intent(this, SetRecipetActivity.class), 1);
        } else {
            tvRecipetTips.setVisibility(View.VISIBLE);
            tvMoney.setVisibility(View.GONE);
            tvMoney.setText("");
            Action1 action1 = new Action1();
            action1.money = "";
            uri.data = action1;
            uri2Code = new Gson().toJson(uri);
            getCodeBitmap();
            tv_setMoney.setText(getString(R.string.set_money));
            isSet = false;
        }
    }

    // 进入我的余额
    public void enterMyBalance(View view) {
        startActivity(new Intent(this, BalanceLeftActivity.class));
    }

    // 收款详情
    public void jump2Detail(View view) {
        startActivity(new Intent(this, DetailListActivity.class));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode != 1 || resultCode != 1) {
            return;
        }

        String money = data.getStringExtra("money");

        tvRecipetTips.setVisibility(View.GONE);
        tvMoney.setVisibility(View.VISIBLE);
        tvMoney.setText(money + " MoT");
        Action1 action1 = new Action1();
        action1.money = money;
        uri.data = action1;
        uri2Code = new Gson().toJson(uri);
        getCodeBitmap();
        tv_setMoney.setText(getString(R.string.clear_money));
        isSet = true;

    }
}

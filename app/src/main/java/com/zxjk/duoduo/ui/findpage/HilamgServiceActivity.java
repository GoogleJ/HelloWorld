package com.zxjk.duoduo.ui.findpage;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewStub;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.blankj.utilcode.util.GsonUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.zxjk.duoduo.Constant;
import com.zxjk.duoduo.R;
import com.zxjk.duoduo.bean.DataTemp628;
import com.zxjk.duoduo.bean.request.PayPhoneRequest;
import com.zxjk.duoduo.network.Api;
import com.zxjk.duoduo.network.ServiceFactory;
import com.zxjk.duoduo.network.rx.RxSchedulers;
import com.zxjk.duoduo.ui.WebActivity;
import com.zxjk.duoduo.ui.base.BaseActivity;
import com.zxjk.duoduo.ui.minepage.CooperateActivity;
import com.zxjk.duoduo.ui.minepage.RewardMotActivity;
import com.zxjk.duoduo.ui.wallet.OneKeyBuyCoinActivity;
import com.zxjk.duoduo.utils.AesUtil;
import com.zxjk.duoduo.utils.CommonUtils;
import com.zxjk.duoduo.utils.GlideUtil;

import razerdp.basepopup.QuickPopupBuilder;
import razerdp.basepopup.QuickPopupConfig;
import razerdp.widget.QuickPopup;

public class HilamgServiceActivity extends BaseActivity {
    private View dot1;
    private View dot2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hilamg_service);

        dot1 = findViewById(R.id.dot1);
        dot2 = findViewById(R.id.dot2);

        if (System.currentTimeMillis() > 1593273600000L && System.currentTimeMillis() < 1593532799000L) {
            dot1.setVisibility(View.VISIBLE);
            dot2.setVisibility(View.VISIBLE);
        }
    }

    public void reward(View view) {
        startActivity(new Intent(this, RewardMotActivity.class));
    }

    @SuppressLint("CheckResult")
    public void showRewardCodePop(View view) {
        QuickPopup rewardPop = QuickPopupBuilder.with(this)
                .contentView(R.layout.pop_rewardcode)
                .config(new QuickPopupConfig()
                        .gravity(Gravity.CENTER)
                        .blurBackground(true)
                        .dismissOnOutSideTouch(true)
                        .fadeInAndOut(true)
                        .withClick(R.id.ivClose, null, true))
                .build();

        EditText et = rewardPop.findViewById(R.id.et);
        ImageView ivOpen = rewardPop.findViewById(R.id.ivOpen);
        LinearLayout llInput = rewardPop.findViewById(R.id.llInput);
        ViewStub stubResult = rewardPop.findViewById(R.id.stubResult);

        ivOpen.setOnClickListener(v -> {
            if (null == et) {
                return;
            }

            if (TextUtils.isEmpty(et.getText().toString().trim())) {
                ToastUtils.showShort(R.string.input_empty);
                return;
            }

            ServiceFactory.getInstance().getBaseService(Api.class)
                    .getRewardCode(et.getText().toString().trim())
                    .compose(bindToLifecycle())
                    .compose(RxSchedulers.normalTrans())
                    .compose(RxSchedulers.ioObserver(CommonUtils.initDialog(this)))
                    .subscribe(r -> {
                        llInput.setVisibility(View.GONE);
                        View resultPopView = stubResult.inflate();

                        ImageView ivLogo = resultPopView.findViewById(R.id.ivLogo);
                        TextView tvSymbolText = resultPopView.findViewById(R.id.tvSymbolText);
                        TextView tvMoney = resultPopView.findViewById(R.id.tvMoney);
                        TextView tvSymbol = resultPopView.findViewById(R.id.tvSymbol);

                        tvSymbol.setText(r.getSymbol());
                        tvMoney.setText(r.getNum());
                        tvSymbolText.setText(getString(R.string.rewardcode_symbol_tips, r.getSymbol()));
                        GlideUtil.loadCircleImg(ivLogo, r.getLogo());
                    }, this::handleApiError);
        });

        rewardPop.showPopupWindow();
    }

    @SuppressLint("CheckResult")
    public void buyCoin(View view) {
        ServiceFactory.getInstance().getBaseService(Api.class)
                .getOpenPurchaseStatus()
                .compose(bindToLifecycle())
                .compose(RxSchedulers.normalTrans())
                .compose(RxSchedulers.ioObserver(CommonUtils.initDialog(this)))
                .subscribe(d -> {
                    if (d.equals("1")) {
                        startActivity(new Intent(this, OneKeyBuyCoinActivity.class));
                    } else {
                        ToastUtils.showShort(R.string.developing);
                    }
                }, this::handleApiError);
    }

    public void payPhone(View view) {
        String data = AesUtil.getInstance().encrypt(GsonUtils.toJson(new PayPhoneRequest(Constant.token, Constant.userId)));
        String url = "http://hilamg-recharge.ztoken.cn/?obj=" + data;
        Intent intent = new Intent(this, WebActivity.class);
        intent.putExtra("url", url);
        intent.putExtra("title", getString(R.string.payPhone));
        startActivity(intent);
    }

    @SuppressLint("CheckResult")
    public void mall(View view) {
        ServiceFactory.getInstance().getBaseService(Api.class)
                .getShoppingUrl("1")
                .compose(bindToLifecycle())
                .compose(RxSchedulers.normalTrans())
                .compose(RxSchedulers.ioObserver(CommonUtils.initDialog(this)))
                .subscribe(url -> {
                    Intent intent = new Intent(this, WebActivity.class);
                    intent.putExtra("url", url);
                    intent.putExtra("title", "商城");
                    intent.putExtra("type", "mall");
                    startActivity(intent);
                }, this::handleApiError);
    }

    public void coo(View view) {
        startActivity(new Intent(this, CooperateActivity.class));
    }

    public void other(View view) {
        ToastUtils.showShort(getString(R.string.toast1));
    }

    public void act628(View view) {
        DataTemp628 data = new DataTemp628();
        data.setId(Constant.currentUser.getId());
        data.setToken(Constant.currentUser.getToken());
        Intent intent = new Intent(this, WebActivity.class);
        intent.putExtra("url", Constant.URL_628ACTIVITY + "/?" + AesUtil.getInstance().encrypt(GsonUtils.toJson(data)));
        startActivity(intent);
    }

    public void back(View view) {
        finish();
    }

    @SuppressLint("CheckResult")
    public void ipfs(View view) {
        ServiceFactory.getInstance().getBaseService(Api.class)
                .getHilamgMillUrl()
                .compose(bindToLifecycle())
                .compose(RxSchedulers.normalTrans())
                .compose(RxSchedulers.ioObserver(CommonUtils.initDialog(this)))
                .subscribe(url -> {
                    Intent intent = new Intent(this, WebActivity.class);
                    intent.putExtra("url", url);
                    intent.putExtra("title", "ipfs");
                    intent.putExtra("type", "mall");
                    startActivity(intent);
                }, this::handleApiError);
    }
}

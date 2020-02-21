package com.zxjk.duoduo.ui.minepage;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.RelativeSizeSpan;
import android.transition.Slide;
import android.transition.TransitionManager;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.blankj.utilcode.util.BarUtils;
import com.blankj.utilcode.util.Utils;
import com.trello.rxlifecycle3.android.ActivityEvent;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.zxjk.duoduo.Application;
import com.zxjk.duoduo.Constant;
import com.zxjk.duoduo.R;
import com.zxjk.duoduo.bean.RedFallActivityLocalBeanDao;
import com.zxjk.duoduo.bean.response.ReceiveAirdropResponse;
import com.zxjk.duoduo.db.RedFallActivityLocalBean;
import com.zxjk.duoduo.network.Api;
import com.zxjk.duoduo.network.ServiceFactory;
import com.zxjk.duoduo.network.rx.RxSchedulers;
import com.zxjk.duoduo.ui.base.BaseActivity;
import com.zxjk.duoduo.ui.socialspace.SocialHomeActivity;
import com.zxjk.duoduo.ui.widget.dialog.MuteRemoveDialog;
import com.zxjk.duoduo.utils.CommonUtils;
import com.zxjk.duoduo.utils.GlideUtil;
import com.zxjk.duoduo.utils.ShareUtil;

import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import razerdp.basepopup.QuickPopupBuilder;
import razerdp.basepopup.QuickPopupConfig;

public class ConfirmRedFallActivity extends BaseActivity {

    private LinearLayout llContent;
    private LinearLayout llAnim1;
    private LinearLayout llAnim2;
    private LinearLayout llAnim3;
    private LinearLayout llAnim4;
    private TextView tvAnim5;
    private TextView tvTips;

    private ImageView ivSocialLogo;
    private TextView tvSocialName;
    private ImageView ivCoinIcon;
    private TextView tvMoney;
    private ImageView ivCoinIcon2;
    private TextView tvCoinContent;

    private ReceiveAirdropResponse data;
    private RedFallActivityLocalBeanDao redFallActivityLocalBeanDao;

    @SuppressLint("CheckResult")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        BarUtils.setStatusBarVisibility(this, false);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            WindowManager.LayoutParams lp = getWindow().getAttributes();
            lp.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
            getWindow().setAttributes(lp);
        }

        setContentView(R.layout.activity_confirm_red_fall);

        data = getIntent().getParcelableExtra("data");
        redFallActivityLocalBeanDao = Application.daoSession.getRedFallActivityLocalBeanDao();

        llContent = findViewById(R.id.llContent);
        llAnim1 = findViewById(R.id.llAnim1);
        llAnim2 = findViewById(R.id.llAnim2);
        llAnim3 = findViewById(R.id.llAnim3);
        llAnim4 = findViewById(R.id.llAnim4);
        tvAnim5 = findViewById(R.id.tvAnim5);

        ivSocialLogo = findViewById(R.id.ivSocialLogo);
        ivCoinIcon = findViewById(R.id.ivCoinIcon);
        ivCoinIcon2 = findViewById(R.id.ivCoinIcon2);
        tvSocialName = findViewById(R.id.tvSocialName);
        tvMoney = findViewById(R.id.tvMoney);
        tvCoinContent = findViewById(R.id.tvCoinContent);
        tvTips = findViewById(R.id.tvTips);

        if (data.getShareCount() == 0) {
            tvTips.setText(R.string.redfall_tips_noshareyet);
            if (data.getReceiveCount() == 0) {
                QuickPopupBuilder.with(this)
                        .contentView(R.layout.redfall_lastnoshare)
                        .config(new QuickPopupConfig()
                                .dismissOnOutSideTouch(false)
                                .withClick(R.id.iv, null, true)
                        ).show();
            }
        } else {
            tvTips.setText(R.string.redfall_tips_shared);
        }

        GlideUtil.loadCircleImg(ivSocialLogo, data.getCommunityLogo());
        GlideUtil.loadCircleImg(ivCoinIcon, data.getSymbolLogo());
        GlideUtil.loadCircleImg(ivCoinIcon2, data.getSymbolLogo());
        tvSocialName.setText(data.getCommunityName());
        tvCoinContent.setText(data.getSymbolIntroduction());
        SpannableString spannableString = new SpannableString(data.getReward() + data.getSymbol());
        spannableString.setSpan(new RelativeSizeSpan(0.56f), spannableString.length() - data.getSymbol().length(), spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        tvMoney.setText(spannableString);
        tvMoney.setText(spannableString);

        Observable.timer(250, TimeUnit.MILLISECONDS, AndroidSchedulers.mainThread())
                .compose(bindToLifecycle())
                .subscribe(a -> {
                    Slide slide = new Slide(Gravity.BOTTOM);
                    slide.setInterpolator(new DecelerateInterpolator());
                    slide.setDuration(700);
                    TransitionManager.beginDelayedTransition(llContent, slide);

                    llAnim1.setVisibility(View.VISIBLE);
                    llAnim2.setVisibility(View.VISIBLE);
                    llAnim3.setVisibility(View.VISIBLE);
                    llAnim4.setVisibility(View.VISIBLE);
                    tvAnim5.setVisibility(View.VISIBLE);
                });
    }

    public void back(View view) {
        finish();
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.redfallconfirm_enteranim, R.anim.redfallconfirm_exitanim);
    }

    @SuppressLint("CheckResult")
    public void share(View view) {
        String text = "【Hilamg APP---专注于区块链+社交】\n" +
                "我在Hilamg抢到了" + data.getReward() + data.getSymbol() + "，你也来试试吧~"
                + "http://hilamg-register.ztoken.cn/red/redPage.html?id=" + Constant.userId + "点击领取奖励";
        ClipboardManager cm = (ClipboardManager) Utils.getApp().getSystemService(Context.CLIPBOARD_SERVICE);
        if (cm != null) {
            cm.setPrimaryClip(ClipData.newPlainText("text", text));
        }

        MuteRemoveDialog dialog = new MuteRemoveDialog(this, "去微信", "取消", "分享至朋友圈", "文案已自动生成，快去粘贴吧！");
        dialog.setOnCancelListener(() -> ShareUtil.share2WTimeline(this, new ShareUtil.ShareListener() {
            @Override
            public void onStart(SHARE_MEDIA share_media) {
                super.onStart(share_media);
                ServiceFactory.getInstance().getBaseService(Api.class)
                        .shareAirdrop()
                        .compose(bindUntilEvent(ActivityEvent.DESTROY))
                        .compose(RxSchedulers.ioObserver(CommonUtils.initDialog(ConfirmRedFallActivity.this)))
                        .compose(RxSchedulers.normalTrans())
                        .subscribe(s -> {
                            if (!TextUtils.isEmpty(s) && s.equals("1")) {
                                List<RedFallActivityLocalBean> redFallActivityLocalBeans = redFallActivityLocalBeanDao.loadAll();
                                if (redFallActivityLocalBeans.size() == 0) {
                                    RedFallActivityLocalBean redFallActivityLocalBean = new RedFallActivityLocalBean();
                                    redFallActivityLocalBean.setLastPlayTime(String.valueOf(System.currentTimeMillis()));
                                    redFallActivityLocalBeanDao.insert(redFallActivityLocalBean);
                                }
                            }
                        }, ConfirmRedFallActivity.this::handleApiError);
            }

            @Override
            public void onError(SHARE_MEDIA share_media, Throwable throwable) {
                super.onError(share_media, throwable);
            }
        }));
        dialog.show();
    }

    public void joinSocial(View view) {
        Intent intent = new Intent(this, SocialHomeActivity.class);
        intent.putExtra("id", data.getGroupId());
        startActivity(intent);
        finish();
    }

}

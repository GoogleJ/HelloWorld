package com.zxjk.duoduo.ui.minepage;

import android.Manifest;
import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.blankj.utilcode.util.ScreenUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMWeb;
import com.zxjk.duoduo.Constant;
import com.zxjk.duoduo.R;
import com.zxjk.duoduo.network.Api;
import com.zxjk.duoduo.network.ServiceFactory;
import com.zxjk.duoduo.network.rx.RxSchedulers;
import com.zxjk.duoduo.ui.base.BaseActivity;
import com.zxjk.duoduo.utils.SaveImageUtil;
import com.zxjk.duoduo.utils.ShareUtil;

import net.lucode.hackware.magicindicator.buildins.UIUtil;

import cn.bingoogolapple.qrcode.zxing.QRCodeEncoder;
import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import razerdp.basepopup.QuickPopupBuilder;
import razerdp.basepopup.QuickPopupConfig;
import razerdp.widget.QuickPopup;

public class InviterActivity extends BaseActivity {

    private TextView tvInviteCode;
    private TextView tvInviteCount;
    private TextView tvReward;
    private TextView tvReward1;
    private ImageView ivQR;

    private QuickPopup invitePop;

    private Bitmap bitmap;

    private String inviteWeb = "http://mochat-register.ztoken.cn/?id=" + Constant.userId;
    private String description = "加入海浪社区";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inviter);

        initView();
        bindData();
    }

    private void initView() {
        TextView tvtitle = findViewById(R.id.tv_title);
        tvtitle.setText(R.string.invite_friends1);
        tvInviteCode = findViewById(R.id.tvInviteCode);
        tvInviteCount = findViewById(R.id.tvInviteCount);
        tvReward = findViewById(R.id.tvReward);
        tvReward1 = findViewById(R.id.tvReward1);
        ivQR = findViewById(R.id.ivQR);
    }

    @SuppressLint("CheckResult")
    private void bindData() {
        findViewById(R.id.rl_back).setOnClickListener(view -> finish());

        tvInviteCode.setText(Constant.currentUser.getInviteCode());

        Observable.create((ObservableOnSubscribe<Bitmap>) e -> {
            bitmap = QRCodeEncoder.syncEncodeQRCode(inviteWeb, UIUtil.dip2px(this, 160), Color.BLACK);
            e.onNext(bitmap);
        })
                .compose(RxSchedulers.ioObserver())
                .compose(bindToLifecycle())
                .subscribe(b -> ivQR.setImageBitmap(b));

        ServiceFactory.getInstance().getBaseService(Api.class)
                .getInviteInfo()
                .compose(bindToLifecycle())
                .compose(RxSchedulers.ioObserver())
                .compose(RxSchedulers.normalTrans())
                .subscribe(r -> {
                    tvInviteCount.setText(r.getInviterCount());
                    tvReward.setText(r.getMot() + "USDT");
                    tvReward1.setText("填写邀请码奖励" + r.getAward() + "USDT");
                }, this::handleApiError);
    }

    public void invite(View view) {
        if (invitePop == null) {
            TranslateAnimation showAnimation = new TranslateAnimation(0f, 0f, ScreenUtils.getScreenHeight(), 0f);
            showAnimation.setDuration(250);
            TranslateAnimation dismissAnimation = new TranslateAnimation(0f, 0f, 0f, ScreenUtils.getScreenHeight());
            dismissAnimation.setDuration(500);
            invitePop = QuickPopupBuilder.with(this)
                    .contentView(R.layout.popup_invite)
                    .config(new QuickPopupConfig()
                            .withShowAnimation(showAnimation)
                            .withDismissAnimation(dismissAnimation)
                            .withClick(R.id.tv1, v -> shareTo(1), true)
                            .withClick(R.id.tv2, v -> shareTo(2), true)
                            .withClick(R.id.tv3, v -> shareTo(3), true)
                            .withClick(R.id.tv4, v -> shareTo(4), true))
                    .show();
            save(invitePop.findViewById(R.id.tv5));
        } else {
            invitePop.showPopupWindow();
        }
    }

    private void shareTo(int plantform) {
        UMWeb link = new UMWeb(inviteWeb);
        link.setTitle("海浪社区");
        link.setDescription(description);
        link.setThumb(new UMImage(this, R.mipmap.ic_launcher));
        SHARE_MEDIA platform = SHARE_MEDIA.QQ;
        switch (plantform) {
            case 1:
                platform = SHARE_MEDIA.WEIXIN;
                break;
            case 2:
                platform = SHARE_MEDIA.WEIXIN_CIRCLE;
                break;
            case 3:
                platform = SHARE_MEDIA.QQ;
                break;
            case 4:
                platform = SHARE_MEDIA.QZONE;
                break;
        }
        new ShareAction(this)
                .setPlatform(platform)
                .withMedia(link)
                .setCallback(new ShareUtil.ShareListener())
                .share();
    }

    private void save(View view) {
        getPermisson(view, g -> {
            //保存到手机
            if (bitmap == null) {
                ToastUtils.showShort(R.string.savefailed);
                return;
            }

            ivQR.buildDrawingCache();

            SaveImageUtil.get().savePic(ivQR.getDrawingCache(), success -> {
                if (success) {
                    invitePop.dismiss(true);
                    ToastUtils.showShort(R.string.savesucceed);
                    return;
                }
                ToastUtils.showShort(R.string.savefailed);
            });
        }, Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE);
    }
}

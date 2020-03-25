package com.zxjk.duoduo.ui.webcast;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.view.ViewStub;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.airbnb.lottie.LottieAnimationView;
import com.blankj.utilcode.util.ImageUtils;
import com.blankj.utilcode.util.ScreenUtils;
import com.blankj.utilcode.util.TimeUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomViewTarget;
import com.bumptech.glide.request.transition.Transition;
import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import com.zxjk.duoduo.Constant;
import com.zxjk.duoduo.R;
import com.zxjk.duoduo.bean.response.GetChatRoomInfoResponse;
import com.zxjk.duoduo.network.Api;
import com.zxjk.duoduo.network.ServiceFactory;
import com.zxjk.duoduo.network.rx.RxSchedulers;
import com.zxjk.duoduo.ui.HomeActivity;
import com.zxjk.duoduo.ui.base.BaseActivity;
import com.zxjk.duoduo.ui.widget.dialog.MuteRemoveDialog;
import com.zxjk.duoduo.utils.CommonUtils;
import com.zxjk.duoduo.utils.GlideUtil;

import io.rong.imkit.RongIM;
import razerdp.basepopup.QuickPopupBuilder;
import razerdp.basepopup.QuickPopupConfig;
import razerdp.widget.QuickPopup;

public class WechatCastDetailActivity extends BaseActivity {

    private ViewStub stub;
    private FrameLayout flRetry;
    private LottieAnimationView lottie;
    private TextView tvRetry;

    private FrameLayout flEnd;
    private ImageView ivEnd;
    private TextView tvModify;

    private ImageView ivSocialLogo;
    private TextView tvSocialName;

    private TextView tvCastTitle;
    private TextView tvCastTime;
    private TextView tvTips;
    private TextView tvCastContent;
    private SubsamplingScaleImageView ivDetail;

    private TextView tvBottom1;
    private View dividerBottom;
    private TextView tvBottom2;

    private String roomId;
    private boolean fromCreate;

    private GetChatRoomInfoResponse info;

    private QuickPopup ownerPop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wechat_cast_detail);

        roomId = getIntent().getStringExtra("roomId");
        fromCreate = getIntent().getBooleanExtra("fromCreate", false);

        stub = findViewById(R.id.stub);
        flRetry = findViewById(R.id.flRetry);
        lottie = findViewById(R.id.lottie);
        tvRetry = findViewById(R.id.tvRetry);

        initData();
    }

    private void initView(View view) {
        flEnd = view.findViewById(R.id.flEnd);
        ivEnd = view.findViewById(R.id.ivEnd);
        tvModify = view.findViewById(R.id.tvModify);

        ivSocialLogo = view.findViewById(R.id.ivSocialLogo);
        tvSocialName = view.findViewById(R.id.tvSocialName);

        tvCastTitle = view.findViewById(R.id.tvCastTitle);
        tvCastTime = view.findViewById(R.id.tvCastTime);
        tvTips = view.findViewById(R.id.tvTips);
        tvCastContent = view.findViewById(R.id.tvCastContent);
        ivDetail = view.findViewById(R.id.ivDetail);

        tvBottom1 = view.findViewById(R.id.tvBottom1);
        dividerBottom = view.findViewById(R.id.dividerBottom);
        tvBottom2 = view.findViewById(R.id.tvBottom2);
    }

    @SuppressLint("CheckResult")
    private void initData() {
        ServiceFactory.getInstance().getBaseService(Api.class)
                .getChatRoomInfo(roomId)
                .doOnSubscribe(disposable -> {
                    lottie.setVisibility(View.VISIBLE);
                    tvRetry.setVisibility(View.GONE);
                })
                .compose(bindToLifecycle())
                .compose(RxSchedulers.normalTrans())
                .compose(RxSchedulers.ioObserver())
                .doOnNext(r -> flRetry.setVisibility(View.GONE))
                .doOnError(t -> {
                    lottie.setVisibility(View.GONE);
                    tvRetry.setVisibility(View.VISIBLE);
                })
                .subscribe(r -> {
                    info = r;
                    if (info.getRoomStatus().equals("3")) {
                        ToastUtils.showShort(R.string.cant_view_cast);
                        finish();
                    }

                    initView(stub.inflate());

                    updateUIByStatus();
                }, this::handleApiError);
    }

    @SuppressLint("CheckResult")
    public void funcEnd(View view) {
        if (fromCreate) {
            if (info != null && info.getRoomStatus().equals("0")) {
                Intent intent = new Intent(this, ModifyWechatCastActivity.class);
                intent.putExtra("info", info);
                startActivityForResult(intent, 1);
            } else {
                ToastUtils.showShort(R.string.cant_modify_inprogress_cast);
            }
        } else {
            if (info != null && info.getRoomOwnerId().equals(Constant.userId)) {
                if (ownerPop == null) {
                    TranslateAnimation showAnimation = new TranslateAnimation(0f, 0f, ScreenUtils.getScreenHeight(), 0f);
                    showAnimation.setDuration(250);
                    TranslateAnimation dismissAnimation = new TranslateAnimation(0f, 0f, 0f, ScreenUtils.getScreenHeight());
                    dismissAnimation.setDuration(500);
                    ownerPop = QuickPopupBuilder.with(this)
                            .contentView(R.layout.popup_wecast_pop)
                            .config(new QuickPopupConfig()
                                    .withShowAnimation(showAnimation)
                                    .withDismissAnimation(dismissAnimation)
                                    .withClick(R.id.tv1, v -> handleOwnerPop(1), true)
                                    .withClick(R.id.tv2, v -> handleOwnerPop(2), true)
                                    .withClick(R.id.tv3, v -> handleOwnerPop(3), true)
                                    .withClick(R.id.tv4, null, true))
                            .build();
                    TextView tv1 = ownerPop.findViewById(R.id.tv1);
                    tv1.setText(R.string.share);
                    TextView tv2 = ownerPop.findViewById(R.id.tv2);
                    tv2.setText(R.string.modify1);
                    TextView tv3 = ownerPop.findViewById(R.id.tv3);
                    tv3.setText(R.string.delete);
                    TextView tv4 = ownerPop.findViewById(R.id.tv4);
                    tv4.setText(R.string.cancel);
                }
                ownerPop.showPopupWindow();
            } else {
                judgeShowPop();
            }
        }
    }

    @SuppressLint("CheckResult")
    private void handleOwnerPop(int i) {
        switch (i) {
            case 1:
                judgeShowPop();
                break;
            case 2:
                if (info != null && info.getRoomStatus().equals("0")) {
                    Intent intent = new Intent(this, ModifyWechatCastActivity.class);
                    intent.putExtra("info", info);
                    startActivityForResult(intent, 1);
                } else {
                    ToastUtils.showShort(R.string.cant_modify_inprogress_cast);
                }
                break;
            case 3:
                MuteRemoveDialog dialog = new MuteRemoveDialog(this, getString(R.string.cancel), getString(R.string.queding),
                        getString(R.string.hinttext), getString(R.string.confirm_delete_cast));
                dialog.setOnCommitListener(() -> ServiceFactory.getInstance().getBaseService(Api.class)
                        .delLive(roomId)
                        .compose(bindToLifecycle())
                        .compose(RxSchedulers.normalTrans())
                        .compose(RxSchedulers.ioObserver(CommonUtils.initDialog(this)))
                        .subscribe(s -> {
                            //todo 更新本地数据库
                            Intent intent1 = new Intent(this, HomeActivity.class);
                            intent1.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent1);
                        }, this::handleApiError));
                dialog.show();
                break;
        }
    }

    @SuppressLint("CheckResult")
    private void judgeShowPop() {
        ServiceFactory.getInstance().getBaseService(Api.class)
                .shareToWx()
                .compose(bindToLifecycle())
                .compose(RxSchedulers.normalTrans())
                .compose(RxSchedulers.ioObserver(CommonUtils.initDialog(this)))
                .subscribe(s -> {
                    if (s.equals("1")) {
                        showSharePop(true);
                    } else {
                        showSharePop(false);
                    }
                }, this::handleApiError);
    }

    private void showSharePop(boolean hasWechatShare) {
        TranslateAnimation showAnimation = new TranslateAnimation(0f, 0f, ScreenUtils.getScreenHeight(), 0f);
        showAnimation.setDuration(250);
        TranslateAnimation dismissAnimation = new TranslateAnimation(0f, 0f, 0f, ScreenUtils.getScreenHeight());
        dismissAnimation.setDuration(500);
        QuickPopup sharePop = QuickPopupBuilder.with(this)
                .contentView(R.layout.popup_wecast_share_pop)
                .config(new QuickPopupConfig()
                        .withShowAnimation(showAnimation)
                        .withDismissAnimation(dismissAnimation)
                        .withClick(R.id.tv1, v -> handleSharePop(1), true)
                        .withClick(R.id.tv2, v -> handleSharePop(2), true)
                        .withClick(R.id.tv3, null, true))
                .build();

        if (!hasWechatShare) {
            sharePop.findViewById(R.id.tv2).setVisibility(View.GONE);
        }

        sharePop.showPopupWindow();
    }

    private void handleSharePop(int i) {
        switch (i) {
            case 1:
                //todo share2Hilamg

                break;
            case 2:
                //todo share2Wechat
//                UMMin umMin = new UMMin(Defaultcontent.url);
////兼容低版本的网页链接
//                umMin.setThumb(imagelocal);
//// 小程序消息封面图片
//                umMin.setTitle(Defaultcontent.title);
//// 小程序消息title
//                umMin.setDescription(Defaultcontent.text);
//// 小程序消息描述
//                umMin.setPath("pages/page10007/xxxxxx");
////小程序页面路径
//                umMin.setUserName("gh_xxxxxxxxxxxx");
//// 小程序原始id,在微信平台查询
//                new ShareAction(this)
//                        .withMedia(umMin)
//                        .share();
//                break;
        }
    }

    public void back(View view) {
        finish();
    }

    public void viewCast(View view) {
        Intent intent = new Intent(this, HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        startActivity(new Intent(this, CastListActivity.class));
    }

    private void updateUIByStatus() {
        GlideUtil.loadCircleImg(ivSocialLogo, info.getCommunityLogo());

        loadLongImg();

        tvSocialName.setText(info.getGroupNikeName());
        tvCastTitle.setText(getString(R.string.cast_topic, info.getTopic()));
        tvCastContent.setText(info.getLiveDetails());
        tvCastTime.setText(getString(R.string.cast_time1, TimeUtils.millis2String(Long.parseLong(info.getStartTime()), "yyyy-MM-dd HH:mm:ss")));

        if (fromCreate) {
            return;
        }

        dividerBottom.setVisibility(View.GONE);
        tvBottom1.setVisibility(View.GONE);

        flEnd.setVisibility(View.VISIBLE);
        tvModify.setVisibility(View.GONE);

        if (!info.getRoomOwnerId().equals(Constant.userId)) {
            ivEnd.setImageResource(R.drawable.icon_titlebar_right_share);
        }

        switch (info.getRoomStatus()) {
            case "0":
                //pre
                long timeLeft = Long.parseLong(info.getStartTime()) - System.currentTimeMillis();
                if (timeLeft <= 300000) {
                    //todo  直播快开启时的倒计时
//                    Observable.intervalRange(0,)
                } else {
                    tvBottom2.setText(R.string.enter_cast);
                }
                break;
            case "1":
                tvBottom2.setText(R.string.enter_cast);
                tvTips.setText(R.string.cast_int);
                tvTips.setTextColor(ContextCompat.getColor(this, R.color.colorTheme));
                break;
            case "2":
                tvBottom2.setText(R.string.cast_recall);
                tvTips.setText(R.string.cast_over);
                flEnd.setVisibility(View.GONE);
                break;
        }
    }

    private void loadLongImg() {
        Glide.with(this).load(info.getLiveContentImg()).into(new CustomViewTarget<SubsamplingScaleImageView, Drawable>(ivDetail) {
            @Override
            protected void onResourceCleared(@Nullable Drawable placeholder) {
            }

            @Override
            public void onLoadFailed(@Nullable Drawable errorDrawable) {
            }

            @Override
            public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                Bitmap bitmap = ImageUtils.drawable2Bitmap(resource);
                ivDetail.setMinimumScaleType(SubsamplingScaleImageView.SCALE_TYPE_CENTER_INSIDE);
                ivDetail.setImage((ImageSource.bitmap(bitmap)));
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data == null || requestCode != 1 || resultCode != 1) {
            return;
        }

        Parcelable extra = data.getParcelableExtra("info");
        if (extra != null) {
            //todo 更新本地数据库
            this.info = (GetChatRoomInfoResponse) extra;
            updateUIByStatus();
        }
    }

    public void funcBottom2(View view) {
        if (fromCreate) {
            judgeShowPop();
        } else {
            if (!info.getRoomStatus().equals("")) {
                Intent intent = new Intent(this, HomeActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                RongIM.getInstance().startChatRoomChat(this, roomId, false);
            } else {
                ToastUtils.showShort(R.string.cant_view_cast1);
            }
        }
    }

    public void retry(View view) {
        initData();
    }
}
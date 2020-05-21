package com.zxjk.duoduo.ui.cast;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.TextUtils;
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
import com.blankj.utilcode.util.ScreenUtils;
import com.blankj.utilcode.util.TimeUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.CustomViewTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMMin;
import com.zxjk.duoduo.Application;
import com.zxjk.duoduo.Constant;
import com.zxjk.duoduo.R;
import com.zxjk.duoduo.bean.CastDao;
import com.zxjk.duoduo.bean.response.GetChatRoomInfoResponse;
import com.zxjk.duoduo.db.Cast;
import com.zxjk.duoduo.network.Api;
import com.zxjk.duoduo.network.ServiceFactory;
import com.zxjk.duoduo.network.rx.RxSchedulers;
import com.zxjk.duoduo.ui.HomeActivity;
import com.zxjk.duoduo.ui.base.BaseActivity;
import com.zxjk.duoduo.ui.msgpage.ShareGroupQRActivity;
import com.zxjk.duoduo.ui.webcast.LivePlayBackActivity;
import com.zxjk.duoduo.ui.webcast.VideoAddressActivity;
import com.zxjk.duoduo.ui.widget.dialog.MuteRemoveDialog;
import com.zxjk.duoduo.utils.CommonUtils;
import com.zxjk.duoduo.utils.GlideUtil;

import org.greenrobot.greendao.query.DeleteQuery;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.functions.Function;
import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
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
    private String chooseFlag;
    private String livePlayBack;

    private GetChatRoomInfoResponse info;

    private QuickPopup ownerPop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wechat_cast_detail);

        roomId = getIntent().getStringExtra("roomId");
        fromCreate = getIntent().getBooleanExtra("fromCreate", false);
        chooseFlag = getIntent().getStringExtra("chooseFlag");
        livePlayBack = getIntent().getStringExtra("livePlayBack");

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
        if (!TextUtils.isEmpty(chooseFlag) && chooseFlag.equals("1")) {
            tvBottom1.setText("推流地址");
            tvBottom2.setText("进入直播");
        }
        if (!TextUtils.isEmpty(livePlayBack)) {

        }
    }

    @SuppressLint("CheckResult")
    private void initData() {
        ServiceFactory.getInstance().getBaseService(Api.class)
                .getChatRoomInfo(roomId)
                .compose(bindToLifecycle())
                .compose(RxSchedulers.normalTrans())
                .compose(RxSchedulers.ioObserver())
                .doOnSubscribe(disposable -> {
                    lottie.setVisibility(View.VISIBLE);
                    tvRetry.setVisibility(View.GONE);
                })
                .doOnNext(r -> flRetry.setVisibility(View.GONE))
                .doOnError(t -> {
                    lottie.setVisibility(View.GONE);
                    tvRetry.setVisibility(View.VISIBLE);
                    finish();
                })
                .subscribe(r -> {
                    info = r;
//                    if (!TextUtils.isEmpty(info.getRoomStatus()) &&
//                            info.getRoomStatus().equals("3")) {
//                        ToastUtils.showShort(R.string.cant_view_cast);
//                        finish();
//                    }

                    chooseFlag = r.getLiveType();
                    initView(stub.inflate());
                    if (!TextUtils.isEmpty(chooseFlag) && chooseFlag.equals("1")) {
                        if (!info.getRoomOwnerId().equals(Constant.userId)) {
                            tvBottom1.setVisibility(View.GONE);
                            dividerBottom.setVisibility(View.GONE);
                        }
                        tvBottom1.setText("推流地址");
                        tvBottom2.setText("进入直播");
                    }
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
                        .doOnNext(s -> {
                            DeleteQuery<Cast> deleteQuery = Application.daoSession.queryBuilder(Cast.class)
                                    .where(CastDao.Properties.RoomId.eq(roomId)).buildDelete();
                            deleteQuery.executeDeleteWithoutDetachingEntities();
                        })
                        .compose(RxSchedulers.ioObserver(CommonUtils.initDialog(this)))
                        .subscribe(s -> {
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
                RongIM.getInstance().getConversationList(new RongIMClient.ResultCallback<List<Conversation>>() {
                    @Override
                    public void onSuccess(List<Conversation> conversations) {
                        Intent intent = new Intent(WechatCastDetailActivity.this, ShareGroupQRActivity.class);
                        intent.putExtra("fromShareCast", true);
                        intent.putExtra("roomId", info.getRoomId());
                        intent.putExtra("icon", info.getCommunityLogo());
                        intent.putExtra("title", info.getTopic());
                        intent.putExtra("type", info.getLiveType());
                        intent.putParcelableArrayListExtra("data", (ArrayList<? extends Parcelable>) conversations);
                        startActivity(intent);
                    }

                    @Override
                    public void onError(RongIMClient.ErrorCode errorCode) {
                    }
                }, Conversation.ConversationType.PRIVATE, Conversation.ConversationType.GROUP);
                break;
            case 2:
                Observable.just("")
                        .flatMap((Function<String, ObservableSource<Bitmap>>) s ->
                                Observable.create(emitter ->
                                        Glide.with(this).asBitmap().load(info.getLivePoster()).listener(new RequestListener<Bitmap>() {
                                            @Override
                                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                                                emitter.tryOnError(new Exception());
                                                return false;
                                            }

                                            @Override
                                            public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
                                                emitter.onNext(resource);
                                                emitter.onComplete();
                                                return true;
                                            }
                                        }).submit(CommonUtils.dip2px(this, 160), CommonUtils.dip2px(this, 96))))
                        .compose(bindToLifecycle())
                        .compose(RxSchedulers.ioObserver())
                        .doOnSubscribe(d -> ToastUtils.showShort(R.string.parseimg))
                        .timeout(8, TimeUnit.SECONDS)
                        .subscribe(bitmap -> {
                            UMMin umMin = new UMMin("http://hilamg.com/");
                            umMin.setThumb(new UMImage(this, bitmap));
                            umMin.setTitle(info.getTopic());
                            umMin.setDescription(getString(R.string.app_name));
                            umMin.setPath("pages/live/index?roomId=" + info.getRoomId() + "&groupId=" + info.getGroupId() + "&inviteCode=" + Constant.currentUser.getInviteCode());
                            umMin.setUserName("gh_ccebc1a7e592");
                            new ShareAction(this)
                                    .withMedia(umMin)
                                    .setPlatform(SHARE_MEDIA.WEIXIN)
                                    .share();
                        }, t -> ToastUtils.showShort(getString(R.string.sharefail)));
                break;
        }
    }

    public void back(View view) {
        finish();
    }

    @SuppressLint("CheckResult")
    public void viewCast(View view) {
        if (!TextUtils.isEmpty(chooseFlag) && chooseFlag.equals("1")) {
            ServiceFactory.getInstance().getBaseService(Api.class)
                    .getVideoInfo(roomId)
                    .compose(bindToLifecycle())
                    .compose(RxSchedulers.normalTrans())
                    .compose(RxSchedulers.ioObserver(CommonUtils.initDialog(this)))
                    .subscribe(s -> {
                        Intent intent = new Intent(this, VideoAddressActivity.class);
                        intent.putExtra("rtmpAdd", s.getRtmpAdd());
                        intent.putExtra("liveCode", s.getLiveCode());
                        startActivity(intent);
                    }, this::handleApiError);
        } else {
            Intent intent = new Intent(this, HomeActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            startActivity(new Intent(this, CastListActivity.class));
        }
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
        if (chooseFlag.equals("0")) {
            dividerBottom.setVisibility(View.GONE);
            tvBottom1.setVisibility(View.GONE);
        }


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
                dividerBottom.setVisibility(View.GONE);
                tvBottom1.setVisibility(View.GONE);
                break;
        }
    }

    private void loadLongImg() {
        Glide.with(this).download(new GlideUrl(info.getLiveContentImg()))
                .into(new CustomViewTarget<SubsamplingScaleImageView, File>(ivDetail) {
                    @Override
                    public void onLoadFailed(@Nullable Drawable errorDrawable) {
                    }

                    @Override
                    public void onResourceReady(@NonNull File resource, @Nullable Transition<? super File> transition) {
                        ivDetail.setImage(ImageSource.uri(Uri.fromFile(resource)));
                    }

                    @Override
                    protected void onResourceCleared(@Nullable Drawable placeholder) {
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
            this.info = (GetChatRoomInfoResponse) extra;
            updateUIByStatus();
        }
    }

    @SuppressLint("CheckResult")
    public void funcBottom2(View view) {
        if (fromCreate) {
            judgeShowPop();
        } else {
            if (!info.getRoomStatus().equals("")) {
                ServiceFactory.getInstance().getBaseService(Api.class)
                        .getRoomStatusByRoomId(roomId)
                        .compose(bindToLifecycle())
                        .compose(RxSchedulers.normalTrans())
                        .compose(RxSchedulers.ioObserver(CommonUtils.initDialog(this)))
                        .subscribe(s -> {
                            if (s.equals("3") || s.equals("0")) {
                                ToastUtils.showShort(R.string.cant_view_cast1);
                            } else {
                                if (!TextUtils.isEmpty(livePlayBack)) {
                                    Intent intent = new Intent(this, LivePlayBackActivity.class);
                                    intent.putExtra("playUrl", info.getPlayUrl());
                                    startActivity(intent);
                                } else {
                                    Intent intent = new Intent(this, HomeActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(intent);
                                    Uri uri = Uri.parse("rong://" + getApplicationInfo().packageName).buildUpon().appendPath("conversation").appendPath(Conversation.ConversationType.CHATROOM.getName().toLowerCase(Locale.US)).appendQueryParameter("targetId", roomId).build();
                                    intent = new Intent("android.intent.action.VIEW", uri);
                                    intent.putExtra("chatRoomOwnerId", info.getRoomOwnerId());
                                    intent.putExtra("chatRoomStatus", s);
                                    intent.putExtra("chatRoomName", info.getRoomName());
                                    intent.putExtra("groupId", info.getGroupId());
                                    intent.putExtra("castTopic", info.getTopic());
                                    intent.putExtra("liveType", info.getLiveType());
                                    intent.putExtra("playUrl", info.getPlayUrl());
                                    startActivity(intent);
                                }

                            }
                        }, this::handleApiError);
            } else {
                ToastUtils.showShort(R.string.cant_view_cast1);
            }
        }
    }

    public void retry(View view) {
        initData();
    }
}
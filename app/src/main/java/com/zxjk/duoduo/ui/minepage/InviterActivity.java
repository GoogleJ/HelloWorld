package com.zxjk.duoduo.ui.minepage;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.BulletSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.blankj.utilcode.util.ScreenUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.blankj.utilcode.util.Utils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.shehuan.nicedialog.BaseNiceDialog;
import com.shehuan.nicedialog.NiceDialog;
import com.shehuan.nicedialog.ViewConvertListener;
import com.shehuan.nicedialog.ViewHolder;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMWeb;
import com.zxjk.duoduo.Constant;
import com.zxjk.duoduo.R;
import com.zxjk.duoduo.bean.response.GetInviteInfoResponse;
import com.zxjk.duoduo.bean.response.GetUInvitationUrlBean;
import com.zxjk.duoduo.network.Api;
import com.zxjk.duoduo.network.ServiceFactory;
import com.zxjk.duoduo.network.rx.RxSchedulers;
import com.zxjk.duoduo.ui.base.BaseActivity;
import com.zxjk.duoduo.ui.widget.NewsLoadMoreView;
import com.zxjk.duoduo.utils.CommonUtils;
import com.zxjk.duoduo.utils.GlideUtil;
import com.zxjk.duoduo.utils.QRCodeEncoder;
import com.zxjk.duoduo.utils.SaveImageUtil;
import com.zxjk.duoduo.utils.ShareUtil;

import net.lucode.hackware.magicindicator.buildins.UIUtil;

import java.text.SimpleDateFormat;

import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import razerdp.basepopup.QuickPopupBuilder;
import razerdp.basepopup.QuickPopupConfig;
import razerdp.widget.QuickPopup;

public class InviterActivity extends BaseActivity {
    private int page = 1;

    private ImageView ivQR;

    private String inviteWeb;
    private String description = "限时注册奖励，先注册先得";

    private QuickPopup invitePop;
    private QuickPopup popup;
    private RecyclerView recyclerView;
    private TextView tvInviteCount;
    private TextView tvCode;
    private TextView tvSocialName;
    private BaseQuickAdapter<GetInviteInfoResponse.ListBean, BaseViewHolder> adapter;

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd HH:mm");
    private int color1;
    private int color2;

    private GetUInvitationUrlBean r;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inviter);
        setTrasnferStatusBar(true);

        color1 = Color.parseColor("#999999");
        color2 = Color.parseColor("#4585F5");

        initView();

        bindData();

        save();
    }

    private void initView() {
        TextView tvtitle = findViewById(R.id.tv_title);
        tvCode = findViewById(R.id.tvCode);
        tvSocialName = findViewById(R.id.tvSocialName);
        tvtitle.setText(R.string.invite_friends1);
        ivQR = findViewById(R.id.ivQR);
        tvSocialName.setText("ID:" + Constant.currentUser.getNick());
    }

    @SuppressLint("CheckResult")
    private void bindData() {
        findViewById(R.id.rl_back).setOnClickListener(view -> finish());

        ServiceFactory.getInstance().getBaseService(Api.class)
                .getUInvitationUrl()
                .compose(bindToLifecycle())
                .compose(RxSchedulers.normalTrans())
                .compose(RxSchedulers.ioObserver(CommonUtils.initDialog(this)))
                .subscribe(r -> {
                    this.r = r;
                    inviteWeb = r.getUrl();
                    tvCode.setText(r.getInviteCode());

                    Observable.create((ObservableOnSubscribe<Bitmap>) e ->
                            e.onNext(QRCodeEncoder.syncEncodeQRCode(inviteWeb, UIUtil.dip2px(this, 72), Color.BLACK))).compose(RxSchedulers.ioObserver())
                            .compose(bindToLifecycle())
                            .subscribe(b -> ivQR.setImageBitmap(b));
                }, this::handleApiError);
    }

    public void showList(View view) {
        if (popup == null) {
            TranslateAnimation showAnimation = new TranslateAnimation(0f, 0f, ScreenUtils.getScreenHeight(), 0f);
            showAnimation.setDuration(250);
            TranslateAnimation dismissAnimation = new TranslateAnimation(0f, 0f, 0f, ScreenUtils.getScreenHeight());
            dismissAnimation.setDuration(500);
            popup = QuickPopupBuilder.with(this)
                    .contentView(R.layout.popup_invite_list)
                    .config(new QuickPopupConfig()
                            .withShowAnimation(showAnimation)
                            .withDismissAnimation(dismissAnimation)
                            .withClick(R.id.ivSlide, null, true))
                    .show();

            recyclerView = popup.findViewById(R.id.recycler);
            tvInviteCount = popup.findViewById(R.id.tvInviteCount);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));

            adapter = new BaseQuickAdapter<GetInviteInfoResponse.ListBean, BaseViewHolder>(R.layout.item_invite) {
                @Override
                protected void convert(BaseViewHolder helper, GetInviteInfoResponse.ListBean item) {
                    helper.setVisible(R.id.ivSigned, item.getIsAuthentication().equals("1"))
                            .setText(R.id.tvName, item.getNick())
                            .setText(R.id.tvTime, sdf.format(Long.parseLong(item.getInviteDate())));
                    TextView tv = helper.getView(R.id.tvReward);
                    if (item.getIsAuthentication().equals("0")) {
                        tv.setTextColor(color1);
                        tv.setText("认证中");
                    } else {
                        tv.setTextColor(color2);
                        tv.setText(item.getBalance() + item.getSymbol());
                    }
                    ImageView iv = helper.getView(R.id.ivHead);
                    GlideUtil.loadCircleImg(iv, item.getHeadPortrait());
                }
            };

            adapter.setLoadMoreView(new NewsLoadMoreView());
            adapter.setEnableLoadMore(true);
            adapter.setOnLoadMoreListener(this::initData, recyclerView);
            View inflate = LayoutInflater.from(this).inflate(R.layout.empty_publicgroup, null, false);
            TextView tv = inflate.findViewById(R.id.tv);
            ImageView iv = inflate.findViewById(R.id.iv);
            tv.setText(R.string.emptylist1);
            iv.setImageResource(R.drawable.ic_empty_orders);
            adapter.setEmptyView(inflate);
            recyclerView.setAdapter(adapter);

            initData();

        } else {
            popup.showPopupWindow();
        }
    }

    @SuppressLint("CheckResult")
    private void initData() {
        ServiceFactory.getInstance().getBaseService(Api.class)
                .getInviteInfo(0)
                .compose(bindToLifecycle())
                .compose(RxSchedulers.ioObserver())
                .compose(RxSchedulers.normalTrans())
                .subscribe(r -> {
                    if (TextUtils.isEmpty(tvInviteCount.getText())) {
                        tvInviteCount.setText(r.getTotal() + "人");
                    }
                    page += 1;
                    if (adapter.getData().size() == 0) {
                        adapter.setNewData(r.getList());
                        adapter.disableLoadMoreIfNotFullPage();
                    } else {
                        if (r.isHasNextPage()) {
                            adapter.loadMoreComplete();
                        } else {
                            adapter.loadMoreEnd(false);
                        }

                        adapter.addData(r.getList());
                    }
                }, t -> {
                    if (page != 1) {
                        adapter.loadMoreFail();
                    }
                    handleApiError(t);
                });
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
                            .withClick(R.id.tv4, v -> shareTo(4), true)
                            .withClick(R.id.tv5, v -> {
                                ToastUtils.showShort(R.string.duplicated_to_clipboard);
                                ClipboardManager cm = (ClipboardManager) Utils.getApp().getSystemService(Context.CLIPBOARD_SERVICE);
                                if (cm != null) {
                                    cm.setPrimaryClip(ClipData.newPlainText("text", inviteWeb));
                                }
                            }, true)
                    )
                    .show();
        } else {
            invitePop.showPopupWindow();
        }
    }

    private void shareTo(int plantform) {
        UMWeb link = new UMWeb(inviteWeb);
        link.setTitle(Constant.currentUser.getNick() + "邀请你加入海浪社区");
        link.setDescription(description);
        link.setThumb(new UMImage(this, R.drawable.ic_share_red));
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

    private void save() {
        getPermisson(findViewById(R.id.llSave),
                granted -> Observable.create((ObservableOnSubscribe<Boolean>)
                        e -> SaveImageUtil.get().savePic(ScreenUtils.screenShot(InviterActivity.this, false),
                                success -> {
                                    if (success) e.onNext(true);
                                    else e.onNext(false);
                                })).compose(bindToLifecycle()).compose(RxSchedulers.ioObserver(CommonUtils.initDialog(InviterActivity.this)))
                        .subscribe(success -> {
                            if (success) {
                                ToastUtils.showShort(R.string.savesucceed);
                                return;
                            }
                            ToastUtils.showShort(R.string.savefailed);
                        }), Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE);
    }

    public void help(View view) {
        NiceDialog.init().setLayoutId(R.layout.dialog_invite_help1).setConvertListener(new ViewConvertListener() {
            @Override
            protected void convertView(ViewHolder holder, BaseNiceDialog dialog) {
                TextView tv = holder.getView(R.id.tv);

                SpannableStringBuilder ssb = new SpannableStringBuilder();
                for (int i = 0; i < r.getInvitationMessage().size(); i++) {
                    ssb.append(r.getInvitationMessage().get(i));
                    if (i != r.getInvitationMessage().size() - 1) {
                        ssb.append("\n");
                    }
                }

                ssb.setSpan(new BulletSpan(CommonUtils.dip2px(InviterActivity.this, 8), 0xff4585f5), 0, r.getInvitationMessage().get(0).length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                ssb.setSpan(new BulletSpan(CommonUtils.dip2px(InviterActivity.this, 8), 0xff3dcc9c), r.getInvitationMessage().get(0).length() + 1, r.getInvitationMessage().get(1).length() + 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                ssb.setSpan(new BulletSpan(CommonUtils.dip2px(InviterActivity.this, 8), 0xff4585f5), r.getInvitationMessage().get(0).length() + r.getInvitationMessage().get(1).length() + 2, ssb.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

                tv.setText(ssb);
            }
        }).setDimAmount(0.5f).setOutCancel(true).show(getSupportFragmentManager());
    }

}

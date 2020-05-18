package com.zxjk.duoduo.ui.minepage;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.blankj.utilcode.util.Utils;
import com.bumptech.glide.Glide;
import com.trello.rxlifecycle3.android.FragmentEvent;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.zxjk.duoduo.Application;
import com.zxjk.duoduo.Constant;
import com.zxjk.duoduo.R;
import com.zxjk.duoduo.bean.RedFallActivityLocalBeanDao;
import com.zxjk.duoduo.db.RedFallActivityLocalBean;
import com.zxjk.duoduo.network.Api;
import com.zxjk.duoduo.network.ServiceFactory;
import com.zxjk.duoduo.network.rx.RxSchedulers;
import com.zxjk.duoduo.ui.HomeActivity;
import com.zxjk.duoduo.ui.base.BaseFragment;
import com.zxjk.duoduo.ui.wallet.WalletActivity;
import com.zxjk.duoduo.ui.msgpage.MyQrCodeActivity;
import com.zxjk.duoduo.ui.widget.dialog.ConfirmDialog;
import com.zxjk.duoduo.ui.widget.dialog.MuteRemoveDialog;
import com.zxjk.duoduo.utils.CommonUtils;
import com.zxjk.duoduo.utils.ShareUtil;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import de.hdodenhof.circleimageview.CircleImageView;
import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;

public class MineFragment extends BaseFragment implements View.OnClickListener {
    private CircleImageView ivHead;
    private ImageView ivMineAuthSign;
    private TextView tvNick;
    private TextView tvMochatID;
    private TextView tvSign;
    private LinearLayout llRedFall;
    private ImageView ivRedFall;
    private TextView tvMindRedCountDown;

    private RedFallActivityLocalBeanDao redFallActivityLocalBeanDao;

    private SimpleDateFormat sdf = new SimpleDateFormat("mm:ss");

    private Disposable go;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.fragment_mine, container, false);

        ivHead = view.findViewById(R.id.ivHead);
        ivMineAuthSign = view.findViewById(R.id.ivMineAuthSign);
        tvNick = view.findViewById(R.id.tvNick);
        tvMochatID = view.findViewById(R.id.tvMochatID);
        tvSign = view.findViewById(R.id.tvSign);
        llRedFall = view.findViewById(R.id.llRedFall);
        ivRedFall = view.findViewById(R.id.ivRedFall);
        tvMindRedCountDown = view.findViewById(R.id.tvMindRedCountDown);

        view.findViewById(R.id.llMineTop).setOnClickListener(this);
        view.findViewById(R.id.ivBlockWallet).setOnClickListener(this);
        view.findViewById(R.id.llMine2).setOnClickListener(this);
        view.findViewById(R.id.llMine4).setOnClickListener(this);
        view.findViewById(R.id.llMine5).setOnClickListener(this);
        view.findViewById(R.id.llMine7).setOnClickListener(this);
        view.findViewById(R.id.ivQR).setOnClickListener(this);
        view.findViewById(R.id.llMineInvite).setOnClickListener(this);

        redFallActivityLocalBeanDao = Application.daoSession.getRedFallActivityLocalBeanDao();

        return view;
    }

    @SuppressLint("CheckResult")
    private void initRedFall(RedFallActivityLocalBean redFallActivityLocalBean) {
        Animator animatorObject = AnimatorInflater.loadAnimator(getActivity(), R.animator.animator_swing_redfall);
        animatorObject.setTarget(llRedFall);
        animatorObject.start();

        ivRedFall.setOnClickListener(v -> {
            if (!tvMindRedCountDown.getText().toString().equals("Go")) return;
            if (redFallActivityLocalBean.getReceiveCount() != 0 && "1".equals(redFallActivityLocalBean.getOpenShare()) && redFallActivityLocalBean.getShareCount() == 0) {
                // 提示分享
                new ConfirmDialog(getActivity(), getString(R.string.rc_ext_warning), getString(R.string.share_tip),
                        view -> {
                            String text = "【Hilamg APP---专注于区块链+社交】\n" +
                                    "我在Hilamg抢到了" + redFallActivityLocalBean.getReward() + redFallActivityLocalBean.getSymbol() + "，你也来试试吧~"
                                    + "http://hilamg-register.ztoken.cn/red/redPage.html?id=" + Constant.userId + "点击领取奖励";
                            ClipboardManager cm = (ClipboardManager) Utils.getApp().getSystemService(Context.CLIPBOARD_SERVICE);
                            if (cm != null) {
                                cm.setPrimaryClip(ClipData.newPlainText("text", text));
                            }

                            MuteRemoveDialog dialog = new MuteRemoveDialog(getActivity(), "取消", "去微信", "分享至朋友圈", "文案已自动生成，快去粘贴吧！");
                            dialog.setOnCommitListener(() -> ShareUtil.share2WTimeline(getActivity(), new ShareUtil.ShareListener() {
                                @Override
                                public void onStart(SHARE_MEDIA share_media) {
                                    super.onStart(share_media);
                                    ServiceFactory.getInstance().getBaseService(Api.class)
                                            .shareAirdrop()
                                            .compose(bindUntilEvent(FragmentEvent.DESTROY))
                                            .compose(RxSchedulers.ioObserver(CommonUtils.initDialog(getActivity())))
                                            .compose(RxSchedulers.normalTrans())
                                            .subscribe(s -> {
                                                if (!TextUtils.isEmpty(s) && s.equals("1")) {
                                                    redFallActivityLocalBean.setLastPlayTime("0");
                                                    redFallActivityLocalBean.setShareCount(redFallActivityLocalBean.getShareCount() + 1);
                                                    redFallActivityLocalBeanDao.update(redFallActivityLocalBean);
                                                }
                                            }, t -> handleApiError(t));
                                }

                                @Override
                                public void onError(SHARE_MEDIA share_media, Throwable throwable) {
                                    super.onError(share_media, throwable);
                                }
                            }));
                            dialog.show();
                        }).show();
            } else {
                ((HomeActivity) getActivity()).hideFourthBadge();
                startActivity(new Intent(getActivity(), RedFallActivity.class));
                getActivity().overridePendingTransition(R.anim.redfall_enteranim, R.anim.redfall_exitanim);
            }
        });

        if (!TextUtils.isEmpty(redFallActivityLocalBean.getLastPlayTime()) && redFallActivityLocalBean.getLastPlayTime().equals("0")) {
            tvMindRedCountDown.setText("Go");
        } else {
            go = Observable.interval(0, 1, TimeUnit.SECONDS)
                    .compose(bindToLifecycle())
                    .compose(RxSchedulers.ioObserver())
                    .subscribe(l -> onRedCountDown(Long.parseLong(redFallActivityLocalBean.getLastPlayTime())));
        }
    }

    private void onRedCountDown(long l1) {
        long timeLeft = 600 * 1000 - (System.currentTimeMillis() - l1);
        if (timeLeft < 1000) {
            tvMindRedCountDown.setText("Go");
            ((HomeActivity) getActivity()).showFourthBadge();
            if (go != null && !go.isDisposed()) {
                go.dispose();
            }
        } else {
            tvMindRedCountDown.setText(sdf.format(new Date(timeLeft)));
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        bindData();

        List<RedFallActivityLocalBean> redFallActivityLocalBeans = redFallActivityLocalBeanDao.loadAll();
        if (redFallActivityLocalBeans.size() == 0) {
            //redfall unable nowtime
            llRedFall.setVisibility(View.GONE);
        } else {
            llRedFall.setVisibility(View.VISIBLE);
            RedFallActivityLocalBean redFallActivityLocalBean = redFallActivityLocalBeans.get(0);
            initRedFall(redFallActivityLocalBean);
        }
    }

    private void bindData() {
        Glide.with(getContext()).load(Constant.currentUser.getHeadPortrait()).into(ivHead);
        tvNick.setText(Constant.currentUser.getNick());
        if (TextUtils.isEmpty(Constant.currentUser.getSignature())) {
            tvSign.setText(getString(R.string.sign1) + getString(R.string.none));
        } else {
            tvSign.setText(getString(R.string.sign1) + Constant.currentUser.getSignature());
        }
        String isAuthentication = Constant.currentUser.getIsAuthentication();
        if (!TextUtils.isEmpty(isAuthentication) && isAuthentication.equals("0")) {
            ivMineAuthSign.setVisibility(View.VISIBLE);
        } else {
            ivMineAuthSign.setVisibility(View.INVISIBLE);
        }
        tvMochatID.setText(getString(R.string.mochatid) + Constant.currentUser.getDuoduoId());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.llMineTop:
                startActivity(new Intent(getActivity(), UserInfoActivity.class));
                break;
            case R.id.ivBlockWallet:
                startActivity(new Intent(getActivity(), WalletActivity.class));
                break;
            case R.id.llMine2:
                startActivity(new Intent(getActivity(), CooperateActivity.class));
                break;
            case R.id.llMineInvite:
                startActivity(new Intent(getActivity(), InviterActivity.class));
                break;
            case R.id.llMine4:
                startActivity(new Intent(getActivity(), OnlineServiceActivity.class));
                break;
            case R.id.llMine5:
                startActivity(new Intent(getActivity(), SettingActivity.class));
                break;
            case R.id.llMine7:
                Intent intent = new Intent(getContext(), RewardMotActivity.class);
                getActivity().startActivityForResult(intent, HomeActivity.REQUEST_REWARD);
                break;
            case R.id.ivQR:
                startActivity(new Intent(getActivity(), MyQrCodeActivity.class));
                break;
            default:
        }
    }
}

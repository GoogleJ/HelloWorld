package com.zxjk.duoduo.ui.minepage;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.annotation.SuppressLint;
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

import com.bumptech.glide.Glide;
import com.zxjk.duoduo.Application;
import com.zxjk.duoduo.Constant;
import com.zxjk.duoduo.R;
import com.zxjk.duoduo.bean.RedFallActivityLocalBeanDao;
import com.zxjk.duoduo.db.RedFallActivityLocalBean;
import com.zxjk.duoduo.network.rx.RxSchedulers;
import com.zxjk.duoduo.ui.HomeActivity;
import com.zxjk.duoduo.ui.base.BaseFragment;
import com.zxjk.duoduo.ui.minepage.wallet.WalletActivity;
import com.zxjk.duoduo.ui.msgpage.MyQrCodeActivity;

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
    private TextView tvMot2Reward;
    private LinearLayout llRedFall;
    private ImageView ivRedFall;
    private TextView tvMindRedCountDown;

    private RedFallActivityLocalBeanDao redFallActivityLocalBeanDao;

    private SimpleDateFormat sdf = new SimpleDateFormat("mm:ss");

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.fragment_mine, container, false);

        ivHead = view.findViewById(R.id.ivHead);
        ivMineAuthSign = view.findViewById(R.id.ivMineAuthSign);
        tvNick = view.findViewById(R.id.tvNick);
        tvMochatID = view.findViewById(R.id.tvMochatID);
        tvSign = view.findViewById(R.id.tvSign);
        tvMot2Reward = view.findViewById(R.id.tvMot2Reward);
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

    private Disposable go;

    @SuppressLint("CheckResult")
    private void initRedFall(String lastPlayTime) {
        Animator animatorObject = AnimatorInflater.loadAnimator(getActivity(), R.animator.animator_swing_redfall);
        animatorObject.setTarget(llRedFall);
        animatorObject.start();

        ivRedFall.setOnClickListener(v -> {
            if (tvMindRedCountDown.getText().toString().equals("Go")) {
                ((HomeActivity) getActivity()).hideFourthBadge();
                startActivity(new Intent(getActivity(), RedFallActivity.class));
                getActivity().overridePendingTransition(R.anim.redfall_enteranim, R.anim.redfall_exitanim);
            }
        });

        if (!TextUtils.isEmpty(lastPlayTime) && lastPlayTime.equals("0")) {
            tvMindRedCountDown.setText("Go");
        } else {
            long l1 = Long.parseLong(lastPlayTime);
            go = Observable.interval(0, 1, TimeUnit.SECONDS)
                    .compose(bindToLifecycle())
                    .compose(RxSchedulers.ioObserver())
                    .subscribe(l -> onRedCountDown(l1));
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
            String lastPlayTime = redFallActivityLocalBean.getLastPlayTime();

            initRedFall(lastPlayTime);
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
                tvMot2Reward.setVisibility(View.GONE);
                Intent intent = new Intent(getContext(),RewardMotActivity.class);
                intent.putExtra("head",Constant.currentUser.getHeadPortrait());
                startActivity(intent);
                break;
            case R.id.ivQR:
                startActivity(new Intent(getActivity(), MyQrCodeActivity.class));
                break;
            default:
        }
    }
}

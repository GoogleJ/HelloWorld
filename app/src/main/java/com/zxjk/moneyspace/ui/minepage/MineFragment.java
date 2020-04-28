package com.zxjk.moneyspace.ui.minepage;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.alibaba.security.rp.RPSDK;
import com.blankj.utilcode.util.ToastUtils;
import com.bumptech.glide.Glide;
import com.trello.rxlifecycle3.android.FragmentEvent;
import com.zxjk.moneyspace.Constant;
import com.zxjk.moneyspace.R;
import com.zxjk.moneyspace.network.Api;
import com.zxjk.moneyspace.network.ServiceFactory;
import com.zxjk.moneyspace.network.rx.RxException;
import com.zxjk.moneyspace.network.rx.RxSchedulers;
import com.zxjk.moneyspace.ui.base.BaseFragment;
import com.zxjk.moneyspace.ui.minepage.wallet.BalanceLeftActivity;
import com.zxjk.moneyspace.ui.minepage.wallet.OneKeyBuyCoinActivity;
import com.zxjk.moneyspace.ui.msgpage.MyQrCodeActivity;
import com.zxjk.moneyspace.utils.CommonUtils;

import de.hdodenhof.circleimageview.CircleImageView;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class MineFragment extends BaseFragment implements View.OnClickListener {
    private CircleImageView ivHead;
    private ImageView ivMineAuthSign;
    private TextView tvNick;
    private TextView tvMochatID;
    private TextView tvSign;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.fragment_mine, container, false);

        ivHead = view.findViewById(R.id.ivHead);
        ivMineAuthSign = view.findViewById(R.id.ivMineAuthSign);
        tvNick = view.findViewById(R.id.tvNick);
        tvMochatID = view.findViewById(R.id.tvMochatID);
        tvSign = view.findViewById(R.id.tvSign);

        view.findViewById(R.id.llMineTop).setOnClickListener(this);
        view.findViewById(R.id.llMine1).setOnClickListener(this);
        view.findViewById(R.id.llMine2).setOnClickListener(this);
        view.findViewById(R.id.llMine3).setOnClickListener(this);
        view.findViewById(R.id.llMine4).setOnClickListener(this);
        view.findViewById(R.id.llMine5).setOnClickListener(this);
        view.findViewById(R.id.ivQR).setOnClickListener(this);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        bindData();
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

    @SuppressLint("CheckResult")
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.llMineTop:
                startActivity(new Intent(getActivity(), UserInfoActivity.class));
                break;
            case R.id.llMine1:
                startActivity(new Intent(getActivity(), BalanceLeftActivity.class));
                break;
            case R.id.llMine2:
                startActivity(new Intent(getActivity(), OneKeyBuyCoinActivity.class));
                break;
            case R.id.llMine3:
                if (Constant.currentUser.getIsAuthentication().equals("2")) {
                    ToastUtils.showShort(R.string.verifying_pleasewait);
                } else if (Constant.currentUser.getIsAuthentication().equals("0")) {
                    ToastUtils.showShort(R.string.authen_true);
                } else {
                    Api api = ServiceFactory.getInstance().getBaseService(Api.class);
                    api.getAuthToken()
                            .compose(bindUntilEvent(FragmentEvent.DESTROY))
                            .compose(RxSchedulers.normalTrans())
                            .compose(RxSchedulers.ioObserver(CommonUtils.initDialog(getActivity())))
                            .flatMap(s -> Observable.create(emitter ->
                                    RPSDK.start(s, getActivity(), (audit, s1) -> {
                                        if (audit == RPSDK.AUDIT.AUDIT_PASS || audit == RPSDK.AUDIT.AUDIT_FAIL) {
                                            emitter.onNext(true);
                                        } else {
                                            emitter.onError(new RxException.ParamsException("认证失败,请稍后尝试", 100));
                                        }
                                    })))
                            .observeOn(Schedulers.io())
                            .flatMap(b -> api.initAuthData())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(s -> Constant.currentUser.setIsAuthentication("0"), this::handleApiError);
                }
                break;
            case R.id.llMine4:
                startActivity(new Intent(getActivity(), BankCardActivity.class));
                break;
            case R.id.llMine5:
                startActivity(new Intent(getActivity(), SettingActivity.class));
                break;
            case R.id.ivQR:
                startActivity(new Intent(getActivity(), MyQrCodeActivity.class));
                break;
            default:
        }
    }
}

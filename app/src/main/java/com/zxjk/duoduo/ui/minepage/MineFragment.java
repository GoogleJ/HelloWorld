package com.zxjk.duoduo.ui.minepage;

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

import com.blankj.utilcode.util.ToastUtils;
import com.bumptech.glide.Glide;
import com.shehuan.nicedialog.BaseNiceDialog;
import com.shehuan.nicedialog.NiceDialog;
import com.shehuan.nicedialog.ViewConvertListener;
import com.shehuan.nicedialog.ViewHolder;
import com.zxjk.duoduo.Constant;
import com.zxjk.duoduo.R;
import com.zxjk.duoduo.network.Api;
import com.zxjk.duoduo.network.ServiceFactory;
import com.zxjk.duoduo.network.rx.RxSchedulers;
import com.zxjk.duoduo.ui.HomeActivity;
import com.zxjk.duoduo.ui.LoginActivity;
import com.zxjk.duoduo.ui.base.BaseFragment;
import com.zxjk.duoduo.ui.msgpage.MyQrCodeActivity;
import com.zxjk.duoduo.utils.CommonUtils;
import com.zxjk.duoduo.utils.MMKVUtils;

import de.hdodenhof.circleimageview.CircleImageView;
import io.rong.imkit.RongIM;

public class MineFragment extends BaseFragment implements View.OnClickListener {

    private CircleImageView ivHead;
    private ImageView ivMineAuthSign;
    private TextView tvNick;
    private TextView tvMochatID;
    private TextView tvSign;

    private TextView tvMot2Reward;

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

        view.findViewById(R.id.llMineTop).setOnClickListener(this);
        view.findViewById(R.id.ivBlockWallet).setOnClickListener(this);
        view.findViewById(R.id.llMine1).setOnClickListener(this);
        view.findViewById(R.id.llMine2).setOnClickListener(this);
        view.findViewById(R.id.llMine3).setOnClickListener(this);
        view.findViewById(R.id.llMine4).setOnClickListener(this);
        view.findViewById(R.id.llMine5).setOnClickListener(this);
        view.findViewById(R.id.llMine6).setOnClickListener(this);
        view.findViewById(R.id.llMine7).setOnClickListener(this);
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.llMineTop:
                startActivity(new Intent(getActivity(), UserInfoActivity.class));
                break;
            case R.id.ivBlockWallet:
                startActivity(new Intent(getActivity(), WalletActivity.class));
                break;
            case R.id.llMine1:
                ToastUtils.showShort(R.string.developing);
                break;
            case R.id.llMine2:
                startActivity(new Intent(getActivity(), CooperateActivity.class));
                break;
            case R.id.llMine3:
                ((HomeActivity) getActivity()).getVersion(true);
                break;
            case R.id.llMine4:
                startActivity(new Intent(getActivity(), OnlineServiceActivity.class));
                break;
            case R.id.llMine5:
                startActivity(new Intent(getActivity(), SettingActivity.class));
                break;
            case R.id.llMine6:
                NiceDialog.init().setLayoutId(R.layout.layout_general_dialog).setConvertListener(new ViewConvertListener() {
                    @SuppressLint("CheckResult")
                    @Override
                    protected void convertView(ViewHolder holder, BaseNiceDialog dialog) {
                        holder.setText(R.id.tv_title, "提示");
                        holder.setText(R.id.tv_content, "您将退出登录");
                        holder.setText(R.id.tv_cancel, "取消");
                        holder.setText(R.id.tv_notarize, "确认");
                        holder.setOnClickListener(R.id.tv_cancel, v1 -> dialog.dismiss());
                        holder.setOnClickListener(R.id.tv_notarize, v12 -> {
                            dialog.dismiss();
                            ServiceFactory.getInstance().getBaseService(Api.class)
                                    .loginOut()
                                    .compose(RxSchedulers.ioObserver(CommonUtils.initDialog(getActivity())))
                                    .compose(RxSchedulers.normalTrans())
                                    .subscribe(s -> {
                                        RongIM.getInstance().logout();
                                        MMKVUtils.getInstance().enCode("isLogin", false);
                                        Constant.clear();
                                        ToastUtils.showShort(R.string.login_out);
                                        Intent intent = new Intent(getActivity(), LoginActivity.class);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);
                                    }, MineFragment.this::handleApiError);
                        });
                    }
                }).setDimAmount(0.5f).setOutCancel(false).show(getChildFragmentManager());
                break;
            case R.id.llMine7:
                tvMot2Reward.setVisibility(View.GONE);
                startActivity(new Intent(getContext(), RewardMotActivity.class));
                break;
            case R.id.ivQR:
                startActivity(new Intent(getActivity(), MyQrCodeActivity.class));
                break;
            default:
        }
    }
}

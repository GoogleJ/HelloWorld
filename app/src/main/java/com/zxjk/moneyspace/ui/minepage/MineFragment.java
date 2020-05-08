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

import com.blankj.utilcode.util.ToastUtils;
import com.bumptech.glide.Glide;
import com.zxjk.moneyspace.Constant;
import com.zxjk.moneyspace.R;
import com.zxjk.moneyspace.ui.ShiRenActivity;
import com.zxjk.moneyspace.ui.base.BaseFragment;
import com.zxjk.moneyspace.ui.minepage.wallet.BalanceLeftActivity;
import com.zxjk.moneyspace.ui.minepage.wallet.EcologyActivity;
import com.zxjk.moneyspace.ui.minepage.wallet.OneKeyBuyCoinActivity;
import com.zxjk.moneyspace.ui.msgpage.MyQrCodeActivity;

import de.hdodenhof.circleimageview.CircleImageView;

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
        view.findViewById(R.id.llMine6).setOnClickListener(this);

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
                    startActivityForResult(new Intent(getContext(), ShiRenActivity.class), 399);
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
            case R.id.llMine6:
                startActivity(new Intent(getActivity(), EcologyActivity.class));
                break;
            default:
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 399 && resultCode == 1) {
            Constant.currentUser.setIsAuthentication("0");
        }
    }
}

package com.zxjk.moneyspace.ui.msgpage;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.blankj.utilcode.util.BarUtils;
import com.blankj.utilcode.util.KeyboardUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.google.gson.Gson;
import com.zxjk.moneyspace.Constant;
import com.zxjk.moneyspace.R;
import com.zxjk.moneyspace.bean.request.SendRedSingleRequest;
import com.zxjk.moneyspace.bean.response.GetPaymentListBean;
import com.zxjk.moneyspace.bean.response.LoginResponse;
import com.zxjk.moneyspace.network.Api;
import com.zxjk.moneyspace.network.ServiceFactory;
import com.zxjk.moneyspace.network.rx.RxSchedulers;
import com.zxjk.moneyspace.ui.base.BaseActivity;
import com.zxjk.moneyspace.ui.minepage.DetailListActivity;
import com.zxjk.moneyspace.ui.minepage.wallet.ChooseCoinActivity;
import com.zxjk.moneyspace.ui.msgpage.rongIM.message.RedPacketMessage;
import com.zxjk.moneyspace.ui.widget.NewPayBoard;
import com.zxjk.moneyspace.utils.CommonUtils;
import com.zxjk.moneyspace.utils.GlideUtil;
import com.zxjk.moneyspace.utils.MD5Utils;
import com.zxjk.moneyspace.utils.MoneyValueFilter;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.functions.Function;
import io.rong.imkit.RongIM;
import io.rong.imlib.IRongCallback;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.Message;
import io.rong.imlib.model.UserInfo;

public class PrivacyRedPacketActivity extends BaseActivity {

    private String money;
    private UserInfo userInfo;

    private ImageView ivCoinIcon;
    private TextView tvCoin;
    private EditText etMoney;
    private EditText etBless;
    private FrameLayout flTop;

    private GetPaymentListBean result;
    private ArrayList<GetPaymentListBean> list = new ArrayList<>();

    @SuppressLint("CheckResult")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_red_envelopes);
        BarUtils.setStatusBarColor(this, Color.parseColor("#FF665B"));

        Api api = ServiceFactory.getInstance().getBaseService(Api.class);

        userInfo = getIntent().getParcelableExtra("user");
        if (null == userInfo) {
            api.getCustomerInfoById(getIntent().getStringExtra("userId"))
                    .compose(bindToLifecycle())
                    .compose(RxSchedulers.normalTrans())
                    .flatMap((Function<LoginResponse, Observable<List<GetPaymentListBean>>>) r -> {
                        userInfo = new UserInfo(r.getId(), r.getNick(), Uri.parse(r.getHeadPortrait()));
                        return api.getPaymentList().compose(RxSchedulers.normalTrans());
                    })
                    .compose(RxSchedulers.ioObserver(CommonUtils.initDialog(this)))
                    .subscribe(l -> {
                        list.addAll(l);
                        result = list.get(0);
                        GlideUtil.loadCircleImg(ivCoinIcon, result.getLogo());
                        tvCoin.setText(result.getSymbol());
                    }, t -> {
                        finish();
                        handleApiError(t);
                    });
        } else {
            api.getPaymentList()
                    .compose(bindToLifecycle())
                    .compose(RxSchedulers.normalTrans())
                    .compose(RxSchedulers.ioObserver(CommonUtils.initDialog(this)))
                    .subscribe(l -> {
                        list.addAll(l);
                        result = list.get(0);
                        GlideUtil.loadCircleImg(ivCoinIcon, result.getLogo());
                        tvCoin.setText(result.getSymbol());
                        etMoney.setHint("可用" + result.getBalance() + result.getSymbol());

                        int moneyNums;
                        if (result.getSymbol().equals("CNY")) {
                            moneyNums = 2;
                        } else {
                            moneyNums = 4;
                        }
                        etMoney.setFilters(new InputFilter[]{new MoneyValueFilter().setDigits(moneyNums)});
                    }, t -> {
                        handleApiError(t);
                        finish();
                    });
        }

        ivCoinIcon = findViewById(R.id.ivCoinIcon);
        tvCoin = findViewById(R.id.tvCoin);
        etMoney = findViewById(R.id.etMoney);
        etBless = findViewById(R.id.etBless);
        flTop = findViewById(R.id.flTop);

        BarUtils.addMarginTopEqualStatusBarHeight(flTop);
    }

    @SuppressLint("CheckResult")
    public void sendRed(View view) {
        money = etMoney.getText().toString().trim();
        if (result == null) {
            ToastUtils.showShort(R.string.select_cointype);
            return;
        }
        if (((TextUtils.isEmpty(money) || Double.parseDouble(money) == 0))) {
            ToastUtils.showShort(R.string.input_redmoney);
            return;
        }
        if (Double.parseDouble(money) < 0.00001) {
            ToastUtils.showShort(R.string.less_min);
            return;
        }

        KeyboardUtils.hideSoftInput(this);
        new NewPayBoard(this).show(psw -> {
            SendRedSingleRequest redSingleRequest = new SendRedSingleRequest();
            String msgRemark;
            if (TextUtils.isEmpty(etBless.getText().toString().trim())) {
                msgRemark = getString(R.string.m_red_envelopes_label);
            } else {
                msgRemark = etBless.getText().toString().trim();
            }
            redSingleRequest.setMessage(msgRemark);
            redSingleRequest.setMoney(Double.parseDouble(money));
            redSingleRequest.setReceiveCustomerId(userInfo.getUserId());
            redSingleRequest.setPayPwd(MD5Utils.getMD5(psw));
            redSingleRequest.setSymbol(result.getSymbol());

            ServiceFactory.getInstance().getBaseService(Api.class)
                    .sendSingleRedPackage(new Gson().toJson(redSingleRequest))
                    .compose(bindToLifecycle())
                    .compose(RxSchedulers.ioObserver(CommonUtils.initDialog(PrivacyRedPacketActivity.this)))
                    .compose(RxSchedulers.normalTrans())
                    .subscribe(red -> {
                        RedPacketMessage message = new RedPacketMessage();
                        message.setFromCustomer(Constant.currentUser.getId());
                        message.setRemark(msgRemark);
                        message.setRedId(red.getId());
                        message.setIsGame("1");
                        Message message1 = Message.obtain(userInfo.getUserId(), Conversation.ConversationType.PRIVATE, message);
                        RongIM.getInstance().sendMessage(message1, null, null, new IRongCallback.ISendMessageCallback() {
                            @Override
                            public void onAttached(Message message) {
                            }

                            @Override
                            public void onSuccess(Message message) {
                                finish();
                            }

                            @Override
                            public void onError(Message message, RongIMClient.ErrorCode errorCode) {
                            }
                        });
                    }, this::handleApiError);
        });
    }

    public void chooseCoin(View view) {
        Intent intent = new Intent(this, ChooseCoinActivity.class);
        intent.putExtra("data", list);
        startActivityForResult(intent, 1);
    }

    public void back(View view) {
        finish();
    }

    public void redList(View view) {
        startActivity(new Intent(this, DetailListActivity.class));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data == null) return;
        if (requestCode == 1 && resultCode == 1) {
            result = data.getParcelableExtra("result");
            GlideUtil.loadCircleImg(ivCoinIcon, result.getLogo());
            tvCoin.setText(result.getSymbol());
            etMoney.setHint("可用" + result.getBalance() + result.getSymbol());

            int moneyNums;
            if (result.getSymbol().equals("CNY")) {
                moneyNums = 2;
            } else {
                moneyNums = 4;
            }
            etMoney.setFilters(new InputFilter[]{new MoneyValueFilter().setDigits(moneyNums)});
        }
    }
}

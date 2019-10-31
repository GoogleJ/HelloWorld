package com.zxjk.duoduo.ui.msgpage;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.blankj.utilcode.util.ToastUtils;
import com.github.ybq.android.spinkit.SpinKitView;
import com.zxjk.duoduo.Constant;
import com.zxjk.duoduo.R;
import com.zxjk.duoduo.network.Api;
import com.zxjk.duoduo.network.ServiceFactory;
import com.zxjk.duoduo.network.rx.RxSchedulers;
import com.zxjk.duoduo.ui.HomeActivity;
import com.zxjk.duoduo.ui.base.BaseActivity;
import com.zxjk.duoduo.ui.widget.NewPayBoard;
import com.zxjk.duoduo.utils.CommonUtils;
import com.zxjk.duoduo.utils.GlideUtil;
import com.zxjk.duoduo.utils.MD5Utils;

import io.reactivex.functions.Action;
import io.rong.imkit.RongIM;
import io.rong.imlib.IRongCallback;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.Message;
import io.rong.message.InformationNotificationMessage;

public class PayEnterGroupPayActivity extends BaseActivity {

    private String groupId;
    private String ownerId;
    private String payMoney;
    private String groupName;
    private String symbol;

    private TextView tvTitle;
    private TextView tvGroupName;
    private TextView tvMoney;
    private ImageView ivHead;
    private TextView tvGroupOnwerName;
    private TextView tvPay;
    private TextView tvUnit;
    private SpinKitView spinkit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_enter_group_pay);

        initView();
        initData();
    }

    private void initView() {
        tvTitle = findViewById(R.id.tv_title);
        tvGroupName = findViewById(R.id.tvGroupName);
        tvMoney = findViewById(R.id.tvMoney);
        ivHead = findViewById(R.id.ivHead);
        tvGroupOnwerName = findViewById(R.id.tvGroupOnwerName);
        tvPay = findViewById(R.id.tvPay);
        tvUnit = findViewById(R.id.tvUnit);
        spinkit = findViewById(R.id.spinkit);
        findViewById(R.id.rl_back).setOnClickListener(v -> finish());
        tvTitle.setText(R.string.pay_enter_group);
    }

    @SuppressLint("CheckResult")
    private void initData() {
        groupId = getIntent().getStringExtra("groupId");
        ownerId = getIntent().getStringExtra("ownerId");
        payMoney = getIntent().getStringExtra("payMoney");
        groupName = getIntent().getStringExtra("groupName");
        symbol = getIntent().getStringExtra("symbol");
        tvUnit.setText(symbol);

        ServiceFactory.getInstance().getBaseService(Api.class)
                .getCustomerInfoById(ownerId)
                .compose(bindToLifecycle())
                .compose(RxSchedulers.normalTrans())
                .compose(RxSchedulers.ioObserver(CommonUtils.initDialog(this)))
                .subscribe(r -> {
                    GlideUtil.loadCircleImg(ivHead, r.getHeadPortrait());
                    tvGroupName.setText(groupName);
                    tvMoney.setText(payMoney);
                    tvGroupOnwerName.setText("群主 " + r.getNick() + " 发起");
                }, this::handleApiError);
    }

    @SuppressLint("CheckResult")
    public void pay(View view) {
        if (spinkit.getVisibility() == View.VISIBLE) return;

        new NewPayBoard(this).show(r -> {
            tvPay.setText(R.string.paying);
            spinkit.setVisibility(View.VISIBLE);

            ServiceFactory.getInstance().getBaseService(Api.class)
                    .payToGroup(groupId, ownerId, MD5Utils.getMD5(r), payMoney, symbol)
                    .compose(bindToLifecycle())
                    .compose(RxSchedulers.normalTrans())
                    .compose(RxSchedulers.ioObserver())
                    .doOnTerminate(() -> spinkit.setVisibility(View.GONE))
                    .subscribe(s -> {
                        //发送进群灰条
                        InformationNotificationMessage notificationMessage = InformationNotificationMessage.obtain("\"" +
                                Constant.currentUser.getNick() + "\"加入了群组");
                        Message message = Message.obtain(groupId, Conversation.ConversationType.GROUP, notificationMessage);
                        RongIM.getInstance().sendMessage(message, "", "", (IRongCallback.ISendMessageCallback) null);
                        Intent intent = new Intent(PayEnterGroupPayActivity.this, HomeActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        RongIM.getInstance().startGroupChat(PayEnterGroupPayActivity.this, groupId, groupName);
                    }, t -> {
                        handleApiError(t);
                        tvPay.setText(R.string.pay_fail);
                    });
        });
    }
}

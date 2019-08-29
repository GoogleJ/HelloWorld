package com.zxjk.duoduo.ui.msgpage;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.blankj.utilcode.util.KeyboardUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.google.gson.Gson;
import com.zxjk.duoduo.Constant;
import com.zxjk.duoduo.R;
import com.zxjk.duoduo.bean.request.SendRedSingleRequest;
import com.zxjk.duoduo.network.Api;
import com.zxjk.duoduo.network.ServiceFactory;
import com.zxjk.duoduo.network.rx.RxSchedulers;
import com.zxjk.duoduo.ui.base.BaseActivity;
import com.zxjk.duoduo.ui.msgpage.rongIM.message.RedPacketMessage;
import com.zxjk.duoduo.ui.widget.NewPayBoard;
import com.zxjk.duoduo.utils.CommonUtils;
import com.zxjk.duoduo.utils.MD5Utils;
import com.zxjk.duoduo.utils.MoneyValueFilter;

import io.rong.imkit.RongIM;
import io.rong.imlib.IRongCallback;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.Message;
import io.rong.imlib.model.UserInfo;

public class PrivacyRedPacketActivity extends BaseActivity {
    TextView sendMessageBtn;
    EditText m_red_envelopes_money_edit;
    EditText m_red_envelopes_label;
    TextView m_red_envelopes_money_text;

    String money;
    UserInfo userInfo;

    public PrivacyRedPacketActivity() {
    }

    @SuppressLint("CheckResult")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_red_envelopes);

        TextView tv_title = findViewById(R.id.tv_title);
        tv_title.setText(getString(R.string.send_red));
        TextView tv_end = findViewById(R.id.tv_end);
        tv_end.setVisibility(View.VISIBLE);
        tv_end.setText(getString(R.string.m_red_envelopes_title_right_text));
        findViewById(R.id.rl_back).setOnClickListener(v -> finish());
        tv_end.setOnClickListener(v ->
                startActivity(new Intent(PrivacyRedPacketActivity.this, RedPackageListActivity.class)));

        m_red_envelopes_money_edit = findViewById(R.id.m_red_envelopes_money_edit);
        m_red_envelopes_label = findViewById(R.id.m_red_envelopes_label);
        m_red_envelopes_money_text = findViewById(R.id.m_red_envelopes_money_text);
        sendMessageBtn = findViewById(R.id.m_red_envelopes_commit_btn);

        userInfo = getIntent().getParcelableExtra("user");
        if (null == userInfo) {
            ServiceFactory.getInstance().getBaseService(Api.class)
                    .getCustomerInfoById(getIntent().getStringExtra("userId"))
                    .compose(bindToLifecycle())
                    .compose(RxSchedulers.normalTrans())
                    .compose(RxSchedulers.ioObserver(CommonUtils.initDialog(this)))
                    .subscribe(loginResponse -> userInfo = new UserInfo(loginResponse.getId(), loginResponse.getNick(), Uri.parse(loginResponse.getHeadPortrait())), this::handleApiError);
        }

        sendMessageBtn.setOnClickListener(v -> {
            money = m_red_envelopes_money_edit.getText().toString().trim();
            if (((TextUtils.isEmpty(money) || Double.parseDouble(money) == 0))) {
                ToastUtils.showShort(R.string.input_redmoney);
                return;
            }

            KeyboardUtils.hideSoftInput(this);
            new NewPayBoard(this).show(psw -> {
                SendRedSingleRequest redSingleRequest = new SendRedSingleRequest();
                String msgRemark;
                if (TextUtils.isEmpty(m_red_envelopes_label.getText().toString().trim())) {
                    msgRemark = getString(R.string.m_red_envelopes_label);
                } else {
                    msgRemark = m_red_envelopes_label.getText().toString().trim();
                }
                redSingleRequest.setMessage(msgRemark);
                redSingleRequest.setMoney(Double.parseDouble(money));
                redSingleRequest.setReceiveCustomerId(userInfo.getUserId());
                redSingleRequest.setPayPwd(MD5Utils.getMD5(psw));

                ServiceFactory.getInstance().getBaseService(Api.class)
                        .sendSingleRedPackage(new Gson().toJson(redSingleRequest))
                        .compose(bindToLifecycle())
                        .compose(RxSchedulers.ioObserver(CommonUtils.initDialog(PrivacyRedPacketActivity.this)))
                        .compose(RxSchedulers.normalTrans())
                        .subscribe(redPackageResponse -> {
                            RedPacketMessage message = new RedPacketMessage();
                            message.setFromCustomer(Constant.currentUser.getId());
                            message.setRemark(msgRemark);
                            message.setRedId(redPackageResponse.getId());
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
        });

        m_red_envelopes_money_edit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                m_red_envelopes_money_text.setText(s);
            }
        });
        m_red_envelopes_money_edit.setFilters(new InputFilter[]{new MoneyValueFilter()});
    }
}

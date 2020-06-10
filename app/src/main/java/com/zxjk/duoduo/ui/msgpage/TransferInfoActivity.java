package com.zxjk.duoduo.ui.msgpage;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.zxjk.duoduo.Constant;
import com.zxjk.duoduo.R;
import com.zxjk.duoduo.network.Api;
import com.zxjk.duoduo.network.ServiceFactory;
import com.zxjk.duoduo.network.rx.RxSchedulers;
import com.zxjk.duoduo.ui.base.BaseActivity;
import com.zxjk.duoduo.rongIM.message.TransferMessage;
import com.zxjk.duoduo.utils.CommonUtils;

import java.text.SimpleDateFormat;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.rong.imkit.RongIM;
import io.rong.imlib.IRongCallback;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.Message;
import io.rong.imlib.model.UserInfo;

public class TransferInfoActivity extends BaseActivity {

    TextView commitBtn;
    TextView m_transfer_info_money_text;
    TextView m_transfer_info_caveat_text;
    TextView m_transfer_info_pending_payment_text;
    TextView tvZhuanZhangTime;
    TextView tvShouKuanTime;
    TextView m_transfer_info_unit_label;
    ImageView m_transfer_info_icon;
    @BindView(R.id.tv_title)
    TextView tvTitle;

    @SuppressLint("CheckResult")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transfer_info);
        ButterKnife.bind(this);
        tvTitle.setText(getString(R.string.m_successful_transfer_title_bar));

        Intent intent = getIntent();
        UserInfo targetUserInfo = intent.getParcelableExtra("targetUserInfo");
        Message message = intent.getParcelableExtra("msg");
        TransferMessage transferMessage = (TransferMessage) message.getContent();

        commitBtn = findViewById(R.id.m_transfer_info_commit_btn);
        m_transfer_info_money_text = findViewById(R.id.m_transfer_info_money_text);
        m_transfer_info_caveat_text = findViewById(R.id.m_transfer_info_caveat_text);
        m_transfer_info_pending_payment_text = findViewById(R.id.m_transfer_info_pending_payment_text);
        tvZhuanZhangTime = findViewById(R.id.tvZhuanZhangTime);
        tvShouKuanTime = findViewById(R.id.tvShouKuanTime);
        m_transfer_info_unit_label = findViewById(R.id.m_transfer_info_unit_label);
        m_transfer_info_icon = findViewById(R.id.m_transfer_info_icon);
        m_transfer_info_unit_label.setText(transferMessage.getSymbol());

        m_transfer_info_money_text.setText(transferMessage.getMoney());

        commitBtn.setOnClickListener(v -> ServiceFactory.getInstance().getBaseService(Api.class)
                .collect(transferMessage.getTransferId())
                .compose(bindToLifecycle())
                .compose(RxSchedulers.ioObserver(CommonUtils.initDialog(this)))
                .compose(RxSchedulers.normalTrans())
                .subscribe(t -> {
                    //发送消息
                    transferMessage.setExtra("1");
                    Message obtain = Message.obtain(message.getSenderUserId(), Conversation.ConversationType.PRIVATE, transferMessage);
                    obtain.setExtra("1");
                    RongIM.getInstance().sendMessage(obtain, null, null, new IRongCallback.ISendMessageCallback() {
                        @Override
                        public void onAttached(Message message) {
                        }

                        @Override
                        public void onSuccess(Message m) {
                            Constant.tempMsg = message;
                            finish();
                        }

                        @Override
                        public void onError(Message message, RongIMClient.ErrorCode errorCode) {
                        }
                    });
                }, this::handleApiError));

        ServiceFactory.getInstance().getBaseService(Api.class)
                .getTransferInfo(transferMessage.getTransferId())
                .compose(bindToLifecycle())
                .compose(RxSchedulers.ioObserver(CommonUtils.initDialog(this)))
                .compose(RxSchedulers.normalTrans())
                .subscribe(transferResponse -> {
                    boolean fromSelf = transferResponse.getFormCustomerId().equals(Constant.currentUser.getId());
                    String status = transferResponse.getStatus();
                    if (status.equals("0") && fromSelf) {
                        m_transfer_info_pending_payment_text.setText(getString(R.string.waitFor) + targetUserInfo.getName() + getString(R.string.confirmReceive));
                        m_transfer_info_caveat_text.setText(R.string.m_transfer_pending_return_text);
                        tvShouKuanTime.setVisibility(View.GONE);
                        tvZhuanZhangTime.setText(getString(R.string.transTime) + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Long.parseLong(transferResponse.getCreateTime())));
                    } else if (status.equals("0") && !fromSelf) {
                        commitBtn.setVisibility(View.VISIBLE);
                        m_transfer_info_caveat_text.setText(R.string.m_transfer_info_caveat_text);
                    } else if (status.equals("1") && fromSelf) {
                        m_transfer_info_pending_payment_text.setText(targetUserInfo.getName() + getString(R.string.received));
                        m_transfer_info_caveat_text.setText(R.string.receive3);
                        m_transfer_info_icon.setImageResource(R.drawable.icon_transfer_successful);
                        tvZhuanZhangTime.setText(getString(R.string.transTime) + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Long.parseLong(transferResponse.getCreateTime())));
                        tvShouKuanTime.setText(getString(R.string.receiveTransTime) + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Long.parseLong(transferResponse.getCloseTime())));
                    } else if (status.equals("1") && !fromSelf) {
                        m_transfer_info_pending_payment_text.setText(R.string.receive1);
                        m_transfer_info_icon.setImageResource(R.drawable.icon_transfer_successful);
                        tvZhuanZhangTime.setText(getString(R.string.transTime) + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Long.parseLong(transferResponse.getCreateTime())));
                        tvShouKuanTime.setText(getString(R.string.receiveTransTime) + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Long.parseLong(transferResponse.getCloseTime())));
                    } else if (status.equals("2") && fromSelf) {
                        m_transfer_info_icon.setImageResource(R.drawable.ic_zhuanzhanginfo_tuihuan);
                        m_transfer_info_pending_payment_text.setText(R.string.yituihuan);
                        tvZhuanZhangTime.setText(getString(R.string.transTime) + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Long.parseLong(transferResponse.getCreateTime())));
                        tvShouKuanTime.setText(getString(R.string.backTransTime) + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Long.parseLong(transferResponse.getCloseTime())));
                    } else {
                        m_transfer_info_icon.setImageResource(R.drawable.ic_zhuanzhanginfo_guoqi);
                        m_transfer_info_pending_payment_text.setText(R.string.yiguoqi);
                        tvZhuanZhangTime.setText(getString(R.string.transTime) + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Long.parseLong(transferResponse.getCreateTime())));
                        tvShouKuanTime.setText(getString(R.string.backTransTime) + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Long.parseLong(transferResponse.getCloseTime())));
                    }
                }, this::handleApiError);
    }

    @OnClick(R.id.rl_back)
    public void onClick() {
        finish();
    }
}

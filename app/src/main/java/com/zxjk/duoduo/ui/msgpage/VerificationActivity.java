package com.zxjk.duoduo.ui.msgpage;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.blankj.utilcode.util.KeyboardUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.zxjk.duoduo.R;
import com.zxjk.duoduo.network.Api;
import com.zxjk.duoduo.network.ServiceFactory;
import com.zxjk.duoduo.network.rx.RxSchedulers;
import com.zxjk.duoduo.ui.base.BaseActivity;
import com.zxjk.duoduo.utils.CommonUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.rong.imkit.RongIM;
import io.rong.imlib.IRongCallback;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.Message;
import io.rong.message.CommandMessage;


@SuppressLint("CheckResult")
public class VerificationActivity extends BaseActivity {

    @BindView(R.id.m_verification_edit)
    EditText verificationEdit;
    @BindView(R.id.tv_title)
    TextView tvTitle;

    String friendId;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification);
        ButterKnife.bind(this);
        tvTitle.setText(getString(R.string.m_verification_title_bar));
        friendId = getIntent().getStringExtra("friendId");
    }

    public void applyAddFriend(String friendId, String remark) {
        String groupId = getIntent().getStringExtra("groupId");

        ServiceFactory.getInstance().getBaseService(Api.class)
                .applyAddFriend(friendId, remark, TextUtils.isEmpty(groupId) ? "" : groupId)
                .compose(bindToLifecycle())
                .compose(RxSchedulers.normalTrans())
                .compose(RxSchedulers.ioObserver(CommonUtils.initDialog(this)))
                .subscribe(listBaseResponse -> {
                    CommandMessage commandMessage = CommandMessage.obtain("addFriend", "");
                    Message message = Message.obtain(friendId, Conversation.ConversationType.PRIVATE, commandMessage);
                    RongIM.getInstance().sendMessage(message, "", "", (IRongCallback.ISendMessageCallback) null);
                    KeyboardUtils.hideSoftInput(VerificationActivity.this);
                    ToastUtils.showShort(getString(R.string.has_bean_sent));
                    finish();
                }, this::handleApiError);
    }

    @OnClick({R.id.rl_back, R.id.m_verification_icon, R.id.m_verification_send_btn})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.rl_back:
                finish();
                break;
            case R.id.m_verification_icon:
                verificationEdit.setText("");
                break;
            case R.id.m_verification_send_btn:
                applyAddFriend(friendId, verificationEdit.getText().toString());
                break;
        }
    }
}

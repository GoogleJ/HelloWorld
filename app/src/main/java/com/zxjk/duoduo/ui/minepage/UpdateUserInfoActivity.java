package com.zxjk.duoduo.ui.minepage;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.blankj.utilcode.util.GsonUtils;
import com.blankj.utilcode.util.RegexUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.zxjk.duoduo.Constant;
import com.zxjk.duoduo.R;
import com.zxjk.duoduo.bean.response.GroupResponse;
import com.zxjk.duoduo.bean.response.LoginResponse;
import com.zxjk.duoduo.network.Api;
import com.zxjk.duoduo.network.ServiceFactory;
import com.zxjk.duoduo.network.rx.RxSchedulers;
import com.zxjk.duoduo.ui.base.BaseActivity;
import com.zxjk.duoduo.utils.CommonUtils;
import com.zxjk.duoduo.utils.MMKVUtils;

import io.rong.imkit.RongIM;
import io.rong.imlib.IRongCallback;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.Message;
import io.rong.imlib.model.UserInfo;
import io.rong.message.InformationNotificationMessage;

import static android.text.InputType.TYPE_CLASS_TEXT;
import static android.text.InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS;

@SuppressLint("CheckResult")
public class UpdateUserInfoActivity extends BaseActivity {

    private EditText etChangeSign;
    private TextView tvChangeSign;

    private static final int TYPE_SIGN = 1;
    private static final int TYPE_NICK = 2;
    private static final int TYPE_EMAIL = 3;
    private static final int TYPE_GROUP_TITLE = 4;
    private int type;

    private TextView tv_title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_user_info);

        initView();

        etChangeSign = findViewById(R.id.etChangeSign);
        tvChangeSign = findViewById(R.id.tvChangeSign);

        etChangeSign.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                int length = s.toString().length();
                tvChangeSign.setText("" + (20 - length));
            }
        });

        Intent intent = getIntent();
        type = intent.getIntExtra("type", 0);
        if (type == TYPE_SIGN) {
            tv_title.setText(R.string.sign);
            etChangeSign.setHint(R.string.hint_sign);
            if (!TextUtils.isEmpty(Constant.currentUser.getSignature())) {
                etChangeSign.setText(Constant.currentUser.getSignature());
            }
        } else if (type == TYPE_NICK) {
            tv_title.setText(R.string.nick);
            etChangeSign.setHint(R.string.hint_nick);
            if (!TextUtils.isEmpty(Constant.currentUser.getNick())) {
                etChangeSign.setText(Constant.currentUser.getNick());
            }
        } else if (type == TYPE_EMAIL) {
            tv_title.setText(R.string.email);
            etChangeSign.setHint(R.string.hint_email);
            etChangeSign.setInputType(TYPE_CLASS_TEXT | TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
            if (!TextUtils.isEmpty(Constant.currentUser.getEmail())) {
                etChangeSign.setText(Constant.currentUser.getEmail());
            }
        } else if (type == TYPE_GROUP_TITLE) {
            tv_title.setText(R.string.changegroupname);
            etChangeSign.setHint(R.string.hint_groupname);
            GroupResponse group = (GroupResponse) getIntent().getSerializableExtra("group");
            if (group != null) {
                etChangeSign.setText(group.getGroupInfo().getGroupNikeName());
            }
        }
        etChangeSign.setSelection(etChangeSign.getText().toString().length());
    }

    private void initView() {
        tv_title = findViewById(R.id.tv_title);
        TextView tv_commit = findViewById(R.id.tv_commit);
        tv_commit.setVisibility(View.VISIBLE);
        tv_commit.setText(getString(R.string.save));
        findViewById(R.id.rl_back).setOnClickListener(v -> finish());
        tv_commit.setOnClickListener(v -> submit());
    }

    //保存
    public void submit() {
        String sign = etChangeSign.getText().toString().trim();
        if (sign.length() == 0) {
            ToastUtils.showShort(R.string.input_empty);
            return;
        }

        if (type == TYPE_GROUP_TITLE) {
            GroupResponse.GroupInfoBean request = new GroupResponse.GroupInfoBean();
            request.setId(getIntent().getStringExtra("groupId"));
            request.setGroupNikeName(sign);
            ServiceFactory.getInstance().getBaseService(Api.class)
                    .updateGroupInfo(GsonUtils.toJson(request))
                    .compose(bindToLifecycle())
                    .compose(RxSchedulers.ioObserver(CommonUtils.initDialog(UpdateUserInfoActivity.this)))
                    .compose(RxSchedulers.normalTrans())
                    .subscribe(s -> {
                        if (getIntent().getBooleanExtra("fromSocial", false)) {
                            InformationNotificationMessage notificationMessage = InformationNotificationMessage.obtain("社群改名为" + sign);
                            Message message = Message.obtain(getIntent().getStringExtra("groupId"), Conversation.ConversationType.GROUP, notificationMessage);
                            RongIM.getInstance().sendMessage(message, "", "", (IRongCallback.ISendMessageCallback) null);
                        }
                        Intent intent = new Intent();
                        intent.putExtra("result", sign);
                        setResult(2, intent);
                        finish();
                    }, this::handleApiError);
            return;
        }

        LoginResponse update = new LoginResponse(Constant.userId);
        switch (type) {
            case TYPE_EMAIL:
                if (!RegexUtils.isEmail(sign)) {
                    ToastUtils.showShort(R.string.notaemail);
                    return;
                }
                update.setEmail(sign);
                break;
            case TYPE_NICK:
                update.setNick(sign);
                break;
            case TYPE_SIGN:
                update.setSignature(sign);
                break;
            default:
        }
        ServiceFactory.getInstance().getBaseService(Api.class)
                .updateUserInfo(GsonUtils.toJson(update))
                .compose(bindToLifecycle())
                .compose(RxSchedulers.ioObserver(CommonUtils.initDialog(this)))
                .compose(RxSchedulers.normalTrans())
                .subscribe(response -> {
                    switch (type) {
                        case TYPE_EMAIL:
                            Constant.currentUser.setEmail(sign);
                            break;
                        case TYPE_NICK:
                            Constant.currentUser.setNick(sign);
                            UserInfo userInfo = new UserInfo(Constant.userId, sign, Uri.parse(Constant.currentUser.getHeadPortrait()));
                            RongIM.getInstance().refreshUserInfoCache(userInfo);
                            break;
                        case TYPE_SIGN:
                            Constant.currentUser.setSignature(sign);
                            break;
                        default:
                    }
                    MMKVUtils.getInstance().enCode("login", Constant.currentUser);
                    UpdateUserInfoActivity.this.finish();
                }, this::handleApiError);
    }
}

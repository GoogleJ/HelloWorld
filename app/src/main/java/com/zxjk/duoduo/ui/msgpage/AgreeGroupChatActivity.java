package com.zxjk.duoduo.ui.msgpage;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.blankj.utilcode.util.ToastUtils;
import com.zxjk.duoduo.Constant;
import com.zxjk.duoduo.R;
import com.zxjk.duoduo.bean.response.AllGroupMembersResponse;
import com.zxjk.duoduo.bean.response.BaseResponse;
import com.zxjk.duoduo.bean.response.GroupResponse;
import com.zxjk.duoduo.network.Api;
import com.zxjk.duoduo.network.ServiceFactory;
import com.zxjk.duoduo.network.rx.RxSchedulers;
import com.zxjk.duoduo.ui.HomeActivity;
import com.zxjk.duoduo.ui.base.BaseActivity;
import com.zxjk.duoduo.utils.CommonUtils;
import com.zxjk.duoduo.utils.ImageUtil;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import io.reactivex.ObservableSource;
import io.reactivex.functions.Function;
import io.rong.imkit.RongIM;
import io.rong.imlib.IRongCallback;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.Message;
import io.rong.message.InformationNotificationMessage;

public class AgreeGroupChatActivity extends BaseActivity {
    private CircleImageView groupHeader;
    private TextView tvGroupName;
    private TextView joinGroupBtn;

    private String groupName;
    private GroupResponse groupResponse;
    private boolean canJoin;

    @SuppressLint("CheckResult")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agree_group_chat);
        TextView tv_title = findViewById(R.id.tv_title);
        tv_title.setText(getString(R.string.group_invitation));
        findViewById(R.id.rl_back).setOnClickListener(v -> finish());

        groupHeader = findViewById(R.id.group_headers);
        tvGroupName = findViewById(R.id.group_chat_name);
        joinGroupBtn = findViewById(R.id.join_a_group_chat);
        String inviterId = getIntent().getStringExtra("inviterId");
        String groupId = getIntent().getStringExtra("groupId");
        groupName = getIntent().getStringExtra("groupName");

        boolean overtime = getIntent().getBooleanExtra("overtime", false);
        if (overtime) {
            joinGroupBtn.setClickable(false);
            joinGroupBtn.setEnabled(false);
            joinGroupBtn.setText(R.string.hasjoined);
        }

        ServiceFactory.getInstance().getBaseService(Api.class)
                .getGroupByGroupIdForQr(groupId, "invite")
                .compose(bindToLifecycle())
                .compose(RxSchedulers.normalTrans())
                .flatMap((Function<GroupResponse, ObservableSource<BaseResponse<List<AllGroupMembersResponse>>>>)
                        groupResponse -> {
                            this.groupResponse = groupResponse;
                            return ServiceFactory.getInstance().getBaseService(Api.class)
                                    .getGroupMemByGroupId(groupResponse.getGroupInfo().getId());
                        })
                .compose(RxSchedulers.ioObserver(CommonUtils.initDialog(this)))
                .compose(RxSchedulers.normalTrans())
                .subscribe(list -> {
                    if (!TextUtils.isEmpty(groupResponse.getMaxNumber())) {
                        if (list.size() >= Integer.parseInt(groupResponse.getMaxNumber())) {
                            canJoin = true;
                        }
                    }
                    String s = "";
                    StringBuilder stringBuilder = new StringBuilder();
                    for (int i = 0; i < list.size(); i++) {
                        stringBuilder.append(list.get(i).getHeadPortrait() + ",");
                        if (i == list.size() - 1 || i == 8) {
                            s = stringBuilder.substring(0, stringBuilder.length() - 1);
                            break;
                        }
                    }

                    ImageUtil.loadGroupPortrait(groupHeader, s, 80, 2);
                    tvGroupName.setText(getString(R.string.groupname_num, groupName, String.valueOf(list.size())));
                    joinGroupBtn.setOnClickListener(v -> {
                        if (canJoin) {
                            ToastUtils.showShort(getString(R.string.group_max_number));
                            return;
                        }
                        enterGroup(groupId, inviterId, Constant.userId);
                    });
                }, this::handleApiError);
    }

    @SuppressLint("CheckResult")
    private void enterGroup(String groupId, String inviterId, String customerIds) {
        ServiceFactory.getInstance().getBaseService(Api.class)
                .enterGroup(groupId, inviterId, customerIds)
                .compose(bindToLifecycle())
                .compose(RxSchedulers.ioObserver(CommonUtils.initDialog(this)))
                .compose(RxSchedulers.normalTrans())
                .subscribe(s -> {
                    //更新名片
                    int id = getIntent().getIntExtra("id", -1);
                    if (id != -1) {
                        RongIM.getInstance().setMessageExtra(id, "1", null);
                    }

                    //发送进群灰条

                    InformationNotificationMessage notificationMessage = InformationNotificationMessage.obtain(
                            getString(R.string.xxx_join_dissection, Constant.currentUser.getNick())
                    );
                    Message message = Message.obtain(groupId, Conversation.ConversationType.GROUP, notificationMessage);
                    RongIM.getInstance().sendMessage(message, "", "", (IRongCallback.ISendMessageCallback) null);

                    Intent intent = new Intent(AgreeGroupChatActivity.this, HomeActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    RongIM.getInstance().startGroupChat(AgreeGroupChatActivity.this, groupId, groupName);
                }, this::handleApiError);
    }
}

package com.zxjk.moneyspace.ui.msgpage;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.blankj.utilcode.util.ToastUtils;
import com.zxjk.moneyspace.Constant;
import com.zxjk.moneyspace.R;
import com.zxjk.moneyspace.bean.response.AllGroupMembersResponse;
import com.zxjk.moneyspace.bean.response.BaseResponse;
import com.zxjk.moneyspace.bean.response.GroupResponse;
import com.zxjk.moneyspace.network.Api;
import com.zxjk.moneyspace.network.ServiceFactory;
import com.zxjk.moneyspace.network.rx.RxSchedulers;
import com.zxjk.moneyspace.ui.HomeActivity;
import com.zxjk.moneyspace.ui.PayEnterGroupPayActivity;
import com.zxjk.moneyspace.ui.base.BaseActivity;
import com.zxjk.moneyspace.utils.CommonUtils;
import com.zxjk.moneyspace.utils.GlideUtil;

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

    private GroupResponse groupResponse;
    private boolean canJoin;
    private String inviterId;

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

        String groupId = getIntent().getStringExtra("groupId");
        inviterId = getIntent().getStringExtra("id");

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
                    if (null != groupResponse.getGroupPayBean() && groupResponse.getGroupPayBean().getIsOpen().equals("1")) {
                        Intent intent = new Intent(this, PayEnterGroupPayActivity.class);
                        intent.putExtra("groupId", groupId);
                        intent.putExtra("ownerId", groupResponse.getGroupInfo().getGroupOwnerId());
                        intent.putExtra("payMoney", groupResponse.getGroupPayBean().getPayFee());
                        intent.putExtra("groupName", groupResponse.getGroupInfo().getGroupNikeName());
                        startActivity(intent);
                        finish();
                        return;
                    }
                    if (!TextUtils.isEmpty(groupResponse.getMaxNumber())) {
                        if (list.size() >= Integer.parseInt(groupResponse.getMaxNumber())) {
                            canJoin = true;
                        }
                    }

                    GlideUtil.loadCircleImg(groupHeader, groupResponse.getGroupInfo().getHeadPortrait());
                    tvGroupName.setText(getString(R.string.groupname_num, groupResponse.getGroupInfo().getGroupNikeName(), String.valueOf(list.size())));
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
                    //发送进群灰条
                    InformationNotificationMessage notificationMessage = InformationNotificationMessage.obtain(
                            getString(R.string.xxx_join_dissection, Constant.currentUser.getNick())
                    );
                    Message message = Message.obtain(groupId, Conversation.ConversationType.GROUP, notificationMessage);
                    RongIM.getInstance().sendMessage(message, "", "", (IRongCallback.ISendMessageCallback) null);

                    Intent intent = new Intent(this, HomeActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    RongIM.getInstance().startGroupChat(AgreeGroupChatActivity.this, groupId, groupResponse.getGroupInfo().getGroupNikeName());
                }, this::handleApiError);
    }
}

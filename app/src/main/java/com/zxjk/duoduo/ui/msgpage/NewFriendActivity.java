package com.zxjk.duoduo.ui.msgpage;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.blankj.utilcode.util.ToastUtils;
import com.zxjk.duoduo.R;
import com.zxjk.duoduo.bean.response.FriendInfoResponse;
import com.zxjk.duoduo.network.Api;
import com.zxjk.duoduo.network.ServiceFactory;
import com.zxjk.duoduo.network.rx.RxSchedulers;
import com.zxjk.duoduo.ui.base.BaseActivity;
import com.zxjk.duoduo.ui.msgpage.adapter.NewFriendAdapter;
import com.zxjk.duoduo.ui.msgpage.widget.dialog.DeleteFriendInformationDialog;
import com.zxjk.duoduo.utils.CommonUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.rong.imkit.RongIM;
import io.rong.imkit.userInfoCache.RongUserInfoManager;
import io.rong.imlib.IRongCallback;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.Message;
import io.rong.imlib.model.UserInfo;
import io.rong.message.CommandMessage;
import io.rong.message.InformationNotificationMessage;

/**
 * 新的朋友
 */
@SuppressLint("CheckResult")
public class NewFriendActivity extends BaseActivity {
    @BindView(R.id.m_fragment_new_friend_recycler_view)
    RecyclerView mRecyclerView;
    @BindView(R.id.llSearch)
    LinearLayout llSearch;

    NewFriendAdapter mAdapter;
    DeleteFriendInformationDialog dialog;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_friend);
        ButterKnife.bind(this);
        initUI();
    }

    List<FriendInfoResponse> list = new ArrayList<>();

    protected void initUI() {
        TextView tv_title = findViewById(R.id.tv_title);
        tv_title.setText(getString(R.string.new_friend));
        findViewById(R.id.rl_back).setOnClickListener(v -> finish());
        LinearLayoutManager manager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(manager);
        mAdapter = new NewFriendAdapter();
        getMyFriendsWaiting();
        mRecyclerView.setAdapter(mAdapter);

        mAdapter.setOnItemChildClickListener((adapter, view, position) -> {
            FriendInfoResponse item = (FriendInfoResponse) adapter.getData().get(position);
            boolean isTrue = item.getStatus().equals("0");
            switch (view.getId()) {
                case R.id.m_item_new_friend_type_btn:
                    if (isTrue) {
                        addFriend(position, item.getNick(), item);
                    }
                    break;
                case R.id.m_add_btn_layout:
                    if (!isTrue) {
                        Intent intent = new Intent(NewFriendActivity.this, FriendDetailsActivity.class);
                        intent.putExtra("friendId", mAdapter.getData().get(position).getId());
                        startActivity(intent);
                    }
                    break;
                default:
            }
        });

        if (mAdapter.getData().size() == 0) {
            View view = LayoutInflater.from(this).inflate(R.layout.view_app_null_type, null);
            mAdapter.setEmptyView(view);
        }
        llSearch.setOnClickListener(v -> {
            startActivity(new Intent(NewFriendActivity.this, GlobalSearchActivity.class));
            overridePendingTransition(0, 0);
        });

        getPermisson(findViewById(R.id.llPhoneContract), g -> {
            if (!g) return;
            startActivity(new Intent(NewFriendActivity.this, AddPhoneContractActivity.class));
        }, Manifest.permission.READ_CONTACTS);
    }

    /**
     * 获取待添加好友列表
     */
    public void getMyFriendsWaiting() {
        ServiceFactory.getInstance().getBaseService(Api.class)
                .getMyfriendsWaiting()
                .compose(bindToLifecycle())
                .compose(RxSchedulers.ioObserver())
                .compose(RxSchedulers.normalTrans())
                .subscribe(s -> {
                    list.addAll(s);
                    mAdapter.setNewData(s);
                }, this::handleApiError);
    }

    /**
     * 同意添加
     */
    public void addFriend(int position, String markName, FriendInfoResponse item) {
        String friendId = mAdapter.getData().get(position).getId();
        ServiceFactory.getInstance().getBaseService(Api.class)
                .addFriend(mAdapter.getData().get(position).getId(), markName)
                .compose(bindToLifecycle())
                .compose(RxSchedulers.ioObserver(CommonUtils.initDialog(NewFriendActivity.this)))
                .compose(RxSchedulers.normalTrans())
                .subscribe(s -> {
                    item.setStatus("2");
                    mAdapter.notifyItemChanged(position);
                    RongUserInfoManager.getInstance().setUserInfo(new UserInfo(item.getId(), item.getNick(), Uri.parse(item.getHeadPortrait())));
                    ToastUtils.showShort(getString(R.string.add_friend_successful));
                    InformationNotificationMessage message = InformationNotificationMessage.obtain(getString(R.string.new_friend1));
                    RongIM.getInstance().sendDirectionalMessage(Conversation.ConversationType.PRIVATE, friendId, message, new String[]{friendId}, null, null, null);
                    CommandMessage commandMessage = CommandMessage.obtain("agreeFriend", "");
                    Message message1 = Message.obtain(friendId, Conversation.ConversationType.PRIVATE, commandMessage);
                    RongIM.getInstance().sendMessage(message1, "", "", (IRongCallback.ISendMessageCallback) null);
                }, this::handleApiError);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mAdapter.notifyDataSetChanged();
    }
}

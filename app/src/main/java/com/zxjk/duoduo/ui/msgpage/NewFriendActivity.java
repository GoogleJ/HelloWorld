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

import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.blankj.utilcode.util.ToastUtils;
import com.blankj.utilcode.util.Utils;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMWeb;
import com.zxjk.duoduo.Constant;
import com.zxjk.duoduo.R;
import com.zxjk.duoduo.bean.response.FriendInfoResponse;
import com.zxjk.duoduo.network.Api;
import com.zxjk.duoduo.network.ServiceFactory;
import com.zxjk.duoduo.network.rx.RxSchedulers;
import com.zxjk.duoduo.ui.base.BaseActivity;
import com.zxjk.duoduo.ui.minepage.NearByActivity;
import com.zxjk.duoduo.ui.msgpage.adapter.NewFriendAdapter;
import com.zxjk.duoduo.ui.msgpage.widget.dialog.DeleteFriendInformationDialog;
import com.zxjk.duoduo.utils.AesUtil;
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
    private String uri2Code;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_friend);
        ButterKnife.bind(this);

        uri2Code = Constant.APP_SHARE_URL + AesUtil.getInstance().encrypt("id=" + Constant.userId );

        TextView tvContactHilamgId = findViewById(R.id.tvContactHilamgId);
        tvContactHilamgId.setText(getString(R.string.my_hilamg_code, Constant.currentUser.getDuoduoId()));
        tvContactHilamgId.setOnClickListener(v -> startActivity(new Intent(this, MyQrCodeActivity.class)));

        getPermisson(findViewById(R.id.llPhoneNearBy), granted -> {
            if (!granted) {
                return;
            }
            AMapLocationClient mLocationClient = new AMapLocationClient(Utils.getApp());
            AMapLocationClientOption mLocationOption = new AMapLocationClientOption();
            mLocationOption.setLocationPurpose(AMapLocationClientOption.AMapLocationPurpose.SignIn);
            mLocationClient.setLocationOption(mLocationOption);
            mLocationClient.setLocationListener(location -> {
                CommonUtils.destoryDialog();
                if (location.getErrorCode() == 0) {
                    Intent intent = new Intent(this, NearByActivity.class);
                    intent.putExtra("location", location);
                    startActivity(intent);
                } else {
                    ToastUtils.showShort(R.string.getlocation_fail);
                }
            });
            CommonUtils.initDialog(this, getString(R.string.getlocation)).show();
            mLocationClient.startLocation();
        }, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION);

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

        findViewById(R.id.llmay_know).setOnClickListener(v -> {
            ToastUtils.showShort(R.string.developing1);
        });

        findViewById(R.id.llwechat).setOnClickListener(v -> {
            UMWeb link = new UMWeb(uri2Code);
            link.setTitle("我在使用Hilamg聊天");
            link.setDescription("加密私聊、社群管理、数字\n" +
                    "支付尽在Hilamg ，你也来\n" +
                    "试试吧～");
            link.setThumb(new UMImage(this, R.drawable.ic_hilamglogo3));
            new ShareAction(this).withMedia(link).setPlatform(SHARE_MEDIA.WEIXIN).share();
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

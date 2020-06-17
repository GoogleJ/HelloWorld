package com.zxjk.duoduo.ui.msgpage;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityOptionsCompat;

import com.blankj.utilcode.util.ToastUtils;
import com.zxjk.duoduo.R;
import com.zxjk.duoduo.bean.response.FriendInfoResponse;
import com.zxjk.duoduo.network.Api;
import com.zxjk.duoduo.network.ServiceFactory;
import com.zxjk.duoduo.network.rx.RxSchedulers;
import com.zxjk.duoduo.ui.ZoomActivity;
import com.zxjk.duoduo.ui.base.BaseActivity;
import com.zxjk.duoduo.utils.CommonUtils;
import com.zxjk.duoduo.utils.GlideUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.rong.imkit.RongIM;
import io.rong.imkit.userInfoCache.RongUserInfoManager;
import io.rong.imlib.IRongCallback;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.Message;
import io.rong.imlib.model.UserInfo;
import io.rong.message.CommandMessage;
import io.rong.message.InformationNotificationMessage;

@SuppressLint("CheckResult")
public class AddFriendDetailsActivity extends BaseActivity {
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_nickname)
    TextView tvNickname;
    @BindView(R.id.iv_gender)
    ImageView ivGender;
    @BindView(R.id.iv_headPortrait)
    ImageView ivHeadPortrait;
    @BindView(R.id.tv_DuoDuoNumber)
    TextView tvDuoDuoNumber;
    @BindView(R.id.tv_district)
    TextView tvDistrict;
    @BindView(R.id.tv_signature)
    TextView tvSignature;
    @BindView(R.id.tv_addAddressBook)
    TextView tvAddAddressBook;

    private String imageUrl;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_information);

        if (getIntent().getBooleanExtra("isQR", false)) {
            String id = getIntent().getStringExtra("friendId");
            ServiceFactory.getInstance().getBaseService(Api.class)
                    .getFriendInfoById(id)
                    .compose(bindToLifecycle())
                    .compose(RxSchedulers.ioObserver(CommonUtils.initDialog(this)))
                    .compose(RxSchedulers.normalTrans())
                    .subscribe(r -> {
                        if (r.getIsFriend().equals("0")) {
                            Intent intent = new Intent(this, AddFriendDetailsActivity.class);
                            intent.putExtra("friendId", id);
                            intent.putExtra("type", "1");
                            startActivity(intent);
                        } else {
                            Intent intent = new Intent(this, FriendDetailsActivity.class);
                            intent.putExtra("friendId", id);
                            startActivity(intent);
                        }
                        finish();
                    }, t -> {
                        handleApiError(t);
                        finish();
                    });
        } else {
            init();
        }
    }

    private void init() {
        ButterKnife.bind(this);

        tvTitle.setText(R.string.personal_details);

        //type为必传项!!
        if ("0".equals(getIntent().getStringExtra("type"))) {
            tvAddAddressBook.setText(R.string.m_personal_information_signature_text);
        } else {
            tvAddAddressBook.setText(R.string.add_to_contact);
        }

        ServiceFactory.getInstance().getBaseService(Api.class)
                .getCustomerInfoById(getIntent().getStringExtra("friendId"))
                .compose(bindToLifecycle())
                .compose(RxSchedulers.normalTrans())
                .compose(RxSchedulers.ioObserver(CommonUtils.initDialog(this)))
                .subscribe(r -> {
                    tvNickname.setText(r.getNick());
                    tvDuoDuoNumber.setText(getString(R.string.duoduo_acount) + " " + r.getDuoduoId());
                    tvDistrict.setText(getString(R.string.district) + " " + r.getAddress());
                    tvSignature.setText(TextUtils.isEmpty(r.getSignature()) ? getString(R.string.none) : r.getSignature());
                    imageUrl = r.getHeadPortrait();
                    GlideUtil.loadCircleImg(ivHeadPortrait, r.getHeadPortrait());

                    if ("0".equals(r.getSex())) {
                        ivGender.setImageDrawable(getDrawable(R.drawable.icon_gender_man));
                    } else {
                        ivGender.setImageDrawable(getDrawable(R.drawable.icon_gender_woman));
                    }

                    RongIM.getInstance().refreshUserInfoCache(new UserInfo(r.getId(), r.getNick(), Uri.parse(r.getHeadPortrait())));
                }, this::handleApiError);
    }

    @OnClick({R.id.rl_back, R.id.tv_addAddressBook, R.id.iv_headPortrait})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.rl_back:
                finish();
                break;
            case R.id.tv_addAddressBook:
                if (getIntent().getStringExtra("type").equals("1")) {
                    Intent intent = new Intent(this, VerificationActivity.class);
                    intent.putExtra("friendId", getIntent().getStringExtra("friendId"));
                    intent.putExtra("groupId", getIntent().getStringExtra("groupId"));
                    startActivity(intent);
                } else {
                    addFriend(getIntent().getStringExtra("nick"), (FriendInfoResponse) getIntent().getSerializableExtra("item"));
                }

                break;
            case R.id.iv_headPortrait:
                Intent intent5 = new Intent(this, ZoomActivity.class);
                intent5.putExtra("image", imageUrl);
                startActivity(intent5,
                        ActivityOptionsCompat.makeSceneTransitionAnimation(this,
                                ivHeadPortrait, "img").toBundle());
                break;
        }
    }

    public void addFriend(String markName, FriendInfoResponse item) {
        ServiceFactory.getInstance().getBaseService(Api.class)
                .addFriend(item.getId(), markName)
                .compose(bindToLifecycle())
                .compose(RxSchedulers.ioObserver(CommonUtils.initDialog(this)))
                .compose(RxSchedulers.normalTrans())
                .subscribe(s -> {
                    item.setStatus("2");
                    RongUserInfoManager.getInstance().setUserInfo(new UserInfo(item.getId(), item.getNick(), Uri.parse(item.getHeadPortrait())));
                    ToastUtils.showShort(getString(R.string.add_friend_successful));
                    InformationNotificationMessage message = InformationNotificationMessage.obtain(getString(R.string.new_friend1));
                    RongIM.getInstance().sendDirectionalMessage(Conversation.ConversationType.PRIVATE, item.getId(), message, new String[]{item.getId()}, null, null, null);
                    CommandMessage commandMessage = CommandMessage.obtain("agreeFriend", "");
                    Message message1 = Message.obtain(item.getId(), Conversation.ConversationType.PRIVATE, commandMessage);
                    RongIM.getInstance().sendMessage(message1, "", "", (IRongCallback.ISendMessageCallback) null);
                    finish();
                }, this::handleApiError);
    }
}
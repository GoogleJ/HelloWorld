package com.zxjk.duoduo.ui.msgpage;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityOptionsCompat;

import com.blankj.utilcode.util.ScreenUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.shehuan.nicedialog.BaseNiceDialog;
import com.shehuan.nicedialog.NiceDialog;
import com.shehuan.nicedialog.ViewConvertListener;
import com.shehuan.nicedialog.ViewHolder;
import com.zxjk.duoduo.Constant;
import com.zxjk.duoduo.R;
import com.zxjk.duoduo.bean.response.FriendInfoResponse;
import com.zxjk.duoduo.network.Api;
import com.zxjk.duoduo.network.ServiceFactory;
import com.zxjk.duoduo.network.rx.RxSchedulers;
import com.zxjk.duoduo.ui.HomeActivity;
import com.zxjk.duoduo.ui.ZoomActivity;
import com.zxjk.duoduo.ui.base.BaseActivity;
import com.zxjk.duoduo.ui.msgpage.widget.CommonPopupWindow;
import com.zxjk.duoduo.ui.msgpage.widget.dialog.DeleteFriendInformationDialog;
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
import razerdp.basepopup.QuickPopupBuilder;
import razerdp.basepopup.QuickPopupConfig;
import razerdp.widget.QuickPopup;

@SuppressLint("CheckResult")
public class FriendDetailsActivity extends BaseActivity implements View.OnClickListener, CommonPopupWindow.ViewInterface {

    @BindView(R.id.tv_nickname)
    TextView tvNickname;
    @BindView(R.id.tv_realName)
    TextView tvRealName;
    @BindView(R.id.tv_DuoDuoNumber)
    TextView tvDuoDuoNumber;
    @BindView(R.id.iv_gender)
    ImageView ivGender;
    @BindView(R.id.iv_headPortrait)
    ImageView ivHeadPortrait;
    @BindView(R.id.tv_district)
    TextView tvDistrict;
    @BindView(R.id.tv_phoneNumber)
    TextView tvPhoneNumber;
    @BindView(R.id.tv_email)
    TextView tvEmail;
    @BindView(R.id.tv_signature)
    TextView tvSignature;
    @BindView(R.id.llRemark)
    LinearLayout llRemark;

    String imageUrl;
    String sex = "0";
    DeleteFriendInformationDialog dialog;
    FriendInfoResponse friendInfoResponse;
    private RelativeLayout rl_end;
    private QuickPopup menuPop;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_people_information);

        initUI();

        initFriendIntent();
    }

    private void initFriendIntent() {
        String friendId = getIntent().getStringExtra("friendId");
        if (TextUtils.isEmpty(friendId)) {
            finish();
            return;
        }

        ServiceFactory.getInstance().getBaseService(Api.class)
                .getFriendInfoById(friendId)
                .compose(bindToLifecycle())
                .compose(RxSchedulers.ioObserver(CommonUtils.initDialog(this)))
                .compose(RxSchedulers.normalTrans())
                .subscribe(r -> {
                    friendInfoResponse = r;

                    if (!friendId.equals(Constant.userId)) {
                        RongIM.getInstance().refreshUserInfoCache(new UserInfo(friendId, TextUtils.isEmpty(r.getRemark()) ?
                                r.getNick() : r.getRemark(), Uri.parse(r.getHeadPortrait())));
                    }

                    handleData();
                }, this::handleApiError);
    }

    @SuppressLint("SetTextI18n")
    private void handleData() {
        imageUrl = friendInfoResponse.getHeadPortrait();
        GlideUtil.loadCircleImg(ivHeadPortrait, friendInfoResponse.getHeadPortrait());
        tvDuoDuoNumber.setText(getString(R.string.duoduo_acount) + friendInfoResponse.getDuoduoId());
        tvNickname.setText(TextUtils.isEmpty(friendInfoResponse.getRemark()) ? friendInfoResponse.getNick() : friendInfoResponse.getRemark());
        tvRealName.setText(getString(R.string.nick1, friendInfoResponse.getNick()));
        tvDistrict.setText(getString(R.string.district) + friendInfoResponse.getAddress());

        String mobile = friendInfoResponse.getMobile();
        tvPhoneNumber.setText(mobile.substring(0, 3) + "****" + mobile.substring(7));
        tvEmail.setText(TextUtils.isEmpty(friendInfoResponse.getEmail()) ? "暂无" : friendInfoResponse.getEmail());
        tvSignature.setText(TextUtils.isEmpty(friendInfoResponse.getSignature()) ? "暂无" : friendInfoResponse.getSignature());
        if (sex.equals(friendInfoResponse.getSex())) {
            ivGender.setImageDrawable(getDrawable(R.drawable.icon_gender_man));
        } else {
            ivGender.setImageDrawable(getDrawable(R.drawable.icon_gender_woman));
        }

        if (friendInfoResponse.getId().equals(Constant.userId)) {
            rl_end.setVisibility(View.INVISIBLE);
            llRemark.setVisibility(View.GONE);
            tvRealName.setVisibility(View.GONE);
        }
        if (TextUtils.isEmpty(friendInfoResponse.getRemark())) {
            tvRealName.setVisibility(View.GONE);
        }
    }

    private void initUI() {
        ButterKnife.bind(this);
        TextView tv_title = findViewById(R.id.tv_title);
        tv_title.setText(getString(R.string.personal_details));
        findViewById(R.id.rl_back).setOnClickListener(v -> finish());
        rl_end = findViewById(R.id.rl_end);
        rl_end.setVisibility(View.VISIBLE);
        rl_end.setOnClickListener(v -> {
            if (menuPop == null) {
                menuPop = QuickPopupBuilder.with(this)
                        .contentView(R.layout.popup_window_people_information)
                        .config(new QuickPopupConfig()
                                .backgroundColor(android.R.color.transparent)
                                .gravity(Gravity.BOTTOM | Gravity.END)
                                .withShowAnimation(AnimationUtils.loadAnimation(this, R.anim.push_scale_in))
                                .withDismissAnimation(AnimationUtils.loadAnimation(this, R.anim.push_scale_out))
                                .withClick(R.id.update_rename, child -> {
                                    Intent intent1 = new Intent(FriendDetailsActivity.this, ModifyNotesActivity.class);
                                    intent1.putExtra("friendId", friendInfoResponse.getId());
                                    intent1.putExtra("name", RongUserInfoManager.getInstance().getUserInfo(friendInfoResponse.getId()).getName());
                                    intent1.putExtra("nick", friendInfoResponse.getNick());
                                    intent1.putExtra("headPortrait", friendInfoResponse.getHeadPortrait());
                                    startActivityForResult(intent1, 1);
                                }, true)
                                .withClick(R.id.recommend_to_friend, child -> {
                                    Intent intentCard = new Intent(FriendDetailsActivity.this, SelectContactForCardActivity.class);
                                    intentCard.putExtra("userId", friendInfoResponse.getId());
                                    intentCard.putExtra("nick", friendInfoResponse.getNick());
                                    intentCard.putExtra("headPortrait", friendInfoResponse.getHeadPortrait());
                                    intentCard.putExtra("duoduoId", getString(R.string.duoduo_acount) + friendInfoResponse.getDuoduoId());
                                    startActivity(intentCard);
                                }, true)
                                .withClick(R.id.delete_friend, child -> NiceDialog.init().setLayoutId(R.layout.layout_general_dialog).setConvertListener(new ViewConvertListener() {
                                    @Override
                                    protected void convertView(ViewHolder holder, BaseNiceDialog dialog) {
                                        holder.setText(R.id.tv_title, R.string.delete_contact);
                                        TextView textView = holder.getView(R.id.tv_content);
                                        textView.setText(String.format(getResources().getString(R.string.m_delete_friend_label), friendInfoResponse.getNick()));
                                        holder.setText(R.id.tv_cancel, R.string.cancel);
                                        holder.setText(R.id.tv_notarize, R.string.delete);
                                        holder.setOnClickListener(R.id.tv_cancel, v1 -> dialog.dismiss());
                                        holder.setOnClickListener(R.id.tv_notarize, v12 -> deleteFriend(friendInfoResponse.getId(), dialog));
                                    }
                                }).setDimAmount(0.5f).setOutCancel(false).show(getSupportFragmentManager()), true))
                        .build();
            }

            menuPop.showPopupWindow(v);
        });

        tvNickname.setMaxWidth(ScreenUtils.getAppScreenWidth() - CommonUtils.dip2px(this, 165));

        llRemark.setOnClickListener(v -> {
            Intent intent1 = new Intent(FriendDetailsActivity.this, ModifyNotesActivity.class);
            intent1.putExtra("friendId", friendInfoResponse.getId());
            intent1.putExtra("name", RongUserInfoManager.getInstance().getUserInfo(friendInfoResponse.getId()).getName());
            intent1.putExtra("nick", friendInfoResponse.getNick());
            intent1.putExtra("headPortrait", friendInfoResponse.getHeadPortrait());
            startActivityForResult(intent1, 1);
        });
    }

    @OnClick({R.id.tv_sendMessage, R.id.iv_headPortrait})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_headPortrait:
                Intent intent5 = new Intent(this, ZoomActivity.class);
                intent5.putExtra("image", imageUrl);
                startActivity(intent5,
                        ActivityOptionsCompat.makeSceneTransitionAnimation(this,
                                ivHeadPortrait, "img").toBundle());
                break;
            case R.id.tv_sendMessage:
                Intent intent = new Intent(this, HomeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                RongIM.getInstance().startPrivateChat(this, friendInfoResponse.getId(), friendInfoResponse.getNick());
                break;
        }
    }

    @Override
    public void getChildView(View view, int layoutResId) {
        if (layoutResId == R.layout.popup_window_people_information) {
            view.findViewById(R.id.update_rename).setOnClickListener(this);
            view.findViewById(R.id.recommend_to_friend).setOnClickListener(this);
            view.findViewById(R.id.delete_friend).setOnClickListener(this);
        }
    }

    private void deleteFriend(String friendId, BaseNiceDialog dialog) {
        ServiceFactory.getInstance().getBaseService(Api.class)
                .deleteFriend(friendId)
                .compose(RxSchedulers.ioObserver())
                .compose(RxSchedulers.normalTrans())
                .compose(bindToLifecycle())
                .subscribe(s -> {
                    dialog.dismiss();
                    ToastUtils.showShort(getString(R.string.the_friend_has_been_deleted));
                    RongIM.getInstance().clearMessages(Conversation.ConversationType.PRIVATE,
                            friendId, null);
                    RongIM.getInstance().removeConversation(Conversation.ConversationType.PRIVATE
                            , friendId, null);
                    CommandMessage commandMessage = CommandMessage.obtain("deleteFriend", "");
                    Message deleteFriend = Message.obtain(friendId, Conversation.ConversationType.PRIVATE, commandMessage);
                    RongIM.getInstance().sendMessage(deleteFriend, "", "", (IRongCallback.ISendMessageCallback) null);
                    Intent intent = new Intent(FriendDetailsActivity.this, HomeActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }, this::handleApiError);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == 1) {
            String remark = data.getStringExtra("remark");
            if (TextUtils.isEmpty(remark)) {
                tvRealName.setVisibility(View.GONE);
                tvNickname.setText(friendInfoResponse.getNick());
            } else {
                tvRealName.setVisibility(View.GONE);
                tvNickname.setText(remark);
            }
        }
    }

    @Override
    public void finish() {
        Intent intent = new Intent();
        intent.putExtra("remark", tvNickname.getText().toString());
        setResult(1000, intent);
        super.finish();
    }
}

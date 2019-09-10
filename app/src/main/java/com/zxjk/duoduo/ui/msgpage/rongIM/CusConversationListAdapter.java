package com.zxjk.duoduo.ui.msgpage.rongIM;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Context;
import android.content.res.Resources;
import android.net.Uri;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.zxjk.duoduo.R;
import com.zxjk.duoduo.network.Api;
import com.zxjk.duoduo.network.ServiceFactory;
import com.zxjk.duoduo.network.rx.RxSchedulers;
import com.zxjk.duoduo.utils.ImageUtil;

import io.rong.imkit.model.UIConversation;
import io.rong.imkit.userInfoCache.RongUserInfoManager;
import io.rong.imkit.widget.adapter.ConversationListAdapter;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.Group;
import io.rong.imlib.model.UserInfo;

public class CusConversationListAdapter extends ConversationListAdapter {

    public CusConversationListAdapter(Context context) {
        super(context);
    }

    @Override
    protected View newView(Context context, int position, ViewGroup group) {
        return super.newView(context, position, group);
    }

    @SuppressLint("CheckResult")
    @Override
    protected void bindView(View v, int position, UIConversation data) {
        super.bindView(v, position, data);
        v.findViewById(R.id.rc_left).setVisibility(View.GONE);
        ImageView groupHead = v.findViewById(R.id.rc_mask);

//        if (data.getConversationTargetId().equals("147")) {
//            groupHead.setVisibility(View.VISIBLE);
//            groupHead.setImageResource(R.drawable.ic_portrait_payment);
//            UserInfo u = RongUserInfoManager.getInstance().getUserInfo(data.getConversationTargetId());
//            if (u == null) {
//                RongUserInfoManager.getInstance().setUserInfo(new UserInfo(data.getConversationTargetId(), "支付凭证",
//                        getUriFromDrawableRes(v.getContext(), R.drawable.ic_portrait_payment)));
//            }
//            return;
//        } else if (data.getConversationTargetId().equals("349")) {
//            groupHead.setVisibility(View.VISIBLE);
//            groupHead.setImageResource(R.drawable.ic_portrait_notice);
//            UserInfo u = RongUserInfoManager.getInstance().getUserInfo(data.getConversationTargetId());
//            if (u == null) {
//                RongUserInfoManager.getInstance().setUserInfo(new UserInfo(data.getConversationTargetId(), "对局结果",
//                        getUriFromDrawableRes(v.getContext(), R.drawable.ic_portrait_notice)));
//            }
//            return;
//        } else if (data.getConversationTargetId().equals("355")) {
//            groupHead.setVisibility(View.VISIBLE);
//            groupHead.setImageResource(R.drawable.ic_portrait_system);
//            UserInfo u = RongUserInfoManager.getInstance().getUserInfo(data.getConversationTargetId());
//            if (u == null) {
//                RongUserInfoManager.getInstance().setUserInfo(new UserInfo(data.getConversationTargetId(), "多多官方",
//                        getUriFromDrawableRes(v.getContext(), R.drawable.ic_portrait_system)));
//            }
//            return;
//        }

        if (data.getConversationType() != Conversation.ConversationType.GROUP) {
            v.findViewById(R.id.rc_left).setVisibility(View.VISIBLE);
            groupHead.setVisibility(View.GONE);
            return;
        }

        groupHead.setVisibility(View.VISIBLE);

        Group groupInfo = RongUserInfoManager.getInstance().getGroupInfo(data.getConversationTargetId());
        if (groupInfo != null && !TextUtils.isEmpty(groupInfo.getPortraitUri().toString())) {
            ImageUtil.loadGroupPortrait(groupHead, groupInfo.getPortraitUri().toString());
        } else {
            ServiceFactory.getInstance().getBaseService(Api.class)
                    .getGroupByGroupId(data.getConversationTargetId())
                    .compose(RxSchedulers.normalTrans())
                    .compose(RxSchedulers.ioObserver())
                    .subscribe(r -> {
                        String s = "";

                        StringBuilder stringBuilder = new StringBuilder();
                        for (int i = 0; i < r.getCustomers().size(); i++) {
                            stringBuilder.append(r.getCustomers().get(i).getHeadPortrait() + ",");
                            if (i == r.getCustomers().size() - 1 || i == 8) {
                                s = stringBuilder.substring(0, stringBuilder.length() - 1);
                                break;
                            }
                        }

                        Group group = new Group(r.getGroupInfo().getId(), r.getGroupInfo().getGroupNikeName(), Uri.parse(s));
                        RongUserInfoManager.getInstance().setGroupInfo(group);

                        ImageUtil.loadGroupPortrait(groupHead, s);
                    }, t -> {
                    });
        }
    }

    private Uri getUriFromDrawableRes(Context context, int id) {
        Resources resources = context.getResources();
        String path = ContentResolver.SCHEME_ANDROID_RESOURCE + "://"
                + resources.getResourcePackageName(id) + "/"
                + resources.getResourceTypeName(id) + "/"
                + resources.getResourceEntryName(id);
        return Uri.parse(path);
    }
}

package com.zxjk.duoduo.ui.msgpage;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.zxjk.duoduo.Constant;
import com.zxjk.duoduo.R;
import com.zxjk.duoduo.ui.base.BaseFragment;
import com.zxjk.duoduo.utils.GlideUtil;

import java.util.List;

import io.rong.imlib.model.Conversation;

public class MsgFragment extends BaseFragment {

    private ImageView ivScan;
    private ImageView ivHead;
    private CusConversationListFragment listFragment;

    private OnHeadClick onHeadClick;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_message, container, false);

        initView();

        createConversationList();

        return rootView;
    }

    private void initView() {
        List<Fragment> fragments = getChildFragmentManager().getFragments();
        listFragment = (CusConversationListFragment) fragments.get(0);
        ivHead = rootView.findViewById(R.id.ivHead);
        ivScan = rootView.findViewById(R.id.ivScan);

        GlideUtil.loadCircleImg(ivHead, Constant.currentUser.getHeadPortrait());

        getPermisson(ivScan, granted -> {
            if (granted) startActivity(new Intent(getActivity(), QrCodeActivity.class));
        }, Manifest.permission.CAMERA);

        ivHead.setOnClickListener(v -> {
            if (onHeadClick != null) {
                onHeadClick.onClick();
            }
        });
    }

    private void createConversationList() {
        Uri uri = Uri.parse("rong://" + getActivity().getApplicationInfo().packageName).buildUpon()
                .appendPath("conversationlist")
                .appendQueryParameter(Conversation.ConversationType.PRIVATE.getName(), "false")
                .appendQueryParameter(Conversation.ConversationType.GROUP.getName(), "false")
                //公共服务号
                .appendQueryParameter(Conversation.ConversationType.PUBLIC_SERVICE.getName(), "true")
                //订阅号
                .appendQueryParameter(Conversation.ConversationType.APP_PUBLIC_SERVICE.getName(), "true")
                //系统消息
                .appendQueryParameter(Conversation.ConversationType.SYSTEM.getName(), "false")
                .build();
        listFragment.setUri(uri);
    }

    public interface OnHeadClick {
        void onClick();
    }

    public void setOnHeadClick(OnHeadClick onHeadClick) {
        this.onHeadClick = onHeadClick;
    }

}

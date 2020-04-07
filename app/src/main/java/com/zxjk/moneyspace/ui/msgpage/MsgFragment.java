package com.zxjk.moneyspace.ui.msgpage;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.zxjk.moneyspace.ui.msgpage.SearchGroupActivity;
import com.zxjk.moneyspace.R;
import com.zxjk.moneyspace.ui.base.BaseFragment;
import com.zxjk.moneyspace.ui.walletpage.RecipetQRActivity;
import com.zxjk.moneyspace.ui.widget.ImagePagerIndicator;
import com.zxjk.moneyspace.ui.widget.MsgTitleView;

import net.lucode.hackware.magicindicator.MagicIndicator;
import net.lucode.hackware.magicindicator.ViewPagerHelper;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView;

import io.rong.imlib.model.Conversation;
import razerdp.basepopup.QuickPopupBuilder;
import razerdp.basepopup.QuickPopupConfig;
import razerdp.widget.QuickPopup;

public class MsgFragment extends BaseFragment implements View.OnClickListener {

    private ImageView ivMenu;
    private int currentPosition;
    private ViewPager pagerMsg;
    private int[] mTitleDataList = new int[]{R.string.chat};
    private QuickPopup menuPop;
    private MsgTitleView[] badgeTitleViews = new MsgTitleView[1];

    public MsgTitleView[] getBadgeTitleViews() {
        return badgeTitleViews;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_message, container, false);

        initView();

        return rootView;
    }

    private void initView() {
        ivMenu = rootView.findViewById(R.id.ivMenu);
        ivMenu.setOnClickListener(this);

        initPager();

    }

    private void initPager() {
        pagerMsg = rootView.findViewById(R.id.pagerMsg);
        pagerMsg.setAdapter(new FragmentPagerAdapter(getChildFragmentManager(), FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
            @NonNull
            @Override
            public Fragment getItem(int position) {
                return createConversationList();
            }

            @Override
            public int getCount() {
                return 1;
            }
        });
        pagerMsg.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                currentPosition = position;
                if (position == 0) {
                    ivMenu.setImageResource(R.drawable.ic_msg_new);
                } else {
                    ivMenu.setImageResource(R.drawable.ic_msg_search);
                }
            }
        });
    }

    private Fragment createConversationList() {
        CusConversationListFragment listFragment = new CusConversationListFragment();
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
        return listFragment;
    }

    @Override
    public void onClick(View v) {
        if (currentPosition == 0) {
            if (menuPop == null) {
                menuPop = QuickPopupBuilder.with(getContext())
                        .contentView(R.layout.pop_msg_top)
                        .config(new QuickPopupConfig()
                                .backgroundColor(android.R.color.transparent)
                                .gravity(Gravity.BOTTOM | Gravity.END)
                                .withShowAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.push_scale_in))
                                .withDismissAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.push_scale_out))
                                .withClick(R.id.send_group_chat, child -> {
                                    Intent intent = new Intent(getActivity(), CreateGroupActivity.class);
                                    intent.putExtra("eventType", 1);
                                    startActivity(intent);
                                }, true)
                                .withClick(R.id.invite_friends, child -> startActivity(new Intent(getActivity(), NewFriendActivity.class)), true)
                                .withClick(R.id.collection_and_payment, child -> startActivity(new Intent(getActivity(), RecipetQRActivity.class)), true))
                        .build();

                getPermisson(menuPop.findViewById(R.id.scan), granted -> {
                    menuPop.dismiss();
                    if (granted) startActivity(new Intent(getActivity(), QrCodeActivity.class));
                }, Manifest.permission.CAMERA);
            }

            menuPop.showPopupWindow(v);
        } else {
            startActivity(new Intent(getContext(), SearchGroupActivity.class));
            getActivity().overridePendingTransition(0, 0);
        }
    }

    public void msgFragmentSelect(){
        pagerMsg.setCurrentItem(1);
    }

}
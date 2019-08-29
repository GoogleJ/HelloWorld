package com.zxjk.duoduo.ui.msgpage;

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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.zxjk.duoduo.R;
import com.zxjk.duoduo.ui.base.BaseFragment;
import com.zxjk.duoduo.ui.walletpage.RecipetQRActivity;
import com.zxjk.duoduo.ui.widget.ImagePagerIndicator;
import com.zxjk.duoduo.ui.widget.MsgTitleView;
import com.zxjk.duoduo.utils.CommonUtils;

import net.lucode.hackware.magicindicator.MagicIndicator;
import net.lucode.hackware.magicindicator.ViewPagerHelper;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.LinePagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.badge.BadgeAnchor;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.badge.BadgePagerTitleView;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.badge.BadgeRule;

import io.rong.imlib.model.Conversation;
import razerdp.basepopup.QuickPopupBuilder;
import razerdp.basepopup.QuickPopupConfig;
import razerdp.widget.QuickPopup;

import static net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.LinePagerIndicator.MODE_WRAP_CONTENT;

public class MsgFragment extends BaseFragment implements View.OnClickListener {

    private ViewPager pagerMsg;
    private MagicIndicator indicator;
    private int[] mTitleDataList = new int[]{R.string.chat, R.string.de_actionbar_sub_group};
    private QuickPopup menuPop;
    private MsgTitleView[] badgeTitleViews = new MsgTitleView[2];

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
        rootView.findViewById(R.id.ivMenu).setOnClickListener(this);

        initPager();

        initIndicator();

        ViewPagerHelper.bind(indicator, pagerMsg);
    }

    private void initPager() {
        pagerMsg = rootView.findViewById(R.id.pagerMsg);
        pagerMsg.setAdapter(new FragmentPagerAdapter(getChildFragmentManager(), FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
            @NonNull
            @Override
            public Fragment getItem(int position) {
                return position == 0 ? createConversationList(false) : createConversationList(true);
            }

            @Override
            public int getCount() {
                return 2;
            }
        });
    }

    private void initIndicator() {
        indicator = rootView.findViewById(R.id.indicator);
        CommonNavigator navigator = new CommonNavigator(getContext());
        navigator.setAdapter(new CommonNavigatorAdapter() {
            @Override
            public int getCount() {
                return mTitleDataList == null ? 0 : mTitleDataList.length;
            }

            @Override
            public IPagerTitleView getTitleView(Context context, final int index) {
                createTitleView(context, index);
                return badgeTitleViews[index];
            }

            private void createTitleView(Context context, int index) {
                MsgTitleView badgeTitleView = new MsgTitleView(context, R.layout.titleview_msgtitle);
                badgeTitleView.setOnClickListener(view -> {
                    pagerMsg.setCurrentItem(index);
                    badgeTitleView.getBadgeView().setVisibility(View.INVISIBLE);
                });
                badgeTitleView.getTitleView().setText(mTitleDataList[index]);
                badgeTitleViews[index] = badgeTitleView;
            }

            @Override
            public IPagerIndicator getIndicator(Context context) {
                return new ImagePagerIndicator(context);
            }
        });

        indicator.setNavigator(navigator);
    }

    private CusConversationListFragment createConversationList(boolean isGroup) {
        CusConversationListFragment listFragment = new CusConversationListFragment();
        Uri uri = Uri.parse("rong://" + getActivity().getApplicationInfo().packageName).buildUpon()
                .appendPath("conversationlist")
                .appendQueryParameter(isGroup ? Conversation.ConversationType.GROUP.getName() :
                        Conversation.ConversationType.PRIVATE.getName(), "false")
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
        switch (v.getId()) {
            case R.id.ivMenu:
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
                                    .withClick(R.id.invite_friends, child -> startActivity(new Intent(getActivity(), AddContactActivity.class)), true)
                                    .withClick(R.id.collection_and_payment, child -> startActivity(new Intent(getActivity(), RecipetQRActivity.class)), true))
                            .build();

                    getPermisson(menuPop.findViewById(R.id.scan), granted -> {
                        menuPop.dismiss();
                        if (granted) startActivity(new Intent(getActivity(), QrCodeActivity.class));
                    }, Manifest.permission.CAMERA);
                }

                menuPop.showPopupWindow(v);
                break;
            default:
        }
    }
}

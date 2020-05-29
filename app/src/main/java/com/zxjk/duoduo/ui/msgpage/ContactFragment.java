package com.zxjk.duoduo.ui.msgpage;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.blankj.utilcode.util.BarUtils;
import com.zxjk.duoduo.Constant;
import com.zxjk.duoduo.R;
import com.zxjk.duoduo.bean.response.FriendInfoResponse;
import com.zxjk.duoduo.ui.HomeActivity;
import com.zxjk.duoduo.ui.base.BaseFragment;
import com.zxjk.duoduo.ui.minepage.InviterActivity;
import com.zxjk.duoduo.utils.MMKVUtils;

import net.lucode.hackware.magicindicator.MagicIndicator;
import net.lucode.hackware.magicindicator.ViewPagerHelper;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.LinePagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.SimplePagerTitleView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ContactFragment extends BaseFragment {
    @BindView(R.id.layout_contract_head)
    View layout_contract_head;
    @BindView(R.id.llSearch)
    LinearLayout llSearch;
    List<FriendInfoResponse> list = new ArrayList<>();
    private MagicIndicator indicator;
    private ViewPager pager;
    private int[] mTitleDataList = new int[]{R.string.friend, R.string.social};
    private View dotNewFriend;
    private View headView;

    public View getDotNewFriend() {
        return dotNewFriend;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        rootView = LayoutInflater.from(getContext()).inflate(R.layout.activity_constacts_new_friend, container, false);

        View topmask = rootView.findViewById(R.id.topmask);
        ViewGroup.LayoutParams layoutParams = topmask.getLayoutParams();
        layoutParams.height = BarUtils.getStatusBarHeight();
        topmask.setLayoutParams(layoutParams);

        TextView tvContactHilamgId = rootView.findViewById(R.id.tvContactHilamgId);

        indicator = rootView.findViewById(R.id.indicator);

        pager = rootView.findViewById(R.id.pager);


        initIndicator();

        initPager();

        ViewPagerHelper.bind(indicator, pager);

        tvContactHilamgId.setText(getString(R.string.my_hilamg_code, Constant.currentUser.getDuoduoId()));
        tvContactHilamgId.setOnClickListener(v -> startActivity(new Intent(getActivity(), MyQrCodeActivity.class)));

        rootView.findViewById(R.id.ll_contract_top1).setOnClickListener(v -> {
            ((HomeActivity) getActivity()).badgeItem2.hide();
            MMKVUtils.getInstance().enCode("newFriendCount", 0);
            dotNewFriend.setVisibility(View.INVISIBLE);
            startActivity(new Intent(getActivity(), NewFriendActivity.class));
        });

        getPermisson(rootView.findViewById(R.id.ll_contract_top2), granted -> {
            if (granted) startActivity(new Intent(getActivity(), QrCodeActivity.class));
        }, Manifest.permission.CAMERA);

        rootView.findViewById(R.id.ll_contract_top3).setOnClickListener(v -> startActivity(new Intent(getActivity(), GroupChatActivity.class)));

        rootView.findViewById(R.id.ll_contract_top4).setOnClickListener(v -> startActivity(new Intent(getActivity(), InviterActivity.class)));

        ButterKnife.bind(this, rootView);

        llSearch.setOnClickListener(v -> {
            startActivity(new Intent(getContext(), GlobalSearchActivity.class));
            getActivity().overridePendingTransition(0, 0);
        });

        return rootView;
    }

    private void initPager() {

        pager.setAdapter(new FragmentPagerAdapter(getChildFragmentManager(), FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
            @NonNull
            @Override
            public Fragment getItem(int position) {
                return position == 0 ? new FriendListFragment() : new SociaListFragment();
            }

            @Override
            public int getCount() {
                return 2;
            }
        });

    }

    private void initIndicator() {
        CommonNavigator navigator = new CommonNavigator(getContext());
        navigator.setAdapter(new CommonNavigatorAdapter() {
            @Override
            public int getCount() {
                return mTitleDataList.length;
            }

            @Override
            public IPagerTitleView getTitleView(Context context, int index) {
                SimplePagerTitleView pagerTitleView = new SimplePagerTitleView(context);
                pagerTitleView.setTextSize(17f);

                pagerTitleView.setNormalColor(ContextCompat.getColor(getContext(), R.color.c909399));
                pagerTitleView.setSelectedColor(ContextCompat.getColor(getContext(), R.color.colorTheme));
                pagerTitleView.setText(mTitleDataList[index]);
                pagerTitleView.setOnClickListener(v -> pager.setCurrentItem(index));
                return pagerTitleView;
            }

            @Override
            public IPagerIndicator getIndicator(Context context) {
                LinePagerIndicator linePagerIndicator = new LinePagerIndicator(context);
                linePagerIndicator.setMode(LinePagerIndicator.MODE_MATCH_EDGE);
                linePagerIndicator.setColors(ContextCompat.getColor(getContext(), R.color.colorTheme));
                return linePagerIndicator;
            }
        });


        indicator.setNavigator(navigator);
    }

    @OnClick({R.id.ll_contract_top1, R.id.ll_contract_top2, R.id.ivScan})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ll_contract_top1:
                ((HomeActivity) getActivity()).badgeItem2.hide();
                MMKVUtils.getInstance().enCode("newFriendCount", 0);
                if (dotNewFriend != null) {
                    dotNewFriend.setVisibility(View.INVISIBLE);
                }
                startActivity(new Intent(getActivity(), NewFriendActivity.class));
                break;
            case R.id.ll_contract_top2:
                startActivity(new Intent(getActivity(), GroupChatActivity.class));
                break;
            case R.id.ivScan:
                startActivity(new Intent(getContext(),NewFriendActivity.class));
                break;
        }
    }

    private void initHead(boolean isEmpty) {
        LinearLayout ll_contract_top1;
        LinearLayout ll_contract_top2;
        LinearLayout ll_contract_top3;
        LinearLayout ll_contract_top4;
        TextView tvContactHilamgId;
        if (isEmpty) {
            layout_contract_head.setVisibility(View.VISIBLE);
            headView = layout_contract_head;
        } else {
            layout_contract_head.setVisibility(View.GONE);
        }
        ll_contract_top1 = headView.findViewById(R.id.ll_contract_top1);
        ll_contract_top2 = headView.findViewById(R.id.ll_contract_top2);
        ll_contract_top3 = headView.findViewById(R.id.ll_contract_top3);
        ll_contract_top4 = headView.findViewById(R.id.ll_contract_top4);
        tvContactHilamgId = headView.findViewById(R.id.tvContactHilamgId);
        dotNewFriend = headView.findViewById(R.id.dotNewFriend);

        tvContactHilamgId.setText(getString(R.string.my_hilamg_code, Constant.currentUser.getDuoduoId()));
        tvContactHilamgId.setOnClickListener(v -> startActivity(new Intent(getActivity(), MyQrCodeActivity.class)));

        if (MMKVUtils.getInstance().decodeLong("newFriendCount") != 0) {
            dotNewFriend.setVisibility(View.VISIBLE);
        }

        ll_contract_top1.setOnClickListener(v -> {
            ((HomeActivity) getActivity()).badgeItem2.hide();
            MMKVUtils.getInstance().enCode("newFriendCount", 0);
            dotNewFriend.setVisibility(View.INVISIBLE);
            startActivity(new Intent(getActivity(), NewFriendActivity.class));
        });

        getPermisson(ll_contract_top2, granted -> {
            if (granted) startActivity(new Intent(getActivity(), QrCodeActivity.class));
        }, Manifest.permission.CAMERA);

        ll_contract_top3.setOnClickListener(v -> startActivity(new Intent(getActivity(), GroupChatActivity.class)));

        ll_contract_top4.setOnClickListener(v -> startActivity(new Intent(getActivity(), InviterActivity.class)));
}


    @Override
    public void onResume() {
        super.onResume();

    }
}
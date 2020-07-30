package com.zxjk.duoduo.ui.msgpage;

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
import com.zxjk.duoduo.ui.HomeActivity;
import com.zxjk.duoduo.ui.base.BaseLazyFragment;
import com.zxjk.duoduo.ui.widget.ContactTitleView;
import com.zxjk.duoduo.utils.MMKVUtils;

import net.lucode.hackware.magicindicator.MagicIndicator;
import net.lucode.hackware.magicindicator.ViewPagerHelper;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.LinePagerIndicator;

public class ContactFragment extends BaseLazyFragment {
    private LinearLayout llSearch;
    private MagicIndicator indicator;
    private ViewPager pager;
    private int[] mTitleDataList = new int[]{R.string.friend, R.string.social};
    private View dotNewFriend;


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
        dotNewFriend = rootView.findViewById(R.id.dotNewFriend);

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        long newFriendCount = MMKVUtils.getInstance().decodeLong("newFriendCount");
        if (newFriendCount == 0) {
            dotNewFriend.setVisibility(View.INVISIBLE);
        } else {
            dotNewFriend.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void loadData() {
        super.loadData();

        llSearch = rootView.findViewById(R.id.llSearch);
        llSearch.setOnClickListener(v -> {
            startActivity(new Intent(getContext(), AddressListSearchActivity.class));
            getActivity().overridePendingTransition(0, 0);
        });

        rootView.findViewById(R.id.ivAdd).setOnClickListener(v -> {
            ((HomeActivity) getActivity()).badgeItem2.hide();
            MMKVUtils.getInstance().enCode("newFriendCount", 0);
            dotNewFriend.setVisibility(View.INVISIBLE);
            startActivity(new Intent(getContext(), NewFriendActivity.class));
        });

        TextView tvContactHilamgId = rootView.findViewById(R.id.tvContactHilamgId);
        tvContactHilamgId.setText(getString(R.string.my_hilamg_code, Constant.currentUser.getDuoduoId()));
        tvContactHilamgId.setOnClickListener(v -> startActivity(new Intent(getActivity(), MyQrCodeActivity.class)));

        indicator = rootView.findViewById(R.id.indicator);
        pager = rootView.findViewById(R.id.pager);
        initIndicator();
        initPager();
        ViewPagerHelper.bind(indicator, pager);
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
        pager.setOffscreenPageLimit(2);
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
                ContactTitleView title = new ContactTitleView(context);
                title.setTextSize(17f);
                title.setNormalColor(ContextCompat.getColor(context, R.color.c909399));
                title.setSelectedColor(ContextCompat.getColor(context, R.color.colorTheme));
                title.setText(mTitleDataList[index]);
                title.setOnClickListener(v -> pager.setCurrentItem(index));
                return title;
            }

            @Override
            public IPagerIndicator getIndicator(Context context) {
                LinePagerIndicator indicator = new LinePagerIndicator(context);
                indicator.setMode(LinePagerIndicator.MODE_WRAP_CONTENT);
                indicator.setColors(ContextCompat.getColor(context, R.color.colorTheme));
                return indicator;
            }
        });

        indicator.setNavigator(navigator);
    }
}
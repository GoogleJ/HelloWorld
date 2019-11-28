package com.zxjk.duoduo.ui.findpage;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.blankj.utilcode.util.ToastUtils;
import com.zxjk.duoduo.R;
import com.zxjk.duoduo.ui.base.BaseFragment;
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

import static net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.LinePagerIndicator.MODE_WRAP_CONTENT;


public class FindFragment extends BaseFragment implements View.OnClickListener {

    private MagicIndicator indicator;
    private ViewPager pager;
    private int[] mTitleDataList = new int[]{R.string.news, R.string.conjuncture};
    private ImageView ivSearch;
    private ImageView ivPublish;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        initView(inflater, container);

        initPager();

        initIndicator();

        ViewPagerHelper.bind(indicator, pager);

        return rootView;
    }

    private void initView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container) {
        rootView = inflater.inflate(R.layout.fragment_find, container, false);

        indicator = rootView.findViewById(R.id.indicator);
        pager = rootView.findViewById(R.id.pager);
        ivSearch = rootView.findViewById(R.id.ivSearch);
        ivPublish = rootView.findViewById(R.id.ivPublish);

        ivSearch.setOnClickListener(this);
        ivPublish.setOnClickListener(this);
    }

    private void initPager() {
        pager = rootView.findViewById(R.id.pager);
        pager.setAdapter(new FragmentPagerAdapter(getChildFragmentManager(), FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
            @NonNull
            @Override
            public Fragment getItem(int position) {
                return position == 0 ? new NewsPager() : new MarketPager();
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
                MsgTitleView titleView = new MsgTitleView(context);
                titleView.setOnClickListener(view -> pager.setCurrentItem(index));
                titleView.getTitleView().setText(mTitleDataList[index]);
                titleView.getBadgeView().setVisibility(View.INVISIBLE);
                return titleView;
            }

            @Override
            public IPagerIndicator getIndicator(Context context) {
                return new ImagePagerIndicator(context);
            }
        });

        indicator.setNavigator(navigator);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ivSearch:

                break;
            case R.id.ivPublish:
                ToastUtils.showShort(R.string.developing);
                break;
            default:
        }
    }
}

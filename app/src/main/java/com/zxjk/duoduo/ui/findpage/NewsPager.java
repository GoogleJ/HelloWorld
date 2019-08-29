package com.zxjk.duoduo.ui.findpage;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.blankj.utilcode.util.ToastUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.zxjk.duoduo.R;
import com.zxjk.duoduo.bean.response.BlockChainNewsBean;
import com.zxjk.duoduo.bean.response.GetCarouselMap;
import com.zxjk.duoduo.network.Api;
import com.zxjk.duoduo.network.ServiceFactory;
import com.zxjk.duoduo.network.rx.RxSchedulers;
import com.zxjk.duoduo.ui.base.BaseFragment;
import com.zxjk.duoduo.ui.widget.CircleNavigator;
import com.zxjk.duoduo.ui.widget.NewsLoadMoreView;
import com.zxjk.duoduo.ui.widget.SlopScrollView;
import com.zxjk.duoduo.utils.CommonUtils;
import com.zxjk.duoduo.utils.GlideUtil;

import net.lucode.hackware.magicindicator.MagicIndicator;
import net.lucode.hackware.magicindicator.ViewPagerHelper;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.LinePagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.SimplePagerTitleView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;

@SuppressLint("CheckResult")
public class NewsPager extends BaseFragment {
    private static final int SCROLL_SLOP = 240;

    private SlopScrollView scrollView;
    private ViewPager pagerBanner;
    private ViewPager pagerDetail;
    private DetailPagerAdapter detailPagerAdapter;
    private MagicIndicator indicatorBanner;
    private MagicIndicator indicatorTop;
    private MagicIndicator indicatorDetail;
    private FrameLayout flIndicatorDetail;
    private FrameLayout flIndicatorTop;
    private TextView tvBanner;
    //    private int[] detailTitles = {R.string.boutique, R.string.news, R.string.quick_news};
    private int[] detailTitles = {R.string.boutique, R.string.quick_news};
    private boolean hasInitHeight;

    private int currentBannerIndex;
    private Disposable bannerIntervel;
    private static final int BANNER_INTERVEL = 3;

    private List<BaseQuickAdapter> adapters = new ArrayList<>();
    private List<Integer> pages = new ArrayList<>();
    private static final int COUNT_PER_PAGE = 15;

    private Api api;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        api = ServiceFactory.getInstance().getBaseService(Api.class);

        //初始化页码集合，保存各个tab当前所在page
        for (int i = 0; i < 2; i++) {
            pages.add(0);
        }

        initView(inflater, container);

        initIndicator();

        initPager();

        bindView();

        initData();

        return rootView;
    }

    private void initBanner(List<GetCarouselMap> list) {
        CircleNavigator navigator = new CircleNavigator(getContext());
        navigator.setFollowTouch(true);
        navigator.setCircleColor(ContextCompat.getColor(getContext(), R.color.colorTheme));
        navigator.setCircleCount(list.size());
        indicatorBanner.setNavigator(navigator);

        pagerBanner.setAdapter(new PagerAdapter() {
            @Override
            public int getCount() {
                return Integer.MAX_VALUE;
            }

            @Override
            public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
                return view == object;
            }

            @NonNull
            @Override
            public Object instantiateItem(@NonNull ViewGroup container, int position) {
                int realIndex = position % list.size();

                ImageView imageView = new ImageView(getContext());

                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

                GlideUtil.loadNormalImg(imageView, list.get(realIndex).getImgUrl());

                container.addView(imageView);

                return imageView;
            }

            @Override
            public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
                container.removeView((View) object);
            }
        });

        pagerBanner.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                indicatorBanner.onPageScrolled(position % list.size(), positionOffset, positionOffsetPixels);
            }

            public void onPageSelected(int position) {
                currentBannerIndex = position;
                tvBanner.setText(list.get(position % list.size()).getImgName());
                indicatorBanner.onPageSelected(position % list.size());
            }

            public void onPageScrollStateChanged(int state) {
                indicatorBanner.onPageScrollStateChanged(state);
                switch (state) {
                    case ViewPager.SCROLL_STATE_IDLE:
                        if (bannerIntervel.isDisposed()) {
                            bannerIntervel = Observable.interval(BANNER_INTERVEL, TimeUnit.SECONDS, AndroidSchedulers.mainThread())
                                    .subscribe(a -> pagerBanner.setCurrentItem(currentBannerIndex + 1, true));
                        }
                        break;
                    case ViewPager.SCROLL_STATE_DRAGGING:
                        if (!bannerIntervel.isDisposed())
                            bannerIntervel.dispose();
                        break;
                }
            }
        });

        bannerIntervel = Observable.interval(BANNER_INTERVEL, TimeUnit.SECONDS, AndroidSchedulers.mainThread())
                .subscribe(a -> pagerBanner.setCurrentItem(currentBannerIndex + 1, true));
    }

    @SuppressLint("CheckResult")
    private void initData() {
        api.getCarouselMap()
                .compose(bindToLifecycle())
                .compose(RxSchedulers.ioObserver())
                .compose(RxSchedulers.normalTrans())
                .subscribe(list -> {
                    tvBanner.setText(list.get(0).getImgName());
                    initBanner(list);
                }, this::handleApiError);
    }

    private void bindView() {
        int slop = CommonUtils.dip2px(getContext(), SCROLL_SLOP);
        scrollView.setScrollSlop(slop);

        scrollView.setOnScrollChangeListener((NestedScrollView.OnScrollChangeListener) (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
            if (scrollY >= slop && flIndicatorTop.getVisibility() == View.INVISIBLE) {
                flIndicatorTop.setVisibility(View.VISIBLE);
                flIndicatorDetail.setVisibility(View.INVISIBLE);
            } else if (scrollY < slop && flIndicatorTop.getVisibility() == View.VISIBLE) {
                flIndicatorTop.setVisibility(View.INVISIBLE);
                flIndicatorDetail.setVisibility(View.VISIBLE);
            }
        });

        pagerDetail.getViewTreeObserver().addOnGlobalLayoutListener(() -> {
            if (hasInitHeight) {
                return;
            }
            ViewGroup.LayoutParams layoutParams = pagerDetail.getLayoutParams();
            layoutParams.height = slop + pagerDetail.getHeight();
            pagerDetail.setLayoutParams(layoutParams);
            hasInitHeight = true;
        });

        ViewPagerHelper.bind(indicatorDetail, pagerDetail);
        ViewPagerHelper.bind(indicatorTop, pagerDetail);
    }

    private void initView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container) {
        rootView = inflater.inflate(R.layout.newspager, container, false);
        scrollView = rootView.findViewById(R.id.scrollView);
        pagerBanner = rootView.findViewById(R.id.pagerBanner);
        pagerDetail = rootView.findViewById(R.id.pagerDetail);
        indicatorBanner = rootView.findViewById(R.id.indicatorBanner);
        indicatorTop = rootView.findViewById(R.id.indicatorTop);
        indicatorDetail = rootView.findViewById(R.id.indicatorDetail);
        flIndicatorDetail = rootView.findViewById(R.id.flIndicatorDetail);
        flIndicatorTop = rootView.findViewById(R.id.flIndicatorTop);
        tvBanner = rootView.findViewById(R.id.tvBanner);
    }

    private void initPager() {
        detailPagerAdapter = new DetailPagerAdapter();
        pagerDetail.setAdapter(detailPagerAdapter);
    }

    private void initIndicator() {
        CommonNavigator navigator1 = new CommonNavigator(getContext());
        navigator1.setAdjustMode(true);
        navigator1.setAdapter(new CommonNavigatorAdapter() {
            @Override
            public int getCount() {
                return 2;
            }

            @Override
            public IPagerTitleView getTitleView(Context context, int index) {
                SimplePagerTitleView pagerTitleView = new SimplePagerTitleView(context);
                pagerTitleView.setTextSize(15);
                pagerTitleView.setNormalColor(ContextCompat.getColor(getContext(), R.color.msgTitle));
                pagerTitleView.setSelectedColor(ContextCompat.getColor(getContext(), R.color.colorTheme));
                pagerTitleView.setText(detailTitles[index]);
                pagerTitleView.setOnClickListener(v -> pagerDetail.setCurrentItem(index));
                return pagerTitleView;
            }

            @Override
            public IPagerIndicator getIndicator(Context context) {
                LinePagerIndicator linePagerIndicator = new LinePagerIndicator(context);
                linePagerIndicator.setMode(LinePagerIndicator.MODE_WRAP_CONTENT);
                linePagerIndicator.setColors(ContextCompat.getColor(getContext(), R.color.colorTheme));
                return linePagerIndicator;
            }
        });

        CommonNavigator navigator2 = new CommonNavigator(getContext());
        navigator2.setAdjustMode(true);
        navigator2.setAdapter(new CommonNavigatorAdapter() {
            @Override
            public int getCount() {
                return detailTitles.length;
            }

            @Override
            public IPagerTitleView getTitleView(Context context, int index) {
                SimplePagerTitleView pagerTitleView = new SimplePagerTitleView(context);
                pagerTitleView.setTextSize(15);
                pagerTitleView.setNormalColor(ContextCompat.getColor(getContext(), R.color.msgTitle));
                pagerTitleView.setSelectedColor(ContextCompat.getColor(getContext(), R.color.colorTheme));
                pagerTitleView.setText(detailTitles[index]);
                pagerTitleView.setOnClickListener(v -> pagerDetail.setCurrentItem(index));
                return pagerTitleView;
            }

            @Override
            public IPagerIndicator getIndicator(Context context) {
                LinePagerIndicator linePagerIndicator = new LinePagerIndicator(context);
                linePagerIndicator.setMode(LinePagerIndicator.MODE_WRAP_CONTENT);
                linePagerIndicator.setColors(ContextCompat.getColor(getContext(), R.color.colorTheme));
                return linePagerIndicator;
            }
        });

        indicatorTop.setNavigator(navigator1);
        indicatorDetail.setNavigator(navigator2);
    }

    private void loadmore(int tabIndex, String finalType) {
        BaseQuickAdapter adapter = adapters.get(tabIndex);
        api.blockChainNews(finalType, String.valueOf(pages.get(tabIndex) + 1), String.valueOf(COUNT_PER_PAGE))
                .compose(bindToLifecycle())
                .compose(RxSchedulers.ioObserver())
                .compose(RxSchedulers.normalTrans())
                .subscribe(list -> {
                    if (list.size() != COUNT_PER_PAGE) {
                        adapter.loadMoreEnd(false);
                    } else {
                        Integer integer = pages.get(tabIndex);
                        pages.set(tabIndex, integer + 1);

                        adapter.loadMoreComplete();
                    }
                    adapter.addData(list);
                }, t -> adapter.loadMoreFail());
    }

    class DetailPagerAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return detailTitles.length;
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return view == object;
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            String type = "";
            switch (position) {
                case 0:
                    type = "news";
                    break;
                case 1:
                    type = "quickNews";
                    break;
                default:
            }
            SwipeRefreshLayout swipeRefreshLayout = new SwipeRefreshLayout(getContext());
            RecyclerView recycler = new RecyclerView(getContext());
            swipeRefreshLayout.addView(recycler);
            swipeRefreshLayout.setOverScrollMode(View.OVER_SCROLL_NEVER);
            recycler.setOverScrollMode(View.OVER_SCROLL_NEVER);
            recycler.setLayoutManager(new LinearLayoutManager(getContext()));

            BaseQuickAdapter<BlockChainNewsBean, BaseViewHolder> adapter;
            if (position + 1 > adapters.size()) {
                if (position == 1) {
                    //快讯
                    adapter = initQuickNewsAdapter();
                } else {
                    //其他
                    adapter = initNewsAdapter();
                }

                adapters.add(adapter);
            } else {
                adapter = adapters.get(position);
            }

            recycler.setAdapter(adapter);
            adapter.setEnableLoadMore(true);
            adapter.isFirstOnly(true);
            adapter.openLoadAnimation(BaseQuickAdapter.SLIDEIN_BOTTOM);
            adapter.setLoadMoreView(new NewsLoadMoreView());

            String finalType = type;
            adapter.setOnLoadMoreListener(() -> loadmore(position, finalType), recycler);

            container.addView(swipeRefreshLayout);

            swipeRefreshLayout.setOnRefreshListener(() -> api.blockChainNews(finalType, "0", String.valueOf(COUNT_PER_PAGE))
                    .compose(bindToLifecycle())
                    .compose(RxSchedulers.ioObserver())
                    .compose(RxSchedulers.normalTrans())
                    .subscribe(list -> {
                        if (swipeRefreshLayout.isRefreshing()) {
                            swipeRefreshLayout.setRefreshing(false);
                        }
                        adapter.replaceData(list);
                        if (list.size() != COUNT_PER_PAGE) {
                            adapter.loadMoreEnd(false);
                        }
                    }, t -> {
                        if (swipeRefreshLayout.isRefreshing()) {
                            swipeRefreshLayout.setRefreshing(false);
                        }
                        adapter.loadMoreFail();
                    }));

            api.blockChainNews(type, "0", String.valueOf(COUNT_PER_PAGE))
                    .compose(bindToLifecycle())
                    .compose(RxSchedulers.ioObserver())
                    .compose(RxSchedulers.normalTrans())
                    .subscribe(list -> {
                        if (swipeRefreshLayout.isRefreshing()) {
                            swipeRefreshLayout.setRefreshing(false);
                        }
                        adapter.setNewData(list);
                        if (list.size() != COUNT_PER_PAGE) {
                            adapter.loadMoreEnd(false);
                        }
                    }, t -> {
                        if (swipeRefreshLayout.isRefreshing()) {
                            swipeRefreshLayout.setRefreshing(false);
                        }
                        adapter.loadMoreFail();
                    });

            return swipeRefreshLayout;
        }

        @NotNull
        public BaseQuickAdapter<BlockChainNewsBean, BaseViewHolder> initNewsAdapter() {
            BaseQuickAdapter<BlockChainNewsBean, BaseViewHolder> adapter;
            adapter = new BaseQuickAdapter<BlockChainNewsBean, BaseViewHolder>(R.layout.item_find_newsdetail, null) {
                @Override
                protected void convert(BaseViewHolder helper, BlockChainNewsBean bean) {
                    helper.setText(R.id.tvTitle, bean.getTitle())
                            .setText(R.id.tvTime, (bean.getNewsTime()))
                            .setText(R.id.tvSource, bean.getArticleSource())
                            .setText(R.id.tvTag, bean.getTag());
                    GlideUtil.loadCornerImg(helper.getView(R.id.ivContent), bean.getThumPic(), 5);
                    helper.getView(R.id.tvTag);
                }
            };
            adapter.setOnItemClickListener((adapter1, view, position1) -> {
                BlockChainNewsBean o = (BlockChainNewsBean) adapter1.getData().get(position1);
                Intent intent = new Intent(getActivity(), NewsDetailActivity.class);
                intent.putExtra("id", o.getId());
                intent.putExtra("url", o.getHtmlUrl());
                intent.putExtra("title", o.getTitle());
                startActivity(intent);
            });
            return adapter;
        }

        @NotNull
        public BaseQuickAdapter<BlockChainNewsBean, BaseViewHolder> initQuickNewsAdapter() {
            BaseQuickAdapter<BlockChainNewsBean, BaseViewHolder> adapter;
            adapter = new BaseQuickAdapter<BlockChainNewsBean, BaseViewHolder>(R.layout.item_find_newsdetail_quick_news, null) {
                @Override
                protected void convert(BaseViewHolder helper, BlockChainNewsBean bean) {
                    helper.setText(R.id.tv_time, bean.getNewsTime())
                            .setText(R.id.tv_title, bean.getTitle())
                            .setText(R.id.tv_quick_new_like, bean.getLikeCount())
                            .setText(R.id.tv_quick_new_dislike, bean.getDislikeCount());

                    View slop = helper.getView(R.id.slop);
                    if (helper.getAdapterPosition() == 0) {
                        slop.setVisibility(View.VISIBLE);
                    } else {
                        slop.setVisibility(View.GONE);
                    }

                    TextView tvContent = helper.getView(R.id.tv_content);

                    if (bean.isShowAll()) {
                        tvContent.setText(bean.getArticle());
                    } else {
                        tvContent.setText(bean.getArticle().substring(0, bean.getArticle().length() / 2) + "...");
                    }

                    helper.setBackgroundRes(R.id.dot, helper.getAdapterPosition() == 0 ? R.drawable.shape_circle_yellow : R.drawable.shape_circle_blue);

                    helper.itemView.setOnClickListener(v -> {
                        bean.setShowAll(!bean.isShowAll());
                        notifyItemChanged(helper.getAdapterPosition());
                    });

                    helper.getView(R.id.iv_quick_new_like).setOnClickListener(v -> {
                        if (bean.getDisLike().equals("1")) return;
                        if ("0".equals(bean.getLike())) {
                            bean.setLike("1");
                            int likeCount = 1;
                            if (!TextUtils.isEmpty(bean.getLikeCount())) {
                                likeCount = Integer.parseInt(bean.getLikeCount()) + 1;
                            }
                            bean.setLikeCount(String.valueOf(likeCount));
                        } else {
                            bean.setLike("0");
                            if (!TextUtils.isEmpty(bean.getLikeCount())) {
                                bean.setLikeCount(String.valueOf(Integer.parseInt(bean.getLikeCount()) - 1));
                            } else {
                                bean.setLikeCount("0");
                            }
                        }
                        notifyItemChanged(helper.getAdapterPosition());
                    });

                    helper.getView(R.id.iv_quick_new_dislike).setOnClickListener(v -> {
                        if (bean.getLike().equals("1")) return;
                        if ("0".equals(bean.getDisLike())) {
                            bean.setDisLike("1");
                            int DisLikeCount = 1;
                            if (!TextUtils.isEmpty(bean.getDislikeCount())) {
                                DisLikeCount = Integer.parseInt(bean.getDislikeCount()) + 1;
                            }
                            bean.setDislikeCount(String.valueOf(DisLikeCount));
                        } else {
                            bean.setDisLike("0");
                            if (!TextUtils.isEmpty(bean.getDislikeCount())) {
                                bean.setDislikeCount(String.valueOf(Integer.parseInt(bean.getDislikeCount()) - 1));
                            } else {
                                bean.setDislikeCount("0");
                            }
                        }
                        notifyItemChanged(helper.getAdapterPosition());
                    });

                    helper.getView(R.id.iv_quick_new_share).setOnClickListener(v -> ToastUtils.showShort(R.string.developing));

                    helper.setImageResource(R.id.iv_quick_new_like, bean.getLike().equals("0") ? R.drawable.ic_quick_news_like_nor : R.drawable.ic_quick_news_like_checked)
                            .setImageResource(R.id.iv_quick_new_dislike, bean.getDisLike().equals("0") ? R.drawable.ic_quick_news_dislike_nor : R.drawable.ic_quick_news_dislike_checked);
                }
            };
            return adapter;
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        }
    }

}

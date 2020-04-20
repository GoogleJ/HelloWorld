package com.zxjk.moneyspace.ui.findpage;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.trello.rxlifecycle3.android.FragmentEvent;
import com.zxjk.moneyspace.R;
import com.zxjk.moneyspace.bean.response.BlockChainNewsBean;
import com.zxjk.moneyspace.bean.response.GetCarouselMap;
import com.zxjk.moneyspace.network.Api;
import com.zxjk.moneyspace.network.ServiceFactory;
import com.zxjk.moneyspace.network.rx.RxSchedulers;
import com.zxjk.moneyspace.ui.WebActivity;
import com.zxjk.moneyspace.ui.base.BaseFragment;
import com.zxjk.moneyspace.ui.widget.CircleNavigator;
import com.zxjk.moneyspace.ui.widget.NewsLoadMoreView;
import com.zxjk.moneyspace.ui.widget.SlopScrollView;
import com.zxjk.moneyspace.utils.CommonUtils;
import com.zxjk.moneyspace.utils.GlideUtil;

import net.lucode.hackware.magicindicator.MagicIndicator;

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
    private static final int BANNER_INTERVEL = 3;
    private static final int COUNT_PER_PAGE = 15;
    private SlopScrollView scrollView;
    private ViewPager pagerBanner;
    private ViewPager pagerDetail;
    private DetailPagerAdapter detailPagerAdapter;
    private MagicIndicator indicatorBanner;
    private TextView tvBanner;
    private boolean hasInitHeight;
    private int currentBannerIndex;
    private Disposable bannerIntervel;
    private List<BaseQuickAdapter> adapters = new ArrayList<>();
    private Api api;
    private Integer page = 0;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        api = ServiceFactory.getInstance().getBaseService(Api.class);

        initView(inflater, container);

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
                imageView.setOnClickListener(v -> {
                    if (!list.get(realIndex).getLinkUrl().equals("")) {
                        Intent intent = new Intent(getActivity(), WebActivity.class);
                        intent.putExtra("url", list.get(realIndex).getLinkUrl());
                        startActivity(intent);
                    }
                });

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
                        startBannerIntervel();
                        break;
                    case ViewPager.SCROLL_STATE_DRAGGING:
                        if (bannerIntervel != null && !bannerIntervel.isDisposed())
                            bannerIntervel.dispose();
                        break;
                }
            }
        });

        startBannerIntervel();
    }

    @Override
    public void onResume() {
        super.onResume();
        startBannerIntervel();
    }

    private void startBannerIntervel() {
        if (bannerIntervel != null && !bannerIntervel.isDisposed()) {
            bannerIntervel.dispose();
        }
        bannerIntervel = Observable.interval(BANNER_INTERVEL, TimeUnit.SECONDS, AndroidSchedulers.mainThread())
                .compose(bindUntilEvent(FragmentEvent.PAUSE))
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

        pagerDetail.getViewTreeObserver().addOnGlobalLayoutListener(() -> {
            if (hasInitHeight) {
                return;
            }
            ViewGroup.LayoutParams layoutParams = pagerDetail.getLayoutParams();
            layoutParams.height = slop + pagerDetail.getHeight();
            pagerDetail.setLayoutParams(layoutParams);
            hasInitHeight = true;
        });
    }

    private void initView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container) {
        rootView = inflater.inflate(R.layout.newspager, container, false);
        scrollView = rootView.findViewById(R.id.scrollView);
        pagerBanner = rootView.findViewById(R.id.pagerBanner);
        pagerDetail = rootView.findViewById(R.id.pagerDetail);
        indicatorBanner = rootView.findViewById(R.id.indicatorBanner);
        tvBanner = rootView.findViewById(R.id.tvBanner);
    }

    private void initPager() {
        detailPagerAdapter = new DetailPagerAdapter();
        pagerDetail.setAdapter(detailPagerAdapter);
    }

    private void loadmore(int tabIndex, String finalType) {
        BaseQuickAdapter adapter = adapters.get(tabIndex);
        api.blockChainNews(finalType, String.valueOf(page + 1), String.valueOf(COUNT_PER_PAGE))
                .compose(bindToLifecycle())
                .compose(RxSchedulers.ioObserver())
                .compose(RxSchedulers.normalTrans())
                .subscribe(list -> {
                    if (list.size() != COUNT_PER_PAGE) {
                        adapter.loadMoreEnd(false);
                    } else {
                        page += 1;
                        adapter.loadMoreComplete();
                    }
                    adapter.addData(list);
                }, t -> adapter.loadMoreFail());
    }

    class DetailPagerAdapter extends PagerAdapter {
        @Override
        public int getCount() {
            return 1;
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
                case 2:
                    type = "sickness";
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
                    adapter = initNewsAdapter();
                }

                adapters.add(adapter);
            } else {
                adapter = adapters.get(position);
            }

            TextView emptyView = new TextView(getContext());
            emptyView.setGravity(Gravity.CENTER);
            emptyView.setText(R.string.nodata_pleasewait_upgrade);
            emptyView.setTextSize(15);
            emptyView.setTextColor(ContextCompat.getColor(getContext(), R.color.textcolor2));
            adapter.setEmptyView(emptyView);

            recycler.setAdapter(adapter);
            adapter.setEnableLoadMore(true);
            adapter.setLoadMoreView(new NewsLoadMoreView());

            String finalType = type;
            adapter.setOnLoadMoreListener(() -> loadmore(position, finalType), recycler);

            container.addView(swipeRefreshLayout);
            swipeRefreshLayout.setColorSchemeColors(Color.parseColor("#272E3F"));

            swipeRefreshLayout.setEnabled(false);
            if (position == 0 || position == 1 || position == 2) {
                swipeRefreshLayout.setEnabled(true);
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
            }

            return swipeRefreshLayout;
        }

        @NotNull
        private BaseQuickAdapter<BlockChainNewsBean, BaseViewHolder> initNewsAdapter() {
            BaseQuickAdapter<BlockChainNewsBean, BaseViewHolder> adapter;
            adapter = new BaseQuickAdapter<BlockChainNewsBean, BaseViewHolder>(R.layout.item_find_newsdetail, null) {
                @Override
                protected void convert(BaseViewHolder helper, BlockChainNewsBean bean) {
                    helper.setText(R.id.tvTitle, bean.getTitle())
                            .setText(R.id.tvTime, bean.getNewsTime())
                            .setText(R.id.tvSource, bean.getArticleSource())
                            .setText(R.id.tvTag, bean.getTag());
                    GlideUtil.loadCornerImg(helper.getView(R.id.ivContent), bean.getThumPic(), 5);
                    helper.getView(R.id.tvTag);
                }
            };
            adapter.setOnItemClickListener((adapter1, view, position1) -> {
                BlockChainNewsBean o = (BlockChainNewsBean) adapter1.getData().get(position1);
                Intent intent = new Intent(getActivity(), NewsDetailActivity.class);
                intent.putExtra("url", o.getHtmlUrl());
                intent.putExtra("title", o.getTitle());
                intent.putExtra("icon", o.getThumPic());
                intent.putExtra("article", o.getArticle());
                startActivity(intent);
            });
            return adapter;
        }

        @NotNull
        private BaseQuickAdapter<BlockChainNewsBean, BaseViewHolder> initQuickNewsAdapter() {
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

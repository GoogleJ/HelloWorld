package com.zxjk.duoduo.ui.socialspace;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.view.animation.AccelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.blankj.utilcode.util.ScreenUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.zxjk.duoduo.R;
import com.zxjk.duoduo.bean.response.CommunityListBean;
import com.zxjk.duoduo.network.Api;
import com.zxjk.duoduo.network.ServiceFactory;
import com.zxjk.duoduo.network.rx.RxSchedulers;
import com.zxjk.duoduo.ui.base.BaseFragment;
import com.zxjk.duoduo.utils.CommonUtils;
import com.zxjk.duoduo.utils.GlideUtil;
import com.zxjk.duoduo.utils.RecyclerItemAverageDecoration;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class SocialPage extends BaseFragment {

    private LinearLayout llEmpty;
    private ImageView ivCreate;
    private RecyclerView recycler;
    private BaseQuickAdapter<CommunityListBean, BaseViewHolder> adapter;
    private int itemHeight;

    private Disposable animDispose;
    private ViewPropertyAnimator scaleXAnim;
    private ViewPropertyAnimator scaleYAnim;
    private boolean isAnimting;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_social, container, false);

        int dp2pixel12 = CommonUtils.dip2px(getContext(), 12);

        itemHeight = (ScreenUtils.getScreenWidth() - dp2pixel12 * 3) / 2;

        llEmpty = rootView.findViewById(R.id.llEmpty);
        ivCreate = rootView.findViewById(R.id.ivCreate);
        recycler = rootView.findViewById(R.id.recycler);

        adapter = new BaseQuickAdapter<CommunityListBean, BaseViewHolder>(R.layout.item_social, null) {
            @Override
            protected void convert(BaseViewHolder helper, CommunityListBean item) {
                ImageView ivhead = helper.getView(R.id.ivHead);
                ImageView ivPay = helper.getView(R.id.ivPay);

                ViewGroup.LayoutParams layoutParams = ivhead.getLayoutParams();
                if (layoutParams.height != itemHeight) {
                    layoutParams.height = itemHeight;
                    ivhead.setLayoutParams(layoutParams);
                }

                GlideUtil.loadNormalImg(ivhead, item.getCommunityLogo());
                helper.setText(R.id.tvTitle, item.getCommunityName())
                        .setText(R.id.tvOwner, item.getOwnerNick());
                if (item.getIsPay().equals("1")) {
                    ivPay.setVisibility(View.VISIBLE);
                } else {
                    ivPay.setVisibility(View.GONE);
                }
            }
        };

        adapter.setOnItemClickListener((adapter, view, position) -> {
            Intent intent = new Intent(getContext(), SocialHomeActivity.class);
            CommunityListBean b = (CommunityListBean) adapter.getData().get(position);
            intent.putExtra("id", b.getGroupId());
            startActivity(intent);
        });

        recycler.setAdapter(adapter);
        recycler.setLayoutManager(new GridLayoutManager(getContext(), 2));
        recycler.addItemDecoration(new RecyclerItemAverageDecoration(dp2pixel12, dp2pixel12, 2));

        recycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                if (animDispose != null && !animDispose.isDisposed()) {
                    animDispose.dispose();
                }
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    animDispose = Observable.timer(800, TimeUnit.MILLISECONDS, Schedulers.newThread())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(a -> {
                                scaleXAnim = ivCreate.animate().scaleX(1).setInterpolator(new AccelerateInterpolator()).setDuration(100);
                                scaleYAnim = ivCreate.animate().scaleY(1).setInterpolator(new AccelerateInterpolator()).setDuration(100);
                                scaleXAnim.start();
                                scaleYAnim.start();
                                isAnimting = true;
                            });
                } else if (newState == RecyclerView.SCROLL_STATE_DRAGGING) {
                    if (ivCreate.getScaleX() == 1 || isAnimting) {
                        if (scaleXAnim != null) {
                            scaleXAnim.cancel();
                            scaleYAnim.cancel();
                            isAnimting = false;
                        }
                        ivCreate.animate().scaleX(0f).setDuration(100).setInterpolator(new AccelerateInterpolator()).start();
                        ivCreate.animate().scaleY(0f).setDuration(100).setInterpolator(new AccelerateInterpolator()).start();
                    }
                }
            }
        });

        ivCreate.setOnClickListener(v -> {
            if (ivCreate.getScaleX() == 1) {
                startActivity(new Intent(getContext(), CreateSocialActivity1.class));
            }
        });

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        initData();
    }

    @SuppressLint("CheckResult")
    private void initData() {
        ServiceFactory.getInstance().getBaseService(Api.class)
                .communityList()
                .compose(bindToLifecycle())
                .compose(RxSchedulers.normalTrans())
                .compose(RxSchedulers.ioObserver())
                .subscribe(list -> {
                    if (list.size() == 0) {
                        llEmpty.setVisibility(View.VISIBLE);
                    } else {
                        llEmpty.setVisibility(View.GONE);
                    }
                    adapter.setNewData(list);
                }, this::handleApiError);
    }
}
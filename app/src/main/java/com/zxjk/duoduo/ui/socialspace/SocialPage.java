package com.zxjk.duoduo.ui.socialspace;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.blankj.utilcode.util.GsonUtils;
import com.blankj.utilcode.util.ScreenUtils;
import com.chad.library.adapter.base.BaseItemDraggableAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.chad.library.adapter.base.callback.ItemDragAndSwipeCallback;
import com.chad.library.adapter.base.listener.OnItemDragListener;
import com.google.gson.reflect.TypeToken;
import com.zxjk.duoduo.R;
import com.zxjk.duoduo.bean.response.CommunityListBean;
import com.zxjk.duoduo.network.Api;
import com.zxjk.duoduo.network.ServiceFactory;
import com.zxjk.duoduo.network.rx.RxSchedulers;
import com.zxjk.duoduo.ui.base.BaseFragment;
import com.zxjk.duoduo.utils.CommonUtils;
import com.zxjk.duoduo.utils.GlideUtil;
import com.zxjk.duoduo.utils.MMKVUtils;
import com.zxjk.duoduo.utils.RecyclerItemAverageDecoration;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class SocialPage extends BaseFragment {

    private LinearLayout llEmpty;
    private ImageView ivCreate;
    private RecyclerView recycler;
    private BaseItemDraggableAdapter<CommunityListBean, BaseViewHolder> adapter;
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

        adapter = new BaseItemDraggableAdapter<CommunityListBean, BaseViewHolder>(R.layout.item_social, null) {
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
                        .setText(R.id.tvOwner, item.getOwnerNick())
                        .setText(R.id.tvCount, item.getMembers());
                if (item.getIsPay().equals("1")) {
                    ivPay.setVisibility(View.VISIBLE);
                } else {
                    ivPay.setVisibility(View.GONE);
                }

                Button btnJoin = helper.getView(R.id.btnJoin);
                if (item.getIsInGroup().equals("1")) {
                    btnJoin.setVisibility(View.VISIBLE);
                    if (item.getIsPay().equals("1")) {
                        btnJoin.setText("付费");
                    } else {
                        btnJoin.setText("进入");
                    }
                } else {
                    btnJoin.setVisibility(View.GONE);
                }
            }
        };

        ItemDragAndSwipeCallback itemDragAndSwipeCallback = new ItemDragAndSwipeCallback(adapter);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(itemDragAndSwipeCallback);
        itemTouchHelper.attachToRecyclerView(recycler);

        adapter.enableDragItem(itemTouchHelper, R.id.cardSocial, true);
        adapter.setOnItemDragListener(new OnItemDragListener() {
            @Override
            public void onItemDragStart(RecyclerView.ViewHolder viewHolder, int i) {
            }

            @Override
            public void onItemDragMoving(RecyclerView.ViewHolder viewHolder, int i, RecyclerView.ViewHolder viewHolder1, int i1) {
            }

            @Override
            public void onItemDragEnd(RecyclerView.ViewHolder viewHolder, int i) {
                MMKVUtils.getInstance().enCode("SocialListOrder", GsonUtils.toJson(adapter.getData()));
            }
        });

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
                                scaleXAnim = ivCreate.animate().scaleX(1).setInterpolator(new OvershootInterpolator()).setDuration(100);
                                scaleYAnim = ivCreate.animate().scaleY(1).setInterpolator(new OvershootInterpolator()).setDuration(100);
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
                .map(originList -> {
                    String orderJson = MMKVUtils.getInstance().decodeString("SocialListOrder");
                    if (!TextUtils.isEmpty(orderJson)) {
                        List<CommunityListBean> cachedList = GsonUtils.fromJson(orderJson, new TypeToken<List<CommunityListBean>>() {
                        }.getType());
                        if (cachedList != null && cachedList.size() != 0) {
                            List<CommunityListBean> result = new ArrayList<>(originList.size());

                            //remove illegal data form cache
                            Iterator<CommunityListBean> cachedIterator = cachedList.iterator();
                            while (cachedIterator.hasNext()) {
                                CommunityListBean cachedBean = cachedIterator.next();
                                boolean isIllegalData = true;
                                for (CommunityListBean bean : originList) {
                                    if (bean.getGroupId().equals(cachedBean.getGroupId())) {
                                        isIllegalData = false;
                                        break;
                                    }
                                }
                                if (isIllegalData) cachedIterator.remove();
                            }

                            if (cachedList.size() == 0) {
                                MMKVUtils.getInstance().enCode("SocialListOrder", "");
                                return originList;
                            }

                            //add origin first
                            for (int i = 0; i < originList.size(); i++) {
                                CommunityListBean originBean = originList.get(i);
                                boolean isInCache = false;
                                for (int j = 0; j < cachedList.size(); j++) {
                                    CommunityListBean cachedBean = cachedList.get(j);
                                    if (cachedBean.getGroupId().equals(originBean.getGroupId())) {
                                        isInCache = true;
                                        cachedBean.setCommunityLogo(originBean.getCommunityLogo());
                                        cachedBean.setCommunityName(originBean.getCommunityName());
                                        cachedBean.setIsPay(originBean.getIsPay());
                                        cachedBean.setMembers(originBean.getMembers());
                                        cachedBean.setOwnerNick(originBean.getOwnerNick());
                                        break;
                                    }
                                }
                                if (!isInCache) result.add(originBean);
                            }

                            //add cached next
                            result.addAll(cachedList);

                            //update cache
                            MMKVUtils.getInstance().enCode("SocialListOrder", GsonUtils.toJson(result));

                            return result;
                        } else {
                            return originList;
                        }
                    }
                    return originList;
                })
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

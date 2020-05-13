package com.zxjk.duoduo.ui.minepage;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.zxjk.duoduo.Constant;
import com.zxjk.duoduo.R;
import com.zxjk.duoduo.bean.response.GetSignListResponse;
import com.zxjk.duoduo.network.Api;
import com.zxjk.duoduo.network.ServiceFactory;
import com.zxjk.duoduo.network.rx.RxSchedulers;
import com.zxjk.duoduo.ui.WebActivity;
import com.zxjk.duoduo.ui.base.BaseActivity;
import com.zxjk.duoduo.ui.widget.dialog.RewardDialog;
import com.zxjk.duoduo.utils.CommonUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RewardMotActivity extends BaseActivity {

    private TextView tvSignDays;
    private ImageView mIvHead;
    private RecyclerView recyclerSign;
    private RecyclerView mRecyclerSignTesk;
    private RelativeLayout mRlRewardrlNavigationBar;

    private String headPortrait;
    private String rewardUSDT;

    private Api api;

    private BaseQuickAdapter<GetSignListResponse.CustomerSignBean, BaseViewHolder> adapter1;
    private BaseQuickAdapter<GetSignListResponse.PointsListBean, BaseViewHolder> adapter2;

    private GetSignListResponse signListResponse;

    private int mDistanceY;


    private int height = 640;// 滑动开始变色的高,真实项目中此高度是由广告轮播或其他首页view高度决定
    private int overallXScroll = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTrasnferStatusBar(true);

        setContentView(R.layout.activity_reward_mot);

        api = ServiceFactory.getInstance().getBaseService(Api.class);

        headPortrait = Constant.currentUser.getHeadPortrait();

        initView();

        initData();

    }

    private void initView() {
        mRecyclerSignTesk = findViewById(R.id.recycler_sign_task);
        mRlRewardrlNavigationBar = findViewById(R.id.rl_reward_navigation_bar);
        findViewById(R.id.rlBack).setOnClickListener(v -> finish());
        findViewById(R.id.tv_rules_web).setOnClickListener(v -> {
            Intent intent = new Intent(RewardMotActivity.this, WebActivity.class);
            intent.putExtra("title", getString(R.string.rule));
            intent.putExtra("url", "file:///android_asset/rules/index.html");
            startActivity(intent);
        });
    }

    @SuppressLint("CheckResult")
    private void initData() {
        adapter2 = new BaseQuickAdapter<GetSignListResponse.PointsListBean, BaseViewHolder>(R.layout.linearlayout_reward_sign_tesk) {
            @Override
            protected void convert(BaseViewHolder helper, GetSignListResponse.PointsListBean p) {
                TextView mTvRewardTaskTitle = helper.getView(R.id.tv_reward_task_title);
                TextView mTvRewardTaskContent = helper.getView(R.id.tv_reward_task_content);
                TextView mTvRewardTaskNumber = helper.getView(R.id.tv_reward_task_number);
                TextView mRewardReceiveTack = helper.getView(R.id.ic_reward_receive_tack);
                TextView mRewardReceiveTack2 = helper.getView(R.id.ic_reward_receive_tack2);
                TextView mTvRewardTaskGo = helper.getView(R.id.tv_reward_task_go);
                ImageView mImgRewardTaskIc = helper.getView(R.id.img_reward_task_ic);

                mTvRewardTaskTitle.setText(p.getActivity());
                mTvRewardTaskContent.setText(p.getActivityDesc());

                Glide.with(RewardMotActivity.this).load(p.getIcon()).into(mImgRewardTaskIc);

                if (p.getReceiveStatus().equals("3")) {
                    mTvRewardTaskGo.setVisibility(View.GONE);
                    mRewardReceiveTack.setVisibility(View.GONE);
                    mRewardReceiveTack2.setVisibility(View.VISIBLE);
                    mRewardReceiveTack2.setText(getString(R.string.cant_receive));
                    taskState(p, mTvRewardTaskNumber);
                }
                //未完成
                if (p.getReceiveStatus().equals("-1")) {
                    taskState(p, mTvRewardTaskNumber);
                    mRewardReceiveTack.setVisibility(View.GONE);
                    mRewardReceiveTack2.setVisibility(View.GONE);
                }
                //未领取
                if (p.getReceiveStatus().equals("0")) {
                    mRewardReceiveTack.setVisibility(View.VISIBLE);
                    mRewardReceiveTack2.setVisibility(View.GONE);
                    if (p.getPointType().equals("0") || p.getPointType().equals("1")) {
                        mTvRewardTaskGo.setVisibility(View.GONE);
                    }
                    taskState(p, mTvRewardTaskNumber);
                }
                //已领取
                if (p.getReceiveStatus().equals("1")) {
                    mRewardReceiveTack.setVisibility(View.GONE);
                    mRewardReceiveTack2.setVisibility(View.GONE);
                    mTvRewardTaskNumber.setText(getString(R.string.rewardTips1, p.getNumber(), p.getCounts(), p.getNumber()));
                }
                //已领取完
                if (p.getReceiveStatus().equals("2")) {
                    mTvRewardTaskGo.setVisibility(View.GONE);
                    mRewardReceiveTack.setVisibility(View.GONE);
                    mRewardReceiveTack2.setVisibility(View.VISIBLE);
                    mRewardReceiveTack2.setText(getString(R.string.received));
                    taskState(p, mTvRewardTaskNumber);
                }

                mTvRewardTaskGo.setOnClickListener(v -> {
                    switch (p.getPointType()) {
                        case "0":
                            startActivity(new Intent(RewardMotActivity.this, SettingActivity.class));
                            break;
                        case "1":
                            break;
                        case "2":
                        case "3":
                            Intent intent1 = new Intent();
                            intent1.putExtra("fromReward", true);
                            intent1.putExtra("action", "social");
                            setResult(1, intent1);
                            break;
                        case "4":
                            Intent intent = new Intent();
                            intent.putExtra("fromReward", true);
                            intent.putExtra("action", "shareNews");
                            setResult(1, intent);
                            break;
                    }
                    finish();
                });

                mRewardReceiveTack.setOnClickListener(v -> {
                    adapter2.getData().add(p);
                    notifyItemInserted(adapter2.getItemCount());
                    adapter2.getData().remove(helper.getAdapterPosition());
                    notifyItemRemoved(helper.getAdapterPosition());
                    api.receivePoint(p.getPointType())
                            .compose(bindToLifecycle())
                            .compose(RxSchedulers.ioObserver(CommonUtils.initDialog(RewardMotActivity.this)))
                            .compose(RxSchedulers.normalTrans())
                            .subscribe(s -> {
                                showDialog(getString(R.string.rewardtips2), getString(R.string.rewardtips3,s.getReceivePoint(), s.getSymbol()));
                                signListResponse.setPointsList(s.getPointsList());
                                List<GetSignListResponse.PointsListBean> pointsList;
                                pointsList = s.getPointsList();
                                if (pointsList.size() > 0) {
                                    Collections.sort(pointsList, (o1, o2) -> {
                                        if (o1.getReceiveStatus().equals("2")) {
                                            return 1;
                                        }
                                        return -1;
                                    });
                                }
                                adapter2.setNewData(pointsList);
                            }, RewardMotActivity.this::handleApiError);
                });
            }
        };

        View headerView = getLayoutInflater().inflate(R.layout.reward_mot_activity_rv_header, null);
        adapter2.addHeaderView(headerView);
        mRecyclerSignTesk.setAdapter(adapter2);
        mRecyclerSignTesk.setItemAnimator(new DefaultItemAnimator());
        mRecyclerSignTesk.setLayoutManager(new LinearLayoutManager(this));

        tvSignDays = headerView.findViewById(R.id.tvSignDays);
        recyclerSign = headerView.findViewById(R.id.recyclerSign);
        mIvHead = headerView.findViewById(R.id.iv_head_reward);
        Glide.with(this).load(headPortrait).into(mIvHead);

        adapter1 = new BaseQuickAdapter<GetSignListResponse.CustomerSignBean, BaseViewHolder>(R.layout.item_sign) {
            @Override
            protected void convert(BaseViewHolder helper, GetSignListResponse.CustomerSignBean b) {
                helper.setText(R.id.tvTime, b.getRepay() + "USDT");
                TextView tvTime = helper.getView(R.id.tvTime);
                TextView tvCoins = helper.getView(R.id.tvCoins);
                LinearLayout fl = helper.getView(R.id.ll_other_day);

                TextView mSignSevenDay = helper.getView(R.id.tv_sign_seven_day);
                TextView mSignSevenUsdt = helper.getView(R.id.tv_sign_seven_usdt);
                LinearLayout mLSignSeven = helper.getView(R.id.ll_sign_seven);
                LinearLayout mOtherDay = helper.getView(R.id.ll_other_day);

                if (helper.getAdapterPosition() == 6) {
                    mLSignSeven.setVisibility(View.VISIBLE);
                    mOtherDay.setVisibility(View.GONE);
                } else {
                    mOtherDay.setVisibility(View.VISIBLE);
                    mLSignSeven.setVisibility(View.GONE);
                }

                tvCoins.setText(getString(R.string.xx_day, String.valueOf(helper.getLayoutPosition() + 1)));
                mSignSevenDay.setText(getString(R.string.xx_day, String.valueOf(helper.getLayoutPosition() + 1)));
                mSignSevenUsdt.setText(getString(R.string.value_format, b.getRepay(), "USDT"));
                if (b.getSignStatus().equals("1")) {
                    helper.getView(R.id.llContent).setBackgroundResource(R.drawable.shape_reward_sign2);
                    helper.getView(R.id.llContent).setAlpha((float) 0.5);
                    tvCoins.setTextColor(ContextCompat.getColor(RewardMotActivity.this, R.color.white));
                    tvTime.setTextColor(ContextCompat.getColor(RewardMotActivity.this, R.color.list_item_bg_press));
                    tvTime.setText(R.string.reward_sign);
                    mSignSevenDay.setTextColor(ContextCompat.getColor(RewardMotActivity.this, R.color.white));
                    mSignSevenUsdt.setTextColor(ContextCompat.getColor(RewardMotActivity.this, R.color.list_item_bg_press));
                    mSignSevenUsdt.setText(R.string.reward_sign);
                } else if (b.getSignStatus().equals("0")) {
                    helper.getView(R.id.llContent).setAlpha((float) 1);
                    helper.getView(R.id.llContent).setBackgroundResource(R.drawable.shape_reward_sign);
                    tvCoins.setTextColor(ContextCompat.getColor(RewardMotActivity.this, R.color.black));
                    tvTime.setTextColor(ContextCompat.getColor(RewardMotActivity.this, R.color.text_click_normal));
                    mSignSevenDay.setTextColor(ContextCompat.getColor(RewardMotActivity.this, R.color.black));
                    mSignSevenUsdt.setTextColor(ContextCompat.getColor(RewardMotActivity.this, R.color.text_click_normal));
                    if (b.getLastModifyTime().equals("今日")) {
                        helper.getView(R.id.llContent).setBackgroundResource(R.drawable.shape_reward_sign2);
                        tvCoins.setTextColor(ContextCompat.getColor(RewardMotActivity.this, R.color.white));
                        tvTime.setTextColor(ContextCompat.getColor(RewardMotActivity.this, R.color.list_item_bg_press));

                        mSignSevenDay.setTextColor(ContextCompat.getColor(RewardMotActivity.this, R.color.white));
                        mSignSevenUsdt.setTextColor(ContextCompat.getColor(RewardMotActivity.this, R.color.list_item_bg_press));
                    }
                }

                mLSignSeven.setOnClickListener(v -> {
                    if (b.getLastModifyTime().equals("今日")) {
                        ServiceFactory.getInstance().getBaseService(Api.class)
                                .createSign()
                                .compose(bindToLifecycle())
                                .compose(RxSchedulers.ioObserver(CommonUtils.initDialog(RewardMotActivity.this)))
                                .compose(RxSchedulers.normalTrans())
                                .subscribe(c -> {
                                    showDialog(getString(R.string.rewardtips2), getString(R.string.rewardtips5, b.getRepay()));
                                    adapter1.setNewData(c.getCustomerSign());
                                    tvSignDays.setText(getString(R.string.xx_day1, String.valueOf(c.getCount())));
                                }, RewardMotActivity.this::handleApiError);
                    }
                });

                fl.setOnClickListener(v -> {
                    if (b.getLastModifyTime().equals("今日")) {
                        ServiceFactory.getInstance().getBaseService(Api.class)
                                .createSign()
                                .compose(bindToLifecycle())
                                .compose(RxSchedulers.ioObserver(CommonUtils.initDialog(RewardMotActivity.this)))
                                .compose(RxSchedulers.normalTrans())
                                .subscribe(c -> {
                                    showDialog(getString(R.string.rewardtips2), getString(R.string.rewardtips5, b.getRepay()));
                                    adapter1.setNewData(c.getCustomerSign());
                                    tvSignDays.setText(getString(R.string.xx_day1, String.valueOf(c.getCount())));
                                }, RewardMotActivity.this::handleApiError);
                    }
                });
            }
        };

        List<GetSignListResponse.CustomerSignBean> customerSignBean1 = new ArrayList<>();
        GetSignListResponse.CustomerSignBean customerSignBean2 = new GetSignListResponse.CustomerSignBean();
        customerSignBean2.setSignStatus("1");
        customerSignBean2.setRepay("1.00");
        for (int i = 0; i < 7; i++) {
            customerSignBean1.add(customerSignBean2);
        }
        adapter1.setNewData(customerSignBean1);

        recyclerSign.setAdapter(adapter1);
        GridLayoutManager gl = new GridLayoutManager(this, 4);
        gl.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if (position == 6) {
                    return 2;
                }
                return 1;
            }
        });
        recyclerSign.setLayoutManager(gl);

        mRecyclerSignTesk.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                mDistanceY += dy;

                int toolbarHeight = 50;

                if (mDistanceY <= toolbarHeight) {
                    float scale = (float) mDistanceY / toolbarHeight;
                    float alpha = scale * 255;
                    mRlRewardrlNavigationBar.setBackgroundColor(Color.argb((int) alpha, 0, 0, 0));
                } else {
                    mRlRewardrlNavigationBar.setBackgroundResource(R.color.colorPrimary);
                }

                overallXScroll = overallXScroll + dy;
                if (overallXScroll <= 0) {
                    mRlRewardrlNavigationBar.setBackgroundColor(Color.argb(0, 30, 144, 255));
                } else if (overallXScroll > 0 && overallXScroll <= height) {
                    float scale = (float) overallXScroll / height;
                    float alpha = (255 * scale);
                    mRlRewardrlNavigationBar.setBackgroundColor(Color.argb((int) alpha, 30, 144, 255));
                } else {
                    mRlRewardrlNavigationBar.setBackgroundColor(Color.argb(255, 30, 144, 255));
                }
            }
        });

        ServiceFactory.getInstance().getBaseService(Api.class)
                .getSignList()
                .compose(bindToLifecycle())
                .compose(RxSchedulers.ioObserver(CommonUtils.initDialog(this)))
                .compose(RxSchedulers.normalTrans())
                .subscribe(r -> {
                    for (int i = 0; i < r.getCustomerSign().size(); i++) {
                        if (r.getCustomerSign().get(i).getLastModifyTime().equals("今日") && i == r.getCustomerSign().size() - 1) {
                            rewardUSDT = r.getCustomerSign().get(0).getRepay();
                        } else if (r.getCustomerSign().get(i).getLastModifyTime().equals("今日")) {
                            rewardUSDT = r.getCustomerSign().get(i + 1).getRepay();
                        }
                    }
                    TextView signTomorrow = headerView.findViewById(R.id.tv_sign_tomorrow);
                    if (rewardUSDT != null) {
                        signTomorrow.setText(getString(R.string.rewardtips4, rewardUSDT));
                    }
                    signListResponse = r;
                    tvSignDays.setText(getString(R.string.xx_day1, String.valueOf(r.getCount())));
                    adapter1.setNewData(r.getCustomerSign());

                    List<GetSignListResponse.PointsListBean> pointsList;
                    pointsList = r.getPointsList();

                    if (pointsList.size() > 0) {
                        Collections.sort(pointsList, (o1, o2) -> {
                            if (o1.getReceiveStatus().equals("2") || o1.getReceiveStatus().equals("3")) {
                                return 1;
                            }
                            return -1;
                        });
                    }
                    adapter2.setNewData(pointsList);
                }, this::handleApiError);
    }

    public void taskState(GetSignListResponse.PointsListBean p, TextView v) {
        if (p.getPointType().equals("0") || p.getPointType().equals("1")) {
            v.setText(R.string.only_once_done);
        } else {
            v.setText(getString(R.string.rewardTips1, p.getNumber(), p.getCounts(), p.getNumber()));
        }
    }

    public void showDialog(String titleName, String title) {
        new RewardDialog(this, R.style.dialog, "", (dialog, confirm) -> {
            if (confirm) {
                dialog.dismiss();
            }
        }).setTitleName(titleName)
                .setTitle(title)
                .show();
    }
}

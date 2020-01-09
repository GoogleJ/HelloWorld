package com.zxjk.duoduo.ui.minepage;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.zxjk.duoduo.R;
import com.zxjk.duoduo.bean.response.GetSignListResponse;
import com.zxjk.duoduo.network.Api;
import com.zxjk.duoduo.network.ServiceFactory;
import com.zxjk.duoduo.network.rx.RxSchedulers;
import com.zxjk.duoduo.ui.HomeActivity;
import com.zxjk.duoduo.ui.WebActivity;
import com.zxjk.duoduo.ui.base.BaseActivity;
import com.zxjk.duoduo.ui.msgpage.GroupChatActivity;
import com.zxjk.duoduo.ui.widget.dialog.RewardDialog;
import com.zxjk.duoduo.utils.CommonUtils;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class RewardMotActivity extends BaseActivity {

    private TextView tvTotalReward;
    private TextView tvSignDays;
    private ImageView mIvHead;
    private RecyclerView recyclerSign;
    private RecyclerView mRecyclerSignTesk;

    private String headPortrait;
    private String rewardUSDT;

    private Api api;

    private BaseQuickAdapter<GetSignListResponse.CustomerSignBean, BaseViewHolder> adapter1;
    private BaseQuickAdapter<GetSignListResponse.PointsListBean, BaseViewHolder> adapter2;

    private GetSignListResponse signListResponse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTrasnferStatusBar(true);

        setContentView(R.layout.activity_reward_mot);

        api = ServiceFactory.getInstance().getBaseService(Api.class);

        headPortrait = getIntent().getStringExtra("head");

        initView();

        initData();

    }

    private void initView() {
        mRecyclerSignTesk = findViewById(R.id.recycler_sign_task);
        findViewById(R.id.rlBack).setOnClickListener(v -> finish());
        findViewById(R.id.tv_rules_web).setOnClickListener(v -> {
            Intent intent = new Intent(RewardMotActivity.this, WebActivity.class);
            intent.putExtra("title", "活动规则");
            intent.putExtra("url", "file:///android_asset/rules/index.html");
            startActivity(intent);
        });
    }

    @SuppressLint("CheckResult")
    private void initData() {
        adapter2 = new BaseQuickAdapter<GetSignListResponse.PointsListBean, BaseViewHolder>(R.layout.linearlayout_reward_sign_tesk) {
            @Override
            protected void convert(BaseViewHolder helper, GetSignListResponse.PointsListBean p) {

                Log.i("tag", p.getPointType()+p.getActivity()+p.getCounts()+"总："+p.getNumber()+"领取状态："+p.getReceiveStatus());
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

                if(p.getReceiveStatus().equals("-1") && p.getPointType().equals("1")){
                    mTvRewardTaskGo.setVisibility(View.GONE);
                    mRewardReceiveTack.setVisibility(View.GONE);
                    mRewardReceiveTack2.setVisibility(View.VISIBLE);
                    mRewardReceiveTack2.setText("不可领");
                    taskState(p, mTvRewardTaskNumber);
                }else {
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
                        mTvRewardTaskNumber.setText("每日" + p.getNumber() + "次（" + p.getCounts() + "/" + p.getNumber() + "）");
                    }
                    //已领取完
                    if (p.getReceiveStatus().equals("2")) {
                        mTvRewardTaskGo.setVisibility(View.GONE);
                        mRewardReceiveTack.setVisibility(View.GONE);
                        mRewardReceiveTack2.setVisibility(View.VISIBLE);
                        mRewardReceiveTack2.setText("已领取");
                        taskState(p, mTvRewardTaskNumber);
                    }
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
                            startActivity(new Intent(RewardMotActivity.this, GroupChatActivity.class));
                            break;
                        case "4":
                            Intent intent = new Intent(RewardMotActivity.this, HomeActivity.class);
                            intent.putExtra("id", 1);
                            startActivity(intent);
                            break;
                    }
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
                                showDialog("恭喜您！", p.getPoints() + "USDT已存入余额钱包~");
                                tvTotalReward.setText(s.getMoney());
                                signListResponse.setPointsList(s.getPointsList());
                                List<GetSignListResponse.PointsListBean> pointsList;
                                pointsList = s.getPointsList();
                                if (pointsList.size() > 0) {
                                    Collections.sort(pointsList, new Comparator<GetSignListResponse.PointsListBean>() {
                                        @Override
                                        public int compare(GetSignListResponse.PointsListBean o1, GetSignListResponse.PointsListBean o2) {
                                            if (o1.getReceiveStatus().equals("2")) {
                                                return 1;
                                            }
                                            return -1;
                                        }
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

        tvTotalReward = headerView.findViewById(R.id.tvTotalReward);
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

                tvCoins.setText("第" + (helper.getLayoutPosition() + 1) + "天");
                mSignSevenDay.setText("第" + (helper.getLayoutPosition() + 1) + "天");
                mSignSevenUsdt.setText(b.getRepay() + "USDT");
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
                    ServiceFactory.getInstance().getBaseService(Api.class)
                            .createSign()
                            .compose(bindToLifecycle())
                            .compose(RxSchedulers.ioObserver(CommonUtils.initDialog(RewardMotActivity.this)))
                            .compose(RxSchedulers.normalTrans())
                            .subscribe(c -> {
                                showDialog("恭喜您，签到成功！", b.getRepay() + "USDT已存入余额钱包~");
                                adapter1.setNewData(c.getCustomerSign());
                                tvTotalReward.setText(c.getSumPay());
                                tvSignDays.setText(c.getCount() + "\u0020天");
                            }, RewardMotActivity.this::handleApiError);
                });

                fl.setOnClickListener(v -> {
                    ServiceFactory.getInstance().getBaseService(Api.class)
                            .createSign()
                            .compose(bindToLifecycle())
                            .compose(RxSchedulers.ioObserver(CommonUtils.initDialog(RewardMotActivity.this)))
                            .compose(RxSchedulers.normalTrans())
                            .subscribe(c -> {
                                showDialog("恭喜您，签到成功！", b.getRepay() + "USDT已存入余额钱包~");
                                adapter1.setNewData(c.getCustomerSign());
                                tvTotalReward.setText(c.getSumPay());
                                tvSignDays.setText(c.getCount() + "\u0020天");
                            }, RewardMotActivity.this::handleApiError);
                });
            }
        };

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

        ServiceFactory.getInstance().getBaseService(Api.class)
                .getSignList()
                .compose(bindToLifecycle())
                .compose(RxSchedulers.ioObserver(CommonUtils.initDialog(this)))
                .compose(RxSchedulers.normalTrans())
                .subscribe(r -> {
                    for (int i = 0; i < r.getCustomerSign().size(); i++) {
                        if (r.getCustomerSign().get(i).getLastModifyTime().equals("今日") && i == r.getCustomerSign().size()-1) {
                            rewardUSDT = r.getCustomerSign().get(0).getRepay();
                        } else if (r.getCustomerSign().get(i).getLastModifyTime().equals("今日")) {
                            rewardUSDT = r.getCustomerSign().get(i + 1).getRepay();
                        }
                    }
                    TextView signTomorrow = headerView.findViewById(R.id.tv_sign_tomorrow);
                    if (rewardUSDT != null) {
                        signTomorrow.setText("明日签到可获得\u0020" + rewardUSDT + "\u0020USDT");
                    }

                    signListResponse = r;
                    tvSignDays.setText(r.getCount() + "\u0020天");
                    tvTotalReward.setText(r.getSumPay());
                    adapter1.setNewData(r.getCustomerSign());

                    List<GetSignListResponse.PointsListBean> pointsList;
                    pointsList = r.getPointsList();

                    if (pointsList.size() > 0) {
                        Collections.sort(pointsList, new Comparator<GetSignListResponse.PointsListBean>() {
                            @Override
                            public int compare(GetSignListResponse.PointsListBean o1, GetSignListResponse.PointsListBean o2) {
                                if (o1.getReceiveStatus().equals("2")) {
                                    return 1;
                                }
                                return -1;
                            }
                        });
                    }
                    adapter2.setNewData(pointsList);
                }, this::handleApiError);
    }

    public void taskState(GetSignListResponse.PointsListBean p, TextView v) {
        if (p.getPointType().equals("0") || p.getPointType().equals("1")) {
            v.setText("仅可完成一次（" + p.getCounts() + "/" + p.getNumber() + "）");
        } else {
            v.setText("每日" + p.getNumber() + "次（" + p.getCounts() + "/" + p.getNumber() + "）");
        }
    }

    public void showDialog(String titleName, String title) {
        new RewardDialog(this, R.style.dialog, "", new RewardDialog.OnCloseListener() {
            @Override
            public void onClick(Dialog dialog, boolean confirm) {
                if (confirm) {
                    dialog.dismiss();
                }
            }
        }).setTitleName(titleName)
                .setTitle(title)
                .show();
    }
}

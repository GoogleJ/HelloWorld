package com.zxjk.duoduo.ui.minepage;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.blankj.utilcode.util.GsonUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMWeb;
import com.zxjk.duoduo.Constant;
import com.zxjk.duoduo.R;
import com.zxjk.duoduo.bean.request.PayPhoneRequest;
import com.zxjk.duoduo.bean.response.GetSignListResponse;
import com.zxjk.duoduo.network.Api;
import com.zxjk.duoduo.network.ServiceFactory;
import com.zxjk.duoduo.network.rx.RxSchedulers;
import com.zxjk.duoduo.ui.HomeActivity;
import com.zxjk.duoduo.ui.WebActivity;
import com.zxjk.duoduo.ui.base.BaseActivity;
import com.zxjk.duoduo.ui.msgpage.CreateGroupActivity;
import com.zxjk.duoduo.ui.msgpage.NewFriendActivity;
import com.zxjk.duoduo.ui.msgpage.QrCodeActivity;
import com.zxjk.duoduo.ui.msgpage.SearchGroupActivity;
import com.zxjk.duoduo.ui.wallet.OneKeyBuyCoinActivity;
import com.zxjk.duoduo.ui.widget.dialog.RewardDialog;
import com.zxjk.duoduo.utils.AesUtil;
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
    private NestedScrollView scroll_view;

    private String headPortrait;
    private String rewardUSDT;
    private int isComplete = 0;

    private Api api;

    private BaseQuickAdapter<GetSignListResponse.CustomerSignBean, BaseViewHolder> adapter1;
    private BaseQuickAdapter<GetSignListResponse.PointsListBean, BaseViewHolder> adapter2;

    private GetSignListResponse signListResponse;
    

    private int height = 640;

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

                TextView mTvRewardTaskGo = helper.getView(R.id.tv_reward_task_go);
                ImageView mImgRewardTaskIc = helper.getView(R.id.img_reward_task_ic);
                TextView rewardAmount = helper.getView(R.id.tv3);
                TextView tv4 = helper.getView(R.id.tv4);

                String html;

                if (signListResponse.getActivity().getActivityType().equals("0")) {
                    html = "完成任务获得\u0020<font color=\"#1484BC\">" + signListResponse.getActivity().getRewardAmount() + "</font>\u0020" + signListResponse.getActivity().getSymbol() + "\u0020";
                } else {
                    html = "完成获得\u0020<font color=\"#1484BC\">" + signListResponse.getActivity().getRewardAmount() + "</font>\u0020" + signListResponse.getActivity().getSymbol() + "申购额度\u0020";
                }
                rewardAmount.setText(Html.fromHtml(html));

                tv4.setText("(" + (signListResponse.getPointsList().size() - isComplete) + "/" + signListResponse.getPointsList().size() + ")");

                if (p.getIsComplete().equals("1")) {
                    mTvRewardTaskGo.setText("已完成");
                    mTvRewardTaskGo.setTextColor(Color.parseColor("#909399"));
                    mTvRewardTaskGo.setCompoundDrawables(null, null, null, null);
                }

                TextView receiveReward = helper.getView(R.id.tv5);

                if (isComplete == 0 && signListResponse.getActivity().getIsReceiveReward().equals("0")) {
                    receiveReward.setBackground(getResources().getDrawable(R.drawable.reward_receive_tack, null));
                    if (signListResponse.getActivity().getActivityType().equals("0")) {
                        //任务完成并且为普通任务并且未领取
                        receiveReward.setText("领取奖励");
                    } else {
                        receiveReward.setText("获取申购额度");
                    }
                } else if (signListResponse.getActivity().getIsReceiveReward().equals("1")) {
                    receiveReward.setBackground(getResources().getDrawable(R.drawable.reward_receive_tack2, null));
                    if (signListResponse.getActivity().getActivityType().equals("0")) {
                        receiveReward.setText("已领取");
                    } else {
                        receiveReward.setText("已获取");
                        tv4.setVisibility(View.GONE);
                        if (signListResponse.getActivity().getHasNext().equals("0")) {
                            rewardAmount.setText("今日活动包已完成");
                        } else {
                            html = "明日完成获得\u0020<font color=\"#1484BC\">" + signListResponse.getActivity().getNextRewardAmount() + "</font>\u0020" + signListResponse.getActivity().getNextSymbol() + "申购额度\u0020";
                            rewardAmount.setText(Html.fromHtml(html));
                        }
                    }
                } else if (isComplete != 0) {
                    if (signListResponse.getActivity().getActivityType().equals("0")) {
                        receiveReward.setText("领取奖励");
                    } else {
                        receiveReward.setText("获取申购额度");
                    }
                }


                mTvRewardTaskTitle.setText(p.getActivity());
                mTvRewardTaskContent.setText(p.getActivityDesc());

                Glide.with(RewardMotActivity.this).load(p.getIcon()).into(mImgRewardTaskIc);

                receiveReward.setOnClickListener(v -> {
                    if (("0").equals(signListResponse.getActivity().getIsReceiveReward()) && 0 == isComplete) {
                        api.getActivityReward()
                                .compose(bindToLifecycle())
                                .compose(RxSchedulers.ioObserver(CommonUtils.initDialog(RewardMotActivity.this)))
                                .compose(RxSchedulers.normalTrans())
                                .subscribe(s -> {
                                    String htmlText;
                                    receiveReward.setBackground(getResources().getDrawable(R.drawable.reward_receive_tack2, null));
                                    if (signListResponse.getActivity().getActivityType().equals("0")) {
                                        receiveReward.setText("已领取");
                                    } else {
                                        receiveReward.setText("已获取");
                                        tv4.setVisibility(View.GONE);
                                        if (signListResponse.getActivity().getHasNext().equals("0")) {
                                            rewardAmount.setText("今日活动包已完成");
                                        } else {
                                            htmlText = "明日完成获得\u0020<font color=\"#1484BC\">" + signListResponse.getActivity().getNextRewardAmount() + "</font>\u0020" + signListResponse.getActivity().getNextSymbol() + "申购额度\u0020";
                                            rewardAmount.setText(Html.fromHtml(htmlText));
                                        }
                                    }
                                    if (signListResponse.getActivity().getActivityType().equals("1")) {
                                        showDialog(getString(R.string.rewardtips2), "已获得\u0020" + signListResponse.getActivity().getRewardAmount() + signListResponse.getActivity().getSymbol() + "\u0020申购额度!");
                                    } else {
                                        showDialog(getString(R.string.rewardtips2), "已获得\u0020" + signListResponse.getActivity().getRewardAmount() + signListResponse.getActivity().getSymbol() + "\u0020活动奖励!");
                                    }

                                }, RewardMotActivity.this::handleApiError);
                    }
                });

                mTvRewardTaskGo.setOnClickListener(v -> {
                    if (!mTvRewardTaskGo.getText().equals("已完成")) {
                        switch (p.getId()) {
                            case "1":
                                UMWeb link = new UMWeb(Constant.APP_SHARE_URL + AesUtil.getInstance().encrypt("id=" + Constant.userId));
                                link.setTitle("我在使用Hilamg聊天");
                                link.setDescription("加密私聊、社群管理、数字\n" +
                                        "支付尽在Hilamg ，你也来\n" +
                                        "试试吧～");
                                link.setThumb(new UMImage(RewardMotActivity.this, R.drawable.ic_hilamglogo4));
                                new ShareAction(RewardMotActivity.this).withMedia(link).setPlatform(SHARE_MEDIA.WEIXIN).share();
                                api.updateActivityStatus("shareToWx")
                                        .compose(bindToLifecycle())
                                        .compose(RxSchedulers.ioObserver(CommonUtils.initDialog(RewardMotActivity.this)))
                                        .compose(RxSchedulers.normalTrans())
                                        .subscribe(s -> ToastUtils.showShort("分享成功"), RewardMotActivity.this::handleApiError);
                                break;
                            case "2":
                            case "9":
                                Intent intent1 = new Intent(RewardMotActivity.this, HomeActivity.class);
                                intent1.putExtra("type", 1);
                                startActivity(intent1);
                                break;
                            case "3":
                            case "4":
                            case "6":
                            case "7":
                            case "11":
                                startActivity(new Intent(RewardMotActivity.this, HomeActivity.class));
                                break;
                            case "5":
                                startActivity(new Intent(RewardMotActivity.this, NewFriendActivity.class));
                                break;
                            case "8":
                                startActivity(new Intent(RewardMotActivity.this, SearchGroupActivity.class));
//                            intent.putExtra("fromReward", true);
//                            intent.putExtra("action", "shareNews");
//                            setResult(1, intent);
                                break;

                            case "10":
                                Intent intent2 = new Intent(RewardMotActivity.this, HomeActivity.class);
                                intent2.putExtra("type", 2);
                                startActivity(intent2);
                                break;
                            case "12":
                                startActivity(new Intent(RewardMotActivity.this, QrCodeActivity.class));
                                break;
                            case "13":
                                String data = AesUtil.getInstance().encrypt(GsonUtils.toJson(new PayPhoneRequest(Constant.token, Constant.userId)));
                                String url = "http://hilamg-recharge.ztoken.cn/?obj=" + data;
                                Intent intent = new Intent(RewardMotActivity.this, WebActivity.class);
                                intent.putExtra("url", url);
                                intent.putExtra("title", getString(R.string.payPhone));
                                startActivity(intent);
                                break;
                            case "14":
                                ServiceFactory.getInstance().getBaseService(Api.class)
                                        .getOpenPurchaseStatus()
                                        .compose(bindToLifecycle())
                                        .compose(RxSchedulers.normalTrans())
                                        .compose(RxSchedulers.ioObserver(CommonUtils.initDialog(RewardMotActivity.this)))
                                        .subscribe(d -> {
                                            if (d.equals("1")) {
                                                startActivity(new Intent(RewardMotActivity.this, OneKeyBuyCoinActivity.class));
                                            } else {
                                                ToastUtils.showShort(R.string.developing);
                                            }
                                        });
                                break;
                            case "15":
                                Intent intent3 = new Intent(RewardMotActivity.this, CreateGroupActivity.class);
                                intent3.putExtra("eventType", 1);
                                startActivity(intent3);
                                break;
                        }
                        finish();
                    }
                });


                RelativeLayout llHean = helper.getView(R.id.rl_head);
                if (helper.getAdapterPosition() == 0) {
                    llHean.setVisibility(View.VISIBLE);
                } else {
                    llHean.setVisibility(View.GONE);
                }
            }
        };


//        View headerView = getLayoutInflater().inflate(R.layout.reward_mot_activity_rv_header, null);
//        adapter2.addHeaderView(headerView);
        mRecyclerSignTesk.setAdapter(adapter2);
        mRecyclerSignTesk.setItemAnimator(new DefaultItemAnimator());
        mRecyclerSignTesk.setLayoutManager(new LinearLayoutManager(this));

        tvSignDays = findViewById(R.id.tvSignDays);
        recyclerSign = findViewById(R.id.recyclerSign);
        mIvHead = findViewById(R.id.iv_head_reward);
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


        scroll_view = findViewById(R.id.scroll_view);
        scroll_view.setOnScrollChangeListener((View.OnScrollChangeListener) (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {

            if (scroll_view.getScrollY() <= 0) {
                mRlRewardrlNavigationBar.setBackgroundColor(Color.argb(0, 0, 131, 191));
            } else if (scroll_view.getScrollY() <= height) {
                float scale = (float) scroll_view.getScrollY() / height;
                float alpha = (255 * scale);
                mRlRewardrlNavigationBar.setBackgroundColor(Color.argb((int) alpha, 0, 131, 191));
            } else {
                mRlRewardrlNavigationBar.setBackgroundColor(Color.argb(255, 0, 131, 191));
            }
        });

        ServiceFactory.getInstance().getBaseService(Api.class)
                .getActivityDetailList()
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

                    for (int i = 0; i < r.getPointsList().size(); i++) {
                        if (r.getPointsList().get(i).getIsComplete().equals("0")) {
                            isComplete += 1;
                        }
                    }
                    TextView signTomorrow = findViewById(R.id.tv_sign_tomorrow);
                    if (rewardUSDT != null) {
                        signTomorrow.setText(getString(R.string.rewardtips4, rewardUSDT));
                    }
                    signListResponse = r;
                    tvSignDays.setText(getString(R.string.xx_day1, String.valueOf(r.getCount())));
                    adapter1.setNewData(r.getCustomerSign());

                    List<GetSignListResponse.PointsListBean> pointsList;
                    pointsList = signListResponse.getPointsList();
                    if (pointsList.size() > 0) {
                        Collections.sort(pointsList, (o1, o2) -> {
                            if (o1.getIsComplete().equals("1")) {
                                return 1;
                            }
                            return -1;
                        });
                    }
                    adapter2.setNewData(pointsList);

                }, this::handleApiError);
    }

    public void taskState(GetSignListResponse.PointsListBean p, TextView v) {
//        if (p.getPointType().equals("0") || p.getPointType().equals("1")) {
//            v.setText(R.string.only_once_done);
//        } else {
//            v.setText(getString(R.string.rewardTips1, p.getNumber(), p.getCounts(), p.getNumber()));
//        }
    }

    public void showDialog(String titleName, String title) {
        new RewardDialog(this, R.style.dialog, "", (dialog, confirm, v) -> {
            if ("1".equals(signListResponse.getActivity().getActivityType()) && v == 1) {
                ToastUtils.showShort("ggg");
            } else {
                if (confirm) {
                    dialog.dismiss();
                }
            }
        }).setTitleName(titleName)
                .setTitle(title)
                .setBtnName("1".equals(signListResponse.getActivity().getActivityType()) ? "去查看" : "确定")
                .show();
    }
}

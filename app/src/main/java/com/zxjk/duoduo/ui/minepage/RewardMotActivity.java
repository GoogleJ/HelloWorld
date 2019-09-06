package com.zxjk.duoduo.ui.minepage;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.blankj.utilcode.util.ToastUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.zxjk.duoduo.R;
import com.zxjk.duoduo.bean.response.GetSignListResponse;
import com.zxjk.duoduo.network.Api;
import com.zxjk.duoduo.network.ServiceFactory;
import com.zxjk.duoduo.network.rx.RxSchedulers;
import com.zxjk.duoduo.ui.base.BaseActivity;
import com.zxjk.duoduo.utils.CommonUtils;
import com.zxjk.duoduo.utils.RecyclerItemAverageDecoration;

public class RewardMotActivity extends BaseActivity implements View.OnClickListener {

    private TextView tvTotalReward;
    private TextView tvSignText;
    private TextView tvSignDays;
    private ImageView ivSigned;
    private TextView tvMission1;
    private TextView tvMission2;
    private TextView tvMission3;
    private TextView tvMission4;
    private TextView tvMission5;
    private RecyclerView recyclerSign;

    private Api api;

    private BaseQuickAdapter<GetSignListResponse.CustomerSignBean, BaseViewHolder> adapter;

    private GetSignListResponse signListResponse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTrasnferStatusBar(true);

        setContentView(R.layout.activity_reward_mot);

        api = ServiceFactory.getInstance().getBaseService(Api.class);

        initView();
        initData();
    }

    private void initView() {
        tvTotalReward = findViewById(R.id.tvTotalReward);
        ivSigned = findViewById(R.id.ivSigned);
        tvSignText = findViewById(R.id.tvSignText);
        tvSignDays = findViewById(R.id.tvSignDays);
        recyclerSign = findViewById(R.id.recyclerSign);
        tvMission1 = findViewById(R.id.tvMission1);
        tvMission2 = findViewById(R.id.tvMission2);
        tvMission3 = findViewById(R.id.tvMission3);
        tvMission4 = findViewById(R.id.tvMission4);
        tvMission5 = findViewById(R.id.tvMission5);

        tvMission1.setOnClickListener(this);
        tvMission2.setOnClickListener(this);
        tvMission3.setOnClickListener(this);
        tvMission4.setOnClickListener(this);
        tvMission5.setOnClickListener(this);
    }

    @SuppressLint("CheckResult")
    private void initData() {
        findViewById(R.id.rlBack).setOnClickListener(v -> finish());

        adapter = new BaseQuickAdapter<GetSignListResponse.CustomerSignBean, BaseViewHolder>(R.layout.item_sign) {
            @Override
            protected void convert(BaseViewHolder helper, GetSignListResponse.CustomerSignBean b) {
                helper.setText(R.id.tvCoins, b.getRepay())
                        .setText(R.id.tvTime, b.getLastModifyTime());
                TextView tvTime = helper.getView(R.id.tvTime);
                TextView tvCoins = helper.getView(R.id.tvCoins);
                FrameLayout fl = helper.getView(R.id.fl);

                if (b.getSignStatus().equals("0")) {
                    helper.getView(R.id.llContent).setBackgroundResource(R.drawable.shape_sign_item_nor);
                    tvCoins.setTextColor(Color.parseColor("#333333"));
                    fl.setBackgroundResource(R.drawable.shape_sign_unsigned);
                    tvTime.setTextColor(ContextCompat.getColor(RewardMotActivity.this, R.color.textColor9));
                } else {
                    helper.getView(R.id.llContent).setBackgroundResource(R.drawable.shape_sign_item_check);
                    tvTime.setTextColor(ContextCompat.getColor(RewardMotActivity.this, R.color.white));
                    if (b.getLastModifyTime().equals("今日")) {
                        tvCoins.setTextColor(Color.parseColor("#FF8900"));
                        fl.setBackgroundResource(R.drawable.ic_sign_coin2);
                    } else {
                        tvCoins.setTextColor(Color.parseColor("#F3A672"));
                        fl.setBackgroundResource(R.drawable.ic_sign_coin1);
                    }
                }
            }
        };

        recyclerSign.setAdapter(adapter);
        recyclerSign.setLayoutManager(new GridLayoutManager(this, 7));
        recyclerSign.addItemDecoration(new RecyclerItemAverageDecoration(0, 24, 7));

        ServiceFactory.getInstance().getBaseService(Api.class)
                .getSignList()
                .compose(bindToLifecycle())
                .compose(RxSchedulers.ioObserver(CommonUtils.initDialog(this)))
                .compose(RxSchedulers.normalTrans())
                .subscribe(r -> {
                    signListResponse = r;
                    for (GetSignListResponse.CustomerSignBean b : r.getCustomerSign()) {
                        if (b.getLastModifyTime().equals("今日")
                                && b.getSignStatus().equals("1")) {
                            ivSigned.setVisibility(View.VISIBLE);
                            tvSignText.setText(R.string.signed);
                            break;
                        }
                    }
                    tvSignDays.setText(r.getCount());
                    tvTotalReward.setText(r.getSumPay());
                    adapter.setNewData(r.getCustomerSign());
                }, this::handleApiError);
    }

    /**
     * 签到
     *
     * @param view
     */
    @SuppressLint("CheckResult")
    public void sign(View view) {
        if (ivSigned.getVisibility() == View.VISIBLE) {
            return;
        }
        ServiceFactory.getInstance().getBaseService(Api.class)
                .createSign()
                .compose(bindToLifecycle())
                .compose(RxSchedulers.ioObserver(CommonUtils.initDialog(this)))
                .compose(RxSchedulers.normalTrans())
                .subscribe(c -> {
                    tvSignText.setText(R.string.signed1);
                    ToastUtils.showShort(R.string.sign_success);
                    ivSigned.setVisibility(View.VISIBLE);
                    adapter.setNewData(c.getCustomerSign());
                    tvTotalReward.setText(c.getSumPay());
                    tvSignDays.setText(c.getCount());
                }, this::handleApiError);
    }

    @SuppressLint("CheckResult")
    @Override
    public void onClick(View view) {
        TextView textView = (TextView) view;
        if (!textView.getText().toString().equals("领取")) {
            return;
        }
        String type = "0";
        switch (view.getId()) {
            case R.id.tvMission1:
                type = "0";
                break;
            case R.id.tvMission2:
                type = "1";
                break;
            case R.id.tvMission3:
                type = "2";
                break;
            case R.id.tvMission4:
                type = "3";
                break;
            case R.id.tvMission5:
                type = "4";
                break;
        }
        api.receivePoint(type)
                .compose(bindToLifecycle())
                .compose(RxSchedulers.ioObserver(CommonUtils.initDialog(this)))
                .compose(RxSchedulers.normalTrans())
                .subscribe(s -> {
                    tvTotalReward.setText(s);
                    refreshMissionUI();
                }, this::handleApiError);
    }

    private void refreshMissionUI() {
        if (signListResponse == null) {
            return;
        }
        for (int i = 0; i < signListResponse.getPointsList().size(); i++) {
            GetSignListResponse.PointsListBean b = signListResponse.getPointsList().get(i);

            switch (b.getPointType()) {
                case "0":
                    setUI(tvMission1, b);
                    break;
                case "1":
                    setUI(tvMission2, b);
                    break;
                case "2":
                    setUI(tvMission3, b);
                    break;
                case "3":
                    setUI(tvMission4, b);
                    break;
                case "4":
                    setUI(tvMission5, b);
                    break;
            }
        }
    }

    private void setUI(TextView tv, GetSignListResponse.PointsListBean b) {
        if (b.getReceiveStatus().equals("0")) {
            tv.setTextColor(Color.parseColor("#ffffff"));
            tv.setText("领取");
            tv.setBackgroundResource(R.drawable.shape_sign_mission2);
            return;
        }
        if (b.getReceiveStatus().equals("1")) {
            tv.setTextColor(Color.parseColor("#FF612A"));
            tv.setBackgroundResource(R.drawable.shape_sign_mission1);
            if (b.getPointType().equals("2")) {
                tv.setText(b.getCounts() + "/" + 5);
            } else if (b.getPointType().equals("4")) {
                tv.setText(b.getCounts() + "/" + 3);
            } else {
                tv.setText("待完成");
            }
            return;
        }
        tv.setTextColor(Color.parseColor("#ffffff"));
    }
}

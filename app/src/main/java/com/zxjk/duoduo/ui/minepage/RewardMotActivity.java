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

public class RewardMotActivity extends BaseActivity {

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

    private BaseQuickAdapter<GetSignListResponse.CustomerSignBean, BaseViewHolder> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTrasnferStatusBar(true);

        setContentView(R.layout.activity_reward_mot);

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
                    tvCoins.setTextColor(Color.parseColor("#333333"));
                    fl.setBackgroundResource(R.drawable.shape_sign_unsigned);
                    tvTime.setTextColor(ContextCompat.getColor(RewardMotActivity.this, R.color.textColor9));
                } else {
                    tvTime.setTextColor(ContextCompat.getColor(RewardMotActivity.this, R.color.white));
                    if (b.getLastModifyTime().equals("今日")) {
                        tvCoins.setTextColor(Color.parseColor("#F3A672"));
                        fl.setBackgroundResource(R.drawable.ic_sign_coin1);
                    } else {
                        tvCoins.setTextColor(Color.parseColor("#FF8900"));
                        fl.setBackgroundResource(R.drawable.ic_sign_coin2);
                    }
                }
            }
        };

        recyclerSign.setAdapter(adapter);
        recyclerSign.setLayoutManager(new GridLayoutManager(this, 7));
        recyclerSign.addItemDecoration(new RecyclerItemAverageDecoration(0, 0, 7));

        ServiceFactory.getInstance().getBaseService(Api.class)
                .getSignList()
                .compose(bindToLifecycle())
                .compose(RxSchedulers.ioObserver(CommonUtils.initDialog(this)))
                .compose(RxSchedulers.normalTrans())
                .subscribe(r -> {
                    GetSignListResponse getSignListResponse = new GetSignListResponse();
                    adapter.setNewData(getSignListResponse.getCustomerSign());
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
            ToastUtils.showShort(R.string.signed);
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
                }, this::handleApiError);
    }
}

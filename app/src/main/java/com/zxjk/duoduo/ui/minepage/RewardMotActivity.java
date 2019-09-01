package com.zxjk.duoduo.ui.minepage;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.blankj.utilcode.util.ToastUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.zxjk.duoduo.R;
import com.zxjk.duoduo.bean.response.ReleasePurchaseResponse;
import com.zxjk.duoduo.ui.base.BaseActivity;

public class RewardMotActivity extends BaseActivity {

    private TextView tvTotalReward;
    private TextView tvSignText;
    private TextView tvSignDays;
    private ImageView ivSigned;
    private RecyclerView recyclerSign;
    private TextView tvMission1;
    private TextView tvMission2;
    private TextView tvMission3;
    private TextView tvMission4;
    private TextView tvMission5;

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

    private void initData() {
        findViewById(R.id.rlBack).setOnClickListener(v -> finish());

        recyclerSign.setAdapter(new BaseQuickAdapter<ReleasePurchaseResponse, BaseViewHolder>(R.layout.item_sign) {
            @Override
            protected void convert(BaseViewHolder helper, ReleasePurchaseResponse item) {

            }
        });
    }

    /**
     * 签到
     *
     * @param view
     */
    public void sign(View view) {
        if (ivSigned.getVisibility() == View.VISIBLE) {
            ToastUtils.showShort(R.string.signed);
            return;
        }

    }
}

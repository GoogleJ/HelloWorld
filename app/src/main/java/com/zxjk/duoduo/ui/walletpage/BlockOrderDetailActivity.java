package com.zxjk.duoduo.ui.walletpage;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zxjk.duoduo.R;
import com.zxjk.duoduo.bean.response.GetTransferAllResponse;
import com.zxjk.duoduo.bean.response.GetTransferEthResponse;
import com.zxjk.duoduo.ui.base.BaseActivity;
import com.zxjk.duoduo.utils.CommonUtils;

public class BlockOrderDetailActivity extends BaseActivity {
    private TextView tv_title;
    private ImageView ivBlockOrdersDetailTitle;
    private TextView tvBlockOrdersDetailTips;
    private TextView tvBlockOrdersDetailMoney;
    private TextView tvItemSecond;
    private TextView tvBlockOrdersDetailCount;
    private TextView tvBlockOrdersDetailKuanggong;
    private TextView tvBlockOrdersDetailTime;
    private TextView tvBlockOrdersDetailAddress1;
    private TextView tvBlockOrdersDetailAddress2;
    private TextView tvBlockOrdersDetailLast1;
    private TextView tvBlockOrdersDetailLast2;
    private TextView tvBlockOrdeerDetailBlock;
    private LinearLayout llBlockOrdeerDetailBlock;
    private TextView tvBlockOrdersDetailCurrency;
    private TextView tvItemFirst;
    private ImageView ivItemArrow;
    private LinearLayout collectionAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_block_order_detail);
        initView();

        initData();
    }

    private void initData() {
        GetTransferAllResponse.ListBean data = (GetTransferAllResponse.ListBean) getIntent().getSerializableExtra("data");


    }

    private void initView() {
        ivBlockOrdersDetailTitle = findViewById(R.id.ivBlockOrdersDetailTitle);
        tvBlockOrdersDetailTips = findViewById(R.id.tvBlockOrdersDetailTips);
        tvBlockOrdersDetailMoney = findViewById(R.id.tvBlockOrdersDetailMoney);
        tvItemSecond = findViewById(R.id.tvItemSecond);
        tvBlockOrdersDetailCount = findViewById(R.id.tvBlockOrdersDetailCount);
        tvBlockOrdersDetailKuanggong = findViewById(R.id.tvBlockOrdersDetailKuanggong);
        tvBlockOrdersDetailTime = findViewById(R.id.tvBlockOrdersDetailTime);
        tvBlockOrdersDetailAddress1 = findViewById(R.id.tvBlockOrdersDetailAddress1);
        tvBlockOrdersDetailAddress2 = findViewById(R.id.tvBlockOrdersDetailAddress2);
        tvBlockOrdersDetailLast1 = findViewById(R.id.tvBlockOrdersDetailLast1);
        tvBlockOrdersDetailLast2 = findViewById(R.id.tvBlockOrdersDetailLast2);
        tvBlockOrdeerDetailBlock = findViewById(R.id.tvBlockOrdeerDetailBlock);
        llBlockOrdeerDetailBlock = findViewById(R.id.llBlockOrdeerDetailBlock);
        tvBlockOrdersDetailCurrency = findViewById(R.id.tvBlockOrdersDetailCurrency);
        tvItemFirst = findViewById(R.id.tvItemFirst);
        ivItemArrow = findViewById(R.id.ivItemArrow);
        collectionAddress = findViewById(R.id.collectionAddress);
        tv_title = findViewById(R.id.tv_title);
        findViewById(R.id.rl_back).setOnClickListener(v -> finish());
    }
}

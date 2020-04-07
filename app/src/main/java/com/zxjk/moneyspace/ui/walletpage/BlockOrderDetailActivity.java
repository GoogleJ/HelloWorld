package com.zxjk.moneyspace.ui.walletpage;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zxjk.moneyspace.R;
import com.zxjk.moneyspace.bean.response.GetTransferAllResponse;
import com.zxjk.moneyspace.ui.base.BaseActivity;
import com.zxjk.moneyspace.utils.GlideUtil;

import java.text.SimpleDateFormat;

public class BlockOrderDetailActivity extends BaseActivity {

    private ImageView ivType;
    private TextView tvType;
    private TextView tvStatus;
    private TextView tvMoney;
    private TextView tvPayAddress;
    private TextView tvReceiptAddress;
    private TextView tvKuangGongPrice;
    private TextView tvTradeOrder;
    private TextView tvBlock;
    private TextView tvTime;
    private LinearLayout llItemTop;
    private LinearLayout llHuaZhuan;
    private TextView tvHuaZhuanType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTrasnferStatusBar(true);
        setContentView(R.layout.activity_block_order_detail);
        initView();

        initData();
    }

    private void initData() {
        GetTransferAllResponse.ListBean data = (GetTransferAllResponse.ListBean) getIntent().getSerializableExtra("data");

        if (data.getSerialType().equals("1")) {
            llItemTop.setVisibility(View.GONE);
            llHuaZhuan.setVisibility(View.VISIBLE);
            tvType.setText(R.string.huazhuan);
            if (data.getInOrOut().equals("1")) {
                tvHuaZhuanType.setText(R.string.block2balance);
            } else if (data.getInOrOut().equals("0")) {
                tvHuaZhuanType.setText(R.string.balance2block);
            }
        } else if (data.getSerialType().equals("0")) {
            if (data.getInOrOut().equals("0")) {
                tvType.setText(R.string.zhuanru);
            } else {
                tvType.setText(R.string.zhuanchu);
            }
            tvPayAddress.setText(data.getFromAddress());
            tvReceiptAddress.setText(data.getToAddress());
        }
        GlideUtil.loadNormalImg(ivType, data.getLogo());
        tvTime.setText(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Long.parseLong(data.getCreateTime())));
        tvBlock.setText(data.getBlockNumber());
        tvTradeOrder.setText(data.getTransactionHash());
        tvKuangGongPrice.setText(data.getGasUsed());
        tvMoney.setText(data.getBalance() + "  " + data.getTokenSymbol());
        tvStatus.setText(data.getTxreceiptStatus().equals("0") ? R.string.failed : (data.getTxreceiptStatus().equals("1") ?
                R.string.success : R.string.procssing));
    }

    private void initView() {
        findViewById(R.id.rl_back).setOnClickListener(v -> finish());
        ivType = findViewById(R.id.ivType);
        tvType = findViewById(R.id.tvType);
        tvStatus = findViewById(R.id.tvStatus);
        tvMoney = findViewById(R.id.tvMoney);
        tvPayAddress = findViewById(R.id.tvPayAddress);
        tvReceiptAddress = findViewById(R.id.tvReceiptAddress);
        tvKuangGongPrice = findViewById(R.id.tvKuangGongPrice);
        tvTradeOrder = findViewById(R.id.tvTradeOrder);
        tvBlock = findViewById(R.id.tvBlock);
        tvTime = findViewById(R.id.tvTime);
        llItemTop = findViewById(R.id.llItemTop);
        llHuaZhuan = findViewById(R.id.llHuaZhuan);
        tvHuaZhuanType = findViewById(R.id.tvHuaZhuanType);
    }
}

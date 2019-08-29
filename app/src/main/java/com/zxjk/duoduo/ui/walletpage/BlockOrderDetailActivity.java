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
        String type = getIntent().getStringExtra("type");
        GetTransferAllResponse.ListBean data = (GetTransferAllResponse.ListBean) getIntent().getSerializableExtra("data");
        GetTransferEthResponse.ListBean data1 = (GetTransferEthResponse.ListBean) getIntent().getSerializableExtra("data1");

        if ("0".equals(type) && data != null) {
            //eth转入失败-成功-进行中
            if (data.getInOrOut().equals("0") && data.getTxreceiptStatus().equals("0")) {
                ivBlockOrdersDetailTitle.setImageResource(R.drawable.icon_transfer_failed);
                tvBlockOrdersDetailTips.setText(getString(R.string.transfer_in_failed));
                tvBlockOrdersDetailMoney.setText(data.getBalance() + "ETH");
                tvBlockOrdersDetailAddress1.setText(getString(R.string.receiptAddress));
                tvBlockOrdersDetailAddress2.setText(data.getFromAddress().substring(0, 7) + "..." + data.getFromAddress().substring(data.getFromAddress().length() - 5));

            } else if (data.getInOrOut().equals("0") && data.getTxreceiptStatus().equals("1")) {
                ivBlockOrdersDetailTitle.setImageResource(R.drawable.icon_transfer_successful);
                tvBlockOrdersDetailTips.setText(getString(R.string.transfer_in_successful));
                tvBlockOrdersDetailMoney.setText(data.getBalance() + "ETH");
                tvBlockOrdersDetailAddress1.setText(getString(R.string.receiptAddress));
                tvBlockOrdersDetailAddress2.setText(data.getFromAddress().substring(0, 7) + "..." + data.getFromAddress().substring(data.getFromAddress().length() - 5));
            } else if (data.getInOrOut().equals("0") && data.getTxreceiptStatus().equals("2")) {
                ivBlockOrdersDetailTitle.setImageResource(R.drawable.icon_caveat);
                tvBlockOrdersDetailTips.setText(getString(R.string.transfer_in_wait));
                tvBlockOrdersDetailMoney.setText(data.getBalance() + "ETH");
                tvBlockOrdersDetailAddress1.setText(getString(R.string.receiptAddress));
                tvBlockOrdersDetailAddress2.setText(data.getFromAddress().substring(0, 7) + "..." + data.getFromAddress().substring(data.getFromAddress().length() - 5));
                return;
            }
            //eth转出失败-成功-进行中
            if (data.getInOrOut().equals("1") && data.getTxreceiptStatus().equals("0")) {
                ivBlockOrdersDetailTitle.setImageResource(R.drawable.icon_transfer_failed);
                tvBlockOrdersDetailTips.setText(getString(R.string.transfer_out_failed));
                tvBlockOrdersDetailMoney.setText("-" + data.getBalance() + "ETH");
                tvBlockOrdersDetailAddress1.setText(getString(R.string.receiptAddress));
                tvBlockOrdersDetailAddress2.setText(data.getFromAddress().substring(0, 7) + "..." + data.getFromAddress().substring(data.getFromAddress().length() - 5));
            } else if (data.getInOrOut().equals("1") && data.getTxreceiptStatus().equals("1")) {
                ivBlockOrdersDetailTitle.setImageResource(R.drawable.icon_transfer_successful);
                tvBlockOrdersDetailTips.setText(getString(R.string.transfer_out_successful));
                tvBlockOrdersDetailMoney.setText("-" + data.getBalance() + "ETH");
                tvBlockOrdersDetailAddress1.setText(getString(R.string.receiptAddress));
                tvBlockOrdersDetailAddress2.setText(data.getFromAddress().substring(0, 7) + "..." + data.getFromAddress().substring(data.getFromAddress().length() - 5));
            } else if (data.getInOrOut().equals("1") && data.getTxreceiptStatus().equals("2")) {
                ivBlockOrdersDetailTitle.setImageResource(R.drawable.icon_caveat);
                tvBlockOrdersDetailTips.setText(getString(R.string.transfer_out_wait));
                tvBlockOrdersDetailMoney.setText(data.getBalance() + "ETH");
                tvBlockOrdersDetailAddress1.setText(getString(R.string.receiptAddress));
                tvBlockOrdersDetailAddress2.setText(data.getFromAddress().substring(0, 7) + "..." + data.getFromAddress().substring(data.getFromAddress().length() - 5));
                return;
            }
            tvItemFirst.setVisibility(View.GONE);
            ivItemArrow.setVisibility(View.GONE);
            tvBlockOrdersDetailCount.setText(data.getBalance());
            tvBlockOrdersDetailKuanggong.setText(data.getGasUsed());
            tvBlockOrdersDetailTime.setText(CommonUtils.formatTime(Long.parseLong(data.getCreateTime())));
            tvBlockOrdersDetailLast2.setText(data.getTransactionHash());
            tvBlockOrdeerDetailBlock.setText(data.getBlockNumber());
            tvItemSecond.setText("MoT");
            tv_title.setText(getString(R.string.transfer_details));
            tvItemSecond.setText("ETH");
        } else if ("0".equals(type) && data1 != null) {
            //eth转入失败-成功-进行中
            if (data1.getInOrOut().equals("0") && data1.getTxreceiptStatus().equals("0")) {
                ivBlockOrdersDetailTitle.setImageResource(R.drawable.icon_transfer_failed);
                tvBlockOrdersDetailTips.setText(getString(R.string.transfer_in_failed));
                tvBlockOrdersDetailMoney.setText(data1.getBalance() + "ETH");
                tvBlockOrdersDetailAddress1.setText(getString(R.string.receiptAddress));
                tvBlockOrdersDetailAddress2.setText(data1.getFromAddress().substring(0, 7) + "..." + data1.getFromAddress().substring(data1.getFromAddress().length() - 5));

            } else if (data1.getInOrOut().equals("0") && data1.getTxreceiptStatus().equals("1")) {
                ivBlockOrdersDetailTitle.setImageResource(R.drawable.icon_transfer_successful);
                tvBlockOrdersDetailTips.setText(getString(R.string.transfer_in_successful));
                tvBlockOrdersDetailMoney.setText(data1.getBalance() + "ETH");
                tvBlockOrdersDetailAddress1.setText(getString(R.string.receiptAddress));
                tvBlockOrdersDetailAddress2.setText(data1.getFromAddress().substring(0, 7) + "..." + data1.getFromAddress().substring(data1.getFromAddress().length() - 5));
            } else if (data1.getInOrOut().equals("0") && data1.getTxreceiptStatus().equals("2")) {
                ivBlockOrdersDetailTitle.setImageResource(R.drawable.icon_caveat);
                tvBlockOrdersDetailTips.setText(getString(R.string.transfer_in_wait));
                tvBlockOrdersDetailMoney.setText(data1.getBalance() + "ETH");
                tvBlockOrdersDetailAddress1.setText(getString(R.string.receiptAddress));
                tvBlockOrdersDetailAddress2.setText(data1.getFromAddress().substring(0, 7) + "..." + data1.getFromAddress().substring(data1.getFromAddress().length() - 5));
                return;
            }
            //eth转出失败-成功-进行中
            if (data1.getInOrOut().equals("1") && data1.getTxreceiptStatus().equals("0")) {
                ivBlockOrdersDetailTitle.setImageResource(R.drawable.icon_transfer_failed);
                tvBlockOrdersDetailTips.setText(getString(R.string.transfer_out_failed));
                tvBlockOrdersDetailMoney.setText("-" + data1.getBalance() + "ETH");
                tvBlockOrdersDetailAddress1.setText(getString(R.string.receiptAddress));
                tvBlockOrdersDetailAddress2.setText(data1.getFromAddress().substring(0, 7) + "..." + data1.getFromAddress().substring(data1.getFromAddress().length() - 5));
            } else if (data1.getInOrOut().equals("1") && data1.getTxreceiptStatus().equals("1")) {
                ivBlockOrdersDetailTitle.setImageResource(R.drawable.icon_transfer_successful);
                tvBlockOrdersDetailTips.setText(getString(R.string.transfer_out_successful));
                tvBlockOrdersDetailMoney.setText("-" + data1.getBalance() + "ETH");
                tvBlockOrdersDetailAddress1.setText(getString(R.string.receiptAddress));
                tvBlockOrdersDetailAddress2.setText(data1.getFromAddress().substring(0, 7) + "..." + data1.getFromAddress().substring(data1.getFromAddress().length() - 5));
            } else if (data1.getInOrOut().equals("1") && data1.getTxreceiptStatus().equals("2")) {
                ivBlockOrdersDetailTitle.setImageResource(R.drawable.icon_caveat);
                tvBlockOrdersDetailTips.setText(getString(R.string.transfer_out_wait));
                tvBlockOrdersDetailMoney.setText(data1.getBalance() + "ETH");
                tvBlockOrdersDetailAddress1.setText(getString(R.string.receiptAddress));
                tvBlockOrdersDetailAddress2.setText(data1.getFromAddress().substring(0, 7) + "..." + data1.getFromAddress().substring(data1.getFromAddress().length() - 5));
                return;
            }
            tvItemFirst.setVisibility(View.GONE);
            ivItemArrow.setVisibility(View.GONE);
            tvBlockOrdersDetailCount.setText(data1.getBalance());
            tvBlockOrdersDetailKuanggong.setText(data1.getGasUsed());
            tvBlockOrdersDetailTime.setText(CommonUtils.formatTime(Long.parseLong(data1.getCreateTime())));
            tvBlockOrdersDetailLast2.setText(data1.getTransactionHash());
            tvBlockOrdeerDetailBlock.setText(data1.getBlockNumber());
            tvItemSecond.setText("MoT");
            tv_title.setText(getString(R.string.transfer_details));
            tvItemSecond.setText("ETH");
        } else if ("1".equals(type)) {
            //HKB转入失败-成功-进行中
            if (data.getInOrOut().equals("0") && data.getTxreceiptStatus().equals("0")) {
                ivBlockOrdersDetailTitle.setImageResource(R.drawable.icon_transfer_failed);
                tvBlockOrdersDetailTips.setText(getString(R.string.transfer_in_failed));
                tvBlockOrdersDetailMoney.setText(data.getBalance() + "MoT");
                tvBlockOrdersDetailAddress1.setText(getString(R.string.receiptAddress));
                tvBlockOrdersDetailAddress2.setText(data.getFromAddress().substring(0, 7) + "..." + data.getFromAddress().substring(data.getFromAddress().length() - 5));

            } else if (data.getInOrOut().equals("0") && data.getTxreceiptStatus().equals("1")) {
                ivBlockOrdersDetailTitle.setImageResource(R.drawable.icon_transfer_successful);
                tvBlockOrdersDetailTips.setText(getString(R.string.transfer_in_successful));
                tvBlockOrdersDetailMoney.setText(data.getBalance() + "MoT");
                tvBlockOrdersDetailAddress1.setText(getString(R.string.receiptAddress));
                tvBlockOrdersDetailAddress2.setText(data.getFromAddress().substring(0, 7) + "..." + data.getFromAddress().substring(data.getFromAddress().length() - 5));
            } else if (data.getInOrOut().equals("0") && data.getTxreceiptStatus().equals("2")) {
                ivBlockOrdersDetailTitle.setImageResource(R.drawable.icon_caveat);
                tvBlockOrdersDetailTips.setText(getString(R.string.transfer_in_wait));
                tvBlockOrdersDetailMoney.setText(data.getBalance() + "MoT");
                tvBlockOrdersDetailAddress1.setText(getString(R.string.receiptAddress));
                tvBlockOrdersDetailAddress2.setText(data.getFromAddress().substring(0, 7) + "..." + data.getFromAddress().substring(data.getFromAddress().length() - 5));
                return;
            }
            //HKB转出失败-成功-进行中
            if (data.getInOrOut().equals("1") && data.getTxreceiptStatus().equals("0")) {
                ivBlockOrdersDetailTitle.setImageResource(R.drawable.icon_transfer_failed);
                tvBlockOrdersDetailTips.setText(getString(R.string.transfer_out_failed));
                tvBlockOrdersDetailMoney.setText("-" + data.getBalance() + "MoT");
                tvBlockOrdersDetailAddress1.setText(getString(R.string.receiptAddress));
                tvBlockOrdersDetailAddress2.setText(data.getFromAddress().substring(0, 7) + "..." + data.getFromAddress().substring(data.getFromAddress().length() - 5));
            } else if (data.getInOrOut().equals("1") && data.getTxreceiptStatus().equals("1")) {
                ivBlockOrdersDetailTitle.setImageResource(R.drawable.icon_transfer_successful);
                tvBlockOrdersDetailTips.setText(getString(R.string.transfer_out_successful));
                tvBlockOrdersDetailMoney.setText("-" + data.getBalance() + "MoT");
                tvBlockOrdersDetailAddress1.setText(getString(R.string.receiptAddress));
                tvBlockOrdersDetailAddress2.setText(data.getFromAddress().substring(0, 7) + "..." + data.getFromAddress().substring(data.getFromAddress().length() - 5));
            } else if (data.getInOrOut().equals("1") && data.getTxreceiptStatus().equals("2")) {
                ivBlockOrdersDetailTitle.setImageResource(R.drawable.icon_caveat);
                tvBlockOrdersDetailTips.setText(getString(R.string.transfer_out_wait));
                tvBlockOrdersDetailMoney.setText(data.getBalance() + "MoT");
                tvBlockOrdersDetailAddress1.setText(getString(R.string.receiptAddress));
                tvBlockOrdersDetailAddress2.setText(data.getFromAddress().substring(0, 7) + "..." + data.getFromAddress().substring(data.getFromAddress().length() - 5));
                return;
            }
            tvItemFirst.setVisibility(View.GONE);
            ivItemArrow.setVisibility(View.GONE);
            tvBlockOrdersDetailCount.setText(data.getBalance());
            tvBlockOrdersDetailKuanggong.setText(data.getGasUsed());
            tvBlockOrdersDetailTime.setText(CommonUtils.formatTime(Long.parseLong(data.getCreateTime())));
            tvBlockOrdersDetailLast2.setText(data.getTransactionHash());
            tvBlockOrdeerDetailBlock.setText(data.getBlockNumber());
            tvItemSecond.setText("MoT");

        } else if ("2".equals(type) || "3".equals(type)) {
            if ("0".equals(data.getTxreceiptStatus())) {
                ivBlockOrdersDetailTitle.setImageResource(R.drawable.icon_transfer_failed);
                tvBlockOrdersDetailTips.setText(getString(R.string.transfer_commit_failed));
                tvBlockOrdersDetailMoney.setText("+" + data.getBalance() + "MoT");
            } else if ("1".equals(data.getTxreceiptStatus())) {
                ivBlockOrdersDetailTitle.setImageResource(R.drawable.icon_transfer_successful);
                tvBlockOrdersDetailTips.setText(getString(R.string.transfer_commit_successful));
                tvBlockOrdersDetailMoney.setText("+" + data.getBalance() + "MoT");
            } else {
                ivBlockOrdersDetailTitle.setImageResource(R.drawable.icon_caveat);
                tvBlockOrdersDetailTips.setText(getString(R.string.transfer_commit_wait));
                tvBlockOrdersDetailMoney.setText("+" + data.getBalance() + "MoT");
            }
            tvItemFirst.setVisibility(View.VISIBLE);
            ivItemArrow.setVisibility(View.VISIBLE);
            tv_title.setText(R.string.transfer_commit_title);
            tvBlockOrdersDetailCurrency.setText(getString(R.string.tvBlockOrdersDetailCurrency));
            tvItemFirst.setText("MoT");
            tvItemSecond.setText("MoT");
            tvBlockOrdersDetailCount.setText(data.getBalance());
            tvBlockOrdersDetailKuanggong.setText(data.getGasUsed());
            tvBlockOrdersDetailTime.setText(CommonUtils.formatTime(Long.parseLong(data.getCreateTime())));
            collectionAddress.setVisibility(View.GONE);
            tvBlockOrdersDetailLast2.setText(data.getTransactionHash());
            llBlockOrdeerDetailBlock.setVisibility(View.GONE);

        }

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

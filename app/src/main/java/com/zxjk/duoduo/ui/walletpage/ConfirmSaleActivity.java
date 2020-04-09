package com.zxjk.moneyspace.ui.walletpage;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.shehuan.nicedialog.BaseNiceDialog;
import com.shehuan.nicedialog.NiceDialog;
import com.shehuan.nicedialog.ViewConvertListener;
import com.shehuan.nicedialog.ViewHolder;
import com.zxjk.moneyspace.R;
import com.zxjk.moneyspace.bean.response.ReleasePurchaseResponse;
import com.zxjk.moneyspace.network.Api;
import com.zxjk.moneyspace.network.ServiceFactory;
import com.zxjk.moneyspace.network.rx.RxSchedulers;
import com.zxjk.moneyspace.ui.base.BaseActivity;
import com.zxjk.moneyspace.utils.CommonUtils;
import com.zxjk.moneyspace.utils.DataUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

@SuppressLint("CheckResult")
public class ConfirmSaleActivity extends BaseActivity {

    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_minimumPurchase)
    TextView tvMinimumPurchase;
    @BindView(R.id.tv_maximumPurchase)
    TextView tvMaximumPurchase;
    @BindView(R.id.tv_numberRemaining)
    TextView tvNumberRemaining;
    private ReleasePurchaseResponse data;

    private TextView tvConfirmSaleOrderId;
    private TextView tvConfirmSaleCoinType;
    private TextView tvConfirmSalePriceReference;
    private TextView tvConfirmSaleCount;
    private TextView tvConfirmSaleTotalPrice;
    private ImageView iv_wechat, iv_alipay, iv_bank;

    private String rate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_sale);
        ButterKnife.bind(this);
        tvTitle.setText(getString(R.string.order_success));

        data = (ReleasePurchaseResponse) getIntent().getSerializableExtra("data");
        rate = getIntent().getStringExtra("rate");

        tvConfirmSaleOrderId = findViewById(R.id.tvConfirmSaleOrderId);
        tvConfirmSaleCoinType = findViewById(R.id.tvConfirmSaleCoinType);
        tvConfirmSalePriceReference = findViewById(R.id.tvConfirmSalePriceReference);
        tvConfirmSaleCount = findViewById(R.id.tvConfirmSaleCount);
        tvConfirmSaleTotalPrice = findViewById(R.id.tvConfirmSaleTotalPrice);
        iv_wechat = findViewById(R.id.iv_wechat);
        iv_alipay = findViewById(R.id.iv_alipay);
        iv_bank = findViewById(R.id.iv_bank);

        tvConfirmSaleOrderId.setText(data.getSellOrderId());
        tvConfirmSaleCoinType.setText(data.getCurrency().equals("1") ? "MoT" : "Other");
        tvConfirmSalePriceReference.setText(rate);
        tvConfirmSaleCount.setText(data.getNumber());
        tvConfirmSaleTotalPrice.setText(data.getMoney());
        tvMinimumPurchase.setText(DataUtils.getInteger(data.getMinNum()));
        tvMaximumPurchase.setText(DataUtils.getInteger(data.getMaxNum()));
        tvNumberRemaining.setText(DataUtils.getInteger(data.getUnSaledNum()));
        if (data.getPayType().contains(",")) {
            String[] split = data.getPayType().split(",");
            for (String str : split) {
                switch (str) {
                    case "1":
                        iv_wechat.setVisibility(View.VISIBLE);
                        break;
                    case "2":
                        iv_alipay.setVisibility(View.VISIBLE);
                        break;
                    case "3":
                        iv_bank.setVisibility(View.VISIBLE);
                        break;
                }
            }
        } else {
            switch (data.getPayType()) {
                case "1":
                    iv_wechat.setVisibility(View.VISIBLE);
                    break;
                case "2":
                    iv_alipay.setVisibility(View.VISIBLE);
                    break;
                case "3":
                    iv_bank.setVisibility(View.VISIBLE);
                    break;
            }
        }

    }

    //取消订单
    public void cancelOrder(View view) {
        NiceDialog.init().setLayoutId(R.layout.layout_general_dialog).setConvertListener(new ViewConvertListener() {
            @Override
            protected void convertView(ViewHolder holder, BaseNiceDialog dialog) {
                holder.setText(R.id.tv_title, "取消订单");
                holder.setText(R.id.tv_content, "您确定要取消正在挂卖的订单吗？");
                holder.setText(R.id.tv_cancel, "暂不取消");
                holder.setText(R.id.tv_notarize, "立即取消");
                holder.setOnClickListener(R.id.tv_cancel, v -> dialog.dismiss());
                holder.setOnClickListener(R.id.tv_notarize, v -> {
                    dialog.dismiss();
                    ServiceFactory.getInstance().getBaseService(Api.class)
                            .closeSellOrder(data.getSellOrderId())
                            .compose(bindToLifecycle())
                            .compose(RxSchedulers.normalTrans())
                            .compose(RxSchedulers.ioObserver(CommonUtils.initDialog(ConfirmSaleActivity.this)))
                            .subscribe(response -> {
                                finish();
                                Intent intent = new Intent(ConfirmSaleActivity.this, com.zxjk.moneyspace.ui.walletpage.CancelOrderActivity.class);
                                intent.putExtra("data", data);
                                intent.putExtra("rate", rate);
                                startActivity(intent);
                            }, ConfirmSaleActivity.this::handleApiError);


                });

            }
        }).setDimAmount(0.5f).setOutCancel(false).show(getSupportFragmentManager());

    }


    public void showOrders(View view) {
        Intent intent = new Intent(this, com.zxjk.moneyspace.ui.walletpage.ExchangeListActivity.class);
        intent.putExtra("rate", rate);
        startActivity(intent);
        finish();
    }

    @OnClick(R.id.rl_back)
    public void onClick() {
        finish();
    }
}

package com.zxjk.duoduo.ui.minepage.wallet;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.RelativeSizeSpan;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.zxjk.duoduo.R;
import com.zxjk.duoduo.bean.response.GetBalanceInfoResponse;
import com.zxjk.duoduo.network.Api;
import com.zxjk.duoduo.network.ServiceFactory;
import com.zxjk.duoduo.network.rx.RxSchedulers;
import com.zxjk.duoduo.ui.base.BaseActivity;
import com.zxjk.duoduo.ui.minepage.DetailListActivity;
import com.zxjk.duoduo.utils.CommonUtils;
import com.zxjk.duoduo.utils.GlideUtil;
import com.zxjk.duoduo.utils.MMKVUtils;

@SuppressLint("CheckResult")
public class BalanceLeftActivity extends BaseActivity {

    private TextView tvBalance;
    private TextView tvBalance2CNY;
    private ImageView ivShowOrHide;
    private RecyclerView recycler;
    private BaseQuickAdapter<GetBalanceInfoResponse.BalanceListBean, BaseViewHolder> adapter;
    private boolean isShow;

    private String hideStr1 = "***********";
    private String hideStr2 = "********";

    private GetBalanceInfoResponse response;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTrasnferStatusBar(true);
        setContentView(R.layout.activity_balance_left);

        tvBalance = findViewById(R.id.tvBalance);
        tvBalance2CNY = findViewById(R.id.tvBalance2CNY);
        ivShowOrHide = findViewById(R.id.ivShowOrHide);
        recycler = findViewById(R.id.recycler);

        adapter = new BaseQuickAdapter<GetBalanceInfoResponse.BalanceListBean, BaseViewHolder>(R.layout.item_balancelist) {
            @Override
            protected void convert(BaseViewHolder helper, GetBalanceInfoResponse.BalanceListBean item) {
                ImageView ivIcon = helper.getView(R.id.ivIcon);
                GlideUtil.loadNormalImg(ivIcon, item.getLogo());

                helper.setText(R.id.tvCoin, item.getCurrencyName())
                        .setText(R.id.tvMoney1, isShow ? item.getBalance() : hideStr2)
                        .setText(R.id.tvMoney2, isShow ? ("≈" + item.getPriceToCny() + "CNY") : hideStr2);
            }
        };
        adapter.setOnItemClickListener((adapter, view, position) -> {
            Intent intent = new Intent(BalanceLeftActivity.this, BalanceDetailActivity.class);
            intent.putExtra("data", (Parcelable) adapter.getData().get(position));
            startActivity(intent);
        });
        recycler.setLayoutManager(new LinearLayoutManager(this));
        recycler.setAdapter(adapter);
        isShow = !MMKVUtils.getInstance().decodeBool("bahaviour2_showWalletBalance");

        initData();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        initData();
    }

    private void initData() {
        ServiceFactory.getInstance().getBaseService(Api.class)
                .getBalanceInfo()
                .compose(bindToLifecycle())
                .compose(RxSchedulers.normalTrans())
                .compose(RxSchedulers.ioObserver(CommonUtils.initDialog(this)))
                .subscribe(response -> {
                    this.response = response;
                    tvBalance.setText(response.getTotalToBtc());
                    tvBalance2CNY.setText("≈" + response.getTotalToCny() + "CNY");
                    adapter.setNewData(response.getBalanceList());
                    showOrHide(null);
                }, throwable -> {
                    handleApiError(throwable);
                    finish();
                });
    }

    public void showOrHide(View view) {
        isShow = !isShow;
        MMKVUtils.getInstance().enCode("bahaviour2_showWalletBalance", isShow);
        if (isShow) {
            ivShowOrHide.setImageResource(R.drawable.ic_blockwallet_hide);

            SpannableString string = new SpannableString(response.getTotalToBtc() + " BTC");
            string.setSpan(new RelativeSizeSpan(0.56f), string.length() - 3, string.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

            tvBalance.setText(string);
            tvBalance2CNY.setText("≈" + response.getTotalToCny() + "CNY");
            adapter.notifyDataSetChanged();
        } else {
            ivShowOrHide.setImageResource(R.drawable.ic_blockwallet_show);
            tvBalance.setText(hideStr1);
            tvBalance2CNY.setText(hideStr2);
            adapter.notifyDataSetChanged();
        }
    }

    public void back(View view) {
        finish();
    }

    public void orderList(View view) {
        startActivity(new Intent(this, DetailListActivity.class));
    }

}

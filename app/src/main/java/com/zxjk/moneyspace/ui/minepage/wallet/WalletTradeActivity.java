package com.zxjk.moneyspace.ui.minepage.wallet;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.zxjk.moneyspace.R;
import com.zxjk.moneyspace.bean.response.GetTransferAllResponse;
import com.zxjk.moneyspace.network.Api;
import com.zxjk.moneyspace.network.ServiceFactory;
import com.zxjk.moneyspace.network.rx.RxSchedulers;
import com.zxjk.moneyspace.ui.base.BaseActivity;
import com.zxjk.moneyspace.ui.walletpage.BlockOrderDetailActivity;
import com.zxjk.moneyspace.ui.walletpage.ZhuanChuActivity;
import com.zxjk.moneyspace.ui.widget.NewsLoadMoreView;
import com.zxjk.moneyspace.utils.GlideUtil;

import java.text.SimpleDateFormat;

public class WalletTradeActivity extends BaseActivity {

    private String walletAddress;
    private String money;
    private String symbol;
    private String sum;
    private String logo;
    private String coinType;
    private String parentSymbol;
    private String tokenDecimal;
    private String contractAddress;

    private int page = 1;
    private int numsPerPage = 10;

    private ImageView ivLogo;
    private TextView tvBalance;
    private TextView tvBalanceToCny;
    private RecyclerView mRcWalletTrade;
    private SwipeRefreshLayout refreshLayout;

    private BaseQuickAdapter<GetTransferAllResponse.ListBean, BaseViewHolder> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet_trade);

        walletAddress = getIntent().getStringExtra("address");
        money = getIntent().getStringExtra("money");
        symbol = getIntent().getStringExtra("symbol");
        sum = getIntent().getStringExtra("sum");
        logo = getIntent().getStringExtra("logo");
        coinType = getIntent().getStringExtra("coinType");
        parentSymbol = getIntent().getStringExtra("parentSymbol");
        tokenDecimal = getIntent().getStringExtra("tokenDecimal");
        contractAddress = getIntent().getStringExtra("contractAddress");

        initView();

        initAdapter();
        refreshLayout.setRefreshing(true);

        initData();
    }

    private void initView() {
        TextView title = findViewById(R.id.tv_title);
        title.setText(symbol);
        findViewById(R.id.rl_back).setOnClickListener(v -> finish());
        ivLogo = findViewById(R.id.ivLogo);
        tvBalance = findViewById(R.id.tvBalance);
        tvBalanceToCny = findViewById(R.id.tvBalanceToCny);
        mRcWalletTrade = findViewById(R.id.rc_wallet_trade);
        mRcWalletTrade.setLayoutManager(new LinearLayoutManager(this));
        refreshLayout = findViewById(R.id.refreshLayout);

        refreshLayout.setOnRefreshListener(() -> {
            page = 1;
            initData();
        });
        refreshLayout.setColorSchemeResources(R.color.color1);

        tvBalanceToCny.setText(money.equals("-") ? "-" : ("≈" + money + "\u0020CNY"));
        tvBalance.setText(sum);
        GlideUtil.loadNormalImg(ivLogo, logo);
    }

    public void tradeOut(View view) {
        Intent intent = new Intent(this, ZhuanChuActivity.class);
        intent.putExtra("symbol", symbol);
        intent.putExtra("address", walletAddress);
        intent.putExtra("balance", tvBalance.getText().toString().trim());
        intent.putExtra("coinType", coinType);
        intent.putExtra("parentSymbol", parentSymbol);
        intent.putExtra("tokenDecimal", tokenDecimal);
        intent.putExtra("contractAddress", contractAddress);
        intent.putExtra("sum", sum);
        startActivity(intent);
    }

    public void tradeIn(View view) {
        Intent intent = new Intent(this, BlockWalletPaymentQRActivity.class);
        intent.putExtra("symbol", symbol);
        intent.putExtra("address", walletAddress);
        intent.putExtra("logo", logo);
        startActivity(intent);
    }

    private void initAdapter() {
        adapter = new BaseQuickAdapter<GetTransferAllResponse.ListBean, BaseViewHolder>(R.layout.recycler_wallet_trade) {
            @Override
            protected void convert(BaseViewHolder helper, GetTransferAllResponse.ListBean item) {
                helper.itemView.setOnClickListener(v -> {
                    Intent intent = new Intent(WalletTradeActivity.this, BlockOrderDetailActivity.class);
                    intent.putExtra("data", item);
                    startActivity(intent);
                });
                TextView mTvWalletCount = helper.getView(R.id.tv_wallet_count);
                TextView mTvWalletTime = helper.getView(R.id.tv_wallet_time);
                TextView mTvRewardBalance = helper.getView(R.id.tv_reward_balance);
                TextView mTvRewardTokenSymbol = helper.getView(R.id.tv_reward_tokenSymbol);
                ImageView mImgTradeIc = helper.getView(R.id.img_trade_ic);

                helper.setText(R.id.tv_reward_month, item.getMonth())
                        .setText(R.id.tv_reward_income, getString(R.string.income, item.getIncome(), symbol))
                        .setText(R.id.tv_reward_expenditure, getString(R.string.outgoing, item.getExpenditure(), symbol));
                mTvWalletCount.setText(item.getTitle());
                mTvWalletTime.setText(new SimpleDateFormat("yyyy.MM.dd HH:mm").format(Long.parseLong(item.getCreateTime())));
                mTvRewardTokenSymbol.setText(symbol);
                GlideUtil.loadNormalImg(mImgTradeIc, item.getLogo());
                if (item.getSerialType().equals("0")) {
                    if (item.getInOrOut().equals("0")) {
                        mTvRewardBalance.setTextColor(getResources().getColor(R.color.count_down));
                        mTvRewardBalance.setText("+" + item.getBalance());
                        mTvWalletCount.setText(R.string.collection_and_payment);
                        mTvRewardTokenSymbol.setTextColor(getResources().getColor(R.color.count_down));
                    } else {
                        mTvRewardBalance.setTextColor(getResources().getColor(R.color.black));
                        mTvRewardBalance.setText("-" + item.getBalance());
                        mTvRewardTokenSymbol.setTextColor(getResources().getColor(R.color.black));
                    }
                } else {
                    if(item.getInOrOut().equals("0")){
                        mTvRewardBalance.setTextColor(getResources().getColor(R.color.count_down));
                        mTvRewardBalance.setText("+" + item.getBalance());
                        mTvRewardTokenSymbol.setTextColor(getResources().getColor(R.color.count_down));
                    }else {
                        mTvRewardBalance.setTextColor(getResources().getColor(R.color.black));
                        mTvRewardBalance.setText("-" + item.getBalance());
                        mTvRewardTokenSymbol.setTextColor(getResources().getColor(R.color.black));
                    }

                }

                LinearLayout llHean = helper.getView(R.id.ll_reward_head);
                if (helper.getAdapterPosition() == 0) {
                    llHean.setVisibility(View.VISIBLE);
                } else if (getData().get(helper.getAdapterPosition() - 1).getMonth().equals(item.getMonth())) {
                    llHean.setVisibility(View.GONE);
                } else {
                    llHean.setVisibility(View.VISIBLE);
                }
            }
        };

        View inflate = LayoutInflater.from(this).inflate(R.layout.empty_publicgroup, null, false);
        TextView tv = inflate.findViewById(R.id.tv);
        ImageView iv = inflate.findViewById(R.id.iv);
        tv.setText(R.string.emptylist3);
        iv.setImageResource(R.drawable.ic_empty_orders);
        adapter.setEmptyView(inflate);

        adapter.setLoadMoreView(new NewsLoadMoreView());
        adapter.setEnableLoadMore(true);
        adapter.setOnLoadMoreListener(this::initData, mRcWalletTrade);
        mRcWalletTrade.setAdapter(adapter);
    }

    @SuppressLint("CheckResult")
    private void initData() {
        ServiceFactory.getInstance().getBaseService(Api.class)
                .getTransferAll(walletAddress, String.valueOf(page), String.valueOf(numsPerPage), symbol)
                .compose(bindToLifecycle())
                .compose(RxSchedulers.normalTrans())
                .compose(RxSchedulers.ioObserver())
                .doOnTerminate(() -> (refreshLayout).setRefreshing(false))
                .subscribe(response -> {
                            page += 1;
                            if (page == 2) {
                                adapter.setNewData(response.getList());
                                adapter.disableLoadMoreIfNotFullPage();
                            } else {
                                adapter.addData(response.getList());
                                if (response.getList().size() >= numsPerPage) {
                                    adapter.loadMoreComplete();
                                } else {
                                    adapter.loadMoreEnd(false);
                                }
                            }
                        }, t -> {
                            if (page != 1) adapter.loadMoreFail();
                            handleApiError(t);
                        }
                );
    }
}
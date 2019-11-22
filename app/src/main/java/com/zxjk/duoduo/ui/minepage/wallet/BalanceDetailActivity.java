package com.zxjk.duoduo.ui.minepage.wallet;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.RelativeSizeSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.blankj.utilcode.util.ToastUtils;
import com.blankj.utilcode.util.Utils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.zxjk.duoduo.R;
import com.zxjk.duoduo.bean.response.GetBalanceInfoResponse;
import com.zxjk.duoduo.bean.response.GetSerialBean;
import com.zxjk.duoduo.bean.response.GetSymbolSerialResponse;
import com.zxjk.duoduo.network.Api;
import com.zxjk.duoduo.network.ServiceFactory;
import com.zxjk.duoduo.network.rx.RxSchedulers;
import com.zxjk.duoduo.ui.base.BaseActivity;
import com.zxjk.duoduo.ui.minepage.DetailInfoActivity;
import com.zxjk.duoduo.ui.widget.NewsLoadMoreView;
import com.zxjk.duoduo.utils.GlideUtil;

import java.text.SimpleDateFormat;

public class BalanceDetailActivity extends BaseActivity {

    private String canTransfer;

    private GetBalanceInfoResponse.BalanceListBean data;
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private int colorRed;
    private int colorBlack;

    private ImageView ivIcon;
    private TextView tvBlance;
    private TextView tvBalance2CNY;
    private TextView tvAddress;
    private SwipeRefreshLayout refreshLayout;
    private RecyclerView recycler;
    private BaseQuickAdapter<GetSymbolSerialResponse.SymbolSerialDTOSBean, BaseViewHolder> adapter;
    private boolean fromTrade;

    private int page = 0;
    private int numsPerPage = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_balance_detail);

        colorRed = Color.parseColor("#E94545");
        colorBlack = Color.parseColor("#000000");

        data = getIntent().getParcelableExtra("data");

        initView();

        initAdapter();

        refreshLayout.setRefreshing(true);
        initData();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        fromTrade = true;
    }

    @Override
    public void finish() {
        if (fromTrade) {
            overridePendingTransition(0, 0);
            Intent intent = new Intent(this, BalanceLeftActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        } else {
            super.finish();
        }
    }

    private void initAdapter() {
        adapter = new BaseQuickAdapter<GetSymbolSerialResponse.SymbolSerialDTOSBean, BaseViewHolder>(R.layout.item_balancedetail, null) {
            @Override
            protected void convert(BaseViewHolder helper, GetSymbolSerialResponse.SymbolSerialDTOSBean item) {
                ImageView ivIcon = helper.getView(R.id.ivIcon);
                TextView tvMoney = helper.getView(R.id.tvMoney);
                GlideUtil.loadNormalImg(ivIcon, item.getLogo());
                helper.setText(R.id.tvTitle, item.getSerialTitle())
                        .setText(R.id.tvTime, sdf.format(Long.parseLong(item.getCreateTime())))
                        .setText(R.id.tvDate, item.getMonth())
                        .setText(R.id.tvIn, "收入:" + item.getIncome() + data.getCurrencyName())
                        .setText(R.id.tvOut, "支出:" + item.getExpenditure() + data.getCurrencyName());

                SpannableString string = new SpannableString((item.getSerialType().equals("0") ? "+" : "-") + item.getAmount() + " " + item.getSymbol());
                string.setSpan(new RelativeSizeSpan(0.70f), string.length() - item.getSymbol().length(), string.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                tvMoney.setText(string);
                tvMoney.setTextColor(item.getSerialType().equals("0") ? colorRed : colorBlack);

                LinearLayout llHean = helper.getView(R.id.llHead);
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
        adapter.setOnLoadMoreListener(this::initData, recycler);
        adapter.setOnItemClickListener((adapter, view, position) -> {
            Intent intent = new Intent(BalanceDetailActivity.this, DetailInfoActivity.class);
            GetSerialBean b = new GetSerialBean();
            GetSymbolSerialResponse.SymbolSerialDTOSBean dtosBean = BalanceDetailActivity.this.adapter.getData().get(position);
            b.setAmount(dtosBean.getAmount());
            b.setCreateTime(dtosBean.getCreateTime());
            b.setLogo(dtosBean.getLogo());
            b.setSerialNumber(dtosBean.getSerialNumber());
            b.setSerialTitle(dtosBean.getSerialTitle());
            b.setSerialType(dtosBean.getSerialType());
            b.setSymbol(dtosBean.getSymbol());
            b.setSource(dtosBean.getSource());
            intent.putExtra("data", b);
            startActivity(intent);
        });
        recycler.setAdapter(adapter);
    }

    @SuppressLint("CheckResult")
    private void initData() {
        ServiceFactory.getInstance().getBaseService(Api.class)
                .getSymbolSerial(String.valueOf(numsPerPage), String.valueOf(page), data.getCurrencyName(),
                        data.getParentSymbol())
                .compose(bindToLifecycle())
                .compose(RxSchedulers.normalTrans())
                .compose(RxSchedulers.ioObserver())
                .doOnTerminate(() -> (refreshLayout).setRefreshing(false))
                .subscribe(response -> {
                    canTransfer = response.getCanTransfer();
                    if (TextUtils.isEmpty(data.getBalanceAddress())) {
                        data.setBalanceAddress(response.getBalanceAddress());
                        tvAddress.setText(data.getBalanceAddress());
                    }
                    page += 1;
                    if (page == 1) {
                        adapter.setNewData(response.getSymbolSerialDTOS());
                        adapter.disableLoadMoreIfNotFullPage();
                    } else {
                        adapter.addData(response.getSymbolSerialDTOS());
                        if (response.getSymbolSerialDTOS().size() >= numsPerPage) {
                            adapter.loadMoreComplete();
                        } else {
                            adapter.loadMoreEnd(false);
                        }
                    }
                }, t -> {
                    if (page != 0) adapter.loadMoreFail();
                    handleApiError(t);
                });
    }

    private void initView() {
        TextView tv_title = findViewById(R.id.tv_title);
        tv_title.setText(data.getCurrencyName());
        findViewById(R.id.rl_back).setOnClickListener(v -> finish());

        ivIcon = findViewById(R.id.ivIcon);
        tvBlance = findViewById(R.id.tvBlance);
        tvBalance2CNY = findViewById(R.id.tvBalance2CNY);
        tvAddress = findViewById(R.id.tvAddress);
        refreshLayout = findViewById(R.id.refreshLayout);
        recycler = findViewById(R.id.recycler);
        recycler.setLayoutManager(new LinearLayoutManager(this));

        GlideUtil.loadNormalImg(ivIcon, data.getLogo());
        tvBlance.setText(data.getBalance());

        SpannableString string = new SpannableString("≈" + data.getPriceToCny() + "CNY");
        string.setSpan(new RelativeSizeSpan(0.75f), string.length() - 3, string.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        tvBalance2CNY.setText(string);
        if (!TextUtils.isEmpty(data.getBalanceAddress())) {
            tvAddress.setText(data.getBalanceAddress());
        }

        refreshLayout.setOnRefreshListener(() -> {
            page = 0;
            initData();
        });
        refreshLayout.setColorSchemeResources(R.color.colorTheme);
    }

    public void downCoin(View view) {
        if (!TextUtils.isEmpty(canTransfer) && canTransfer.equals("1")) {
            ToastUtils.showShort(R.string.developing);
            return;
        }
        Intent intent = new Intent(this, DownCoinActivity.class);
        intent.putExtra("data", data);
        startActivity(intent);
    }

    public void upCoin(View view) {
        if (!TextUtils.isEmpty(canTransfer) && canTransfer.equals("1")) {
            ToastUtils.showShort(R.string.developing);
            return;
        }
        Intent intent = new Intent(this, UpCoinActivity.class);
        intent.putExtra("data", data);
        startActivity(intent);
    }

    public void copyAddress(View view) {
        ToastUtils.showShort(R.string.duplicated_to_clipboard);
        ClipboardManager cm = (ClipboardManager) Utils.getApp().getSystemService(Context.CLIPBOARD_SERVICE);
        if (cm != null) {
            cm.setPrimaryClip(ClipData.newPlainText("text", data.getBalanceAddress()));
        }
    }
}

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
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.blankj.utilcode.util.ToastUtils;
import com.blankj.utilcode.util.Utils;
import com.chad.library.adapter.base.BaseSectionQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.zxjk.duoduo.R;
import com.zxjk.duoduo.bean.response.GetBalanceInfoResponse;
import com.zxjk.duoduo.bean.response.GetSymbolSerialResponse;
import com.zxjk.duoduo.bean.response.GetSymbolSerialSection;
import com.zxjk.duoduo.network.Api;
import com.zxjk.duoduo.network.ServiceFactory;
import com.zxjk.duoduo.network.rx.RxSchedulers;
import com.zxjk.duoduo.ui.base.BaseActivity;
import com.zxjk.duoduo.ui.widget.NewsLoadMoreView;
import com.zxjk.duoduo.utils.GlideUtil;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class BalanceDetailActivity extends BaseActivity {

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
    private BaseSectionQuickAdapter<GetSymbolSerialSection, BaseViewHolder> adapter;

    private int page = 1;
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

    private void initAdapter() {
        adapter = new BaseSectionQuickAdapter<GetSymbolSerialSection, BaseViewHolder>(R.layout.item_balancedetail, R.layout.item_balancedetail_head, null) {
            @Override
            protected void convertHead(BaseViewHolder helper, GetSymbolSerialSection item) {
                helper.setText(R.id.tvDate, item.getMonth())
                        .setText(R.id.tvIn, "收入:" + item.getIncome() + data.getCurrencyName())
                        .setText(R.id.tvOut, "支出:" + item.getExpenditure() + data.getCurrencyName());
            }

            @Override
            protected void convert(BaseViewHolder helper, GetSymbolSerialSection item) {
                ImageView ivIcon = helper.getView(R.id.ivIcon);
                TextView tvMoney = helper.getView(R.id.tvMoney);
                GlideUtil.loadNormalImg(ivIcon, item.t.getLogo());
                helper.setText(R.id.tvTitle, item.t.getSerialTitle())
                        .setText(R.id.tvTime, sdf.format(Long.parseLong(item.t.getCreateTime())));

                SpannableString string = new SpannableString((item.t.getSerialType().equals("0") ? "+" : "-") + item.t.getAmount() + " " + item.t.getSymbol());
                string.setSpan(new RelativeSizeSpan(0.70f), string.length() - item.t.getSymbol().length(), string.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                tvMoney.setText(string);
                tvMoney.setTextColor(item.t.getSerialType().equals("0") ? colorRed : colorBlack);
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
        recycler.setAdapter(adapter);
    }

    @SuppressLint("CheckResult")
    private void initData() {
        ServiceFactory.getInstance().getBaseService(Api.class)
                .getSymbolSerial(String.valueOf(page), String.valueOf(numsPerPage), data.getCurrencyName(),
                        data.getParentSymbol())
                .compose(bindToLifecycle())
                .compose(RxSchedulers.normalTrans())
                .doOnTerminate(() -> (refreshLayout).setRefreshing(false))
                .map(response -> {
                    runOnUiThread(() -> {
                        if (TextUtils.isEmpty(data.getBalanceAddress())) {
                            data.setBalanceAddress(response.getBalanceAddress());
                            tvAddress.setText(data.getBalanceAddress());
                        }
                    });
                    return parseResponse(response);
                })
                .compose(RxSchedulers.ioObserver())
                .subscribe(list -> {
                    page += 1;
                    if (page == 2) {
                        adapter.setNewData(list);
                        adapter.disableLoadMoreIfNotFullPage();
                    } else {
                        adapter.addData(list);
                        if (list.size() >= numsPerPage) {
                            adapter.loadMoreComplete();
                        } else {
                            adapter.loadMoreEnd(false);
                        }
                    }
                }, t -> {
                    if (page != 1) adapter.loadMoreFail();
                    handleApiError(t);
                });
    }

    private List<GetSymbolSerialSection> parseResponse(GetSymbolSerialResponse response) {
        ArrayList<GetSymbolSerialSection> result = new ArrayList<>();
        for (int i = 0; i < response.getSymbolSerialDTOS().size(); i++) {
            GetSymbolSerialResponse.SymbolSerialDTOSBean symbolSerialDTOSBean = response.getSymbolSerialDTOS().get(i);
            GetSymbolSerialResponse.SymbolSerialDTOSBean.SerialListBean serialListBean = new GetSymbolSerialResponse.SymbolSerialDTOSBean.SerialListBean();

            GetSymbolSerialSection section = new GetSymbolSerialSection(serialListBean, symbolSerialDTOSBean.getMonth(), symbolSerialDTOSBean.getIncome(), symbolSerialDTOSBean.getExpenditure());
            section.isHeader = true;
            result.add(section);
            for (int j = 0; j < symbolSerialDTOSBean.getSerialList().size(); j++) {
                result.add(new GetSymbolSerialSection(symbolSerialDTOSBean.getSerialList().get(j), "", "", ""));
            }
        }
        return result;
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
            page = 1;
            initData();
        });
        refreshLayout.setColorSchemeResources(R.color.colorTheme);
    }

    public void downCoin(View view) {
        Intent intent = new Intent(this, DownCoinActivity.class);
        intent.putExtra("data", data);
        startActivity(intent);
    }

    public void upCoin(View view) {
        Intent intent = new Intent(this, UpCoinActivity.class);
        intent.putExtra("data", data);
        startActivity(intent);
    }

    public void copyAddress(View view) {
        ToastUtils.showShort(R.string.duplicated_to_clipboard);
        ClipboardManager cm = (ClipboardManager) Utils.getApp().getSystemService(Context.CLIPBOARD_SERVICE);
        cm.setPrimaryClip(ClipData.newPlainText("text", data.getBalanceAddress()));
    }
}

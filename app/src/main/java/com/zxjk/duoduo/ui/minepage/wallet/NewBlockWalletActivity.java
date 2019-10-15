package com.zxjk.duoduo.ui.minepage.wallet;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.blankj.utilcode.util.ToastUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.zxjk.duoduo.R;
import com.zxjk.duoduo.bean.response.WalletChainInfosResponse;
import com.zxjk.duoduo.network.Api;
import com.zxjk.duoduo.network.ServiceFactory;
import com.zxjk.duoduo.network.rx.RxSchedulers;
import com.zxjk.duoduo.ui.base.BaseActivity;
import com.zxjk.duoduo.utils.CommonUtils;
import com.zxjk.duoduo.utils.GlideUtil;

@SuppressLint("CheckResult")
public class NewBlockWalletActivity extends BaseActivity {
    private WalletChainInfosResponse response;

    private boolean isShow = true;
    private String hideStr = "****  ****";

    private ImageView ivShowOrHide;
    private TextView tvNewBlockWalletSum;
    private TextView tvSign;

    private RecyclerView recycler;
    private BaseQuickAdapter<WalletChainInfosResponse.SymbolListBean, BaseViewHolder> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_block_wallet);
        setTrasnferStatusBar(true);

        initView();

        initRecycler();

        initData();

        showOrHideMoney();
    }

    private void initView() {
        ivShowOrHide = findViewById(R.id.ivShowOrHide);
        tvNewBlockWalletSum = findViewById(R.id.tvNewBlockWalletSum);
        tvSign = findViewById(R.id.tvSign);

        ivShowOrHide.setOnClickListener(v -> showOrHideMoney());
    }

    private void showOrHideMoney() {
        isShow = !isShow;
        adapter.notifyDataSetChanged();
        if (isShow) {
            ivShowOrHide.setImageResource(R.drawable.ic_blockwallet_hide);
            tvSign.setVisibility(View.VISIBLE);
            tvNewBlockWalletSum.setText(response.getBalanceTotal());
        } else {
            ivShowOrHide.setImageResource(R.drawable.ic_blockwallet_show);
            tvSign.setVisibility(View.GONE);
            tvNewBlockWalletSum.setText(hideStr);
        }
    }

    private void initData() {
        response = getIntent().getParcelableExtra("response");

        if (response == null) {
            ServiceFactory.getInstance().getBaseService(Api.class)
                    .getWalletChainInfos()
                    .compose(bindToLifecycle())
                    .compose(RxSchedulers.normalTrans())
                    .compose(RxSchedulers.ioObserver(CommonUtils.initDialog(this)))
                    .subscribe(response -> {
                        this.response = response;
                        adapter.setNewData(response.getSymbolList());
                    }, this::handleApiError);
        } else {
            adapter.setNewData(response.getSymbolList());
        }
    }

    private void initRecycler() {
        recycler = findViewById(R.id.recycler);
        recycler.setLayoutManager(new LinearLayoutManager(this));
        adapter = new BaseQuickAdapter<WalletChainInfosResponse.SymbolListBean, BaseViewHolder>(R.layout.item_new_blockwallet) {
            @Override
            protected void convert(BaseViewHolder helper, WalletChainInfosResponse.SymbolListBean item) {
                ImageView ivlogo = helper.getView(R.id.ivLogo);
                GlideUtil.loadNormalImg(ivlogo, item.getLogo());
                helper.setText(R.id.tvCoin, item.getSymbol())
                        .setText(R.id.tvMoney1, isShow ? (item.getSumBalance() + "") : hideStr)
                        .setText(R.id.tvMoney2, isShow ? ("≈¥" + item.getSumBalanceToCny()) : hideStr);
            }
        };
        recycler.setAdapter(adapter);
    }

    public void back(View view) {
        finish();
    }

    public void list(View view) {
        ToastUtils.showShort(R.string.developing);
    }

    public void add(View view) {
        ToastUtils.showShort(R.string.developing);
    }
}

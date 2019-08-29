package com.zxjk.duoduo.ui.walletpage;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.zxjk.duoduo.Constant;
import com.zxjk.duoduo.R;
import com.zxjk.duoduo.network.Api;
import com.zxjk.duoduo.network.ServiceFactory;
import com.zxjk.duoduo.network.rx.RxSchedulers;
import com.zxjk.duoduo.ui.base.BaseActivity;
import com.zxjk.duoduo.ui.walletpage.adapter.ETHOrdersAdapter;
import com.zxjk.duoduo.utils.CommonUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

@SuppressLint("CheckResult")
public class BlockETHOrders extends BaseActivity {

    @BindView(R.id.tv_title)
    TextView tvTitle;
    private int page = 1;

    private TextView tvMoney1;
    private TextView tvMoney2;

    private RecyclerView recyclerview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_block_ethorders);
        ButterKnife.bind(this);
        String money1 = getIntent().getStringExtra("money1");
        String money2 = getIntent().getStringExtra("money2");

        tvTitle.setText("ETH");
        tvMoney1 = findViewById(R.id.money1);
        tvMoney2 = findViewById(R.id.money2);

        tvMoney1.setText(money1);
        tvMoney2.setText(money2);

        recyclerview = findViewById(R.id.recyclerview);
        recyclerview.setLayoutManager(new LinearLayoutManager(this));

        ServiceFactory.getInstance().getBaseService(Api.class)
                .getTransferEth(Constant.currentUser.getWalletAddress(), String.valueOf(page), "15")
                .compose(bindToLifecycle())
                .compose(RxSchedulers.ioObserver(CommonUtils.initDialog(this)))
                .compose(RxSchedulers.normalTrans())
                .subscribe(response -> recyclerview.setAdapter(new ETHOrdersAdapter(response.getList())), this::handleApiError);
    }

    public void zhuanchu(View view) {
        startActivity(new Intent(this, ZhuanChuActivity.class));
    }


    @OnClick(R.id.rl_back)
    public void onViewClicked() {
        finish();
    }
}

package com.zxjk.duoduo.ui.minepage.wallet;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.blankj.utilcode.util.ToastUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.zxjk.duoduo.R;
import com.zxjk.duoduo.bean.response.CurrencyInfosByCustomerBean;
import com.zxjk.duoduo.network.Api;
import com.zxjk.duoduo.network.ServiceFactory;
import com.zxjk.duoduo.network.rx.RxSchedulers;
import com.zxjk.duoduo.ui.base.BaseActivity;
import com.zxjk.duoduo.ui.widget.NewPayBoard;
import com.zxjk.duoduo.utils.CommonUtils;
import com.zxjk.duoduo.utils.GlideUtil;
import com.zxjk.duoduo.utils.MD5Utils;

public class CreateWalletActivity extends BaseActivity {
    private TextView tvTips;
    private RecyclerView recycler;
    private BaseQuickAdapter adapter;

    private boolean isImport;

    @SuppressLint("CheckResult")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_wallet);

        isImport = getIntent().getBooleanExtra("isImport", false);

        TextView title = findViewById(R.id.tv_title);
        tvTips = findViewById(R.id.tvTips);
        if (isImport) {
            title.setText(R.string.importwallet);
            tvTips.setText(R.string.createwallet_tips5);
        } else {
            title.setText(R.string.createwallet);
            tvTips.setText(R.string.createwallet_tips1);
        }
        findViewById(R.id.rl_back).setOnClickListener(v -> finish());

        recycler = findViewById(R.id.recycler);
        adapter = new BaseQuickAdapter<CurrencyInfosByCustomerBean, BaseViewHolder>(R.layout.item_createwallet) {
            @Override
            protected void convert(BaseViewHolder helper, CurrencyInfosByCustomerBean item) {
                helper.setText(R.id.tvCoin, item.getCoin());
                GlideUtil.loadCircleImg(helper.getView(R.id.ivIcon), item.getLogo());
                View view = helper.getView(R.id.ivCreated);

                if (!isImport) {
                    if (item.getIsAlready().equals("1")) {
                        view.setVisibility(View.VISIBLE);
                    } else {
                        view.setVisibility(View.INVISIBLE);
                    }
                }
            }
        };
        recycler.setAdapter(adapter);
        recycler.setLayoutManager(new LinearLayoutManager(this));

        adapter.setOnItemClickListener((adapter, view, position) -> {
            CurrencyInfosByCustomerBean item = (CurrencyInfosByCustomerBean) adapter.getData().get(position);
            if (!isImport) {
                if (item.getIsDelete().equals("1")) {
                    ToastUtils.showShort(R.string.createwallet_coinunable);
                    return;
                }
                if (item.getIsAlready().equals("1")) {
                    return;
                }
                new NewPayBoard(CreateWalletActivity.this).show(pwd -> ServiceFactory.getInstance().getBaseService(Api.class)
                        .checkWalletPwd(MD5Utils.getMD5(pwd))
                        .compose(bindToLifecycle())
                        .compose(RxSchedulers.normalTrans())
                        .compose(RxSchedulers.ioObserver(CommonUtils.initDialog(this)))
                        .subscribe(s -> {
                            Intent intent = new Intent(CreateWalletActivity.this, CreateingWalletActivity.class);
                            intent.putExtra("pwd", pwd);
                            intent.putExtra("symbol", item.getCoin());
                            startActivity(intent);
                            finish();
                        }, this::handleApiError));
            } else {
                Intent intent = new Intent(this, ImportWalletActivity.class);
                intent.putExtra("symbol", item.getCoin());
                startActivity(intent);
                finish();
            }
        });

        ServiceFactory.getInstance().getBaseService(Api.class)
                .currencyInfosByCustomerId("0")
                .compose(bindToLifecycle())
                .compose(RxSchedulers.ioObserver(CommonUtils.initDialog(this)))
                .compose(RxSchedulers.normalTrans())
                .subscribe(adapter::setNewData, this::handleApiError);
    }
}

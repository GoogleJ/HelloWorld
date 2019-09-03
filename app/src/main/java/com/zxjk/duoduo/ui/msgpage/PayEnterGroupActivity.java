package com.zxjk.duoduo.ui.msgpage;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Switch;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.blankj.utilcode.util.ToastUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.zxjk.duoduo.R;
import com.zxjk.duoduo.bean.response.GetGroupPayInfoResponse;
import com.zxjk.duoduo.network.Api;
import com.zxjk.duoduo.network.ServiceFactory;
import com.zxjk.duoduo.network.rx.RxSchedulers;
import com.zxjk.duoduo.ui.base.BaseActivity;
import com.zxjk.duoduo.ui.widget.dialog.PayEnterDialog;
import com.zxjk.duoduo.utils.CommonUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class PayEnterGroupActivity extends BaseActivity {

    private SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
    private SimpleDateFormat sdf2 = new SimpleDateFormat("HH:mm:ss");

    private String groupId;

    private TextView tvTitle;
    private Switch switchOpen;
    private TextView tvMoney;
    private TextView tvTotalMoney;
    private TextView tvCount;
    private RecyclerView recycler;
    private BaseQuickAdapter<GetGroupPayInfoResponse.GroupPayListBean, BaseViewHolder> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_enter_group);

        initView();
        initData();
    }

    private void initView() {
        tvTitle = findViewById(R.id.tv_title);
        switchOpen = findViewById(R.id.switchOpen);
        tvMoney = findViewById(R.id.tvMoney);
        tvTotalMoney = findViewById(R.id.tvTotalMoney);
        tvCount = findViewById(R.id.tvCount);
        recycler = findViewById(R.id.recycler);
    }

    @SuppressLint("CheckResult")
    private void initData() {
        groupId = getIntent().getStringExtra("groupId");
        findViewById(R.id.rl_back).setOnClickListener(v -> finish());
        tvTitle.setText(R.string.pay_enter_group);

        initRecycler();

        switchOpen.setOnClickListener(v -> {
            if (tvMoney.getText().toString().equals("0") && switchOpen.isChecked()) {
                ToastUtils.showShort(R.string.setmoneyfirst);
                switchOpen.setChecked(false);
                return;
            }

            ServiceFactory.getInstance().getBaseService(Api.class)
                    .groupPayInfo(groupId, tvMoney.getText().toString(), switchOpen.isChecked() ? "1" : "0")
                    .compose(bindToLifecycle())
                    .compose(RxSchedulers.ioObserver(CommonUtils.initDialog(PayEnterGroupActivity.this)))
                    .compose(RxSchedulers.normalTrans())
                    .subscribe(s -> {
                                if (switchOpen.isChecked()) {
                                    ToastUtils.showShort(R.string.open_payenter_success);
                                } else {
                                    ToastUtils.showShort(R.string.close_payenter_success);
                                }
                            },
                            t -> {
                                handleApiError(t);
                                switchOpen.setChecked(!switchOpen.isChecked());
                            });
        });

        ServiceFactory.getInstance().getBaseService(Api.class)
                .getGroupPayInfo(groupId)
                .compose(bindToLifecycle())
                .compose(RxSchedulers.ioObserver(CommonUtils.initDialog(this)))
                .compose(RxSchedulers.normalTrans())
                .subscribe(r -> {
                    switchOpen.setChecked(r.getIsOpen().equals("1"));

                    tvMoney.setText(r.getPayFee());
                    tvTotalMoney.setText(r.getSumPayFee());
                    tvCount.setText(r.getPayFeeNumbers());

                    adapter.setNewData(r.getGroupPayList());
                }, this::handleApiError);
    }

    private void initRecycler() {
        adapter = new BaseQuickAdapter<GetGroupPayInfoResponse.GroupPayListBean, BaseViewHolder>(R.layout.item_pay_entergroup, new ArrayList<>(0)) {
            @Override
            protected void convert(BaseViewHolder helper, GetGroupPayInfoResponse.GroupPayListBean item) {
                String time = item.getCreateTime();
                helper.setText(R.id.tvNick, item.getNick())
                        .setText(R.id.tvMoney, item.getPayMot())
                        .setText(R.id.tvTime1, sdf1.format(Long.parseLong(time)))
                        .setText(R.id.tvTime2, sdf2.format(Long.parseLong(time)));
            }
        };
        TextView emptyView = new TextView(this);
        emptyView.setGravity(Gravity.CENTER);
        emptyView.setText("暂无付费入群信息");
        emptyView.setTextSize(15);
        emptyView.setTextColor(ContextCompat.getColor(this, R.color.textcolor2));

        adapter.setEmptyView(emptyView);
        recycler.setLayoutManager(new LinearLayoutManager(this));
        recycler.setAdapter(adapter);
    }

    public void setMoney(View view) {
        if (switchOpen.isChecked()) {
            ToastUtils.showShort(R.string.close_payenter_first);
            return;
        }
        PayEnterDialog payEnterDialog = new PayEnterDialog(this);
        payEnterDialog.setOnCommitClick(str -> {
            payEnterDialog.dismiss();
            tvMoney.setText(str);
        });
        payEnterDialog.show();
    }
}

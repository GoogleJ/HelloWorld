package com.zxjk.moneyspace.ui.msgpage;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.blankj.utilcode.util.ToastUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.zxjk.moneyspace.R;
import com.zxjk.moneyspace.bean.response.BaseResponse;
import com.zxjk.moneyspace.bean.response.GetGroupPayInfoResponse;
import com.zxjk.moneyspace.bean.response.GetPaymentListBean;
import com.zxjk.moneyspace.network.Api;
import com.zxjk.moneyspace.network.ServiceFactory;
import com.zxjk.moneyspace.network.rx.RxSchedulers;
import com.zxjk.moneyspace.ui.base.BaseActivity;
import com.zxjk.moneyspace.ui.minepage.wallet.ChooseCoinActivity;
import com.zxjk.moneyspace.ui.widget.dialog.PayEnterDialog;
import com.zxjk.moneyspace.utils.CommonUtils;
import com.zxjk.moneyspace.utils.GlideUtil;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.functions.Function;
import io.rong.imkit.RongIM;
import io.rong.imlib.IRongCallback;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.Message;
import io.rong.message.InformationNotificationMessage;

public class PayEnterGroupActivity extends BaseActivity {
    private GetPaymentListBean result;
    private ArrayList<GetPaymentListBean> list = new ArrayList<>();

    private SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
    private SimpleDateFormat sdf2 = new SimpleDateFormat("HH:mm:ss");

    private String groupId;

    private TextView tvTitle;
    private Switch switchOpen;
    private TextView tvMoney;
    private TextView tvCount;
    private RecyclerView recycler;
    private BaseQuickAdapter<GetGroupPayInfoResponse.GroupPayListBean, BaseViewHolder> adapter;

    private ImageView ivCoinIcon;
    private TextView tvCoin;
    private TextView tvUnit;

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
        tvCount = findViewById(R.id.tvCount);
        recycler = findViewById(R.id.recycler);
        ivCoinIcon = findViewById(R.id.ivCoinIcon);
        tvCoin = findViewById(R.id.tvCoin);
        tvUnit = findViewById(R.id.tvUnit);
    }

    @SuppressLint("CheckResult")
    private void initData() {
        groupId = getIntent().getStringExtra("groupId");
        findViewById(R.id.rl_back).setOnClickListener(v -> finish());
        tvTitle.setText(R.string.pay_enter_group);

        initRecycler();

        Api api = ServiceFactory.getInstance().getBaseService(Api.class);
        switchOpen.setOnClickListener(v -> {
            if (tvMoney.getText().toString().equals("0") && switchOpen.isChecked()) {
                ToastUtils.showShort(R.string.setmoneyfirst);
                switchOpen.setChecked(false);
                return;
            }
            if (result == null) {
                ToastUtils.showShort(R.string.select_cointype);
                return;
            }

            api.groupPayInfo(groupId, tvMoney.getText().toString(), switchOpen.isChecked() ? "1" : "0", result.getSymbol())
                    .compose(bindToLifecycle())
                    .compose(RxSchedulers.ioObserver(CommonUtils.initDialog(PayEnterGroupActivity.this)))
                    .compose(RxSchedulers.normalTrans())
                    .subscribe(s -> {
                                String text;
                                if (switchOpen.isChecked()) {
                                    ToastUtils.showShort(R.string.open_payenter_success);
                                    text = getString(R.string.open_payenter_success);
                                } else {
                                    ToastUtils.showShort(R.string.close_payenter_success);
                                    text = getString(R.string.close_payenter_success);
                                }
                                InformationNotificationMessage notificationMessage = InformationNotificationMessage.obtain(text);
                                Message message = Message.obtain(groupId, Conversation.ConversationType.GROUP, notificationMessage);
                                RongIM.getInstance().sendMessage(message, "", "", (IRongCallback.ISendMessageCallback) null);
                            },
                            t -> {
                                handleApiError(t);
                                switchOpen.setChecked(!switchOpen.isChecked());
                            });
        });

        api.getGroupPayInfo(groupId)
                .compose(bindToLifecycle())
                .compose(RxSchedulers.normalTrans())
                .flatMap((Function<GetGroupPayInfoResponse, Observable<BaseResponse<List<GetPaymentListBean>>>>) r -> {
                    runOnUiThread(() -> {
                        if (!TextUtils.isEmpty(r.getSymbol())) {
                            result = new GetPaymentListBean();
                            result.setLogo(r.getLogo());
                            result.setSymbol(r.getSymbol());
                        }
                        switchOpen.setChecked(r.getIsOpen().equals("1"));

                        tvMoney.setText(r.getPayFee());
                        tvCount.setText(r.getPayFeeNumbers());

                        adapter.setNewData(r.getGroupPayList());
                    });
                    return api.getPaymentList();
                })
                .compose(RxSchedulers.normalTrans())
                .compose(RxSchedulers.ioObserver(CommonUtils.initDialog(this)))
                .subscribe(l -> {
                    list.addAll(l);
                    if (result == null) {
                        result = list.get(0);
                    }
                    GlideUtil.loadCircleImg(ivCoinIcon, result.getLogo());
                    tvCoin.setText(result.getSymbol());
                    tvUnit.setText(result.getSymbol());
                }, t -> {
                    handleApiError(t);
                    finish();
                });
    }

    private void initRecycler() {
        adapter = new BaseQuickAdapter<GetGroupPayInfoResponse.GroupPayListBean, BaseViewHolder>(R.layout.item_pay_entergroup, new ArrayList<>(0)) {
            @Override
            protected void convert(BaseViewHolder helper, GetGroupPayInfoResponse.GroupPayListBean item) {
                String time = item.getCreateTime();
                helper.setText(R.id.tvNick, item.getNick())
                        .setText(R.id.tvMoney, item.getPayMot())
                        .setText(R.id.tvTime1, sdf1.format(Long.parseLong(time)))
                        .setText(R.id.tvTime2, sdf2.format(Long.parseLong(time)))
                        .setText(R.id.tvUnit, item.getSymbol());
            }
        };
        TextView emptyView = new TextView(this);
        emptyView.setGravity(Gravity.CENTER);
        emptyView.setText(R.string.no_enter_red_data);
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
            if (Float.parseFloat(str) == 0) {
                ToastUtils.showShort(R.string.input_money1);
                return;
            }
            tvMoney.setText(str);
        });
        payEnterDialog.show();
    }

    public void chooseCoin(View view) {
        if (switchOpen.isChecked()) {
            ToastUtils.showShort(R.string.close_payenter_first);
            return;
        }
        Intent intent = new Intent(this, ChooseCoinActivity.class);
        intent.putExtra("data", list);
        startActivityForResult(intent, 1);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data == null) return;
        if (requestCode == 1 && resultCode == 1) {
            result = data.getParcelableExtra("result");
            GlideUtil.loadCircleImg(ivCoinIcon, result.getLogo());
            tvCoin.setText(result.getSymbol());
            tvUnit.setText(result.getSymbol());
        }
    }
}

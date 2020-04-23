package com.zxjk.moneyspace.ui.minepage;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.transition.Fade;
import androidx.transition.TransitionManager;

import com.zxjk.moneyspace.R;
import com.zxjk.moneyspace.bean.response.GetCustomerBankInfoResponse;
import com.zxjk.moneyspace.network.Api;
import com.zxjk.moneyspace.network.ServiceFactory;
import com.zxjk.moneyspace.network.rx.RxSchedulers;
import com.zxjk.moneyspace.ui.base.BaseActivity;
import com.zxjk.moneyspace.ui.walletpage.BindCNYCardActivity;
import com.zxjk.moneyspace.ui.widget.NewPayBoard;
import com.zxjk.moneyspace.utils.CommonUtils;
import com.zxjk.moneyspace.utils.MD5Utils;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.functions.Function;

public class BankCardActivity extends BaseActivity {
    private TextView tvBank;
    private TextView tvBankNum;
    private TextView tv_commit;

    private LinearLayout llEmpty;
    private LinearLayout llBank;

    private LinearLayout llroot;

    @SuppressLint("CheckResult")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bank_card);

        llroot = findViewById(R.id.llroot);

        TextView title = findViewById(R.id.tv_title);
        tv_commit = findViewById(R.id.tv_commit);
        title.setText(R.string.bank_card);
        tv_commit.setText(R.string.unbind);
        findViewById(R.id.rl_back).setOnClickListener(v -> finish());
        tv_commit.setOnClickListener(v -> new NewPayBoard(this).show(result -> {
            ServiceFactory.getInstance().getBaseService(Api.class)
                    .deleteCustomerBankInfo(MD5Utils.getMD5(result))
                    .compose(bindToLifecycle())
                    .compose(RxSchedulers.normalTrans())
                    .compose(RxSchedulers.ioObserver(CommonUtils.initDialog(this)))
                    .subscribe(s -> {
                        Fade fade = new Fade();
                        fade.setDuration(200);
                        TransitionManager.beginDelayedTransition(llroot, fade);

                        llBank.setVisibility(View.GONE);
                        llEmpty.setVisibility(View.VISIBLE);
                        tv_commit.setVisibility(View.GONE);
                    }, this::handleApiError);
        }));

        tvBank = findViewById(R.id.tvBank);
        tvBankNum = findViewById(R.id.tvBankNum);
        llEmpty = findViewById(R.id.llEmpty);
        llBank = findViewById(R.id.llBank);

        ServiceFactory.getInstance().getBaseService(Api.class)
                .isBandBankInfo()
                .compose(bindToLifecycle())
                .compose(RxSchedulers.normalTrans())
                .flatMap((Function<String, ObservableSource<GetCustomerBankInfoResponse>>) s -> {
                    if (!"0".equals(s)) {
                        return ServiceFactory.getInstance().getBaseService(Api.class).getCustomerBankInfo().compose(RxSchedulers.normalTrans());
                    } else {
                        return Observable.empty();
                    }
                })
                .compose(RxSchedulers.ioObserver(CommonUtils.initDialog(this, 0)))
                .subscribe(r -> {
                    if (r == null) return;

                    Fade fade = new Fade();
                    fade.setDuration(200);
                    TransitionManager.beginDelayedTransition(llroot, fade);

                    llEmpty.setVisibility(View.GONE);
                    llBank.setVisibility(View.VISIBLE);
                    tv_commit.setVisibility(View.VISIBLE);

                    tvBank.setText(r.getBank());
                    tvBankNum.setText("**** " + r.getBankNum().substring(r.getBankNum().length() - 4));

                }, this::handleApiError);

    }

    public void bind(View view) {
        Intent intent = new Intent(this, BindCNYCardActivity.class);
        intent.putExtra("fromBind", true);
        startActivityForResult(intent, 1);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (data == null) return;
        if (requestCode == 1 && resultCode == 1) {
            String bank = data.getStringExtra("bank");
            String num = data.getStringExtra("num");

            Fade fade = new Fade();
            fade.setDuration(200);
            TransitionManager.beginDelayedTransition(llroot, fade);

            llEmpty.setVisibility(View.GONE);
            llBank.setVisibility(View.VISIBLE);

            tvBank.setText(bank);
            tvBankNum.setText("**** " + num.substring(num.length() - 4));

            tv_commit.setVisibility(View.VISIBLE);
        }
    }
}

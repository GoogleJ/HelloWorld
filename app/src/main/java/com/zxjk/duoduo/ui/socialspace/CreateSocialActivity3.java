package com.zxjk.duoduo.ui.socialspace;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.blankj.utilcode.util.ToastUtils;
import com.zxjk.duoduo.R;
import com.zxjk.duoduo.bean.response.GetPaymentListBean;
import com.zxjk.duoduo.network.Api;
import com.zxjk.duoduo.network.ServiceFactory;
import com.zxjk.duoduo.network.rx.RxSchedulers;
import com.zxjk.duoduo.ui.base.BaseActivity;
import com.zxjk.duoduo.ui.minepage.wallet.ChooseCoinActivity;
import com.zxjk.duoduo.utils.CommonUtils;
import com.zxjk.duoduo.utils.GlideUtil;
import com.zxjk.duoduo.utils.MoneyValueFilter;

import java.util.ArrayList;

public class CreateSocialActivity3 extends BaseActivity {

    private GetPaymentListBean result;
    private ArrayList<GetPaymentListBean> list = new ArrayList<>();

    private EditText etMoney;
    private ImageView ivLogo;
    private TextView tvCoin;

    @SuppressLint("CheckResult")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_social3);

        TextView title = findViewById(R.id.tv_title);
        title.setText(R.string.set_money1);
        findViewById(R.id.rl_back).setOnClickListener(v -> finish());

        etMoney = findViewById(R.id.etMoney);
        ivLogo = findViewById(R.id.ivLogo);
        tvCoin = findViewById(R.id.tvCoin);
        etMoney.setFilters(new InputFilter[]{new MoneyValueFilter().setDigits(5)});


        ServiceFactory.getInstance().getBaseService(Api.class).getPaymentList()
                .compose(RxSchedulers.normalTrans())
                .compose(RxSchedulers.ioObserver(CommonUtils.initDialog(this)))
                .compose(bindToLifecycle())
                .subscribe(l -> {
                    list.addAll(l);
                    result = list.get(0);
                    GlideUtil.loadCircleImg(ivLogo, result.getLogo());
                    tvCoin.setText(result.getSymbol());
                }, t -> {
                    handleApiError(t);
                    finish();
                });
    }

    public void chooseCoin(View view) {
        Intent intent = new Intent(this, ChooseCoinActivity.class);
        intent.putExtra("data", list);
        startActivityForResult(intent, 1);
    }

    public void nextStep(View view) {
        if (result == null) {
            ToastUtils.showShort(R.string.select_cointype);
            return;
        }

        String money = etMoney.getText().toString().trim();
        if (TextUtils.isEmpty(money)) {
            ToastUtils.showShort(R.string.tips_create_social1);
            return;
        }

        startActivity(new Intent(this, CreateSocialActivity4.class));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data == null) return;
        if (requestCode == 1 && resultCode == 1) {
            result = data.getParcelableExtra("result");
            GlideUtil.loadCircleImg(ivLogo, result.getLogo());
            tvCoin.setText(result.getSymbol());
        }
    }

}

package com.zxjk.duoduo.ui.wallet;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.TextView;

import com.blankj.utilcode.util.ToastUtils;
import com.zxjk.duoduo.R;
import com.zxjk.duoduo.bean.response.GetLinkCoinOrdersOrderDetails;
import com.zxjk.duoduo.network.Api;
import com.zxjk.duoduo.network.ServiceFactory;
import com.zxjk.duoduo.network.rx.RxSchedulers;
import com.zxjk.duoduo.ui.base.BaseActivity;

public class TheAppealActivity extends BaseActivity {
    private GetLinkCoinOrdersOrderDetails byBoinsResponse;

    private EditText etSocialSlogan;
    private TextView tvSubmit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_the_complaint);

        initView();

        initData();
    }

    private void initView() {
        etSocialSlogan = findViewById(R.id.etSocialSlogan);
        tvSubmit = findViewById(R.id.tv_submit);
        findViewById(R.id.rl_back).setOnClickListener(v -> finish());
    }

    @SuppressLint("CheckResult")
    private void initData() {
        byBoinsResponse = (GetLinkCoinOrdersOrderDetails) getIntent().getSerializableExtra("ByBoinsResponse");

        tvSubmit.setOnClickListener(v -> {
            if (TextUtils.isEmpty(etSocialSlogan.getText())) {
                ToastUtils.showShort(R.string.appeal_message);
                return;
            }

            ServiceFactory.getInstance().getBaseService(Api.class)
                    .dispute(byBoinsResponse.getOtherOrderId(), etSocialSlogan.getText().toString())
                    .compose(RxSchedulers.otc())
                    .compose(RxSchedulers.ioObserver())
                    .compose(bindToLifecycle())
                    .subscribe(data -> {
                        ToastUtils.showShort("申诉成功");
                        finish();
                    }, this::handleApiError);
        });
    }
}
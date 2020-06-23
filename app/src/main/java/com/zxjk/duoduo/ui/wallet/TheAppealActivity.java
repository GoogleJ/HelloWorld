package com.zxjk.duoduo.ui.wallet;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.TextView;

import com.blankj.utilcode.util.ToastUtils;
import com.zxjk.duoduo.R;
import com.zxjk.duoduo.bean.response.GetLinkCoinOrdersOrderDetails;
import com.zxjk.duoduo.network.Api;
import com.zxjk.duoduo.network.ServiceFactory;
import com.zxjk.duoduo.network.rx.RxSchedulers;
import com.zxjk.duoduo.ui.base.BaseActivity;
import com.zxjk.duoduo.utils.EmojiUtils;

public class TheAppealActivity extends BaseActivity {
    private GetLinkCoinOrdersOrderDetails byBoinsResponse;

    private EditText etSocialSlogan;
    private TextView tvSubmit;
    private TextView tv1;

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
        tv1 = findViewById(R.id.tv1);
        findViewById(R.id.rl_back).setOnClickListener(v -> finish());
    }

    @SuppressLint("CheckResult")
    private void initData() {

        etSocialSlogan.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (EmojiUtils.containsEmoji(s.toString())) {
                    ToastUtils.showShort("禁止输入表情");
                    if (!s.equals("")) {//判断输入框不为空，执行删除
                        int index = etSocialSlogan.getSelectionStart();   //获取Edittext光标所在位置
                        etSocialSlogan.getText().delete(index - 1, index);
                    }
                }
                tv1.setText(s.length() + "/140");
            }
        });
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
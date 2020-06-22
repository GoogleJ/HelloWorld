package com.zxjk.duoduo.ui.minepage;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Switch;
import android.widget.TextView;

import com.zxjk.duoduo.Constant;
import com.zxjk.duoduo.R;
import com.zxjk.duoduo.network.Api;
import com.zxjk.duoduo.network.ServiceFactory;
import com.zxjk.duoduo.network.rx.RxSchedulers;
import com.zxjk.duoduo.ui.base.BaseActivity;
import com.zxjk.duoduo.utils.CommonUtils;
import com.zxjk.duoduo.utils.MMKVUtils;

public class PrivicyActivity extends BaseActivity {
    private Switch switch1;
    private Switch switch2;
    private Switch switch3;

    @SuppressLint("CheckResult")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privicy);

        TextView tv_title = findViewById(R.id.tv_title);
        tv_title.setText(R.string.pricacy);

        findViewById(R.id.llPrivicyAddWays).setOnClickListener(v -> startActivity(new Intent(this, AddWaysActivity.class)));

        findViewById(R.id.rl_back).setOnClickListener(v -> finish());

        switch1 = findViewById(R.id.switch1);
        switch2 = findViewById(R.id.switch2);
        switch3 = findViewById(R.id.switch3);

        if ("1".equals(Constant.currentUser.getIsShowRealname())) {
            switch2.setChecked(false);
        } else {
            switch2.setChecked(true);
        }

        switch2.setOnClickListener(v -> ServiceFactory.getInstance().getBaseService(Api.class)
                .operateRealName("1".equals(Constant.currentUser.getIsShowRealname()) ? "0" : "1")
                .compose(bindToLifecycle())
                .compose(RxSchedulers.normalTrans())
                .compose(RxSchedulers.ioObserver(CommonUtils.initDialog(this)))
                .subscribe(s -> {
                    Constant.currentUser.setIsShowRealname("1".equals(Constant.currentUser.getIsShowRealname()) ? "0" : "1");
                    MMKVUtils.getInstance().enCode("login", Constant.currentUser);
                }, this::handleApiError));
    }

    @Override
    public void handleApiError(Throwable throwable) {
        if ("1".equals(Constant.currentUser.getIsShowRealname())) {
            switch2.setChecked(false);
        } else {
            switch2.setChecked(true);
        }
        super.handleApiError(throwable);
    }
}

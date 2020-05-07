package com.zxjk.moneyspace.ui.minepage;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Switch;
import android.widget.TextView;

import com.zxjk.moneyspace.Constant;
import com.zxjk.moneyspace.R;
import com.zxjk.moneyspace.network.Api;
import com.zxjk.moneyspace.network.ServiceFactory;
import com.zxjk.moneyspace.network.rx.RxSchedulers;
import com.zxjk.moneyspace.ui.base.BaseActivity;
import com.zxjk.moneyspace.utils.CommonUtils;
import com.zxjk.moneyspace.utils.MMKVUtils;

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
                .operateRealName(Constant.currentUser.getIsShowRealname().equals("1") ? "0" : "1")
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

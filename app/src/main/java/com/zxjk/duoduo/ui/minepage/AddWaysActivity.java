package com.zxjk.duoduo.ui.minepage;

import android.annotation.SuppressLint;
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

public class AddWaysActivity extends BaseActivity {

    private Switch switch1;
    private Switch switch2;
    private Switch switch3;
    private Switch switch4;
    private Switch switch5;

    @SuppressLint("CheckResult")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_ways);

        TextView tv_title = findViewById(R.id.tv_title);
        tv_title.setText(R.string.m_add_my_way_title_bar);

        switch1 = findViewById(R.id.switch1);
        switch2 = findViewById(R.id.switch2);
        switch3 = findViewById(R.id.switch3);
        switch4 = findViewById(R.id.switch4);
        switch5 = findViewById(R.id.switch5);

        findViewById(R.id.rl_back).setOnClickListener(v -> finish());

        if (Constant.currentUser.getOpenPhone() != null) {
            if (Constant.currentUser.getOpenPhone().equals("1")) {
                switch2.setChecked(true);
            } else {
                switch2.setChecked(false);
            }
        } else {
            Constant.currentUser.setOpenPhone("1");
            switch2.setChecked(true);
        }

        switch2.setOnClickListener(v -> ServiceFactory.getInstance().getBaseService(Api.class)
                .operateOpenPhone(Constant.currentUser.getOpenPhone().equals("0") ? "1" : "0")
                .compose(bindToLifecycle())
                .compose(RxSchedulers.normalTrans())
                .compose(RxSchedulers.ioObserver(CommonUtils.initDialog(this)))
                .subscribe(s -> {
                    Constant.currentUser.setOpenPhone(Constant.currentUser.getOpenPhone().equals("0") ? "1" : "0");
                    MMKVUtils.getInstance().enCode("login", Constant.currentUser);
                }, this::handleApiError));
    }

    @Override
    public void handleApiError(Throwable throwable) {
        if (Constant.currentUser.getOpenPhone().equals("1")) {
            switch2.setChecked(true);
        } else {
            switch2.setChecked(false);
        }
        super.handleApiError(throwable);
    }
}

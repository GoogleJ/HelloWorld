package com.zxjk.moneyspace.ui.minepage;

import android.annotation.SuppressLint;
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
    private Switch switch2;
    private Switch switch3;

    @SuppressLint("CheckResult")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privicy);

        TextView tv_title = findViewById(R.id.tv_title);
        tv_title.setText(R.string.pricacy);

        switch3 = findViewById(R.id.switch3);

        findViewById(R.id.rl_back).setOnClickListener(v -> finish());

        switch2 = findViewById(R.id.switch2);

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


        if (Constant.currentUser.getOpenPhone() != null) {
            if (Constant.currentUser.getOpenPhone().equals("1")) {
                switch3.setChecked(true);
            } else {
                switch3.setChecked(false);
            }
        } else {
            Constant.currentUser.setOpenPhone("1");
            switch3.setChecked(true);
        }

        switch3.setOnClickListener(v -> ServiceFactory.getInstance().getBaseService(Api.class)
                .operateOpenPhone(Constant.currentUser.getOpenPhone().equals("0") ? "1" : "0")
                .compose(bindToLifecycle())
                .compose(RxSchedulers.normalTrans())
                .compose(RxSchedulers.ioObserver(CommonUtils.initDialog(this)))
                .subscribe(s -> {
                    Constant.currentUser.setOpenPhone(Constant.currentUser.getOpenPhone().equals("0") ? "1" : "0");
                    MMKVUtils.getInstance().enCode("login", Constant.currentUser);
                }, t -> {
                    if (Constant.currentUser.getOpenPhone().equals("1")) {
                        switch3.setChecked(true);
                    } else {
                        switch3.setChecked(false);
                    }
                    super.handleApiError(t);
                }));
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

package com.zxjk.moneyspace.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;

import com.blankj.utilcode.util.BarUtils;
import com.blankj.utilcode.util.RegexUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.zxjk.moneyspace.R;
import com.zxjk.moneyspace.network.Api;
import com.zxjk.moneyspace.network.ServiceFactory;
import com.zxjk.moneyspace.network.rx.RxSchedulers;
import com.zxjk.moneyspace.ui.base.BaseActivity;
import com.zxjk.moneyspace.utils.CommonUtils;

public class NewLoginActivity1 extends BaseActivity {
    private EditText et;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        BarUtils.setStatusBarVisibility(this, false);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            WindowManager.LayoutParams lp = getWindow().getAttributes();
            lp.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
            getWindow().setAttributes(lp);
        }
        setContentView(R.layout.activity_new_login1);

        et = findViewById(R.id.et);
    }

    @SuppressLint("CheckResult")
    public void code(View view) {
        if (!"".equals(et.getText().toString().trim())
                && RegexUtils.isEmail(et.getText().toString().trim())) {
            ServiceFactory.getInstance().getBaseService(Api.class)
                    .getEmailCode(et.getText().toString().trim())
                    .compose(bindToLifecycle())
                    .compose(RxSchedulers.normalTrans())
                    .compose(RxSchedulers.ioObserver(CommonUtils.initDialog(this, 0)))
                    .subscribe(s -> {
                        Intent intent = new Intent(this, GetCodeActivity.class);
                        intent.putExtra("email", et.getText().toString().trim());
                        startActivity(intent);
                    }, this::handleApiError);

        } else {
            ToastUtils.showShort(R.string.input_wrong);
        }
    }

    public void phone(View view) {
        startActivity(new Intent(this, NewLoginActivity.class));
        finish();
    }

    public void back(View view) {
        finish();
    }

}

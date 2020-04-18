package com.zxjk.moneyspace.ui;

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
import com.zxjk.moneyspace.ui.base.BaseActivity;

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

    public void code(View view) {
        if (!"".equals(et.getText().toString().trim())
                && RegexUtils.isEmail(et.getText().toString().trim())) {
            Intent intent = new Intent(this, GetCodeActivity.class);
            intent.putExtra("email", et.getText().toString().trim());
            startActivity(intent);
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

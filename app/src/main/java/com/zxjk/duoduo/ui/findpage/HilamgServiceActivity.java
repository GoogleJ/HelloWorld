package com.zxjk.duoduo.ui.findpage;

import android.os.Bundle;
import android.view.View;

import com.blankj.utilcode.util.ToastUtils;
import com.zxjk.duoduo.R;
import com.zxjk.duoduo.ui.base.BaseActivity;

public class HilamgServiceActivity extends BaseActivity implements View.OnClickListener {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hilamg_service);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv1:
            case R.id.tv2:
            case R.id.tv3:
            case R.id.tv4:
            case R.id.tv5:
            case R.id.tv6:
            case R.id.tv7:
            case R.id.tv8:
            case R.id.tv9:
                ToastUtils.showShort(getString(R.string.toast1));
                break;
        }
    }
}

package com.zxjk.duoduo.ui.minepage;

import android.os.Bundle;
import android.transition.Slide;
import android.view.Gravity;
import android.view.View;

import com.blankj.utilcode.util.ScreenUtils;
import com.zxjk.duoduo.R;
import com.zxjk.duoduo.ui.base.BaseActivity;

public class ConfirmRedFallActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ScreenUtils.setFullScreen(this);
        setContentView(R.layout.activity_confirm_red_fall);

        Slide slide = new Slide(Gravity.BOTTOM);
        slide.setDuration(500);
        slide.setStartDelay(300);

        getWindow().setEnterTransition(slide);
    }

    public void back(View view) {

    }

    public void share(View view) {

    }

}

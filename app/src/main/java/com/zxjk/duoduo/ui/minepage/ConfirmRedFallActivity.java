package com.zxjk.duoduo.ui.minepage;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.transition.Slide;
import android.transition.TransitionManager;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.DecelerateInterpolator;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.blankj.utilcode.util.BarUtils;
import com.zxjk.duoduo.R;
import com.zxjk.duoduo.ui.base.BaseActivity;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;

public class ConfirmRedFallActivity extends BaseActivity {

    private LinearLayout llContent;
    private LinearLayout llAnim1;
    private LinearLayout llAnim2;
    private LinearLayout llAnim3;
    private LinearLayout llAnim4;
    private TextView tvAnim5;

    @SuppressLint("CheckResult")
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

        setContentView(R.layout.activity_confirm_red_fall);

        llContent = findViewById(R.id.llContent);
        llAnim1 = findViewById(R.id.llAnim1);
        llAnim2 = findViewById(R.id.llAnim2);
        llAnim3 = findViewById(R.id.llAnim3);
        llAnim4 = findViewById(R.id.llAnim4);
        tvAnim5 = findViewById(R.id.tvAnim5);

        Observable.timer(250, TimeUnit.MILLISECONDS, AndroidSchedulers.mainThread())
                .compose(bindToLifecycle())
                .subscribe(a -> {
                    Slide slide = new Slide(Gravity.BOTTOM);
                    slide.setInterpolator(new DecelerateInterpolator());
                    slide.setDuration(700);
                    TransitionManager.beginDelayedTransition(llContent, slide);

                    llAnim1.setVisibility(View.VISIBLE);
                    llAnim2.setVisibility(View.VISIBLE);
                    llAnim3.setVisibility(View.VISIBLE);
                    llAnim4.setVisibility(View.VISIBLE);
                    tvAnim5.setVisibility(View.VISIBLE);
                });
    }

    public void back(View view) {
        finish();
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.redfallconfirm_enteranim, R.anim.redfallconfirm_exitanim);
    }

    public void share(View view) {

    }

}

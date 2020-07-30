package com.zxjk.duoduo.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.transition.ArcMotion;
import android.transition.ChangeBounds;
import android.transition.Transition;
import android.transition.TransitionManager;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewPropertyAnimator;
import android.view.WindowManager;
import android.view.animation.OvershootInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.vectordrawable.graphics.drawable.ArgbEvaluator;

import com.blankj.utilcode.util.BarUtils;
import com.zxjk.duoduo.R;
import com.zxjk.duoduo.ui.base.BaseActivity;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;

public class AppFirstLogin extends BaseActivity {

    private FrameLayout flContent;
    private View viewContent;
    private ImageView ivIcon;
    private LinearLayout llIcon;
    private TextView tvTips1;
    private TextView tvTips2;

    private ChangeBounds mediumAnim;

    private ArgbEvaluator argbEvaluator = new ArgbEvaluator();
    private int START_COLOR;
    private int END_COLOR;
    private boolean setupPayPass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setEnableCheckConstant(false);
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        BarUtils.setStatusBarVisibility(this, false);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            WindowManager.LayoutParams lp = getWindow().getAttributes();
            lp.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
            getWindow().setAttributes(lp);
        }
        setContentView(R.layout.activity_app_first_login);

        START_COLOR = ContextCompat.getColor(this, R.color.white);
        END_COLOR = ContextCompat.getColor(this, R.color.colorTheme);

        setupPayPass = getIntent().getBooleanExtra("setupPayPass", false);

        initView();

        mediumAnim();

        getWindow().setSharedElementEnterTransition(initTransition());
    }

    private void initView() {
        flContent = findViewById(R.id.flContent);
        viewContent = findViewById(R.id.viewContent);
        ivIcon = findViewById(R.id.ivIcon);
        llIcon = findViewById(R.id.llIcon);
        tvTips1 = findViewById(R.id.tvTips1);
        tvTips2 = findViewById(R.id.tvTips2);
    }

    private Transition initTransition() {
        ArcMotion arcMotion = new ArcMotion();
        arcMotion.setMaximumAngle(90f);
        arcMotion.setMinimumVerticalAngle(0f);
        arcMotion.setMinimumHorizontalAngle(90f);

        ChangeBounds changeBounds = new ChangeBounds();
        changeBounds.setDuration(600);
        changeBounds.setPathMotion(arcMotion);

        changeBounds.addListener(new Transition.TransitionListener() {
            @Override
            public void onTransitionStart(Transition transition) {
            }

            @SuppressLint("CheckResult")
            @Override
            public void onTransitionEnd(Transition transition) {
                explodeAnim();

                TransitionManager.beginDelayedTransition(flContent, mediumAnim);
                llIcon.setVisibility(View.VISIBLE);

                ViewPropertyAnimator animate1 = tvTips1.animate();
                ViewPropertyAnimator animate2 = tvTips2.animate();

                animate1.alpha(1f);
                animate2.alpha(1f);

                animate1.setDuration(1000);
                animate2.setDuration(2000);
                animate2.setStartDelay(1000);

                animate1.start();
                animate2.start();

                animate2.setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        Observable.timer(600, TimeUnit.MILLISECONDS, AndroidSchedulers.mainThread())
                                .subscribe(a -> {
                                    Intent intent;
                                    if (setupPayPass) {
                                        intent = new Intent(AppFirstLogin.this, SetUpPaymentPwdActivity.class);
                                        intent.putExtra("firstLogin", true);
                                    } else {
                                        intent = new Intent(AppFirstLogin.this, HomeActivity.class);
                                    }
                                    intent.putExtra("resultUri", getIntent().getStringExtra("resultUri"));
                                    startActivity(intent);
                                    finish();
                                });
                    }
                });
            }

            @Override
            public void onTransitionCancel(Transition transition) {
            }

            @Override
            public void onTransitionPause(Transition transition) {
            }

            @Override
            public void onTransitionResume(Transition transition) {
            }
        });

        return changeBounds;
    }

    private void mediumAnim() {
        mediumAnim = new ChangeBounds();
        mediumAnim.setDuration(1000);
        mediumAnim.setInterpolator(new OvershootInterpolator());
    }

    private void explodeAnim() {

        Animator anim1 = ViewAnimationUtils.createCircularReveal(viewContent, viewContent.getWidth() / 2,
                viewContent.getHeight() / 2, 0, Math.max(viewContent.getWidth(), viewContent.getHeight()));

        anim1.setDuration(1000);

        ValueAnimator anim2 = ValueAnimator.ofFloat(1f);
        anim2.addUpdateListener(v ->
                viewContent.setBackgroundColor((Integer) argbEvaluator.evaluate(v.getAnimatedFraction(), START_COLOR, END_COLOR)));
        anim2.setDuration(1000);

        AnimatorSet set = new AnimatorSet();
        set.playTogether(anim1, anim2);
        set.start();
    }

    @Override
    public void onBackPressed() {
    }
}

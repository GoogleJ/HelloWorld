package com.zxjk.duoduo.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.transition.ArcMotion;
import android.transition.ChangeBounds;
import android.transition.Transition;
import android.transition.TransitionManager;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.animation.OvershootInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.vectordrawable.graphics.drawable.ArgbEvaluator;

import com.airbnb.lottie.LottieAnimationView;
import com.blankj.utilcode.util.ScreenUtils;
import com.zxjk.duoduo.R;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;

public class AppFirstLogin extends AppCompatActivity {

    private FrameLayout flContent;
    private View viewContent;
    private ImageView ivIcon;
    private LinearLayout llIcon;
    private LottieAnimationView lavMochat1;
    private LottieAnimationView lavMochat2;
    private LottieAnimationView lavMochat3;
    private LottieAnimationView lavMochat4;
    private LottieAnimationView lavMochat5;
    private LottieAnimationView lavMochat6;

    private ChangeBounds mediumAnim;

    private ArgbEvaluator argbEvaluator = new ArgbEvaluator();
    private int START_COLOR;
    private int END_COLOR;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ScreenUtils.setFullScreen(this);
        setContentView(R.layout.activity_app_first_login);

        START_COLOR = ContextCompat.getColor(this, R.color.white);
        END_COLOR = ContextCompat.getColor(this, R.color.colorTheme);

        initView();

        mediumAnim();

        getWindow().setSharedElementEnterTransition(initTransition());
    }

    private void initView() {
        flContent = findViewById(R.id.flContent);
        viewContent = findViewById(R.id.viewContent);
        ivIcon = findViewById(R.id.ivIcon);
        llIcon = findViewById(R.id.llIcon);

        lavMochat1 = findViewById(R.id.lavMochat1);
        lavMochat2 = findViewById(R.id.lavMochat2);
        lavMochat3 = findViewById(R.id.lavMochat3);
        lavMochat4 = findViewById(R.id.lavMochat4);
        lavMochat5 = findViewById(R.id.lavMochat5);
        lavMochat6 = findViewById(R.id.lavMochat6);

        lavMochat6.addAnimatorListener(new AnimatorListenerAdapter() {
            @SuppressLint("CheckResult")
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                Observable.timer(200, TimeUnit.MILLISECONDS, AndroidSchedulers.mainThread())
                        .subscribe(a -> {
                            finish();
                            startActivity(new Intent(AppFirstLogin.this, HomeActivity.class));
                        });
            }
        });
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

                Observable.interval(1000, 400, TimeUnit.MILLISECONDS)
                        .take(6)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(a -> {
                            if (a == 0) lavMochat1.playAnimation();

                            if (a == 1) lavMochat2.playAnimation();

                            if (a == 2) lavMochat3.playAnimation();

                            if (a == 3) lavMochat4.playAnimation();

                            if (a == 4) lavMochat5.playAnimation();

                            if (a == 5) lavMochat6.playAnimation();
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
                viewContent.setBackgroundColor((Integer) argbEvaluator.evaluate(v.getAnimatedFraction(),
                        START_COLOR, END_COLOR)));
        anim2.setDuration(1000);

        AnimatorSet set = new AnimatorSet();
        set.playTogether(anim1, anim2);
        set.start();
    }

    @Override
    public void onBackPressed() {
    }
}

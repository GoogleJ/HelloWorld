package com.zxjk.duoduo.ui.minepage;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.blankj.utilcode.util.BarUtils;
import com.blankj.utilcode.util.ScreenUtils;
import com.zxjk.duoduo.R;
import com.zxjk.duoduo.ui.base.BaseActivity;
import com.zxjk.duoduo.ui.widget.RainView;
import com.zxjk.duoduo.utils.CommonUtils;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;

public class RedFallActivity extends BaseActivity {
    private RainView rain;

    private ImageView ivStartCountDown;
    private int countDown = 4;

    private ImageView ivRedFallTop;
    private ImageView ivRedFallTips;
    private FrameLayout flRedFallProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ScreenUtils.setFullScreen(this);
        BarUtils.setNavBarVisibility(this, false);
        setContentView(R.layout.activity_red_fall);

        initStartAnim();
    }

    private void initStartAnim() {
        ivRedFallTop = findViewById(R.id.ivRedFallTop);
        ivRedFallTips = findViewById(R.id.ivRedFallTips);
        flRedFallProgress = findViewById(R.id.flRedFallProgress);

        ivRedFallTop.post(() -> {
            ObjectAnimator firstAnim = ObjectAnimator.ofFloat(ivRedFallTop, "translationY", -ivRedFallTop.getHeight(), 0f).setDuration(500);
            firstAnim.setStartDelay(300);
            firstAnim.setInterpolator(new OvershootInterpolator(2.5f));
            firstAnim.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    ObjectAnimator middleAnim1 = ObjectAnimator.ofFloat(flRedFallProgress, "translationY", -flRedFallProgress.getHeight() - CommonUtils.dip2px(RedFallActivity.this, 16), 0f).setDuration(400);
                    middleAnim1.setInterpolator(new OvershootInterpolator(2.5f));
                    flRedFallProgress.setVisibility(View.VISIBLE);
                    middleAnim1.start();

                    ObjectAnimator middleAnim2 = ObjectAnimator.ofFloat(ivRedFallTips, "translationY", ivRedFallTips.getHeight() + CommonUtils.dip2px(RedFallActivity.this, 120), 0f).setDuration(400);
                    middleAnim2.setStartDelay(100);
                    middleAnim2.setInterpolator(new OvershootInterpolator(2.5f));
                    middleAnim2.start();
                    middleAnim2.addListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            startCountDown();
                        }

                        @Override
                        public void onAnimationStart(Animator animation) {
                            ivRedFallTips.setVisibility(View.VISIBLE);
                        }
                    });
                }
            });

            firstAnim.start();
        });
    }

    @SuppressLint("CheckResult")
    private void startCountDown() {
        ivStartCountDown = findViewById(R.id.ivStartCountDown);

        ObjectAnimator countDownDismissAnim = ObjectAnimator.ofFloat(ivStartCountDown, "alpha", 1f, 0f);
        countDownDismissAnim.setStartDelay(300);
        countDownDismissAnim.setDuration(200);
        countDownDismissAnim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                ivStartCountDown.setVisibility(View.GONE);
                startRedFall();
            }
        });

        ObjectAnimator countDownAnimX = ObjectAnimator.ofFloat(ivStartCountDown, "scaleX", 1.8f, 0.2f);
        ObjectAnimator countDownAnimY = ObjectAnimator.ofFloat(ivStartCountDown, "scaleY", 1.8f, 0.2f);
        AnimatorSet countDownAnim = new AnimatorSet();
        countDownAnim.setDuration(300);
        countDownAnim.playTogether(countDownAnimX, countDownAnimY);

        Observable.interval(0, 1000, TimeUnit.MILLISECONDS, AndroidSchedulers.mainThread())
                .take(4)
                .compose(bindToLifecycle())
                .doOnDispose(countDownAnim::cancel)
                .subscribe(l -> {
                    countDown--;
                    if (countDown == 3) {
                        ivStartCountDown.setVisibility(View.VISIBLE);
                        countDownAnim.start();
                    } else if (countDown == 2) {
                        ivStartCountDown.setImageResource(R.drawable.ic_redfall_num2_large);
                        countDownAnim.start();
                    } else if (countDown == 1) {
                        ivStartCountDown.setImageResource(R.drawable.ic_redfall_num1_large);
                        countDownAnim.start();
                    } else {
                        countDownDismissAnim.start();
                    }
                });
    }

    private void startRedFall() {
        rain = findViewById(R.id.rain);
        rain.start(true);
    }

}

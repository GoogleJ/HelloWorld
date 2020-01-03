package com.zxjk.duoduo.ui.minepage;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.blankj.utilcode.util.BarUtils;
import com.blankj.utilcode.util.ScreenUtils;
import com.zxjk.duoduo.R;
import com.zxjk.duoduo.network.rx.RxSchedulers;
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

    private TextView tvCountDown;

    private ImageView ivTop;
    private ImageView ivBottom;
    private ImageView ivOpen;
    private FrameLayout flOpen;

    private FrameLayout flMask;
    private FrameLayout flContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ScreenUtils.setFullScreen(this);
        BarUtils.setNavBarVisibility(this, false);
        setContentView(R.layout.activity_red_fall);

        tvCountDown = findViewById(R.id.tvCountDown);
        flMask = findViewById(R.id.flMask);
        flContainer = findViewById(R.id.flContainer);

        ivTop = findViewById(R.id.ivTop);
        ivBottom = findViewById(R.id.ivBottom);
        ivOpen = findViewById(R.id.ivOpen);
        flOpen = findViewById(R.id.flOpen);

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
                ivRedFallTips.setVisibility(View.GONE);
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

    @SuppressLint("CheckResult")
    private void startRedFall() {
        rain = findViewById(R.id.rain);
        rain.start(true);

        Observable.interval(0, 1000, TimeUnit.MILLISECONDS)
                .take(11)
                .compose(bindToLifecycle())
                .compose(RxSchedulers.ioObserver())
                .subscribe(a -> {
                    tvCountDown.setText((10 - a) + "");
                    if (a == 10) {
                        ObjectAnimator showOpenLayoutAnim = ObjectAnimator.ofFloat(flOpen, "translationY", -flOpen.getHeight(), (ScreenUtils.getScreenHeight() - flOpen.getHeight()) / 2f);
                        showOpenLayoutAnim.setDuration(600);
                        showOpenLayoutAnim.setInterpolator(new OvershootInterpolator(3f));

                        showOpenLayoutAnim.addListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationStart(Animator animation) {
                                flOpen.setVisibility(View.VISIBLE);
                                flMask.setVisibility(View.VISIBLE);
                                ivOpen.setOnClickListener(v -> openRed());
                            }

                            @Override
                            public void onAnimationEnd(Animator animation) {
                                rain.start(false);
                            }
                        });
                        showOpenLayoutAnim.start();
                    }
                });
    }

    private void openRed() {
        ivOpen.animate().alpha(0f)
                .setDuration(150)
                .setListener(new AnimatorListenerAdapter() {
                    @SuppressLint("CheckResult")
                    @Override
                    public void onAnimationStart(Animator animation) {
                        Observable.timer(150, TimeUnit.MILLISECONDS, AndroidSchedulers.mainThread())
                                .compose(bindToLifecycle())
                                .subscribe(l -> {
                                    Intent intent = new Intent(RedFallActivity.this, ConfirmRedFallActivity.class);
                                    startActivity(intent);
                                    overridePendingTransition(R.anim.redfallconfirm_enteranim, R.anim.redfallconfirm_exitanim);
                                    finish();
                                });
                    }
                })
                .start();
        ivTop.animate().translationYBy(-ScreenUtils.getScreenHeight())
                .setDuration(150).start();
        ivBottom.animate().translationYBy(ScreenUtils.getScreenHeight())
                .setDuration(150).start();
    }

}

package com.zxjk.duoduo.ui.minepage;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.OvershootInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.blankj.utilcode.util.BarUtils;
import com.blankj.utilcode.util.ScreenUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.trello.rxlifecycle3.android.ActivityEvent;
import com.zxjk.duoduo.Application;
import com.zxjk.duoduo.R;
import com.zxjk.duoduo.bean.RedFallActivityLocalBeanDao;
import com.zxjk.duoduo.bean.response.ReceiveAirdropResponse;
import com.zxjk.duoduo.db.RedFallActivityLocalBean;
import com.zxjk.duoduo.network.Api;
import com.zxjk.duoduo.network.ServiceFactory;
import com.zxjk.duoduo.network.rx.RxSchedulers;
import com.zxjk.duoduo.ui.base.BaseActivity;
import com.zxjk.duoduo.ui.widget.RainView;
import com.zxjk.duoduo.ui.widget.RoundCornerImageView;
import com.zxjk.duoduo.utils.CommonUtils;

import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import tyrantgit.explosionfield.ExplosionField;

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
    private RoundCornerImageView ivProgress;

    private LinearLayout llCount;
    private ImageView ivNum1;
    private ImageView ivNum2;
    private ImageView ivTail;
    private int[] numImgIds = new int[]{R.drawable.ic_redfall_num0, R.drawable.ic_redfall_num1, R.drawable.ic_redfall_num2, R.drawable.ic_redfall_num3,
            R.drawable.ic_redfall_num4, R.drawable.ic_redfall_num5, R.drawable.ic_redfall_num6,
            R.drawable.ic_redfall_num7, R.drawable.ic_redfall_num8, R.drawable.ic_redfall_num9};

    private int count;

    private RedFallActivityLocalBeanDao redFallActivityLocalBeanDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            WindowManager.LayoutParams lp = getWindow().getAttributes();
            lp.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
            getWindow().setAttributes(lp);
        }
        ScreenUtils.setFullScreen(this);
        BarUtils.setNavBarVisibility(this, false);

        setContentView(R.layout.activity_red_fall);

        redFallActivityLocalBeanDao = Application.daoSession.getRedFallActivityLocalBeanDao();

        tvCountDown = findViewById(R.id.tvCountDown);
        ivProgress = findViewById(R.id.ivProgress);
        flMask = findViewById(R.id.flMask);
        ivTop = findViewById(R.id.ivTop);
        ivBottom = findViewById(R.id.ivBottom);
        ivOpen = findViewById(R.id.ivOpen);
        flOpen = findViewById(R.id.flOpen);
        ivRedFallTop = findViewById(R.id.ivRedFallTop);
        ivRedFallTips = findViewById(R.id.ivRedFallTips);
        flRedFallProgress = findViewById(R.id.flRedFallProgress);
        ivStartCountDown = findViewById(R.id.ivStartCountDown);
        rain = findViewById(R.id.rain);
        llCount = findViewById(R.id.llCount);
        ivNum1 = findViewById(R.id.ivNum1);
        ivNum2 = findViewById(R.id.ivNum2);
        ivTail = findViewById(R.id.ivTail);

        ExplosionField explosionField = ExplosionField.attach2Window(this);

        rain.setOnRedClicked((bitmap, bound) -> {
            if (llCount.getVisibility() == View.GONE) llCount.setVisibility(View.VISIBLE);
            count++;
            if (count >= 10 && count < 20) {
                if (ivNum1.getVisibility() == View.GONE) {
                    ivNum1.setVisibility(View.VISIBLE);
                    ivNum1.setImageResource(R.drawable.ic_redfall_num1);
                }
                ivNum2.setImageResource(numImgIds[count - 10]);
            } else if (count >= 20 && count < 30) {
                ivNum1.setImageResource(R.drawable.ic_redfall_num2);
                ivNum2.setImageResource(numImgIds[count - 20]);
            } else if (count >= 30 && count < 40) {
                ivNum1.setImageResource(R.drawable.ic_redfall_num3);
                ivNum2.setImageResource(numImgIds[count - 30]);
            } else if (count >= 40 && count < 50) {
                ivNum1.setImageResource(R.drawable.ic_redfall_num4);
                ivNum2.setImageResource(numImgIds[count - 40]);
            } else if (count >= 50 && count < 60) {
                ivNum1.setImageResource(R.drawable.ic_redfall_num5);
                ivNum2.setImageResource(numImgIds[count - 50]);
            } else {
                ivNum2.setImageResource(numImgIds[count]);
            }

            if (ivNum1.getVisibility() == View.VISIBLE) {
                ObjectAnimator.ofFloat(ivNum1, "scaleX", 1.6f, 1f).setDuration(100).start();
                ObjectAnimator.ofFloat(ivNum1, "scaleY", 1.6f, 1f).setDuration(100).start();
            }
            ObjectAnimator.ofFloat(ivNum2, "scaleX", 1.6f, 1f).setDuration(100).start();
            ObjectAnimator.ofFloat(ivNum2, "scaleY", 1.6f, 1f).setDuration(100).start();
            ObjectAnimator.ofFloat(ivTail, "scaleX", 1.6f, 1f).setDuration(100).start();
            ObjectAnimator.ofFloat(ivTail, "scaleY", 1.6f, 1f).setDuration(100).start();

            explosionField.explode(bitmap, bound, 0, 300);
        });

        initStartAnim();
    }

    @Override
    public void onBackPressed() {
    }

    private void initStartAnim() {
        ivRedFallTop.post(() -> {
            ObjectAnimator firstAnim = ObjectAnimator.ofFloat(ivRedFallTop, "translationY", -ivRedFallTop.getHeight(), 0f).setDuration(500);
            firstAnim.setStartDelay(300);
            firstAnim.setInterpolator(new OvershootInterpolator(2.5f));
            firstAnim.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationStart(Animator animation) {
                    ivRedFallTop.setVisibility(View.VISIBLE);
                }

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
        ObjectAnimator countDownDismissAnim = ObjectAnimator.ofFloat(ivStartCountDown, "alpha", 1f, 0f);
        countDownDismissAnim.setStartDelay(150);
        countDownDismissAnim.setDuration(100);
        countDownDismissAnim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                ivRedFallTips.setVisibility(View.GONE);
            }

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

    @SuppressLint("CheckResult")
    private void startRedFall() {
        rain.start(true);

        Observable.interval(0, 1000, TimeUnit.MILLISECONDS)
                .take(11)
                .compose(bindUntilEvent(ActivityEvent.DESTROY))
                .compose(RxSchedulers.ioObserver())
                .subscribe(a -> {
                    tvCountDown.setText(String.valueOf(10 - a));
                    if (a == 10) {
                        ObjectAnimator showOpenLayoutAnim = ObjectAnimator.ofFloat(flOpen, "translationY", -flOpen.getHeight(), (ScreenUtils.getScreenHeight() - flOpen.getHeight()) / 2f);
                        showOpenLayoutAnim.setDuration(600);
                        showOpenLayoutAnim.setInterpolator(new OvershootInterpolator(3f));

                        showOpenLayoutAnim.addListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationStart(Animator animation) {
                                flOpen.setVisibility(View.VISIBLE);
                                flMask.setVisibility(View.VISIBLE);
                                ivOpen.setOnClickListener(v -> ServiceFactory.getInstance().getBaseService(Api.class)
                                        .receiveAirdrop(String.valueOf(count))
                                        .compose(bindToLifecycle())
                                        .compose(RxSchedulers.ioObserver(CommonUtils.initDialog(RedFallActivity.this)))
                                        .compose(RxSchedulers.normalTrans())
                                        .subscribe(r -> {
                                            if (r.getReceive().equals("0")) {
                                                redFallActivityLocalBeanDao.deleteAll();
                                                ToastUtils.showShort(R.string.noredfalldata);
                                                finish();
                                            } else {
                                                if (r.getNextReceive().equals("1") && r.getReceiveCount() != 0) {
                                                    List<RedFallActivityLocalBean> redFallActivityLocalBeans = redFallActivityLocalBeanDao.loadAll();
                                                    RedFallActivityLocalBean redFallActivityLocalBean = redFallActivityLocalBeans.get(0);
                                                    redFallActivityLocalBean.setLastPlayTime(String.valueOf(System.currentTimeMillis()));
                                                    redFallActivityLocalBeanDao.update(redFallActivityLocalBean);
                                                } else {
                                                    redFallActivityLocalBeanDao.deleteAll();
                                                }
                                                openRed(r);
                                            }
                                        }, t -> {
                                            handleApiError(t);
                                            finish();
                                        }));
                            }

                            @Override
                            public void onAnimationEnd(Animator animation) {
                                rain.start(false);
                            }
                        });
                        showOpenLayoutAnim.start();
                    }
                });

        Observable.interval(0, 10, TimeUnit.MILLISECONDS)
                .take(1001)
                .compose(bindUntilEvent(ActivityEvent.DESTROY))
                .compose(RxSchedulers.ioObserver())
                .subscribe(a -> {
                    if (progressWidth == 0) {
                        progressWidth = ScreenUtils.getScreenWidth() - CommonUtils.dip2px(this, 172);
                        minProgressWidth = CommonUtils.dip2px(this, 12);
                    }

                    ViewGroup.LayoutParams layoutParams = ivProgress.getLayoutParams();
                    int width = (int) ((1 - (a / 1000f)) * progressWidth);
                    layoutParams.width = minProgressWidth + width;
                    ivProgress.setLayoutParams(layoutParams);
                });
    }

    private int progressWidth;
    private int minProgressWidth;

    private void openRed(ReceiveAirdropResponse r) {
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
                                    intent.putExtra("data", r);
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

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.redfallconfirm_enteranim, R.anim.redfallconfirm_exitanim);
    }
}

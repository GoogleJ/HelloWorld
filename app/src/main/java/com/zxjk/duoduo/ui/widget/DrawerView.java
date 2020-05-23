package com.zxjk.duoduo.ui.widget;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.Region;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.ViewConfigurationCompat;

public class DrawerView extends FrameLayout {

    private VelocityTracker velocityTracker;

    private View menu;
    private View content;
    private View contentMask;

    //当前状态是否为展开
    private boolean isOpened = false;

    //展开状态内容区域alpha值
    private float contentMinAlpha = 0.4f;

    //改变开关状态需要的总时间
    private int animTime = 250;

    //展开状态内容区域scale值
    private float contentMinScale = 0.8f;

    //展开状态内容区域展示比例
    private float contentShowScaleWhenOpen = 0.375f;

    //内容最大高度
    private float maxTransitionZ;

    //关闭状态菜单区域向左偏移像素值
    private int menuOffset = 0;

    //用户触发横向滑动操作的最小像素值
    private float inScrollFlag;

    //横向滑动最大像素值
    private int totalScrollDistance;

    //内容滑动值
    private int contentScrollDistance;

    //手指落点坐标
    private float actionDownX;

    //上次移动的X坐标
    private float preMoveX = -1f;

    //当前是否可打开
    private boolean openable = true;

    private Region contentRegionWhenOpen;

    private OnStateChangeListener onStateChangeListener;

    public static final int STATE_OPEN = 1;
    public static final int STATE_CLOSE = 2;

    public interface OnStateChangeListener {
        void onStateChange(int state);
    }

    public DrawerView(@NonNull Context context) {
        this(context, null);
    }

    public DrawerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DrawerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        inScrollFlag = ViewConfigurationCompat.getScaledHorizontalScrollFactor(ViewConfiguration.get(context), context);
        velocityTracker = VelocityTracker.obtain();
    }

    public void setOpenable(boolean openable) {
        this.openable = openable;
    }

    public void setOnStateChangeListener(OnStateChangeListener onStateChangeListener) {
        this.onStateChangeListener = onStateChangeListener;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        menu = getChildAt(0);
        content = getChildAt(1);
        contentMask = ((ViewGroup) content).getChildAt(1);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        measureChildren(widthMeasureSpec, heightMeasureSpec);

        contentScrollDistance = totalScrollDistance = (int) (getMeasuredWidth() - getMeasuredWidth() * contentShowScaleWhenOpen);
        menuOffset = getMeasuredWidth() / 4;
        maxTransitionZ = 24;
    }


    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
//            menu.setPadding(-menuOffset, 0, 0, 0);
        menu.layout(left - menuOffset, top, right - menuOffset, bottom);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
//        super.onInterceptTouchEvent(ev);
//
//        switch (ev.getAction()) {
//            case MotionEvent.ACTION_DOWN:
//                actionDownX = ev.getX();
//                preMoveX = -1;
//                break;
//            case MotionEvent.ACTION_MOVE:
//                if (!openable) return false;
//                float distance = ev.getX() - actionDownX;
//                boolean isRightMove = distance > inScrollFlag && distance > 0;
//
//                if (!isOpened && isRightMove) {
//                    //关闭状态，向右滑动（打开）
//                    return true;
//                } else if (isOpened && !isRightMove) {
//                    //打开状态，向左滑动（关闭）
//                    return true;
//                }
//        }
//        return false;

        if (contentScrollDistance != 0 && contentScrollDistance != totalScrollDistance) {
            return true;
        }

        if (ev.getAction() == MotionEvent.ACTION_MOVE) {
            if (contentRegionWhenOpen == null && isOpened) {
                contentRegionWhenOpen = new Region();
                Rect rect = new Rect();
                content.getHitRect(rect);
                contentRegionWhenOpen.set(rect);
            }

            if (isOpened) {
                if (contentRegionWhenOpen.contains((int) ev.getX(), (int) ev.getY())) {
                    return true;
                }
            }
        }

        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {
            if (isOpened) {
                if (contentRegionWhenOpen.contains((int) event.getX(), (int) event.getY())) {
                    close();
                    return true;
                }
            }
        }
        return super.onTouchEvent(event);
    }

    //    @Override
//    public boolean onTouchEvent(MotionEvent event) {
//        super.onTouchEvent(event);
//
//        velocityTracker.addMovement(event);
//
//        float distance;
//
//        if (preMoveX == -1) {
//            distance = event.getX() - actionDownX;
//        } else {
//            distance = event.getX() - preMoveX;
//        }
//
//        preMoveX = event.getX();
//
//        boolean isRightMove = distance > 0;
//
//        switch (event.getAction()) {
//            case MotionEvent.ACTION_MOVE:
//                if (isRightMove) {
//                    moveRight(distance);
//                } else {
//                    moveLeft(-distance);
//                }
//                break;
//            case MotionEvent.ACTION_UP:
//                final VelocityTracker velocityTracker = this.velocityTracker;
//                velocityTracker.computeCurrentVelocity(1);
//                float velocityX = Math.abs(velocityTracker.getXVelocity());
//
//                if (velocityX >= totalScrollDistance * 0.4f / animTime) {
//                    isOpened = !isOpened;
//                } else {
//                    if (isOpened) {
//                        if (contentScrollDistance > totalScrollDistance / 3) {
//                            isOpened = false;
//                        }
//                    } else {
//                        if (totalScrollDistance - contentScrollDistance > totalScrollDistance / 3) {
//                            isOpened = true;
//                        }
//                    }
//                }
//
//                changeState();
//                break;
//        }
//
//        return true;
//    }

    public void changeState() {
        ValueAnimator openAnim;

        if (isOpened) {
            openAnim = ValueAnimator.ofFloat(contentScrollDistance * 1f / totalScrollDistance, 0f).setDuration((long) (animTime * contentScrollDistance * 1f / totalScrollDistance));
        } else {
            openAnim = ValueAnimator.ofFloat(contentScrollDistance * 1f / totalScrollDistance, 1f).setDuration((long) (animTime * (1f - contentScrollDistance * 1f / totalScrollDistance)));
        }

        openAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float fraction = (float) animation.getAnimatedValue();
                if (isOpened) {
                    moveRight(contentScrollDistance - totalScrollDistance * fraction);
                } else {
                    moveLeft(totalScrollDistance * fraction - contentScrollDistance);
                }
            }
        });

        openAnim.start();
    }

    private void moveRight(float distance) {
        if (contentScrollDistance != 0) {
            distance *= 1.1f;
            if (!content.getClipToOutline()) {
                content.setClipToOutline(true);
            }

            float realScrollX = contentScrollDistance - distance < 0 ?
                    contentScrollDistance : distance;

            contentScrollDistance -= realScrollX;

            content.setTranslationX(totalScrollDistance - contentScrollDistance);

            float progress = contentScrollDistance * 1f / totalScrollDistance;
            content.setScaleX(1f - (1f - contentMinScale) * (1f - progress));
            content.setScaleY(1f - (1f - contentMinScale) * (1f - progress));

            menu.setTranslationX(menuOffset - menuOffset * (contentScrollDistance * 1f / totalScrollDistance));
//            menu.setPadding((int) (-menuOffset * progress), 0, 0, 0);

            content.setTranslationZ((1f - progress) * maxTransitionZ);

            String hexString = Integer.toHexString((int) (255 * contentMinAlpha * (1f - progress)));
            if (hexString.length() == 1) {
                hexString = "0" + hexString;
            }

            contentMask.setBackgroundColor(Color.parseColor("#" + hexString + "ffffff"));

            if (contentScrollDistance == totalScrollDistance) {
                content.setClipToOutline(false);
            }
        }
    }

    private void moveLeft(float distance) {
        if (contentScrollDistance != totalScrollDistance) {
            distance *= 1.1f;
            if (!content.getClipToOutline()) {
                content.setClipToOutline(true);
            }

            float realScrollX = contentScrollDistance + distance > totalScrollDistance ?
                    totalScrollDistance - contentScrollDistance : distance;

            contentScrollDistance += realScrollX;

            content.setTranslationX(totalScrollDistance - contentScrollDistance);

            float progress = contentScrollDistance * 1f / totalScrollDistance;
            content.setScaleX(1f - (1f - contentMinScale) * (1f - progress));
            content.setScaleY(1f - (1f - contentMinScale) * (1f - progress));

            menu.setTranslationX(menuOffset - menuOffset * (contentScrollDistance * 1f / totalScrollDistance));
//            menu.setPadding((int) (-menuOffset * progress), 0, 0, 0);

            content.setTranslationZ((1f - progress) * maxTransitionZ);

            String hexString = Integer.toHexString((int) (255 * contentMinAlpha * (1f - progress)));
            if (hexString.length() == 1) {
                hexString = "0" + hexString;
            }

            contentMask.setBackgroundColor(Color.parseColor("#" + hexString + "ffffff"));

            if (contentScrollDistance == totalScrollDistance) {
                content.setClipToOutline(false);
            }
        }
    }

    public void switchState() {
        isOpened = !isOpened;
        changeState();
    }

    public void open() {
        isOpened = true;
        changeState();
    }

    public void close() {
        isOpened = false;
        changeState();
    }

    @Override
    protected void onDetachedFromWindow() {
        velocityTracker.recycle();
        super.onDetachedFromWindow();
    }

}

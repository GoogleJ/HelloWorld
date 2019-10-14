package com.zxjk.duoduo.ui.widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.zxjk.duoduo.R;
import com.zxjk.duoduo.utils.CommonUtils;

public class CreateWLoadingView extends View {
    private Paint paint;

    //当前进度，最大值为1
    private float progress = 0f;

    //view绘制区
    private Rect rect;
    private RectF rectf;

    //弧线路径
    private Path path;
    private PathMeasure pathMeasure;
    //当前path首尾两点坐标
    private float[] pathPoints;

    //尾部小圆半径
    private float endCircleRadius;

    private ValueAnimator startAnim;
    private ValueAnimator middleAnim;

    public CreateWLoadingView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        init();
    }

    private void init() {
        rect = new Rect();
        rectf = new RectF();
        path = new Path();
        pathMeasure = new PathMeasure();
        endCircleRadius = CommonUtils.dip2px(getContext(), 4);
        pathPoints = new float[2];

        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (rect.isEmpty()) {
            getDrawingRect(rect);

            paint.setColor(Color.parseColor("#000000"));
            canvas.drawCircle(rect.right / 2f, rect.right / 2f, rect.right / 2f, paint);

            rect.top += endCircleRadius + (endCircleRadius / 2f);
            rect.left += endCircleRadius + (endCircleRadius / 2f);
            rect.bottom -= endCircleRadius + (endCircleRadius / 2f);
            rect.right -= endCircleRadius + (endCircleRadius / 2f);
            rectf.set(rect);

            paint.setStrokeWidth(endCircleRadius / 2f);
            paint.setColor(ContextCompat.getColor(getContext(), R.color.colorTheme));
        }

        path.reset();
        path.moveTo(rectf.right / 2f, rectf.top);
        path.arcTo(rectf, 270, 360f * progress);
        paint.setStyle(Paint.Style.STROKE);
        canvas.drawPath(path, paint);

        pathMeasure.setPath(path, false);
        pathMeasure.getPosTan(pathMeasure.getLength(), pathPoints, null);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawCircle(pathPoints[0], pathPoints[1], endCircleRadius, paint);
    }

    public void setProgress(float progress) {
        this.progress = progress;
        invalidate();
    }

    public void startAnim() {
        middleAnim = ValueAnimator.ofFloat(0.55f, 0.9f);
        middleAnim.setDuration(6000);
        middleAnim.addUpdateListener(va -> setProgress((Float) va.getAnimatedValue()));

        startAnim = ValueAnimator.ofFloat(0f, 0.55f);
        startAnim.setDuration(3000);
        startAnim.addUpdateListener(va -> setProgress((Float) va.getAnimatedValue()));
        startAnim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                middleAnim.start();
            }
        });
        startAnim.start();
    }

    public void finish() {
        ValueAnimator endAnim = ValueAnimator.ofFloat(progress, 1f);
        endAnim.setDuration(300);
        endAnim.addUpdateListener(va -> setProgress(va.getAnimatedFraction()));

        if (startAnim != null && startAnim.isRunning()) {
            startAnim.end();
        }

        if (middleAnim != null && middleAnim.isRunning()) {
            middleAnim.end();
        }

        endAnim.start();
    }
}

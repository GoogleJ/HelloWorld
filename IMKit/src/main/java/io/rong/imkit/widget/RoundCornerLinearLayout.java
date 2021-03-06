package io.rong.imkit.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.Path.Direction;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class RoundCornerLinearLayout extends LinearLayout {
    private Path path = new Path();
    private RectF rect = new RectF();
    private float radius = 15.0F;

    public RoundCornerLinearLayout(@NonNull Context context) {
        super(context);
    }

    public RoundCornerLinearLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        this.path.reset();
        this.rect.set(0.0F, 0.0F, w, h);
        this.path.addRoundRect(this.rect, this.radius, this.radius, Direction.CW);
        this.path.close();
    }

    protected void dispatchDraw(Canvas canvas) {
        int save = canvas.save();
        canvas.clipPath(this.path);
        super.dispatchDraw(canvas);
        canvas.restoreToCount(save);
    }
}
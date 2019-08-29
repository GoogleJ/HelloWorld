package com.zxjk.duoduo.ui.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;
import androidx.core.view.ViewConfigurationCompat;
import androidx.core.widget.NestedScrollView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

public class SlopScrollView extends NestedScrollView {
    private int scrollSlop;

    private float startY;

    public SlopScrollView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public void setScrollSlop(int scrollSlop) {
        this.scrollSlop = scrollSlop;
    }

    @Override
    public void onNestedPreScroll(@NonNull View target, int dx, int dy, @NonNull int[] consumed, int type) {
        if (getScrollY() == 0 && dy < 0) {
            return;
        }
        if (getScrollY() < scrollSlop) {
            consumed[1] = dy;
            scrollBy(dx, dy);
        }
    }

}

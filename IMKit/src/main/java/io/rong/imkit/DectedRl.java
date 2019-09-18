package io.rong.imkit;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;

public class DectedRl extends RelativeLayout {
    private VisibilityChange visibilityChange;

    public void setVisibilityChange(VisibilityChange visibilityChange) {
        this.visibilityChange = visibilityChange;
    }

    public interface VisibilityChange {
        void change(boolean isVisiable);
    }

    public DectedRl(Context context) {
        super(context);
    }

    public DectedRl(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DectedRl(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onVisibilityChanged(View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);
        if (visibilityChange != null && changedView == this) {
            if (visibility == View.VISIBLE) visibilityChange.change(true);
            else visibilityChange.change(false);
        }
    }
}

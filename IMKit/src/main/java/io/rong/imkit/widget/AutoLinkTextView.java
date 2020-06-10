package io.rong.imkit.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.text.Layout;
import android.text.Spannable;
import android.text.TextPaint;
import android.text.style.URLSpan;
import android.util.AttributeSet;
import android.widget.TextView;

import io.rong.imkit.R;

public class AutoLinkTextView extends TextView {
    private int mMaxWidth;

    public AutoLinkTextView(Context context) {
        super(context);
    }

    public AutoLinkTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initWidth(context, attrs);
        setAutoLinkMask(7);
    }

    public AutoLinkTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initWidth(context, attrs);
        setAutoLinkMask(7);
    }

    @TargetApi(21)
    public AutoLinkTextView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initWidth(context, attrs);
        setAutoLinkMask(7);
    }

    private void initWidth(Context context, AttributeSet attrs) {
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.AutoLinkTextView);
        this.mMaxWidth = array.getDimensionPixelSize(R.styleable.AutoLinkTextView_RCMaxWidth, 0);
        setMaxWidth(this.mMaxWidth);
        array.recycle();
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        Layout layout = getLayout();
        float width = 0.0F;
        for (int i = 0; i < layout.getLineCount(); ++i)
            width = Math.max(width, layout.getLineWidth(i));

        width += getCompoundPaddingLeft() + getCompoundPaddingRight();
        if (getBackground() != null)
            width = Math.max(width, getBackground().getIntrinsicWidth());

        if (this.mMaxWidth != 0)
            width = Math.min(width, this.mMaxWidth);

        setMeasuredDimension((int) Math.ceil(width), getMeasuredHeight());
    }

    public void stripUnderlines() {
        Spannable s;
        TextView textView = this;
        if ((textView != null) && (textView.getText() instanceof Spannable)) {
            s = (Spannable) textView.getText();
            URLSpan[] spans = (URLSpan[]) s.getSpans(0, s.length(), URLSpan.class);
            for (URLSpan span : spans) {
                int start = s.getSpanStart(span);
                int end = s.getSpanEnd(span);
                s.removeSpan(span);
                span = new URLSpanNoUnderline(span.getURL());
                s.setSpan(span, start, end, 0);
            }
        }
    }

    private class URLSpanNoUnderline extends URLSpan {
        public URLSpanNoUnderline(String paramString) {
            super(paramString);
        }

        public void updateDrawState(TextPaint ds) {
            super.updateDrawState(ds);
            ds.setUnderlineText(false);
        }
    }
}
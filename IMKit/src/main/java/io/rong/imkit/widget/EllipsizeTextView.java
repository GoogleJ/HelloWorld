//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package io.rong.imkit.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Paint;
import android.text.Layout;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.CharacterStyle;
import android.util.AttributeSet;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import io.rong.common.RLog;
import io.rong.imkit.R.styleable;

public class EllipsizeTextView extends TextView {
    private static final String TAG = "EllipsizeTextView";
    private static final String DEFAULT_ELLIPSIZE_TEXT = "...";
    private CharSequence mEllipsizeText;
    private CharSequence mOriginText;
    private int mEllipsizeIndex;
    private int mMaxLines;
    private boolean mIsExactlyMode;
    private boolean mEnableUpdateOriginText;

    public EllipsizeTextView(Context context) {
        this(context, (AttributeSet) null);
    }

    public EllipsizeTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mEnableUpdateOriginText = true;
        TypedArray ta = context.obtainStyledAttributes(attrs, styleable.EllipsizeTextView);
        this.mEllipsizeIndex = ta.getInt(styleable.EllipsizeTextView_RCEllipsizeIndex, 0);
        this.mEllipsizeText = ta.getText(styleable.EllipsizeTextView_RCEllipsizeText);
        if (this.mEllipsizeText == null) {
            this.mEllipsizeText = "...";
        }

        ta.recycle();
    }

    public void setMaxLines(int maxLines) {
        if (this.mMaxLines != maxLines) {
            super.setMaxLines(maxLines);
            this.mMaxLines = maxLines;
        }

    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        try {
            this.mIsExactlyMode = MeasureSpec.getMode(widthMeasureSpec) == 1073741824;
            Layout layout = this.getLayout();
            if (layout != null && (this.isExceedMaxLine(layout) || this.isOutOfBounds(layout))) {
                this.adjustEllipsizeEndText(layout);
            }
        } catch (Exception var4) {
            RLog.d("EllipsizeTextView", "onMeasure:" + var4);
        }

    }

    public void setText(CharSequence text, BufferType type) {
        if (this.mEnableUpdateOriginText) {
            this.mOriginText = text;
        }

        super.setText(text, type);
        if (this.mIsExactlyMode) {
            this.requestLayout();
        }

    }

    private boolean isExceedMaxLine(Layout layout) {
        return layout.getLineCount() > this.mMaxLines && this.mMaxLines > 0;
    }

    private boolean isOutOfBounds(Layout layout) {
        return layout.getHeight() > this.getMeasuredHeight() - this.getPaddingBottom() - this.getPaddingTop();
    }

    private void adjustEllipsizeEndText(Layout layout) {
        CharSequence originText = this.mOriginText;
        CharSequence restSuffixText = originText.subSequence(originText.length() - this.mEllipsizeIndex, originText.length());
        int width = layout.getWidth() - this.getPaddingLeft() - this.getPaddingRight();
        int maxLineCount = Math.max(1, this.computeMaxLineCount(layout));
        int lastLineWidth = (int) layout.getLineWidth(maxLineCount - 1);
        int mLastCharacterIndex = layout.getLineEnd(maxLineCount - 1);
        int suffixWidth = (int) (Layout.getDesiredWidth(this.mEllipsizeText, this.getPaint()) + Layout.getDesiredWidth(restSuffixText, this.getPaint())) + 1;
        this.mEnableUpdateOriginText = false;
        if (lastLineWidth + suffixWidth > width) {
            int widthDiff = lastLineWidth + suffixWidth - width;
            int removedCharacterCount = this.computeRemovedEllipsizeEndCharacterCount(widthDiff, originText.subSequence(0, mLastCharacterIndex));
            this.setText(originText.subSequence(0, mLastCharacterIndex - removedCharacterCount));
            this.append(this.mEllipsizeText);
            this.append(restSuffixText);
        } else {
            this.setText(originText.subSequence(0, mLastCharacterIndex));
            this.append(this.mEllipsizeText);
            this.append(restSuffixText);
        }

        this.mEnableUpdateOriginText = true;
    }

    private int computeMaxLineCount(Layout layout) {
        int availableHeight = this.getMeasuredHeight() - this.getPaddingTop() - this.getPaddingBottom();

        for (int i = 0; i < layout.getLineCount(); ++i) {
            if (availableHeight < layout.getLineBottom(i)) {
                return i;
            }
        }

        return layout.getLineCount();
    }

    private int computeRemovedEllipsizeEndCharacterCount(int widthDiff, CharSequence text) {
        if (TextUtils.isEmpty(text)) {
            return 0;
        } else {
            List<EllipsizeTextView.Range<Integer>> characterStyleRanges = this.computeCharacterStyleRanges(text);
            String textStr = text.toString();
            int characterIndex = text.length();
            int codePointIndex = textStr.codePointCount(0, text.length());

            for (int currentRemovedWidth = 0; codePointIndex > 0 && widthDiff > currentRemovedWidth; currentRemovedWidth = (int) Layout.getDesiredWidth(text.subSequence(characterIndex, text.length()), this.getPaint())) {
                --codePointIndex;
                characterIndex = textStr.offsetByCodePoints(0, codePointIndex);
                EllipsizeTextView.Range<Integer> characterStyleRange = this.computeCharacterStyleRange(characterStyleRanges, characterIndex);
                if (characterStyleRange != null) {
                    characterIndex = (Integer) characterStyleRange.getLower();
                    codePointIndex = textStr.codePointCount(0, characterIndex);
                }
            }

            return text.length() - textStr.offsetByCodePoints(0, codePointIndex);
        }
    }

    private EllipsizeTextView.Range<Integer> computeCharacterStyleRange(List<EllipsizeTextView.Range<Integer>> characterStyleRanges, int index) {
        if (characterStyleRanges != null && !characterStyleRanges.isEmpty()) {
            Iterator var3 = characterStyleRanges.iterator();

            EllipsizeTextView.Range characterStyleRange;
            do {
                if (!var3.hasNext()) {
                    return null;
                }

                characterStyleRange = (EllipsizeTextView.Range) var3.next();
            } while (!characterStyleRange.contains(index));

            return characterStyleRange;
        } else {
            return null;
        }
    }

    private List<EllipsizeTextView.Range<Integer>> computeCharacterStyleRanges(CharSequence text) {
        SpannableStringBuilder ssb = SpannableStringBuilder.valueOf(text);
        CharacterStyle[] characterStyles = (CharacterStyle[]) ssb.getSpans(0, ssb.length(), CharacterStyle.class);
        if (characterStyles != null && characterStyles.length != 0) {
            List<EllipsizeTextView.Range<Integer>> ranges = new ArrayList();
            CharacterStyle[] var5 = characterStyles;
            int var6 = characterStyles.length;

            for (int var7 = 0; var7 < var6; ++var7) {
                CharacterStyle characterStyle = var5[var7];
                ranges.add(new EllipsizeTextView.Range(ssb.getSpanStart(characterStyle), ssb.getSpanEnd(characterStyle)));
            }

            return ranges;
        } else {
            return Collections.EMPTY_LIST;
        }
    }

    public void setEllipsizeText(CharSequence ellipsizeText, int ellipsizeIndex) {
        this.mEllipsizeText = ellipsizeText;
        this.mEllipsizeIndex = ellipsizeIndex;
    }

    public void setAdaptiveText(final String text) {
        this.post(new Runnable() {
            public void run() {
                EllipsizeTextView.this.setText(text);
                EllipsizeTextView.this.setText(EllipsizeTextView.this.adaptiveText(EllipsizeTextView.this));
            }
        });
    }

    private String adaptiveText(TextView textView) {
        String originalText = textView.getText().toString();
        Paint tvPaint = textView.getPaint();
        float tvWidth = (float) (textView.getWidth() - textView.getPaddingLeft() - textView.getPaddingRight());
        int enterCount = 0;
        String[] originalTextLines = originalText.replaceAll("\r", "").split("\n");
        StringBuilder newTextBuilder = new StringBuilder();
        String[] var8 = originalTextLines;
        int var9 = originalTextLines.length;

        for (int var10 = 0; var10 < var9; ++var10) {
            String originalTextLine = var8[var10];
            if (tvPaint.measureText(originalTextLine) <= tvWidth) {
                newTextBuilder.append(originalTextLine);
            } else {
                float lineWidth = 0.0F;

                for (int i = 0; i != originalTextLine.length(); ++i) {
                    char charAt = originalTextLine.charAt(i);
                    lineWidth += tvPaint.measureText(String.valueOf(charAt));
                    if (lineWidth <= tvWidth) {
                        newTextBuilder.append(charAt);
                    } else {
                        if (enterCount < this.getMaxLines() - 1) {
                            newTextBuilder.append("\n");
                            ++enterCount;
                        }

                        lineWidth = 0.0F;
                        --i;
                    }
                }
            }
        }

        return newTextBuilder.toString();
    }

    public static final class Range<T extends Comparable<? super T>> {
        private final T mLower;
        private final T mUpper;

        public Range(T lower, T upper) {
            this.mLower = lower;
            this.mUpper = upper;
            if (lower.compareTo(upper) > 0) {
                throw new IllegalArgumentException("lower must be less than or equal to upper");
            }
        }

        public T getLower() {
            return this.mLower;
        }

        public T getUpper() {
            return this.mUpper;
        }

        public boolean contains(T value) {
            boolean gteLower = value.compareTo(this.mLower) >= 0;
            boolean lteUpper = value.compareTo(this.mUpper) < 0;
            return gteLower && lteUpper;
        }
    }
}
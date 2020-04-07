package com.zxjk.moneyspace.ui.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.View;

import com.zxjk.moneyspace.R;

import net.lucode.hackware.magicindicator.FragmentContainerHelper;
import net.lucode.hackware.magicindicator.buildins.UIUtil;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.model.PositionData;

import java.util.List;

public class ImagePagerIndicator extends View implements IPagerIndicator {

    private Paint mPaint;
    private float mYOffset;
    private float mXOffset;
    private Bitmap bitmap;
    private List<PositionData> mPositionDataList;
    private float currentXCenter;

    public ImagePagerIndicator(Context context, float yOffset) {
        super(context);
        init(context, yOffset);
    }

    public ImagePagerIndicator(Context context) {
        this(context, 0.5f);
    }

    private void init(Context context, float yOffset) {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.indicator_msg);
        mYOffset = UIUtil.dip2px(context, yOffset);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawBitmap(bitmap, currentXCenter - bitmap.getWidth() / 2f, getHeight() - mYOffset - bitmap.getHeight(), mPaint);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        PositionData current = FragmentContainerHelper.getImitativePositionData(mPositionDataList, position);
        PositionData next = FragmentContainerHelper.getImitativePositionData(mPositionDataList, position + 1);

        float leftX = current.mLeft + (current.mRight - current.mLeft) / 2;
        float rightX = next.mLeft + (next.mRight - next.mLeft) / 2;

        currentXCenter = leftX + (rightX - leftX) * positionOffset;

        invalidate();
    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onPositionDataProvide(List<PositionData> dataList) {
        mPositionDataList = dataList;
    }

    public float getmYOffset() {
        return mYOffset;
    }

    public void setmYOffset(float mYOffset) {
        this.mYOffset = mYOffset;
    }

    public float getmXOffset() {
        return mXOffset;
    }

    public void setmXOffset(float mXOffset) {
        this.mXOffset = mXOffset;
    }
}

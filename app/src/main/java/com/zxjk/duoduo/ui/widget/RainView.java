package com.zxjk.duoduo.ui.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import com.zxjk.duoduo.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RainView extends View {

    private Paint paint;
    private Matrix matrix;
    private Random random;
    private boolean isRun;
    private List<ItemEmoje> bitmapList;
    private int[] imgResIds = new int[]{R.drawable.ic_redfall_redbtc, R.drawable.ic_redfall_redeth, R.drawable.ic_redfall_redeos,
            R.drawable.ic_redfall_redltc, R.drawable.ic_redfall_redltc, R.drawable.ic_redfall_redxrp};

    public RainView(Context context) {
        this(context, null);
    }

    public RainView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RainView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setFilterBitmap(true);
        paint.setDither(true);
        matrix = new Matrix();
        random = new Random();
        bitmapList = new ArrayList<>();

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (isRun) {
            boolean isInScreen = false;
            for (int i = 0; i < bitmapList.size(); i++) {
                ItemEmoje bean = bitmapList.get(i);
                matrix.reset();
                matrix.setScale(bean.scale, bean.scale);
                bean.x = bean.x + bean.offsetX;
                if (bean.x > getWidth()) {
                    bean.x = (int) (getWidth() - (bean.bitmap.getWidth() * bean.scale));
                } else if (bean.x < 0) {
                    bean.x = 0;
                }
                bean.y = bean.y + bean.offsetY;
                if (bean.y < getHeight()) {
                    isInScreen = true;
                }
                matrix.postTranslate(bean.x, bean.y);
                canvas.drawBitmap(bean.bitmap, matrix, paint);
            }
            if (isInScreen) {
                postInvalidate();
            } else {
                release();
                start(true);
            }
        }
    }

    private void release() {
        if (bitmapList != null && bitmapList.size() > 0) {
            for (ItemEmoje itemEmoje : bitmapList) {
                if (!itemEmoje.bitmap.isRecycled()) {
                    itemEmoje.bitmap.recycle();
                }
            }
            bitmapList.clear();
        }
    }

    public void start(boolean isRun) {
        this.isRun = isRun;
        initData();
        postInvalidate();
    }

    private void initData() {
        release();
        for (int i = 0; i < 24; i++) {
            ItemEmoje itemEmoje = new ItemEmoje();
            itemEmoje.bitmap = BitmapFactory.decodeResource(getResources(), imgResIds[random.nextInt(6)]);
            itemEmoje.x = random.nextInt(getWidth() - 200) + 100;
            itemEmoje.y = -random.nextInt(getHeight() * 2);
            itemEmoje.offsetX = random.nextInt(3) - 1;
            itemEmoje.offsetY = 16 + random.nextInt(12);
            itemEmoje.scale = (float) (random.nextInt(40) + 40) / 100f;
            itemEmoje.degree = (float) (random.nextInt(361));
            bitmapList.add(itemEmoje);
        }
    }

    public class ItemEmoje {
        public int x;
        public int y;
        public int offsetX;
        public int offsetY;
        public float scale;
        public float degree;
        public Bitmap bitmap;
    }

}
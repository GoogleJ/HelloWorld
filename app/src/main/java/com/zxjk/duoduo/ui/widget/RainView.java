package com.zxjk.duoduo.ui.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Region;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

import com.zxjk.duoduo.R;

import java.util.ArrayList;
import java.util.Iterator;
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

    private OnRedClicked onRedClicked;

    public interface OnRedClicked {
        void onClick();
    }

    public void setOnRedClicked(OnRedClicked onRedClicked) {
        this.onRedClicked = onRedClicked;
    }

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

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            float x = event.getX();
            float y = event.getY();

            Region region = new Region();
            Iterator<ItemEmoje> iterator = bitmapList.iterator();

            while (iterator.hasNext()) {
                ItemEmoje itemEmoje = iterator.next();

                region.set(itemEmoje.x, itemEmoje.y, itemEmoje.x + (int) (itemEmoje.bitmap.getWidth() * itemEmoje.scale), itemEmoje.y + (int) (itemEmoje.bitmap.getHeight() * itemEmoje.scale));
                if (region.contains((int) x, (int) y)) {
                    iterator.remove();
                    if (onRedClicked != null) onRedClicked.onClick();
//                    Log.e("click", "click");
//                    ExplosionAnimator explosion = new ExplosionAnimator(this, itemEmoje.bitmap, region.getBounds());
////                    explosion.setStartDelay(startDelay);
//                    explosion.setDuration(100);
//                    explosion.start();
                    return true;
                }
            }
        }
        return super.onTouchEvent(event);
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
            itemEmoje.y = -random.nextInt(getHeight());
            itemEmoje.offsetX = random.nextInt(5) - 2;
            itemEmoje.offsetY = 24;
            itemEmoje.scale = 0.8f;
            bitmapList.add(itemEmoje);
        }
    }

    public class ItemEmoje {
        public int x;
        public int y;
        public int offsetX;
        public int offsetY;
        public float scale;
        public Bitmap bitmap;
    }

}
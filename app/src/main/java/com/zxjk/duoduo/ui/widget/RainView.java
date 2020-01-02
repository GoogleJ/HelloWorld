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
    //图片处理
    private Matrix matrix;
    private Random random;
    //判断是否运行的，默认没有
    private boolean isRun;
    //表情包集合
    private List<ItemEmoje> bitmapList;
    //表情图片
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
            //用于判断表情下落结束，结束即不再进行重绘
            boolean isInScreen = false;
            for (int i = 0; i < bitmapList.size(); i++) {
                matrix.reset();
                //缩放
                matrix.setScale(bitmapList.get(i).scale, bitmapList.get(i).scale);
                matrix.setRotate(bitmapList.get(i).degree, 0.5f, 0.5f);
                //下落过程坐标
                bitmapList.get(i).x = bitmapList.get(i).x + bitmapList.get(i).offsetX;
                bitmapList.get(i).y = bitmapList.get(i).y + bitmapList.get(i).offsetY;
                if (bitmapList.get(i).y < getHeight() - 10) {//当表情仍在视图内，则继续重绘
                    isInScreen = true;
                }
                //位移
                matrix.postTranslate(bitmapList.get(i).x, bitmapList.get(i).y);
                canvas.drawBitmap(bitmapList.get(i).bitmap, matrix, paint);
            }
            if (isInScreen) {
                postInvalidate();
            } else {
                release();
                start(true);
            }
        }
    }

    /**
     * 释放资源
     */
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
            //起始横坐标在[100,getWidth()-100) 之间
            itemEmoje.x = random.nextInt(getWidth() - 200) + 100;
            //起始纵坐标在(-getHeight(),0] 之间，即一开始位于屏幕上方以外
            itemEmoje.y = -random.nextInt(getHeight() * 2);
            //横向偏移[-1,2) ，即左右摇摆区间
            itemEmoje.offsetX = random.nextInt(3) - 1;
            //纵向固定下落12
            itemEmoje.offsetY = 16 + random.nextInt(12);
            //缩放比例[0.4,0.8) 之间
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
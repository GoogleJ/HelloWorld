package com.zxjk.duoduo.ui;

import android.graphics.Color;
import android.os.Bundle;
import android.view.WindowManager;
import com.blankj.utilcode.util.ScreenUtils;
import com.zxjk.duoduo.R;
import com.zxjk.duoduo.ui.base.BaseActivity;
import com.zxjk.duoduo.ui.widget.PinchImageView;
import com.zxjk.duoduo.utils.GlideUtil;
import butterknife.BindView;
import butterknife.ButterKnife;


public class ZoomActivity extends BaseActivity {
    @BindView(R.id.pic)
    PinchImageView pic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ScreenUtils.setFullScreen(this);
        setContentView(R.layout.activity_zoom);
        getWindow().getDecorView().setBackgroundColor(Color.BLACK);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        String imageUrl = getIntent().getStringExtra("image");
        GlideUtil.loadNormalImg(pic, imageUrl);
        pic.setOnClickListener(v -> finishAfterTransition());
    }

    @Override
    public void finishAfterTransition() {
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.finishAfterTransition();
    }
}

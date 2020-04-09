package com.zxjk.moneyspace.ui;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.blankj.utilcode.util.ScreenUtils;
import com.zxjk.moneyspace.R;
import com.zxjk.moneyspace.ui.base.BaseActivity;
import com.zxjk.moneyspace.ui.widget.PinchImageView;
import com.zxjk.moneyspace.utils.GlideUtil;

import java.util.LinkedList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ProofComplaintActivity extends BaseActivity {
    String imageUrl;
    String[] images;
    final LinkedList<PinchImageView> viewCache = new LinkedList<>();
    @BindView(R.id.pager)
    ViewPager pager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ScreenUtils.setFullScreen(this);
        setContentView(R.layout.activity_proof_complaint);
        getWindow().getDecorView().setBackgroundColor(Color.BLACK);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        imageUrl = getIntent().getStringExtra("images");
        images = imageUrl.split(",");
        pager.setAdapter(new PagerAdapter() {
            @Override
            public int getCount() {
                return images.length;
            }

            @Override
            public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
                return view == object;
            }

            @NonNull
            @Override
            public Object instantiateItem(@NonNull ViewGroup container, int position) {
                PinchImageView piv;
                if (viewCache.size() > 0) {
                    piv = viewCache.remove();
                    piv.reset();
                } else {
                    piv = new PinchImageView(ProofComplaintActivity.this);
                }
                GlideUtil.loadNormalImg(piv, images[position]);
                container.addView(piv);
                piv.setOnClickListener(v -> finishAfterTransition());
                return piv;
            }

            @Override
            public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
                PinchImageView piv = (PinchImageView) object;
                container.removeView(piv);
                viewCache.add(piv);

            }

            @Override
            public void setPrimaryItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
                PinchImageView piv = (PinchImageView) object;
                GlideUtil.loadNormalImg(piv, images[position]);
            }
        });
    }

    @Override
    public void finishAfterTransition() {
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.finishAfterTransition();
    }
}

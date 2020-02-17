package com.zxjk.duoduo.ui.msgpage;

import android.os.Bundle;
import android.view.View;
import android.view.animation.TranslateAnimation;
import android.widget.TextView;

import com.blankj.utilcode.util.ScreenUtils;
import com.zxjk.duoduo.R;
import com.zxjk.duoduo.utils.MapUtils;

import io.rong.imkit.plugin.location.AMapPreviewActivity;
import io.rong.message.LocationMessage;
import razerdp.basepopup.QuickPopupBuilder;
import razerdp.basepopup.QuickPopupConfig;

public class MessageLocationActivity extends AMapPreviewActivity implements View.OnClickListener {
    private LocationMessage locationMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        findViewById(R.id.llBottom).setVisibility(View.VISIBLE);
        findViewById(R.id.ivOpen).setOnClickListener(this);

        TextView tvLocation = findViewById(R.id.tvLocation);
        locationMessage = getIntent().getParcelableExtra("location");
        tvLocation.setText(locationMessage != null ? locationMessage.getPoi() : getString(R.string.cant_load_position));
    }

    @Override
    public void onClick(View view) {
        TranslateAnimation showAnimation = new TranslateAnimation(0f, 0f, ScreenUtils.getScreenHeight(), 0f);
        showAnimation.setDuration(250);
        TranslateAnimation dismissAnimation = new TranslateAnimation(0f, 0f, 0f, ScreenUtils.getScreenHeight());
        dismissAnimation.setDuration(500);
        QuickPopupBuilder.with(this)
                .contentView(R.layout.pop_choosemap)
                .config(new QuickPopupConfig()
                        .withShowAnimation(showAnimation)
                        .withDismissAnimation(dismissAnimation)
                        .withClick(R.id.tv1, v -> openMap(MapUtils.PACKAGE_TECENT), true)
                        .withClick(R.id.tv2, v -> openMap(MapUtils.PACKAGE_GAODE), true))
                .show();
    }

    private void openMap(@MapUtils.MapType String mapType) {
        MapUtils.locate(locationMessage.getLat(), locationMessage.getLng())
                .openMap(this, mapType);
    }
}

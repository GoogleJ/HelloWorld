package com.zxjk.duoduo.ui.webcast;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.blankj.utilcode.util.ToastUtils;
import com.zxjk.duoduo.R;
import com.zxjk.duoduo.ui.base.BaseActivity;

public class VideoAddressActivity extends BaseActivity {
    private ImageView imgEnd;
    private TextView ToViewALive;
    private TextView tvLiveCode;
    private TextView tvRtmpAdd;
    private String chooseFlag;
    private String rtmpAdd;
    private String liveCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_address);

        initView();

        initData();

    }

    private void initView() {
        imgEnd = findViewById(R.id.iv_end);
        imgEnd.setImageDrawable(getResources().getDrawable(R.drawable.ic_more, null));
        ToViewALive = findViewById(R.id.to_view_a_live);
        tvLiveCode = findViewById(R.id.tv_live_code);
        tvRtmpAdd = findViewById(R.id.tv_rtmp_add);
        chooseFlag = getIntent().getStringExtra("chooseFlag");
        rtmpAdd = getIntent().getStringExtra("rtmpAdd");
        liveCode = getIntent().getStringExtra("liveCode");

        findViewById(R.id.rl_back).setOnClickListener(v -> finish());

        findViewById(R.id.tv_copy).setOnClickListener(v -> {
            ClipboardManager clipboard = (ClipboardManager)getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("text", tvRtmpAdd.getText());
            clipboard.setPrimaryClip(clip);
            ToastUtils.showShort(R.string.duplicated_to_clipboard);

        });

        findViewById(R.id.tv_copy2).setOnClickListener(v -> {
            ClipboardManager clipboard = (ClipboardManager)getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("simple text", tvLiveCode.getText());
            clipboard.setPrimaryClip(clip);
            ToastUtils.showShort(R.string.duplicated_to_clipboard);
        });
    }

    private void initData() {
        tvLiveCode.setText(liveCode);
        tvRtmpAdd.setText(rtmpAdd);
        ToViewALive.setOnClickListener(v -> {
            finish();
        });
    }
}
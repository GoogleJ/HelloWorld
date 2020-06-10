package com.zxjk.duoduo.ui.webcast;

import android.content.Context;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.OrientationEventListener;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.pili.pldroid.player.widget.PLVideoTextureView;
import com.pili.pldroid.player.widget.PLVideoView;
import com.zxjk.duoduo.R;
import com.zxjk.duoduo.ui.base.BaseActivity;

public class LivePlayBackActivity extends BaseActivity {

    private LinearLayout ll_back;
    private LinearLayout ll_back2;

    private PLVideoTextureView mVideoView;
    private int mOrientation;
    private AlbumOrientationEventListener mAlbumOrientationEventListener;
    private String playUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_live_play_back);
        setTrasnferStatusBar(true);
        playUrl = getIntent().getStringExtra("playBackUrl");

        mVideoView = findViewById(R.id.PLVideoTextureView);
        ll_back = findViewById(R.id.ll_back);
        ll_back2 = findViewById(R.id.ll_back2);

        View loadingView = findViewById(R.id.LoadingView);
        mVideoView.setBufferingIndicator(loadingView);

        mVideoView.setDisplayAspectRatio(PLVideoView.ASPECT_RATIO_PAVED_PARENT);

        mVideoView.setVideoPath(playUrl);
        mVideoView.setDisplayOrientation(270);
        mVideoView.start();

        ll_back.setOnClickListener(v -> finish());
        ll_back2.setOnClickListener(v -> finish());

        mAlbumOrientationEventListener = new AlbumOrientationEventListener(this, SensorManager.SENSOR_DELAY_NORMAL);
        if (mAlbumOrientationEventListener.canDetectOrientation()) {
            mAlbumOrientationEventListener.enable();
        }
    }

    private class AlbumOrientationEventListener extends OrientationEventListener {
        public AlbumOrientationEventListener(Context context) {
            super(context);
        }

        public AlbumOrientationEventListener(Context context, int rate) {
            super(context, rate);
        }

        @Override
        public void onOrientationChanged(int orientation) {
            if (orientation == OrientationEventListener.ORIENTATION_UNKNOWN) {
                return;
            }

            int newOrientation = ((orientation + 45) / 90 * 90) % 360;
            if (newOrientation != mOrientation) {
                mOrientation = newOrientation;
                    if (mOrientation != 0 && mOrientation != 180) {
                        mVideoView.setDisplayOrientation(mOrientation);
                        if (mOrientation == 90) {
                            ll_back.setVisibility(View.GONE);
                            ll_back2.setVisibility(View.VISIBLE);
                        } else {
                            ll_back.setVisibility(View.VISIBLE);
                            ll_back2.setVisibility(View.GONE);
                        }

                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mVideoView.stopPlayback();
    }
}

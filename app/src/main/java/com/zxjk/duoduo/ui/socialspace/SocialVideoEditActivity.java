package com.zxjk.duoduo.ui.socialspace;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.zxjk.duoduo.R;
import com.zxjk.duoduo.network.rx.RxSchedulers;
import com.zxjk.duoduo.ui.base.BaseActivity;
import com.zxjk.duoduo.utils.GlideUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;

public class SocialVideoEditActivity extends BaseActivity {

    private RecyclerView recycler;
    private BaseQuickAdapter<MediaBean, BaseViewHolder> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_social_video_edit);

        findViewById(R.id.rl_back).setOnClickListener(v -> finish());
        TextView title = findViewById(R.id.tv_title);
        title.setText(R.string.video_manage1);

        recycler = findViewById(R.id.recycler);
        recycler.setLayoutManager(new LinearLayoutManager(this));

        adapter = new BaseQuickAdapter<MediaBean, BaseViewHolder>(R.layout.item_social_video_list, null) {
            @Override
            protected void convert(BaseViewHolder helper, MediaBean item) {
                GlideUtil.loadCornerImg(helper.getView(R.id.ivHead), item.getThumbPath(), 3);
                helper.setText(R.id.tvTitle, item.getDisplayName());
            }
        };
        recycler.setAdapter(adapter);

        getAllVideoInfos();
    }

    /**
     * 获取手机中所有视频的信息
     */
    @SuppressLint("CheckResult")
    private void getAllVideoInfos() {
        Observable.create((ObservableOnSubscribe<List<MediaBean>>) emitter -> {
            List<MediaBean> data = new ArrayList<>();

            Uri mImageUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
            String[] proj = {MediaStore.Video.Thumbnails._ID
                    , MediaStore.Video.Thumbnails.DATA
                    , MediaStore.Video.Media.DURATION
                    , MediaStore.Video.Media.SIZE
                    , MediaStore.Video.Media.DISPLAY_NAME
                    , MediaStore.Video.Media.DATE_MODIFIED};
            Cursor mCursor = getContentResolver().query(mImageUri,
                    proj,
                    MediaStore.Video.Media.MIME_TYPE + "=?",
                    new String[]{"video/mp4"},
                    MediaStore.Video.Media.DATE_MODIFIED + " desc");
            if (mCursor != null) {
                while (mCursor.moveToNext()) {
                    int videoId = mCursor.getInt(mCursor.getColumnIndex(MediaStore.Video.Media._ID));
                    String path = mCursor.getString(mCursor.getColumnIndex(MediaStore.Video.Media.DATA));
                    int duration = mCursor.getInt(mCursor.getColumnIndex(MediaStore.Video.Media.DURATION));
                    long size = mCursor.getLong(mCursor.getColumnIndex(MediaStore.Video.Media.SIZE));
                    if (size < 0) {
                        //某些设备获取size<0，直接计算
                        size = new File(path).length();
                    }
                    String displayName = mCursor.getString(mCursor.getColumnIndex(MediaStore.Video.Media.DISPLAY_NAME));
                    long modifyTime = mCursor.getLong(mCursor.getColumnIndex(MediaStore.Video.Media.DATE_MODIFIED));

                    MediaStore.Video.Thumbnails.getThumbnail(getContentResolver(), videoId, MediaStore.Video.Thumbnails.MICRO_KIND, null);
                    String[] projection = {MediaStore.Video.Thumbnails._ID, MediaStore.Video.Thumbnails.DATA};
                    Cursor cursor = getContentResolver().query(MediaStore.Video.Thumbnails.EXTERNAL_CONTENT_URI
                            , projection
                            , MediaStore.Video.Thumbnails.VIDEO_ID + "=?"
                            , new String[]{videoId + ""}
                            , null);
                    String thumbPath = "";
                    while (cursor.moveToNext()) {
                        thumbPath = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Thumbnails.DATA));
                    }
                    cursor.close();
                    data.add(new MediaBean(path, thumbPath, duration, size, displayName, modifyTime));
                }
                mCursor.close();
            }
            emitter.onNext(data);
        }).compose(bindToLifecycle())
                .compose(RxSchedulers.ioObserver())
                .subscribe(adapter::setNewData);
    }

    static class MediaBean {
        private String path;
        private String thumbPath;
        private long duration;
        private long size;
        private String displayName;
        private long modifyTime;

        public MediaBean(String path, String thumbPath, long duration, long size, String displayName, long modifyTime) {
            this.path = path;
            this.thumbPath = thumbPath;
            this.duration = duration;
            this.size = size;
            this.displayName = displayName;
            this.modifyTime = modifyTime;
        }

        public String getPath() {
            return path;
        }

        public String getThumbPath() {
            return thumbPath;
        }

        public long getDuration() {
            return duration;
        }

        public long getSize() {
            return size;
        }

        public String getDisplayName() {
            return displayName;
        }

        public long getModifyTime() {
            return modifyTime;
        }
    }
}

package com.zxjk.duoduo.ui.socialspace;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.text.format.Formatter;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.blankj.utilcode.util.FileUtils;
import com.blankj.utilcode.util.GsonUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.mabeijianxi.smallvideorecord2.LocalMediaCompress;
import com.mabeijianxi.smallvideorecord2.model.AutoVBRMode;
import com.mabeijianxi.smallvideorecord2.model.BaseMediaBitrateConfig;
import com.mabeijianxi.smallvideorecord2.model.LocalMediaConfig;
import com.mabeijianxi.smallvideorecord2.model.OnlyCompressOverBean;
import com.trello.rxlifecycle3.android.ActivityEvent;
import com.zxjk.duoduo.R;
import com.zxjk.duoduo.bean.request.EditCommunityVideoRequest;
import com.zxjk.duoduo.network.Api;
import com.zxjk.duoduo.network.ServiceFactory;
import com.zxjk.duoduo.network.rx.RxSchedulers;
import com.zxjk.duoduo.ui.base.BaseActivity;
import com.zxjk.duoduo.ui.widget.dialog.LoadingDialog;
import com.zxjk.duoduo.utils.CommonUtils;
import com.zxjk.duoduo.utils.GlideUtil;
import com.zxjk.duoduo.utils.OssUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;

public class SocialVideoAddActivity extends BaseActivity {

    private LinearLayout llTopTips;
    private TextView tvCurrentCount;
    private TextView tvMaxCount;
    private ArrayList<MediaBean> selectedVideos;
    private RecyclerView recycler;
    private BaseQuickAdapter<MediaBean, BaseViewHolder> adapter;
    private java.util.Formatter timeFormatter;
    private StringBuilder mFormatBuilder;

    private int currentCount;
    private int maxCount;

    //200M
    private final String MAX_VIDEO_SIZE = "209715200";
    //10M
    private final String MIN_VIDEO_SIZE = "10485760";
    //6min
    private final String MAX_VIDEO_DURATION = "360000";
    //10S
    private final String MIN_VIDEO_DURATION = "10000";

    private LoadingDialog uploadLoading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_social_video_add);

        maxCount = getIntent().getIntExtra("currentMax", 3);

        uploadLoading = new LoadingDialog(this, getString(R.string.uploading));
        uploadLoading.setDelayTimeStamp(0);

        selectedVideos = new ArrayList<>(maxCount);

        mFormatBuilder = new StringBuilder();
        timeFormatter = new java.util.Formatter(mFormatBuilder, Locale.ENGLISH);

        findViewById(R.id.rl_back).setOnClickListener(v -> finish());
        TextView title = findViewById(R.id.tv_title);
        title.setText(R.string.video_add);

        tvMaxCount = findViewById(R.id.tvMaxCount);
        tvMaxCount.setText(getString(R.string.tips_uploadSocialVideo));
        llTopTips = findViewById(R.id.llTopTips);
        tvCurrentCount = findViewById(R.id.tvCurrentCount);
        if (maxCount == 0) {
            tvCurrentCount.setText(getString(R.string.hitMaxUploadCount));
        } else {
            tvCurrentCount.setText(getString(R.string.selected) + " (0" + "/" + maxCount + ")");
        }
        recycler = findViewById(R.id.recycler);
        recycler.setLayoutManager(new LinearLayoutManager(this));
        recycler.setItemAnimator(null);

        adapter = new BaseQuickAdapter<MediaBean, BaseViewHolder>(R.layout.item_social_video_list, null) {
            @Override
            protected void convert(BaseViewHolder helper, MediaBean item) {
                GlideUtil.loadCornerImg(helper.getView(R.id.ivHead), item.getThumbPath(), 4);
                helper.setText(R.id.tvTitle, item.getDisplayName())
                        .setText(R.id.tvSize, Formatter.formatFileSize(SocialVideoAddActivity.this, item.getSize()))
                        .setText(R.id.tvDuration, stringForTime(item.getDuration()));
                helper.setChecked(R.id.cbSelectVideo, item.isChecked);
            }
        };

        adapter.setOnItemClickListener((adapter, view, position) -> {
            MediaBean b = (MediaBean) adapter.getData().get(position);
            if (!b.isChecked() && currentCount == maxCount) {
                return;
            }
            if (!b.isChecked()) {
                b.setChecked(true);
                selectedVideos.add(((MediaBean) adapter.getData().get(position)));
                currentCount += 1;
                tvCurrentCount.setText(getString(R.string.selected) + " (" + currentCount + "/" + maxCount + ")");
                adapter.notifyItemChanged(position);
            } else {
                b.setChecked(false);
                selectedVideos.remove(adapter.getData().get(position));
                currentCount -= 1;
                tvCurrentCount.setText(getString(R.string.selected) + " (" + currentCount + "/" + maxCount + ")");
                adapter.notifyItemChanged(position);
            }
        });

        View emptyview = LayoutInflater.from(this).inflate(R.layout.empty_publicgroup, null, false);
        TextView tv = emptyview.findViewById(R.id.tv);
        ImageView iv = emptyview.findViewById(R.id.iv);
        tv.setText(R.string.emptylist4);
        iv.setImageResource(R.drawable.ic_empty_videos);

        adapter.setEmptyView(emptyview);

        recycler.setAdapter(adapter);

        getAllVideoInfos();
    }

    private String stringForTime(long timeMs) {
        long totalSeconds = timeMs / 1000;
        long seconds = totalSeconds % 60;

        long minutes = (totalSeconds / 60) % 60;

        long hours = totalSeconds / 3600;

        mFormatBuilder.setLength(0);
        if (hours > 0) {
            return timeFormatter.format("%d:%02d:%02d", hours, minutes, seconds)
                    .toString();
        } else {
            return timeFormatter.format("%02d:%02d", minutes, seconds).toString();
        }
    }

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
            Cursor mCursor = getContentResolver().query(mImageUri, proj,
                    MediaStore.Video.Media.MIME_TYPE + "=?" +
                            "and " + MediaStore.Video.Media.DURATION + "<=?" +
                            "and " + MediaStore.Video.Media.DURATION + ">=?" +
                            "and " + MediaStore.Video.Media.SIZE + "<=?" +
                            "and " + MediaStore.Video.Media.SIZE + ">=?",
                    new String[]{"video/mp4", MAX_VIDEO_DURATION, MIN_VIDEO_DURATION, MAX_VIDEO_SIZE, MIN_VIDEO_SIZE},
                    MediaStore.Video.Media.DATE_MODIFIED + " desc");
            if (mCursor != null) {
                while (mCursor.moveToNext()) {
                    int videoId = mCursor.getInt(mCursor.getColumnIndex(MediaStore.Video.Media._ID));
                    String path = mCursor.getString(mCursor.getColumnIndex(MediaStore.Video.Media.DATA));
                    long duration = mCursor.getLong(mCursor.getColumnIndex(MediaStore.Video.Media.DURATION));
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
                    if (!TextUtils.isEmpty(thumbPath)) {
                        data.add(new MediaBean(path, thumbPath, duration, size, displayName, modifyTime));
                    }
                }
                mCursor.close();
            }
            emitter.onNext(data);
        }).compose(bindToLifecycle())
                .compose(RxSchedulers.ioObserver())
                .subscribe(adapter::setNewData, t -> {
                });
    }

    public void closeTip(View view) {
        llTopTips.setVisibility(View.GONE);
    }

    private List<EditCommunityVideoRequest.VideoListBean> uploadList;

    public void uploadVideo(View view) {
        if (currentCount == 0) {
            ToastUtils.showShort(R.string.selectVideos);
            return;
        }
        uploadList = new ArrayList<>(maxCount);
        uploadVideos();
    }

    @SuppressLint("CheckResult")
    private void uploadVideos() {
        if (uploadLoading.isShowing()) {
            uploadLoading.dismissReally();
        }

        if (selectedVideos.size() == 0) {
            if (uploadList.size() != 0) {
                //do upload
                EditCommunityVideoRequest request = new EditCommunityVideoRequest();
                request.setType("add");
                request.setGroupId(getIntent().getStringExtra("id"));
                request.setVideoList(uploadList);

                ServiceFactory.getInstance().getBaseService(Api.class)
                        .editCommunityVideo(GsonUtils.toJson(request, false))
                        .compose(bindToLifecycle())
                        .compose(RxSchedulers.normalTrans())
                        .compose(RxSchedulers.ioObserver(CommonUtils.initDialog(this)))
                        .subscribe(s -> {
                            ToastUtils.showShort(R.string.upload_social_video_success);
                            Intent intent = new Intent();
                            setResult(1, intent);
                            finish();
                        }, t -> {
                            finish();
                            handleApiError(t);
                        });
            }
            return;
        }

        uploadLoading.setText(getString(R.string.ziping_pleaseWait));
        uploadLoading.show();

        Observable.create((ObservableOnSubscribe<OnlyCompressOverBean>) e -> {
            LocalMediaConfig.Buidler buidler = new LocalMediaConfig.Buidler();
            final LocalMediaConfig config = buidler
                    .setVideoPath(selectedVideos.get(0).getPath())
                    .captureThumbnailsTime(1)
                    .doH264Compress(new AutoVBRMode(32).setVelocity(BaseMediaBitrateConfig.Velocity.ULTRAFAST))
                    .setFramerate(24)
                    .build();
            LocalMediaCompress compress = new LocalMediaCompress(config);
            e.onNext(compress.startCompress());
        }).compose(bindUntilEvent(ActivityEvent.DESTROY)).compose(RxSchedulers.ioObserver())
                .doOnError(throwable -> {
                    if (uploadLoading.isShowing()) uploadLoading.dismissReally();
                })
                .doOnDispose(() -> {
                    if (uploadLoading.isShowing()) uploadLoading.dismissReally();
                })
                .doOnTerminate(() -> {
                    if (uploadLoading.isShowing()) uploadLoading.dismissReally();
                })
                .subscribe(onlyCompressOverBean -> {
                    if (onlyCompressOverBean.isSucceed()) {
                        OssUtils.uploadFile(onlyCompressOverBean.getVideoPath(), new OssUtils.OssCallBack1() {
                            @Override
                            public void onSuccess(String videoAddress) {
                                uploadLoading.setText(getString(R.string.uploading_thumb));
                                OssUtils.uploadFile(selectedVideos.get(0).getThumbPath(), new OssUtils.OssCallBack1() {
                                    @Override
                                    public void onSuccess(String thumbUrl) {
                                        EditCommunityVideoRequest.VideoListBean bean = new EditCommunityVideoRequest.VideoListBean();
                                        bean.setVideoAddress(videoAddress);
                                        bean.setVideoPic(thumbUrl);
                                        bean.setVideoDuration(selectedVideos.get(0).getDuration() + "");
                                        bean.setVideoName(selectedVideos.get(0).getDisplayName());
                                        uploadList.add(bean);
                                        selectedVideos.remove(0);

                                        if (!TextUtils.isEmpty(onlyCompressOverBean.getVideoPath())) {
                                            File file = new File(onlyCompressOverBean.getVideoPath());
                                            try {
                                                FileUtils.deleteDir(file.getParentFile());
                                            } catch (Exception e) {
                                            }
                                        }

                                        uploadVideos();
                                    }

                                    @Override
                                    public void onFail() {
                                        ToastUtils.showShort(R.string.upload_social_video_fail);
                                        if (uploadLoading.isShowing()) {
                                            uploadLoading.dismissReally();
                                        }
                                        if (!TextUtils.isEmpty(onlyCompressOverBean.getVideoPath())) {
                                            File file = new File(onlyCompressOverBean.getVideoPath());
                                            try {
                                                FileUtils.deleteDir(file.getParentFile());
                                            } catch (Exception e) {
                                            }
                                        }
                                    }
                                }, null);
                            }

                            @Override
                            public void onFail() {
                                ToastUtils.showShort(R.string.upload_social_video_fail);
                                if (uploadLoading.isShowing()) {
                                    uploadLoading.dismissReally();
                                }
                                if (!TextUtils.isEmpty(onlyCompressOverBean.getVideoPath())) {
                                    File file = new File(onlyCompressOverBean.getVideoPath());
                                    try {
                                        FileUtils.deleteDir(file.getParentFile());
                                    } catch (Exception e) {
                                    }
                                }
                            }
                        }, progress -> runOnUiThread(() -> uploadLoading.setText(getString(R.string.uploading) + "," + (int) (progress * 100) + "%")));
                    } else {
                        Observable.error(new Exception());
                    }
                }, t -> ToastUtils.showShort(R.string.upload_social_video_fail));
    }

    static class MediaBean {
        private String path;
        private String thumbPath;
        private long duration;
        private long size;
        private String displayName;
        private long modifyTime;

        private boolean isChecked;

        public boolean isChecked() {
            return isChecked;
        }

        public void setChecked(boolean checked) {
            isChecked = checked;
        }

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

package com.zxjk.duoduo.ui.socialspace;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.blankj.utilcode.util.GsonUtils;
import com.blankj.utilcode.util.ScreenUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.zxjk.duoduo.R;
import com.zxjk.duoduo.bean.request.EditCommunityVideoRequest;
import com.zxjk.duoduo.bean.response.EditListCommunityCultureResponse;
import com.zxjk.duoduo.bean.response.SocialCaltureListBean;
import com.zxjk.duoduo.network.Api;
import com.zxjk.duoduo.network.ServiceFactory;
import com.zxjk.duoduo.network.rx.RxSchedulers;
import com.zxjk.duoduo.ui.base.BaseActivity;
import com.zxjk.duoduo.ui.widget.dialog.MuteRemoveDialog;
import com.zxjk.duoduo.utils.CommonUtils;
import com.zxjk.duoduo.utils.GlideUtil;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

import razerdp.basepopup.QuickPopupBuilder;
import razerdp.basepopup.QuickPopupConfig;

public class SocialVideoEditActivity extends BaseActivity {

    private static final int REQUEST_ADD = 1;
    private static final int REQUEST_RENAME = 2;

    private LinearLayout llTopTips;
    private TextView tvMaxCount;
    private RecyclerView recycler;
    private BaseQuickAdapter<EditListCommunityCultureResponse.VideoBean.VideoListBean, BaseViewHolder> adapter;

    private SocialCaltureListBean bean;

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd HH:mm上传");

    private int maxCount;

    private java.util.Formatter timeFormatter;
    private StringBuilder mFormatBuilder;

    @SuppressLint("CheckResult")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_social_video_edit);

        mFormatBuilder = new StringBuilder();
        timeFormatter = new java.util.Formatter(mFormatBuilder, Locale.ENGLISH);

        bean = getIntent().getParcelableExtra("bean");

        llTopTips = findViewById(R.id.llTopTips);
        tvMaxCount = findViewById(R.id.tvMaxCount);
        findViewById(R.id.rl_back).setOnClickListener(v -> finish());
        TextView title = findViewById(R.id.tv_title);
        title.setText(R.string.video_manage1);

        recycler = findViewById(R.id.recycler);

        recycler.setLayoutManager(new LinearLayoutManager(this));

        adapter = new BaseQuickAdapter<EditListCommunityCultureResponse.VideoBean.VideoListBean, BaseViewHolder>(R.layout.item_social_video_list_edit, null) {
            @Override
            protected void convert(BaseViewHolder helper, EditListCommunityCultureResponse.VideoBean.VideoListBean item) {
                GlideUtil.loadCornerImg(helper.getView(R.id.ivHead), item.getVideoPic(), 4);
                helper.setText(R.id.tvTitle, item.getVideoName())
                        .setText(R.id.tvDuration, stringForTime(Long.parseLong(item.getVideoDuration())))
                        .setText(R.id.tvUploadDate, sdf.format(Long.parseLong(item.getCreateTime())));
            }
        };

        View emptyview = LayoutInflater.from(this).inflate(R.layout.empty_publicgroup, null, false);
        TextView tv = emptyview.findViewById(R.id.tv);
        ImageView iv = emptyview.findViewById(R.id.iv);
        tv.setText(R.string.emptylist4);
        iv.setImageResource(R.drawable.ic_empty_videos);
        adapter.setEmptyView(emptyview);

        adapter.setOnItemClickListener((adapter, view, position) -> {
            TranslateAnimation showAnimation = new TranslateAnimation(0f, 0f, ScreenUtils.getScreenHeight(), 0f);
            showAnimation.setDuration(250);
            TranslateAnimation dismissAnimation = new TranslateAnimation(0f, 0f, 0f, ScreenUtils.getScreenHeight());
            dismissAnimation.setDuration(500);
            QuickPopupBuilder.with(SocialVideoEditActivity.this)
                    .contentView(R.layout.popup_socialvideo_edit)
                    .config(new QuickPopupConfig()
                            .withShowAnimation(showAnimation)
                            .withDismissAnimation(dismissAnimation)
                            .withClick(R.id.tv1, v -> renameVideo(position), true)
                            .withClick(R.id.tv2, v -> ToastUtils.showShort(R.string.developing), true)
                            .withClick(R.id.tv3, v -> deleteVideo(position), true)
                            .withClick(R.id.tv4, null, true))
                    .show();
        });

        recycler.setAdapter(adapter);

        initData();
    }

    @Override
    public void finish() {
        Intent intent = new Intent();
        intent.putExtra("data", bean);
        setResult(1, intent);
        super.finish();
    }

    private int currentRenamePosition;

    private void renameVideo(int position) {
        currentRenamePosition = position;
        EditListCommunityCultureResponse.VideoBean.VideoListBean videoBean = adapter.getData().get(position);
        Intent intent = new Intent(this, EditSocialVideoNameActivity.class);
        intent.putExtra("videoName", videoBean.getVideoName());
        intent.putExtra("videoId", videoBean.getVideoId());
        intent.putExtra("groupId", getIntent().getStringExtra("id"));
        startActivityForResult(intent, REQUEST_RENAME);
    }

    @SuppressLint("CheckResult")
    private void deleteVideo(int position) {
        MuteRemoveDialog dialog = new MuteRemoveDialog(this, "确认", "取消", "提示", "是否确定删除此视频？");
        dialog.setOnCancelListener(() -> {
            EditCommunityVideoRequest request = new EditCommunityVideoRequest();
            request.setGroupId(getIntent().getStringExtra("id"));
            request.setVideoId(adapter.getData().get(position).getVideoId());
            request.setType("del");
            ServiceFactory.getInstance().getBaseService(Api.class)
                    .editCommunityVideo(GsonUtils.toJson(request))
                    .compose(bindToLifecycle())
                    .compose(RxSchedulers.ioObserver(CommonUtils.initDialog(this)))
                    .compose(RxSchedulers.normalTrans())
                    .subscribe(s -> {
                        adapter.getData().remove(position);
                        adapter.notifyItemChanged(position);
                    }, this::handleApiError);
        });
        dialog.show();
    }

    @SuppressLint("CheckResult")
    private void initData() {
        ServiceFactory.getInstance().getBaseService(Api.class)
                .communityVideoList(getIntent().getStringExtra("id"))
                .compose(bindToLifecycle())
                .compose(RxSchedulers.normalTrans())
                .compose(RxSchedulers.ioObserver(CommonUtils.initDialog(this)))
                .subscribe(r -> {
                    maxCount = Integer.parseInt(r.getVideoCreate());
                    tvMaxCount.setText("最多上传" + maxCount + "份企业宣传视频，请上传体验");
                    adapter.setNewData(r.getVideo());
                    if (r.getVideo() == null || r.getVideo().size() == 0) {
                        bean.getVideo().setVideoList(new ArrayList<>());
                    } else {
                        bean.getVideo().setVideoList(r.getVideo());
                    }
                }, this::handleApiError);
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

    public void closeTip(View view) {
        llTopTips.setVisibility(View.GONE);
    }

    public void createVideo(View view) {
        if (bean.getVideo().getVideoList().size() == maxCount) {
            ToastUtils.showShort(R.string.upload_video_max);
            return;
        }
        Intent intent = new Intent(this, SocialVideoAddActivity.class);
        intent.putExtra("id", getIntent().getStringExtra("id"));
        intent.putExtra("currentMax", maxCount - bean.getVideo().getVideoList().size());
        intent.putExtra("maxCount", maxCount);
        startActivityForResult(intent, REQUEST_ADD);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (data == null) return;
        if (resultCode != 1) return;

        switch (requestCode) {
            case REQUEST_ADD:
                initData();
                break;
            case REQUEST_RENAME:
                adapter.getData().get(currentRenamePosition).setVideoName(data.getStringExtra("name"));
                bean.getVideo().getVideoList().get(currentRenamePosition).setVideoName(data.getStringExtra("name"));
                adapter.notifyItemChanged(currentRenamePosition);
        }
    }
}

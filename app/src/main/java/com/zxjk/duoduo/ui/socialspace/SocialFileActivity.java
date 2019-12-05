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
import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.zxjk.duoduo.R;
import com.zxjk.duoduo.bean.request.EditCommunityFileRequest;
import com.zxjk.duoduo.bean.response.EditListCommunityCultureResponse;
import com.zxjk.duoduo.bean.response.SocialCaltureListBean;
import com.zxjk.duoduo.network.Api;
import com.zxjk.duoduo.network.ServiceFactory;
import com.zxjk.duoduo.network.rx.RxSchedulers;
import com.zxjk.duoduo.ui.base.BaseActivity;
import com.zxjk.duoduo.ui.widget.dialog.MuteRemoveDialog;
import com.zxjk.duoduo.utils.CommonUtils;

import java.text.SimpleDateFormat;

import razerdp.basepopup.QuickPopupBuilder;
import razerdp.basepopup.QuickPopupConfig;

@SuppressLint("CheckResult")
public class SocialFileActivity extends BaseActivity {

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd HH:mm上传");

    private final int REQUEST_ADD = 1;
    private final int REQUEST_MODIFY = 2;
    private final int REQUEST_DELETE = 3;

    private LinearLayout llTopTips;
    private TextView tvMaxCount;
    private RecyclerView recycler;
    private BaseQuickAdapter<EditListCommunityCultureResponse.FilesBean.FilesListBean, BaseViewHolder> adapter;

    private SocialCaltureListBean bean;

    private int maxCount;
    private int currentEditIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_social_file);

        bean = getIntent().getParcelableExtra("bean");

        TextView title = findViewById(R.id.tv_title);
        title.setText(R.string.social_files);
        findViewById(R.id.rl_back).setOnClickListener(v -> finish());

        llTopTips = findViewById(R.id.llTopTips);
        tvMaxCount = findViewById(R.id.tvMaxCount);
        recycler = findViewById(R.id.recycler);

        adapter = new BaseQuickAdapter<EditListCommunityCultureResponse.FilesBean.FilesListBean, BaseViewHolder>(R.layout.item_social_files_list_edit, null) {
            @Override
            protected void convert(BaseViewHolder helper, EditListCommunityCultureResponse.FilesBean.FilesListBean item) {
                ImageView ivHead = helper.getView(R.id.ivHead);

                switch (item.getFileFormat()) {
                    case "excel":
                        Glide.with(SocialFileActivity.this).load(R.drawable.ic_social_file_excel).into(ivHead);
                        break;
                    case "ppt":
                        Glide.with(SocialFileActivity.this).load(R.drawable.ic_social_file_ppt).into(ivHead);
                        break;
                    case "word":
                        Glide.with(SocialFileActivity.this).load(R.drawable.ic_social_file_word).into(ivHead);
                        break;
                    case "pdf":
                        Glide.with(SocialFileActivity.this).load(R.drawable.ic_social_file_pdf).into(ivHead);
                        break;
                }

                helper.setText(R.id.tvTitle, item.getFileName())
                        .setText(R.id.tvUploadDate, item.getFileSize() + "   " + sdf.format(item.getCreateTime()));
            }
        };

        View emptyview = LayoutInflater.from(this).inflate(R.layout.empty_publicgroup, null, false);
        TextView tv = emptyview.findViewById(R.id.tv);
        ImageView iv = emptyview.findViewById(R.id.iv);
        tv.setText(R.string.emptylist5);
        iv.setImageResource(R.drawable.ic_empty_videos);
        adapter.setEmptyView(emptyview);

        adapter.setOnItemClickListener((adapter, view, position) -> {
            TranslateAnimation showAnimation = new TranslateAnimation(0f, 0f, ScreenUtils.getScreenHeight(), 0f);
            showAnimation.setDuration(250);
            TranslateAnimation dismissAnimation = new TranslateAnimation(0f, 0f, 0f, ScreenUtils.getScreenHeight());
            dismissAnimation.setDuration(500);
            QuickPopupBuilder.with(this)
                    .contentView(R.layout.popup_socialfiles_edit)
                    .config(new QuickPopupConfig()
                            .withShowAnimation(showAnimation)
                            .withDismissAnimation(dismissAnimation)
                            .withClick(R.id.tv1, v -> renameFile(position), true)
                            .withClick(R.id.tv2, v -> deleteFile(position), true)
                            .withClick(R.id.tv3, null, true))
                    .show();
        });

        recycler.setAdapter(adapter);
        recycler.setLayoutManager(new LinearLayoutManager(this));

        initData();
    }

    private void deleteFile(int position) {
        MuteRemoveDialog dialog = new MuteRemoveDialog(this, "确认", "取消", "提示", "是否确定删除此视频？");
        dialog.setOnCancelListener(() -> {
            EditCommunityFileRequest request = new EditCommunityFileRequest();
            request.setGroupId(getIntent().getStringExtra("id"));
            request.setFileId(adapter.getData().get(position).getFileId());
            request.setType("del");
            ServiceFactory.getInstance().getBaseService(Api.class)
                    .editCommunityFile(GsonUtils.toJson(request))
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

    private void renameFile(int position) {
        currentEditIndex = position;
        EditListCommunityCultureResponse.FilesBean.FilesListBean filesListBean = adapter.getData().get(position);
        Intent intent = new Intent(this, EditSocialVideoNameActivity.class);
        intent.putExtra("fromFile", true);
        intent.putExtra("fileName", filesListBean.getFileName());
        intent.putExtra("fileId", filesListBean.getFileId());
        intent.putExtra("groupId", getIntent().getStringExtra("id"));
        startActivityForResult(intent, REQUEST_MODIFY);
    }

    @SuppressLint("CheckResult")
    private void initData() {
        ServiceFactory.getInstance().getBaseService(Api.class)
                .communityFilesList(getIntent().getStringExtra("id"))
                .compose(bindToLifecycle())
                .compose(RxSchedulers.normalTrans())
                .compose(RxSchedulers.ioObserver(CommonUtils.initDialog(this)))
                .subscribe(r -> {
                    maxCount = Integer.parseInt(r.getFilesCreate());
                    tvMaxCount.setText("最多上传" + maxCount + "份社群资料，请上传体验");
                    bean.getFiles().setFilesList(r.getFiles());
                    adapter.setNewData(r.getFiles());
                }, this::handleApiError);
    }

    @Override
    public void finish() {
        Intent intent = new Intent();
        intent.putExtra("data", bean);
        setResult(1, intent);

        super.finish();
    }

    public void closeTip(View view) {
        llTopTips.setVisibility(View.GONE);
    }

    public void createFile(View view) {
        if (bean.getFiles().getFilesList().size() == maxCount) {
            ToastUtils.showShort(R.string.upload_file_max);
            return;
        }
        Intent intent = new Intent(this, SocialFileEditActivity.class);
        intent.putExtra("id", getIntent().getStringExtra("id"));
        intent.putExtra("maxCount", maxCount);
        intent.putExtra("currentMax", maxCount - bean.getFiles().getFilesList().size());
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
            case REQUEST_MODIFY:
                adapter.getData().get(currentEditIndex).setFileName(data.getStringExtra("fileName"));
                adapter.notifyItemChanged(currentEditIndex);
                bean.getFiles().getFilesList().get(currentEditIndex).setFileName(data.getStringExtra("fileName"));
                break;
            case REQUEST_DELETE:
                initData();
                break;
        }
    }
}

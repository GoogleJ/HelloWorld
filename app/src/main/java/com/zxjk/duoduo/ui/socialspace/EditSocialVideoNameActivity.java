package com.zxjk.duoduo.ui.socialspace;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.blankj.utilcode.util.GsonUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.zxjk.duoduo.R;
import com.zxjk.duoduo.bean.request.EditCommunityFileRequest;
import com.zxjk.duoduo.bean.request.EditCommunityVideoRequest;
import com.zxjk.duoduo.network.Api;
import com.zxjk.duoduo.network.ServiceFactory;
import com.zxjk.duoduo.network.rx.RxSchedulers;
import com.zxjk.duoduo.ui.base.BaseActivity;
import com.zxjk.duoduo.utils.CommonUtils;

public class EditSocialVideoNameActivity extends BaseActivity {

    private EditText et;
    private TextView tvTitle;

    private boolean fromFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_social_video_name);

        et = findViewById(R.id.et);
        tvTitle = findViewById(R.id.tvTitle);

        fromFile = getIntent().getBooleanExtra("fromFile", false);
        if (fromFile) {
            tvTitle.setText("资料重命名");
            et.setHint("商业计划书名称");
        }
    }

    @SuppressLint("CheckResult")
    public void save(View view) {
        String trim = et.getText().toString().trim();
        if (TextUtils.isEmpty(trim)) {
            ToastUtils.showShort(R.string.input_empty);
            return;
        }

        if (fromFile) {
            EditCommunityFileRequest request = new EditCommunityFileRequest();
            request.setGroupId(getIntent().getStringExtra("groupId"));
            request.setFileId(getIntent().getStringExtra("fileId"));
            request.setType("update");
            request.setFileName(trim);

            ServiceFactory.getInstance().getBaseService(Api.class)
                    .editCommunityFile(GsonUtils.toJson(request))
                    .compose(bindToLifecycle())
                    .compose(RxSchedulers.normalTrans())
                    .compose(RxSchedulers.ioObserver(CommonUtils.initDialog(this)))
                    .subscribe(s -> {
                        ToastUtils.showShort(R.string.success1);
                        Intent result = new Intent();
                        result.putExtra("fileName", trim);
                        setResult(1, result);
                        finish();
                    }, this::handleApiError);
            return;
        }

        EditCommunityVideoRequest request = new EditCommunityVideoRequest();
        request.setGroupId(getIntent().getStringExtra("groupId"));
        request.setVideoId(getIntent().getStringExtra("videoId"));
        request.setType("update");
        request.setVideoName(trim);

        ServiceFactory.getInstance().getBaseService(Api.class)
                .editCommunityVideo(GsonUtils.toJson(request))
                .compose(bindToLifecycle())
                .compose(RxSchedulers.normalTrans())
                .compose(RxSchedulers.ioObserver(CommonUtils.initDialog(this)))
                .subscribe(s -> {
                    ToastUtils.showShort(R.string.success1);
                    Intent result = new Intent();
                    result.putExtra("name", trim);
                    setResult(1, result);
                    finish();
                }, this::handleApiError);
    }

    public void cancel(View view) {
        finish();
    }
}

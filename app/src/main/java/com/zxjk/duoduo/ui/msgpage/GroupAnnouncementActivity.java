package com.zxjk.duoduo.ui.msgpage;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.blankj.utilcode.util.GsonUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.zxjk.duoduo.R;
import com.zxjk.duoduo.network.Api;
import com.zxjk.duoduo.network.ServiceFactory;
import com.zxjk.duoduo.bean.response.GroupResponse;
import com.zxjk.duoduo.network.rx.RxSchedulers;
import com.zxjk.duoduo.ui.base.BaseActivity;
import com.zxjk.duoduo.utils.CommonUtils;

import io.rong.imkit.userInfoCache.RongUserInfoManager;
import io.rong.imlib.model.Group;

@SuppressLint("CheckResult")
public class GroupAnnouncementActivity extends BaseActivity {

    EditText announcementEdit;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_announcement);
        TextView tv_title = findViewById(R.id.tv_title);
        tv_title.setText(getString(R.string.group_announcement));
        TextView tv_commit = findViewById(R.id.tv_commit);
        tv_commit.setVisibility(View.VISIBLE);
        tv_commit.setText(getString(R.string.ok));
        findViewById(R.id.rl_back).setOnClickListener(v -> finish());
        tv_commit.setOnClickListener(v -> {
            GroupResponse.GroupInfoBean request = new GroupResponse.GroupInfoBean();
            if (!TextUtils.isEmpty(announcementEdit.getText().toString())) {
                request.setId(getIntent().getStringExtra("groupId"));
                request.setGroupNotice(announcementEdit.getText().toString());
                updateGroupInfo(GsonUtils.toJson(request));
            } else {
                ToastUtils.showShort(getString(R.string.please_input_announcement));
            }
        });
        announcementEdit = findViewById(R.id.announcement_edit);
    }


    public void updateGroupInfo(String groupInfo) {
        ServiceFactory.getInstance().getBaseService(Api.class)
                .updateGroupInfo(groupInfo)
                .compose(bindToLifecycle())
                .compose(RxSchedulers.ioObserver(CommonUtils.initDialog(this)))
                .compose(RxSchedulers.normalTrans())
                .subscribe(groupResponse -> {
                    RongUserInfoManager.getInstance().setGroupInfo(new Group(groupResponse.getId(),
                            groupResponse.getGroupNikeName(), Uri.parse(groupResponse.getHeadPortrait())));
                    ToastUtils.showShort(GroupAnnouncementActivity.this.getString(R.string.announcement_edit_successful));
                    Intent intent = new Intent();
                    intent.putExtra("result", announcementEdit.getText().toString().trim());
                    setResult(3, intent);
                    finish();
                }, this::handleApiError);
    }
}

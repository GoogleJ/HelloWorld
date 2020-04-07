package com.zxjk.moneyspace.ui.msgpage;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.blankj.utilcode.util.ToastUtils;
import com.zxjk.moneyspace.R;
import com.zxjk.moneyspace.network.Api;
import com.zxjk.moneyspace.network.ServiceFactory;
import com.zxjk.moneyspace.network.rx.RxSchedulers;
import com.zxjk.moneyspace.ui.base.BaseActivity;
import com.zxjk.moneyspace.utils.CommonUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.rong.imkit.userInfoCache.RongUserInfoManager;
import io.rong.imlib.model.UserInfo;


@SuppressLint("CheckResult")
public class ModifyNotesActivity extends BaseActivity {
    @BindView(R.id.m_modify_notes_edit)
    EditText modifyNotesEdit;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_notes);
        ButterKnife.bind(this);
        TextView tv_title = findViewById(R.id.tv_title);
        TextView tv_commit = findViewById(R.id.tv_commit);
        String currentName = getIntent().getStringExtra("name");
        tv_title.setText(getString(R.string.note));
        tv_commit.setVisibility(View.VISIBLE);
        tv_commit.setText(getString(R.string.queding));
        tv_commit.setOnClickListener(v -> {
            String s = modifyNotesEdit.getText().toString();
            updateRemark(getIntent().getStringExtra("friendId"), s);
        });
        findViewById(R.id.rl_back).setOnClickListener(v -> finish());
        modifyNotesEdit.setText(currentName);
        modifyNotesEdit.setSelection(currentName.length() > 20 ? 20 : currentName.length());
    }

    public void updateRemark(String friendId, String remark) {
        ServiceFactory.getInstance().getBaseService(Api.class)
                .updateRemark(friendId, remark)
                .compose(bindToLifecycle())
                .compose(RxSchedulers.ioObserver(CommonUtils.initDialog(this)))
                .compose(RxSchedulers.normalTrans())
                .subscribe(r -> {
                    RongUserInfoManager.getInstance().setUserInfo(new UserInfo(r.getId(),
                            TextUtils.isEmpty(remark) ? getIntent().getStringExtra("nick") : remark, Uri.parse(r.getHeadPortrait())));
                    ToastUtils.showShort(getString(R.string.successfully_modified));
                    Intent intent = new Intent();
                    if (!TextUtils.isEmpty(remark)) {
                        intent.putExtra("remark", remark);
                    }
                    setResult(1, intent);
                    finish();
                }, this::handleApiError);
    }
}

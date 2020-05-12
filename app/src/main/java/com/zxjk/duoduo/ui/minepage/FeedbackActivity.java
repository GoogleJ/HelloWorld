package com.zxjk.duoduo.ui.minepage;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.blankj.utilcode.util.ToastUtils;
import com.zxjk.duoduo.R;
import com.zxjk.duoduo.network.Api;
import com.zxjk.duoduo.network.ServiceFactory;
import com.zxjk.duoduo.network.rx.RxSchedulers;
import com.zxjk.duoduo.ui.base.BaseActivity;
import com.zxjk.duoduo.utils.CommonUtils;

public class FeedbackActivity extends BaseActivity {
    EditText feedbackEdit;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);
        initView();
    }

    @SuppressLint("CheckResult")
    private void initView() {
        TextView tv_title = findViewById(R.id.tv_title);
        TextView tv_commit = findViewById(R.id.tv_commit);
        feedbackEdit = findViewById(R.id.feedback_edit);

        feedbackEdit.postDelayed(() -> {
            feedbackEdit.requestFocus();
            InputMethodManager manager = ((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE));
            if (manager != null) manager.showSoftInput(feedbackEdit, 0);
        }, 200);

        tv_commit.setVisibility(View.VISIBLE);
        tv_commit.setText(getString(R.string.commit));
        tv_title.setText(getString(R.string.feedback_title));
        findViewById(R.id.rl_back).setOnClickListener(v -> finish());
        tv_commit.setOnClickListener(v -> {
            if (feedbackEdit.getText().toString().isEmpty()) {
                ToastUtils.showShort(getString(R.string.please_enter_feedback_comments));
            } else {
                ServiceFactory.getInstance().getBaseService(Api.class)
                        .feedback(feedbackEdit.getText().toString().trim())
                        .compose(bindToLifecycle())
                        .compose(RxSchedulers.normalTrans())
                        .compose(RxSchedulers.ioObserver(CommonUtils.initDialog(this)))
                        .subscribe(s -> {
                            ToastUtils.showShort(getString(R.string.transfer_commit_successful));
                            finish();
                        }, this::handleApiError);
            }
        });
    }
}

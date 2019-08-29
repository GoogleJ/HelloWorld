package com.zxjk.duoduo.ui.msgpage;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.blankj.utilcode.util.ToastUtils;
import com.zxjk.duoduo.R;
import com.zxjk.duoduo.ui.base.BaseActivity;

public class SkinReportActivity extends BaseActivity {

    private EditText feedback_edit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_skin_report);
        initView();

    }

    private void initView() {
        feedback_edit = findViewById(R.id.feedback_edit);
        TextView tv_title = findViewById(R.id.tv_title);
        TextView tv_commit = findViewById(R.id.tv_commit);
        tv_commit.setVisibility(View.VISIBLE);
        tv_commit.setText(getString(R.string.queding));
        tv_title.setText(getString(R.string.report));
        findViewById(R.id.rl_back).setOnClickListener(v -> finish());
        tv_commit.setOnClickListener(v -> {
            if (TextUtils.isEmpty(feedback_edit.getText().toString().trim())) {
                ToastUtils.showShort(R.string.input_skin);
                return;
            }
            ToastUtils.showShort(R.string.report_success);
            finish();
        });
    }


}

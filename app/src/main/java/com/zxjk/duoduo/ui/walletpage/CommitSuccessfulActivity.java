package com.zxjk.moneyspace.ui.walletpage;

import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.Nullable;
import com.zxjk.moneyspace.R;
import com.zxjk.moneyspace.ui.base.BaseActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CommitSuccessfulActivity extends BaseActivity {
    @BindView(R.id.tv_title)
    TextView tvTitle;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_commit_successful);
        ButterKnife.bind(this);
        tvTitle.setText(getString(R.string.commit_successful));
    }

    @OnClick(R.id.rl_back)
    public void onClick() {
        finish();
    }
}

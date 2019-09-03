package com.zxjk.duoduo.ui.msgpage;

import android.os.Bundle;
import android.widget.TextView;

import com.zxjk.duoduo.R;
import com.zxjk.duoduo.ui.base.BaseActivity;

public class EnterGroupGetRedActivity extends BaseActivity {

    private String groupId;

    private TextView

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_group_get_red);
    }

    private void initView() {

    }

    private void initData() {
        groupId = getIntent().getStringExtra("groupId");
    }
}

package com.zxjk.duoduo.ui.widget.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.zxjk.duoduo.R;
import com.zxjk.duoduo.utils.DataUtils;
import com.zxjk.duoduo.utils.MMKVUtils;

public class BuyCoinDialog extends Dialog {
    private TextView tvTitle;
    private TextView tvContent;
    private TextView tvDetermineBtn;
    private LinearLayout llHint;
    private CheckBox checkBox;
    private String title;
    private String content;
    private Boolean isShowHint;
    private String btntext;
    private String key;
    private Context context;
    private boolean isVisibilityHint;
    private Intent intent;

    public BuyCoinDialog(@NonNull Context context, String title, String content, String btnText, Boolean isShowHint) {
        super(context, R.style.dialogstyle);
        this.context = context;
        this.title = title;
        this.content = content;
        this.isShowHint = isShowHint;
        this.btntext = btnText;
    }


    public BuyCoinDialog setKey(String key) {
        this.key = key;
        return this;
    }

    public BuyCoinDialog start(Intent intent) {
        this.intent = intent;
        return this;
    }

    public BuyCoinDialog setVisibilityHint(boolean isVisibility) {
        this.isVisibilityHint = isVisibility;
        return this;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_buycoin_hint);
        tvTitle = findViewById(R.id.tv_title);
        tvContent = findViewById(R.id.tv_content);
        tvDetermineBtn = findViewById(R.id.tv_determine_btn);
        llHint = findViewById(R.id.ll_hint);
        checkBox = findViewById(R.id.checkobx);
        tvTitle.setText(title);
        tvContent.setText(content);
        tvDetermineBtn.setText(btntext);
        if (isShowHint) {
            llHint.setVisibility(View.VISIBLE);
        }
        if (isVisibilityHint) {
            findViewById(R.id.tv_hint).setVisibility(View.VISIBLE);
        }
        findViewById(R.id.tv_determine_btn).setOnClickListener(v -> {
            if (checkBox.isChecked()) {
                MMKVUtils.getInstance().enCode(key, DataUtils.getCurTimeLong());
            }
            if (intent != null) {
                context.startActivity(intent);
            }
            dismiss();
        });
    }

}

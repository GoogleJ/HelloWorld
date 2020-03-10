package com.zxjk.duoduo.ui.widget.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.zxjk.duoduo.R;

public class RewardDialog extends Dialog implements View.OnClickListener {
    private TextView contentTxt;
    private TextView titleTxt;
    private Button btn_reward_dialog_submit;
    private ImageView img;

    private Context mContext;
    private String content;
    private OnCloseListener listener;
    private String titleName;
    private String title;

    public RewardDialog(Context context) {
        super(context);
        this.mContext = context;
    }

    public RewardDialog(Context context, int themeResId, String content) {
        super(context, themeResId);
        this.mContext = context;
        this.content = content;
    }

    public RewardDialog(Context context, int themeResId, String content, OnCloseListener listener) {
        super(context, themeResId);
        this.mContext = context;
        this.content = content;
        this.listener = listener;
    }

    protected RewardDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        this.mContext = context;
    }

    public RewardDialog setTitle(String title) {
        this.title = title;
        return this;
    }

    public RewardDialog setTitleName(String titleName) {
        this.titleName = titleName;
        return this;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_reward);
        setCanceledOnTouchOutside(false);

        initView();

        initAnim();
    }

    private void initAnim() {
        RotateAnimation rotate = new RotateAnimation(0f, 360f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        LinearInterpolator lin = new LinearInterpolator();
        rotate.setInterpolator(lin);
        rotate.setDuration(6000);
        rotate.setRepeatCount(-1);
        rotate.setFillAfter(true);
        rotate.setStartOffset(10);
        img.setAnimation(rotate);
    }

    private void initView() {
        contentTxt = findViewById(R.id.tv_content_txt);
        titleTxt = findViewById(R.id.titleTxt);
        btn_reward_dialog_submit = findViewById(R.id.btn_reward_dialog_submit);
        btn_reward_dialog_submit.setOnClickListener(this);

        img = findViewById(R.id.img_reward_light);

        if (!TextUtils.isEmpty(title)) {
            titleTxt.setText(title);
        }

        if (!TextUtils.isEmpty(titleName)) {
            contentTxt.setText(titleName);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_reward_dialog_submit:
                listener.onClick(this, true);
                break;
        }
    }

    public interface OnCloseListener {
        void onClick(Dialog dialog, boolean confirm);
    }
}

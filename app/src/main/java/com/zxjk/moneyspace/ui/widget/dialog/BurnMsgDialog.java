package com.zxjk.moneyspace.ui.widget.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.zxjk.moneyspace.R;

import cn.qqtheme.framework.widget.WheelView;

public class BurnMsgDialog extends Dialog {
    private View view;
    private WheelView wheelView;
    protected int textSize = WheelView.TEXT_SIZE;
    private Typeface typeface = Typeface.DEFAULT;
    private WheelView.DividerConfig dividerConfig = new WheelView.DividerConfig();

    private TextView tvTitle;
    private TextView tvCancel;
    private TextView tvCommit;

    private String[] selections1 = new String[]{"关闭", "即刻焚烧", "10秒", "5分钟", "1小时", "24小时"};
    private String[] selections2 = new String[]{"关闭", "30秒", "1分钟", "5分钟", "10分钟", "1小时"};
    private String[] second2 = new String[]{"0", "30", "60", "300", "600", "3600"};

    public interface OnCommitListener {
        void commit(String str);
    }

    private OnCommitListener onCommitListener;

    public void setOnCommitListener(OnCommitListener onCommitListener) {
        this.onCommitListener = onCommitListener;
    }

    public BurnMsgDialog(@NonNull Context context) {
        super(context, R.style.dialogstyle);
        this.view = View.inflate(context, R.layout.layout_burn_middle, null);
        tvTitle = view.findViewById(R.id.tvTitle);
        wheelView = view.findViewById(R.id.wheelView);
        tvCancel = view.findViewById(R.id.tvCancel);
        tvCommit = view.findViewById(R.id.tvCommit);
        tvCancel.setOnClickListener(v -> dismiss());
        tvCommit.setOnClickListener(view -> {
            if (onCommitListener != null) {
                onCommitListener.commit(selections1[wheelView.getSelectedIndex()]);
            }
            dismiss();
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(view);
        setCancelable(false);
        setCanceledOnTouchOutside(false);
        Window window = this.getWindow();
        window.setGravity(Gravity.CENTER);
        WindowManager.LayoutParams layoutParams = window.getAttributes();
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(layoutParams);

        float lineSpaceMultiplier = WheelView.LINE_SPACE_MULTIPLIER;
        wheelView.setLineSpaceMultiplier(lineSpaceMultiplier);
        int textPadding = WheelView.TEXT_PADDING;
        wheelView.setTextPadding(textPadding);
        wheelView.setTextSize(textSize);
        wheelView.setTypeface(typeface);
        int textColorNormal = WheelView.TEXT_COLOR_NORMAL;
        int textColorFocus = WheelView.TEXT_COLOR_FOCUS;
        wheelView.setTextColor(textColorNormal, textColorFocus);
        wheelView.setDividerConfig(dividerConfig);
        int offset = WheelView.ITEM_OFF_SET;
        wheelView.setOffset(offset);
        boolean cycleDisable = true;
        wheelView.setCycleDisable(cycleDisable);
        boolean useWeight = true;
        wheelView.setUseWeight(useWeight);
        boolean textSizeAutoFit = true;
        wheelView.setTextSizeAutoFit(textSizeAutoFit);
        wheelView.setTextColor(Color.parseColor("#DDDDDD"), Color.parseColor("#000000"));
        wheelView.setDividerColor(ContextCompat.getColor(getContext(), R.color.colorD));
        wheelView.setTextSize(17);
        wheelView.setVisibleItemCount(5);
        wheelView.setLineSpaceMultiplier(4);
    }

    public void show(int selectIndex) {
        wheelView.setItems(selections1, selectIndex);
        show();
    }

    public void showSlowMode(int selectIndex) {
        tvTitle.setText(R.string.slowmode);
        tvCommit.setOnClickListener(view -> {
            if (onCommitListener != null) {
                onCommitListener.commit(second2[wheelView.getSelectedIndex()]);
            }
            dismiss();
        });
        wheelView.setItems(selections2, selectIndex);
        show();
    }
}

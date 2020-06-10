package io.rong.imkit.widget;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import io.rong.imkit.R;

public class BottomMenuDialog extends Dialog
        implements OnClickListener {
    private View.OnClickListener confirmListener;
    private View.OnClickListener middleListener;
    private View.OnClickListener cancelListener;
    private String confirmText;
    private String middleText;
    private String cancelText;

    BottomMenuDialog(Context context) {
        super(context, R.style.dialogFullscreen);
    }

    public BottomMenuDialog(Context context, int theme) {
        super(context, theme);
    }

    public BottomMenuDialog(Context context, String confirmText, String middleText) {
        super(context, R.style.dialogFullscreen);
        this.confirmText = confirmText;
        this.middleText = middleText;
    }

    public BottomMenuDialog(Context context, String confirmText, String middleText, String cancelText) {
        super(context, R.style.dialogFullscreen);
        this.confirmText = confirmText;
        this.middleText = middleText;
        this.cancelText = cancelText;
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rc_dialog_bottom);
        Window window = getWindow();
        WindowManager.LayoutParams layoutParams = window.getAttributes();
        layoutParams.flags = 2;
        layoutParams.dimAmount = 0.5F;
        window.setAttributes(layoutParams);
        window.setBackgroundDrawableResource(17170445);

        Button step = (Button) findViewById(R.id.bt_by_step);
        Button combine = (Button) findViewById(R.id.bt_combine);
        Button cancel = (Button) findViewById(R.id.bt_cancel);

        if (!(TextUtils.isEmpty(this.confirmText)))
            step.setText(this.confirmText);

        if (!(TextUtils.isEmpty(this.middleText)))
            combine.setText(this.middleText);

        if (!(TextUtils.isEmpty(this.cancelText))) {
            cancel.setText(this.cancelText);
        }

        cancel.setOnClickListener(this);
        step.setOnClickListener(this);
        combine.setOnClickListener(this);
    }

    public void onClick(View v) {
        int i = v.getId();
        View.OnClickListener listener = null;
        if (i == R.id.bt_by_step)
            listener = this.confirmListener;
        else if (i == R.id.bt_combine)
            listener = this.middleListener;
        else if (i == R.id.bt_cancel)
            listener = this.cancelListener;

        if (listener != null)
            listener.onClick(v);
    }

    public View.OnClickListener getConfirmListener() {
        return this.confirmListener;
    }

    void setConfirmListener(View.OnClickListener confirmListener) {
        this.confirmListener = confirmListener;
    }

    public View.OnClickListener getCancelListener() {
        return this.cancelListener;
    }

    void setCancelListener(View.OnClickListener cancelListener) {
        this.cancelListener = cancelListener;
    }

    public View.OnClickListener getMiddleListener() {
        return this.middleListener;
    }

    void setMiddleListener(View.OnClickListener middleListener) {
        this.middleListener = middleListener;
    }
}
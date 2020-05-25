//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package io.rong.imkit.utilities;

import android.app.AlertDialog;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.TextView;

import io.rong.imkit.R.dimen;
import io.rong.imkit.R.id;
import io.rong.imkit.R.layout;

public class PromptPopupDialog extends AlertDialog {
    private Context mContext;
    private PromptPopupDialog.OnPromptButtonClickedListener mPromptButtonClickedListener;
    private String mTitle;
    private String mPositiveButton;
    private String mMessage;
    private int mLayoutResId;

    public static PromptPopupDialog newInstance(Context context, String title, String message) {
        return new PromptPopupDialog(context, title, message);
    }

    public static PromptPopupDialog newInstance(Context context, String message) {
        return new PromptPopupDialog(context, message);
    }

    public static PromptPopupDialog newInstance(Context context, String title, String message, String positiveButton) {
        return new PromptPopupDialog(context, title, message, positiveButton);
    }

    public PromptPopupDialog(Context context, String title, String message, String positiveButton) {
        this(context, title, message);
        this.mPositiveButton = positiveButton;
    }

    public PromptPopupDialog(Context context, String title, String message) {
        super(context);
        this.mLayoutResId = layout.rc_dialog_popup_prompt;
        this.mContext = context;
        this.mTitle = title;
        this.mMessage = message;
    }

    public PromptPopupDialog(Context context, String message) {
        super(context);
        this.mContext = context;
        this.mMessage = message;
    }

    protected void onStart() {
        super.onStart();
        LayoutInflater inflater = (LayoutInflater) this.mContext.getSystemService("layout_inflater");
        View view = inflater.inflate(this.mLayoutResId, (ViewGroup) null);
        TextView txtViewTitle = (TextView) view.findViewById(id.popup_dialog_title);
        TextView txtViewMessage = (TextView) view.findViewById(id.popup_dialog_message);
        TextView txtViewOK = (TextView) view.findViewById(id.popup_dialog_button_ok);
        TextView txtViewCancel = (TextView) view.findViewById(id.popup_dialog_button_cancel);
        txtViewOK.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (PromptPopupDialog.this.mPromptButtonClickedListener != null) {
                    PromptPopupDialog.this.mPromptButtonClickedListener.onPositiveButtonClicked();
                }

                PromptPopupDialog.this.dismiss();
            }
        });
        txtViewCancel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                PromptPopupDialog.this.dismiss();
            }
        });
        if (!TextUtils.isEmpty(this.mTitle)) {
            txtViewTitle.setText(this.mTitle);
            txtViewTitle.setVisibility(0);
        }

        if (!TextUtils.isEmpty(this.mPositiveButton)) {
            txtViewOK.setText(this.mPositiveButton);
        }

        txtViewMessage.setText(this.mMessage);
        this.setContentView(view);
        LayoutParams layoutParams = this.getWindow().getAttributes();
        layoutParams.width = this.gePopupWidth();
        layoutParams.height = -2;
        this.getWindow().setAttributes(layoutParams);
    }

    public PromptPopupDialog setPromptButtonClickedListener(PromptPopupDialog.OnPromptButtonClickedListener buttonClickedListener) {
        this.mPromptButtonClickedListener = buttonClickedListener;
        return this;
    }

    public PromptPopupDialog setLayoutRes(int resId) {
        this.mLayoutResId = resId;
        return this;
    }

    private int gePopupWidth() {
        int distanceToBorder = (int) this.mContext.getResources().getDimension(dimen.rc_popup_dialog_distance_to_edge);
        return this.getScreenWidth() - 2 * distanceToBorder;
    }

    private int getScreenWidth() {
        return ((WindowManager) ((WindowManager) this.mContext.getSystemService("window"))).getDefaultDisplay().getWidth();
    }

    public interface OnPromptButtonClickedListener {
        void onPositiveButtonClicked();
    }
}
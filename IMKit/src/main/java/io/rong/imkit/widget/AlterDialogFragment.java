package io.rong.imkit.widget;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;

public class AlterDialogFragment extends BaseDialogFragment {
    private static final String ARGS_TITLE = "args_title";
    private static final String ARGS_MESSAGE = "args_message";
    private static final String ARGS_CANCEL_BTN_TXT = "args_cancel_button_text";
    private static final String ARGS_OK_BTN_TXT = "args_ok_button_text";
    private AlterDialogBtnListener mAlterDialogBtnListener;

    public static AlterDialogFragment newInstance(String title, String message, String cancelBtnText, String okBtnText) {
        AlterDialogFragment dialogFragment = new AlterDialogFragment();
        Bundle args = new Bundle();
        args.putString("args_title", title);
        args.putString("args_message", message);
        args.putString("args_cancel_button_text", cancelBtnText);
        args.putString("args_ok_button_text", okBtnText);
        dialogFragment.setArguments(args);

        return dialogFragment;
    }

    @NonNull
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        String title = getArguments().getString("args_title");
        String message = getArguments().getString("args_message");
        String cancelBtnText = getArguments().getString("args_cancel_button_text");
        String okBtnText = getArguments().getString("args_ok_button_text");

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        if (!(TextUtils.isEmpty(title))) {
            builder.setTitle(title);
        }

        if (!(TextUtils.isEmpty(message))) {
            builder.setMessage(message);
        }

        if (!(TextUtils.isEmpty(okBtnText)))
            builder.setPositiveButton(okBtnText, new OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    if (AlterDialogFragment.this.mAlterDialogBtnListener != null)
                        AlterDialogFragment.this.mAlterDialogBtnListener.onDialogPositiveClick(AlterDialogFragment.this);

                }

            });

        if (!(TextUtils.isEmpty(cancelBtnText)))
            builder.setNegativeButton(cancelBtnText, new OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    if (AlterDialogFragment.this.mAlterDialogBtnListener != null)
                        AlterDialogFragment.this.mAlterDialogBtnListener.onDialogNegativeClick(AlterDialogFragment.this);

                }

            });

        return builder.create();
    }

    public void show(FragmentManager manager) {
        show(manager, "AlterDialogFragment");
    }

    public void setOnAlterDialogBtnListener(AlterDialogBtnListener alterDialogListener) {
        this.mAlterDialogBtnListener = alterDialogListener;
    }

    public static abstract interface AlterDialogBtnListener {
        public abstract void onDialogPositiveClick(AlterDialogFragment paramAlterDialogFragment);

        public abstract void onDialogNegativeClick(AlterDialogFragment paramAlterDialogFragment);
    }
}
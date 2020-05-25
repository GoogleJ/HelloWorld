package io.rong.imkit.widget;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;

import io.rong.common.RLog;

public class LoadingDialogFragment extends BaseDialogFragment {
    private static final String TAG = "LoadingDialogFragment";
    private static final String ARGS_TITLE = "args_title";
    private static final String ARGS_MESSAGE = "args_message";

    public static LoadingDialogFragment newInstance(String title, String message) {
        LoadingDialogFragment dialogFragment = new LoadingDialogFragment();
        Bundle args = new Bundle();
        args.putString("args_title", title);
        args.putString("args_message", message);
        dialogFragment.setArguments(args);

        return dialogFragment;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @NonNull
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        ProgressDialog dialog = new ProgressDialog(getActivity());
        String title = getArguments().getString("args_title");
        String message = getArguments().getString("args_message");

        dialog.setIndeterminate(true);
        dialog.setProgressStyle(0);

        if (!(TextUtils.isEmpty(title)))
            dialog.setTitle(title);

        if (!(TextUtils.isEmpty(message)))
            dialog.setMessage(message);

        return dialog;
    }

    public void show(FragmentManager manager) {
        try {
            manager.beginTransaction().remove(this).commit();
            super.show(manager, "LoadingDialogFragment");
        } catch (Exception e) {
            RLog.e("LoadingDialogFragment", "show", e);
        }
    }
}
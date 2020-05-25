package io.rong.imkit.widget;

import android.view.View;

import androidx.fragment.app.DialogFragment;

public class BaseDialogFragment extends DialogFragment {
    protected <T extends View> T getView(View view, int id) {
        return view.findViewById(id);
    }
}
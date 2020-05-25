package io.rong.imkit.widget;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import java.util.List;

import io.rong.imkit.utilities.OptionsPopupDialog;
import io.rong.imkit.utilities.OptionsPopupDialog.OnOptionsItemClickedListener;

@Deprecated
public class ArraysDialogFragment extends BaseDialogFragment {
    private static final String ARGS_ARRAYS = "args_arrays";
    private OnArraysDialogItemListener mItemListener;
    private int count;

    public static ArraysDialogFragment newInstance(String title, String[] arrays) {
        ArraysDialogFragment dialogFragment = new ArraysDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putStringArray("args_arrays", arrays);
        dialogFragment.setArguments(bundle);
        return dialogFragment;
    }

    public int getCount() {
        return this.count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public ArraysDialogFragment setArraysDialogItemListener(OnArraysDialogItemListener mItemListener) {
        this.mItemListener = mItemListener;
        return this;
    }

    public void show(FragmentManager manager) {
        String[] arrays = getArguments().getStringArray("args_arrays");
        setCount(arrays.length);
        List fragmentList = manager.getFragments();
        if (fragmentList != null) {
            Fragment fragment = (Fragment) fragmentList.get(0);
            if (fragment != null) {
                Context context = fragment.getActivity();
                if (context != null)
                    OptionsPopupDialog.newInstance(context, arrays).setOptionsPopupDialogListener(new OnOptionsItemClickedListener() {
                        public void onOptionsItemClicked(int which) {
                            ArraysDialogFragment.this.mItemListener.OnArraysDialogItemClick(null, which);
                        }
                    }).show();
            }
        }
    }

    public static abstract interface OnArraysDialogItemListener {
        public abstract void OnArraysDialogItemClick(DialogInterface paramDialogInterface, int paramInt);
    }
}
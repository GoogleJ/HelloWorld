//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package io.rong.imkit.widget.provider;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import io.rong.imkit.R.drawable;
import io.rong.imkit.R.string;
import io.rong.imkit.RongExtension;
import io.rong.imkit.plugin.IPluginModule;
import io.rong.imkit.widget.CSEvaluateDialog;
import io.rong.imkit.widget.CSEvaluateDialog.EvaluateClickListener;

public class EvaluatePlugin implements IPluginModule, EvaluateClickListener {
    private CSEvaluateDialog mEvaluateDialog;
    private boolean mResolvedButton;

    public EvaluatePlugin(boolean mResolvedButton) {
        this.mResolvedButton = mResolvedButton;
    }

    public Drawable obtainDrawable(Context context) {
        return ContextCompat.getDrawable(context, drawable.rc_cs_evaluate_selector);
    }

    public String obtainTitle(Context context) {
        return context.getString(string.rc_cs_evaluate);
    }

    public void onClick(Fragment currentFragment, RongExtension extension) {
        this.mEvaluateDialog = new CSEvaluateDialog(currentFragment.getActivity(), extension.getTargetId());
        this.mEvaluateDialog.showStarMessage(this.mResolvedButton);
        this.mEvaluateDialog.setClickListener(this);
        extension.collapseExtension();
    }

    public void onEvaluateSubmit() {
        this.mEvaluateDialog.destroy();
        this.mEvaluateDialog = null;
    }

    public void onEvaluateCanceled() {
        this.mEvaluateDialog.destroy();
        this.mEvaluateDialog = null;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
    }
}
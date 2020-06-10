//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package io.rong.imkit.actions;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;

import androidx.fragment.app.Fragment;

import java.util.List;

import io.rong.imkit.R.dimen;
import io.rong.imkit.R.layout;

public class MoreClickAdapter implements IMoreClickAdapter {
    private MoreActionLayout moreActionLayout;

    public MoreClickAdapter() {
    }

    public void bindView(ViewGroup viewGroup, Fragment fragment, List<IClickActions> actions) {
        if (this.moreActionLayout == null) {
            Context context = viewGroup.getContext();
            this.moreActionLayout = (MoreActionLayout) LayoutInflater.from(context).inflate(layout.rc_ext_actions_container, (ViewGroup) null);
            this.moreActionLayout.setFragment(fragment);
            this.moreActionLayout.addActions(actions);
            int height = context.getResources().getDimensionPixelOffset(dimen.rc_ext_more_layout_height);
            LayoutParams params = new LayoutParams(-1, height);
            this.moreActionLayout.setLayoutParams(params);
            viewGroup.addView(this.moreActionLayout);
        }

        this.moreActionLayout.setVisibility(0);
    }

    public void hideMoreActionLayout() {
        if (this.moreActionLayout != null) {
            this.moreActionLayout.setVisibility(8);
        }

    }

    public void setMoreActionEnable(boolean enable) {
        if (this.moreActionLayout != null) {
            this.moreActionLayout.refreshView(enable);
        }

    }

    public boolean isMoreActionShown() {
        return this.moreActionLayout != null && this.moreActionLayout.getVisibility() == 0;
    }
}

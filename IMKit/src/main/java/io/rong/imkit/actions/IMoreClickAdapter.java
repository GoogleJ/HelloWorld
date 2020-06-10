//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package io.rong.imkit.actions;

import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import java.util.List;

public interface IMoreClickAdapter {
    void bindView(ViewGroup var1, Fragment var2, List<IClickActions> var3);

    void hideMoreActionLayout();

    void setMoreActionEnable(boolean var1);

    boolean isMoreActionShown();
}

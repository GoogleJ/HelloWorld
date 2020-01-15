package com.zxjk.duoduo.ui.msgpage.rongIM;

import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;

import com.zxjk.duoduo.utils.IExtensionClickAdapter;

import java.util.ArrayList;
import java.util.List;

import io.rong.imkit.DeleteClickActions;
import io.rong.imkit.actions.IClickActions;
import io.rong.imkit.fragment.ConversationFragment;
import io.rong.imkit.plugin.IPluginModule;

public class CusConversationFragment extends ConversationFragment {
    private IExtensionClickAdapter clickAdapter;

    public void setClickAdapter(IExtensionClickAdapter clickAdapter) {
        this.clickAdapter = clickAdapter;
    }

    @Override
    public boolean showMoreClickItem() {
        return true;
    }

    @Override
    public List<IClickActions> getMoreClickActions() {
        ArrayList<IClickActions> actions = new ArrayList(2);
        actions.add(new DeleteClickActions());
        actions.add(new ForwardAction());
        return actions;
    }

    @Override
    public void onSendToggleClick(View v, String text) {
        super.onSendToggleClick(v, text);
        if (clickAdapter != null) clickAdapter.onSendToggleClick(v, text);
    }

    @Override
    public void onPluginClicked(IPluginModule pluginModule, int position) {
        super.onPluginClicked(pluginModule, position);
        if (clickAdapter != null) clickAdapter.onPluginClicked(pluginModule, position);
    }

    @Override
    public void onEmoticonToggleClick(View v, ViewGroup extensionBoard) {
        super.onEmoticonToggleClick(v, extensionBoard);
        if (clickAdapter != null) clickAdapter.onEmoticonToggleClick(v, extensionBoard);
    }

    @Override
    protected void initFragment(Uri uri) {
        super.initFragment(uri);
        if (clickAdapter != null) clickAdapter.fragmentInitDone();
    }
}

package com.zxjk.duoduo.ui.msgpage.rongIM;

import java.util.ArrayList;
import java.util.List;

import io.rong.imkit.DeleteClickActions;
import io.rong.imkit.actions.IClickActions;
import io.rong.imkit.fragment.ConversationFragment;

public class CusConversationFragment extends ConversationFragment {
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
}

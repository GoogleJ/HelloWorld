package io.rong.imkit;

import android.content.Context;
import android.graphics.drawable.Drawable;

import androidx.fragment.app.Fragment;

import java.util.List;

import io.rong.imkit.actions.IClickActions;
import io.rong.imkit.fragment.ConversationFragment;
import io.rong.imlib.model.Message;

public class DeleteClickActions
        implements IClickActions {
    public Drawable obtainDrawable(Context context) {
        return context.getResources().getDrawable(R.drawable.rc_select_multi_delete);
    }

    public void onClick(Fragment curFragment) {
        ConversationFragment fragment = (ConversationFragment) curFragment;
        List messages = fragment.getCheckedMessages();
        if ((messages != null) && (messages.size() > 0)) {
            int[] messageIds = new int[messages.size()];
            for (int i = 0; i < messages.size(); ++i)
                messageIds[i] = ((Message) messages.get(i)).getMessageId();

            RongIM.getInstance().deleteMessages(messageIds, null);
            ((ConversationFragment) curFragment).resetMoreActionState();
        }
    }
}
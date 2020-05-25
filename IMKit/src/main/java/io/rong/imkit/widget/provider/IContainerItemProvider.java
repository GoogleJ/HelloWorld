//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package io.rong.imkit.widget.provider;

import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.net.Uri;
import android.os.Parcelable;
import android.text.Spannable;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import io.rong.imkit.R.string;
import io.rong.imkit.RongMessageItemLongClickActionManager;
import io.rong.imkit.model.UIMessage;
import io.rong.imkit.userInfoCache.RongUserInfoManager;
import io.rong.imkit.utilities.OptionsPopupDialog;
import io.rong.imkit.utilities.OptionsPopupDialog.OnOptionsItemClickedListener;
import io.rong.imlib.model.Message;
import io.rong.imlib.model.MessageContent;
import io.rong.imlib.model.UserInfo;

public interface IContainerItemProvider<T> {
    View newView(Context var1, ViewGroup var2);

    void bindView(View var1, int var2, T var3);

    public interface ConversationProvider<T extends Parcelable> extends IContainerItemProvider<T> {
        String getTitle(String var1);

        Uri getPortraitUri(String var1);
    }

    public abstract static class MessageProvider<K extends MessageContent> implements IContainerItemProvider<UIMessage>, Cloneable {
        public MessageProvider() {
        }

        public final void bindView(View v, int position, UIMessage data) {
            this.bindView(v, position, (K) data.getContent(), data);
        }

        public abstract void bindView(View var1, int var2, K var3, UIMessage var4);

        public Spannable getContentSummary(Context context, K data) {
            return this.getContentSummary(data);
        }

        public Spannable getSummary(UIMessage data) {
            return this.getContentSummary((K) data.getContent());
        }

        /** @deprecated */
        public abstract Spannable getContentSummary(K var1);

        public abstract void onItemClick(View var1, int var2, K var3, UIMessage var4);

        public void onItemLongClick(final View view, final int position, K content, final UIMessage message) {
            final List<MessageItemLongClickAction> messageItemLongClickActions = RongMessageItemLongClickActionManager.getInstance().getMessageItemLongClickActions(message);
            Collections.sort(messageItemLongClickActions, new Comparator<MessageItemLongClickAction>() {
                public int compare(MessageItemLongClickAction lhs, MessageItemLongClickAction rhs) {
                    return rhs.priority - lhs.priority;
                }
            });
            List<String> titles = new ArrayList();
            Iterator var7 = messageItemLongClickActions.iterator();

            while(var7.hasNext()) {
                MessageItemLongClickAction action = (MessageItemLongClickAction)var7.next();
                titles.add(action.getTitle(view.getContext()));
            }

            OptionsPopupDialog dialog = OptionsPopupDialog.newInstance(view.getContext(), (String[])titles.toArray(new String[titles.size()])).setOptionsPopupDialogListener(new OnOptionsItemClickedListener() {
                public void onOptionsItemClicked(int which) {
                    if (!((MessageItemLongClickAction)messageItemLongClickActions.get(which)).listener.onMessageItemLongClick(view.getContext(), message)) {
                        MessageProvider.this.onItemLongClickAction(view, position, message);
                    }

                }
            });
            RongMessageItemLongClickActionManager.getInstance().setLongClickDialog(dialog);
            RongMessageItemLongClickActionManager.getInstance().setLongClickMessage(message.getMessage());
            dialog.setOnDismissListener(new OnDismissListener() {
                public void onDismiss(DialogInterface dialog) {
                    RongMessageItemLongClickActionManager.getInstance().setLongClickDialog((OptionsPopupDialog)null);
                    RongMessageItemLongClickActionManager.getInstance().setLongClickMessage((Message)null);
                }
            });
            dialog.show();
        }

        public void onItemLongClickAction(View view, int position, UIMessage message) {
        }

        public Object clone() throws CloneNotSupportedException {
            return super.clone();
        }

        public String getPushContent(Context context, UIMessage message) {
            String userName = "";
            UserInfo userInfo = RongUserInfoManager.getInstance().getUserInfo(message.getSenderUserId());
            if (userInfo != null) {
                userName = userInfo.getName();
            }

            return context.getString(string.rc_user_recalled_message, new Object[]{userName});
        }
    }
}

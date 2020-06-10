//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package io.rong.imkit.widget;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.View.OnClickListener;

import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import io.rong.imkit.R.drawable;
import io.rong.imkit.actions.IClickActions;
import io.rong.imkit.fragment.ConversationFragment;
import io.rong.imkit.utils.ForwardManager;
import io.rong.imlib.model.Message;

public class ForwardClickActions implements IClickActions {
    private BottomMenuDialog dialog;

    public ForwardClickActions() {
    }

    public Drawable obtainDrawable(Context context) {
        return context.getResources().getDrawable(drawable.rc_selector_multi_forward);
    }

    public void onClick(Fragment fragment) {
        List<Message> messages = ForwardManager.filterMessagesList(fragment.getContext(), ((ConversationFragment) fragment).getCheckedMessages());
        if (messages.size() != 0) {
            ArrayList<Integer> messageIds = new ArrayList();
            Iterator var4 = messages.iterator();

            while (var4.hasNext()) {
                Message msg = (Message) var4.next();
                messageIds.add(msg.getMessageId());
            }

            this.showOptions(fragment, messageIds);
        }
    }

    private void showOptions(final Fragment fragment, final ArrayList<Integer> messageIds) {
        Activity activity = fragment.getActivity();
        if (activity != null && !activity.isFinishing()) {
            if (this.dialog != null && this.dialog.isShowing()) {
                this.dialog.dismiss();
            }

            this.dialog = new BottomMenuDialog(activity);
            this.dialog.setConfirmListener(new OnClickListener() {
                @TargetApi(23)
                public void onClick(View arg0) {
                    ForwardClickActions.this.startSelectConversationActivity(fragment, 0, messageIds);
                    if (ForwardClickActions.this.dialog != null) {
                        ForwardClickActions.this.dialog.dismiss();
                    }

                }
            });
            this.dialog.setMiddleListener(new OnClickListener() {
                public void onClick(View arg0) {
                    ForwardClickActions.this.startSelectConversationActivity(fragment, 1, messageIds);
                    if (ForwardClickActions.this.dialog != null) {
                        ForwardClickActions.this.dialog.dismiss();
                    }

                }
            });
            this.dialog.setCancelListener(new OnClickListener() {
                public void onClick(View v) {
                    if (ForwardClickActions.this.dialog != null) {
                        ForwardClickActions.this.dialog.dismiss();
                    }

                }
            });
            this.dialog.show();
        }
    }

    private void startSelectConversationActivity(Fragment pFragment, int index, ArrayList<Integer> messageIds) {
        ConversationFragment fragment = (ConversationFragment) pFragment;
        Intent intent = fragment.getSelectIntentForForward();
        intent.putExtra("index", index);
        intent.putIntegerArrayListExtra("messageIds", messageIds);
        fragment.startActivityForResult(intent, 104);
    }
}
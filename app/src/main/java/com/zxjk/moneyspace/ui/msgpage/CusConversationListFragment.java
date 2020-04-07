package com.zxjk.moneyspace.ui.msgpage;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.zxjk.moneyspace.R;
import com.zxjk.moneyspace.ui.msgpage.rongIM.CusConversationListAdapter;

import io.rong.imkit.RongIM;
import io.rong.imkit.fragment.ConversationListFragment;
import io.rong.imkit.model.UIConversation;
import io.rong.imkit.widget.adapter.ConversationListAdapter;
import razerdp.basepopup.QuickPopupBuilder;
import razerdp.basepopup.QuickPopupConfig;
import razerdp.widget.QuickPopup;

public class CusConversationListFragment extends ConversationListFragment {

    @Override
    public ConversationListAdapter onResolveAdapter(Context context) {
        return new CusConversationListAdapter(context);
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        handleLongClick((UIConversation) parent.getItemAtPosition(position));
        return true;
    }

    @Override
    public boolean onPortraitItemLongClick(View v, UIConversation data) {
        handleLongClick(data);
        return true;
    }

    private void handleLongClick(UIConversation c) {
        QuickPopup longClickPop = QuickPopupBuilder.with(((Fragment) this).getContext())
                .contentView(R.layout.pop_conversationlist)
                .config(new QuickPopupConfig()
                        .withClick(R.id.tv1, v -> RongIM.getInstance().setConversationToTop(c.getConversationType(),
                                c.getConversationTargetId(), !c.isTop(), null), true)
                        .withClick(R.id.tv2, v -> {
                            RongIM.getInstance().removeConversation(c.getConversationType(), c.getConversationTargetId(), null);
                            RongIM.getInstance().clearMessages(c.getConversationType(), c.getConversationTargetId(), null);
                        }, true))
                .show();

        String cancelTop = ((Fragment) this).getContext().getString(R.string.cancelTop);
        String setTop = ((Fragment) this).getContext().getString(R.string.setTop);
        TextView tvSetTop = longClickPop.findViewById(R.id.tv1);
        tvSetTop.setText(c.isTop() ? cancelTop : setTop);
    }

    @SuppressLint("CheckResult")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = super.onCreateView(inflater, container, savedInstanceState);

        return rootView;
    }
}

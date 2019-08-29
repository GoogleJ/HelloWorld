package com.zxjk.duoduo.ui.msgpage.rongIM;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Parcelable;
import android.text.TextUtils;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.blankj.utilcode.util.ToastUtils;
import com.zxjk.duoduo.R;
import com.zxjk.duoduo.ui.HomeActivity;
import com.zxjk.duoduo.ui.msgpage.ShareGroupQRActivity;
import com.zxjk.duoduo.ui.msgpage.rongIM.message.DuoDuoMessage;
import com.zxjk.duoduo.ui.msgpage.rongIM.message.GameResultMessage;
import com.zxjk.duoduo.ui.msgpage.rongIM.message.GroupCardMessage;
import com.zxjk.duoduo.ui.msgpage.rongIM.message.RedPacketMessage;
import com.zxjk.duoduo.ui.msgpage.rongIM.message.SystemMessage;
import com.zxjk.duoduo.ui.msgpage.rongIM.message.TransferMessage;

import java.util.ArrayList;
import java.util.List;

import io.rong.imkit.actions.IClickActions;
import io.rong.imkit.fragment.ConversationFragment;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.Message;
import io.rong.message.TextMessage;
import io.rong.message.VoiceMessage;

public class ForwardAction implements IClickActions {

    public Drawable obtainDrawable(Context context) {
        return ContextCompat.getDrawable(context, R.drawable.selector_multi_forward);
    }

    public void onClick(Fragment curFragment) {
        ConversationFragment fragment = (ConversationFragment) curFragment;
        List<Message> messages = fragment.getCheckedMessages();
        for (Message msg : messages) {
            boolean cantForward = msg.getContent() instanceof RedPacketMessage ||
                    msg.getContent() instanceof TransferMessage ||
                    msg.getContent() instanceof SystemMessage ||
                    msg.getContent() instanceof GameResultMessage ||
                    msg.getContent() instanceof DuoDuoMessage ||
                    msg.getContent() instanceof VoiceMessage ||
                    msg.getContent() instanceof GroupCardMessage ||
                    (msg.getContent() instanceof TextMessage && !TextUtils.isEmpty(((TextMessage) msg.getContent()).getExtra()) && ((TextMessage) msg.getContent()).getExtra().equals("start"));
            if (cantForward) {
                ToastUtils.showShort(R.string.cant_forward);
                return;
            }
        }
        if (messages.size() > 0) {
            RongIMClient.getInstance().getConversationList(new RongIMClient.ResultCallback<List<Conversation>>() {
                @Override
                public void onSuccess(List<Conversation> conversations) {
                    Intent intent = new Intent(curFragment.getContext(), ShareGroupQRActivity.class);
                    intent.putParcelableArrayListExtra("data", (ArrayList<Conversation>) conversations);
                    intent.putExtra("action", "transfer");
                    intent.putParcelableArrayListExtra("messagelist", (ArrayList<? extends Parcelable>) messages);
                    curFragment.startActivity(intent);
                    ((ConversationFragment) curFragment).resetMoreActionState();
                }

                @Override
                public void onError(RongIMClient.ErrorCode errorCode) {

                }
            });
        }
    }
}

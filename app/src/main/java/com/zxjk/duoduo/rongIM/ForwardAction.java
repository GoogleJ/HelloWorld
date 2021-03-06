package com.zxjk.duoduo.rongIM;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Parcelable;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.blankj.utilcode.util.GsonUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.zxjk.duoduo.R;
import com.zxjk.duoduo.bean.ConversationInfo;
import com.zxjk.duoduo.ui.msgpage.ShareGroupQRActivity;

import java.util.ArrayList;
import java.util.List;

import io.rong.imkit.actions.IClickActions;
import io.rong.imkit.fragment.ConversationFragment;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.Message;
import io.rong.message.HQVoiceMessage;
import io.rong.message.ImageMessage;
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
            String objectName = msg.getObjectName();

            boolean cantForward = msg.getSentStatus() != Message.SentStatus.SENT || (!objectName.equals("RC:TxtMsg") &&
                    !objectName.equals("RC:ImgMsg") && !objectName.equals("RC:FileMsg") &&
                    !objectName.equals("RC:VcMsg") && !objectName.equals("RC:HQVCMsg"));

            String extra = "";
            if (msg.getContent() instanceof TextMessage) {
                TextMessage textMessage = (TextMessage) msg.getContent();
                extra = textMessage.getExtra();
            } else if (msg.getContent() instanceof ImageMessage) {
                ImageMessage imageMessage = (ImageMessage) msg.getContent();
                extra = imageMessage.getExtra();
            } else if (msg.getContent() instanceof VoiceMessage) {
                VoiceMessage voiceMessage = (VoiceMessage) msg.getContent();
                extra = voiceMessage.getExtra();
            } else if (msg.getContent() instanceof HQVoiceMessage) {
                HQVoiceMessage hqVoiceMessage = (HQVoiceMessage) msg.getContent();
                extra = hqVoiceMessage.getExtra();
            }
            try {
                ConversationInfo info = GsonUtils.fromJson(extra, ConversationInfo.class);
                if (info.getMessageBurnTime() != -1) {
                    cantForward = true;
                }
            } catch (Exception e) {
            }

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

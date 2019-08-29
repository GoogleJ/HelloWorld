package com.zxjk.duoduo.ui.msgpage.rongIM.provider;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.AnimationDrawable;
import android.media.AudioManager;
import android.net.Uri;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.blankj.utilcode.util.GsonUtils;
import com.zxjk.duoduo.R;
import com.zxjk.duoduo.bean.ConversationInfo;

import io.rong.imkit.R.bool;
import io.rong.imkit.R.drawable;
import io.rong.imkit.R.id;
import io.rong.imkit.R.layout;
import io.rong.imkit.R.string;
import io.rong.eventbus.EventBus;
import io.rong.imkit.manager.AudioPlayManager;
import io.rong.imkit.manager.AudioRecordManager;
import io.rong.imkit.manager.IAudioPlayListener;
import io.rong.imkit.model.Event;
import io.rong.imkit.model.ProviderTag;
import io.rong.imkit.model.UIMessage;
import io.rong.imkit.widget.provider.IContainerItemProvider;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.destruct.MessageBufferPool;
import io.rong.imlib.model.Message;
import io.rong.message.DestructionCmdMessage;
import io.rong.message.VoiceMessage;

@ProviderTag(
        messageContent = VoiceMessage.class,
        showReadState = true
)
public class BurnVoiceMessageProvider extends IContainerItemProvider.MessageProvider<VoiceMessage> {

    public BurnVoiceMessageProvider() {
    }

    public View newView(Context context, ViewGroup group) {
        View view = LayoutInflater.from(context).inflate(layout.rc_item_voice_message, null);
        BurnVoiceMessageProvider.ViewHolder holder = new BurnVoiceMessageProvider.ViewHolder();
        holder.left = view.findViewById(id.rc_left);
        holder.right = view.findViewById(id.rc_right);
        holder.img = view.findViewById(id.rc_img);
        holder.unread = view.findViewById(id.rc_voice_unread);
        holder.rc_layout = view.findViewById(id.rc_layout);
        holder.ivFireLeft = view.findViewById(R.id.ivFireLeft);
        holder.ivFireRight = view.findViewById(R.id.ivFireRight);
        view.setTag(holder);
        return view;
    }

    public void bindView(View v, int position, VoiceMessage content, UIMessage message) {
        BurnVoiceMessageProvider.ViewHolder holder = (BurnVoiceMessageProvider.ViewHolder) v.getTag();
        Uri playingUri;
        boolean listened;
        if (message.continuePlayAudio) {
            playingUri = AudioPlayManager.getInstance().getPlayingUri();
            if (playingUri == null || !playingUri.equals(content.getUri())) {
                listened = message.getMessage().getReceivedStatus().isListened();
                this.sendDestructReceiptMessage(message);
                AudioPlayManager.getInstance().startPlay(v.getContext(), content.getUri(), new BurnVoiceMessageProvider.VoiceMessagePlayListener(v.getContext(), message, holder, listened));
            }
        } else {
            playingUri = AudioPlayManager.getInstance().getPlayingUri();
            if (playingUri != null && playingUri.equals(content.getUri())) {
                this.setLayout(v.getContext(), holder, message, true);
                listened = message.getMessage().getReceivedStatus().isListened();
                AudioPlayManager.getInstance().setPlayListener(new BurnVoiceMessageProvider.VoiceMessagePlayListener(v.getContext(), message, holder, listened));
            } else {
                this.setLayout(v.getContext(), holder, message, false);
            }
        }
    }

    public void onItemClick(View view, int position, VoiceMessage content, UIMessage message) {
        if (content != null) {
            this.sendDestructReceiptMessage(message);
            BurnVoiceMessageProvider.ViewHolder holder = (BurnVoiceMessageProvider.ViewHolder) view.getTag();
            if (AudioPlayManager.getInstance().isPlaying()) {
                if (AudioPlayManager.getInstance().getPlayingUri().equals(content.getUri())) {
                    AudioPlayManager.getInstance().stopPlay();
                    return;
                }

                AudioPlayManager.getInstance().stopPlay();
            }

            if (!AudioPlayManager.getInstance().isInNormalMode(view.getContext()) && AudioPlayManager.getInstance().isInVOIPMode(view.getContext())) {
                Toast.makeText(view.getContext(), view.getContext().getString(string.rc_voip_occupying), Toast.LENGTH_SHORT).show();
            } else {
                holder.unread.setVisibility(View.GONE);
                boolean listened = message.getMessage().getReceivedStatus().isListened();
                AudioPlayManager.getInstance().startPlay(view.getContext(), content.getUri(), new BurnVoiceMessageProvider.VoiceMessagePlayListener(view.getContext(), message, holder, listened));
            }
        }
    }

    private void sendDestructReceiptMessage(UIMessage message) {
        if (message.getContent().isDestruct() && message.getMessageDirection() == Message.MessageDirection.RECEIVE && message.getMessage().getReadTime() <= 0L && !TextUtils.isEmpty(message.getUId())) {
            long currentTimeMillis = System.currentTimeMillis();
            RongIMClient.getInstance().setMessageReadTime((long) message.getMessageId(), currentTimeMillis, null);
            message.getMessage().setReadTime(currentTimeMillis);
            DestructionCmdMessage destructionCmdMessage = new DestructionCmdMessage();
            destructionCmdMessage.addBurnMessageUId(message.getUId());
            MessageBufferPool.getInstance().putMessageInBuffer(Message.obtain(message.getTargetId(), message.getConversationType(), destructionCmdMessage));
            EventBus.getDefault().post(message.getMessage());
        }
    }

    private void setLayout(Context context, BurnVoiceMessageProvider.ViewHolder holder, UIMessage message, boolean playing) {
        VoiceMessage content = (VoiceMessage) message.getContent();

        ConversationInfo conversationInfo = null;
        if (!TextUtils.isEmpty(content.getExtra())) {
            conversationInfo = GsonUtils.fromJson(content.getExtra(), ConversationInfo.class);
        }
        float scale = context.getResources().getDisplayMetrics().density;
        int minWidth = (int) ((float) 70 * scale + 0.5F);
        int maxWidth = (int) ((float) 204 * scale + 0.5F);
        int duration = AudioRecordManager.getInstance().getMaxVoiceDuration();
        holder.img.getLayoutParams().width = minWidth + (maxWidth - minWidth) / duration * content.getDuration();
        AnimationDrawable animationDrawable;
        if (message.getMessageDirection() == Message.MessageDirection.SEND) {
            if (conversationInfo != null && conversationInfo.getMessageBurnTime() != -1) {
                holder.ivFireRight.setVisibility(View.VISIBLE);
                holder.ivFireLeft.setVisibility(View.INVISIBLE);
            } else {
                holder.ivFireRight.setVisibility(View.INVISIBLE);
                holder.ivFireLeft.setVisibility(View.INVISIBLE);
            }
            holder.left.setText(String.format("%s\"", content.getDuration()));
            holder.left.setVisibility(View.VISIBLE);
            holder.right.setVisibility(View.GONE);
            holder.unread.setVisibility(View.GONE);
            holder.img.setScaleType(ImageView.ScaleType.FIT_END);
            holder.rc_layout.setBackgroundResource(drawable.shape_rc_bubble_right);
            animationDrawable = (AnimationDrawable) context.getResources().getDrawable(drawable.rc_an_voice_sent);
            if (playing) {
                holder.img.setImageDrawable(animationDrawable);
                if (animationDrawable != null) {
                    animationDrawable.start();
                }
            } else {
                holder.img.setImageDrawable(holder.img.getResources().getDrawable(drawable.rc_ic_voice_sent));
                if (animationDrawable != null) {
                    animationDrawable.stop();
                }
            }
        } else {
            if (conversationInfo != null && conversationInfo.getMessageBurnTime() != -1) {
                holder.ivFireRight.setVisibility(View.INVISIBLE);
                holder.ivFireLeft.setVisibility(View.VISIBLE);
            } else {
                holder.ivFireRight.setVisibility(View.INVISIBLE);
                holder.ivFireLeft.setVisibility(View.INVISIBLE);
            }
            holder.right.setText(String.format("%s\"", content.getDuration()));
            holder.right.setVisibility(View.VISIBLE);
            holder.left.setVisibility(View.GONE);
            if (!message.getReceivedStatus().isListened()) {
                holder.unread.setVisibility(View.VISIBLE);
            } else {
                holder.unread.setVisibility(View.GONE);
            }

            holder.rc_layout.setBackgroundResource(drawable.shape_rc_bubble_left);
            animationDrawable = (AnimationDrawable) context.getResources().getDrawable(drawable.rc_an_voice_receive);
            if (playing) {
                holder.img.setImageDrawable(animationDrawable);
                if (animationDrawable != null) {
                    animationDrawable.start();
                }
            } else {
                holder.img.setImageDrawable(holder.img.getResources().getDrawable(drawable.rc_ic_voice_receive));
                if (animationDrawable != null) {
                    animationDrawable.stop();
                }
            }

            holder.img.setScaleType(ImageView.ScaleType.FIT_START);
        }

    }

    public Spannable getContentSummary(VoiceMessage data) {
        return null;
    }

    public Spannable getContentSummary(Context context, VoiceMessage data) {
        return new SpannableString(context.getString(string.rc_message_content_voice));
    }

    @TargetApi(8)
    private boolean muteAudioFocus(Context context, boolean bMute) {
        if (context == null) {
            return false;
        } else {
            boolean bool;
            AudioManager am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
            int result;
            if (bMute) {
                result = am.requestAudioFocus(null, 3, 2);
                bool = result == 1;
            } else {
                result = am.abandonAudioFocus(null);
                bool = result == 1;
            }
            return bool;
        }
    }

    private class VoiceMessagePlayListener implements IAudioPlayListener {
        private Context context;
        private UIMessage message;
        private BurnVoiceMessageProvider.ViewHolder holder;
        private boolean listened;

        public VoiceMessagePlayListener(Context context, UIMessage message, BurnVoiceMessageProvider.ViewHolder holder, boolean listened) {
            this.context = context;
            this.message = message;
            this.holder = holder;
            this.listened = listened;
        }

        public void onStart(Uri uri) {
            this.message.continuePlayAudio = false;
            this.message.setListening(true);
            this.message.getReceivedStatus().setListened();
            RongIMClient.getInstance().setMessageReceivedStatus(this.message.getMessageId(), this.message.getReceivedStatus(), null);
            BurnVoiceMessageProvider.this.setLayout(this.context, this.holder, this.message, true);
            EventBus.getDefault().post(new Event.AudioListenedEvent(this.message.getMessage()));
        }

        public void onStop(Uri uri) {
            if (this.message.getContent() instanceof VoiceMessage) {
                this.message.setListening(false);
                BurnVoiceMessageProvider.this.setLayout(this.context, this.holder, this.message, false);
            }

        }

        public void onComplete(Uri uri) {
            Event.PlayAudioEvent event = Event.PlayAudioEvent.obtain();
            event.messageId = this.message.getMessageId();
            if (this.message.isListening() && this.message.getMessageDirection().equals(Message.MessageDirection.RECEIVE)) {
                try {
                    event.continuously = this.context.getResources().getBoolean(bool.rc_play_audio_continuous);
                } catch (Resources.NotFoundException var4) {
                    var4.printStackTrace();
                }
            }

            if (event.continuously) {
                EventBus.getDefault().post(event);
            }

            this.message.setListening(false);
            BurnVoiceMessageProvider.this.setLayout(this.context, this.holder, this.message, false);
        }
    }

    private static class ViewHolder {
        ImageView img;
        TextView left;
        TextView right;
        ImageView unread;
        FrameLayout rc_layout;
        ImageView ivFireLeft;
        ImageView ivFireRight;

        private ViewHolder() {
        }
    }
}

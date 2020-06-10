package com.zxjk.duoduo.rongIM.provider;

import android.content.Context;
import android.content.res.Resources.NotFoundException;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.blankj.utilcode.util.GsonUtils;
import com.zxjk.duoduo.bean.ConversationInfo;

import java.io.File;
import java.lang.ref.WeakReference;

import io.rong.common.RLog;
import io.rong.eventbus.EventBus;
import io.rong.imkit.R.bool;
import io.rong.imkit.R.drawable;
import io.rong.imkit.R.id;
import io.rong.imkit.R.layout;
import io.rong.imkit.R.string;
import io.rong.imkit.RongIM;
import io.rong.imkit.destruct.DestructManager;
import io.rong.imkit.manager.AudioPlayManager;
import io.rong.imkit.manager.AudioRecordManager;
import io.rong.imkit.manager.IAudioPlayListener;
import io.rong.imkit.model.Event.AudioListenedEvent;
import io.rong.imkit.model.Event.PlayAudioEvent;
import io.rong.imkit.model.Event.changeDestructionReadTimeEvent;
import io.rong.imkit.model.ProviderTag;
import io.rong.imkit.model.UIMessage;
import io.rong.imkit.widget.provider.IContainerItemProvider.MessageProvider;
import io.rong.imlib.IRongCallback.IDownloadMediaMessageCallback;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.RongIMClient.DestructCountDownTimerListener;
import io.rong.imlib.RongIMClient.ErrorCode;
import io.rong.imlib.common.NetUtils;
import io.rong.imlib.model.Message;
import io.rong.imlib.model.Message.MessageDirection;
import io.rong.message.HQVoiceMessage;

@ProviderTag(
        messageContent = HQVoiceMessage.class,
        showReadState = true,
        showPortrait = false
)
public class BurnHQVoiceMessageProvider extends MessageProvider<HQVoiceMessage> {
    private static final String TAG = "BurnHQVoiceMessageProvider";

    public BurnHQVoiceMessageProvider() {
    }

    public View newView(Context context, ViewGroup group) {
        View view = LayoutInflater.from(context).inflate(layout.rc_item_hq_voice_message, null);
        BurnHQVoiceMessageProvider.ViewHolder holder = new BurnHQVoiceMessageProvider.ViewHolder();
        holder.left = view.findViewById(id.rc_left);
        holder.right = view.findViewById(id.rc_right);
        holder.img = view.findViewById(id.rc_img);
        holder.unread = view.findViewById(id.rc_voice_unread);
        holder.downloadError = view.findViewById(id.rc_voice_download_error);
        holder.downloadProcessing = view.findViewById(id.rc_download_progress);
        holder.sendFire = view.findViewById(id.fl_send_fire);
        holder.receiverFire = view.findViewById(id.fl_receiver_fire);
        holder.receiverFireImg = view.findViewById(id.iv_receiver_fire);
        holder.receiverFireText = view.findViewById(id.tv_receiver_fire);
        holder.content = view.findViewById(id.rc_layout);
        holder.ivFireLeft = view.findViewById(id.ivFireLeft);
        holder.ivFireRight = view.findViewById(id.ivFireRight);
        view.setTag(holder);
        return view;
    }

    public void bindView(View v, int position, HQVoiceMessage content, UIMessage message) {
        BurnHQVoiceMessageProvider.ViewHolder holder = (BurnHQVoiceMessageProvider.ViewHolder) v.getTag();
        if (content.isDestruct()) {
            if (message.getMessageDirection() == MessageDirection.SEND) {
                holder.sendFire.setVisibility(View.VISIBLE);
                holder.receiverFire.setVisibility(View.GONE);
            } else {
                holder.sendFire.setVisibility(View.GONE);
                holder.receiverFire.setVisibility(View.VISIBLE);
                DestructManager.getInstance().addListener(message.getUId(), new BurnHQVoiceMessageProvider.DestructListener(holder, message), "BurnHQVoiceMessageProvider");
                if (message.getMessage().getReadTime() > 0L) {
                    holder.receiverFireText.setVisibility(View.VISIBLE);
                    holder.receiverFireImg.setVisibility(View.GONE);
                    String unFinishTime;
                    if (TextUtils.isEmpty(message.getUnDestructTime())) {
                        unFinishTime = DestructManager.getInstance().getUnFinishTime(message.getUId());
                    } else {
                        unFinishTime = message.getUnDestructTime();
                    }

                    holder.receiverFireText.setText(unFinishTime);
                    DestructManager.getInstance().startDestruct(message.getMessage());
                } else {
                    holder.receiverFireText.setVisibility(View.GONE);
                    holder.receiverFireImg.setVisibility(View.VISIBLE);
                }
            }
        } else {
            holder.sendFire.setVisibility(View.GONE);
            holder.receiverFire.setVisibility(View.GONE);
        }

        boolean listened;
        Uri playingUri;
        if (message.continuePlayAudio) {
            playingUri = AudioPlayManager.getInstance().getPlayingUri();
            if (playingUri == null || !playingUri.equals(content.getLocalPath())) {
                listened = message.getMessage().getReceivedStatus().isListened();
                AudioPlayManager.getInstance().startPlay(v.getContext(), content.getLocalPath(), new BurnHQVoiceMessageProvider.VoiceMessagePlayListener(v.getContext(), message, holder, listened));
            }
        } else {
            playingUri = AudioPlayManager.getInstance().getPlayingUri();
            if (playingUri != null && playingUri.equals(content.getLocalPath())) {
                this.setLayout(v.getContext(), holder, message, true);
                listened = message.getMessage().getReceivedStatus().isListened();
                AudioPlayManager.getInstance().setPlayListener(new BurnHQVoiceMessageProvider.VoiceMessagePlayListener(v.getContext(), message, holder, listened));
            } else {
                this.setLayout(v.getContext(), holder, message, false);
            }
        }

    }

    public void onItemClick(View view, int position, HQVoiceMessage content, UIMessage message) {
        if (content != null) {
            RLog.d("BurnHQVoiceMessageProvider", "Item index:" + position + " content.getLocalPath():" + content.getLocalPath());
            BurnHQVoiceMessageProvider.ViewHolder holder = (BurnHQVoiceMessageProvider.ViewHolder) view.getTag();
            if (AudioPlayManager.getInstance().isPlaying()) {
                if (AudioPlayManager.getInstance().getPlayingUri().equals(content.getLocalPath())) {
                    AudioPlayManager.getInstance().stopPlay();
                    return;
                }

                AudioPlayManager.getInstance().stopPlay();
            }

            if (!AudioPlayManager.getInstance().isInNormalMode(view.getContext()) && AudioPlayManager.getInstance().isInVOIPMode(view.getContext())) {
                Toast.makeText(view.getContext(), view.getContext().getString(string.rc_voip_occupying), Toast.LENGTH_SHORT).show();
            } else {
                boolean listened = message.getMessage().getReceivedStatus().isListened();
                this.playOrDownloadHQVoiceMsg(view, content, message, holder, listened);
            }
        }
    }

    private void playOrDownloadHQVoiceMsg(View view, HQVoiceMessage content, UIMessage message, BurnHQVoiceMessageProvider.ViewHolder holder, boolean listened) {
        boolean ifDownloadHQVoiceMsg = content.getLocalPath() == null || TextUtils.isEmpty(content.getLocalPath().toString());
        if (message.getMessageDirection() == MessageDirection.RECEIVE) {
            ifDownloadHQVoiceMsg = content.getLocalPath() == null || TextUtils.isEmpty(content.getLocalPath().toString()) || !this.isFileExists(content.getLocalPath().toString());
        }

        if (ifDownloadHQVoiceMsg) {
            this.downloadHQVoiceMsg(view, message, holder, listened);
        } else {
            this.playHQVoiceMessage(view, content, message, holder, listened);
        }

    }

    private void downloadHQVoiceMsg(final View view, final UIMessage uiMessage, final BurnHQVoiceMessageProvider.ViewHolder holder, final boolean listened) {
        RongIM.getInstance().downloadMediaMessage(uiMessage.getMessage(), new IDownloadMediaMessageCallback() {
            public void onSuccess(Message message) {
                RLog.d("BurnHQVoiceMessageProvider", "playOrDownloadHQVoiceMsg onSuccess");
                holder.downloadError.setVisibility(View.GONE);
                holder.downloadProcessing.setVisibility(View.GONE);
                BurnHQVoiceMessageProvider.this.playHQVoiceMessage(view, (HQVoiceMessage) message.getContent(), uiMessage, holder, listened);
            }

            public void onProgress(Message message, int progress) {
                holder.downloadProcessing.setVisibility(View.VISIBLE);
            }

            public void onError(Message message, ErrorCode code) {
                RLog.d("BurnHQVoiceMessageProvider", "playOrDownloadHQVoiceMsg onError");
                holder.downloadError.setVisibility(View.VISIBLE);
                holder.downloadProcessing.setVisibility(View.GONE);
            }

            public void onCanceled(Message message) {
            }
        });
    }

    private void playHQVoiceMessage(View view, HQVoiceMessage content, UIMessage message, BurnHQVoiceMessageProvider.ViewHolder holder, boolean listened) {
        holder.unread.setVisibility(View.GONE);
        AudioPlayManager.getInstance().startPlay(view.getContext(), content.getLocalPath(), new BurnHQVoiceMessageProvider.VoiceMessagePlayListener(view.getContext(), message, holder, listened));
    }

    private boolean isFileExists(String filePath) {
        if (TextUtils.isEmpty(filePath)) {
            return false;
        } else {
            if (filePath.startsWith("file://")) {
                filePath = filePath.substring(7);
            }

            File file = new File(filePath);
            return file.exists();
        }
    }

    private void setLayout(Context context, BurnHQVoiceMessageProvider.ViewHolder holder, UIMessage message, boolean playing) {
        holder.ivFireRight.setVisibility(View.INVISIBLE);
        holder.ivFireLeft.setVisibility(View.INVISIBLE);
        HQVoiceMessage content = (HQVoiceMessage) message.getContent();
        ConversationInfo conversationInfo = null;
        if (!TextUtils.isEmpty(content.getExtra())) {
            conversationInfo = GsonUtils.fromJson(content.getExtra(), ConversationInfo.class);
        }
        int minWidth = 70;
        int maxWidth = 204;
        float scale = context.getResources().getDisplayMetrics().density;
        minWidth = (int) ((float) minWidth * scale + 0.5F);
        maxWidth = (int) ((float) maxWidth * scale + 0.5F);
        int duration = AudioRecordManager.getInstance().getMaxVoiceDuration();
        holder.img.getLayoutParams().width = minWidth + (maxWidth - minWidth) / duration * content.getDuration();
        AnimationDrawable animationDrawable;
        if (message.getMessageDirection() == MessageDirection.SEND) {
            if (conversationInfo != null && conversationInfo.getMessageBurnTime() != -1) {
                holder.ivFireRight.setVisibility(View.VISIBLE);
            }
            holder.left.setText(String.format("%s\"", content.getDuration()));
            holder.left.setVisibility(View.VISIBLE);
            holder.right.setVisibility(View.GONE);
            holder.unread.setVisibility(View.GONE);
            holder.downloadError.setVisibility(View.GONE);
            holder.downloadProcessing.setVisibility(View.GONE);
            holder.img.setScaleType(ScaleType.FIT_END);
            holder.content.setBackgroundResource(drawable.shape_rc_bubble_right);
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
            holder.content.setBackground(null);
            holder.right.setText(String.format("%s\"", content.getDuration()));
            holder.right.setVisibility(View.VISIBLE);
            holder.left.setVisibility(View.GONE);
            RLog.d("BurnHQVoiceMessageProvider", "message.getExtra() = " + message.getExtra());
            holder.downloadProcessing.setVisibility(View.VISIBLE);
            holder.downloadError.setVisibility(View.GONE);
            if (((HQVoiceMessage) message.getContent()).getLocalPath() != null) {
                holder.downloadProcessing.setVisibility(View.GONE);
                holder.downloadError.setVisibility(View.GONE);
                holder.content.setBackgroundResource(drawable.shape_rc_bubble_left);
                if (conversationInfo != null && conversationInfo.getMessageBurnTime() != -1) {
                    holder.ivFireLeft.setVisibility(View.VISIBLE);
                }
            } else if (!NetUtils.isNetWorkAvailable(context)) {
                holder.downloadError.setVisibility(View.VISIBLE);
                holder.downloadProcessing.setVisibility(View.GONE);
            }

            if (!message.getReceivedStatus().isListened() && ((HQVoiceMessage) message.getContent()).getLocalPath() != null) {
                holder.unread.setVisibility(View.VISIBLE);
            } else {
                holder.unread.setVisibility(View.GONE);
            }

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

            holder.img.setScaleType(ScaleType.FIT_START);
        }

    }

    public Spannable getContentSummary(HQVoiceMessage data) {
        return null;
    }

    public Spannable getContentSummary(Context context, HQVoiceMessage data) {
        return data.isDestruct() ? new SpannableString(context.getString(string.rc_message_content_burn)) : new SpannableString(context.getString(string.rc_message_content_voice));
    }

    private static class DestructListener implements DestructCountDownTimerListener {
        private WeakReference<BurnHQVoiceMessageProvider.ViewHolder> mHolder;
        private UIMessage mUIMessage;

        DestructListener(BurnHQVoiceMessageProvider.ViewHolder pHolder, UIMessage pUIMessage) {
            this.mHolder = new WeakReference(pHolder);
            this.mUIMessage = pUIMessage;
        }

        public void onTick(long millisUntilFinished, String messageId) {
            if (this.mUIMessage.getUId().equals(messageId)) {
                BurnHQVoiceMessageProvider.ViewHolder viewHolder = this.mHolder.get();
                if (viewHolder != null) {
                    viewHolder.receiverFireText.setVisibility(View.VISIBLE);
                    viewHolder.receiverFireImg.setVisibility(View.GONE);
                    String unDestructTime = String.valueOf(Math.max(millisUntilFinished, 1L));
                    viewHolder.receiverFireText.setText(unDestructTime);
                    this.mUIMessage.setUnDestructTime(unDestructTime);
                }
            }
        }

        public void onStop(String messageId) {
            if (this.mUIMessage.getUId().equals(messageId)) {
                BurnHQVoiceMessageProvider.ViewHolder viewHolder = this.mHolder.get();
                if (viewHolder != null) {
                    viewHolder.receiverFireText.setVisibility(View.GONE);
                    viewHolder.receiverFireImg.setVisibility(View.VISIBLE);
                    this.mUIMessage.setUnDestructTime(null);
                }
            }
        }
    }

    private static class ViewHolder {
        ImageView img;
        TextView left;
        TextView right;
        ImageView unread;
        ImageView downloadError;
        ProgressBar downloadProcessing;
        FrameLayout sendFire;
        FrameLayout receiverFire;
        ImageView receiverFireImg;
        TextView receiverFireText;
        FrameLayout content;
        ImageView ivFireLeft;
        ImageView ivFireRight;

        private ViewHolder() {
        }
    }

    private class VoiceMessagePlayListener implements IAudioPlayListener {
        private Context context;
        private UIMessage message;
        private BurnHQVoiceMessageProvider.ViewHolder holder;
        private boolean listened;

        VoiceMessagePlayListener(Context context, UIMessage message, BurnHQVoiceMessageProvider.ViewHolder holder, boolean listened) {
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
            BurnHQVoiceMessageProvider.this.setLayout(this.context, this.holder, this.message, true);
            EventBus.getDefault().post(new AudioListenedEvent(this.message.getMessage()));
            if (this.message.getContent().isDestruct() && this.message.getMessageDirection().equals(MessageDirection.RECEIVE)) {
                DestructManager.getInstance().stopDestruct(this.message.getMessage());
                EventBus.getDefault().post(new changeDestructionReadTimeEvent(this.message.getMessage()));
            }

        }

        public void onStop(Uri uri) {
            if (this.message.getContent() instanceof HQVoiceMessage) {
                this.message.setListening(false);
                BurnHQVoiceMessageProvider.this.setLayout(this.context, this.holder, this.message, false);
                if (this.message.getContent().isDestruct() && this.message.getMessageDirection().equals(MessageDirection.RECEIVE)) {
                    DestructManager.getInstance().startDestruct(this.message.getMessage());
                }
            }

        }

        public void onComplete(Uri uri) {
            PlayAudioEvent event = PlayAudioEvent.obtain();
            event.messageId = this.message.getMessageId();
            if (this.message.isListening() && this.message.getMessageDirection().equals(MessageDirection.RECEIVE)) {
                try {
                    event.continuously = this.context.getResources().getBoolean(bool.rc_play_audio_continuous);
                } catch (NotFoundException var4) {
                    RLog.e("BurnHQVoiceMessageProvider", "VoiceMessagePlayListener.onComplete", var4);
                }
            }

            if (event.continuously && !this.message.getContent().isDestruct()) {
                EventBus.getDefault().post(event);
            }

            this.message.setListening(false);
            BurnHQVoiceMessageProvider.this.setLayout(this.context, this.holder, this.message, false);
            if (this.message.getContent().isDestruct() && this.message.getMessageDirection().equals(MessageDirection.RECEIVE)) {
                DestructManager.getInstance().startDestruct(this.message.getMessage());
            }

        }
    }
}

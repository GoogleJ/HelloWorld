//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package io.rong.imkit.widget.provider;

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
import io.rong.imlib.RongIMClient.ResultCallback;
import io.rong.imlib.common.NetUtils;
import io.rong.imlib.model.Message;
import io.rong.imlib.model.Message.MessageDirection;
import io.rong.message.HQVoiceMessage;

@ProviderTag(
        messageContent = HQVoiceMessage.class,
        showReadState = true
)
public class HQVoiceMessageItemProvider extends MessageProvider<HQVoiceMessage> {
    private static final String TAG = "HQVoiceMessageItemProvider";

    public HQVoiceMessageItemProvider() {
    }

    public View newView(Context context, ViewGroup group) {
        View view = LayoutInflater.from(context).inflate(layout.rc_item_hq_voice_message, (ViewGroup) null);
        HQVoiceMessageItemProvider.ViewHolder holder = new HQVoiceMessageItemProvider.ViewHolder();
        holder.left = (TextView) view.findViewById(id.rc_left);
        holder.right = (TextView) view.findViewById(id.rc_right);
        holder.img = (ImageView) view.findViewById(id.rc_img);
        holder.unread = (ImageView) view.findViewById(id.rc_voice_unread);
        holder.downloadError = (ImageView) view.findViewById(id.rc_voice_download_error);
        holder.downloadProcessing = (ProgressBar) view.findViewById(id.rc_download_progress);
        holder.sendFire = (FrameLayout) view.findViewById(id.fl_send_fire);
        holder.receiverFire = (FrameLayout) view.findViewById(id.fl_receiver_fire);
        holder.receiverFireImg = (ImageView) view.findViewById(id.iv_receiver_fire);
        holder.receiverFireText = (TextView) view.findViewById(id.tv_receiver_fire);
        view.setTag(holder);
        return view;
    }

    public void bindView(View v, int position, HQVoiceMessage content, UIMessage message) {
        HQVoiceMessageItemProvider.ViewHolder holder = (HQVoiceMessageItemProvider.ViewHolder) v.getTag();
        if (content.isDestruct()) {
            if (message.getMessageDirection() == MessageDirection.SEND) {
                holder.sendFire.setVisibility(0);
                holder.receiverFire.setVisibility(8);
            } else {
                holder.sendFire.setVisibility(8);
                holder.receiverFire.setVisibility(0);
                DestructManager.getInstance().addListener(message.getUId(), new HQVoiceMessageItemProvider.DestructListener(holder, message), "HQVoiceMessageItemProvider");
                if (message.getMessage().getReadTime() > 0L) {
                    holder.receiverFireText.setVisibility(0);
                    holder.receiverFireImg.setVisibility(8);
                    String unFinishTime;
                    if (TextUtils.isEmpty(message.getUnDestructTime())) {
                        unFinishTime = DestructManager.getInstance().getUnFinishTime(message.getUId());
                    } else {
                        unFinishTime = message.getUnDestructTime();
                    }

                    holder.receiverFireText.setText(unFinishTime);
                    DestructManager.getInstance().startDestruct(message.getMessage());
                } else {
                    holder.receiverFireText.setVisibility(8);
                    holder.receiverFireImg.setVisibility(0);
                }
            }
        } else {
            holder.sendFire.setVisibility(8);
            holder.receiverFire.setVisibility(8);
        }

        boolean listened;
        Uri playingUri;
        if (message.continuePlayAudio) {
            playingUri = AudioPlayManager.getInstance().getPlayingUri();
            if (playingUri == null || !playingUri.equals(content.getLocalPath())) {
                listened = message.getMessage().getReceivedStatus().isListened();
                AudioPlayManager.getInstance().startPlay(v.getContext(), content.getLocalPath(), new HQVoiceMessageItemProvider.VoiceMessagePlayListener(v.getContext(), message, holder, listened));
            }
        } else {
            playingUri = AudioPlayManager.getInstance().getPlayingUri();
            if (playingUri != null && playingUri.equals(content.getLocalPath())) {
                this.setLayout(v.getContext(), holder, message, true);
                listened = message.getMessage().getReceivedStatus().isListened();
                AudioPlayManager.getInstance().setPlayListener(new HQVoiceMessageItemProvider.VoiceMessagePlayListener(v.getContext(), message, holder, listened));
            } else {
                this.setLayout(v.getContext(), holder, message, false);
            }
        }

    }

    public void onItemClick(View view, int position, HQVoiceMessage content, UIMessage message) {
        if (content != null) {
            RLog.d("HQVoiceMessageItemProvider", "Item index:" + position + " content.getLocalPath():" + content.getLocalPath());
            HQVoiceMessageItemProvider.ViewHolder holder = (HQVoiceMessageItemProvider.ViewHolder) view.getTag();
            if (AudioPlayManager.getInstance().isPlaying()) {
                if (AudioPlayManager.getInstance().getPlayingUri().equals(content.getLocalPath())) {
                    AudioPlayManager.getInstance().stopPlay();
                    return;
                }

                AudioPlayManager.getInstance().stopPlay();
            }

            if (!AudioPlayManager.getInstance().isInNormalMode(view.getContext()) && AudioPlayManager.getInstance().isInVOIPMode(view.getContext())) {
                Toast.makeText(view.getContext(), view.getContext().getString(string.rc_voip_occupying), 0).show();
            } else {
                boolean listened = message.getMessage().getReceivedStatus().isListened();
                this.playOrDownloadHQVoiceMsg(view, content, message, holder, listened);
            }
        }
    }

    private void playOrDownloadHQVoiceMsg(View view, HQVoiceMessage content, UIMessage message, HQVoiceMessageItemProvider.ViewHolder holder, boolean listened) {
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

    private void downloadHQVoiceMsg(final View view, final UIMessage uiMessage, final HQVoiceMessageItemProvider.ViewHolder holder, final boolean listened) {
        RongIM.getInstance().downloadMediaMessage(uiMessage.getMessage(), new IDownloadMediaMessageCallback() {
            public void onSuccess(Message message) {
                RLog.d("HQVoiceMessageItemProvider", "playOrDownloadHQVoiceMsg onSuccess");
                holder.downloadError.setVisibility(8);
                holder.downloadProcessing.setVisibility(8);
                HQVoiceMessageItemProvider.this.playHQVoiceMessage(view, (HQVoiceMessage) message.getContent(), uiMessage, holder, listened);
            }

            public void onProgress(Message message, int progress) {
                holder.downloadProcessing.setVisibility(0);
            }

            public void onError(Message message, ErrorCode code) {
                RLog.d("HQVoiceMessageItemProvider", "playOrDownloadHQVoiceMsg onError");
                holder.downloadError.setVisibility(0);
                holder.downloadProcessing.setVisibility(8);
            }

            public void onCanceled(Message message) {
            }
        });
    }

    private void playHQVoiceMessage(View view, HQVoiceMessage content, UIMessage message, HQVoiceMessageItemProvider.ViewHolder holder, boolean listened) {
        holder.unread.setVisibility(8);
        AudioPlayManager.getInstance().startPlay(view.getContext(), content.getLocalPath(), new HQVoiceMessageItemProvider.VoiceMessagePlayListener(view.getContext(), message, holder, listened));
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

    private void setLayout(Context context, HQVoiceMessageItemProvider.ViewHolder holder, UIMessage message, boolean playing) {
        HQVoiceMessage content = (HQVoiceMessage) message.getContent();
        int minWidth = 70;
        int maxWidth = 204;
        float scale = context.getResources().getDisplayMetrics().density;
        minWidth = (int) ((float) minWidth * scale + 0.5F);
        maxWidth = (int) ((float) maxWidth * scale + 0.5F);
        int duration = AudioRecordManager.getInstance().getMaxVoiceDuration();
        holder.img.getLayoutParams().width = minWidth + (maxWidth - minWidth) / duration * content.getDuration();
        AnimationDrawable animationDrawable;
        if (message.getMessageDirection() == MessageDirection.SEND) {
            holder.left.setText(String.format("%s\"", content.getDuration()));
            holder.left.setVisibility(0);
            holder.right.setVisibility(8);
            holder.unread.setVisibility(8);
            holder.downloadError.setVisibility(8);
            holder.downloadProcessing.setVisibility(8);
            holder.img.setScaleType(ScaleType.FIT_END);
            holder.img.setBackgroundResource(drawable.rc_ic_bubble_right);
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
            holder.right.setText(String.format("%s\"", content.getDuration()));
            holder.right.setVisibility(0);
            holder.left.setVisibility(8);
            RLog.d("HQVoiceMessageItemProvider", "message.getExtra() = " + message.getExtra());
            holder.downloadProcessing.setVisibility(0);
            holder.downloadError.setVisibility(8);
            if (((HQVoiceMessage) message.getContent()).getLocalPath() != null) {
                holder.downloadProcessing.setVisibility(8);
                holder.downloadError.setVisibility(8);
            } else if (!NetUtils.isNetWorkAvailable(context)) {
                holder.downloadError.setVisibility(0);
                holder.downloadProcessing.setVisibility(8);
            }

            if (!message.getReceivedStatus().isListened() && ((HQVoiceMessage) message.getContent()).getLocalPath() != null) {
                holder.unread.setVisibility(0);
            } else {
                holder.unread.setVisibility(8);
            }

            holder.img.setBackgroundResource(drawable.rc_ic_bubble_left);
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
        private WeakReference<HQVoiceMessageItemProvider.ViewHolder> mHolder;
        private UIMessage mUIMessage;

        DestructListener(HQVoiceMessageItemProvider.ViewHolder pHolder, UIMessage pUIMessage) {
            this.mHolder = new WeakReference(pHolder);
            this.mUIMessage = pUIMessage;
        }

        public void onTick(long millisUntilFinished, String messageId) {
            if (this.mUIMessage.getUId().equals(messageId)) {
                HQVoiceMessageItemProvider.ViewHolder viewHolder = (HQVoiceMessageItemProvider.ViewHolder) this.mHolder.get();
                if (viewHolder != null) {
                    viewHolder.receiverFireText.setVisibility(0);
                    viewHolder.receiverFireImg.setVisibility(8);
                    String unDestructTime = String.valueOf(Math.max(millisUntilFinished, 1L));
                    viewHolder.receiverFireText.setText(unDestructTime);
                    this.mUIMessage.setUnDestructTime(unDestructTime);
                }
            }

        }

        public void onStop(String messageId) {
            if (this.mUIMessage.getUId().equals(messageId)) {
                HQVoiceMessageItemProvider.ViewHolder viewHolder = (HQVoiceMessageItemProvider.ViewHolder) this.mHolder.get();
                if (viewHolder != null) {
                    viewHolder.receiverFireText.setVisibility(8);
                    viewHolder.receiverFireImg.setVisibility(0);
                    this.mUIMessage.setUnDestructTime((String) null);
                }
            }

        }
    }

    private class VoiceMessagePlayListener implements IAudioPlayListener {
        private Context context;
        private UIMessage message;
        private HQVoiceMessageItemProvider.ViewHolder holder;
        private boolean listened;

        VoiceMessagePlayListener(Context context, UIMessage message, HQVoiceMessageItemProvider.ViewHolder holder, boolean listened) {
            this.context = context;
            this.message = message;
            this.holder = holder;
            this.listened = listened;
        }

        public void onStart(Uri uri) {
            this.message.continuePlayAudio = false;
            this.message.setListening(true);
            this.message.getReceivedStatus().setListened();
            RongIMClient.getInstance().setMessageReceivedStatus(this.message.getMessageId(), this.message.getReceivedStatus(), (ResultCallback) null);
            HQVoiceMessageItemProvider.this.setLayout(this.context, this.holder, this.message, true);
            EventBus.getDefault().post(new AudioListenedEvent(this.message.getMessage()));
            if (this.message.getContent().isDestruct() && this.message.getMessageDirection().equals(MessageDirection.RECEIVE)) {
                DestructManager.getInstance().stopDestruct(this.message.getMessage());
                EventBus.getDefault().post(new changeDestructionReadTimeEvent(this.message.getMessage()));
            }

        }

        public void onStop(Uri uri) {
            if (this.message.getContent() instanceof HQVoiceMessage) {
                this.message.setListening(false);
                HQVoiceMessageItemProvider.this.setLayout(this.context, this.holder, this.message, false);
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
                    RLog.e("HQVoiceMessageItemProvider", "VoiceMessagePlayListener.onComplete", var4);
                }
            }

            if (event.continuously && !this.message.getContent().isDestruct()) {
                EventBus.getDefault().post(event);
            }

            this.message.setListening(false);
            HQVoiceMessageItemProvider.this.setLayout(this.context, this.holder, this.message, false);
            if (this.message.getContent().isDestruct() && this.message.getMessageDirection().equals(MessageDirection.RECEIVE)) {
                DestructManager.getInstance().startDestruct(this.message.getMessage());
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

        private ViewHolder() {
        }
    }
}
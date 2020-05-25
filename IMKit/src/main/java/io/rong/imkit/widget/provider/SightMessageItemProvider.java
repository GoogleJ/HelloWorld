//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package io.rong.imkit.widget.provider;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.net.Uri.Builder;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.lang.ref.WeakReference;

import io.rong.imkit.R;
import io.rong.imkit.R.dimen;
import io.rong.imkit.R.drawable;
import io.rong.imkit.R.id;
import io.rong.imkit.R.layout;
import io.rong.imkit.R.string;
import io.rong.imkit.RongIM;
import io.rong.imkit.destruct.DestructManager;
import io.rong.imkit.manager.SendMediaManager;
import io.rong.imkit.model.ProviderTag;
import io.rong.imkit.model.UIMessage;
import io.rong.imkit.utilities.OptionsPopupDialog;
import io.rong.imkit.utilities.OptionsPopupDialog.OnOptionsItemClickedListener;
import io.rong.imkit.utilities.PermissionCheckUtil;
import io.rong.imkit.utilities.RongUtils;
import io.rong.imkit.utils.RongOperationPermissionUtils;
import io.rong.imkit.widget.AsyncImageView;
import io.rong.imkit.widget.CircleProgressView;
import io.rong.imkit.widget.provider.IContainerItemProvider.MessageProvider;
import io.rong.imlib.IRongCallback.ISendMediaMessageCallback;
import io.rong.imlib.RongIMClient.DestructCountDownTimerListener;
import io.rong.imlib.RongIMClient.ErrorCode;
import io.rong.imlib.RongIMClient.OperationCallback;
import io.rong.imlib.RongIMClient.ResultCallback;
import io.rong.imlib.model.Message.MessageDirection;
import io.rong.imlib.model.Message.SentStatus;
import io.rong.message.SightMessage;

@ProviderTag(
        messageContent = SightMessage.class,
        showProgress = false,
        showReadState = true
)
public class SightMessageItemProvider extends MessageProvider<SightMessage> {
    private static final String TAG = "Sight-SightMessageItemProvider";

    public SightMessageItemProvider() {
    }

    public View newView(Context context, ViewGroup group) {
        View view = LayoutInflater.from(context).inflate(layout.rc_item_sight_message, (ViewGroup) null);
        SightMessageItemProvider.ViewHolder holder = new SightMessageItemProvider.ViewHolder();
        holder.operationButton = (RelativeLayout) view.findViewById(id.rc_sight_operation);
        holder.operationIcon = (ImageView) view.findViewById(id.rc_sight_operation_icon);
        holder.message = (FrameLayout) view.findViewById(id.rc_message);
        holder.compressProgress = (ProgressBar) view.findViewById(id.compressVideoBar);
        holder.loadingProgress = (CircleProgressView) view.findViewById(id.rc_sight_progress);
        holder.thumbImg = (AsyncImageView) view.findViewById(id.rc_sight_thumb);
        holder.tagImg = (ImageView) view.findViewById(id.rc_sight_tag);
        holder.duration = (TextView) view.findViewById(id.rc_sight_duration);
        holder.fireView = (FrameLayout) view.findViewById(id.rc_destruct_click);
        holder.sendFire = (FrameLayout) view.findViewById(id.fl_send_fire);
        holder.receiverFire = (FrameLayout) view.findViewById(id.fl_receiver_fire);
        holder.receiverFireImg = (ImageView) view.findViewById(id.iv_receiver_fire);
        holder.receiverFireText = (TextView) view.findViewById(id.tv_receiver_fire);
        holder.clickHint = (TextView) view.findViewById(id.rc_destruct_click_hint);
        view.setTag(holder);
        return view;
    }

    public void onItemClick(View view, int position, SightMessage content, UIMessage uiMessage) {
        if (content != null) {
            if (!RongOperationPermissionUtils.isMediaOperationPermit(view.getContext())) {
                return;
            }

            String[] permissions = new String[]{"android.permission.WRITE_EXTERNAL_STORAGE"};
            if (!PermissionCheckUtil.checkPermissions(view.getContext(), permissions)) {
                Activity activity = (Activity) view.getContext();
                PermissionCheckUtil.requestPermissions(activity, permissions, 100);
                return;
            }

            Builder builder = new Builder();
            builder.scheme("rong").authority(view.getContext().getPackageName()).appendPath("sight").appendPath("player");
            String intentUrl = builder.build().toString();
            Intent intent = new Intent("android.intent.action.VIEW", Uri.parse(intentUrl));
            intent.setPackage(view.getContext().getPackageName());
            intent.putExtra("SightMessage", content);
            intent.putExtra("Message", uiMessage.getMessage());
            intent.putExtra("Progress", uiMessage.getProgress());
            if (intent.resolveActivity(view.getContext().getPackageManager()) != null) {
                view.getContext().startActivity(intent);
            } else {
                Toast.makeText(view.getContext(), "Sight Module does not exist.", 0).show();
            }
        }

    }

    public void bindView(View v, int position, SightMessage content, UIMessage message) {
        SightMessageItemProvider.ViewHolder holder = (SightMessageItemProvider.ViewHolder) v.getTag();
        int progress;
        if (message.getMessageDirection() == MessageDirection.SEND) {
            holder.message.setBackgroundResource(drawable.rc_ic_bubble_no_right);
            progress = (int) v.getContext().getResources().getDimension(dimen.rc_dimen_size_12);
            holder.duration.setPadding(0, 0, progress, 0);
        } else {
            holder.message.setBackgroundResource(drawable.rc_ic_bubble_no_left);
            progress = (int) v.getContext().getResources().getDimension(dimen.rc_dimen_size_6);
            holder.duration.setPadding(0, 0, progress, 0);
        }

        progress = message.getProgress();
        SentStatus status = message.getSentStatus();
        if (content.isDestruct()) {
            holder.fireView.setVisibility(0);
            holder.thumbImg.setVisibility(8);
            Drawable drawable;
            if (message.getMessageDirection() == MessageDirection.SEND) {
                holder.sendFire.setVisibility(0);
                holder.receiverFire.setVisibility(8);
                holder.fireView.setBackgroundResource(R.drawable.rc_ic_bubble_no_right);
                drawable = v.getContext().getResources().getDrawable(R.drawable.rc_destruct_video_play);
                drawable.setBounds(0, 0, RongUtils.dip2px(22.0F), RongUtils.dip2px(22.0F));
                holder.clickHint.setCompoundDrawables((Drawable) null, drawable, (Drawable) null, (Drawable) null);
                holder.clickHint.setTextColor(Color.parseColor("#FFFFFF"));
                holder.duration.setTextColor(Color.parseColor("#FFFFFF"));
            } else {
                holder.sendFire.setVisibility(8);
                holder.receiverFire.setVisibility(0);
                holder.fireView.setBackgroundResource(R.drawable.rc_ic_bubble_no_left);
                drawable = v.getContext().getResources().getDrawable(R.drawable.rc_icon_fire_video_play);
                drawable.setBounds(0, 0, RongUtils.dip2px(22.0F), RongUtils.dip2px(22.0F));
                holder.clickHint.setCompoundDrawables((Drawable) null, drawable, (Drawable) null, (Drawable) null);
                holder.clickHint.setTextColor(Color.parseColor("#F4B50B"));
                holder.duration.setTextColor(Color.parseColor("#F4B50B"));
                DestructManager.getInstance().addListener(message.getUId(), new SightMessageItemProvider.DestructListener(holder, message), "Sight-SightMessageItemProvider");
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
            holder.fireView.setVisibility(8);
            holder.thumbImg.setVisibility(0);
            holder.thumbImg.setResource(content.getThumbUri());
            holder.duration.setTextColor(Color.parseColor("#FFFFFF"));
        }

        holder.duration.setText(this.getSightDuration(content.getDuration()));
        if (progress > 0 && progress < 100) {
            holder.loadingProgress.setProgress(progress, true);
            holder.tagImg.setVisibility(8);
            holder.loadingProgress.setVisibility(0);
            holder.compressProgress.setVisibility(8);
        } else if (status.equals(SentStatus.SENDING)) {
            holder.tagImg.setVisibility(8);
            holder.loadingProgress.setVisibility(8);
            holder.compressProgress.setVisibility(0);
        } else {
            holder.tagImg.setVisibility(0);
            holder.loadingProgress.setVisibility(8);
            holder.compressProgress.setVisibility(8);
        }

    }

    public Spannable getContentSummary(SightMessage data) {
        return null;
    }

    public Spannable getContentSummary(Context context, SightMessage data) {
        return data.isDestruct() ? new SpannableString(context.getString(string.rc_message_content_burn)) : new SpannableString(context.getResources().getString(string.rc_message_content_sight));
    }

    public void onItemLongClick(View view, int position, SightMessage content, final UIMessage message) {
        if (message.getMessage().getSentStatus().getValue() < SentStatus.SENT.getValue()) {
            String[] items = new String[]{view.getContext().getResources().getString(string.rc_dialog_item_message_delete)};
            OptionsPopupDialog.newInstance(view.getContext(), items).setOptionsPopupDialogListener(new OnOptionsItemClickedListener() {
                public void onOptionsItemClicked(int which) {
                    if (which == 0) {
                        SendMediaManager.getInstance().cancelSendingMedia(message.getConversationType(), message.getTargetId(), message.getMessageId());
                        RongIM.getInstance().cancelSendMediaMessage(message.getMessage(), (OperationCallback) null);
                        RongIM.getInstance().deleteMessages(new int[]{message.getMessageId()}, (ResultCallback) null);
                    }

                }
            }).show();
        } else {
            super.onItemLongClick(view, position, content, message);
        }

    }

    private boolean isSightDownloaded(SightMessage sightMessage) {
        if (sightMessage.getLocalPath() != null && !TextUtils.isEmpty(sightMessage.getLocalPath().toString())) {
            String path = sightMessage.getLocalPath().toString();
            if (path.startsWith("file://")) {
                path = path.substring(7);
            }

            File file = new File(path);
            return file.exists();
        } else {
            return false;
        }
    }

    private void handleSendingView(final UIMessage message, final SightMessageItemProvider.ViewHolder holder) {
        final SentStatus status = message.getSentStatus();
        if (status.equals(SentStatus.SENDING)) {
            holder.operationButton.setVisibility(0);
            holder.operationIcon.setImageResource(drawable.rc_file_icon_cancel);
        } else if (status.equals(SentStatus.CANCELED)) {
            holder.operationButton.setVisibility(0);
            holder.operationIcon.setImageResource(drawable.rc_ic_warning);
        } else {
            holder.operationButton.setVisibility(8);
        }

        holder.operationButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                if (status.equals(SentStatus.SENDING)) {
                    RongIM.getInstance().cancelSendMediaMessage(message.getMessage(), new OperationCallback() {
                        public void onSuccess() {
                            holder.operationButton.setVisibility(0);
                            holder.operationIcon.setImageResource(drawable.rc_ic_warning);
                            holder.tagImg.setVisibility(0);
                            holder.loadingProgress.setVisibility(8);
                            holder.compressProgress.setVisibility(8);
                        }

                        public void onError(ErrorCode errorCode) {
                        }
                    });
                } else if (message.getSentStatus().equals(SentStatus.CANCELED)) {
                    RongIM.getInstance().deleteMessages(new int[]{message.getMessageId()}, new ResultCallback<Boolean>() {
                        public void onSuccess(Boolean aBoolean) {
                            if (aBoolean) {
                                message.getMessage().setMessageId(0);
                                RongIM.getInstance().sendMediaMessage(message.getMessage(), (String) null, (String) null, (ISendMediaMessageCallback) null);
                            }

                        }

                        public void onError(ErrorCode e) {
                        }
                    });
                }

            }
        });
    }

    private String getSightDuration(int time) {
        if (time <= 0) {
            return "00:00";
        } else {
            int minute = time / 60;
            String recordTime;
            int second;
            if (minute < 60) {
                second = time % 60;
                recordTime = this.unitFormat(minute) + ":" + this.unitFormat(second);
            } else {
                int hour = minute / 60;
                if (hour > 99) {
                    return "99:59:59";
                }

                minute %= 60;
                second = time - hour * 3600 - minute * 60;
                recordTime = this.unitFormat(hour) + ":" + this.unitFormat(minute) + ":" + this.unitFormat(second);
            }

            return recordTime;
        }
    }

    private String unitFormat(int time) {
        String formatTime;
        if (time >= 0 && time < 10) {
            formatTime = "0" + Integer.toString(time);
        } else {
            formatTime = "" + time;
        }

        return formatTime;
    }

    private static class DestructListener implements DestructCountDownTimerListener {
        private WeakReference<SightMessageItemProvider.ViewHolder> mHolder;
        private UIMessage mUIMessage;

        public DestructListener(SightMessageItemProvider.ViewHolder pHolder, UIMessage pUIMessage) {
            this.mHolder = new WeakReference(pHolder);
            this.mUIMessage = pUIMessage;
        }

        public void onTick(long millisUntilFinished, String pMessageId) {
            if (this.mUIMessage.getUId().equals(pMessageId)) {
                SightMessageItemProvider.ViewHolder viewHolder = (SightMessageItemProvider.ViewHolder) this.mHolder.get();
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
                SightMessageItemProvider.ViewHolder viewHolder = (SightMessageItemProvider.ViewHolder) this.mHolder.get();
                if (viewHolder != null) {
                    viewHolder.receiverFireText.setVisibility(8);
                    viewHolder.receiverFireImg.setVisibility(0);
                    this.mUIMessage.setUnDestructTime((String) null);
                }
            }

        }
    }

    private static class ViewHolder {
        RelativeLayout operationButton;
        ImageView operationIcon;
        FrameLayout message;
        AsyncImageView thumbImg;
        ImageView tagImg;
        ProgressBar compressProgress;
        CircleProgressView loadingProgress;
        TextView duration;
        FrameLayout fireView;
        FrameLayout sendFire;
        FrameLayout receiverFire;
        ImageView receiverFireImg;
        TextView receiverFireText;
        TextView clickHint;

        private ViewHolder() {
        }
    }
}
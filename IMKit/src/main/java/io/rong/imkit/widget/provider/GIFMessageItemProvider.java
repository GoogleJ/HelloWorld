//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package io.rong.imkit.widget.provider;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import java.lang.ref.WeakReference;

import io.rong.imkit.R;
import io.rong.imkit.R.drawable;
import io.rong.imkit.R.id;
import io.rong.imkit.R.integer;
import io.rong.imkit.R.layout;
import io.rong.imkit.R.string;
import io.rong.imkit.RongIM;
import io.rong.imkit.destruct.DestructManager;
import io.rong.imkit.model.ProviderTag;
import io.rong.imkit.model.UIMessage;
import io.rong.imkit.utilities.PermissionCheckUtil;
import io.rong.imkit.utilities.RongUtils;
import io.rong.imkit.widget.AsyncImageView;
import io.rong.imkit.widget.CircleProgressView;
import io.rong.imkit.widget.provider.IContainerItemProvider.MessageProvider;
import io.rong.imlib.IRongCallback.IDownloadMediaMessageCallback;
import io.rong.imlib.RongIMClient.DestructCountDownTimerListener;
import io.rong.imlib.model.Message;
import io.rong.imlib.model.Message.MessageDirection;
import io.rong.imlib.model.Message.SentStatus;
import io.rong.message.GIFMessage;

@ProviderTag(
        messageContent = GIFMessage.class,
        showProgress = false,
        showReadState = true
)
public class GIFMessageItemProvider extends MessageProvider<GIFMessage> {
    private static final String TAG = "GIFMessageItemProvider";

    public GIFMessageItemProvider() {
    }

    public View newView(Context context, ViewGroup group) {
        View view = LayoutInflater.from(context).inflate(layout.rc_item_gif_message, (ViewGroup) null);
        GIFMessageItemProvider.ViewHolder holder = new GIFMessageItemProvider.ViewHolder();
        holder.img = (AsyncImageView) view.findViewById(id.rc_img);
        holder.preProgress = (ProgressBar) view.findViewById(id.rc_pre_progress);
        holder.loadingProgress = (CircleProgressView) view.findViewById(id.rc_gif_progress);
        holder.startDownLoad = (ImageView) view.findViewById(id.rc_start_download);
        holder.downLoadFailed = (ImageView) view.findViewById(id.rc_download_failed);
        holder.length = (TextView) view.findViewById(id.rc_length);
        holder.fireView = (FrameLayout) view.findViewById(id.rc_destruct_click);
        holder.sendFire = (FrameLayout) view.findViewById(id.fl_send_fire);
        holder.receiverFire = (FrameLayout) view.findViewById(id.fl_receiver_fire);
        holder.receiverFireImg = (ImageView) view.findViewById(id.iv_receiver_fire);
        holder.receiverFireText = (TextView) view.findViewById(id.tv_receiver_fire);
        holder.clickHint = (TextView) view.findViewById(id.rc_destruct_click_hint);
        view.setTag(holder);
        return view;
    }

    public void onItemClick(View view, int position, GIFMessage content, UIMessage message) {
        GIFMessageItemProvider.ViewHolder holder = (GIFMessageItemProvider.ViewHolder) view.getTag();
        if (holder.startDownLoad.getVisibility() == 0) {
            holder.startDownLoad.setVisibility(8);
            if (this.checkPermission(view.getContext())) {
                this.downLoad(message.getMessage(), holder);
            } else {
                holder.downLoadFailed.setVisibility(0);
                holder.length.setVisibility(0);
                holder.length.setText(this.formatSize(content.getGifDataSize()));
                Toast.makeText(view.getContext(), string.rc_ac_file_download_request_permission, 0).show();
            }
        } else if (holder.downLoadFailed.getVisibility() == 0) {
            holder.downLoadFailed.setVisibility(8);
            if (this.checkPermission(view.getContext())) {
                this.downLoad(message.getMessage(), holder);
            } else {
                holder.downLoadFailed.setVisibility(0);
                holder.length.setVisibility(0);
                holder.length.setText(this.formatSize(content.getGifDataSize()));
                Toast.makeText(view.getContext(), string.rc_ac_file_download_request_permission, 0).show();
            }
        } else if (holder.preProgress.getVisibility() != 0 && holder.loadingProgress.getVisibility() != 0 && content != null) {
            Intent intent = new Intent("io.rong.imkit.intent.action.gifrview");
            intent.setPackage(view.getContext().getPackageName());
            intent.putExtra("message", message.getMessage());
            view.getContext().startActivity(intent);
        }

    }

    public void bindView(View v, int position, GIFMessage content, UIMessage message) {
        GIFMessageItemProvider.ViewHolder holder = (GIFMessageItemProvider.ViewHolder) v.getTag();
        holder.startDownLoad.setVisibility(8);
        holder.downLoadFailed.setVisibility(8);
        holder.preProgress.setVisibility(8);
        holder.loadingProgress.setVisibility(8);
        holder.length.setVisibility(8);
        int[] paramsValue = this.getParamsValue(v.getContext(), content.getWidth(), content.getHeight());
        holder.img.setLayoutParam(paramsValue[0], paramsValue[1]);
        holder.img.setImageDrawable(v.getContext().getResources().getDrawable(drawable.def_gif_bg));
        int progress = message.getProgress();
        if (message.getMessageDirection() == MessageDirection.SEND) {
            SentStatus status = message.getSentStatus();
            if (progress > 0 && progress < 100) {
                holder.loadingProgress.setProgress(progress, true);
                holder.loadingProgress.setVisibility(0);
                holder.preProgress.setVisibility(8);
            } else if (status.equals(SentStatus.SENDING)) {
                holder.loadingProgress.setVisibility(8);
            } else if (progress == -1) {
                holder.loadingProgress.setVisibility(8);
                holder.preProgress.setVisibility(8);
                holder.downLoadFailed.setVisibility(0);
                holder.length.setVisibility(0);
            } else {
                holder.loadingProgress.setVisibility(8);
                holder.preProgress.setVisibility(8);
            }
        } else if (message.getReceivedStatus().isDownload()) {
            if (progress > 0 && progress < 100) {
                holder.loadingProgress.setProgress(progress, true);
                holder.loadingProgress.setVisibility(0);
                holder.preProgress.setVisibility(8);
                holder.startDownLoad.setVisibility(8);
            } else if (progress == 100) {
                holder.loadingProgress.setVisibility(8);
                holder.preProgress.setVisibility(8);
                holder.length.setVisibility(8);
                holder.startDownLoad.setVisibility(8);
            } else if (progress == -1) {
                holder.loadingProgress.setVisibility(8);
                holder.preProgress.setVisibility(8);
                holder.downLoadFailed.setVisibility(0);
                holder.length.setVisibility(0);
                holder.startDownLoad.setVisibility(8);
            } else {
                holder.loadingProgress.setVisibility(8);
                holder.preProgress.setVisibility(0);
                holder.length.setVisibility(0);
                holder.startDownLoad.setVisibility(8);
            }
        } else {
            holder.loadingProgress.setVisibility(8);
            holder.preProgress.setVisibility(8);
            holder.length.setVisibility(8);
            holder.startDownLoad.setVisibility(8);
            if (progress == -1) {
                holder.downLoadFailed.setVisibility(0);
                holder.length.setVisibility(0);
                holder.length.setText(this.formatSize(content.getGifDataSize()));
            }
        }

        if (content.isDestruct()) {
            Drawable drawable;
            if (message.getMessageDirection() == MessageDirection.SEND) {
                holder.sendFire.setVisibility(0);
                holder.receiverFire.setVisibility(8);
                holder.fireView.setBackgroundResource(R.drawable.rc_ic_bubble_no_right);
                drawable = v.getContext().getResources().getDrawable(R.drawable.rc_fire_sender_album);
                drawable.setBounds(0, 0, RongUtils.dip2px(40.0F), RongUtils.dip2px(34.0F));
                holder.clickHint.setCompoundDrawables((Drawable) null, drawable, (Drawable) null, (Drawable) null);
                holder.clickHint.setTextColor(Color.parseColor("#FFFFFF"));
            } else {
                holder.sendFire.setVisibility(8);
                holder.receiverFire.setVisibility(0);
                holder.fireView.setBackgroundResource(R.drawable.rc_ic_bubble_no_left);
                drawable = v.getContext().getResources().getDrawable(R.drawable.rc_fire_receiver_album);
                drawable.setBounds(0, 0, RongUtils.dip2px(40.0F), RongUtils.dip2px(34.0F));
                holder.clickHint.setCompoundDrawables((Drawable) null, drawable, (Drawable) null, (Drawable) null);
                holder.clickHint.setTextColor(Color.parseColor("#F4B50B"));
                DestructManager.getInstance().addListener(message.getUId(), new GIFMessageItemProvider.DestructListener(holder, message), "GIFMessageItemProvider");
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
            holder.receiverFire.setVisibility(8);
            holder.sendFire.setVisibility(8);
        }

        if (content.getLocalPath() != null) {
            if (content.isDestruct()) {
                holder.fireView.setVisibility(0);
                holder.img.setVisibility(8);
            } else {
                holder.fireView.setVisibility(8);
                holder.img.setVisibility(0);
                this.loadGif(v, content.getLocalUri(), holder);
            }
        } else {
            int size = v.getContext().getResources().getInteger(integer.rc_gifmsg_auto_download_size);
            if (content.getGifDataSize() <= (long) (size * 1024)) {
                if (this.checkPermission(v.getContext())) {
                    if (!message.getReceivedStatus().isDownload()) {
                        message.getReceivedStatus().setDownload();
                        this.downLoad(message.getMessage(), holder);
                    }
                } else if (progress != -1) {
                    holder.startDownLoad.setVisibility(0);
                    holder.length.setVisibility(0);
                    holder.length.setText(this.formatSize(content.getGifDataSize()));
                }
            } else if (progress > 0 && progress < 100) {
                holder.startDownLoad.setVisibility(8);
                holder.length.setVisibility(0);
                holder.length.setText(this.formatSize(content.getGifDataSize()));
            } else if (progress != -1) {
                holder.startDownLoad.setVisibility(0);
                holder.preProgress.setVisibility(8);
                holder.loadingProgress.setVisibility(8);
                holder.downLoadFailed.setVisibility(8);
                holder.length.setVisibility(0);
                holder.length.setText(this.formatSize(content.getGifDataSize()));
            }
        }

    }

    public Spannable getContentSummary(GIFMessage data) {
        return null;
    }

    public Spannable getContentSummary(Context context, GIFMessage data) {
        return data.isDestruct() ? new SpannableString(context.getString(string.rc_message_content_burn)) : new SpannableString(context.getString(string.rc_message_content_image));
    }

    private void downLoad(Message downloadMsg, GIFMessageItemProvider.ViewHolder holder) {
        holder.preProgress.setVisibility(0);
        RongIM.getInstance().downloadMediaMessage(downloadMsg, (IDownloadMediaMessageCallback) null);
    }

    private void loadGif(View v, Uri uri, GIFMessageItemProvider.ViewHolder holder) {
        ((RequestBuilder) Glide.with(v.getContext()).asGif().diskCacheStrategy(DiskCacheStrategy.RESOURCE)).load(uri.getPath()).listener(new RequestListener<GifDrawable>() {
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<GifDrawable> target, boolean isFirstResource) {
                return false;
            }

            public boolean onResourceReady(GifDrawable resource, Object model, Target<GifDrawable> target, DataSource dataSource, boolean isFirstResource) {
                return false;
            }
        }).into(holder.img);
    }

    private String formatSize(long length) {
        float size;
        if (length > 1048576L) {
            size = (float) Math.round((float) length / 1048576.0F * 100.0F) / 100.0F;
            return size + "M";
        } else if (length > 1024L) {
            size = (float) Math.round((float) length / 1024.0F * 100.0F) / 100.0F;
            return size + "KB";
        } else {
            return length + "B";
        }
    }

    private int[] getParamsValue(Context context, int width, int height) {
        int maxWidth = dip2px(context, 120.0F);
        int minValue = dip2px(context, 80.0F);
        float scale;
        int finalWidth;
        int finalHeight;
        if (width > maxWidth) {
            finalWidth = maxWidth;
            scale = (float) width / (float) maxWidth;
            finalHeight = Math.round((float) height / scale);
            if (finalHeight < minValue) {
                finalHeight = minValue;
            }
        } else if (width < minValue) {
            finalWidth = minValue;
            scale = (float) width / (float) minValue;
            finalHeight = Math.round((float) height * scale);
            if (finalHeight < minValue) {
                finalHeight = minValue;
            }
        } else {
            finalWidth = Math.round((float) height);
            finalHeight = Math.round((float) width);
        }

        int[] params = new int[]{finalWidth, finalHeight};
        return params;
    }

    public static int dip2px(Context context, float dpValue) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5F);
    }

    private boolean checkPermission(Context context) {
        String[] permission = new String[]{"android.permission.WRITE_EXTERNAL_STORAGE"};
        return PermissionCheckUtil.checkPermissions(context, permission);
    }

    private static class DestructListener implements DestructCountDownTimerListener {
        private WeakReference<GIFMessageItemProvider.ViewHolder> mHolder;
        private UIMessage mUIMessage;

        public DestructListener(GIFMessageItemProvider.ViewHolder pHolder, UIMessage pUIMessage) {
            this.mHolder = new WeakReference(pHolder);
            this.mUIMessage = pUIMessage;
        }

        public void onTick(long millisUntilFinished, String pMessageId) {
            if (this.mUIMessage.getUId().equals(pMessageId)) {
                GIFMessageItemProvider.ViewHolder viewHolder = (GIFMessageItemProvider.ViewHolder) this.mHolder.get();
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
                GIFMessageItemProvider.ViewHolder viewHolder = (GIFMessageItemProvider.ViewHolder) this.mHolder.get();
                if (viewHolder != null) {
                    viewHolder.receiverFireText.setVisibility(8);
                    viewHolder.receiverFireImg.setVisibility(0);
                    this.mUIMessage.setUnDestructTime((String) null);
                }
            }

        }
    }

    private static class ViewHolder {
        AsyncImageView img;
        ProgressBar preProgress;
        CircleProgressView loadingProgress;
        ImageView startDownLoad;
        ImageView downLoadFailed;
        TextView length;
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
//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package io.rong.imkit.widget.provider;

import android.content.Context;
import android.content.Intent;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import io.rong.imkit.R.id;
import io.rong.imkit.R.layout;
import io.rong.imkit.R.string;
import io.rong.imkit.model.ProviderTag;
import io.rong.imkit.model.UIMessage;
import io.rong.imkit.utils.FileTypeUtils;
import io.rong.imkit.widget.EllipsizeTextView;
import io.rong.imkit.widget.provider.IContainerItemProvider.MessageProvider;
import io.rong.imlib.model.Message.SentStatus;
import io.rong.message.FileMessage;

@ProviderTag(
        messageContent = FileMessage.class,
        showProgress = false,
        showReadState = true,
        showPortrait = false
)
public class FileMessageItemProvider extends MessageProvider<FileMessage> {
    private static final String TAG = "FileMessageItemProvider";

    public FileMessageItemProvider() {
    }

    public View newView(Context context, ViewGroup group) {
        View view = LayoutInflater.from(context).inflate(layout.rc_item_file_message, group, false);
        FileMessageItemProvider.ViewHolder holder = new FileMessageItemProvider.ViewHolder();
        holder.message = (RelativeLayout) view.findViewById(id.rc_message);
        holder.fileTypeImage = (ImageView) view.findViewById(id.rc_msg_iv_file_type_image);
        holder.fileName = (EllipsizeTextView) view.findViewById(id.rc_msg_tv_file_name);
        holder.fileSize = (TextView) view.findViewById(id.rc_msg_tv_file_size);
        holder.fileUploadProgress = (ProgressBar) view.findViewById(id.rc_msg_pb_file_upload_progress);
        view.setTag(holder);
        return view;
    }

    public void bindView(View v, int position, FileMessage content, final UIMessage message) {
        final FileMessageItemProvider.ViewHolder holder = (FileMessageItemProvider.ViewHolder) v.getTag();
        holder.fileName.setAdaptiveText(content.getName());
        long fileSizeBytes = content.getSize();
        holder.fileSize.setText(FileTypeUtils.formatFileSize(fileSizeBytes));
        holder.fileTypeImage.setImageResource(FileTypeUtils.fileTypeImageId(content.getName()));
        if (message.getSentStatus().equals(SentStatus.SENDING) && message.getProgress() < 100) {
            holder.fileUploadProgress.setVisibility(View.VISIBLE);
            holder.fileUploadProgress.setProgress(message.getProgress());
        } else {
            holder.fileUploadProgress.setVisibility(View.GONE);
        }

    }

    public Spannable getContentSummary(FileMessage data) {
        return null;
    }

    public Spannable getContentSummary(Context context, FileMessage data) {
        StringBuilder summaryPhrase = new StringBuilder();
        String fileName = data.getName();
        summaryPhrase.append(context.getString(string.rc_message_content_file)).append(" ").append(fileName);
        return new SpannableString(summaryPhrase);
    }

    public void onItemClick(View view, int position, FileMessage content, UIMessage message) {
        Intent intent = new Intent("io.rong.imkit.intent.action.openfile");
        intent.setPackage(view.getContext().getPackageName());
        intent.putExtra("FileMessage", content);
        intent.putExtra("Message", message.getMessage());
        intent.putExtra("Progress", message.getProgress());
        view.getContext().startActivity(intent);
    }

    private static class ViewHolder {
        RelativeLayout message;
        EllipsizeTextView fileName;
        TextView fileSize;
        ImageView fileTypeImage;
        ProgressBar fileUploadProgress;

        private ViewHolder() {
        }
    }
}
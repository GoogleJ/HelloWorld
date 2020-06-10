//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package io.rong.imkit.manager;

import android.net.Uri;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import io.rong.common.FileUtils;
import io.rong.common.RLog;
import io.rong.imkit.R.string;
import io.rong.imkit.RongContext;
import io.rong.imkit.RongIM;
import io.rong.imkit.RongIM.OnSendMessageListener;
import io.rong.imkit.model.Event.OnReceiveMessageProgressEvent;
import io.rong.imkit.utilities.KitStorageUtils;
import io.rong.imkit.utilities.RongUtils;
import io.rong.imkit.utilities.videocompressor.VideoCompress;
import io.rong.imkit.utilities.videocompressor.VideoCompress.CompressListener;
import io.rong.imlib.IRongCallback.ISendMediaMessageCallback;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.RongIMClient.ErrorCode;
import io.rong.imlib.RongIMClient.ResultCallback;
import io.rong.imlib.model.Conversation.ConversationType;
import io.rong.imlib.model.Message;
import io.rong.imlib.model.Message.SentStatus;
import io.rong.message.SightMessage;

public class SendMediaManager {
  private static final String TAG = SendMediaManager.class.getSimpleName();
  private ExecutorService executorService;
  private SendMediaManager.UploadController uploadController;

  public static SendMediaManager getInstance() {
    return SendMediaManager.SingletonHolder.sInstance;
  }

  private SendMediaManager() {
    this.executorService = this.getExecutorService();
    this.uploadController = new SendMediaManager.UploadController();
  }

  public void sendMedia(ConversationType conversationType, String targetId, List<Uri> mediaList, boolean isFull) {
    this.sendMedia(conversationType, targetId, mediaList, isFull, false, 0L);
  }

  public void sendMedia(ConversationType conversationType, String targetId, List<Uri> mediaList, boolean isFull, boolean isDestruct, long destructTime) {
    RLog.d(TAG, "The size of media is " + mediaList.size());
    Iterator var8 = mediaList.iterator();

    while(var8.hasNext()) {
      Uri mediaUri = (Uri)var8.next();
      if (!TextUtils.isEmpty(mediaUri.toString()) && FileUtils.isFileExistsWithUri(RongContext.getInstance().getApplicationContext(), mediaUri)) {
        int mediaDuration = RongUtils.getVideoDuration(RongContext.getInstance(), mediaUri.toString());
        SightMessage sightMessage = SightMessage.obtain(mediaUri, mediaDuration / 1000);
        if (isDestruct) {
          sightMessage.setDestructTime(destructTime);
        }

        OnSendMessageListener listener = RongContext.getInstance().getOnSendMessageListener();
        if (listener != null) {
          Message message = listener.onSend(Message.obtain(targetId, conversationType, sightMessage));
          if (message != null) {
            RongIMClient.getInstance().insertMessage(conversationType, targetId, (String)null, message.getContent(), new ResultCallback<Message>() {
              public void onSuccess(Message message) {
                message.setSentStatus(SentStatus.SENDING);
                RongIMClient.getInstance().setMessageSentStatus(message.getMessageId(), SentStatus.SENDING, (ResultCallback)null);
                RongContext.getInstance().getEventBus().post(message);
                SendMediaManager.this.uploadController.execute(message);
              }

              public void onError(ErrorCode e) {
              }
            });
          }
        } else {
          RongIMClient.getInstance().insertMessage(conversationType, targetId, (String)null, sightMessage, new ResultCallback<Message>() {
            public void onSuccess(Message message) {
              message.setSentStatus(SentStatus.SENDING);
              RongIMClient.getInstance().setMessageSentStatus(message.getMessageId(), SentStatus.SENDING, (ResultCallback)null);
              RongContext.getInstance().getEventBus().post(message);
              SendMediaManager.this.uploadController.execute(message);
            }

            public void onError(ErrorCode e) {
            }
          });
        }
      }
    }

  }

  public void cancelSendingMedia(ConversationType conversationType, String targetId) {
    RLog.d(TAG, "cancel Sending media");
    if (conversationType != null && targetId != null && this.uploadController != null) {
      this.uploadController.cancel(conversationType, targetId);
    }

  }

  public void cancelSendingMedia(ConversationType conversationType, String targetId, int messageId) {
    RLog.d(TAG, "cancel Sending media");
    if (conversationType != null && targetId != null && this.uploadController != null && messageId > 0) {
      this.uploadController.cancel(conversationType, targetId, messageId);
    }

  }

  public void reset() {
    this.uploadController.reset();
  }

  private ExecutorService getExecutorService() {
    if (this.executorService == null) {
      this.executorService = new ThreadPoolExecutor(1, 2147483647, 60L, TimeUnit.SECONDS, new SynchronousQueue(), this.threadFactory("Rong SendMediaManager", false));
    }

    return this.executorService;
  }

  private ThreadFactory threadFactory(final String name, final boolean daemon) {
    return new ThreadFactory() {
      public Thread newThread(@Nullable Runnable runnable) {
        Thread result = new Thread(runnable, name);
        result.setDaemon(daemon);
        return result;
      }
    };
  }

  private class UploadController implements Runnable {
    final List<Message> pendingMessages = new ArrayList();
    Message executingMessage;

    public UploadController() {
    }

    public void execute(Message message) {
      synchronized(this.pendingMessages) {
        this.pendingMessages.add(message);
        if (this.executingMessage == null) {
          this.executingMessage = (Message)this.pendingMessages.remove(0);
          SendMediaManager.this.executorService.submit(this);
        }

      }
    }

    public void reset() {
      RLog.w(SendMediaManager.TAG, "Reset Sending media.");
      synchronized(this.pendingMessages) {
        Iterator var2 = this.pendingMessages.iterator();

        while(true) {
          if (!var2.hasNext()) {
            this.pendingMessages.clear();
            break;
          }

          Message message = (Message)var2.next();
          message.setSentStatus(SentStatus.FAILED);
          RongContext.getInstance().getEventBus().post(message);
        }
      }

      if (this.executingMessage != null) {
        this.executingMessage.setSentStatus(SentStatus.FAILED);
        RongContext.getInstance().getEventBus().post(this.executingMessage);
        this.executingMessage = null;
      }

    }

    public void cancel(ConversationType conversationType, String targetId) {
      synchronized(this.pendingMessages) {
        int count = this.pendingMessages.size();

        for(int i = 0; i < count; ++i) {
          Message msg = (Message)this.pendingMessages.get(i);
          if (msg.getConversationType().equals(conversationType) && msg.getTargetId().equals(targetId)) {
            this.pendingMessages.remove(msg);
          }
        }

        if (this.pendingMessages.size() == 0) {
          this.executingMessage = null;
        }

      }
    }

    public void cancel(ConversationType conversationType, String targetId, int messageId) {
      synchronized(this.pendingMessages) {
        int count = this.pendingMessages.size();

        for(int i = 0; i < count; ++i) {
          Message msg = (Message)this.pendingMessages.get(i);
          if (msg.getConversationType().equals(conversationType) && msg.getTargetId().equals(targetId) && msg.getMessageId() == messageId) {
            this.pendingMessages.remove(msg);
            break;
          }
        }

        if (this.pendingMessages.size() == 0) {
          this.executingMessage = null;
        }

      }
    }

    private void polling() {
      synchronized(this.pendingMessages) {
        RLog.d(SendMediaManager.TAG, "polling " + this.pendingMessages.size());
        if (this.pendingMessages.size() > 0) {
          this.executingMessage = (Message)this.pendingMessages.remove(0);
          SendMediaManager.this.executorService.submit(this);
        } else {
          this.pendingMessages.clear();
          this.executingMessage = null;
        }

      }
    }

    public void run() {
      final OnReceiveMessageProgressEvent result = new OnReceiveMessageProgressEvent();
      result.setMessage(this.executingMessage);
      String originLocalPath = ((SightMessage)this.executingMessage.getContent()).getLocalPath().toString().substring(7);
      final String compressPath = KitStorageUtils.getImageSavePath(RongContext.getInstance()) + File.separator + "VID_" + (new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.CHINA)).format(new Date()) + ".mp4";
      VideoCompress.compressVideo(originLocalPath, compressPath, new CompressListener() {
        public void onStart() {
          RongContext.getInstance().getEventBus().post(result);
          RLog.d(SendMediaManager.TAG, "Compressing video file starts.");
        }

        public void onSuccess() {
          RLog.d(SendMediaManager.TAG, "Compressing video file successes.");
          if (UploadController.this.executingMessage != null) {
            ((SightMessage)UploadController.this.executingMessage.getContent()).setLocalPath(Uri.parse("file://" + compressPath));
            boolean isDestruct = false;
            if (UploadController.this.executingMessage.getContent() != null) {
              isDestruct = UploadController.this.executingMessage.getContent().isDestruct();
            }

            String filePath = Uri.parse("file://" + compressPath).toString().substring(7);
            File file = new File(filePath);
            ((SightMessage)UploadController.this.executingMessage.getContent()).setSize(file.length());
            RongIM.getInstance().sendMediaMessage(UploadController.this.executingMessage, isDestruct ? RongContext.getInstance().getString(string.rc_message_content_burn) : null, (String)null, new ISendMediaMessageCallback() {
              public void onAttached(Message message) {
              }

              public void onSuccess(Message message) {
                UploadController.this.polling();
              }

              public void onError(Message message, ErrorCode errorCode) {
                UploadController.this.polling();
              }

              public void onProgress(Message message, int progress) {
              }

              public void onCanceled(Message message) {
              }
            });
            RLog.d(SendMediaManager.TAG, "Compressing video file successes.");
          }
        }

        public void onFail() {
          Toast.makeText(RongContext.getInstance(), RongContext.getInstance().getString(string.rc_picsel_video_corrupted), 0).show();
          UploadController.this.polling();
          RLog.d(SendMediaManager.TAG, "Compressing video file failed.");
        }

        public void onProgress(float percent) {
          RLog.d(SendMediaManager.TAG, "The progress of compressing video file is " + percent);
        }
      });
    }
  }

  static class SingletonHolder {
    static SendMediaManager sInstance = new SendMediaManager();

    SingletonHolder() {
    }
  }
}

//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package io.rong.imkit.activity;

import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import io.rong.common.FileUtils;
import io.rong.common.RLog;
import io.rong.eventbus.EventBus;
import io.rong.imageloader.core.DisplayImageOptions;
import io.rong.imageloader.core.ImageLoader;
import io.rong.imageloader.core.assist.FailReason;
import io.rong.imageloader.core.assist.ImageScaleType;
import io.rong.imageloader.core.assist.ImageSize;
import io.rong.imageloader.core.listener.ImageLoadingListener;
import io.rong.imageloader.core.listener.ImageLoadingProgressListener;
import io.rong.imkit.R.id;
import io.rong.imkit.R.layout;
import io.rong.imkit.R.string;
import io.rong.imkit.RongBaseNoActionbarActivity;
import io.rong.imkit.destruct.DestructManager;
import io.rong.imkit.model.Event.MessageDeleteEvent;
import io.rong.imkit.model.Event.RemoteMessageRecallEvent;
import io.rong.imkit.model.Event.changeDestructionReadTimeEvent;
import io.rong.imkit.plugin.image.HackyViewPager;
import io.rong.imkit.utilities.KitStorageUtils;
import io.rong.imkit.utilities.OptionsPopupDialog;
import io.rong.imkit.utilities.OptionsPopupDialog.OnOptionsItemClickedListener;
import io.rong.imkit.utilities.PermissionCheckUtil;
import io.rong.imkit.utils.ImageDownloadManager;
import io.rong.imkit.utils.ImageDownloadManager.DownloadStatusError;
import io.rong.imkit.utils.ImageDownloadManager.DownloadStatusListener;
import io.rong.imlib.RongCommonDefine.GetMessageDirection;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.RongIMClient.DestructCountDownTimerListener;
import io.rong.imlib.RongIMClient.ErrorCode;
import io.rong.imlib.RongIMClient.ResultCallback;
import io.rong.imlib.model.Conversation.ConversationType;
import io.rong.imlib.model.Message;
import io.rong.imlib.model.Message.MessageDirection;
import io.rong.message.ImageMessage;
import io.rong.subscaleview.ImageSource;
import io.rong.subscaleview.SubsamplingScaleImageView;

public class PicturePagerActivity extends RongBaseNoActionbarActivity implements OnLongClickListener {
    private static final String TAG = "PicturePagerActivity";
    private static final int IMAGE_MESSAGE_COUNT = 10;
    private HackyViewPager mViewPager;
    private ImageMessage mCurrentImageMessage;
    private Message mMessage;
    private ConversationType mConversationType;
    private int mCurrentMessageId;
    private String mTargetId = null;
    private int mCurrentIndex = 0;
    private PicturePagerActivity.ImageAdapter mImageAdapter;
    private boolean isFirstTime = false;
    private ViewPager.OnPageChangeListener mPageChangeListener = new ViewPager.OnPageChangeListener() {
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        }

        public void onPageSelected(int position) {
            RLog.i("PicturePagerActivity", "onPageSelected. position:" + position);
            PicturePagerActivity.this.mCurrentIndex = position;
            View view = PicturePagerActivity.this.mViewPager.findViewById(position);
            if (view != null) {
                PicturePagerActivity.this.mImageAdapter.updatePhotoView(position, view);
            }

            if (position == PicturePagerActivity.this.mImageAdapter.getCount() - 1) {
                PicturePagerActivity.this.getConversationImageUris(PicturePagerActivity.this.mImageAdapter.getItem(position).getMessageId().getMessageId(), GetMessageDirection.BEHIND);
            } else if (position == 0) {
                PicturePagerActivity.this.getConversationImageUris(PicturePagerActivity.this.mImageAdapter.getItem(position).getMessageId().getMessageId(), GetMessageDirection.FRONT);
            }

        }

        public void onPageScrollStateChanged(int state) {
        }
    };

    public PicturePagerActivity() {
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(layout.rc_fr_photo);
        Message currentMessage = (Message) this.getIntent().getParcelableExtra("message");
        this.mMessage = currentMessage;
        this.mCurrentImageMessage = (ImageMessage) currentMessage.getContent();
        this.mConversationType = currentMessage.getConversationType();
        this.mCurrentMessageId = currentMessage.getMessageId();
        this.mTargetId = currentMessage.getTargetId();
        this.mViewPager = (HackyViewPager) this.findViewById(id.viewpager);
        this.mViewPager.setOnPageChangeListener(this.mPageChangeListener);
        this.mImageAdapter = new PicturePagerActivity.ImageAdapter();
        this.isFirstTime = true;
        if (!this.mMessage.getContent().isDestruct()) {
            this.getConversationImageUris(this.mCurrentMessageId, GetMessageDirection.FRONT);
            this.getConversationImageUris(this.mCurrentMessageId, GetMessageDirection.BEHIND);
        } else {
            ArrayList<PicturePagerActivity.ImageInfo> lists = new ArrayList();
            lists.add(new PicturePagerActivity.ImageInfo(this.mMessage, this.mCurrentImageMessage.getThumUri(), this.mCurrentImageMessage.getLocalUri() == null ? this.mCurrentImageMessage.getRemoteUri() : this.mCurrentImageMessage.getLocalUri()));
            this.mImageAdapter.addData(lists, true);
            this.mViewPager.setAdapter(this.mImageAdapter);
        }

        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }

    }

    public void onEventMainThread(RemoteMessageRecallEvent event) {
        if (this.mCurrentMessageId == event.getMessageId()) {
            (new Builder(this, 5)).setMessage(this.getString(string.rc_recall_success)).setPositiveButton(this.getString(string.rc_dialog_ok), new OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    PicturePagerActivity.this.finish();
                }
            }).setCancelable(false).show();
        } else {
            this.mImageAdapter.removeRecallItem(event.getMessageId());
            this.mImageAdapter.notifyDataSetChanged();
            if (this.mImageAdapter.getCount() == 0) {
                this.finish();
            }
        }

    }

    public void onEventMainThread(MessageDeleteEvent deleteEvent) {
        RLog.d("PicturePagerActivity", "MessageDeleteEvent");
        if (deleteEvent.getMessageIds() != null) {
            Iterator var2 = deleteEvent.getMessageIds().iterator();

            while (var2.hasNext()) {
                int messageId = (Integer) var2.next();
                this.mImageAdapter.removeRecallItem(messageId);
            }

            this.mImageAdapter.notifyDataSetChanged();
            if (this.mImageAdapter.getCount() == 0) {
                this.finish();
            }
        }

    }

    private void getConversationImageUris(int mesageId, final GetMessageDirection direction) {
        if (this.mConversationType != null && !TextUtils.isEmpty(this.mTargetId)) {
            RongIMClient.getInstance().getHistoryMessages(this.mConversationType, this.mTargetId, "RC:ImgMsg", mesageId, 10, direction, new ResultCallback<List<Message>>() {
                public void onSuccess(List<Message> messages) {
                    ArrayList<PicturePagerActivity.ImageInfo> lists = new ArrayList();
                    if (messages != null) {
                        if (direction.equals(GetMessageDirection.FRONT)) {
                            Collections.reverse(messages);
                        }

                        for (int i = 0; i < messages.size(); ++i) {
                            Message message = (Message) messages.get(i);
                            if (message.getContent() instanceof ImageMessage && !message.getContent().isDestruct()) {
                                ImageMessage imageMessage = (ImageMessage) message.getContent();
                                Uri largeImageUri = imageMessage.getLocalUri() == null ? imageMessage.getRemoteUri() : imageMessage.getLocalUri();
                                if (imageMessage.getThumUri() != null && largeImageUri != null) {
                                    lists.add(PicturePagerActivity.this.new ImageInfo(message, imageMessage.getThumUri(), largeImageUri));
                                }
                            }
                        }
                    }

                    if (direction.equals(GetMessageDirection.FRONT) && PicturePagerActivity.this.isFirstTime) {
                        lists.add(PicturePagerActivity.this.new ImageInfo(PicturePagerActivity.this.mMessage, PicturePagerActivity.this.mCurrentImageMessage.getThumUri(), PicturePagerActivity.this.mCurrentImageMessage.getLocalUri() == null ? PicturePagerActivity.this.mCurrentImageMessage.getRemoteUri() : PicturePagerActivity.this.mCurrentImageMessage.getLocalUri()));
                        PicturePagerActivity.this.mImageAdapter.addData(lists, direction.equals(GetMessageDirection.FRONT));
                        PicturePagerActivity.this.mViewPager.setAdapter(PicturePagerActivity.this.mImageAdapter);
                        PicturePagerActivity.this.isFirstTime = false;
                        PicturePagerActivity.this.mViewPager.setCurrentItem(lists.size() - 1);
                        PicturePagerActivity.this.mCurrentIndex = lists.size() - 1;
                    } else if (lists.size() > 0) {
                        PicturePagerActivity.this.mImageAdapter.addData(lists, direction.equals(GetMessageDirection.FRONT));
                        PicturePagerActivity.this.mImageAdapter.notifyDataSetChanged();
                        if (direction.equals(GetMessageDirection.FRONT)) {
                            PicturePagerActivity.this.mViewPager.setCurrentItem(lists.size());
                            PicturePagerActivity.this.mCurrentIndex = lists.size();
                        }
                    }

                }

                public void onError(ErrorCode e) {
                }
            });
        }

    }

    protected void onPause() {
        super.onPause();
    }

    protected void onDestroy() {
        ImageLoader.getInstance().clearMemoryCache();
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    public boolean onPictureLongClick(View v, Uri thumbUri, Uri largeImageUri) {
        return false;
    }

    public boolean onLongClick(View v) {
        if (this.mCurrentImageMessage.isDestruct()) {
            return false;
        } else {
            PicturePagerActivity.ImageInfo imageInfo = this.mImageAdapter.getImageInfo(this.mCurrentIndex);
            if (imageInfo != null) {
                Uri thumbUri = imageInfo.getThumbUri();
                Uri largeImageUri = imageInfo.getLargeImageUri();
                if (this.onPictureLongClick(v, thumbUri, largeImageUri)) {
                    return true;
                }

                if (largeImageUri == null) {
                    return false;
                }

                final File file;
                if (!largeImageUri.getScheme().startsWith("http") && !largeImageUri.getScheme().startsWith("https")) {
                    file = new File(largeImageUri.getPath());
                } else {
                    file = ImageLoader.getInstance().getDiskCache().get(largeImageUri.toString());
                }

                if (file == null || !file.exists()) {
                    return false;
                }

                String[] items = new String[]{this.getString(string.rc_save_picture)};
                OptionsPopupDialog.newInstance(this, items).setOptionsPopupDialogListener(new OnOptionsItemClickedListener() {
                    public void onOptionsItemClicked(int which) {
                        if (which == 0) {
                            String[] permissions = new String[]{"android.permission.READ_EXTERNAL_STORAGE", "android.permission.WRITE_EXTERNAL_STORAGE"};
                            if (!PermissionCheckUtil.requestPermissions(PicturePagerActivity.this, permissions)) {
                                return;
                            }

                            if (file != null && file.exists()) {
                                String name = "rong_" + System.currentTimeMillis();
                                KitStorageUtils.saveMediaToPublicDir(PicturePagerActivity.this, file, "image");
                                Toast.makeText(PicturePagerActivity.this, PicturePagerActivity.this.getString(string.rc_save_picture_at), 0).show();
                            } else {
                                Toast.makeText(PicturePagerActivity.this, PicturePagerActivity.this.getString(string.rc_src_file_not_found), 0).show();
                            }
                        }

                    }
                }).show();
            }

            return true;
        }
    }

    private static class DestructListener implements DestructCountDownTimerListener {
        private WeakReference<PicturePagerActivity.ImageAdapter.ViewHolder> mHolder;
        private String mMessageId;

        public DestructListener(PicturePagerActivity.ImageAdapter.ViewHolder pHolder, String pMessageId) {
            this.mHolder = new WeakReference(pHolder);
            this.mMessageId = pMessageId;
        }

        public void onTick(long millisUntilFinished, String pMessageId) {
            if (this.mMessageId.equals(pMessageId)) {
                PicturePagerActivity.ImageAdapter.ViewHolder viewHolder = (PicturePagerActivity.ImageAdapter.ViewHolder) this.mHolder.get();
                if (viewHolder != null) {
                    viewHolder.mCountDownView.setVisibility(0);
                    viewHolder.mCountDownView.setText(String.valueOf(Math.max(millisUntilFinished, 1L)));
                }
            }

        }

        public void onStop(String messageId) {
            if (this.mMessageId.equals(messageId)) {
                PicturePagerActivity.ImageAdapter.ViewHolder viewHolder = (PicturePagerActivity.ImageAdapter.ViewHolder) this.mHolder.get();
                if (viewHolder != null) {
                    viewHolder.mCountDownView.setVisibility(8);
                }
            }

        }
    }

    private class ImageInfo {
        private Message message;
        private Uri thumbUri;
        private Uri largeImageUri;

        ImageInfo(Message message, Uri thumbnail, Uri largeImageUri) {
            this.message = message;
            this.thumbUri = thumbnail;
            this.largeImageUri = largeImageUri;
        }

        public Message getMessageId() {
            return this.message;
        }

        public Uri getLargeImageUri() {
            return this.largeImageUri;
        }

        public Uri getThumbUri() {
            return this.thumbUri;
        }
    }

    private class ImageAdapter extends PagerAdapter {
        private ArrayList<PicturePagerActivity.ImageInfo> mImageList;

        private ImageAdapter() {
            this.mImageList = new ArrayList();
        }

        private View newView(Context context, PicturePagerActivity.ImageInfo imageInfo) {
            View result = LayoutInflater.from(context).inflate(layout.rc_fr_image, (ViewGroup) null);
            PicturePagerActivity.ImageAdapter.ViewHolder holder = new PicturePagerActivity.ImageAdapter.ViewHolder();
            holder.progressBar = (ProgressBar) result.findViewById(id.rc_progress);
            holder.progressText = (TextView) result.findViewById(id.rc_txt);
            holder.photoView = (SubsamplingScaleImageView) result.findViewById(id.rc_photoView);
            holder.mCountDownView = (TextView) result.findViewById(id.rc_count_down);
            holder.photoView.setOnLongClickListener(PicturePagerActivity.this);
            holder.photoView.setOnClickListener(new android.view.View.OnClickListener() {
                public void onClick(View v) {
                    Window window = PicturePagerActivity.this.getWindow();
                    if (window != null) {
                        window.setFlags(2048, 2048);
                    }

                    PicturePagerActivity.this.finish();
                }
            });
            result.setTag(holder);
            return result;
        }

        public void addData(ArrayList<PicturePagerActivity.ImageInfo> newImages, boolean direction) {
            if (newImages != null && newImages.size() != 0) {
                if (this.mImageList.size() == 0) {
                    this.mImageList.addAll(newImages);
                } else if (direction && !PicturePagerActivity.this.isFirstTime && !this.isDuplicate(((PicturePagerActivity.ImageInfo) newImages.get(0)).getMessageId().getMessageId())) {
                    ArrayList<PicturePagerActivity.ImageInfo> temp = new ArrayList();
                    temp.addAll(this.mImageList);
                    this.mImageList.clear();
                    this.mImageList.addAll(newImages);
                    this.mImageList.addAll(this.mImageList.size(), temp);
                } else if (!PicturePagerActivity.this.isFirstTime && !this.isDuplicate(((PicturePagerActivity.ImageInfo) newImages.get(0)).getMessageId().getMessageId())) {
                    this.mImageList.addAll(this.mImageList.size(), newImages);
                }

            }
        }

        private boolean isDuplicate(int messageId) {
            Iterator var2 = this.mImageList.iterator();

            PicturePagerActivity.ImageInfo info;
            do {
                if (!var2.hasNext()) {
                    return false;
                }

                info = (PicturePagerActivity.ImageInfo) var2.next();
            } while (info.getMessageId().getMessageId() != messageId);

            return true;
        }

        public PicturePagerActivity.ImageInfo getItem(int index) {
            return (PicturePagerActivity.ImageInfo) this.mImageList.get(index);
        }

        public int getItemPosition(Object object) {
            return -2;
        }

        public int getCount() {
            return this.mImageList.size();
        }

        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        public Object instantiateItem(ViewGroup container, int position) {
            RLog.i("PicturePagerActivity", "instantiateItem.position:" + position);
            View imageView = this.newView(container.getContext(), (PicturePagerActivity.ImageInfo) this.mImageList.get(position));
            this.updatePhotoView(position, imageView);
            imageView.setId(position);
            container.addView(imageView);
            return imageView;
        }

        public void destroyItem(ViewGroup container, int position, Object object) {
            RLog.i("PicturePagerActivity", "destroyItem.position:" + position);
            container.removeView((View) object);
        }

        private void removeRecallItem(int messageId) {
            for (int i = this.mImageList.size() - 1; i >= 0; --i) {
                if (((PicturePagerActivity.ImageInfo) this.mImageList.get(i)).message.getMessageId() == messageId) {
                    this.mImageList.remove(i);
                    break;
                }
            }

        }

        private void updatePhotoView(final int position, View view) {
            final PicturePagerActivity.ImageAdapter.ViewHolder holder = (PicturePagerActivity.ImageAdapter.ViewHolder) view.getTag();
            Uri originalUri = ((PicturePagerActivity.ImageInfo) this.mImageList.get(position)).getLargeImageUri();
            final Uri thumbUri = ((PicturePagerActivity.ImageInfo) this.mImageList.get(position)).getThumbUri();
            if (originalUri != null && thumbUri != null) {
                if (PicturePagerActivity.this.mCurrentImageMessage.isDestruct() && PicturePagerActivity.this.mMessage.getMessageDirection().equals(MessageDirection.RECEIVE)) {
                    DestructManager.getInstance().addListener(PicturePagerActivity.this.mMessage.getUId(), new PicturePagerActivity.DestructListener(holder, PicturePagerActivity.this.mMessage.getUId()), "PicturePagerActivity");
                }

                File file = ImageLoader.getInstance().getDiskCache().get(originalUri.toString());
                if (file != null && file.exists()) {
                    Uri resultUri = Uri.fromFile(file);
                    String path = "";
                    if (!resultUri.equals(holder.photoView.getUri())) {
                        if (resultUri.getScheme().equals("file")) {
                            path = resultUri.toString().substring(5);
                        } else if (resultUri.getScheme().equals("content")) {
                            Cursor cursor = PicturePagerActivity.this.getApplicationContext().getContentResolver().query(resultUri, new String[]{"_data"}, (String) null, (String[]) null, (String) null);
                            cursor.moveToFirst();
                            path = cursor.getString(0);
                            cursor.close();
                        }

                        holder.photoView.setOrientation(FileUtils.readPictureDegree(PicturePagerActivity.this, path));
                        Bitmap bitmap = BitmapFactory.decodeFile(path);
                        holder.photoView.setBitmapAndFileUri(bitmap, resultUri);
                    }

                } else {
                    DisplayImageOptions options = (new io.rong.imageloader.core.DisplayImageOptions.Builder()).cacheInMemory(true).cacheOnDisk(true).imageScaleType(ImageScaleType.NONE).bitmapConfig(Config.RGB_565).considerExifParams(true).build();
                    ImageLoader.getInstance().loadImage(originalUri.toString(), (ImageSize) null, options, new ImageLoadingListener() {
                        public void onLoadingStarted(String imageUri, View view) {
                            String thumbPath = null;
                            Bitmap thumbBitmap = null;
                            if ("file".equals(thumbUri.getScheme())) {
                                thumbPath = thumbUri.toString().substring(5);
                            }

                            if (thumbPath != null) {
                                thumbBitmap = BitmapFactory.decodeFile(thumbPath);
                            }

                            holder.photoView.setBitmapAndFileUri(thumbBitmap, (Uri) null);
                            holder.progressText.setVisibility(0);
                            holder.progressBar.setVisibility(0);
                            holder.progressText.setText("0%");
                        }

                        public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                            if (imageUri.startsWith("file://")) {
                                holder.progressText.setVisibility(8);
                                holder.progressBar.setVisibility(8);
                            } else {
                                String[] permissions = new String[]{"android.permission.WRITE_EXTERNAL_STORAGE"};
                                if (!PermissionCheckUtil.requestPermissions(PicturePagerActivity.this, permissions)) {
                                    holder.progressText.setVisibility(8);
                                    holder.progressBar.setVisibility(8);
                                    return;
                                }

                                ImageDownloadManager.getInstance().downloadImage(imageUri, new DownloadStatusListener() {
                                    public void downloadSuccess(String localPath, Bitmap bitmap) {
                                        holder.photoView.setImage(ImageSource.uri(localPath));
                                        holder.progressText.setVisibility(8);
                                        holder.progressBar.setVisibility(8);
                                    }

                                    public void downloadFailed(DownloadStatusError error) {
                                        holder.progressText.setVisibility(8);
                                        holder.progressBar.setVisibility(8);
                                    }
                                });
                            }

                        }

                        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                            if (PicturePagerActivity.this.mCurrentImageMessage.isDestruct() && PicturePagerActivity.this.mMessage.getMessageDirection().equals(MessageDirection.RECEIVE)) {
                                DestructManager.getInstance().startDestruct(PicturePagerActivity.this.mMessage);
                                EventBus.getDefault().post(new changeDestructionReadTimeEvent(PicturePagerActivity.this.mMessage));
                            }

                            holder.progressText.setVisibility(8);
                            holder.progressBar.setVisibility(8);
                            File file = ImageLoader.getInstance().getDiskCache().get(imageUri);
                            Uri resultUri = null;
                            if (file != null) {
                                resultUri = Uri.fromFile(file);
                            }

                            holder.photoView.setBitmapAndFileUri(loadedImage, resultUri);
                            View inPagerView = PicturePagerActivity.this.mViewPager.findViewById(position);
                            if (inPagerView != null) {
                                PicturePagerActivity.ImageAdapter.ViewHolder inPagerHolder = (PicturePagerActivity.ImageAdapter.ViewHolder) inPagerView.getTag();
                                if (inPagerHolder != holder) {
                                    inPagerHolder.progressText.setVisibility(8);
                                    inPagerHolder.progressBar.setVisibility(8);
                                    PicturePagerActivity.this.mImageAdapter.updatePhotoView(position, inPagerView);
                                }
                            }

                        }

                        public void onLoadingCancelled(String imageUri, View view) {
                            holder.progressText.setVisibility(8);
                            holder.progressText.setVisibility(8);
                        }
                    }, new ImageLoadingProgressListener() {
                        public void onProgressUpdate(String imageUri, View view, int current, int total) {
                            holder.progressText.setText(current * 100 / total + "%");
                            if (current == total) {
                                holder.progressText.setVisibility(8);
                                holder.progressBar.setVisibility(8);
                            } else {
                                holder.progressText.setVisibility(0);
                                holder.progressBar.setVisibility(0);
                            }

                        }
                    });
                }
            } else {
                RLog.e("PicturePagerActivity", "large uri and thumbnail uri of the image should not be null.");
            }
        }

        public PicturePagerActivity.ImageInfo getImageInfo(int position) {
            return (PicturePagerActivity.ImageInfo) this.mImageList.get(position);
        }

        public class ViewHolder {
            ProgressBar progressBar;
            TextView progressText;
            SubsamplingScaleImageView photoView;
            TextView mCountDownView;

            public ViewHolder() {
            }
        }
    }
}

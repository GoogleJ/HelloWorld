//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package io.rong.imkit.tools;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import io.rong.common.RLog;
import io.rong.imageloader.core.DisplayImageOptions;
import io.rong.imageloader.core.DisplayImageOptions.Builder;
import io.rong.imageloader.core.ImageLoader;
import io.rong.imageloader.core.assist.FailReason;
import io.rong.imageloader.core.imageaware.ImageAware;
import io.rong.imageloader.core.imageaware.ImageViewAware;
import io.rong.imageloader.core.listener.ImageLoadingListener;
import io.rong.imageloader.core.listener.ImageLoadingProgressListener;
import io.rong.imkit.R.id;
import io.rong.imkit.R.layout;
import io.rong.imkit.fragment.BaseFragment;
import io.rong.imkit.plugin.image.AlbumBitmapCacheHelper;
import io.rong.imkit.plugin.image.AlbumBitmapCacheHelper.ILoadImageCallback;
import io.rong.imkit.plugin.image.HackyViewPager;
import io.rong.imkit.widget.PicturePopupWindow;
import io.rong.imlib.RongCommonDefine.GetMessageDirection;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.RongIMClient.ErrorCode;
import io.rong.imlib.RongIMClient.ResultCallback;
import io.rong.imlib.model.Conversation.ConversationType;
import io.rong.imlib.model.Message;
import io.rong.message.ImageMessage;
import io.rong.photoview.PhotoView;
import io.rong.photoview.PhotoViewAttacher.OnPhotoTapListener;

public class PhotoFragment extends BaseFragment {
    private static final String TAG = "PhotoFragment";
    private static final int IMAGE_MESSAGE_COUNT = 10;
    private HackyViewPager mViewPager;
    private ImageMessage mCurrentImageMessage;
    private ConversationType mConversationType;
    private int mCurrentMessageId;
    private String mTargetId = null;
    private int mCurrentIndex = 0;
    private PhotoFragment.PhotoDownloadListener mDownloadListener;
    private ImageAware mDownloadingImageAware;
    private PhotoFragment.ImageAdapter mImageAdapter;
    private boolean isFirstTime = false;
    private ViewPager.OnPageChangeListener mPageChangeListener = new ViewPager.OnPageChangeListener() {
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        }

        public void onPageSelected(int position) {
            RLog.i("PhotoFragment", "onPageSelected. position:" + position);
            PhotoFragment.this.mCurrentIndex = position;
            View view = PhotoFragment.this.mViewPager.findViewById(position);
            if (view != null) {
                PhotoFragment.this.mImageAdapter.updatePhotoView(position, view, PhotoFragment.this.mDownloadListener);
            }

            if (position == PhotoFragment.this.mImageAdapter.getCount() - 1) {
                PhotoFragment.this.getConversationImageUris(PhotoFragment.this.mImageAdapter.getItem(position).getMessageId(), GetMessageDirection.BEHIND);
            } else if (position == 0) {
                PhotoFragment.this.getConversationImageUris(PhotoFragment.this.mImageAdapter.getItem(position).getMessageId(), GetMessageDirection.FRONT);
            }

        }

        public void onPageScrollStateChanged(int state) {
        }
    };

    public PhotoFragment() {
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(layout.rc_fr_photo, container, true);
        this.mViewPager = (HackyViewPager) view.findViewById(id.viewpager);
        return view;
    }

    public void initPhoto(Message currentMessage, PhotoFragment.PhotoDownloadListener downloadListener) {
        if (currentMessage != null) {
            this.mCurrentImageMessage = (ImageMessage) currentMessage.getContent();
            this.mConversationType = currentMessage.getConversationType();
            this.mCurrentMessageId = currentMessage.getMessageId();
            this.mTargetId = currentMessage.getTargetId();
            this.mDownloadListener = downloadListener;
            if (this.mCurrentMessageId < 0) {
                RLog.e("PhotoFragment", "The value of messageId is wrong!");
            } else {
                this.mImageAdapter = new PhotoFragment.ImageAdapter();
                this.isFirstTime = true;
                this.mViewPager.setOnPageChangeListener(this.mPageChangeListener);
                this.getConversationImageUris(this.mCurrentMessageId, GetMessageDirection.FRONT);
                this.getConversationImageUris(this.mCurrentMessageId, GetMessageDirection.BEHIND);
            }
        }
    }

    private void getConversationImageUris(int mesageId, final GetMessageDirection direction) {
        if (this.mConversationType != null && !TextUtils.isEmpty(this.mTargetId)) {
            RongIMClient.getInstance().getHistoryMessages(this.mConversationType, this.mTargetId, "RC:ImgMsg", mesageId, 10, direction, new ResultCallback<List<Message>>() {
                public void onSuccess(List<Message> messages) {
                    ArrayList<PhotoFragment.ImageInfo> lists = new ArrayList();
                    if (messages != null) {
                        if (direction.equals(GetMessageDirection.FRONT)) {
                            Collections.reverse(messages);
                        }

                        for (int i = 0; i < messages.size(); ++i) {
                            Message message = (Message) messages.get(i);
                            if (message.getContent() instanceof ImageMessage) {
                                ImageMessage imageMessage = (ImageMessage) message.getContent();
                                Uri largeImageUri = imageMessage.getLocalUri() == null ? imageMessage.getRemoteUri() : imageMessage.getLocalUri();
                                if (imageMessage.getThumUri() != null && largeImageUri != null) {
                                    lists.add(PhotoFragment.this.new ImageInfo(message.getMessageId(), imageMessage.getThumUri(), largeImageUri));
                                }
                            }
                        }
                    }

                    if (direction.equals(GetMessageDirection.FRONT) && PhotoFragment.this.isFirstTime) {
                        lists.add(PhotoFragment.this.new ImageInfo(PhotoFragment.this.mCurrentMessageId, PhotoFragment.this.mCurrentImageMessage.getThumUri(), PhotoFragment.this.mCurrentImageMessage.getLocalUri() == null ? PhotoFragment.this.mCurrentImageMessage.getRemoteUri() : PhotoFragment.this.mCurrentImageMessage.getLocalUri()));
                        PhotoFragment.this.mImageAdapter.addData(lists, direction.equals(GetMessageDirection.FRONT));
                        PhotoFragment.this.mViewPager.setAdapter(PhotoFragment.this.mImageAdapter);
                        PhotoFragment.this.isFirstTime = false;
                        PhotoFragment.this.mViewPager.setCurrentItem(lists.size() - 1);
                        PhotoFragment.this.mCurrentIndex = lists.size() - 1;
                    } else if (lists.size() > 0) {
                        PhotoFragment.this.mImageAdapter.addData(lists, direction.equals(GetMessageDirection.FRONT));
                        PhotoFragment.this.mImageAdapter.notifyDataSetChanged();
                        if (direction.equals(GetMessageDirection.FRONT)) {
                            PhotoFragment.this.mViewPager.setCurrentItem(lists.size());
                            PhotoFragment.this.mCurrentIndex = lists.size();
                        }
                    }

                }

                public void onError(ErrorCode e) {
                }
            });
        }

    }

    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    public boolean onBackPressed() {
        return false;
    }

    public void onRestoreUI() {
    }

    public void onDestroy() {
        super.onDestroy();
    }

    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    private class ImageInfo {
        private int messageId;
        private Uri thumbUri;
        private Uri largeImageUri;

        ImageInfo(int messageId, Uri thumbnail, Uri largeImageUri) {
            this.messageId = messageId;
            this.thumbUri = thumbnail;
            this.largeImageUri = largeImageUri;
        }

        public int getMessageId() {
            return this.messageId;
        }

        public Uri getLargeImageUri() {
            return this.largeImageUri;
        }

        public Uri getThumbUri() {
            return this.thumbUri;
        }
    }

    private class ImageAdapter extends PagerAdapter {
        private ArrayList<PhotoFragment.ImageInfo> mImageList;
        private PicturePopupWindow menuWindow;
        private OnClickListener onMenuWindowClick;

        private ImageAdapter() {
            this.mImageList = new ArrayList();
            this.onMenuWindowClick = new OnClickListener() {
                public void onClick(View v) {
                    if (v.getId() == id.rc_content) {
                    }

                    ImageAdapter.this.menuWindow.dismiss();
                }
            };
        }

        private View newView(Context context, final PhotoFragment.ImageInfo imageInfo) {
            View result = LayoutInflater.from(context).inflate(layout.rc_fr_image, (ViewGroup) null);
            PhotoFragment.ImageAdapter.ViewHolder holder = new PhotoFragment.ImageAdapter.ViewHolder();
            holder.progressBar = (ProgressBar) result.findViewById(id.rc_progress);
            holder.progressText = (TextView) result.findViewById(id.rc_txt);
            holder.photoView = (PhotoView) result.findViewById(id.rc_photoView);
            holder.photoView.setOnLongClickListener(new OnLongClickListener() {
                public boolean onLongClick(View v) {
                    Uri uri = imageInfo.getLargeImageUri();
                    File file = null;
                    if (uri != null) {
                        if (!uri.getScheme().startsWith("http") && !uri.getScheme().startsWith("https")) {
                            file = new File(uri.getPath());
                        } else {
                            file = ImageLoader.getInstance().getDiskCache().get(uri.toString());
                        }
                    }

                    ImageAdapter.this.menuWindow = new PicturePopupWindow(PhotoFragment.this.getActivity(), file);
                    ImageAdapter.this.menuWindow.showAtLocation(v, 81, 0, 0);
                    ImageAdapter.this.menuWindow.setOutsideTouchable(false);
                    return false;
                }
            });
            holder.photoView.setOnPhotoTapListener(new OnPhotoTapListener() {
                public void onPhotoTap(View view, float x, float y) {
                    PhotoFragment.this.getActivity().finish();
                }

                public void onOutsidePhotoTap() {
                }
            });
            result.setTag(holder);
            return result;
        }

        public void addData(ArrayList<PhotoFragment.ImageInfo> newImages, boolean direction) {
            if (newImages != null && newImages.size() != 0) {
                if (this.mImageList.size() == 0) {
                    this.mImageList.addAll(newImages);
                } else if (direction && !PhotoFragment.this.isFirstTime && !this.isDuplicate(((PhotoFragment.ImageInfo) newImages.get(0)).getMessageId())) {
                    ArrayList<PhotoFragment.ImageInfo> temp = new ArrayList();
                    temp.addAll(this.mImageList);
                    this.mImageList.clear();
                    this.mImageList.addAll(newImages);
                    this.mImageList.addAll(this.mImageList.size(), temp);
                } else if (!PhotoFragment.this.isFirstTime && !this.isDuplicate(((PhotoFragment.ImageInfo) newImages.get(0)).getMessageId())) {
                    this.mImageList.addAll(this.mImageList.size(), newImages);
                }

            }
        }

        private boolean isDuplicate(int messageId) {
            Iterator var2 = this.mImageList.iterator();

            PhotoFragment.ImageInfo info;
            do {
                if (!var2.hasNext()) {
                    return false;
                }

                info = (PhotoFragment.ImageInfo) var2.next();
            } while (info.getMessageId() != messageId);

            return true;
        }

        public PhotoFragment.ImageInfo getItem(int index) {
            return (PhotoFragment.ImageInfo) this.mImageList.get(index);
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
            RLog.i("PhotoFragment", "instantiateItem.position:" + position);
            View imageView = this.newView(container.getContext(), (PhotoFragment.ImageInfo) this.mImageList.get(position));
            this.updatePhotoView(position, imageView, PhotoFragment.this.mDownloadListener);
            imageView.setId(position);
            container.addView(imageView);
            return imageView;
        }

        public void destroyItem(ViewGroup container, int position, Object object) {
            RLog.i("PhotoFragment", "destroyItem.position:" + position);
            PhotoFragment.ImageAdapter.ViewHolder holder = (PhotoFragment.ImageAdapter.ViewHolder) container.findViewById(position).getTag();
            holder.photoView.setImageURI((Uri) null);
            container.removeView((View) object);
        }

        private void updatePhotoView(int position, View view, final PhotoFragment.PhotoDownloadListener downloadListener) {
            final PhotoFragment.ImageAdapter.ViewHolder holder = (PhotoFragment.ImageAdapter.ViewHolder) view.getTag();
            Uri originalUri = ((PhotoFragment.ImageInfo) this.mImageList.get(position)).getLargeImageUri();
            Uri thumbUri = ((PhotoFragment.ImageInfo) this.mImageList.get(position)).getThumbUri();
            if (originalUri != null && thumbUri != null) {
                File file;
                if (!originalUri.getScheme().startsWith("http") && !originalUri.getScheme().startsWith("https")) {
                    file = new File(originalUri.getPath());
                } else {
                    file = ImageLoader.getInstance().getDiskCache().get(originalUri.toString());
                }

                if (file != null && file.exists()) {
                    if (PhotoFragment.this.mDownloadListener != null) {
                        PhotoFragment.this.mDownloadListener.onDownloaded(originalUri);
                    }

                    AlbumBitmapCacheHelper.getInstance().addPathToShowlist(file.getAbsolutePath());
                    Bitmap bitmap = AlbumBitmapCacheHelper.getInstance().getBitmap(file.getAbsolutePath(), 0, 0, new ILoadImageCallback() {
                        public void onLoadImageCallBack(Bitmap bitmap, String p, Object... objects) {
                            if (bitmap != null) {
                                holder.photoView.setImageBitmap(bitmap);
                            }
                        }
                    }, new Object[]{position});
                    if (bitmap != null) {
                        holder.photoView.setImageBitmap(bitmap);
                    } else {
                        Drawable drawablex = Drawable.createFromPath(thumbUri.getPath());
                        holder.photoView.setImageDrawable(drawablex);
                    }
                } else if (position != PhotoFragment.this.mCurrentIndex) {
                    Drawable drawable = Drawable.createFromPath(thumbUri.getPath());
                    holder.photoView.setImageDrawable(drawable);
                } else {
                    ImageAware imageAware = new ImageViewAware(holder.photoView);
                    if (PhotoFragment.this.mDownloadingImageAware != null) {
                        ImageLoader.getInstance().cancelDisplayTask(PhotoFragment.this.mDownloadingImageAware);
                    }

                    ImageLoader.getInstance().displayImage(originalUri.toString(), imageAware, this.createDisplayImageOptions(thumbUri), new ImageLoadingListener() {
                        public void onLoadingStarted(String imageUri, View view) {
                            holder.progressText.setVisibility(0);
                            holder.progressBar.setVisibility(0);
                            holder.progressText.setText("0%");
                        }

                        public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                            if (downloadListener != null) {
                                downloadListener.onDownloadError();
                            }

                            holder.progressText.setVisibility(8);
                            holder.progressBar.setVisibility(8);
                        }

                        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                            if (downloadListener != null) {
                                downloadListener.onDownloaded(Uri.parse(imageUri));
                            }

                            holder.progressText.setVisibility(8);
                            holder.progressBar.setVisibility(8);
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
                    PhotoFragment.this.mDownloadingImageAware = imageAware;
                }

            } else {
                RLog.e("PhotoFragment", "large uri and thumbnail uri of the image should not be null.");
            }
        }

        private DisplayImageOptions createDisplayImageOptions(Uri uri) {
            Builder builder = new Builder();
            Drawable drawable = Drawable.createFromPath(uri.getPath());
            return builder.resetViewBeforeLoading(false).cacheInMemory(false).cacheOnDisk(true).bitmapConfig(Config.RGB_565).showImageForEmptyUri(drawable).showImageOnFail(drawable).showImageOnLoading(drawable).handler(new Handler()).build();
        }

        public class ViewHolder {
            ProgressBar progressBar;
            TextView progressText;
            PhotoView photoView;

            public ViewHolder() {
            }
        }
    }

    public interface PhotoDownloadListener {
        void onDownloaded(Uri var1);

        void onDownloadError();
    }
}

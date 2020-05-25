//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package io.rong.imkit.plugin.image;

import android.annotation.TargetApi;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.DrawableRes;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.content.FileProvider;
import androidx.documentfile.provider.DocumentFile;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.concurrent.TimeUnit;

import io.rong.common.FileUtils;
import io.rong.common.RLog;
import io.rong.imkit.R.color;
import io.rong.imkit.R.drawable;
import io.rong.imkit.R.id;
import io.rong.imkit.R.layout;
import io.rong.imkit.R.string;
import io.rong.imkit.RongBaseNoActionbarActivity;
import io.rong.imkit.plugin.image.PictureSelectorActivity.MediaItem;
import io.rong.imkit.plugin.image.PictureSelectorActivity.PicItemHolder;
import io.rong.imkit.utilities.KitStorageUtils;
import io.rong.imlib.RongIMClient;
import io.rong.subscaleview.ImageSource;
import io.rong.subscaleview.SubsamplingScaleImageView;

public class PicturePreviewActivity extends RongBaseNoActionbarActivity {
    private static final String TAG = "PicturePreviewActivity";
    public static final int RESULT_SEND = 1;
    private TextView mIndexTotal;
    private View mWholeView;
    private View mToolbarTop;
    private View mToolbarBottom;
    private ImageButton mBtnBack;
    private Button mBtnSend;
    private PicturePreviewActivity.CheckButton mUseOrigin;
    private PicturePreviewActivity.CheckButton mSelectBox;
    private HackyViewPager mViewPager;
    private ArrayList<MediaItem> mItemList;
    private ArrayList<MediaItem> mItemSelectedList;
    private ArrayList<MediaItem> mItemAllSelectedList;
    private int mCurrentIndex;

    public PicturePreviewActivity() {
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(1);
        this.setContentView(layout.rc_picprev_activity);
        this.initView();
        this.mUseOrigin.setChecked(this.getIntent().getBooleanExtra("sendOrigin", false));
        this.mCurrentIndex = this.getIntent().getIntExtra("index", 0);
        if (this.mItemList == null) {
            this.mItemList = PicItemHolder.itemList;
            this.mItemSelectedList = PicItemHolder.itemSelectedList;
            this.mItemAllSelectedList = PicItemHolder.itemAllSelectedMediaItemList;
        }

        if (this.mItemList == null) {
            RLog.e("PicturePreviewActivity", "Itemlist is null");
        } else {
            this.mIndexTotal.setText(String.format("%d/%d", this.mCurrentIndex + 1, this.mItemList.size()));
            int result;
            if (VERSION.SDK_INT >= 11) {
                this.mWholeView.setSystemUiVisibility(1024);
                result = getSmartBarHeight(this);
                if (result > 0) {
                    LayoutParams lp = (LayoutParams) this.mToolbarBottom.getLayoutParams();
                    lp.setMargins(0, 0, 0, result);
                    this.mToolbarBottom.setLayoutParams(lp);
                }
            }

            result = 0;
            int resourceId = this.getResources().getIdentifier("status_bar_height", "dimen", "android");
            if (resourceId > 0) {
                result = this.getResources().getDimensionPixelSize(resourceId);
            }

            LayoutParams lp = new LayoutParams(this.mToolbarTop.getLayoutParams());
            lp.setMargins(0, result, 0, 0);
            this.mToolbarTop.setLayoutParams(lp);
            this.mBtnBack.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    Intent intent = new Intent();
                    intent.putExtra("sendOrigin", PicturePreviewActivity.this.mUseOrigin.getChecked());
                    PicturePreviewActivity.this.setResult(-1, intent);
                    PicturePreviewActivity.this.finish();
                }
            });
            this.mBtnSend.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    LinkedHashMap<String, Integer> mLinkedHashMap = new LinkedHashMap();
                    Iterator var3;
                    MediaItem item;
                    String filePath;
                    String fileName;
                    boolean result;
                    if (PicturePreviewActivity.this.mItemSelectedList != null) {
                        var3 = PicturePreviewActivity.this.mItemSelectedList.iterator();

                        while (var3.hasNext()) {
                            item = (MediaItem) var3.next();
                            if (item.selected) {
                                if (KitStorageUtils.isBuildAndTargetForQ(PicturePreviewActivity.this.getApplicationContext())) {
                                    fileName = FileUtils.getFileNameWithPath(item.uri);
                                    if (item.mediaType == 1) {
                                        filePath = KitStorageUtils.getImageSavePath(PicturePreviewActivity.this.getApplicationContext()) + File.separator + fileName;
                                    } else if (item.mediaType == 3) {
                                        filePath = KitStorageUtils.getVideoSavePath(PicturePreviewActivity.this.getApplicationContext()) + File.separator + fileName;
                                    } else {
                                        filePath = KitStorageUtils.getFileSavePath(PicturePreviewActivity.this.getApplicationContext()) + File.separator + fileName;
                                    }

                                    result = FileUtils.copyFile(v.getContext(), Uri.parse(item.uri_sdk29), filePath);
                                    if (result) {
                                        mLinkedHashMap.put("file://" + filePath, item.mediaType);
                                    }
                                } else {
                                    mLinkedHashMap.put("file://" + item.uri, item.mediaType);
                                }
                            }
                        }
                    }

                    var3 = PicturePreviewActivity.this.mItemList.iterator();

                    while (var3.hasNext()) {
                        item = (MediaItem) var3.next();
                        if (item.selected) {
                            if (KitStorageUtils.isBuildAndTargetForQ(PicturePreviewActivity.this.getApplicationContext())) {
                                fileName = FileUtils.getFileNameWithPath(item.uri);
                                if (item.mediaType == 1) {
                                    filePath = KitStorageUtils.getImageSavePath(PicturePreviewActivity.this.getApplicationContext()) + File.separator + fileName;
                                } else if (item.mediaType == 3) {
                                    filePath = KitStorageUtils.getVideoSavePath(PicturePreviewActivity.this.getApplicationContext()) + File.separator + fileName;
                                } else {
                                    filePath = KitStorageUtils.getFileSavePath(PicturePreviewActivity.this.getApplicationContext()) + File.separator + fileName;
                                }

                                result = FileUtils.copyFile(PicturePreviewActivity.this.getApplicationContext(), Uri.parse(item.uri_sdk29), filePath);
                                if (result) {
                                    mLinkedHashMap.put("file://" + filePath, item.mediaType);
                                }
                            } else {
                                mLinkedHashMap.put("file://" + item.uri, item.mediaType);
                            }
                        }
                    }

                    Gson gson = new Gson();
                    String mediaList = gson.toJson(mLinkedHashMap);
                    Intent data = new Intent();
                    data.putExtra("sendOrigin", PicturePreviewActivity.this.mUseOrigin.getChecked());
                    data.putExtra("android.intent.extra.RETURN_RESULT", mediaList);
                    PicturePreviewActivity.this.setResult(1, data);
                    PicturePreviewActivity.this.finish();
                }
            });
            this.mUseOrigin.setText(string.rc_picprev_origin);
            this.mUseOrigin.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    MediaItem item = (MediaItem) PicturePreviewActivity.this.mItemList.get(PicturePreviewActivity.this.mCurrentIndex);
                    if (item.uri.endsWith(".gif")) {
                        int length = RongIMClient.getInstance().getGIFLimitSize() * 1024;
                        File file = new File(item.uri);
                        if (file != null && file.exists() && file.length() > (long) length) {
                            (new Builder(PicturePreviewActivity.this)).setMessage(PicturePreviewActivity.this.getResources().getString(string.rc_picsel_selected_max_gif_size_span_with_param)).setPositiveButton(string.rc_confirm, (android.content.DialogInterface.OnClickListener) null).setCancelable(false).create().show();
                            return;
                        }
                    }

                    PicturePreviewActivity.this.mUseOrigin.setChecked(!PicturePreviewActivity.this.mUseOrigin.getChecked());
                    if (PicturePreviewActivity.this.mUseOrigin.getChecked() && PicturePreviewActivity.this.getTotalSelectedNum() == 0) {
                        PicturePreviewActivity.this.mSelectBox.setChecked(!PicturePreviewActivity.this.mSelectBox.getChecked());
                        ((MediaItem) PicturePreviewActivity.this.mItemList.get(PicturePreviewActivity.this.mCurrentIndex)).selected = PicturePreviewActivity.this.mSelectBox.getChecked();
                        PicturePreviewActivity.this.updateToolbar();
                    }

                }
            });
            this.mSelectBox.setText(string.rc_picprev_select);
            this.mSelectBox.setChecked(((MediaItem) this.mItemList.get(this.mCurrentIndex)).selected);
            this.mSelectBox.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    MediaItem item = (MediaItem) PicturePreviewActivity.this.mItemList.get(PicturePreviewActivity.this.mCurrentIndex);
                    int maxDuration;
                    if (item.mediaType == 3) {
                        maxDuration = RongIMClient.getInstance().getVideoLimitTime();
                        if (maxDuration < 1) {
                            maxDuration = 300;
                        }

                        if (TimeUnit.MILLISECONDS.toSeconds((long) item.duration) > (long) maxDuration) {
                            (new Builder(PicturePreviewActivity.this)).setMessage(PicturePreviewActivity.this.getResources().getString(string.rc_picsel_selected_max_time_span_with_param, new Object[]{maxDuration / 60})).setPositiveButton(string.rc_confirm, (android.content.DialogInterface.OnClickListener) null).setCancelable(false).create().show();
                            return;
                        }
                    }

                    if (item.uri.endsWith(".gif")) {
                        maxDuration = RongIMClient.getInstance().getGIFLimitSize() * 1024;
                        File file = new File(item.uri);
                        if (file != null && file.exists() && file.length() > (long) maxDuration) {
                            (new Builder(PicturePreviewActivity.this)).setMessage(PicturePreviewActivity.this.getResources().getString(string.rc_picsel_selected_max_gif_size_span_with_param)).setPositiveButton(string.rc_confirm, (android.content.DialogInterface.OnClickListener) null).setCancelable(false).create().show();
                            return;
                        }
                    }

                    if (!PicturePreviewActivity.this.mSelectBox.getChecked() && PicturePreviewActivity.this.getTotalSelectedNum() == 9) {
                        Toast.makeText(PicturePreviewActivity.this, string.rc_picsel_selected_max_pic_count, 0).show();
                    } else {
                        PicturePreviewActivity.this.mSelectBox.setChecked(!PicturePreviewActivity.this.mSelectBox.getChecked());
                        ((MediaItem) PicturePreviewActivity.this.mItemList.get(PicturePreviewActivity.this.mCurrentIndex)).selected = PicturePreviewActivity.this.mSelectBox.getChecked();
                        if (PicturePreviewActivity.this.mItemAllSelectedList != null) {
                            if (PicturePreviewActivity.this.mSelectBox.getChecked()) {
                                PicturePreviewActivity.this.mItemAllSelectedList.add(PicturePreviewActivity.this.mItemList.get(PicturePreviewActivity.this.mCurrentIndex));
                            } else {
                                PicturePreviewActivity.this.mItemAllSelectedList.remove(PicturePreviewActivity.this.mItemList.get(PicturePreviewActivity.this.mCurrentIndex));
                            }
                        } else {
                            RLog.e("PicturePreviewActivity", "mItemAllSelectedList is null");
                        }

                        PicturePreviewActivity.this.updateToolbar();
                    }
                }
            });
            this.mViewPager.setAdapter(new PicturePreviewActivity.PreviewAdapter());
            this.mViewPager.setCurrentItem(this.mCurrentIndex);
            this.mViewPager.setOffscreenPageLimit(1);
            this.mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                }

                public void onPageSelected(int position) {
                    PicturePreviewActivity.this.mCurrentIndex = position;
                    PicturePreviewActivity.this.mIndexTotal.setText(String.format("%d/%d", position + 1, PicturePreviewActivity.this.mItemList.size()));
                    PicturePreviewActivity.this.mSelectBox.setChecked(((MediaItem) PicturePreviewActivity.this.mItemList.get(position)).selected);
                    MediaItem mediaItem = (MediaItem) PicturePreviewActivity.this.mItemList.get(position);
                    PicturePreviewActivity.this.updateToolbar();
                    if (mediaItem.mediaType == 3) {
                        PicturePreviewActivity.this.mUseOrigin.rootView.setVisibility(8);
                    } else {
                        PicturePreviewActivity.this.mUseOrigin.rootView.setVisibility(0);
                    }

                }

                public void onPageScrollStateChanged(int state) {
                }
            });
            this.updateToolbar();
        }
    }

    private void initView() {
        this.mToolbarTop = this.findViewById(id.toolbar_top);
        this.mIndexTotal = (TextView) this.findViewById(id.index_total);
        this.mBtnBack = (ImageButton) this.findViewById(id.back);
        this.mBtnSend = (Button) this.findViewById(id.send);
        this.mWholeView = this.findViewById(id.whole_layout);
        this.mViewPager = (HackyViewPager) this.findViewById(id.viewpager);
        this.mToolbarBottom = this.findViewById(id.toolbar_bottom);
        this.mUseOrigin = new PicturePreviewActivity.CheckButton(this.findViewById(id.origin_check), drawable.rc_origin_check_nor, drawable.rc_origin_check_sel);
        this.mSelectBox = new PicturePreviewActivity.CheckButton(this.findViewById(id.select_check), drawable.rc_select_check_nor, drawable.rc_select_check_sel);
    }

    protected void onResume() {
        super.onResume();
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == 4) {
            Intent intent = new Intent();
            intent.putExtra("sendOrigin", this.mUseOrigin.getChecked());
            this.setResult(-1, intent);
        }

        return super.onKeyDown(keyCode, event);
    }

    private int getTotalSelectedNum() {
        int sum = 0;

        for (int i = 0; i < this.mItemList.size(); ++i) {
            if (((MediaItem) this.mItemList.get(i)).selected) {
                ++sum;
            }
        }

        if (this.mItemSelectedList != null) {
            sum += this.mItemSelectedList.size();
        }

        return sum;
    }

    private String getTotalSelectedSize() {
        float size = 0.0F;

        int i;
        File file;
        for (i = 0; i < this.mItemList.size(); ++i) {
            if (((MediaItem) this.mItemList.get(i)).selected) {
                file = new File(((MediaItem) this.mItemList.get(i)).uri);
                size += (float) file.length() / 1024.0F;
            }
        }

        if (this.mItemSelectedList != null) {
            for (i = 0; i < this.mItemSelectedList.size(); ++i) {
                if (((MediaItem) this.mItemSelectedList.get(i)).selected) {
                    file = new File(((MediaItem) this.mItemSelectedList.get(i)).uri);
                    size += (float) file.length() / 1024.0F;
                }
            }
        }

        String totalSize;
        if (size < 1024.0F) {
            totalSize = String.format("%.0fK", size);
        } else {
            totalSize = String.format("%.1fM", size / 1024.0F);
        }

        return totalSize;
    }

    private String getSelectedSize(int index) {
        float size = 0.0F;
        if (this.mItemList != null && this.mItemList.size() > 0) {
            long maxSize = 0L;
            if (KitStorageUtils.isBuildAndTargetForQ(this)) {
                maxSize = DocumentFile.fromSingleUri(this, Uri.parse(((MediaItem) this.mItemList.get(index)).uri_sdk29)).length();
            } else {
                maxSize = (new File(((MediaItem) this.mItemList.get(index)).uri)).length();
            }

            size = (float) maxSize / 1024.0F;
        }

        String returnSize;
        if (size < 1024.0F) {
            returnSize = String.format("%.0fK", size);
        } else {
            returnSize = String.format("%.1fM", size / 1024.0F);
        }

        return returnSize;
    }

    private void updateToolbar() {
        int selNum = this.getTotalSelectedNum();
        if (this.mItemList.size() == 1 && selNum == 0) {
            this.mBtnSend.setText(string.rc_picsel_toolbar_send);
            this.mUseOrigin.setText(string.rc_picprev_origin);
            this.mBtnSend.setEnabled(false);
            this.mBtnSend.setTextColor(this.getResources().getColor(color.rc_picsel_toolbar_send_text_disable));
        } else {
            if (selNum == 0) {
                this.mBtnSend.setText(string.rc_picsel_toolbar_send);
                this.mUseOrigin.setText(string.rc_picprev_origin);
                this.mUseOrigin.setChecked(false);
                this.mBtnSend.setEnabled(false);
                this.mBtnSend.setTextColor(this.getResources().getColor(color.rc_picsel_toolbar_send_text_disable));
            } else if (selNum <= 9) {
                this.mBtnSend.setEnabled(true);
                this.mBtnSend.setTextColor(this.getResources().getColor(color.rc_picsel_toolbar_send_text_normal));
                this.mBtnSend.setText(String.format(this.getResources().getString(string.rc_picsel_toolbar_send_num), selNum));
            }

            this.mUseOrigin.setText(String.format(this.getResources().getString(string.rc_picprev_origin_size), this.getSelectedSize(this.mCurrentIndex)));
            MediaItem mediaItem = (MediaItem) this.mItemList.get(this.mCurrentIndex);
            if (mediaItem.mediaType == 3) {
                this.mUseOrigin.rootView.setVisibility(8);
            } else {
                this.mUseOrigin.rootView.setVisibility(0);
            }

        }
    }

    @TargetApi(11)
    public static int getSmartBarHeight(Context context) {
        try {
            Class c = Class.forName("com.android.internal.R$dimen");
            Object obj = c.newInstance();
            Field field = c.getField("mz_action_button_min_height");
            int height = Integer.parseInt(field.get(obj).toString());
            return context.getResources().getDimensionPixelSize(height);
        } catch (Exception var5) {
            RLog.e("PicturePreviewActivity", "getSmartBarHeight", var5);
            return 0;
        }
    }

    public int readPictureDegree(String path, Context context) {
        return FileUtils.readPictureDegree(context, path);
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

    private class PreviewAdapter extends PagerAdapter {
        private PreviewAdapter() {
        }

        public int getCount() {
            return PicturePreviewActivity.this.mItemList.size();
        }

        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        public Object instantiateItem(ViewGroup container, int position) {
            final MediaItem mediaItem = (MediaItem) PicturePreviewActivity.this.mItemList.get(position);
            View view = LayoutInflater.from(container.getContext()).inflate(layout.rc_picsel_preview, container, false);
            SubsamplingScaleImageView subsamplingScaleImageView = (SubsamplingScaleImageView) view.findViewById(id.rc_photoView);
            ImageView gifview = (ImageView) view.findViewById(id.rc_gifview);
            ImageButton playButton = (ImageButton) view.findViewById(id.rc_play_video);
            container.addView(view, -1, -1);
            String imagePath;
            if (mediaItem.mediaType == 3) {
                imagePath = KitStorageUtils.getImageSavePath(PicturePreviewActivity.this) + File.separator + mediaItem.name;
                if (!(new File(imagePath)).exists()) {
                    Bitmap videoFrame = null;
                    if (KitStorageUtils.isBuildAndTargetForQ(PicturePreviewActivity.this.getApplicationContext())) {
                        try {
                            ParcelFileDescriptor pfd = PicturePreviewActivity.this.getApplicationContext().getContentResolver().openFileDescriptor(Uri.parse(mediaItem.uri_sdk29), "r");
                            MediaMetadataRetriever retriever = new MediaMetadataRetriever();
                            retriever.setDataSource(pfd.getFileDescriptor());
                            if (VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                                videoFrame = retriever.getFrameAtIndex(0);
                            }
                        } catch (IOException var12) {
                            RLog.e("PicturePreviewActivity", "instantiateItem Q is error");
                        }
                    } else {
                        videoFrame = ThumbnailUtils.createVideoThumbnail(mediaItem.uri, 1);
                    }

                    if (videoFrame != null) {
                        imagePath = FileUtils.convertBitmap2File(videoFrame, KitStorageUtils.getImageSavePath(PicturePreviewActivity.this), mediaItem.name).toString();
                    } else {
                        imagePath = "";
                    }
                }

                playButton.setOnClickListener(new OnClickListener() {
                    public void onClick(View v) {
                        Intent intent = new Intent("android.intent.action.VIEW");
                        if (VERSION.SDK_INT >= 24) {
                            intent.setFlags(335544320);
                            Uri uri = FileProvider.getUriForFile(v.getContext(), v.getContext().getPackageName() + v.getContext().getResources().getString(string.rc_authorities_fileprovider), new File(mediaItem.uri));
                            if (KitStorageUtils.isBuildAndTargetForQ(PicturePreviewActivity.this.getApplicationContext())) {
                                intent.setDataAndType(Uri.parse(mediaItem.uri_sdk29), mediaItem.mimeType);
                            } else {
                                intent.setDataAndType(uri, mediaItem.mimeType);
                            }

                            intent.addFlags(1);
                        } else {
                            intent.setDataAndType(Uri.parse("file://" + mediaItem.uri), mediaItem.mimeType);
                        }

                        PicturePreviewActivity.this.startActivity(intent);
                    }
                });
                playButton.setVisibility(0);
                subsamplingScaleImageView.setImage(ImageSource.uri(imagePath));
                gifview.setVisibility(8);
                subsamplingScaleImageView.setOrientation(PicturePreviewActivity.this.readPictureDegree(imagePath, container.getContext()));
            } else {
                playButton.setVisibility(8);
                if (KitStorageUtils.isBuildAndTargetForQ(PicturePreviewActivity.this.getApplicationContext())) {
                    imagePath = ((MediaItem) PicturePreviewActivity.this.mItemList.get(position)).uri_sdk29;
                } else {
                    imagePath = ((MediaItem) PicturePreviewActivity.this.mItemList.get(position)).uri;
                }

                if (((MediaItem) PicturePreviewActivity.this.mItemList.get(position)).uri.endsWith(".gif")) {
                    gifview.setVisibility(0);
                    Glide.with(PicturePreviewActivity.this).asGif().load(imagePath).into(gifview);
                } else {
                    gifview.setVisibility(8);
                    subsamplingScaleImageView.setImage(ImageSource.uri(imagePath));
                    subsamplingScaleImageView.setOrientation(PicturePreviewActivity.this.readPictureDegree(imagePath, container.getContext()));
                }
            }

            AlbumBitmapCacheHelper.getInstance().removePathFromShowlist(imagePath);
            AlbumBitmapCacheHelper.getInstance().addPathToShowlist(imagePath);
            return view;
        }

        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }

    private static class CheckButton {
        private View rootView;
        private ImageView image;
        private TextView text;
        private boolean checked = false;
        private int nor_resId;
        private int sel_resId;

        public CheckButton(View root, @DrawableRes int norId, @DrawableRes int selId) {
            this.rootView = root;
            this.image = (ImageView) root.findViewById(id.image);
            this.text = (TextView) root.findViewById(id.text);
            this.nor_resId = norId;
            this.sel_resId = selId;
            this.image.setImageResource(this.nor_resId);
        }

        public void setChecked(boolean check) {
            this.checked = check;
            this.image.setImageResource(this.checked ? this.sel_resId : this.nor_resId);
        }

        public boolean getChecked() {
            return this.checked;
        }

        public void setText(int resId) {
            this.text.setText(resId);
        }

        public void setText(CharSequence chars) {
            this.text.setText(chars);
        }

        public void setOnClickListener(@Nullable OnClickListener l) {
            this.rootView.setOnClickListener(l);
        }

        public void setSelectButtonVisibility(int visibility) {
            this.image.setVisibility(visibility);
        }
    }
}

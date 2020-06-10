//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package io.rong.imkit.plugin.image;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaMetadataRetriever;
import android.media.MediaScannerConnection;
import android.media.MediaScannerConnection.OnScanCompletedListener;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Parcel;
import android.os.ParcelFileDescriptor;
import android.os.Parcelable;
import android.provider.MediaStore.Files;
import android.provider.MediaStore.Images.Media;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.core.content.FileProvider;

import com.google.gson.Gson;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import io.rong.common.FileUtils;
import io.rong.common.RLog;
import io.rong.imkit.R;
import io.rong.imkit.R.bool;
import io.rong.imkit.R.color;
import io.rong.imkit.R.drawable;
import io.rong.imkit.R.id;
import io.rong.imkit.R.layout;
import io.rong.imkit.R.string;
import io.rong.imkit.RongBaseNoActionbarActivity;
import io.rong.imkit.plugin.image.AlbumBitmapCacheHelper.ILoadImageCallback;
import io.rong.imkit.utilities.KitStorageUtils;
import io.rong.imkit.utilities.PermissionCheckUtil;
import io.rong.imlib.IMLibExtensionModuleManager;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.HardwareResource.ResourceType;

public class PictureSelectorActivity extends RongBaseNoActionbarActivity {
  private static final String TAG = PictureSelectorActivity.class.getSimpleName();
  public static final int REQUEST_PREVIEW = 0;
  public static final int REQUEST_CAMERA = 1;
  public static final int REQUEST_CODE_ASK_PERMISSIONS = 100;
  public static final int SIGHT_DEFAULT_DURATION_LIMIT = 300;
  private GridView mGridView;
  private ImageButton mBtnBack;
  private Button mBtnSend;
  private PictureSelectorActivity.PicTypeBtn mPicType;
  private PictureSelectorActivity.PreviewBtn mPreviewBtn;
  private View mCatalogView;
  private ListView mCatalogListView;
  private List<PictureSelectorActivity.MediaItem> mAllItemList;
  private Map<String, List<PictureSelectorActivity.MediaItem>> mItemMap;
  private ArrayList<Uri> mAllSelectedItemList;
  private List<String> mCatalogList;
  private String mCurrentCatalog = "";
  private Uri mTakePictureUri;
  private boolean mSendOrigin = false;
  private int perWidth;
  private int perHeight;
  private ExecutorService pool;
  private Handler bgHandler;
  private Handler uiHandler;
  private HandlerThread thread;

  public PictureSelectorActivity() {
  }

  @TargetApi(23)
  protected void onCreate(Bundle savedInstanceState) {
    this.requestWindowFeature(1);
    super.onCreate(savedInstanceState);
    this.setContentView(layout.rc_picsel_activity);
    this.thread = new HandlerThread(TAG);
    this.thread.start();
    this.bgHandler = new Handler(this.thread.getLooper());
    this.uiHandler = new Handler(this.getMainLooper());
    this.mGridView = (GridView)this.findViewById(id.gridlist);
    this.mBtnBack = (ImageButton)this.findViewById(id.back);
    this.mBtnBack.setOnClickListener(new OnClickListener() {
      public void onClick(View v) {
        PictureSelectorActivity.this.finish();
      }
    });
    this.mBtnSend = (Button)this.findViewById(id.send);
    this.mPicType = (PictureSelectorActivity.PicTypeBtn)this.findViewById(id.pic_type);
    this.mPicType.init(this);
    this.mPicType.setEnabled(false);
    this.mPreviewBtn = (PictureSelectorActivity.PreviewBtn)this.findViewById(id.preview);
    this.mPreviewBtn.init(this);
    this.mPreviewBtn.setEnabled(false);
    this.mCatalogView = this.findViewById(id.catalog_window);
    this.mCatalogListView = (ListView)this.findViewById(id.catalog_listview);
    String[] permissions = new String[]{"android.permission.READ_EXTERNAL_STORAGE"};
    if (!PermissionCheckUtil.checkPermissions(this, permissions)) {
      PermissionCheckUtil.requestPermissions(this, permissions, 100);
    } else {
      this.pool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
      this.initView();
    }
  }

  private void initView() {
    this.updatePictureItems(new PictureSelectorActivity.IExecutedCallback() {
      public void executed() {
        if (PictureSelectorActivity.this.uiHandler != null) {
          PictureSelectorActivity.this.uiHandler.post(new Runnable() {
            public void run() {
              PictureSelectorActivity.this.initWidget();
            }
          });
        }

      }
    });
  }

  private void initWidget() {
    this.mGridView.setAdapter(new PictureSelectorActivity.GridViewAdapter());
    this.mGridView.setOnItemClickListener(new OnItemClickListener() {
      public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (position != 0) {
          PictureSelectorActivity.PicItemHolder.itemList = new ArrayList();
          if (PictureSelectorActivity.this.mCurrentCatalog.isEmpty()) {
            PictureSelectorActivity.PicItemHolder.itemList.addAll(PictureSelectorActivity.this.mAllItemList);
            PictureSelectorActivity.PicItemHolder.itemSelectedList = null;
          } else {
            PictureSelectorActivity.PicItemHolder.itemList.addAll((Collection)PictureSelectorActivity.this.mItemMap.get(PictureSelectorActivity.this.mCurrentCatalog));
            PictureSelectorActivity.PicItemHolder.itemSelectedList = new ArrayList();
            Iterator var6 = PictureSelectorActivity.this.mItemMap.keySet().iterator();

            label32:
            while(true) {
              String key;
              do {
                if (!var6.hasNext()) {
                  break label32;
                }

                key = (String)var6.next();
              } while(key.equals(PictureSelectorActivity.this.mCurrentCatalog));

              Iterator var8 = ((List)PictureSelectorActivity.this.mItemMap.get(key)).iterator();

              while(var8.hasNext()) {
                PictureSelectorActivity.MediaItem item = (PictureSelectorActivity.MediaItem)var8.next();
                if (item.selected) {
                  PictureSelectorActivity.PicItemHolder.itemSelectedList.add(item);
                }
              }
            }
          }

          Intent intent = new Intent(PictureSelectorActivity.this, PicturePreviewActivity.class);
          intent.putExtra("index", position - 1);
          intent.putExtra("sendOrigin", PictureSelectorActivity.this.mSendOrigin);
          PictureSelectorActivity.this.startActivityForResult(intent, 0);
        }
      }
    });
    this.mBtnSend.setOnClickListener(new OnClickListener() {
      public void onClick(View v) {
        LinkedHashMap<String, Integer> mLinkedHashMap = new LinkedHashMap();
        Iterator var3 = PictureSelectorActivity.this.mItemMap.entrySet().iterator();

        while(var3.hasNext()) {
          Entry<String, List<PictureSelectorActivity.MediaItem>> entry = (Entry)var3.next();
          Iterator var5 = ((List)entry.getValue()).iterator();

          while(var5.hasNext()) {
            PictureSelectorActivity.MediaItem item = (PictureSelectorActivity.MediaItem)var5.next();
            if (item.selected) {
              if (KitStorageUtils.isBuildAndTargetForQ(PictureSelectorActivity.this)) {
                String fileName = FileUtils.getFileNameWithPath(item.uri);
                String filePath;
                if (item.mediaType == 1) {
                  filePath = KitStorageUtils.getImageSavePath(PictureSelectorActivity.this) + File.separator + fileName;
                } else if (item.mediaType == 3) {
                  filePath = KitStorageUtils.getVideoSavePath(PictureSelectorActivity.this) + File.separator + fileName;
                } else {
                  filePath = KitStorageUtils.getFileSavePath(PictureSelectorActivity.this) + File.separator + fileName;
                }

                boolean result = FileUtils.copyFile(PictureSelectorActivity.this.getApplicationContext(), Uri.parse(item.uri_sdk29), filePath);
                if (result) {
                  mLinkedHashMap.put("file://" + filePath, item.mediaType);
                }
              } else {
                mLinkedHashMap.put("file://" + item.uri, item.mediaType);
              }
            }
          }
        }

        Gson gson = new Gson();
        String mediaList = gson.toJson(mLinkedHashMap);
        Intent data = new Intent();
        data.putExtra("sendOrigin", PictureSelectorActivity.this.mSendOrigin);
        data.putExtra("android.intent.extra.RETURN_RESULT", mediaList);
        PictureSelectorActivity.this.setResult(-1, data);
        PictureSelectorActivity.this.finish();
      }
    });
    this.mPicType.setEnabled(true);
    this.mPicType.setTextColor(this.getResources().getColor(color.rc_picsel_toolbar_send_text_normal));
    this.mPicType.setOnClickListener(new OnClickListener() {
      public void onClick(View v) {
        PictureSelectorActivity.this.mCatalogView.setVisibility(0);
      }
    });
    this.mPreviewBtn.setOnClickListener(new OnClickListener() {
      public void onClick(View v) {
        PictureSelectorActivity.PicItemHolder.itemList = new ArrayList();
        Iterator var2 = PictureSelectorActivity.this.mItemMap.keySet().iterator();

        PictureSelectorActivity.MediaItem mediaItem;
        while(var2.hasNext()) {
          String key = (String)var2.next();
          Iterator var4 = ((List)PictureSelectorActivity.this.mItemMap.get(key)).iterator();

          while(var4.hasNext()) {
            mediaItem = (PictureSelectorActivity.MediaItem)var4.next();
            if (mediaItem.selected) {
              PictureSelectorActivity.PicItemHolder.itemList.add(mediaItem);
            }
          }
        }

        if (PictureSelectorActivity.this.mAllSelectedItemList != null && PictureSelectorActivity.PicItemHolder.itemList != null) {
          for(int i = 0; i < PictureSelectorActivity.this.mAllSelectedItemList.size(); ++i) {
            Uri imageUri = (Uri)PictureSelectorActivity.this.mAllSelectedItemList.get(i);

            for(int j = i + 1; j < PictureSelectorActivity.PicItemHolder.itemList.size(); ++j) {
              mediaItem = (PictureSelectorActivity.MediaItem)PictureSelectorActivity.PicItemHolder.itemList.get(j);
              if (mediaItem != null && imageUri.toString().contains(mediaItem.uri.toString())) {
                PictureSelectorActivity.PicItemHolder.itemList.remove(j);
                PictureSelectorActivity.PicItemHolder.itemList.add(i, mediaItem);
              }
            }
          }
        }

        PictureSelectorActivity.PicItemHolder.itemSelectedList = null;
        Intent intent = new Intent(PictureSelectorActivity.this, PicturePreviewActivity.class);
        intent.putExtra("sendOrigin", PictureSelectorActivity.this.mSendOrigin);
        PictureSelectorActivity.this.startActivityForResult(intent, 0);
      }
    });
    this.mCatalogView.setOnTouchListener(new OnTouchListener() {
      public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction() == 1 && PictureSelectorActivity.this.mCatalogView.getVisibility() == 0) {
          PictureSelectorActivity.this.mCatalogView.setVisibility(8);
        }

        return true;
      }
    });
    this.mCatalogListView.setAdapter(new PictureSelectorActivity.CatalogAdapter());
    this.mCatalogListView.setOnItemClickListener(new OnItemClickListener() {
      public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        String catalog;
        if (position == 0) {
          catalog = "";
        } else {
          catalog = (String)PictureSelectorActivity.this.mCatalogList.get(position - 1);
        }

        if (catalog.equals(PictureSelectorActivity.this.mCurrentCatalog)) {
          PictureSelectorActivity.this.mCatalogView.setVisibility(8);
        } else {
          PictureSelectorActivity.this.mCurrentCatalog = catalog;
          TextView textView = (TextView)view.findViewById(R.id.name);
          PictureSelectorActivity.this.mPicType.setText(textView.getText().toString());
          PictureSelectorActivity.this.mCatalogView.setVisibility(8);
          ((PictureSelectorActivity.CatalogAdapter)PictureSelectorActivity.this.mCatalogListView.getAdapter()).notifyDataSetChanged();
          ((PictureSelectorActivity.GridViewAdapter)PictureSelectorActivity.this.mGridView.getAdapter()).notifyDataSetChanged();
        }
      }
    });
    this.perWidth = ((WindowManager)((WindowManager)this.getSystemService("window"))).getDefaultDisplay().getWidth() / 3;
    this.perHeight = ((WindowManager)((WindowManager)this.getSystemService("window"))).getDefaultDisplay().getHeight() / 5;
  }

  @TargetApi(23)
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (requestCode == 100) {
      if (VERSION.SDK_INT >= 23 && this.checkSelfPermission("android.permission.READ_EXTERNAL_STORAGE") == 0) {
        this.initView();
      } else {
        Toast.makeText(this.getApplicationContext(), this.getString(string.rc_permission_grant_needed), 0).show();
        this.finish();
      }
    }

    if (resultCode != 0) {
      if (resultCode == 1) {
        this.setResult(-1, data);
        this.finish();
      } else {
        switch(requestCode) {
          case 0:
            this.mSendOrigin = data.getBooleanExtra("sendOrigin", false);
            PictureSelectorActivity.GridViewAdapter gridViewAdapter = (PictureSelectorActivity.GridViewAdapter)this.mGridView.getAdapter();
            if (gridViewAdapter != null) {
              gridViewAdapter.notifyDataSetChanged();
            }

            PictureSelectorActivity.CatalogAdapter catalogAdapter = (PictureSelectorActivity.CatalogAdapter)this.mCatalogListView.getAdapter();
            if (catalogAdapter != null) {
              catalogAdapter.notifyDataSetChanged();
            }

            this.updateToolbar();
            break;
          case 1:
            if (this.mTakePictureUri != null) {
              PictureSelectorActivity.PicItemHolder.itemList = new ArrayList();
              PictureSelectorActivity.MediaItem item = new PictureSelectorActivity.MediaItem();
              item.uri = this.mTakePictureUri.getPath();
              item.mediaType = 1;
              PictureSelectorActivity.PicItemHolder.itemList.add(item);
              PictureSelectorActivity.PicItemHolder.itemSelectedList = null;
              item.uri_sdk29 = this.mTakePictureUri.toString();
              Intent intent = new Intent(this, PicturePreviewActivity.class);
              this.startActivityForResult(intent, 0);
              MediaScannerConnection.scanFile(this.getApplicationContext(), new String[]{this.mTakePictureUri.getPath()}, (String[])null, new OnScanCompletedListener() {
                public void onScanCompleted(String path, Uri uri) {
                  PictureSelectorActivity.this.updatePictureItems((PictureSelectorActivity.IExecutedCallback)null);
                }
              });
            }
        }

      }
    }
  }

  public boolean onKeyDown(int keyCode, KeyEvent event) {
    if (keyCode == 4 && this.mCatalogView != null && this.mCatalogView.getVisibility() == 0) {
      this.mCatalogView.setVisibility(8);
      return true;
    } else {
      return super.onKeyDown(keyCode, event);
    }
  }

  protected void requestCamera() {
    if (IMLibExtensionModuleManager.getInstance().onRequestHardwareResource(ResourceType.VIDEO)) {
      Toast.makeText(this, this.getString(string.rc_voip_call_video_start_fail), 1).show();
    } else if (IMLibExtensionModuleManager.getInstance().onRequestHardwareResource(ResourceType.AUDIO)) {
      Toast.makeText(this, this.getString(string.rc_voip_call_audio_start_fail), 1).show();
    } else {
      Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
      List<ResolveInfo> resInfoList = this.getPackageManager().queryIntentActivities(intent, 65536);
      if (resInfoList.size() <= 0) {
        Toast.makeText(this, this.getResources().getString(string.rc_voip_cpu_error), 0).show();
      } else {
        String name;
        Uri uri;
        if (KitStorageUtils.isBuildAndTargetForQ(this)) {
          name = String.valueOf(System.currentTimeMillis());
          ContentValues values = new ContentValues();
          values.put("description", "This is an image");
          values.put("_display_name", name);
          values.put("mime_type", "image/jpeg");
          values.put("title", name);
          values.put("relative_path", "Pictures");
          uri = Media.EXTERNAL_CONTENT_URI;
          ContentResolver resolver = this.getContentResolver();
          Uri insertUri = resolver.insert(uri, values);
          this.mTakePictureUri = insertUri;
          intent.putExtra("output", insertUri);
        } else {
          name = System.currentTimeMillis() + ".jpg";
          File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
          if (!path.exists()) {
            path.mkdirs();
          }

          File file = new File(path, name);
          this.mTakePictureUri = Uri.fromFile(file);

          try {
            uri = FileProvider.getUriForFile(this, this.getPackageName() + this.getString(string.rc_authorities_fileprovider), file);
          } catch (Exception var10) {
            RLog.e(TAG, "requestCamera", var10);
            throw new RuntimeException("Please check IMKit Manifest FileProvider config. Please refer to http://support.rongcloud.cn/kb/NzA1");
          }

          Iterator var12 = resInfoList.iterator();

          while(var12.hasNext()) {
            ResolveInfo resolveInfo = (ResolveInfo)var12.next();
            String packageName = resolveInfo.activityInfo.packageName;
            this.grantUriPermission(packageName, uri, 2);
            this.grantUriPermission(packageName, uri, 1);
          }

          intent.putExtra("output", uri);
        }

        this.startActivityForResult(intent, 1);
      }
    }
  }

  private void updatePictureItems(final PictureSelectorActivity.IExecutedCallback iExecutedCallback) {
    this.bgHandler.post(new Runnable() {
      public void run() {
        String[] projection = new String[]{"_id", "_data", "date_added", "media_type", "mime_type", "title", "duration"};
        Class clazz = null;

        try {
          clazz = Class.forName("io.rong.sight.SightExtensionModule");
        } catch (ClassNotFoundException var13) {
          RLog.e(PictureSelectorActivity.TAG, "updatePictureItems", var13);
        }

        String selection;
        if (clazz == null) {
          selection = "media_type=1";
        } else if (PictureSelectorActivity.this.getResources().getBoolean(bool.rc_media_selector_contain_video)) {
          selection = "media_type=1 OR media_type=3";
        } else {
          selection = "media_type=1";
        }

        Uri queryUri = Files.getContentUri("external");
        CursorLoader cursorLoader = new CursorLoader(PictureSelectorActivity.this, queryUri, projection, selection, (String[])null, "date_added DESC");
        Cursor cursor = cursorLoader.loadInBackground();
        PictureSelectorActivity.this.mAllItemList = new ArrayList();
        PictureSelectorActivity.this.mCatalogList = new ArrayList();
        PictureSelectorActivity.this.mAllSelectedItemList = new ArrayList();
        PictureSelectorActivity.PicItemHolder.itemAllSelectedMediaItemList = new ArrayList();
        PictureSelectorActivity.this.mItemMap = new ArrayMap();
        if (cursor != null) {
          if (cursor.moveToFirst()) {
            do {
              PictureSelectorActivity.MediaItem item = new PictureSelectorActivity.MediaItem();
              item.name = cursor.getString(5);
              item.mediaType = cursor.getInt(3);
              item.mimeType = cursor.getString(4);
              item.uri = cursor.getString(1);
              item.duration = cursor.getInt(6);
              Uri imageUri = ContentUris.withAppendedId(queryUri, cursor.getLong(0));
              item.uri_sdk29 = imageUri.toString();
              if (item.uri != null && (item.mediaType != 3 || item.duration != 0) && (item.mediaType != 3 || "video/mp4".equals(item.mimeType))) {
                File file = new File(item.uri);
                if (file.exists() && file.length() != 0L) {
                  PictureSelectorActivity.this.mAllItemList.add(item);
                  int last = item.uri.lastIndexOf("/");
                  if (last != -1) {
                    String catalog;
                    if (last == 0) {
                      catalog = "/";
                    } else {
                      int secondLast = item.uri.lastIndexOf("/", last - 1);
                      catalog = item.uri.substring(secondLast + 1, last);
                    }

                    if (PictureSelectorActivity.this.mItemMap.containsKey(catalog)) {
                      ((List)PictureSelectorActivity.this.mItemMap.get(catalog)).add(item);
                    } else {
                      ArrayList<PictureSelectorActivity.MediaItem> itemList = new ArrayList();
                      itemList.add(item);
                      PictureSelectorActivity.this.mItemMap.put(catalog, itemList);
                      PictureSelectorActivity.this.mCatalogList.add(catalog);
                    }
                  }
                }
              }
            } while(cursor.moveToNext());
          }

          cursor.close();
          if (iExecutedCallback != null) {
            iExecutedCallback.executed();
          }
        }

      }
    });
  }

  private int getTotalSelectedNum() {
    int sum = 0;
    Iterator var2 = this.mItemMap.keySet().iterator();

    while(var2.hasNext()) {
      String key = (String)var2.next();
      Iterator var4 = ((List)this.mItemMap.get(key)).iterator();

      while(var4.hasNext()) {
        PictureSelectorActivity.MediaItem item = (PictureSelectorActivity.MediaItem)var4.next();
        if (item.selected) {
          ++sum;
        }
      }
    }

    return sum;
  }

  private void updateToolbar() {
    int sum = this.getTotalSelectedNum();
    if (sum == 0) {
      this.mBtnSend.setEnabled(false);
      this.mBtnSend.setTextColor(this.getResources().getColor(color.rc_picsel_toolbar_send_text_disable));
      this.mBtnSend.setText(string.rc_picsel_toolbar_send);
      this.mPreviewBtn.setEnabled(false);
      this.mPreviewBtn.setText(string.rc_picsel_toolbar_preview);
    } else if (sum <= 9) {
      this.mBtnSend.setEnabled(true);
      this.mBtnSend.setTextColor(this.getResources().getColor(color.rc_picsel_toolbar_send_text_normal));
      this.mBtnSend.setText(String.format(this.getResources().getString(string.rc_picsel_toolbar_send_num), sum));
      this.mPreviewBtn.setEnabled(true);
      this.mPreviewBtn.setText(String.format(this.getResources().getString(string.rc_picsel_toolbar_preview_num), sum));
    }

  }

  private PictureSelectorActivity.MediaItem getItemAt(int index) {
    int sum = 0;
    Iterator var3 = this.mItemMap.keySet().iterator();

    while(var3.hasNext()) {
      String key = (String)var3.next();

      for(Iterator var5 = ((List)this.mItemMap.get(key)).iterator(); var5.hasNext(); ++sum) {
        PictureSelectorActivity.MediaItem item = (PictureSelectorActivity.MediaItem)var5.next();
        if (sum == index) {
          return item;
        }
      }
    }

    return null;
  }

  private PictureSelectorActivity.MediaItem getItemAt(String catalog, int index) {
    if (!this.mItemMap.containsKey(catalog)) {
      return null;
    } else {
      int sum = 0;

      for(Iterator var4 = ((List)this.mItemMap.get(catalog)).iterator(); var4.hasNext(); ++sum) {
        PictureSelectorActivity.MediaItem item = (PictureSelectorActivity.MediaItem)var4.next();
        if (sum == index) {
          return item;
        }
      }

      return null;
    }
  }

  private PictureSelectorActivity.MediaItem findByUri(String uri) {
    Iterator var2 = this.mItemMap.keySet().iterator();

    while(var2.hasNext()) {
      String key = (String)var2.next();
      Iterator var4 = ((List)this.mItemMap.get(key)).iterator();

      while(var4.hasNext()) {
        PictureSelectorActivity.MediaItem item = (PictureSelectorActivity.MediaItem)var4.next();
        if (item.uri.equals(uri)) {
          return item;
        }
      }
    }

    return null;
  }

  private void setImageViewBackground(String imagePath, ImageView imageView, int position) {
    Bitmap bitmap = AlbumBitmapCacheHelper.getInstance().getBitmap(imagePath, this.perWidth, this.perHeight, new ILoadImageCallback() {
      public void onLoadImageCallBack(Bitmap bitmap, String path1, Object... objects) {
        if (bitmap != null) {
          BitmapDrawable bd = new BitmapDrawable(PictureSelectorActivity.this.getResources(), bitmap);
          View v = PictureSelectorActivity.this.mGridView.findViewWithTag(path1);
          if (v != null) {
            v.setBackgroundDrawable(bd);
          }

        }
      }
    }, new Object[]{position});
    if (bitmap != null) {
      BitmapDrawable bd = new BitmapDrawable(this.getResources(), bitmap);
      imageView.setBackgroundDrawable(bd);
    } else {
      imageView.setBackgroundResource(drawable.rc_grid_image_default);
    }

  }

  public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
    switch(requestCode) {
      case 100:
        if (grantResults[0] == 0) {
          if (permissions[0].equals("android.permission.READ_EXTERNAL_STORAGE")) {
            this.initView();
          } else if (permissions[0].equals("android.permission.CAMERA")) {
            this.requestCamera();
          }
        } else if (permissions[0].equals("android.permission.CAMERA")) {
          Toast.makeText(this.getApplicationContext(), this.getString(string.rc_permission_grant_needed), 0).show();
        } else {
          Toast.makeText(this.getApplicationContext(), this.getString(string.rc_permission_grant_needed), 0).show();
          this.finish();
        }
        break;
      default:
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

  }

  protected void onDestroy() {
    PictureSelectorActivity.PicItemHolder.itemList = null;
    PictureSelectorActivity.PicItemHolder.itemSelectedList = null;
    PictureSelectorActivity.PicItemHolder.itemAllSelectedMediaItemList = null;
    this.thread.quit();
    this.bgHandler.removeCallbacks(this.thread);
    this.bgHandler = null;
    this.uiHandler = null;
    super.onDestroy();
  }

  private void shutdownAndAwaitTermination(ExecutorService pool) {
    pool.shutdown();

    try {
      if (!pool.awaitTermination(60L, TimeUnit.SECONDS)) {
        pool.shutdownNow();
        if (!pool.awaitTermination(60L, TimeUnit.SECONDS)) {
          System.err.println("Pool did not terminate");
        }
      }
    } catch (InterruptedException var3) {
      pool.shutdownNow();
      Thread.currentThread().interrupt();
    }

  }

  private String formatSize(long length) {
    float size;
    if (length > 1048576L) {
      size = (float)Math.round((float)length / 1048576.0F * 100.0F) / 100.0F;
      return size + "M";
    } else if (length > 1024L) {
      size = (float)Math.round((float)length / 1024.0F * 100.0F) / 100.0F;
      return size + "KB";
    } else {
      return length + "B";
    }
  }

  static class PicItemHolder {
    static ArrayList<PictureSelectorActivity.MediaItem> itemList;
    static ArrayList<PictureSelectorActivity.MediaItem> itemSelectedList;
    static ArrayList<PictureSelectorActivity.MediaItem> itemAllSelectedMediaItemList;

    PicItemHolder() {
    }
  }

  public static class SelectBox extends ImageView {
    private boolean mIsChecked;

    public SelectBox(Context context, AttributeSet attrs) {
      super(context, attrs);
      this.setImageResource(drawable.rc_select_check_nor);
    }

    public void setChecked(boolean check) {
      this.mIsChecked = check;
      this.setImageResource(this.mIsChecked ? drawable.rc_select_check_sel : drawable.rc_select_check_nor);
    }

    public boolean getChecked() {
      return this.mIsChecked;
    }
  }

  public static class PreviewBtn extends LinearLayout {
    private TextView mText;

    public PreviewBtn(Context context, AttributeSet attrs) {
      super(context, attrs);
    }

    public void init(Activity root) {
      this.mText = (TextView)root.findViewById(id.preview_text);
    }

    public void setText(int id) {
      this.mText.setText(id);
    }

    public void setText(String text) {
      this.mText.setText(text);
    }

    public void setEnabled(boolean enabled) {
      super.setEnabled(enabled);
      int color = enabled ? R.color.rc_picsel_toolbar_send_text_normal : R.color.rc_picsel_toolbar_send_text_disable;
      this.mText.setTextColor(this.getResources().getColor(color));
    }

    public boolean onTouchEvent(MotionEvent event) {
      if (this.isEnabled()) {
        switch(event.getAction()) {
          case 0:
            this.mText.setVisibility(4);
            break;
          case 1:
            this.mText.setVisibility(0);
        }
      }

      return super.onTouchEvent(event);
    }
  }

  public static class PicTypeBtn extends LinearLayout {
    TextView mText;

    public PicTypeBtn(Context context, AttributeSet attrs) {
      super(context, attrs);
    }

    public void init(Activity root) {
      this.mText = (TextView)root.findViewById(id.type_text);
    }

    public void setText(String text) {
      this.mText.setText(text);
    }

    public void setTextColor(int color) {
      this.mText.setTextColor(color);
    }

    public boolean onTouchEvent(MotionEvent event) {
      if (this.isEnabled()) {
        switch(event.getAction()) {
          case 0:
            this.mText.setVisibility(4);
            break;
          case 1:
            this.mText.setVisibility(0);
        }
      }

      return super.onTouchEvent(event);
    }
  }

  public static class MediaItem implements Parcelable {
    String name;
    int mediaType;
    String mimeType;
    String uri;
    boolean selected;
    int duration;
    String uri_sdk29;
    public static final Creator<PictureSelectorActivity.MediaItem> CREATOR = new Creator<PictureSelectorActivity.MediaItem>() {
      public PictureSelectorActivity.MediaItem createFromParcel(Parcel source) {
        return new PictureSelectorActivity.MediaItem(source);
      }

      public PictureSelectorActivity.MediaItem[] newArray(int size) {
        return new PictureSelectorActivity.MediaItem[size];
      }
    };

    public int describeContents() {
      return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
      dest.writeString(this.name);
      dest.writeInt(this.mediaType);
      dest.writeString(this.mimeType);
      dest.writeString(this.uri);
      dest.writeByte((byte)(this.selected ? 1 : 0));
      dest.writeInt(this.duration);
      dest.writeString(this.uri_sdk29);
    }

    public MediaItem() {
    }

    protected MediaItem(Parcel in) {
      this.name = in.readString();
      this.mediaType = in.readInt();
      this.mimeType = in.readString();
      this.uri = in.readString();
      this.selected = in.readByte() != 0;
      this.duration = in.readInt();
      this.uri_sdk29 = in.readString();
    }
  }

  private class CatalogAdapter extends BaseAdapter {
    private LayoutInflater mInflater = PictureSelectorActivity.this.getLayoutInflater();

    public CatalogAdapter() {
    }

    public int getCount() {
      return PictureSelectorActivity.this.mItemMap.size() + 1;
    }

    public Object getItem(int position) {
      return null;
    }

    public long getItemId(int position) {
      return (long)position;
    }

    public View getView(final int position, View convertView, ViewGroup parent) {
      View view = convertView;
      final PictureSelectorActivity.CatalogAdapter.ViewHolder holder;
      if (convertView == null) {
        view = this.mInflater.inflate(layout.rc_picsel_catalog_listview, parent, false);
        holder = new PictureSelectorActivity.CatalogAdapter.ViewHolder();
        holder.image = (ImageView)view.findViewById(id.image);
        holder.name = (TextView)view.findViewById(id.name);
        holder.number = (TextView)view.findViewById(id.number);
        holder.selected = (ImageView)view.findViewById(id.selected);
        view.setTag(holder);
      } else {
        holder = (PictureSelectorActivity.CatalogAdapter.ViewHolder)convertView.getTag();
      }

      String path;
      if (holder.image.getTag() != null) {
        path = (String)holder.image.getTag();
        AlbumBitmapCacheHelper.getInstance().removePathFromShowlist(path);
      }

      int num = 0;
      boolean showSelected = false;
      String name;
      final PictureSelectorActivity.MediaItem mediaItem;
      if (position == 0) {
        if (PictureSelectorActivity.this.mItemMap.size() == 0) {
          holder.image.setImageResource(drawable.rc_picsel_empty_pic);
        } else {
          mediaItem = (PictureSelectorActivity.MediaItem)((List)PictureSelectorActivity.this.mItemMap.get(PictureSelectorActivity.this.mCatalogList.get(0))).get(0);
          if (mediaItem.mediaType == 1) {
            if (KitStorageUtils.isBuildAndTargetForQ(PictureSelectorActivity.this)) {
              path = mediaItem.uri_sdk29;
            } else {
              path = mediaItem.uri;
            }
          } else {
            path = KitStorageUtils.getImageSavePath(PictureSelectorActivity.this) + File.separator + mediaItem.name;
            if (!(new File(path)).exists()) {
              (new Thread(new Runnable() {
                @RequiresApi(
                        api = 28
                )
                public void run() {
                  Bitmap videoFrame = null;
                  if (KitStorageUtils.isBuildAndTargetForQ(PictureSelectorActivity.this)) {
                    try {
                      ParcelFileDescriptor pfd = PictureSelectorActivity.this.getApplicationContext().getContentResolver().openFileDescriptor(Uri.parse(mediaItem.uri_sdk29), "r");
                      MediaMetadataRetriever retriever = new MediaMetadataRetriever();
                      retriever.setDataSource(pfd.getFileDescriptor());
                      videoFrame = retriever.getFrameAtIndex(0);
                    } catch (IOException var4) {
                      RLog.e(PictureSelectorActivity.TAG, "CatalogAdapter getView:", var4);
                    }
                  } else {
                    videoFrame = ThumbnailUtils.createVideoThumbnail(mediaItem.uri, 1);
                  }

                  if (videoFrame != null) {
                    final File captureImageFile = FileUtils.convertBitmap2File(videoFrame, KitStorageUtils.getImageSavePath(PictureSelectorActivity.this), mediaItem.name);
                    PictureSelectorActivity.this.runOnUiThread(new Runnable() {
                      public void run() {
                        PictureSelectorActivity.this.setImageViewBackground(captureImageFile.getAbsolutePath(), holder.image, position);
                      }
                    });
                  }

                }
              })).start();
            }
          }

          AlbumBitmapCacheHelper.getInstance().addPathToShowlist(path);
          holder.image.setTag(path);
          PictureSelectorActivity.this.setImageViewBackground(path, holder.image, position);
        }

        name = PictureSelectorActivity.this.getResources().getString(string.rc_picsel_catalog_allpic);
        holder.number.setVisibility(8);
        showSelected = PictureSelectorActivity.this.mCurrentCatalog.isEmpty();
      } else {
        mediaItem = (PictureSelectorActivity.MediaItem)((List)PictureSelectorActivity.this.mItemMap.get(PictureSelectorActivity.this.mCatalogList.get(position - 1))).get(0);
        if (mediaItem.mediaType == 1) {
          if (KitStorageUtils.isBuildAndTargetForQ(PictureSelectorActivity.this)) {
            path = mediaItem.uri_sdk29;
          } else {
            path = mediaItem.uri;
          }
        } else {
          path = KitStorageUtils.getImageSavePath(PictureSelectorActivity.this) + File.separator + mediaItem.name;
          if (!(new File(path)).exists()) {
            (new Thread(new Runnable() {
              @RequiresApi(
                      api = 28
              )
              public void run() {
                Bitmap videoFrame = null;
                if (KitStorageUtils.isBuildAndTargetForQ(PictureSelectorActivity.this)) {
                  try {
                    ParcelFileDescriptor pfd = PictureSelectorActivity.this.getApplicationContext().getContentResolver().openFileDescriptor(Uri.parse(mediaItem.uri_sdk29), "r");
                    MediaMetadataRetriever retriever = new MediaMetadataRetriever();
                    retriever.setDataSource(pfd.getFileDescriptor());
                    videoFrame = retriever.getFrameAtIndex(0);
                  } catch (IOException var4) {
                    RLog.e(PictureSelectorActivity.TAG, "CatalogAdapter getView:", var4);
                  }
                } else {
                  videoFrame = ThumbnailUtils.createVideoThumbnail(mediaItem.uri, 1);
                }

                if (videoFrame != null) {
                  final File captureImageFile = FileUtils.convertBitmap2File(videoFrame, KitStorageUtils.getImageSavePath(PictureSelectorActivity.this), mediaItem.name);
                  PictureSelectorActivity.this.runOnUiThread(new Runnable() {
                    public void run() {
                      PictureSelectorActivity.this.setImageViewBackground(captureImageFile.getAbsolutePath(), holder.image, position);
                    }
                  });
                }

              }
            })).start();
          }
        }

        name = (String)PictureSelectorActivity.this.mCatalogList.get(position - 1);
        num = ((List)PictureSelectorActivity.this.mItemMap.get(PictureSelectorActivity.this.mCatalogList.get(position - 1))).size();
        holder.number.setVisibility(0);
        showSelected = name.equals(PictureSelectorActivity.this.mCurrentCatalog);
        AlbumBitmapCacheHelper.getInstance().addPathToShowlist(path);
        holder.image.setTag(path);
        PictureSelectorActivity.this.setImageViewBackground(path, holder.image, position);
      }

      holder.name.setText(name);
      holder.number.setText(String.format(PictureSelectorActivity.this.getResources().getString(string.rc_picsel_catalog_number), num));
      holder.selected.setVisibility(showSelected ? 0 : 4);
      return view;
    }

    private class ViewHolder {
      ImageView image;
      TextView name;
      TextView number;
      ImageView selected;

      private ViewHolder() {
      }
    }
  }

  private class GridViewAdapter extends BaseAdapter {
    private LayoutInflater mInflater = PictureSelectorActivity.this.getLayoutInflater();

    public GridViewAdapter() {
    }

    public int getCount() {
      int sum = 1;
      String key;
      if (PictureSelectorActivity.this.mCurrentCatalog.isEmpty()) {
        for(Iterator var2 = PictureSelectorActivity.this.mItemMap.keySet().iterator(); var2.hasNext(); sum += ((List)PictureSelectorActivity.this.mItemMap.get(key)).size()) {
          key = (String)var2.next();
        }
      } else {
        sum += ((List)PictureSelectorActivity.this.mItemMap.get(PictureSelectorActivity.this.mCurrentCatalog)).size();
      }

      return sum;
    }

    public Object getItem(int position) {
      return null;
    }

    public long getItemId(int position) {
      return (long)position;
    }

    @TargetApi(23)
    public View getView(final int position, View convertView, ViewGroup parent) {
      if (position == 0) {
        View view = this.mInflater.inflate(layout.rc_picsel_grid_camera, parent, false);
        ImageButton mask = (ImageButton)view.findViewById(id.camera_mask);
        mask.setOnClickListener(new OnClickListener() {
          public void onClick(View v) {
            String[] permissions = new String[]{"android.permission.CAMERA"};
            if (!PermissionCheckUtil.checkPermissions(PictureSelectorActivity.this, permissions)) {
              PermissionCheckUtil.requestPermissions(PictureSelectorActivity.this, permissions, 100);
            } else {
              PictureSelectorActivity.this.requestCamera();
            }
          }
        });
        return view;
      } else {
        final PictureSelectorActivity.MediaItem item;
        if (PictureSelectorActivity.this.mCurrentCatalog.isEmpty()) {
          item = (PictureSelectorActivity.MediaItem)PictureSelectorActivity.this.mAllItemList.get(position - 1);
        } else {
          item = PictureSelectorActivity.this.getItemAt(PictureSelectorActivity.this.mCurrentCatalog, position - 1);
        }

        View viewx = convertView;
        final PictureSelectorActivity.GridViewAdapter.ViewHolder holder;
        if (convertView != null && convertView.getTag() != null) {
          holder = (PictureSelectorActivity.GridViewAdapter.ViewHolder)convertView.getTag();
        } else {
          viewx = this.mInflater.inflate(layout.rc_picsel_grid_item, parent, false);
          holder = new PictureSelectorActivity.GridViewAdapter.ViewHolder();
          holder.image = (ImageView)viewx.findViewById(id.image);
          holder.mask = viewx.findViewById(id.mask);
          holder.checkBox = (PictureSelectorActivity.SelectBox)viewx.findViewById(id.checkbox);
          holder.videoContainer = viewx.findViewById(id.video_container);
          holder.videoDuration = (TextView)viewx.findViewById(id.video_duration);
          viewx.setTag(holder);
        }

        String thumbImagePath;
        if (holder.image.getTag() != null) {
          thumbImagePath = (String)holder.image.getTag();
          AlbumBitmapCacheHelper.getInstance().removePathFromShowlist(thumbImagePath);
        }

        thumbImagePath = "";
        if (item == null) {
          return viewx;
        } else {
          switch(item.mediaType) {
            case 1:
              if (KitStorageUtils.isBuildAndTargetForQ(PictureSelectorActivity.this)) {
                thumbImagePath = item.uri_sdk29;
              } else {
                thumbImagePath = item.uri;
              }
              break;
            case 3:
              thumbImagePath = KitStorageUtils.getImageSavePath(PictureSelectorActivity.this) + File.separator + item.name;
              if (!(new File(thumbImagePath)).exists()) {
                Runnable runnable = new Runnable() {
                  @RequiresApi(
                          api = 28
                  )
                  public void run() {
                    Bitmap videoFrame = null;
                    if (KitStorageUtils.isBuildAndTargetForQ(PictureSelectorActivity.this)) {
                      try {
                        ParcelFileDescriptor pfd = PictureSelectorActivity.this.getApplicationContext().getContentResolver().openFileDescriptor(Uri.parse(item.uri_sdk29), "r");
                        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
                        retriever.setDataSource(pfd.getFileDescriptor());
                        videoFrame = retriever.getFrameAtIndex(0);
                      } catch (IOException var4) {
                        RLog.e(PictureSelectorActivity.TAG, "GridViewAdapter getView:", var4);
                      }
                    } else {
                      videoFrame = ThumbnailUtils.createVideoThumbnail(item.uri, 1);
                    }

                    if (videoFrame != null) {
                      final File captureImageFile = FileUtils.convertBitmap2File(videoFrame, KitStorageUtils.getImageSavePath(PictureSelectorActivity.this), item.name);
                      PictureSelectorActivity.this.runOnUiThread(new Runnable() {
                        public void run() {
                          PictureSelectorActivity.this.setImageViewBackground(captureImageFile.getAbsolutePath(), holder.image, position);
                        }
                      });
                    }

                  }
                };
                PictureSelectorActivity.this.pool.execute(runnable);
              }
          }

          AlbumBitmapCacheHelper.getInstance().addPathToShowlist(thumbImagePath);
          holder.image.setTag(thumbImagePath);
          PictureSelectorActivity.this.setImageViewBackground(thumbImagePath, holder.image, position);
          if (item.mediaType == 3) {
            holder.videoContainer.setVisibility(0);
            long minutes = TimeUnit.MILLISECONDS.toMinutes((long)item.duration);
            long seconds = TimeUnit.MILLISECONDS.toSeconds((long)item.duration) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long)item.duration));
            holder.videoDuration.setText(String.format(Locale.CHINA, seconds < 10L ? "%d:0%d" : "%d:%d", minutes, seconds));
          } else {
            holder.videoContainer.setVisibility(8);
          }

          holder.checkBox.setChecked(item.selected);
          holder.checkBox.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
              int maxDuration;
              if (item.mediaType == 3) {
                if (TextUtils.isEmpty(holder.videoDuration.getText())) {
                  return;
                }

                maxDuration = RongIMClient.getInstance().getVideoLimitTime();
                if (maxDuration < 1) {
                  maxDuration = 300;
                }

                String[] videoTime = holder.videoDuration.getText().toString().split(":");
                if (Integer.parseInt(videoTime[0]) * 60 + Integer.parseInt(videoTime[1]) > maxDuration) {
                  (new Builder(PictureSelectorActivity.this)).setMessage(PictureSelectorActivity.this.getResources().getString(string.rc_picsel_selected_max_time_span_with_param, new Object[]{maxDuration / 60})).setPositiveButton(string.rc_confirm, (android.content.DialogInterface.OnClickListener)null).setCancelable(false).create().show();
                  return;
                }
              }

              if (item.uri.endsWith(".gif")) {
                maxDuration = RongIMClient.getInstance().getGIFLimitSize() * 1024;
                File file = new File(item.uri);
                if (file != null && file.exists() && file.length() > (long)maxDuration) {
                  (new Builder(PictureSelectorActivity.this)).setMessage(PictureSelectorActivity.this.getResources().getString(string.rc_picsel_selected_max_gif_size_span_with_param)).setPositiveButton(string.rc_confirm, (android.content.DialogInterface.OnClickListener)null).setCancelable(false).create().show();
                  return;
                }
              }

              if (!holder.checkBox.getChecked() && PictureSelectorActivity.this.getTotalSelectedNum() == 9) {
                Toast.makeText(PictureSelectorActivity.this, string.rc_picsel_selected_max_pic_count, 0).show();
              } else {
                holder.checkBox.setChecked(!holder.checkBox.getChecked());
                item.selected = holder.checkBox.getChecked();
                if (item.selected) {
                  PictureSelectorActivity.this.mAllSelectedItemList.add(Uri.parse("file://" + item.uri));
                  PictureSelectorActivity.PicItemHolder.itemAllSelectedMediaItemList.add(item);
                  holder.mask.setBackgroundColor(PictureSelectorActivity.this.getResources().getColor(color.rc_picsel_grid_mask_pressed));
                } else {
                  try {
                    PictureSelectorActivity.this.mAllSelectedItemList.remove(Uri.parse("file://" + item.uri));
                  } catch (Exception var4) {
                    RLog.e(PictureSelectorActivity.TAG, "GridViewAdapter getView", var4);
                  }

                  PictureSelectorActivity.PicItemHolder.itemAllSelectedMediaItemList.remove(item);
                  holder.mask.setBackgroundDrawable(PictureSelectorActivity.this.getResources().getDrawable(drawable.rc_sp_grid_mask));
                }

                PictureSelectorActivity.this.updateToolbar();
              }
            }
          });
          if (item.selected) {
            holder.mask.setBackgroundColor(PictureSelectorActivity.this.getResources().getColor(color.rc_picsel_grid_mask_pressed));
          } else {
            holder.mask.setBackgroundDrawable(PictureSelectorActivity.this.getResources().getDrawable(drawable.rc_sp_grid_mask));
          }

          return viewx;
        }
      }
    }

    private class ViewHolder {
      ImageView image;
      View mask;
      PictureSelectorActivity.SelectBox checkBox;
      View videoContainer;
      TextView videoDuration;

      private ViewHolder() {
      }
    }
  }

  interface IExecutedCallback {
    void executed();
  }
}

//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package io.rong.imkit.utilities;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.net.Uri;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore.Images.Media;
import android.text.TextUtils;

import androidx.annotation.StringRes;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.FileChannel;

import io.rong.common.LibStorageUtils;
import io.rong.common.RLog;
import io.rong.imkit.R.string;
import io.rong.imlib.common.SavePathUtils;

public class KitStorageUtils {
    private static final String TAG = "LibStorageUtils";

    public KitStorageUtils() {
    }

    public static boolean isQMode(Context context) {
        return LibStorageUtils.isQMode(context);
    }

    public static boolean isBuildAndTargetForQ(Context context) {
        return LibStorageUtils.isBuildAndTargetForQ(context);
    }

    public static String getImageSavePath(Context context) {
        return getSavePath(context, "image", string.rc_image_default_saved_path);
    }

    public static String getVideoSavePath(Context context) {
        return getSavePath(context, "video", string.rc_video_default_saved_path);
    }

    public static String getFileSavePath(Context context) {
        return getSavePath(context, "file", string.rc_file_default_saved_path);
    }

    public static String getSavePath(Context context, String type, @StringRes int res) {
        if (!SavePathUtils.isSavePathEmpty()) {
            String savePath = SavePathUtils.getSavePath();
            File imageDir = new File(savePath, type);
            if (!imageDir.exists() && !imageDir.mkdirs()) {
                RLog.e("LibStorageUtils", "getSavePath mkdirs error path is  " + imageDir.getAbsolutePath());
            }

            return imageDir.getAbsolutePath();
        } else {
            boolean sdCardExist = Environment.getExternalStorageState().equals("mounted");
            String result = context.getCacheDir().getPath();
            if (!sdCardExist) {
                RLog.d("LibStorageUtils", "getSavePath error, sdcard does not exist.");
                return result;
            } else {
                if (isQMode(context)) {
                    File path = context.getExternalFilesDir("RongCloud");
                    File file = new File(path, type);
                    if (!file.exists() && !file.mkdirs()) {
                        result = path.getPath();
                    } else {
                        result = file.getPath();
                    }
                } else {
                    String path = Environment.getExternalStorageDirectory().getPath();
                    String defaultPath = context.getString(res);
                    StringBuilder builder = new StringBuilder(defaultPath);
                    String appName = LibStorageUtils.getAppName(context);
                    if (!TextUtils.isEmpty(appName)) {
                        builder.append(appName).append(File.separator);
                    }

                    String appPath = builder.toString();
                    path = path + appPath;
                    File dir = new File(path);
                    if (!dir.exists() && !dir.mkdirs()) {
                        RLog.e("LibStorageUtils", "mkdirs error path is  " + path);
                        result = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath();
                    } else {
                        result = path;
                    }
                }

                return result;
            }
        }
    }

    private static boolean copyVideoToPublicDir(Context context, File file) {
        if (file != null && file.exists()) {
            boolean result = true;
            if (!isBuildAndTargetForQ(context)) {
                File dirFile = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES);
                if (dirFile != null && !dirFile.exists()) {
                    boolean mkdirResult = dirFile.mkdirs();
                    if (!mkdirResult) {
                        return false;
                    }
                }

                if (dirFile == null) {
                    RLog.e("LibStorageUtils", "dirFile is null");
                    return false;
                }

                FileInputStream fis = null;
                FileOutputStream fos = null;

                try {
                    String filePath = dirFile.getPath() + "/" + file.getName();
                    fis = new FileInputStream(file);
                    fos = new FileOutputStream(filePath);
                    copy(fis, fos);
                    File destFile = new File(filePath);
                    updatePhotoMedia(destFile, context);
                } catch (FileNotFoundException var20) {
                    result = false;
                    RLog.e("LibStorageUtils", "copyVideoToPublicDir file not found", var20);
                } finally {
                    try {
                        if (fis != null) {
                            fis.close();
                        }
                    } catch (IOException var19) {
                        RLog.e("LibStorageUtils", "copyVideoToPublicDir: ", var19);
                    }

                    try {
                        if (fos != null) {
                            fos.close();
                        }
                    } catch (IOException var18) {
                        RLog.e("LibStorageUtils", "copyVideoToPublicDir: ", var18);
                    }

                }
            } else {
                result = copyVideoToPublicDirForQ(context, file);
            }

            return result;
        } else {
            RLog.e("LibStorageUtils", "file is not exist");
            return false;
        }
    }

    public static void updatePhotoMedia(File file, Context context) {
        if (file != null && file.exists()) {
            Intent intent = new Intent();
            intent.setAction("android.intent.action.MEDIA_SCANNER_SCAN_FILE");
            intent.setData(Uri.fromFile(file));
            context.sendBroadcast(intent);
        }

    }

    private static boolean copyVideoToPublicDirForQ(Context context, File file) {
        boolean result = true;
        String filePath = "";
        if (file.exists() && file.isFile() && context != null) {
            Uri uri = insertVideoIntoMediaStore(context, file);
            if (uri != null) {
                filePath = uri.getPath();
            }

            try {
                ParcelFileDescriptor w = context.getContentResolver().openFileDescriptor(uri, "w");
                writeToPublicDir(file, w);
            } catch (FileNotFoundException var6) {
                RLog.e("LibStorageUtils", "copyVideoToPublicDir uri is not Found, uri is" + uri.toString());
                result = false;
            }

            File destFile = new File(filePath);
            updatePhotoMedia(destFile, context);
        } else {
            RLog.e("LibStorageUtils", "file is not Found or context is null ");
            result = false;
        }

        return result;
    }

    private static boolean copyImageToPublicDir(Context pContext, File pFile) {
        boolean result = true;
        File file = pFile;
        if (pFile.exists() && pFile.isFile() && pContext != null) {
            String imgMimeType = getImgMimeType(pFile);
            Uri uri = insertImageIntoMediaStore(pContext, pFile.getName(), imgMimeType);

            try {
                ParcelFileDescriptor w = pContext.getContentResolver().openFileDescriptor(uri, "w");
                writeToPublicDir(file, w);
            } catch (FileNotFoundException var7) {
                result = false;
                RLog.e("LibStorageUtils", "copyImageToPublicDir uri is not Found, uri is" + uri.toString());
            }
        } else {
            result = false;
            RLog.e("LibStorageUtils", "file is not Found or context is null ");
        }

        return result;
    }

    public static Uri insertImageIntoMediaStore(Context context, String fileName, String mimeType) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("_display_name", fileName);
        contentValues.put("datetaken", System.currentTimeMillis());
        contentValues.put("mime_type", mimeType);
        Uri uri = context.getContentResolver().insert(Media.EXTERNAL_CONTENT_URI, contentValues);
        return uri;
    }

    public static Uri insertVideoIntoMediaStore(Context context, File file) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("_display_name", file.getName());
        contentValues.put("datetaken", System.currentTimeMillis());
        contentValues.put("mime_type", "video/mp4");
        Uri uri = context.getContentResolver().insert(android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI, contentValues);
        return uri;
    }

    public static void writeToPublicDir(File pFile, ParcelFileDescriptor pParcelFileDescriptor) {
        FileInputStream fis = null;
        FileOutputStream fos = null;

        try {
            fis = new FileInputStream(pFile);
            fos = new FileOutputStream(pParcelFileDescriptor.getFileDescriptor());
            copy(fis, fos);
        } catch (FileNotFoundException var17) {
            RLog.e("LibStorageUtils", "writeToPublicDir file is not found file path is " + pFile.getAbsolutePath());
        } finally {
            try {
                if (fis != null) {
                    fis.close();
                }
            } catch (IOException var16) {
                RLog.e("LibStorageUtils", "writeToPublicDir: ", var16);
            }

            try {
                if (fos != null) {
                    fos.close();
                }
            } catch (IOException var15) {
                RLog.e("LibStorageUtils", "writeToPublicDir: ", var15);
            }

        }

    }

    public static void read(ParcelFileDescriptor parcelFileDescriptor, File dst) throws IOException {
        FileInputStream istream = new FileInputStream(parcelFileDescriptor.getFileDescriptor());

        try {
            FileOutputStream ostream = new FileOutputStream(dst);

            try {
                copy(istream, ostream);
            } finally {
                ostream.close();
            }
        } finally {
            istream.close();
        }

    }

    public static void copy(FileInputStream ist, FileOutputStream ost) {
        if (ist != null && ost != null) {
            FileChannel fileChannelInput = null;
            FileChannel fileChannelOutput = null;

            try {
                fileChannelInput = ist.getChannel();
                fileChannelOutput = ost.getChannel();
                fileChannelInput.transferTo(0L, fileChannelInput.size(), fileChannelOutput);
            } catch (IOException var13) {
                RLog.e("LibStorageUtils", "copy method error", var13);
            } finally {
                try {
                    ist.close();
                    if (fileChannelInput != null) {
                        fileChannelInput.close();
                    }

                    ost.close();
                    if (fileChannelOutput != null) {
                        fileChannelOutput.close();
                    }
                } catch (IOException var12) {
                    RLog.e("LibStorageUtils", "copy method error", var12);
                }

            }

        }
    }

    public static String getImgMimeType(File imgFile) {
        Options options = new Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imgFile.getPath(), options);
        return options.outMimeType;
    }

    public Uri getContentUri(int type, String id) {
        Uri uri;
        switch (type) {
            case 0:
                uri = Media.EXTERNAL_CONTENT_URI.buildUpon().appendPath(String.valueOf(id)).build();
                break;
            case 1:
                uri = android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI.buildUpon().appendPath(String.valueOf(id)).build();
                break;
            case 2:
                uri = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI.buildUpon().appendPath(String.valueOf(id)).build();
                break;
            default:
                uri = null;
        }

        return uri;
    }

    public InputStream getFileInputStreamWithUri(Context pContext, Uri pUri) {
        InputStream inputStream = null;
        ContentResolver cr = pContext.getContentResolver();

        try {
            AssetFileDescriptor r = cr.openAssetFileDescriptor(pUri, "r");
            ParcelFileDescriptor parcelFileDescriptor = r.getParcelFileDescriptor();
            if (parcelFileDescriptor != null) {
                inputStream = new FileInputStream(parcelFileDescriptor.getFileDescriptor());
            }
        } catch (FileNotFoundException var7) {
            RLog.e("LibStorageUtils", "getFileInputStreamWithUri: ", var7);
        }

        return inputStream;
    }

    public static boolean saveMediaToPublicDir(Context context, File file, String type) {
        if ("image".equals(type)) {
            return copyImageToPublicDir(context, file);
        } else if ("video".equals(type)) {
            return copyVideoToPublicDir(context, file);
        } else {
            RLog.i("LibStorageUtils", "type is error");
            return false;
        }
    }

    public static class MediaType {
        public static final String IMAGE = "image";
        public static final String VIDEO = "video";

        public MediaType() {
        }
    }
}
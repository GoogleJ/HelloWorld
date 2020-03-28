package com.zxjk.duoduo.utils;

import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.TextUtils;

import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;

import com.zxjk.duoduo.BuildConfig;

import java.io.File;
import java.util.List;

public class TakePicUtil {
    private static final int CODE_PICTURE = 101;
    private static final int CODE_ALBUM = 102;
    private static final int CODE_CORP = 103;
    private static final int CODE_PICTURE_NOCORP = 104;
    private static final int CODE_ALBUM_NOCORP = 105;
    private static Config config;

    private static File output = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Hilamg/portraits/" + "temp.png");

    public static void config(Config c) {
        config = c;
    }

    public static void takePicture(Activity activity) {
        takePicture(activity, true);
    }

    public static void albumPhoto(Activity activity) {
        albumPhoto(activity, true);
    }

    public static void takePicture(Activity activity, boolean corp) {
        if (hasSdcard()) {
            if (output.getParentFile() != null && !output.getParentFile().exists()) {
                output.getParentFile().mkdirs();
            }

            Intent intentCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            Uri imageUri = getUriForFile(activity, output);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                intentCamera.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            }
            intentCamera.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
            if (corp) {
                activity.startActivityForResult(intentCamera, CODE_PICTURE);
            } else {
                activity.startActivityForResult(intentCamera, CODE_PICTURE_NOCORP);
            }
        }
    }

    public static void albumPhoto(Activity activity, boolean corp) {
        if (output.getParentFile() != null && !output.getParentFile().exists()) {
            output.getParentFile().mkdirs();
        }
        Intent photoPickerIntent = new Intent(Intent.ACTION_GET_CONTENT);
        photoPickerIntent.setType("image/*");
        if (corp) {
            activity.startActivityForResult(photoPickerIntent, CODE_ALBUM);
        } else {
            activity.startActivityForResult(photoPickerIntent, CODE_ALBUM_NOCORP);
        }
    }

    private static boolean corp(Activity activity, File file) {
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        Uri uri = getUriForFile(activity, file);

        Intent cropIntent = new Intent("com.android.camera.action.CROP");

        cropIntent.setDataAndType(uri, "image/*");
        cropIntent.putExtra("crop", "true");
        cropIntent.putExtra("circleCrop", false);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            cropIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            cropIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        }
        if (config == null) {
            cropIntent.putExtra("aspectX", 1);
            cropIntent.putExtra("aspectY", 1);
        } else {
            if (config.circleCorp) {
                cropIntent.putExtra("circleCrop", true);
            }
            if (config.acceptX != 0) {
                cropIntent.putExtra("aspectX", config.acceptX);
                cropIntent.putExtra("aspectY", config.acceptY);
            }
            if (config.enableZipPixel) {
                cropIntent.putExtra("outputX", (config.acceptX * 400));
                cropIntent.putExtra("outputY", (config.acceptY * 400));
            }
        }
        config = null;

        cropIntent.putExtra("return-data", false);
        cropIntent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        cropIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(output));

        if (!isIntentAvailable(activity, cropIntent)) {
            return false;
        } else {
            try {
                activity.startActivityForResult(cropIntent, CODE_CORP);
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
    }

    public static File onActivityResult(Activity activity, int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode != Activity.RESULT_OK || ((requestCode == CODE_ALBUM || requestCode == CODE_ALBUM_NOCORP) && data == null)) {
            return null;
        }

        if (requestCode == CODE_PICTURE || requestCode == CODE_ALBUM) {
            String filePath = "";
            switch (requestCode) {
                case CODE_PICTURE:
                    filePath = output.getAbsolutePath();
                    break;
                case CODE_ALBUM:
                    filePath = getPath(activity, data.getData());
                    break;
            }

            if (TextUtils.isEmpty(filePath)) {
                return null;
            }

            corp(activity, new File(filePath));
            return null;
        }

        if (requestCode == CODE_CORP || requestCode == CODE_PICTURE_NOCORP) {
            return output;
        }

        if (requestCode == CODE_ALBUM_NOCORP) {
            String path = getPath(activity, data.getData());
            if (TextUtils.isEmpty(path)) {
                return null;
            }
            return new File(path);
        }

        return null;
    }

    private static boolean isIntentAvailable(Activity activity, Intent intent) {
        PackageManager packageManager = activity.getPackageManager();
        List<ResolveInfo> list = packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        return list.size() > 0;
    }

    private static Uri getUriForFile(Context context, File file) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".FileProvider", file);
        }
        return Uri.fromFile(file);
    }

    private static boolean hasSdcard() {
        String state = Environment.getExternalStorageState();
        return state.equals(Environment.MEDIA_MOUNTED);
    }

    public static String getPath(Context context, Uri uri) {
        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
        String pathHead = "";
        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];
                if ("primary".equalsIgnoreCase(type)) {
                    return pathHead + Environment.getExternalStorageDirectory() + "/" + split[1];
                }
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {
                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
                return pathHead + getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{split[1]};

                return pathHead + getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            return pathHead + getDataColumn(context, uri, null, null);
        }
        // File
        else if ("output".equalsIgnoreCase(uri.getScheme())) {
            return pathHead + uri.getPath();
        }
        return null;
    }

    private static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {
        final String column = "_data";
        final String[] projection = {column};
        try (Cursor cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null)) {
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        }
        return null;
    }

    private static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    private static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    private static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    public static class Config {
        private boolean enableZipPixel;
        private boolean circleCorp;
        private int acceptX = 1;
        private int acceptY = 1;

        public Config enableZipPixel() {
            enableZipPixel = true;
            return this;
        }

        public Config enableCircleCorp() {
            circleCorp = true;
            return this;
        }

        public Config rectParm(int acceptX, int acceptY) {
            this.acceptX = acceptX;
            this.acceptY = acceptY;
            return this;
        }

    }

}

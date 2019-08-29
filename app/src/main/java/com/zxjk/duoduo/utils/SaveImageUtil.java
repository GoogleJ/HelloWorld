package com.zxjk.duoduo.utils;

import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.os.Environment;

import com.blankj.utilcode.util.EncryptUtils;
import com.blankj.utilcode.util.Utils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.zxjk.duoduo.Constant;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class SaveImageUtil {
    private static final SaveImageUtil INSTANCE = new SaveImageUtil();

    private SaveImageUtil() {
    }

    private static File pictureFile;

    public interface SaveResultListener {
        void success(boolean success);
    }

    public static SaveImageUtil get() {
        if (pictureFile == null) {
            pictureFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/MoChat/images/");
            if (!pictureFile.exists()) {
                pictureFile.mkdirs();
            }
        }
        return INSTANCE;
    }

    public void savePic(Bitmap bitmap, SaveResultListener listener) {
        File dest = new File(pictureFile, System.currentTimeMillis() + ".jpg");
        FileOutputStream fileOutputStream = null;
        BufferedOutputStream bof = null;
        try {
            fileOutputStream = new FileOutputStream(dest);
            bof = new BufferedOutputStream(fileOutputStream);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bof);
            bof.flush();
            MediaScannerConnection.scanFile(Utils.getApp(), new String[]{dest.getAbsolutePath()}, new String[]{"image/jpeg"}, null);
            listener.success(true);
        } catch (Exception e) {
            e.printStackTrace();
            listener.success(false);
        } finally {
            if (fileOutputStream != null) {
                try {
                    fileOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (bof != null) {
                try {
                    bof.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void savePic(final String url, SaveResultListener listener) {
        Glide.with(Utils.getApp())
                .asBitmap()
                .load(url)
                .listener(new RequestListener<Bitmap>() {
                    @Override
                    public boolean onLoadFailed(GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                        listener.success(false);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
                        File dest = new File(pictureFile, EncryptUtils.encryptMD5ToString(url) + ".jpg");
                        FileOutputStream fileOutputStream = null;
                        BufferedOutputStream bof = null;
                        try {
                            fileOutputStream = new FileOutputStream(dest);
                            bof = new BufferedOutputStream(fileOutputStream);
                            resource.compress(Bitmap.CompressFormat.JPEG, 100, bof);
                            bof.flush();
                            MediaScannerConnection.scanFile(Utils.getApp(), new String[]{dest.getAbsolutePath()}, new String[]{"image/jpeg"}, null);
                            listener.success(true);
                        } catch (Exception e) {
                            e.printStackTrace();
                            listener.success(false);
                        } finally {
                            if (fileOutputStream != null) {
                                try {
                                    fileOutputStream.close();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                            if (bof != null) {
                                try {
                                    bof.close();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }

                        return false;
                    }
                })
                .submit();
    }
}

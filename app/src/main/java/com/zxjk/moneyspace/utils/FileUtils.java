package com.zxjk.moneyspace.utils;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;

import java.io.File;
import java.io.FileInputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FileUtils {
    /**
     * 转换文件大小
     *
     * @param fileS
     * @return
     */
    public static String FormatFileSize(long fileS) {
        DecimalFormat df = new DecimalFormat("#.00");
        String fileSizeString = "";
        String wrongSize = "0B";
        if (fileS == 0) {
            return wrongSize;
        }
        if (fileS < 1024) {
            fileSizeString = df.format((double) fileS) + "B";
        } else if (fileS < 1048576) {
            fileSizeString = df.format((double) fileS / 1024) + "KB";
        } else if (fileS < 1073741824) {
            fileSizeString = df.format((double) fileS / 1048576) + "MB";
        } else {
            fileSizeString = df.format((double) fileS / 1073741824) + "GB";
        }
        return fileSizeString;
    }

    /**
     * 将毫秒转特定时间格式
     *
     * @param time
     * @return
     */
    public static String LongTimeToStr(long time) {
        int m = 1000;
        int f = m * 60;
        int h = f * 60;
        Date date = new Date(time);
        if (time < h * f * m) {
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
            return sdf.format(date);
        } else {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
            return sdf.format(date);
        }
    }

    /**
     * 获取文件数量
     *
     * @param path
     * @return
     */
    public static int getFileNum(String path) {
        File file = new File(path);
        int fileNum = 0;
        File[] fileArray = file.listFiles();
        if (fileArray != null) {
            for (File f : fileArray) {
                if (f.isFile() || f.isDirectory() && !f.isHidden()) {
                    fileNum++;
                }
            }
        }
        return fileNum;
    }

    /**
     * 获取文件大小
     *
     * @param file
     * @return
     * @throws Exception
     */
    public static long getFileSize(File file) throws Exception {
        long size = 0;
        if (file.exists() && !file.isDirectory()) {
            FileInputStream fis = null;
            fis = new FileInputStream(file);
            size = fis.available();
        } else {
            file.createNewFile();

        }
        return size;
    }

    /**
     * 采用了新的办法获取APK图标，之前的失败是因为android中存在的一个BUG,通过
     * appInfo.publicSourceDir = apkPath;来修正这个问题，详情参见:
     * http://code.google.com/p/android/issues/detail?id=9151
     */
    public static Drawable getApkIcon(Context context, String apkPath) {
        PackageManager pm = context.getPackageManager();
        PackageInfo info = pm.getPackageArchiveInfo(apkPath,
                PackageManager.GET_ACTIVITIES);
        if (info != null) {
            ApplicationInfo appInfo = info.applicationInfo;
            appInfo.sourceDir = apkPath;
            appInfo.publicSourceDir = apkPath;
            try {
                return appInfo.loadIcon(pm);
            } catch (OutOfMemoryError e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static String getMIMEType(File f) {
        String type = "";
        String fileName = f.getName();
        String ext = fileName.substring(fileName.lastIndexOf(".")
                + 1, fileName.length()).toLowerCase();
        if (ext.equals("jpg") || ext.equals("jpeg") || ext.equals("gif")
                || ext.equals("png") || ext.equals("bmp") || ext.equals("wbmp")) {
            type = "image/*";
        } else if (ext.equals("mp3") || ext.equals("amr") || ext.equals("wma")
                || ext.equals("aac") || ext.equals("m4a") || ext.equals("mid")
                || ext.equals("xmf") || ext.equals("ogg") || ext.equals("wav")
                || ext.equals("qcp") || ext.equals("awb") || ext.equals("flac") || ext.equals("3gpp")) {
            type = "audio/*";
        } else if (ext.equals("3gp") || ext.equals("avi") || ext.equals("mp4")
                || ext.equals("3g2") || ext.equals("wmv") || ext.equals("divx")
                || ext.equals("mkv") || ext.equals("webm") || ext.equals("ts")
                || ext.equals("asf") || ext.equals("flv") || ext.equals("mov") || ext.equals("mpg")) {
            type = "video/*";
        } else if (ext.equals("apk")) {
            type = "application/vnd.android.package-archive";
        } else if (ext.equals("vcf")) {
            type = "text/x-vcard";
        } else if (ext.equals("txt")) {
            type = "text/plain";
        } else if (ext.equals("doc") || ext.equals("docx")) {
            type = "application/msword";
        } else if (ext.equals("xls") || ext.equals("xlsx")) {
            type = "application/vnd.ms-excel";
        } else if (ext.equals("ppt") || ext.equals("pptx")) {
            type = "application/vnd.ms-powerpoint";
        } else if (ext.equals("pdf")) {
            type = "application/pdf";
        } else if (ext.equals("xml")) {
            type = "text/xml";
        } else if (ext.equals("html")) {
            type = "text/html";
        } else if (ext.equals("zip")) {
            type = "application/zip";
        } else {
            type = "application/octet-stream";
        }
        return type;
    }

    public static String getExtFromFilename(String filename) {
        int dotPosition = filename.lastIndexOf('.');
        if (dotPosition != -1) {
            return filename.substring(dotPosition + 1, filename.length());
        }
        return "";
    }

    public static String getNameFromFilename(String filename) {
        int dotPosition = filename.lastIndexOf('.');
        if (dotPosition != -1) {
            return filename.substring(0, dotPosition);
        }
        return "";
    }


}

package com.zxjk.moneyspace.utils;

import android.os.Handler;
import android.os.Looper;

import com.alibaba.sdk.android.oss.ClientException;
import com.alibaba.sdk.android.oss.ServiceException;
import com.alibaba.sdk.android.oss.callback.OSSCompletedCallback;
import com.alibaba.sdk.android.oss.model.PutObjectRequest;
import com.alibaba.sdk.android.oss.model.PutObjectResult;
import com.blankj.utilcode.util.ToastUtils;
import com.zxjk.moneyspace.Application;
import com.zxjk.moneyspace.BuildConfig;
import com.zxjk.moneyspace.Constant;

public class OssUtils {

    public interface OssCallBack {
        void onSuccess(String url);
    }

    public interface OssProgressCallBack {
        void onUpload(float progress);
    }

    public interface OssCallBack1 {
        void onSuccess(String url);

        void onFail();
    }

    public static void uploadFile(String filePath, String fileTail, OssCallBack1 ossCallBack, OssProgressCallBack progressCallBack) {
        String fileName = Constant.userId + System.currentTimeMillis() + fileTail;
        PutObjectRequest put;
        if (BuildConfig.enableLog) {
            put = new PutObjectRequest("moneyspace", "upload/" +
                    fileName, filePath);
        } else {
            put = new PutObjectRequest("moneyspace", "upload/" +
                    fileName, filePath);
        }

        if (progressCallBack != null) {
            put.setProgressCallback((request, currentSize, totalSize) -> progressCallBack.onUpload((currentSize + 0f) / totalSize));
        }
        Application.oss.asyncPutObject(put, new OSSCompletedCallback<PutObjectRequest, PutObjectResult>() {
            @Override
            public void onSuccess(PutObjectRequest request, PutObjectResult result) {
                if (ossCallBack != null) {
                    new Handler(Looper.getMainLooper()).post(() -> ossCallBack.onSuccess(Constant.OSS_URL + fileName));
                }
            }

            @Override
            public void onFailure(PutObjectRequest request, ClientException clientExcepion, ServiceException serviceException) {
                if (ossCallBack != null) {
                    ossCallBack.onFail();
                }
                if (clientExcepion != null) {
                    clientExcepion.printStackTrace();
                }
            }
        });
    }

    public static void uploadFile(String filePath, OssCallBack ossCallBack, OssProgressCallBack progressCallBack) {
        String fileName = Constant.userId + System.currentTimeMillis();
        PutObjectRequest put;
        if (BuildConfig.enableLog) {
            put = new PutObjectRequest("moneyspace", "upload/" +
                    fileName, filePath);
        } else {
            put = new PutObjectRequest("moneyspace", "upload/" +
                    fileName, filePath);
        }
        if (progressCallBack != null) {
            put.setProgressCallback((request, currentSize, totalSize) -> progressCallBack.onUpload((currentSize + 0f) / totalSize));
        }
        Application.oss.asyncPutObject(put, new OSSCompletedCallback<PutObjectRequest, PutObjectResult>() {
            @Override
            public void onSuccess(PutObjectRequest request, PutObjectResult result) {
                if (ossCallBack != null) {
                    new Handler(Looper.getMainLooper()).post(() -> ossCallBack.onSuccess(Constant.OSS_URL + fileName));
                }
            }

            @Override
            public void onFailure(PutObjectRequest request, ClientException clientExcepion, ServiceException serviceException) {
                ToastUtils.showShort("上传失败，请重试");
                if (clientExcepion != null) {
                    clientExcepion.printStackTrace();
                }
            }
        });
    }

    public static void uploadFile(String filePath, OssCallBack callBack) {
        uploadFile(filePath, callBack, null);
    }

    public static void uploadFile(String filePath, OssCallBack1 callBack, OssProgressCallBack progressCallBack) {
        uploadFile(filePath, "", callBack, progressCallBack);
    }

}

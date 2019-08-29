package com.zxjk.duoduo.utils;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.alibaba.sdk.android.oss.ClientException;
import com.alibaba.sdk.android.oss.ServiceException;
import com.alibaba.sdk.android.oss.callback.OSSCompletedCallback;
import com.alibaba.sdk.android.oss.model.PutObjectRequest;
import com.alibaba.sdk.android.oss.model.PutObjectResult;
import com.blankj.utilcode.util.ToastUtils;
import com.zxjk.duoduo.Application;
import com.zxjk.duoduo.Constant;

public class OssUtils {

    public interface OssCallBack {
        void onSuccess(String url);
    }

    public interface OssProgressCallBack {
        void onUpload(float progress);
    }

    public static void uploadFile(String filePath, OssCallBack ossCallBack, OssProgressCallBack progressCallBack) {
        String fileName = Constant.userId + System.currentTimeMillis();
        PutObjectRequest put = new PutObjectRequest("zhongxingjike2", "upload/" +
                fileName, filePath);
        if (progressCallBack != null) {
            put.setProgressCallback((request, currentSize, totalSize) -> {
                progressCallBack.onUpload((totalSize + 0f) / currentSize);
            });
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

}

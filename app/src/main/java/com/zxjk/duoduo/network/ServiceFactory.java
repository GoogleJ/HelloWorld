package com.zxjk.duoduo.network;

import android.text.TextUtils;

import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.GsonUtils;
import com.blankj.utilcode.util.Utils;
import com.zxjk.duoduo.BuildConfig;
import com.zxjk.duoduo.Constant;
import com.zxjk.duoduo.utils.OkHttpClientUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class ServiceFactory {
    private static ServiceFactory instance;
    private Retrofit retrofit;

    private ServiceFactory() {
        initRetrofit();
    }

    public static ServiceFactory getInstance() {
        if (instance == null) {
            instance = new ServiceFactory();
        }
        return instance;
    }

    private static void downloadFile(String address, okhttp3.Callback callback) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(address)
                .build();
        client.newCall(request).enqueue(callback);
    }

    public static void downloadFile(String version, String url, DownloadListener downloadListener) {
        downloadFile(url, new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                downloadListener.onFailure();
            }

            @Override
            public void onResponse(Call call, Response response) {
                if (response.code() != 200) {
                    downloadListener.onFailure();
                    return;
                }
                InputStream is = null;//输入流
                FileOutputStream fos = null;//输出流
                try {
                    is = response.body().byteStream();//获取输入流
                    long total = response.body().contentLength();//获取文件大小
                    downloadListener.onStart(total);
                    if (is != null) {
                        File file = new File(Utils.getApp().getCacheDir(), version + ".apk");
                        fos = new FileOutputStream(file);
                        byte[] buf = new byte[1024];
                        int ch;
                        int process = 0;
                        while ((ch = is.read(buf)) != -1) {
                            fos.write(buf, 0, ch);
                            process += ch;
                            downloadListener.onProgress(process);
                        }
                        fos.flush();
                    }
                    downloadListener.onSuccess();
                } catch (Exception e) {
                    downloadListener.onFailure();
                } finally {
                    try {
                        if (is != null)
                            is.close();
                    } catch (IOException e) {
                        downloadListener.onFailure();
                    }
                    try {
                        if (fos != null)
                            fos.close();
                    } catch (IOException e) {
                        downloadListener.onFailure();
                    }
                }
            }
        });
    }

    private void initRetrofit() {
        //Log拦截器
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        if (BuildConfig.enableLog) {
            interceptor.level(HttpLoggingInterceptor.Level.BODY);
        } else {
            interceptor.level(HttpLoggingInterceptor.Level.NONE);
        }
        //构造client对象
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(interceptor).connectTimeout(8, TimeUnit.SECONDS)
                .addInterceptor(chain -> {
                    Request original = chain.request();
                    Request.Builder requestBuilder = original.newBuilder()
                            .header("id", Constant.userId)
                            .header("token", Constant.token)
                            .header("Accept-Language", Constant.language)
                            .header("phoneUuid", Constant.phoneUuid)
                            .header("version", AppUtils.getAppVersionName())
                            .header("systemType", "1");
                    Request request = requestBuilder.build();
                    return chain.proceed(request);
                })
                .hostnameVerifier((hostname, session) -> BuildConfig.enableLog || hostname.equals("mochart.ztoken.cn")
                        || hostname.equals("api.hilamg.com"))
                .readTimeout(8, TimeUnit.SECONDS)
                .writeTimeout(8, TimeUnit.SECONDS)
                .build();

        //buildApi
        retrofit = new Retrofit.Builder().baseUrl(Constant.BASE_URL)
                .client(OkHttpClientUtil.getSSLClient(client, Utils.getApp(), "cacert.cer", "new.cer"))
                .addConverterFactory(BasicConvertFactory.create())
                .addConverterFactory(GsonConverterFactory.create(GsonUtils.getGson(false)))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
    }

    private Retrofit initNormal(String url) {
        //Log拦截器
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        if (BuildConfig.enableLog) {
            interceptor.level(HttpLoggingInterceptor.Level.BODY);
        } else {
            interceptor.level(HttpLoggingInterceptor.Level.NONE);
        }
        //构造client对象
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(interceptor).connectTimeout(8, TimeUnit.SECONDS)
                .addInterceptor(chain -> {
                    Request original = chain.request();
                    Request.Builder requestBuilder = original.newBuilder()
                            .header("Authorization", "APPCODE " + Constant.APP_CODE);
                    Request request = requestBuilder.build();
                    return chain.proceed(request);
                })
                .readTimeout(8, TimeUnit.SECONDS)
                .writeTimeout(8, TimeUnit.SECONDS)
                .build();

        return new Retrofit.Builder().baseUrl(url)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
    }

    public <T> T getNormalService(String url, Class<T> from) {
        return initNormal(url).create(from);
    }

    public <T> T getBaseService(Class<T> from) {
        if (TextUtils.isEmpty(Constant.userId)) {
            initRetrofit();
        }
        return retrofit.create(from);
    }

    public interface DownloadListener {
        void onStart(long max);

        void onProgress(long progress);

        void onSuccess();

        void onFailure();
    }

}
package com.maning.updatelibrary.http;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * @author : maning
 * @desc :  文件上传下载相关的工具
 */
public class DownloadFileUtils {
    private static final String TAG = DownloadFileUtils.class.getSimpleName();
    /**
     * 请求的集合
     */
    private static HashMap<Object, Call> mCallHashMap = new HashMap<>();
    /**
     * 当前实例
     */
    private static DownloadFileUtils instance;
    /**
     * UI线程
     */
    private static Handler mUIHandler = new Handler(Looper.getMainLooper());

    /**
     * 默认AbsFileProgressCallback
     */
    private AbsFileProgressCallback defaultFileProgressCallback = new AbsFileProgressCallback() {

        @Override
        public void onSuccess(String result) {

        }

        @Override
        public void onProgress(long bytesRead, long contentLength, boolean done) {

        }

        @Override
        public void onFailed(String errorMsg) {

        }

        @Override
        public void onStart() {

        }

        @Override
        public void onCancle() {

        }
    };
    /**
     * 请求相关参数
     */
    private DownloadModel downloadModel;

    private DownloadFileUtils() {
        downloadModel = new DownloadModel();
    }

    /**
     * 回去当前实例
     *
     * @return
     */
    public static DownloadFileUtils with() {
        instance = new DownloadFileUtils();
        return instance;
    }

    /**
     * 设置请求Url
     *
     * @param url
     * @return
     */
    public DownloadFileUtils url(String url) {
        downloadModel.setHttpUrl(url);
        return instance;
    }

    /**
     * 下载文件保存的路径
     *
     * @param filePath
     * @return
     */
    public DownloadFileUtils downloadPath(String filePath) {
        downloadModel.setDownloadPath(filePath);
        return instance;
    }

    /**
     * 下载文件保存的路径
     *
     * @param tag
     * @return
     */
    public DownloadFileUtils tag(Object tag) {
        downloadModel.setTag(tag);
        return instance;
    }

    /**
     * 设置请求头
     *
     * @param headersMap
     * @return
     */
    public DownloadFileUtils headers(Map<String, String> headersMap) {
        downloadModel.setHeadersMap(headersMap);
        return instance;
    }

    /**
     * 设置单个请求头
     *
     * @param headerKey
     * @param headerValue
     * @return
     */
    public DownloadFileUtils addHeader(String headerKey, String headerValue) {
        downloadModel.getHeadersMap().put(headerKey, headerValue);
        return instance;
    }

    /**
     * 上传下载进度回调
     *
     * @param fileProgressCallback
     */
    public void execute(AbsFileProgressCallback fileProgressCallback) {
        if (fileProgressCallback == null) {
            fileProgressCallback = defaultFileProgressCallback;
        }
        downloadModel.setFileProgressCallback(fileProgressCallback);
        //开始请求
        startDonwload();
    }

    private void startDonwload() {
        if (downloadModel == null) {
            throw new NullPointerException("OkhttpRequestModel初始化失败");
        }
        //获取参数
        //请求地址
        String httpUrl = downloadModel.getHttpUrl();
        //请求Tag
        Object tag = downloadModel.getTag();
        if (tag == null) {
            tag = httpUrl;
        }
        //请求头
        Map<String, String> headersMap = downloadModel.getHeadersMap();
        //下载保存的路径
        final String downloadPath = downloadModel.getDownloadPath();
        //文件回调
        final AbsFileProgressCallback fileProgressCallback = downloadModel.getFileProgressCallback();

        //获取OkHttpClient
        final OkHttpClient.Builder okhttpBuilder = getOkhttpDefaultBuilder();
        //初始化请求
        final Request.Builder requestBuild = new Request.Builder();
        //添加请求地址
        requestBuild.url(httpUrl);
        //添加请求头
        if (headersMap != null && headersMap.size() > 0) {
            for (String key : headersMap.keySet()) {
                requestBuild.addHeader(key, headersMap.get(key));
            }
        }
        okhttpBuilder.addNetworkInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Response originalResponse = chain.proceed(chain.request());
                return originalResponse.newBuilder()
                        .body(new ProgressResponseBody(originalResponse.body(), fileProgressCallback))
                        .build();
            }
        });
        mUIHandler.post(new Runnable() {
            @Override
            public void run() {
                fileProgressCallback.onStart();
            }
        });
        Call call = okhttpBuilder.build().newCall(requestBuild.get().build());
        //添加请求到集合
        mCallHashMap.put(tag, call);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(final Call call, final IOException e) {
                Log.e(TAG, "onFailure:" + e.toString());
                mUIHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (call.isCanceled()) {
                            // 下载取消
                            fileProgressCallback.onCancle();
                        } else {
                            // 下载失败
                            fileProgressCallback.onFailed(e.toString());
                        }

                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                InputStream is = null;
                byte[] buf = new byte[2048];
                int len = 0;
                FileOutputStream fos = null;
                // 储存下载文件的目录
                checkDownloadFilePath(downloadPath);
                try {
                    is = response.body().byteStream();
                    long total = response.body().contentLength();
                    File file = new File(downloadPath);
                    fos = new FileOutputStream(file);
                    long sum = 0;
                    while ((len = is.read(buf)) != -1) {
                        fos.write(buf, 0, len);
                        sum += len;
                    }
                    fos.flush();
                    mUIHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            // 下载完成
                            fileProgressCallback.onSuccess("");
                        }
                    });
                } catch (final Exception e) {
                    mUIHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            Log.e(TAG, "onFailure:" + e.getMessage());
                            if (e.getMessage().equals("Socket closed")) {
                                // 下载失败
                                fileProgressCallback.onCancle();
                            } else {
                                // 下载失败
                                fileProgressCallback.onFailed(e.toString());
                            }
                        }
                    });
                } finally {
                    try {
                        if (is != null) {
                            is.close();
                        }
                    } catch (IOException e) {
                    }
                    try {
                        if (fos != null) {
                            fos.close();
                        }
                    } catch (IOException e) {
                    }
                }

            }
        });

    }


    private static void checkDownloadFilePath(String localFilePath) {
        File path = new File(localFilePath.substring(0,
                localFilePath.lastIndexOf("/") + 1));
        File file = new File(localFilePath);
        if (!path.exists()) {
            path.mkdirs();
        }
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 获取默认OkHttpClient.Builder
     *
     * @return
     */
    @NonNull
    public static OkHttpClient.Builder getOkhttpDefaultBuilder() {
        //默认信任所有的证书
        X509TrustManager trustManager = new X509TrustManager() {
            @Override
            public void checkClientTrusted(X509Certificate[] chain, String authType) {
            }

            @Override
            public void checkServerTrusted(X509Certificate[] chain, String authType) {
            }

            @Override
            public X509Certificate[] getAcceptedIssuers() {
                X509Certificate[] x509Certificates = new X509Certificate[0];
                return x509Certificates;
            }
        };
        SSLContext sslContext = null;
        try {
            sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, new TrustManager[]{trustManager}, new SecureRandom());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        }
        SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();
        HostnameVerifier DO_NOT_VERIFY = new HostnameVerifier() {
            @Override
            public boolean verify(String hostname, SSLSession session) {
                return true;
            }
        };

        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.connectTimeout(30000, TimeUnit.MILLISECONDS);
        builder.readTimeout(30000, TimeUnit.MILLISECONDS);
        builder.writeTimeout(30000, TimeUnit.MILLISECONDS);
        builder.sslSocketFactory(sslSocketFactory, trustManager);
        builder.hostnameVerifier(DO_NOT_VERIFY);
        return builder;
    }

    /**
     * 取消一个请求
     *
     * @param tag
     */
    public static void cancle(Object tag) {
        try {
            if (mCallHashMap != null && mCallHashMap.size() > 0) {
                if (mCallHashMap.containsKey(tag)) {
                    //获取对应的Call
                    Call call = mCallHashMap.get(tag);
                    if (call != null) {
                        //如果没有被取消 执行取消的方法
                        if (!call.isCanceled()) {
                            call.cancel();
                        }
                        //移除对应的KEY
                        mCallHashMap.remove(tag);
                    }
                }
            }
        } catch (Exception e) {

        }
    }

    /**
     * 取消所有请求
     */
    public static void cancleAll() {
        try {
            if (mCallHashMap != null && mCallHashMap.size() > 0) {
                //获取KEY的集合
                Set<Map.Entry<Object, Call>> keyEntries = mCallHashMap.entrySet();
                for (Map.Entry<Object, Call> entry : keyEntries) {
                    //key
                    Object key = entry.getKey();
                    //获取对应的Call
                    Call call = entry.getValue();
                    if (call != null) {
                        //如果没有被取消 执行取消的方法
                        if (!call.isCanceled()) {
                            call.cancel();
                        }
                        //移除对应的KEY
                        mCallHashMap.remove(key);
                    }
                }
            }
        } catch (Exception e) {

        }
    }

}

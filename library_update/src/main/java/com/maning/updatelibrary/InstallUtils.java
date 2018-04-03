package com.maning.updatelibrary;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by maning on 16/8/15.
 * 下载更新APK的工具
 */
public class InstallUtils {

    private static final String TAG = "InstallUtils";
    private static InstallUtils mInstance;

    //任务定时器
    private Timer mTimer;
    //定时任务
    private TimerTask mTask;
    //文件总大小
    private int fileLength = 1;
    //下载的文件大小
    private int fileCurrentLength;

    private String httpUrl;
    private String savePath;
    private String saveName;
    private static File saveFile;

    private static Context context;
    private static Handler handler = new Handler();
    private static DownloadCallBack downloadCallBack;
    private static boolean isComplete = false;   //是不是完成下载
    private static boolean isHttp302 = false;  //是不是302重定向
    private static boolean isCancle = false;  //是不是手动取消


    public interface DownloadCallBack {
        void onStart();

        void onComplete(String path);

        void onLoading(long total, long current);

        void onFail(Exception e);

        void cancle();
    }

    public interface InstallCallBack {
        void onSuccess();

        void onFail(Exception e);
    }

    /**
     * 私有构造函数
     */
    private InstallUtils() {
    }

    /**
     * 设置监听
     *
     * @param downloadCallBack
     */
    public static void setDownloadCallBack(DownloadCallBack downloadCallBack) {
        InstallUtils.downloadCallBack = downloadCallBack;
    }

    /**
     * 取消下载
     */
    public static void cancleDownload() {
        isCancle = true;
    }

    /**
     * 是否完成下载操作
     *
     * @return
     */
    public static boolean isComplete() {
        return isComplete;
    }

    /**
     * 初始化对象
     *
     * @param context 上下文
     * @return
     */
    public static InstallUtils with(Context context) {
        InstallUtils.context = context;
        if (mInstance == null) {
            mInstance = new InstallUtils();
        }
        isCancle = false;
        isComplete = false;
        isHttp302 = false;
        return mInstance;
    }

    /**
     * 设置保存的名字
     *
     * @param apkName
     * @return
     */
    public InstallUtils setApkName(String apkName) {
        this.saveName = apkName;
        return mInstance;
    }

    /**
     * 设置下载地址
     *
     * @param apkUrl
     * @return
     */
    public InstallUtils setApkUrl(String apkUrl) {
        this.httpUrl = apkUrl;
        return mInstance;
    }

    /**
     * 设置下载后保存的地址
     *
     * @param apkPath
     * @return
     */
    public InstallUtils setApkPath(String apkPath) {
        this.savePath = apkPath;
        return mInstance;
    }

    /**
     * 设置回调监听
     *
     * @param downloadCallBack
     * @return
     */
    public InstallUtils setCallBack(DownloadCallBack downloadCallBack) {
        InstallUtils.downloadCallBack = downloadCallBack;
        return mInstance;
    }

    /**
     * 开始下载
     */
    public void startDownload() {
        if (TextUtils.isEmpty(this.savePath)) {
            this.savePath = MNUtils.getCachePath(this.context);
        }
        if (TextUtils.isEmpty(this.saveName)) {
            this.saveName = "update";
        }
        try {
            if (TextUtils.isEmpty(httpUrl)) {
                downloadFail(new Exception("下载地址为空"));
                return;
            }
            saveFile = new File(savePath);
            if (!saveFile.exists()) {
                boolean isMK = saveFile.mkdirs();
                if (!isMK) {
                    //创建失败
                    downloadFail(new Exception("创建文件夹失败"));
                    return;
                }
            }
            if (saveFile.getAbsolutePath().endsWith("/")) {
                saveFile = new File(savePath + saveName + ".apk");
            } else {
                saveFile = new File(savePath + File.separator + saveName + ".apk");
            }
            //开始下载
            downloadStart();
            //开启线程下载
            new Thread(new Runnable() {
                @Override
                public void run() {
                    InputStream inputStream = null;
                    FileOutputStream outputStream = null;
                    HttpURLConnection connection = null;
                    try {
                        URL url = new URL(httpUrl);
                        connection = (HttpURLConnection) url.openConnection();
                        connection.setConnectTimeout(30 * 1000);
                        connection.setReadTimeout(30 * 1000);
                        connection.connect();

                        //判断是不是成功
                        int responseCode = connection.getResponseCode();
                        //302重定向问题
                        if (responseCode == 302) {
                            String location = connection.getHeaderField("Location");
                            downloadAlgin(location);
                            return;
                        }
                        //失败了
                        if (responseCode != 200) {
                            //失败的地址
                            final String responseMessage = connection.getResponseMessage();
                            downloadFail(new Exception(responseMessage));
                            return;
                        }

                        inputStream = connection.getInputStream();
                        outputStream = new FileOutputStream(saveFile);
                        fileLength = connection.getContentLength();

                        //判断fileLength大小
                        if (fileLength <= 0) {
                            //失败
                            downloadFail(new Exception("下载失败"));
                            return;
                        }

                        //计时器
                        initTimer();

                        byte[] buffer = new byte[1024];
                        int current = 0;
                        int len;
                        while (!isCancle && (len = inputStream.read(buffer)) > 0) {
                            outputStream.write(buffer, 0, len);
                            current += len;
                            if (fileLength > 0) {
                                fileCurrentLength = current;
                            }
                        }
                        isComplete = true;
                        if (isCancle) {
                            //下载取消
                            downloadCancle();
                            return;
                        }
                        //延时通知下载完成
                        Thread.sleep(500);
                        //下载完成
                        downloadComplete();
                    } catch (final Exception e) {
                        e.printStackTrace();
                        downloadFail(e);
                    } finally {
                        try {
                            if (inputStream != null)
                                inputStream.close();
                            if (outputStream != null)
                                outputStream.close();
                            if (connection != null)
                                connection.disconnect();
                        } catch (IOException e) {
                        }
                        //销毁Timer
                        destroyTimer();
                        isCancle = false;
                        isHttp302 = false;
                        fileLength = 1;
                        fileCurrentLength = 0;
                        handler.removeCallbacksAndMessages(null);
                    }
                }
            }).start();

        } catch (Exception e) {
            downloadFail(new Exception("下载异常"));
        }

    }

    private void downloadAlgin(final String newHttp) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                isHttp302 = true;
                httpUrl = newHttp;
                startDownload();
            }
        });
    }

    private void downloadStart() {
        if (isHttp302) {
            return;
        }
        isHttp302 = false;
        if (downloadCallBack != null) {
            //下载开始
            downloadCallBack.onStart();
        }
    }

    private void downloadComplete() {
        try {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    if (downloadCallBack != null) {
                        downloadCallBack.onComplete(saveFile.getPath());
                        downloadCallBack = null;
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void downloadFail(final Exception exception) {
        try {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    if (downloadCallBack != null) {
                        downloadCallBack.onFail(exception);
                        downloadCallBack = null;
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void downloadCancle() {
        try {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    if (downloadCallBack != null) {
                        downloadCallBack.cancle();
                        downloadCallBack = null;
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initTimer() {
        mTimer = new Timer();
        mTask = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (downloadCallBack != null && !isComplete) {
                            downloadCallBack.onLoading(fileLength, fileCurrentLength);
                        }
                    }
                });
            }
        };
        mTimer.schedule(mTask, 0, 200);
    }


    private void destroyTimer() {
        if (mTimer != null && mTask != null) {
            mTask.cancel();
            mTimer.cancel();
            mTask = null;
            mTimer = null;
        }
    }

    /**
     * 安装APK工具类
     *
     * @param context  上下文
     * @param filePath 文件路径
     * @param callBack 安装界面成功调起的回调
     */
    public static void installAPK(Context context, String filePath, InstallCallBack callBack) {
        try {
            Intent intent = new Intent();
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setAction(Intent.ACTION_VIEW);
            File apkFile = new File(filePath);
            Uri apkUri;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                // 授予目录临时共享权限
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                String authority = context.getPackageName() + ".updateFileProvider";
                apkUri = MNUpdateApkFileProvider.getUriForFile(context, authority, apkFile);
            } else {
                apkUri = Uri.fromFile(apkFile);
            }
            intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
            context.startActivity(intent);
            if (callBack != null) {
                callBack.onSuccess();
            }
        } catch (Exception e) {
            if (callBack != null) {
                callBack.onFail(e);
            }
        }
    }

    /**
     * 通过浏览器下载APK更新安装
     *
     * @param context    上下文
     * @param httpUrlApk APK下载地址
     */
    public static void installAPKWithBrower(Context context, String httpUrlApk) {
        Uri uri = Uri.parse(httpUrlApk);
        Intent viewIntent = new Intent(Intent.ACTION_VIEW, uri);
        context.startActivity(viewIntent);
    }

}

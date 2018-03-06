package com.maning.updatelibrary;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
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
 * <pre>
 *     author : maning
 *     e-mail : xxx@xx
 *     time   : 2018/03/06
 *     desc   : 下载服务
 *     version: 1.0
 * </pre>
 */
public class DownloadService extends IntentService {

    private static final String INTENTKEY_URL = "intentkey_url";
    private static final String INTENTKEY_NAME = "intentkey_name";

    public static final String BROADCAST_INTENTFILTER_DOWNLOADSERVICE = "broadcast_intentfilter_downloadservice";
    public static final String DOWNLOAD_APK_START = "ACTION_DOWNLOAD_APK_START";
    public static final String DOWNLOAD_APK_FAIL = "ACTION_DOWNLOAD_APK_FAIL";
    public static final String DOWNLOAD_APK_COMPLETE = "ACTION_DOWNLOAD_APK_COMPLETE";
    public static final String DOWNLOAD_APK_LOADING = "ACTION_DOWNLOAD_APK_LOADING";
    private static final String TAG = "DownloadService";

    //任务定时器
    private Timer mTimer;
    //定时任务
    private TimerTask mTask;
    //文件总大小
    private int fileLength = 1;
    //下载的文件大小
    private int fileCurrentLength;
    //下载地址
    private String httpUrl;
    //保存路径
    private String savePath;
    private static File saveFile;
    //保存名字
    private String saveName;

    private boolean isComplete = false;
    private boolean isStart = false;

    private LocalBroadcastManager mLocalBroadcastManager;

    public DownloadService() {
        super("DownloadService");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mLocalBroadcastManager = LocalBroadcastManager.getInstance(this);
    }

    public void startService(Context context, String url, String name) {
        Intent intent = new Intent(context, DownloadService.class);
        intent.putExtra(INTENTKEY_URL, url);
        intent.putExtra(INTENTKEY_NAME, name);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if (intent == null) {
            return;
        }
        if (isStart) {
            return;
        }
        isStart = true;
        httpUrl = intent.getStringExtra(INTENTKEY_URL);
        saveName = intent.getStringExtra(INTENTKEY_NAME);
        savePath = CommonUtils.getCachePath(this);
        saveFile = new File(savePath);
        if (!saveFile.exists()) {
            boolean isMK = saveFile.mkdirs();
            if (!isMK) {
                //创建失败
                return;
            }
        }
        saveFile = new File(savePath + File.separator + saveName + ".apk");
        //开始下载
        downloadFile();
    }

    private void downloadFile() {
        if (TextUtils.isEmpty(httpUrl)) {
            //失败的
            sendFailBroadcast("下载地址为空");
            return;
        }
        //开始下载
        sendStartBroadcast();

        InputStream inputStream = null;
        FileOutputStream outputStream = null;
        HttpURLConnection connection = null;
        try {
            URL url = new URL(httpUrl);
            connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(10 * 1000);
            connection.setReadTimeout(10 * 1000);
            connection.connect();
            inputStream = connection.getInputStream();
            outputStream = new FileOutputStream(saveFile);
            fileLength = connection.getContentLength();

            //判断fileLength大小
            if (fileLength <= 0) {
                //失败
                sendFailBroadcast("下载地址异常");
                return;
            }
            //计时器
            initTimer();
            byte[] buffer = new byte[1024];
            int current = 0;
            int len;
            while ((len = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, len);
                current += len;
                if (fileLength > 0) {
                    fileCurrentLength = current;
                }
            }
            isComplete = true;
            //下载完成
            sendCompleteBroadcast();
        } catch (final Exception e) {
            e.printStackTrace();
            sendFailBroadcast(e.toString());
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
        }

    }

    private void sendStartBroadcast() {
        Intent intent = new Intent(BROADCAST_INTENTFILTER_DOWNLOADSERVICE);
        intent.putExtra("state", DOWNLOAD_APK_START);
        mLocalBroadcastManager.sendBroadcast(intent);
    }

    private void sendCompleteBroadcast() {
        Intent intent = new Intent(BROADCAST_INTENTFILTER_DOWNLOADSERVICE);
        intent.putExtra("state", DOWNLOAD_APK_COMPLETE);
        intent.putExtra("savePath", saveFile.getPath());
        mLocalBroadcastManager.sendBroadcast(intent);
    }

    private void sendFailBroadcast(String msg) {
        Intent intent = new Intent(BROADCAST_INTENTFILTER_DOWNLOADSERVICE);
        intent.putExtra("failMessage", msg);
        intent.putExtra("state", DOWNLOAD_APK_FAIL);
        mLocalBroadcastManager.sendBroadcast(intent);
    }

    private void sendLoadingBroadcast(long total, long current) {
        Intent intent = new Intent(BROADCAST_INTENTFILTER_DOWNLOADSERVICE);
        intent.putExtra("state", DOWNLOAD_APK_LOADING);
        intent.putExtra("totalProgress", total);
        intent.putExtra("currentProgress", current);
        mLocalBroadcastManager.sendBroadcast(intent);
    }

    private void initTimer() {
        mTimer = new Timer();
        mTask = new TimerTask() {//在run方法中执行定时的任务
            @Override
            public void run() {
                if (!isComplete) {
                    //发送通知
                    sendLoadingBroadcast(fileLength, fileCurrentLength);
                }
            }
        };
        //任务定时器一定要启动
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        destroyTimer();
        Log.i(TAG, "DownloadService-onDestroy");
    }

}

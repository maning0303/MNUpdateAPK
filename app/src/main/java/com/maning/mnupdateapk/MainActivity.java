package com.maning.mnupdateapk;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.maning.updatelibrary.DownloadService;
import com.maning.updatelibrary.InstallUtils;

public class MainActivity extends AppCompatActivity {

    //    public static final String APK_URL = "http://download.fir.im/v2/app/install/56dd4bb7e75e2d27f2000046?download_token=e415c0fd1ac3b7abcb65ebc6603c59d9&source=update";
    public static final String APK_URL = "http://download.fir.im/v2/app/install/5a52e936ca87a8600e0002f9?download_token=cd8662357947f151de92975b46082ba6&source=update";
    public static final String APK_NAME = "update";
    private static final String TAG = "InstallUtils";
    private Context context;

    private TextView tv_progress;
    private TextView tv_info;
    private Button btnDownload;


    private LocalBroadcastManager localBroadcastManager;
    private BroadcastReceiver downloadReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(DownloadService.BROADCAST_INTENTFILTER_DOWNLOADSERVICE)) {
                String state = intent.getStringExtra("state");
                String failMessage = intent.getStringExtra("failMessage");
                long total = intent.getLongExtra("totalProgress", 1);
                long current = intent.getLongExtra("currentProgress", 0);
                String savePath = intent.getStringExtra("savePath");
                switch (state) {
                    case DownloadService.DOWNLOAD_APK_START:
                        Log.i(TAG, "BroadcastReceiver---DOWNLOAD_APK_START");
                        tv_progress.setText("0%");
                        break;
                    case DownloadService.DOWNLOAD_APK_FAIL:
                        Log.i(TAG, "BroadcastReceiver---DOWNLOAD_APK_FAIL");
                        tv_info.setText("下载失败:" + failMessage);
                        btnDownload.setClickable(true);
                        break;
                    case DownloadService.DOWNLOAD_APK_COMPLETE:
                        Log.i(TAG, "BroadcastReceiver---DOWNLOAD_APK_COMPLETE:" + savePath);
                        InstallUtils.installAPK(context, savePath, new InstallUtils.InstallCallBack() {
                            @Override
                            public void onSuccess() {
                                Toast.makeText(MainActivity.this, "正在安装程序", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onFail(Exception e) {
                                tv_info.setText("安装失败:" + e.toString());
                            }
                        });
                        tv_progress.setText("100%");
                        tv_info.setText("下载成功");
                        btnDownload.setClickable(true);
                        break;
                    case DownloadService.DOWNLOAD_APK_LOADING:
                        Log.i(TAG, "BroadcastReceiver----DOWNLOAD_APK_LOADING:-----total:" + total + ",current:" + current);
                        tv_progress.setText((int) (current * 100 / total) + "%");
                        break;
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;

        tv_progress = (TextView) findViewById(R.id.tv_progress);
        tv_info = (TextView) findViewById(R.id.tv_info);
        btnDownload = (Button) findViewById(R.id.btnDownload);

        //判断是不是最新版本
        if (getVersionCode() > 1) {
            //最新版本
            tv_info.setText("当前版本是最新版本");
        } else {
            tv_info.setText("当前版本有更新");
        }

        registerReceiver();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //取消注册广播,防止内存泄漏
        localBroadcastManager.unregisterReceiver(downloadReceiver);
    }

    private void registerReceiver() {
        localBroadcastManager = LocalBroadcastManager.getInstance(this);
        IntentFilter filter = new IntentFilter(DownloadService.BROADCAST_INTENTFILTER_DOWNLOADSERVICE);
        localBroadcastManager.registerReceiver(downloadReceiver, filter);
    }

    public void btnDownloadService(View view) {
        DownloadService downloadService = new DownloadService();
        downloadService.startService(this, APK_URL, APK_NAME);
    }

    public void download2(View view) {
        //通过浏览器去下载APK
        InstallUtils.installAPKWithBrower(this, APK_URL);
    }

    public void download(View view) {
        btnDownload.setClickable(false);
        new InstallUtils(context, APK_URL, APK_NAME, new InstallUtils.DownloadCallBack() {
            @Override
            public void onStart() {
                Log.i(TAG, "InstallUtils---onStart");
                tv_progress.setText("0%");
            }

            @Override
            public void onComplete(String path) {
                Log.i(TAG, "InstallUtils---onComplete:" + path);
                InstallUtils.installAPK(context, path, new InstallUtils.InstallCallBack() {
                    @Override
                    public void onSuccess() {
                        Toast.makeText(context, "正在安装程序", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFail(Exception e) {
                        tv_info.setText("安装失败:" + e.toString());
                    }
                });
                tv_progress.setText("100%");
                tv_info.setText("下载成功");
                btnDownload.setClickable(true);
            }

            @Override
            public void onLoading(long total, long current) {
                Log.i(TAG, "InstallUtils----onLoading:-----total:" + total + ",current:" + current);
                tv_progress.setText((int) (current * 100 / total) + "%");
            }

            @Override
            public void onFail(Exception e) {
                Log.i(TAG, "InstallUtils---onFail:" + e.getMessage());
                tv_info.setText("下载失败:" + e.toString());
                btnDownload.setClickable(true);
            }

        }).downloadAPK();

    }


    //版本名
    public String getVersionName() {
        return getPackageInfo().versionName;
    }

    //版本号
    public int getVersionCode() {
        return getPackageInfo().versionCode;
    }

    private PackageInfo getPackageInfo() {
        PackageInfo pi = null;
        try {
            PackageManager pm = getPackageManager();
            pi = pm.getPackageInfo(getPackageName(),
                    PackageManager.GET_CONFIGURATIONS);
            return pi;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return pi;
    }

}

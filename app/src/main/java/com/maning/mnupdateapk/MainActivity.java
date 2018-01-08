package com.maning.mnupdateapk;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.maning.updatelibrary.InstallUtils;

public class MainActivity extends AppCompatActivity {

//    public static final String APK_URL = "http://download.fir.im/v2/app/install/56dd4bb7e75e2d27f2000046?download_token=e415c0fd1ac3b7abcb65ebc6603c59d9&source=update";
    public static final String APK_URL = "http://download.fir.im/v2/app/install/5a52e936ca87a8600e0002f9?download_token=cd8662357947f151de92975b46082ba6&source=update";
    public static final String APK_NAME = "update";
    private static final String TAG = "InstallUtils";
    private Context context;

    private TextView tv_progress;
    private TextView tv_info;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;

        tv_progress = (TextView) findViewById(R.id.tv_progress);
        tv_info = (TextView) findViewById(R.id.tv_info);

        //判断是不是最新版本
        if (getVersionCode() > 1) {
            //最新版本
            tv_info.setText("当前版本是最新版本");
        } else {
            tv_info.setText("当前版本有更新");
        }

    }

    public void download(View view) {
        new InstallUtils(context, APK_URL, APK_NAME, new InstallUtils.DownloadCallBack() {
            @Override
            public void onStart() {
                Log.i(TAG, "InstallUtils---onStart");
                tv_progress.setText("0%");
            }

            @Override
            public void onComplete(String path) {
                Log.i(TAG, "InstallUtils---onComplete:" + path);
                InstallUtils.installAPK(context, path, getPackageName() + ".fileProvider", new InstallUtils.InstallCallBack() {
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

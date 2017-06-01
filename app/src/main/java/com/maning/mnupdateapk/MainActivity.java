package com.maning.mnupdateapk;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.daimajia.numberprogressbar.NumberProgressBar;
import com.maning.updatelibrary.InstallUtils;

public class MainActivity extends AppCompatActivity {

    public static final String APK_URL = "http://mobile.ac.qq.com/qqcomic_android.apk";
    public static final String APK_NAME = "update";
    private static final String TAG = "InstallUtils";

    private Context context;

    private NumberProgressBar numberProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;

        numberProgressBar = (NumberProgressBar) findViewById(R.id.number_progress_bar);

    }

    public void download(View view) {

        new InstallUtils(context, APK_URL, APK_NAME, new InstallUtils.DownloadCallBack() {
            @Override
            public void onStart() {
                Log.i(TAG, "InstallUtils---onStart");
                numberProgressBar.setProgress(0);
            }

            @Override
            public void onComplete(String path) {
                Log.i(TAG, "InstallUtils---onComplete:" + path);
                InstallUtils.installAPK(context, path, "com.maning.mnupdateapk.fileProvider");
                numberProgressBar.setProgress(100);
            }

            @Override
            public void onLoading(long total, long current) {
                Log.i(TAG, "InstallUtils----onLoading:-----total:" + total + ",current:" + current);
                numberProgressBar.setProgress((int) (current * 100 / total));
            }

            @Override
            public void onFail(Exception e) {
                Log.i(TAG, "InstallUtils---onFail:" + e.getMessage());
            }

        }).downloadAPK();

    }

}

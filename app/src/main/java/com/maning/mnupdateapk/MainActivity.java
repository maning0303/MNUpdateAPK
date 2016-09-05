package com.maning.mnupdateapk;

import android.content.Context;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.daimajia.numberprogressbar.NumberProgressBar;
import com.maning.library.InstallUtils;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    public static final String CACHE_FILE_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "MNUpdateAPK";
    public static final String APK_URL = "http://download.fir.im/v2/app/install/56dd4bb7e75e2d27f2000046?download_token=e415c0fd1ac3b7abcb65ebc6603c59d9";
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

        new InstallUtils(context, APK_URL, CACHE_FILE_PATH, APK_NAME, new InstallUtils.DownloadCallBack() {
            @Override
            public void onStart() {
                Log.i(TAG, "onStart");
                numberProgressBar.setProgress(0);
            }

            @Override
            public void onComplete(String path) {
                Log.i(TAG, "onComplete:" + path);
                InstallUtils.installAPK(context, path);
                numberProgressBar.setProgress(100);
            }

            @Override
            public void onLoading(long total, long current) {
                Log.i(TAG, "onLoading:-----total:" + total + ",current:" + current);
                numberProgressBar.setProgress((int) (current * 100 / total));
            }

            @Override
            public void onFail(Exception e) {
                Log.i(TAG, "onFail:" + e.getMessage());
            }

        }).downloadAPK();

    }

}

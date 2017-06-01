package com.maning.mnupdateapk;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

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
                InstallUtils.installAPK(context, path, new InstallUtils.InstallCallBack() {
                    @Override
                    public void onComplete() {
                        Toast.makeText(context, "正在安装程序", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFail(Exception e) {
                        Toast.makeText(context, "安装失败:" + e.toString(), Toast.LENGTH_SHORT).show();
                    }
                });
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

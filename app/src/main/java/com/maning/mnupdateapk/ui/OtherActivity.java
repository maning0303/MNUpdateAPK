package com.maning.mnupdateapk.ui;

import android.app.Activity;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.maning.mnupdateapk.R;
import com.maning.updatelibrary.InstallUtils;

public class OtherActivity extends AppCompatActivity {

    public static final String TAG = "OtherActivity";
    private TextView tv_progress;
    private TextView tv_info;

    private Activity context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_other);
        context = this;

        tv_progress = (TextView) findViewById(R.id.tv_progress);
        tv_info = (TextView) findViewById(R.id.tv_info);

        //设置监听
        InstallUtils.setDownloadCallBack(new InstallUtils.DownloadCallBack() {
            @Override
            public void onStart() {
                Log.i(TAG, "OtherActivity---InstallUtils---onStart");
                tv_progress.setText("0%");
                tv_info.setText("正在下载...");
            }

            @Override
            public void onComplete(String path) {
                Log.i(TAG, "OtherActivity---InstallUtils---onComplete:" + path);
                tv_progress.setText("100%");
                tv_info.setText("下载完成");
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
            }

            @Override
            public void onLoading(long total, long current) {
                Log.i(TAG, "OtherActivity---InstallUtils----onLoading:-----total:" + total + ",current:" + current);
                tv_progress.setText((int) (current * 100 / total) + "%");
            }

            @Override
            public void onFail(Exception e) {
                Log.i(TAG, "OtherActivity---InstallUtils---onFail:" + e.getMessage());
                tv_progress.setText("0%");
                tv_info.setText("下载失败:" + e.toString());
            }

            @Override
            public void cancle() {

            }
        });
    }
}

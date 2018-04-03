package com.maning.mnupdateapk;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.maning.updatelibrary.InstallUtils;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "InstallUtils";


    private Context context;

    private TextView tv_progress;
    private TextView tv_info;
    private Button btnDownload;
    private Button btnCancle;
    private Button btnDownloadBrowser;
    private Button btnOther;
    private InstallUtils.DownloadCallBack downloadCallBack;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;

        initViews();

        initCallBack();

    }

    private void initViews() {
        tv_progress = (TextView) findViewById(R.id.tv_progress);
        tv_info = (TextView) findViewById(R.id.tv_info);
        btnDownload = (Button) findViewById(R.id.btnDownload);
        btnCancle = (Button) findViewById(R.id.btnCancle);
        btnDownloadBrowser = (Button) findViewById(R.id.btnDownloadBrowser);
        btnOther = (Button) findViewById(R.id.btnOther);

        btnDownload.setOnClickListener(this);
        btnCancle.setOnClickListener(this);
        btnDownloadBrowser.setOnClickListener(this);
        btnOther.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //设置监听
        InstallUtils.setDownloadCallBack(downloadCallBack);
    }

    private void initCallBack() {
        downloadCallBack = new InstallUtils.DownloadCallBack() {
            @Override
            public void onStart() {
                Log.i(TAG, "InstallUtils---onStart");
                tv_progress.setText("0%");
                tv_info.setText("正在下载...");
                btnDownload.setClickable(false);
                btnDownload.setBackgroundResource(R.color.colorGray);
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
                btnDownload.setBackgroundResource(R.color.colorPrimary);
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
                btnDownload.setBackgroundResource(R.color.colorPrimary);
            }

            @Override
            public void cancle() {
                Log.i(TAG, "InstallUtils---cancle");
                tv_info.setText("下载取消");
                btnDownload.setClickable(true);
                btnDownload.setBackgroundResource(R.color.colorPrimary);
            }
        };
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnCancle:
                //取消下载
                InstallUtils.cancleDownload();
                btnDownload.setClickable(true);
                btnDownload.setBackgroundResource(R.color.colorPrimary);
                break;
            case R.id.btnOther:
                startActivity(new Intent(this, OtherActivity.class));
                break;
            case R.id.btnDownloadBrowser:
                //通过浏览器去下载APK
                InstallUtils.installAPKWithBrower(this, Constants.APK_URL_02);
                break;
            case R.id.btnDownload:
                InstallUtils.with(this)
                        //必须-下载地址
                        .setApkUrl(Constants.APK_URL_03)
                        //非必须，默认update
                        .setApkName("update")
                        //非必须-下载保存的路径
                        .setApkPath(Constants.APK_SAVE_PATH)
                        //非必须-下载回调
                        .setCallBack(downloadCallBack)
                        //开始下载
                        .startDownload();
                break;
        }
    }
}

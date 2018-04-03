package com.maning.mnupdateapk;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.maning.updatelibrary.InstallUtils;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "InstallUtils";

//    public static final String APK_URL = "http://download.fir.im/v2/app/install/56dd4bb7e75e2d27f2000046?download_token=e415c0fd1ac3b7abcb65ebc6603c59d9&source=update";
    public static final String APK_URL = "http://download.fir.im/v2/app/install/5a52e936ca87a8600e0002f9?download_token=cd8662357947f151de92975b46082ba6&source=update";
//    public static final String APK_URL = "https://www.pgyer.com/apiv2/app/install?appKey=e6fcefdffc8c0ef2d7700e867f3b9685&_api_key=ae839fd4e088946dc307140042b97e17";

    public static final String APK_NAME = "update";
    public static final String APK_SAVE_PATH = Environment.getExternalStorageDirectory() + "/MNUpdateAPK";


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
                InstallUtils.setCancle(true);
                break;
            case R.id.btnOther:
                startActivity(new Intent(this, OtherActivity.class));
                break;
            case R.id.btnDownloadBrowser:
                //通过浏览器去下载APK
                InstallUtils.installAPKWithBrower(this, APK_URL);
                break;
            case R.id.btnDownload:
                InstallUtils.with(this)
                        .apkUrl(APK_URL)
                        .apkName(APK_NAME)
                        .setCallBack(downloadCallBack)
                        .downloadAPK();
                break;
        }
    }
}

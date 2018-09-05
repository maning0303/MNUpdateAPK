package com.maning.updatelibrary;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;

import com.maning.updatelibrary.http.AbsFileProgressCallback;
import com.maning.updatelibrary.http.DownloadFileUtils;
import com.maning.updatelibrary.utils.ActForResultCallback;
import com.maning.updatelibrary.utils.ActResultRequest;
import com.maning.updatelibrary.utils.MNUtils;

import java.io.File;

import static android.app.Activity.RESULT_OK;

/**
 * Created by maning on 16/8/15.
 * 安装APK的工具
 */
public class InstallUtils {

    private static final String TAG = "InstallUtils";
    private static InstallUtils mInstance;
    private static Context mContext;

    //------------------下载相关---------------------
    private String httpUrl;
    private String filePath;
    private static DownloadCallBack mDownloadCallBack;
    /**
     * 是不是在正在下载
     */
    private static boolean isDownloading = false;

    /**
     * 下载回调监听
     */
    public interface DownloadCallBack {
        void onStart();

        void onComplete(String path);

        void onLoading(long total, long current);

        void onFail(Exception e);

        void cancle();
    }

    /**
     * 私有构造函数
     */
    private InstallUtils() {
    }

    /**
     * 是否正在下载
     */
    public static boolean isDownloading() {
        return isDownloading;
    }

    /**
     * 设置监听
     *
     * @param downloadCallBack
     */
    public static void setDownloadCallBack(DownloadCallBack downloadCallBack) {
        //判断有没有开始
        if (isDownloading) {
            mDownloadCallBack = downloadCallBack;
        }
    }


    /**
     * 初始化对象
     *
     * @param context 上下文
     * @return
     */
    public static InstallUtils with(Context context) {
        mContext = context.getApplicationContext();
        if (mInstance == null) {
            mInstance = new InstallUtils();
        }
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
     * 设置下载后保存的地址,带后缀
     *
     * @param apkPath
     * @return
     */
    public InstallUtils setApkPath(String apkPath) {
        this.filePath = apkPath;
        return mInstance;
    }

    /**
     * 设置回调监听
     *
     * @param downloadCallBack
     * @return
     */
    public InstallUtils setCallBack(DownloadCallBack downloadCallBack) {
        mDownloadCallBack = downloadCallBack;
        return mInstance;
    }

    /**
     * 开始下载
     */
    public void startDownload() {
        //先取消之前的下载
        if (isDownloading) {
            cancleDownload();
        }
        //判断下载保存路径是不是空
        if (TextUtils.isEmpty(filePath)) {
            filePath = MNUtils.getCachePath(mContext) + "/update.apk";
        }
        DownloadFileUtils.with()
                .downloadPath(filePath)
                .url(httpUrl)
                .tag(InstallUtils.class)
                .execute(new AbsFileProgressCallback() {
                    int currentProgress = 0;

                    @Override
                    public void onSuccess(String result) {
                        isDownloading = false;
                        if (mDownloadCallBack != null) {
                            mDownloadCallBack.onComplete(filePath);
                        }
                    }

                    @Override
                    public void onProgress(long bytesRead, long contentLength, boolean done) {
                        isDownloading = true;
                        if (mDownloadCallBack != null) {
                            //计算进度
                            int progress = (int) (bytesRead * 100 / contentLength);
                            //只有进度+1才回调，防止过快
                            if (progress - currentProgress >= 1) {
                                mDownloadCallBack.onLoading(contentLength, bytesRead);
                            }
                            currentProgress = progress;
                        }
                    }

                    @Override
                    public void onFailed(String errorMsg) {
                        isDownloading = false;
                        if (mDownloadCallBack != null) {
                            mDownloadCallBack.onFail(new Exception(errorMsg));
                        }
                    }

                    @Override
                    public void onStart() {
                        isDownloading = true;
                        if (mDownloadCallBack != null) {
                            mDownloadCallBack.onStart();
                        }
                    }

                    @Override
                    public void onCancle() {
                        isDownloading = false;
                        if (mDownloadCallBack != null) {
                            mDownloadCallBack.cancle();
                        }
                    }
                });
    }

    public static void cancleDownload() {
        DownloadFileUtils.cancle(InstallUtils.class);
    }

    //------------------安装相关---------------------

    /**
     * 安装回调监听
     */
    public interface InstallCallBack {
        void onSuccess();

        void onFail(Exception e);
    }


    /**
     * 安装APK工具类
     *
     * @param context  上下文
     * @param filePath 文件路径
     * @param callBack 安装界面成功调起的回调
     */
    public static void installAPK(Activity context, String filePath, final InstallCallBack callBack) {
        try {
            MNUtils.changeApkFileMode(new File(filePath));
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
            new ActResultRequest(context).startForResult(intent, new ActForResultCallback() {
                @Override
                public void onActivityResult(int resultCode, Intent data) {
                    Log.i(TAG, "onActivityResult:" + resultCode);
                    //调起了系统安装页面
                    if (callBack != null) {
                        callBack.onSuccess();
                    }
                }
            });

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


    /**
     * 8.0权限检查回调监听
     */
    public interface InstallPermissionCallBack {
        void onGranted();

        void onDenied();
    }


    /**
     * 检查有没有安装权限
     * @param activity
     * @param installPermissionCallBack
     */
    public static void checkInstallPermission(Activity activity, InstallPermissionCallBack installPermissionCallBack) {
        if (hasInstallPermission(activity)) {
            if (installPermissionCallBack != null) {
                installPermissionCallBack.onGranted();
            }
        } else {
            openInstallPermissionSetting(activity, installPermissionCallBack);
        }
    }


    /**
     * 判断有没有安装权限
     * @param context
     * @return
     */
    public static boolean hasInstallPermission(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //先获取是否有安装未知来源应用的权限
            return context.getPackageManager().canRequestPackageInstalls();
        }
        return true;
    }

    /**
     * 去打开安装权限的页面
     * @param activity
     * @param installPermissionCallBack
     */
    public static void openInstallPermissionSetting(Activity activity, final InstallPermissionCallBack installPermissionCallBack) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Uri packageURI = Uri.parse("package:" + activity.getPackageName());
            Intent intent = new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES, packageURI);
            new ActResultRequest(activity).startForResult(intent, new ActForResultCallback() {
                @Override
                public void onActivityResult(int resultCode, Intent data) {
                    Log.i(TAG, "onActivityResult:" + resultCode);
                    if (resultCode == RESULT_OK) {
                        //用户授权了
                        if (installPermissionCallBack != null) {
                            installPermissionCallBack.onGranted();
                        }
                    } else {
                        //用户没有授权
                        if (installPermissionCallBack != null) {
                            installPermissionCallBack.onDenied();
                        }
                    }
                }
            });
        } else {
            //用户授权了
            if (installPermissionCallBack != null) {
                installPermissionCallBack.onGranted();
            }
        }

    }


}

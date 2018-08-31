package com.maning.updatelibrary.utils;

import android.content.Context;
import android.os.Environment;

import java.io.File;
import java.io.IOException;

/**
 * author : maning
 * time   : 2018/03/30
 * desc   : 工具类
 * version: 1.0
 */
public class MNUtils {

    /**
     * 获取app缓存路径    SDCard/Android/data/你的应用的包名/cache
     *
     * @param context
     * @return
     */
    public static String getCachePath(Context context) {
        String cachePath;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
                || !Environment.isExternalStorageRemovable()) {
            //外部存储可用
            cachePath = context.getExternalCacheDir().getPath();
        } else {
            //外部存储不可用
            cachePath = context.getCacheDir().getPath();
        }
        return cachePath;
    }

    //参照：APK放到data/data/下面提示解析失败 (http://blog.csdn.net/lonely_fireworks/article/details/27693073)
    public static void changeApkFileMode(File file) {
        try {
            //apk放在缓存目录时，低版本安装提示权限错误，需要对父级目录和apk文件添加权限
            String cmd1 = "chmod 777 " + file.getParent();
            Runtime.getRuntime().exec(cmd1);

            String cmd = "chmod 777 " + file.getAbsolutePath();
            Runtime.getRuntime().exec(cmd);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

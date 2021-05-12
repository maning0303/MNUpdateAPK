package com.maning.mnupdateapk;

import android.os.Environment;

/**
 * <pre>
 *     author : maning
 *     e-mail : xxx@xx
 *     time   : 2018/04/03
 *     desc   :
 *     version: 1.0
 * </pre>
 */
public class Constants {

    public static final String APK_URL_01 = "http://download.fir.im/v2/app/install/56dd4bb7e75e2d27f2000046?download_token=e415c0fd1ac3b7abcb65ebc6603c59d9&source=update";
    public static final String APK_URL_02 = "http://download.fir.im/v2/app/install/5a52e936ca87a8600e0002f9?download_token=cd8662357947f151de92975b46082ba6&source=update";
    public static final String APK_URL_03 = "https://static.clouderwork.com/static/apk/yunwoke_yingyongbao.apk";


    public static final String APK_SAVE_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + "/MNUpdateAPK/update.apk";

}

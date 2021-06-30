package com.maning.mnupdateapk.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

/**
 * @author : maning
 * @desc :
 */
public class PermissionUtils {

    /**
     * 是否有权限
     *
     * @param context
     * @return
     */
    public static boolean checkSelfPermission(Context context, String permission) {
        if (null == context) {
            return false;
        }
        int per = ContextCompat.checkSelfPermission(context, permission);
        return per == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * Check that all given permissions have been granted by verifying that each entry in the
     * given array is of the value {@link PackageManager#PERMISSION_GRANTED}.
     *
     * @see Activity#onRequestPermissionsResult(int, String[], int[])
     */
    public static boolean verifyPermissions(int[] grantResults) {
        // At least one result must be checked.
        if (null == grantResults || grantResults.length < 1) {
            return false;
        }

        // Verify that each required permission has been granted, otherwise return false.
        for (int result : grantResults) {
            if (result != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    public static boolean isGrantSDCardReadPermission(Context context) {
        return checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE);
    }

    public static void requestSDCardReadPermission(Activity activity, int requestCode) {
        ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, requestCode);
    }

}

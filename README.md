# MNUpdateAPK
Android APK 版本更新的下载和安装,支持7.0安装
[![](https://jitpack.io/v/maning0303/MNUpdateAPK.svg)](https://jitpack.io/#maning0303/MNUpdateAPK)

## 功能：
    1：下载APK
    2：安装APK
    
## 截图:
![image](https://github.com/maning0303/MNUpdateAPK/blob/master/screenshots/001.gif)


## 如何添加
### Gradle添加：
#### 1.在Project的build.gradle中添加仓库地址

``` gradle
	allprojects {
		repositories {
			...
			maven { url "https://jitpack.io" }
		}
	}
```

#### 2.在Module目录下的build.gradle中添加依赖
``` gradle
	dependencies {
	     compile 'com.github.maning0303:MNUpdateAPK:V1.0.3'
	}
```

## 关于7.0适配(已经在库中配置完成):
### 1:在Manifest.xml中添加配置
``` gradle
      <provider
            android:name="android.support.v4.content.FileProvider"
            android:authorities="${applicationId}.fileProvider"
            android:exported="false"
            android:grantUriPermissions="true">
            <meta-data
                android:name="android.support.FILE_PROVIDER_PATHS"
                android:resource="@xml/file_provider" />
       </provider>
```

### 2:res/xml/file_provider.xml:
``` java
        <?xml version="1.0" encoding="utf-8"?>
        <paths>

            <!--升级-->
            <external-cache-path
                name="mn_update_external_cache"
                path="" />

            <cache-path
                name="mn_update_cache"
                path="" />

        </paths>
```

### 3:安装APK代码适配:
``` java
        public static void installAPK(Context context, String filePath, InstallCallBack callBack) {
            try {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                File apkFile = new File(filePath);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    //这里的authority和Manifest.xml中添加provider中的authorities保持一致
                    Uri contentUri = FileProvider.getUriForFile(context, context.getPackageName() + ".fileProvider", apkFile);
                    intent.setDataAndType(contentUri, "application/vnd.android.package-archive");
                } else {
                    intent.setDataAndType(Uri.fromFile(apkFile), "application/vnd.android.package-archive");
                }
                context.startActivity(intent);
            } catch (Exception e) {
                Log.e("InstallUtils", "安装APK失败:" + e.toString());
            }
        }
```


## 使用方法:

### 1:代码使用
    
``` java

      //最新APK的下载地址
      public static final String APK_URL = "http://mobile.ac.qq.com/qqcomic_android.apk";
      //下载后的APK的命名
      public static final String APK_NAME = "update";

      //下载
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
      
```

### 默认下载路径:
``` java

     /Android/data/包名/cache/

```

# MNUpdateAPK
Android APK 版本更新的下载和安装

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
	     compile 'com.github.maning0303:MNUpdateAPK:v1.0.0'
	}
```

### 源码添加(建议使用这种)：
#### 直接下载library_update这个libary的InstallUtols这个类就能使用了，很简单的一个类

## 使用方法:

### 1:manifest中申明FileProvider：

``` java

    <manifest>
        ...
        <application>
            ...
            <provider
                android:name="android.support.v4.content.FileProvider"
                android:authorities="包名.fileprovider"
                android:exported="false"
                android:grantUriPermissions="true">
                <meta-data
                      android:name="android.support.FILE_PROVIDER_PATHS"
                      android:resource="@xml/file_paths" />
            </provider>
            ...
        </application>
    </manifest>

```

### 2: res/xml中定义对外暴露的文件夹路径：

``` java

    <?xml version="1.0" encoding="utf-8"?>
    <paths>

        <!--升级-->
        <external-cache-path
            name="update_external_cache"
            path="" />

        <cache-path
            name="update_cache"
            path="" />

    </paths>

```

### 3:代码使用
    
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

                      InstallUtils.installAPK(context, path, "com.maning.mnupdateapk.fileProvider");
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

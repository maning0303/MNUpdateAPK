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

### 源码添加(建议使用这种)：
#### 直接下载library_update这个libary的InstallUtols这个类就能使用了，很简单的一个类

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

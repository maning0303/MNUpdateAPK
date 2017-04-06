# MNUpdateAPK
Android APK 版本更新的下载和安装

##功能：
    1：下载APK
    2：安装APK
    
##截图:
![image](https://github.com/maning0303/MNUpdateAPK/blob/master/screenshots/001.gif)


## 如何添加
###Gradle添加：
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

### 源码添加：
#### 直接下载library_update这个libary的InstallUtols这个类就能使用了，很简单的一个类

##使用方法:  
    
``` java
      //下载位置
      public static final String CACHE_FILE_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "MNUpdateAPK";
      //最新APK的下载地址
      public static final String APK_URL = "http://download.fir.im/v2/app/install/56dd4bb7e75e2d27f2000046?download_token=e415c0fd1ac3b7abcb65ebc6603c59d9";
      //下载后的APK的命名
      public static final String APK_NAME = "update";
      
      //下载
      new InstallUtils(context, APK_URL, CACHE_FILE_PATH, APK_NAME, new InstallUtils.DownloadCallBack() {
            @Override
            public void onStart() {
                Log.i(TAG, "onStart");
                //开始下载.....
            }

            @Override
            public void onComplete(String path) {
                Log.i(TAG, "onComplete:" + path);
                //完成下载.....
                
                //安装APK
                InstallUtils.installAPK(context, path);
            }

            @Override
            public void onLoading(long total, long current) {
                Log.i(TAG, "onLoading:-----total:" + total + ",current:" + current);
                //正在下载...
            }

            @Override
            public void onFail(Exception e) {
                Log.i(TAG, "onFail:" + e.getMessage());
                //下载失败...
            }

        }).downloadAPK();
      
``` 

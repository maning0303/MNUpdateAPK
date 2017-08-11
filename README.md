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
	     compile 'com.github.maning0303:MNUpdateAPK:V1.0.5'
	}
```

## 使用步骤:
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

### 3:代码使用
    
``` java

      //最新APK的下载地址
      public static final String APK_URL = "http://mobile.ac.qq.com/qqcomic_android.apk";
      //下载后的APK的命名
      public static final String APK_NAME = "update";

      new InstallUtils(context, APK_URL, APK_NAME, new InstallUtils.DownloadCallBack() {
          @Override
          public void onStart() {
              Log.i(TAG, "InstallUtils---onStart");
              tv_progress.setText("0%");
          }

          @Override
          public void onComplete(String path) {
              Log.i(TAG, "InstallUtils---onComplete:" + path);

              /**
               * 安装APK工具类
               * @param context       上下文
               * @param filePath      文件路径
               * @param authorities   ---------Manifest中配置provider的authorities字段---------
               * @param callBack      安装界面成功调起的回调
               */
              InstallUtils.installAPK(context, path, getPackageName() + ".fileProvider", new InstallUtils.InstallCallBack() {
                  @Override
                  public void onSuccess() {
                      Toast.makeText(context, "正在安装程序", Toast.LENGTH_SHORT).show();
                  }

                  @Override
                  public void onFail(Exception e) {
                      Toast.makeText(context, "安装失败:" + e.toString(), Toast.LENGTH_SHORT).show();
                  }
              });
              tv_progress.setText("100%");
          }

          @Override
          public void onLoading(long total, long current) {
              Log.i(TAG, "InstallUtils----onLoading:-----total:" + total + ",current:" + current);
              tv_progress.setText((int) (current * 100 / total)+"%");
          }

          @Override
          public void onFail(Exception e) {
              Log.i(TAG, "InstallUtils---onFail:" + e.getMessage());
          }

      }).downloadAPK();
      
```

### 使用注意的地方:
#### 安装APK的第三个参数authorities 必须和在Manifest 中配置的authorities一致

### 默认下载路径:
``` java

     /Android/data/包名/cache/

```


## 推荐:
Name | Describe |
--- | --- |
[MNUpdateAPK](https://github.com/maning0303/MNUpdateAPK) | Android APK 版本更新的下载和安装,适配7.0,简单方便。 |
[MNImageBrowser](https://github.com/maning0303/MNImageBrowser) | 交互特效的图片浏览框架,微信向下滑动动态关闭 |
[MNCalendar](https://github.com/maning0303/MNCalendar) | 简单的日历控件练习，水平方向日历支持手势滑动切换，跳转月份；垂直方向日历选取区间范围。 |
[MClearEditText](https://github.com/maning0303/MClearEditText) | 带有删除功能的EditText |
[MNCrashMonitor](https://github.com/maning0303/MNCrashMonitor) | Debug监听程序崩溃日志,展示崩溃日志列表，方便自己平时调试。 |
[MNProgressHUD](https://github.com/maning0303/MNProgressHUD) | MNProgressHUD是对常用的自定义弹框封装,加载ProgressDialog,状态显示的StatusDialog和自定义Toast,支持背景颜色,圆角,边框和文字的自定义。 |
[MNXUtilsDB](https://github.com/maning0303/MNXUtilsDB) | xUtils3 数据库模块单独抽取出来，方便使用。 |
[MNVideoPlayer](https://github.com/maning0303/MNVideoPlayer) | SurfaceView + MediaPlayer 实现的视频播放器，支持横竖屏切换，手势快进快退、调节音量，亮度等。------代码简单，新手可以看一看。 |
[MNZXingCode](https://github.com/maning0303/MNZXingCode) | 快速集成二维码扫描和生成二维码 |
[MNChangeSkin](https://github.com/maning0303/MNChangeSkin) | Android夜间模式，通过Theme实现 |
[SwitcherView](https://github.com/maning0303/SwitcherView) | 垂直滚动的广告栏文字展示。 |

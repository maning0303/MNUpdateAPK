# MNUpdateAPK
Android APK 版本更新的下载和安装,适配7.0，8.0下载安装
[![](https://jitpack.io/v/maning0303/MNUpdateAPK.svg)](https://jitpack.io/#maning0303/MNUpdateAPK)

## 功能：
    1：下载APK
    2：安装APK
    
## 截图:
![](https://github.com/maning0303/MNUpdateAPK/raw/master/screenshots/002.jpg)
##### gif 比较慢
![](https://github.com/maning0303/MNUpdateAPK/raw/master/screenshots/001.gif)


## 如何添加(请认真读完下面步骤和注意事项)
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
	     compile 'com.github.maning0303:MNUpdateAPK:V2.0.0'
	}
```

## 使用步骤:
### 1:在Manifest.xml中添加配置
``` gradle

      <!--网络权限问题-->
      <uses-permission android:name="android.permission.INTERNET"/>
      <!--8.0安装需要的权限-->
      <uses-permission android:name="android.permission.REQUEST_INSTALL_PACKAGES" />
      <!--读写权限-->
      <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"/>
      <uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE"/>

```

### 2:代码使用
#### 本地下载安装：
    
``` java

      //下载APK
      InstallUtils.with(this)
              //必须-下载地址
              .setApkUrl(Constants.APK_URL_03)
              //非必须-下载保存的文件的完整路径+/name.apk，使用自定义路径需要获取读写权限
              .setApkPath(Constants.APK_SAVE_PATH)
              //非必须-下载回调
              .setCallBack(new InstallUtils.DownloadCallBack() {
                  @Override
                  public void onStart() {
                     //下载开始
                  }
      
                  @Override
                  public void onComplete(String path) {
                      //下载完成
                      //安装APK
                      /**
                       * 安装APK工具类
                       * @param activity       上下文
                       * @param filePath      文件路径
                       * @param callBack      安装界面成功调起的回调
                       */
                      InstallUtils.installAPK(activity, path, new InstallUtils.InstallCallBack() {
                          @Override
                          public void onSuccess() {
                              //onSuccess：表示系统的安装界面被打开
                              //防止用户取消安装，在这里可以关闭当前应用，以免出现安装被取消
                              Toast.makeText(context, "正在安装程序", Toast.LENGTH_SHORT).show();
                          }
                      
                          @Override
                          public void onFail(Exception e) {
                              Toast.makeText(context, "安装失败:" + e.toString(), Toast.LENGTH_SHORT).show();
                          }
                      });
                  }
      
                  @Override
                  public void onLoading(long total, long current) {
                     //下载中
                  }
      
                  @Override
                  public void onFail(Exception e) {
                      //下载失败
                  }
      
                  @Override
                  public void cancle() {
                      //下载取消
                  }
              })
              //开始下载
              .startDownload();
              
              
      //取消下载
      InstallUtils.cancleDownload();
      
      
      //单独设置下载监听
      InstallUtils.setDownloadCallBack(new InstallUtils.DownloadCallBack() {
                  @Override
                  public void onStart() {
                      
                  }
      
                  @Override
                  public void onComplete(String path) {
      
                  }
      
                  @Override
                  public void onLoading(long total, long current) {
      
                  }
      
                  @Override
                  public void onFail(Exception e) {
      
                  }
                  
                  @Override
                  public void cancle() {
                      
                  }
              });
              
              
      //安装APK
      /**
       * 安装APK工具类
       * @param activity       上下文
       * @param filePath      文件路径
       * @param callBack      安装界面成功调起的回调
       */
      InstallUtils.installAPK(activity, path, new InstallUtils.InstallCallBack() {
          @Override
          public void onSuccess() {
              //onSuccess：表示系统的安装界面被打开
              //防止用户取消安装，在这里可以关闭当前应用，以免出现安装被取消
              Toast.makeText(context, "正在安装程序", Toast.LENGTH_SHORT).show();
          }
      
          @Override
          public void onFail(Exception e) {
              Toast.makeText(context, "安装失败:" + e.toString(), Toast.LENGTH_SHORT).show();
          }
      });
      
```

#### 浏览器下载安装：
``` java

     //通过浏览器去下载APK
     InstallUtils.installAPKWithBrower(this, APK_URL);

```


### 默认下载路径（支持自定义下载路径）:
``` java
        
     /Android/data/包名/cache/
     或者
     /data/data/包名/cache/

```

### 注意注意注意:
##### 一加手机8.0碰到了安装解析失败问题请添加下面判断
``` java

         if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
             //先获取是否有安装未知来源应用的权限
             boolean haveInstallPermission = getPackageManager().canRequestPackageInstalls();
             if (!haveInstallPermission) {
                 //跳转设置开启允许安装
                 Uri packageURI = Uri.parse("package:"+context.getPackageName());
                 Intent intent =new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES,packageURI);
                 startActivityForResult(intent,1000);
                 return;
             }
         }
         //安装APK代码
        

```

## 混淆注意:
##### 请添加okhttp3混淆

## 版本记录:
##### 版本 V2.0.0:
    1.升级下载，使用okhttp下载
    2.优化安装代码，使用startActivityForResult()

##### 版本 V1.1.8:
    1.可以取消下载
    2.链式调用
    3.版本改动大没需要重新设置代码
    
##### 版本 V1.1.7:
    1.可以自定义下载路径
    2.删除安装后kill自己代码
    3.优化代码



## 推荐:
Name | Describe |
--- | --- |
[GankMM](https://github.com/maning0303/GankMM) | （Material Design & MVP & Retrofit + OKHttp & RecyclerView ...）Gank.io Android客户端：每天一张美女图片，一个视频短片，若干Android，iOS等程序干货，周一到周五每天更新，数据全部由 干货集中营 提供,持续更新。 |
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
[MNPasswordEditText](https://github.com/maning0303/MNPasswordEditText) | 类似微信支付宝的密码输入框。 |
[MNSwipeToLoadDemo](https://github.com/maning0303/MNSwipeToLoadDemo) | 利用SwipeToLoadLayout实现的各种下拉刷新效果（饿了吗，京东，百度外卖，美团外卖，天猫下拉刷新等）。 |




叮呗远程问诊 SDK 集成文档
--------

# 使用方式


### Step 1. 在 project 层级的 build.gradle 中，添加仓库地址:

```
allprojects {
    repositories {
        ...
        maven { url "https://jitpack.io" }
    }
}
```


### Step 2. 在主 module 的 build.gradle 中添加依赖：

- 把下面的`$Version$`替换为最新版本号 ![Download](https://api.bintray.com/packages/dingbei/maven/diagnose/images/download.svg?version=2.5) 

```
implementation 'com.dottmed.dingbei:diagnose:$Version$'
```

>如有依赖冲突，可参考下面示例exclude对应冲突的依赖（大的版本修改涉及到api改动可能无法解决）
```
implementation ('com.dottmed.dingbei:diagnose:$Version$') {
    exclude group: 'com.squareup.okhttp3', module: 'okhttp'
}
```

>以下是本SDK用到的部分库及其版本
```
'com.squareup.okhttp3:okhttp:3.11.0'
'com.squareup.okio:okio:1.14.0'
'pub.devrel:easypermissions:1.3.0'
'com.github.bumptech.glide:glide:4.8.0'
'jp.wasabeef:glide-transformations:3.0.1'
'com.alibaba:fastjson:1.2.8'
'de.hdodenhof:circleimageview:2.2.0'
'com.makeramen:roundedimageview:2.3.0'
'com.scwang.smartrefresh:SmartRefreshLayout:1.1.0-alpha-22'
'com.scwang.smartrefresh:SmartRefreshHeader:1.1.0-alpha-22'
'org.java-websocket:Java-WebSocket:1.3.8'
'com.github.w446108264:XhsEmoticonsKeyboard:2.0.4'
'cn.finalteam:toolsfinal:1.1.1'
'fm.jiecao:jiecaovideoplayer:5.5.4'
'io.reactivex.rxjava2:rxjava:2.2.3'
'io.reactivex.rxjava2:rxandroid:2.1.0'
'org.greenrobot:eventbus:3.0.0'
'com.tencent.liteav:LiteAVSDK_TRTC:6.6.7458'
```

### Step 3. 在主 module 的 AndroidManifest.xml 中添加需要的权限：


```
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
<uses-permission android:name="android.permission.GET_ACCOUNTS"/>
<uses-permission android:name="android.permission.CAMERA"/>
<uses-permission android:name="android.permission.FLASHLIGHT" />
<uses-permission android:name="android.permission.RECORD_AUDIO" />
<uses-permission android:name="android.permission.MODIFY_AUDIO_SETTINGS" />
<uses-permission android:name="android.permission.SYSTEM_ALERT_WINDOW" />

```


### Step 4. 在主 module 中添加需要 Android 7.0 适配：

> 1.在 res 文件夹下创建 xml 文件夹，创建 file_paths.xml

```
<resources>
    <paths>
        <external-path name="images" path=""/>
        <external-path name="rc_external_path" path="."/>
        <root-path name="root_path" path="."/>
    </paths>
</resources>

```

> 2.在 AndroidManifest.xml 中定义

```
<!--7.0适配-->
<provider
    android:name="android.support.v4.content.FileProvider"
    android:authorities="${applicationId}.FileProvider"
    android:exported="false"
    android:grantUriPermissions="true">
    <meta-data android:name="android.support.FILE_PROVIDER_PATHS"
        android:resource="@xml/file_paths" />
</provider>
```


### Step 5. 在 Application 的 onCreate() 中调用初始化代码：


```
public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        ...
        /**
         * 初始化 SDK
         * @param applicationContext 上下文
         * @param fileProvider 是app中manifest中定义的<provider>中的authorities
         * @param snType 来源标识(区分大小写)
         * @param release 是否为正式环境：true 为正式环境，false 为测试环境
         */
       DiagnoseUtil.init(Context applicationContext, String fileProvider, String type, boolean release);
        
    }
}
```


### Step 6. 在需要用到远程问诊处调用：

```
/**
 * 调起问诊
 * @param context 上下文
 * @param app_key 身份认证 Key
 * @param inquiry_no 病程 id
 * @param name 病人名称
 * @param idno 病人身份证号
 * @param sign 病人体征（没有可为null）
 */
DiagnoseUtil.diagnose(Context context, String app_key, String inquiry_no, String name, String idno, SignBean sign);
```

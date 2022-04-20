<p align="center" >
  <b style = "font-size:55px">Multi-Progress</b>
   <br>
   <br>
   <a href = "https://github.com/ZBL-Kiven/">
   <img src = "https://img.shields.io/static/v1?label=By&message=ZBL-Kiven&color=2af"/>
   </a>
   <a href = "https://github.com/ZBL-Kiven/album">
      <img src = "https://img.shields.io/static/v1?label=platform&message=Android&color=6bf"/>
   </a>
   <a href = "https://github.com/ZBL-Kiven">
      <img src = "https://img.shields.io/static/v1?label=author&message=ZJJ&color=9cf"/>
  </a>
  <a href = "https://developer.android.google.cn/jetpack/androidx">
      <img src = "https://img.shields.io/static/v1?label=usage&message=Multi-Progress&color=8ce"/>
  </a>
  <a href = "https://www.android-doc.com/guide/components/android7.0.html">
      <img src = "https://img.shields.io/static/v1?label=minVersion&message=5.0&color=cce"/>
  </a>
</p>



## Introduction：

###### 基于 AIDL 的简单秒用多进程框架，使用它可以非常轻松的完成多进程的启动和通信，就像 Listener 一样。

## Features：

* 支持 启动 子进程(:Remote) / 主进程(com.xx.xxxx)。
* 支持 自动心跳、自我恢复。
* 支持 自动注册、注销 AIDL 服务，无需考虑关闭和回收。
* 支持 任意线程调用或传递参数。
* 支持 设置 Log 接收器。

## Usage:

### 一、在 Manifest Application 定义你需要运行在多进程的 Activity。

```html
 <activity
      android:name=".act.RemoteActivity" // replace to your activity.
      android:process="com.zj.multi_progress" // use this scheme or override with RemoteService.[1.1]
      android:exported="true" //for android P . recommend to set.>
      <intent-filter>
          <action android:name="com.zj.remoteTest.act.RemoteActivity"  // importante ，see [1.2]
                  /> 
          <category android:name="android.intent.category.DEFAULT" />
      </intent-filter>
 </activity>
```

[1.1] ProgressName 是 Service 预置的，可以直接使用  ‘com.zj.multi_progress’ ，如果你想使用特定的进程名称，如 [子进程]() 或 已有 [WebView]() 多进程 CachePath 的情况，则可以覆盖 RemoteService 的声明，Eg：

```html
 <service
      android:name="com.zj.multiProgress.nimbus.web.RemoteService"
      android:enabled="true"
      android:process="your custom progress name"
      android:exported="true">
      <intent-filter>
          <action android:name="com.zj.multi.service"  //must keep it
                  /> 
          <category android:name="android.intent.category.DEFAULT" />
      </intent-filter>
  </service>
```

[1.2] intent-filter - actionName 表明了多进程启动时可到达的访问路径 ，在接下来的使用中会遇到。

### 二、开启多进程

```kotlin
/**
 * @param actionName , example : "com.zj.remoteTest.act.RemoteActivity" or your custom . see [2]
 * */

ClientService.startServer(context:Context, actionName:String) {
     //todo on multiProgress started
 }
```

### 三、进程间通讯

> Client -> Server

3.1.1 Send:

```kotlin
/**
 * @param cmd: Command name, usually the event name agreed upon by both parties
 * @param level: The Level of this Command , egs: level-log , level-destroy ... has Default.
 * @param callId: Label the ID of this command, has default
 * @param content: The content , usually it is a JsonString...
 * */ 
ClientService.postToServer(cmd: String, level: Int, callId: Int, content: String)
```



3.1.2 receive:

```kotlin
ClientService.registerCmdListener { cmd, level, callId, content? ->
    //todo on received from server, params see 3.1.1
}
```

> Server -> Client

3.2.1 Send:

```kotlin
RemoteService.postToClient(cmd: String, level: Int, callId: Int, content: String) //params see 3.1.1
```

3.2.2 Receive：

```kotlin
RemoteService.registerCmdListener(lo:LifecycleOwner) { cmd, level, callId, content ->
    //todo on received from client, params see 3.1.1
}
```

至此，你已启动了一个多进程，并向发送普通的 Bus 一样完成了它们之间的通信。

### Contributing

Contributions are very welcome 🎉

### Licence :

Copyright (c) 2022 io.github zjj0888@gmail.com<br>
Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.<br>
THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NON INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
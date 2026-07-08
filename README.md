# MicSwitch-LSPosed-app

> 本项目由 CodeX 生成。

这是一个 Android 项目，路径为 `E:\app to do`。

## 功能

- 类似 Windows“录音设备选择”的输入设备列表界面。
- 可一键选择内置麦克风、有线耳机麦克风、USB 麦克风、蓝牙麦克风等 Android 已识别的输入设备。
- 普通 App 模式会保存首选设备，供本应用读取。
- LSPosed/Xposed 激活后，会在被勾选的目标应用调用 `AudioRecord.startRecording()` 或 `MediaRecorder.prepare()` 前，尝试设置 `preferredDevice`。

## 构建结果

Debug APK:

`E:\app to do\app\build\outputs\apk\debug\app-debug.apk`

## 使用步骤

1. 安装 APK。
2. 打开“麦克风切换器”，授权录音权限；如果使用蓝牙设备，Android 12+ 还需要蓝牙连接权限。
3. 连接需要使用的输入设备，点“刷新”，选择一个设备。
4. 在 LSPosed 中启用该模块。
5. 在模块作用域中勾选需要强制切换麦克风的目标应用。
6. 重启目标应用，必要时重启手机。

## 重要限制

Android 没有开放“全系统默认麦克风”的普通应用 API。这个项目使用的是 Android 官方的 `setPreferredDevice()` 能力：

- 对普通应用：只能影响当前应用自己创建的录音会话。
- 对 LSPosed 模式：只能影响已在 LSPosed 作用域里勾选、并且使用 `AudioRecord` 或 `MediaRecorder` 标准 API 的应用。
- 如果某个应用使用厂商私有音频栈、WebRTC 深度封装、native 直连，或者 ROM 禁止目标输入设备，可能无法强制切换。
- 蓝牙麦克风是否可用还受系统蓝牙 SCO/通话音频策略影响。

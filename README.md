# MicSwitch-LSPosed-app

> 本项目由 CodeX 生成。

MicSwitch-LSPosed-app 是一款 Android 麦克风输入设备切换工具。它提供类似 Windows“录音设备选择”的界面，让用户可以在手机已识别的输入设备之间快速选择，例如内置麦克风、有线耳机麦克风、USB 麦克风和蓝牙麦克风。

## 下载

请前往 GitHub Releases 下载最新版 APK：

https://github.com/ShuimoqingzhuAql/MicSwitch-LSPosed-app/releases

当前版本：`1.0`

## 主要功能

- 以列表形式显示 Android 当前识别到的录音输入设备。
- 一键选择首选麦克风输入设备。
- 支持内置麦克风、有线耳机麦克风、USB 麦克风、蓝牙麦克风等常见输入设备。
- 支持 LSPosed/Xposed 模块模式，尝试让已勾选的目标应用使用所选输入设备。

## 使用方法

1. 从 Releases 页面下载并安装 APK。
2. 打开应用，授予录音权限。
3. 如果要使用蓝牙输入设备，请在 Android 12 及以上系统授予蓝牙连接权限。
4. 连接耳机、USB 麦克风或蓝牙耳机后，点击“刷新”。
5. 在设备列表中选择需要使用的输入设备。
6. 如需影响其他应用，请在 LSPosed 中启用本模块，并在作用域中勾选目标应用。
7. 重启目标应用，必要时重启手机。

## 注意事项

Android 普通应用无法修改全系统默认麦克风。本项目使用 Android 官方 `setPreferredDevice()` 能力，并通过 LSPosed/Xposed 在目标应用创建录音会话时尝试指定输入设备。

- 普通模式下，选择结果主要供本应用自身使用。
- LSPosed 模式下，仅对已勾选作用域、并使用 `AudioRecord` 或 `MediaRecorder` 标准录音 API 的应用生效。
- 某些应用使用私有音频栈、深度封装的 WebRTC、native 直连录音或厂商定制逻辑时，可能无法强制切换。
- 蓝牙麦克风是否可用还会受到系统蓝牙 SCO 和通话音频策略影响。

## License

MIT License

package com.example.micselector;

import android.media.AudioDeviceInfo;

final class DeviceNames {
    private DeviceNames() {
    }

    static String typeName(int type) {
        switch (type) {
            case AudioDeviceInfo.TYPE_BUILTIN_MIC:
                return "内置麦克风";
            case AudioDeviceInfo.TYPE_WIRED_HEADSET:
                return "有线耳机麦克风";
            case AudioDeviceInfo.TYPE_USB_DEVICE:
            case AudioDeviceInfo.TYPE_USB_HEADSET:
                return "USB 麦克风";
            case AudioDeviceInfo.TYPE_BLUETOOTH_SCO:
            case AudioDeviceInfo.TYPE_BLE_HEADSET:
                return "蓝牙麦克风";
            case AudioDeviceInfo.TYPE_TELEPHONY:
                return "通话输入";
            case AudioDeviceInfo.TYPE_FM_TUNER:
                return "FM 输入";
            case AudioDeviceInfo.TYPE_HDMI:
            case AudioDeviceInfo.TYPE_HDMI_ARC:
            case AudioDeviceInfo.TYPE_HDMI_EARC:
                return "HDMI 输入";
            case AudioDeviceInfo.TYPE_IP:
                return "网络音频输入";
            case AudioDeviceInfo.TYPE_BUS:
                return "系统总线输入";
            case AudioDeviceInfo.TYPE_REMOTE_SUBMIX:
                return "远程混音输入";
            default:
                return "输入设备";
        }
    }

    static String productName(AudioDeviceInfo device) {
        CharSequence name = device.getProductName();
        if (name == null || name.length() == 0) {
            return "";
        }
        return name.toString();
    }

    static String address(AudioDeviceInfo device) {
        try {
            String address = device.getAddress();
            return address == null ? "" : address;
        } catch (SecurityException ignored) {
            return "";
        }
    }

    static String label(AudioDeviceInfo device) {
        String product = productName(device);
        String type = typeName(device.getType());
        if (product.length() == 0 || product.equals(type)) {
            return type;
        }
        return type + " - " + product;
    }
}

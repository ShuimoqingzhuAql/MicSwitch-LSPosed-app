package com.example.micselector;

import android.app.AndroidAppHelper;
import android.app.Application;
import android.database.Cursor;
import android.media.AudioDeviceInfo;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.net.Uri;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class MicSelectorHook implements IXposedHookLoadPackage {
    private static final Uri CONFIG_URI = Uri.parse("content://com.example.micselector.config/current");

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam loadPackageParam) {
        String packageName = loadPackageParam.packageName;
        if (packageName == null
                || packageName.equals("com.example.micselector")
                || packageName.equals("android")
                || packageName.equals("com.android.systemui")) {
            return;
        }

        XposedHelpers.findAndHookMethod(AudioRecord.class, "startRecording", new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) {
                applyToAudioRecord((AudioRecord) param.thisObject, packageName);
            }
        });

        XposedHelpers.findAndHookMethod(MediaRecorder.class, "prepare", new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) {
                applyToMediaRecorder((MediaRecorder) param.thisObject, packageName);
            }
        });
    }

    private void applyToAudioRecord(AudioRecord record, String packageName) {
        try {
            AudioDeviceInfo device = findPreferredDevice();
            if (device != null) {
                record.setPreferredDevice(device);
                XposedBridge.log("MicSelector: applied " + device.getType() + " to AudioRecord in " + packageName);
            }
        } catch (Throwable throwable) {
            XposedBridge.log("MicSelector: AudioRecord hook failed in " + packageName + ": " + throwable);
        }
    }

    private void applyToMediaRecorder(MediaRecorder recorder, String packageName) {
        try {
            AudioDeviceInfo device = findPreferredDevice();
            if (device != null) {
                recorder.setPreferredDevice(device);
                XposedBridge.log("MicSelector: applied " + device.getType() + " to MediaRecorder in " + packageName);
            }
        } catch (Throwable throwable) {
            XposedBridge.log("MicSelector: MediaRecorder hook failed in " + packageName + ": " + throwable);
        }
    }

    private AudioDeviceInfo findPreferredDevice() {
        Application app = AndroidAppHelper.currentApplication();
        if (app == null) {
            return null;
        }

        Selection selection = readSelection(app);
        if (!selection.enabled) {
            return null;
        }

        AudioManager audioManager = (AudioManager) app.getSystemService(Application.AUDIO_SERVICE);
        if (audioManager == null) {
            return null;
        }

        AudioDeviceInfo[] devices = audioManager.getDevices(AudioManager.GET_DEVICES_INPUTS);
        AudioDeviceInfo typeFallback = null;
        for (AudioDeviceInfo device : devices) {
            if (!device.isSource()) {
                continue;
            }
            if (selection.deviceId >= 0 && device.getId() == selection.deviceId) {
                return device;
            }
            if (device.getType() == selection.deviceType) {
                String product = safeProductName(device);
                String address = safeAddress(device);
                if (selection.productName.equals(product) || selection.address.equals(address)) {
                    return device;
                }
                if (typeFallback == null) {
                    typeFallback = device;
                }
            }
        }
        return typeFallback;
    }

    private Selection readSelection(Application app) {
        Selection selection = new Selection();
        Cursor cursor = null;
        try {
            cursor = app.getContentResolver().query(CONFIG_URI, null, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                selection.enabled = cursor.getInt(cursor.getColumnIndexOrThrow(MicConfig.KEY_ENABLED)) == 1;
                selection.deviceId = cursor.getInt(cursor.getColumnIndexOrThrow(MicConfig.KEY_DEVICE_ID));
                selection.deviceType = cursor.getInt(cursor.getColumnIndexOrThrow(MicConfig.KEY_DEVICE_TYPE));
                selection.productName = cursor.getString(cursor.getColumnIndexOrThrow(MicConfig.KEY_PRODUCT_NAME));
                selection.address = cursor.getString(cursor.getColumnIndexOrThrow(MicConfig.KEY_ADDRESS));
            }
        } catch (Throwable throwable) {
            XposedBridge.log("MicSelector: cannot read selection: " + throwable);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return selection;
    }

    private String safeProductName(AudioDeviceInfo device) {
        CharSequence name = device.getProductName();
        return name == null ? "" : name.toString();
    }

    private String safeAddress(AudioDeviceInfo device) {
        try {
            String address = device.getAddress();
            return address == null ? "" : address;
        } catch (Throwable ignored) {
            return "";
        }
    }

    private static class Selection {
        boolean enabled = true;
        int deviceId = -1;
        int deviceType = -1;
        String productName = "";
        String address = "";
    }
}

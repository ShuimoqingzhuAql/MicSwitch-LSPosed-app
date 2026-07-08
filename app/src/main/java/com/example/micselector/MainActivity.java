package com.example.micselector;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.AudioDeviceInfo;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {
    private AudioManager audioManager;
    private SharedPreferences prefs;
    private LinearLayout deviceList;
    private Switch enabledSwitch;
    private TextView statusText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        prefs = getSharedPreferences(MicConfig.PREFS, MODE_PRIVATE);
        deviceList = findViewById(R.id.deviceList);
        enabledSwitch = findViewById(R.id.enabledSwitch);
        statusText = findViewById(R.id.statusText);
        Button refreshButton = findViewById(R.id.refreshButton);

        enabledSwitch.setChecked(prefs.getBoolean(MicConfig.KEY_ENABLED, true));
        enabledSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            prefs.edit().putBoolean(MicConfig.KEY_ENABLED, isChecked).apply();
            renderDevices();
        });
        refreshButton.setOnClickListener(v -> renderDevices());

        requestUsefulPermissions();
        renderDevices();
    }

    private void requestUsefulPermissions() {
        List<String> permissions = new ArrayList<>();
        if (checkSelfPermission(Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.RECORD_AUDIO);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
                && checkSelfPermission(Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.BLUETOOTH_CONNECT);
        }
        if (!permissions.isEmpty()) {
            requestPermissions(permissions.toArray(new String[0]), 7);
        }
    }

    private void renderDevices() {
        deviceList.removeAllViews();
        AudioDeviceInfo[] devices = audioManager.getDevices(AudioManager.GET_DEVICES_INPUTS);
        int selectedId = prefs.getInt(MicConfig.KEY_DEVICE_ID, -1);
        int selectedType = prefs.getInt(MicConfig.KEY_DEVICE_TYPE, -1);
        int inputCount = 0;

        for (AudioDeviceInfo device : devices) {
            if (!device.isSource()) {
                continue;
            }
            inputCount++;
            deviceList.addView(createDeviceRow(device, selectedId, selectedType));
        }

        if (inputCount == 0) {
            TextView empty = new TextView(this);
            empty.setText("未发现可用输入设备。请连接耳机、USB 麦克风或蓝牙耳机后刷新。");
            empty.setTextColor(getColor(R.color.muted));
            empty.setTextSize(15);
            deviceList.addView(empty);
        }

        String selectedLabel = prefs.getString(MicConfig.KEY_LABEL, "未选择");
        String mode = enabledSwitch.isChecked() ? "已启用" : "已暂停";
        statusText.setText(mode + " | 当前选择：" + selectedLabel + " | 发现 " + inputCount + " 个输入设备");
    }

    private View createDeviceRow(AudioDeviceInfo device, int selectedId, int selectedType) {
        boolean selected = device.getId() == selectedId || selectedId == -1 && device.getType() == selectedType;
        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.VERTICAL);
        row.setGravity(Gravity.CENTER_VERTICAL);
        row.setBackgroundResource(selected ? R.drawable.device_card_selected : R.drawable.device_card);
        row.setClickable(true);
        row.setMinimumHeight(dp(82));
        LinearLayout.LayoutParams rowParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        rowParams.setMargins(0, 0, 0, dp(10));
        row.setLayoutParams(rowParams);

        TextView name = new TextView(this);
        name.setText(DeviceNames.label(device));
        name.setTextColor(getColor(R.color.ink));
        name.setTextSize(17);
        name.setTypeface(null, selected ? 1 : 0);
        row.addView(name);

        TextView details = new TextView(this);
        String address = DeviceNames.address(device);
        String detailText = "ID " + device.getId() + " | Type " + device.getType();
        if (address.length() > 0) {
            detailText += " | " + address;
        }
        details.setText(detailText);
        details.setTextColor(getColor(R.color.muted));
        details.setTextSize(13);
        details.setPadding(0, dp(5), 0, 0);
        row.addView(details);

        row.setOnClickListener(v -> selectDevice(device));
        return row;
    }

    private void selectDevice(AudioDeviceInfo device) {
        prefs.edit()
                .putBoolean(MicConfig.KEY_ENABLED, true)
                .putInt(MicConfig.KEY_DEVICE_ID, device.getId())
                .putInt(MicConfig.KEY_DEVICE_TYPE, device.getType())
                .putString(MicConfig.KEY_PRODUCT_NAME, DeviceNames.productName(device))
                .putString(MicConfig.KEY_ADDRESS, DeviceNames.address(device))
                .putString(MicConfig.KEY_LABEL, DeviceNames.label(device))
                .apply();
        enabledSwitch.setChecked(true);
        renderDevices();
    }

    private int dp(int value) {
        return Math.round(value * getResources().getDisplayMetrics().density);
    }
}

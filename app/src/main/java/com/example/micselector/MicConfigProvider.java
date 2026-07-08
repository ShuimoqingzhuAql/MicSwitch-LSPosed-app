package com.example.micselector;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;

public class MicConfigProvider extends ContentProvider {
    public static final String AUTHORITY = "com.example.micselector.config";
    public static final Uri CONFIG_URI = Uri.parse("content://" + AUTHORITY + "/current");

    @Override
    public boolean onCreate() {
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SharedPreferences prefs = getContext().getSharedPreferences(MicConfig.PREFS, 0);
        MatrixCursor cursor = new MatrixCursor(new String[]{
                MicConfig.KEY_ENABLED,
                MicConfig.KEY_DEVICE_ID,
                MicConfig.KEY_DEVICE_TYPE,
                MicConfig.KEY_PRODUCT_NAME,
                MicConfig.KEY_ADDRESS,
                MicConfig.KEY_LABEL
        });
        cursor.addRow(new Object[]{
                prefs.getBoolean(MicConfig.KEY_ENABLED, true) ? 1 : 0,
                prefs.getInt(MicConfig.KEY_DEVICE_ID, -1),
                prefs.getInt(MicConfig.KEY_DEVICE_TYPE, -1),
                prefs.getString(MicConfig.KEY_PRODUCT_NAME, ""),
                prefs.getString(MicConfig.KEY_ADDRESS, ""),
                prefs.getString(MicConfig.KEY_LABEL, "")
        });
        return cursor;
    }

    @Override
    public String getType(Uri uri) {
        return "vnd.android.cursor.item/vnd.com.example.micselector.config";
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        throw new UnsupportedOperationException("Read-only provider");
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        throw new UnsupportedOperationException("Read-only provider");
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        throw new UnsupportedOperationException("Read-only provider");
    }
}

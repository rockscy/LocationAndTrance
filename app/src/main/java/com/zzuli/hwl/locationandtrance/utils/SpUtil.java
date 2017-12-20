package com.zzuli.hwl.locationandtrance.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.zzuli.hwl.locationandtrance.MyApplication;


/**
 * Created by WangJinRui on 2017/1/26.
 */

public class SpUtil {

    private static SharedPreferences sp;

    static {
        if (sp == null) {
            sp = MyApplication.getContext().getSharedPreferences("config", Context.MODE_PRIVATE);
        }
    }

    public static boolean getBoolean(String key, boolean defValue) {
        return sp.getBoolean(key, defValue);
    }

    public static void putBoolean(String key, boolean value) {
        sp.edit().putBoolean(key, value).apply();
    }

    // 读
    public static String getString(String key, String defValue) {
        return sp.getString(key, defValue);
    }

    // 写
    public static void putString(String key, String value) {
        sp.edit().putString(key, value).apply();
    }

    public static void putInt(String key, int value) {
        sp.edit().putInt(key, value).apply();
    }

    public static int getInt(String key, int defValue) {
        return sp.getInt(key, defValue);
    }

    // 移除某个节点，根据key的唯一性来移除
    public static void remove(String key) {
        sp.edit().remove(key).apply();
    }
}

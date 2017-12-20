package com.zzuli.hwl.locationandtrance.utils;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by WangJinRui on 2017/1/23.
 */
public class ToastUtil {
    public static void show(Context context, String msg){
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }
}

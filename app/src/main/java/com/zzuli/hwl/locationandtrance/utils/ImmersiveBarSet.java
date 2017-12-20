package com.zzuli.hwl.locationandtrance.utils;

import android.app.Activity;
import android.graphics.Color;
import android.os.Build;
import android.view.View;

/**
 * Created by WangJinRui on 2017/4/29.
 */

public class ImmersiveBarSet {
    // 实现沉浸式状态栏效果
    public static void setImmersiveBar(Activity activity) {
        if (Build.VERSION.SDK_INT >= 21) {
            View decorView = activity.getWindow().getDecorView();
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            // 将状态栏设置成透明色
            activity.getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
    }
}

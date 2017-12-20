package com.zzuli.hwl.locationandtrance.utils;

import android.app.Activity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by WangJinRui on 2017/4/28.
 */

public class ActivityCollector {
    public static List<Activity> activities = new ArrayList<>();

    public static void addActivity(Activity activity) {
        activities.add(activity);
    }

    public static void removeActivity(Activity activity) {
        activities.remove(activity);
    }

    public static void finishAll() {
        for (Activity activity : activities) {
            if (!activity.isFinishing()) {
                activity.finish();
            }
        }
    }
}

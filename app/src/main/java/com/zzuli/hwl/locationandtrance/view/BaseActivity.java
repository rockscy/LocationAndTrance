package com.zzuli.hwl.locationandtrance.view;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.zzuli.hwl.locationandtrance.R;
import com.zzuli.hwl.locationandtrance.utils.ActivityCollector;
import com.zzuli.hwl.locationandtrance.utils.ImmersiveBarSet;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by WangJinRui on 2017/4/28.
 */

public class BaseActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityCollector.addActivity(this);
        init();
    }
    private void init() {
        ImmersiveBarSet.setImmersiveBar(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityCollector.removeActivity(this);
    }

}

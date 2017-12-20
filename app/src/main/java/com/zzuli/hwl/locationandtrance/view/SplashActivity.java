package com.zzuli.hwl.locationandtrance.view;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.LinearLayout;

import com.zzuli.hwl.locationandtrance.R;
import com.zzuli.hwl.locationandtrance.utils.ConstantValue;
import com.zzuli.hwl.locationandtrance.utils.ImmersiveBarSet;
import com.zzuli.hwl.locationandtrance.utils.SpUtil;
import com.zzuli.hwl.locationandtrance.widget.SimpleButton;

public class SplashActivity extends AppCompatActivity {

    /**
     * 跳过秒数
     */
    private int skipTime = 5;

    private SimpleButton mSp_skip_sb;

    /**
     * 指示器
     */
    private LinearLayout mSp_point_linear;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (skipTime == 0) {
                enterHome();
            } else {
                mSp_skip_sb.setText("跳过  " + skipTime);
                skipTime--;
                mHandler.sendEmptyMessageDelayed(0, 1000);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ImmersiveBarSet.setImmersiveBar(this);
        init();
    }

    // 判断是否是第一次进入本程序
    private void init() {
            setContentView(R.layout.activity_welcome);
            initUI();

    }
    /**
     * 初始化SimpleButton
     */
    private void initUI() {
        mSp_skip_sb = (SimpleButton) findViewById(R.id.sp_skip_sb);
        mSp_skip_sb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enterHome();
            }
        });

        mHandler.sendEmptyMessage(0);
    }

    /**
     * 进入主界面
     */
    private void enterHome() {
        mHandler.removeCallbacksAndMessages(null);
        mHandler = null;
        String userid = SpUtil.getString(ConstantValue.USER_LOGIN_INFO,"");
        if(!"".equals(userid)) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        }else {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        }

    }

}

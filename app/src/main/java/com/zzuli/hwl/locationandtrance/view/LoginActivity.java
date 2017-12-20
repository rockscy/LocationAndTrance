package com.zzuli.hwl.locationandtrance.view;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.InputType;
import android.text.Selection;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;


import com.zzuli.hwl.locationandtrance.R;
import com.zzuli.hwl.locationandtrance.bean.User;
import com.zzuli.hwl.locationandtrance.utils.ConstantValue;
import com.zzuli.hwl.locationandtrance.utils.ImmersiveBarSet;
import com.zzuli.hwl.locationandtrance.utils.SpUtil;
import com.zzuli.hwl.locationandtrance.utils.ToastUtil;

import org.litepal.crud.DataSupport;

import java.util.List;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private ProgressDialog mProgressDialog;

    private EditText mUsernameEdt;

    private EditText mPwdEdt;

    private Button mCleanBt;

    private Button mLockBt;

    // 密码是否明文
    private boolean pwdIsLock;

    private final int loginCode = 0;

    private final int registCode = 1;

    private String userId;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case loginCode:
                    ToastUtil.show(LoginActivity.this, "登录成功！");
                    break;
                case registCode:
                    ToastUtil.show(LoginActivity.this, "注册成功！");
                    break;
            }
            mProgressDialog.dismiss();
            loginSuccess(userId);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ImmersiveBarSet.setImmersiveBar(this);
        setContentView(R.layout.activity_login);
        init();
    }

    private void init() {
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setTitle("提示");
        mProgressDialog.setMessage("请稍后...");
        mProgressDialog.setCancelable(false);
        mUsernameEdt = (EditText) findViewById(R.id.username_edt);
        mPwdEdt = (EditText) findViewById(R.id.pwd_edt);
        mCleanBt = (Button) findViewById(R.id.clean_bt);
        mLockBt = (Button) findViewById(R.id.lock_bt);
        Button loginBt = (Button) findViewById(R.id.login_bt);
        Button registBt = (Button) findViewById(R.id.regist_bt);

        mCleanBt.setOnClickListener(this);
        mLockBt.setOnClickListener(this);
        loginBt.setOnClickListener(this);
        registBt.setOnClickListener(this);

        mUsernameEdt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    mCleanBt.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == 0) {
                    mCleanBt.setVisibility(View.INVISIBLE);
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.clean_bt:
                mUsernameEdt.getText().clear();
                break;
            case R.id.lock_bt:
                if (pwdIsLock) {
                    mPwdEdt.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    mLockBt.setBackgroundResource(R.mipmap.unlook_pwd);
                } else {
                    mPwdEdt.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    mLockBt.setBackgroundResource(R.mipmap.look_pwd);
                }
                // 使光标始终在最后一位
                Editable editable = mPwdEdt.getText();
                Selection.setSelection(editable, editable.length());

                pwdIsLock = !pwdIsLock;
                break;
            case R.id.login_bt:
                // 登录模块
                String usernameStr = mUsernameEdt.getText().toString();
                String pwdStr = mPwdEdt.getText().toString();

                if (TextUtils.isEmpty(usernameStr) || TextUtils.isEmpty(pwdStr)) {
                    ToastUtil.show(this, "账号或密码不能为空！");
                    return;
                }

                List<User> users = DataSupport.where("username = ? and password = ?", usernameStr, pwdStr).find(User.class);
                if (users!=null && users.size()>0) {
                    // 登陆成功
                    mProgressDialog.show();
                    userId = users.get(0).getUsername();
                    mHandler.sendEmptyMessageDelayed(loginCode, 2000);
                } else {
                    ToastUtil.show(this, "账号或密码错误！");
                    return;
                }

                break;
            case R.id.regist_bt:
                String usernameRgs = mUsernameEdt.getText().toString();
                String pwdRgs = mPwdEdt.getText().toString();

                if (TextUtils.isEmpty(usernameRgs) || TextUtils.isEmpty(pwdRgs)) {
                    ToastUtil.show(this, "账号或密码不能为空！");
                    return;
                }

                List<User> alreadyUsers = DataSupport.where("username = ?", usernameRgs).find(User.class);
                if (alreadyUsers!=null && alreadyUsers.size()>0) {
                    ToastUtil.show(this, "用户已存在！");
                    return;
                } else {
                    // 注册成功
                    mProgressDialog.show();
                    User user = new User();
                    user.setUsername(usernameRgs);
                    user.setPassword(pwdRgs);
                    user.save();

                    userId = user.getUsername();
                    mHandler.sendEmptyMessageDelayed(registCode, 2000);
                }

                break;
        }
    }

    // 登录成功
    private void loginSuccess(String userId) {
        // 保存登陆状态
        SpUtil.putBoolean(ConstantValue.USER_IS_LOGIN, true);
        // 保存此账号id
        SpUtil.putString(ConstantValue.USER_LOGIN_INFO, userId);
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}

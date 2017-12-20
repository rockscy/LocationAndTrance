package com.zzuli.hwl.locationandtrance.view;

import android.Manifest;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.telephony.SmsManager;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.fence.GeoFence;
import com.amap.api.fence.GeoFenceClient;
import com.amap.api.fence.GeoFenceListener;
import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.DPoint;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptor;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CircleOptions;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.LatLngBounds;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.maps.model.PolygonOptions;
import com.mylhyl.acp.Acp;
import com.mylhyl.acp.AcpListener;
import com.mylhyl.acp.AcpOptions;
import com.zzuli.hwl.locationandtrance.R;
import com.zzuli.hwl.locationandtrance.bean.User;
import com.zzuli.hwl.locationandtrance.utils.Const;
import com.zzuli.hwl.locationandtrance.utils.ConstantValue;
import com.zzuli.hwl.locationandtrance.utils.SpUtil;
import com.zzuli.hwl.locationandtrance.utils.ToastUtil;
import com.zzuli.hwl.locationandtrance.view.BaseActivity;
import com.zzuli.hwl.locationandtrance.view.LoginActivity;

import org.litepal.crud.DataSupport;
import org.litepal.util.LogUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener,View.OnClickListener,
        GeoFenceListener,
        AMap.OnMapClickListener,
        LocationSource,
        AMapLocationListener,
        CompoundButton.OnCheckedChangeListener {
    private View lyOption;

    private TextView tvGuide;
    private TextView tvResult;

    private EditText etRadius;

    private CheckBox cbAlertIn;
    private CheckBox cbAlertOut;
    private CheckBox cbAldertStated;

    private Button btAddFence;
    private Button btOption;
    private AMapLocationClient mlocationClient;
    private LocationSource.OnLocationChangedListener mListener;
    private AMapLocationClientOption mLocationOption;

    private MapView mMapView;
    private AMap mAMap;

    // 中心点坐标
    private LatLng centerLatLng = null;
    // 中心点marker
    private Marker centerMarker;
    private BitmapDescriptor ICON_YELLOW = BitmapDescriptorFactory
            .defaultMarker(BitmapDescriptorFactory.HUE_YELLOW);
    private BitmapDescriptor ICON_RED = BitmapDescriptorFactory
            .defaultMarker(BitmapDescriptorFactory.HUE_RED);
    private MarkerOptions markerOption = null;
    private List<Marker> markerList = new ArrayList<Marker>();
    // 当前的坐标点集合，主要用于进行地图的可视区域的缩放
    private LatLngBounds.Builder boundsBuilder = new LatLngBounds.Builder();

    // 地理围栏客户端
    private GeoFenceClient fenceClient = null;
    // 要创建的围栏半径
    private float fenceRadius = 0.0F;
    // 触发地理围栏的行为，默认为进入提醒
    private int activatesAction = GeoFenceClient.GEOFENCE_IN;
    // 地理围栏的广播action
    private static final String GEOFENCE_BROADCAST_ACTION = "com.example.geofence.round";

    // 记录已经添加成功的围栏
    private HashMap<String, GeoFence> fenceMap = new HashMap<String, GeoFence>();
    private View map_top;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        requestPermission();
        initView();
        initMap(savedInstanceState);

    }

    private void requestPermission() {
        Acp.getInstance(this).request(new AcpOptions.Builder()
                        .setPermissions(Manifest.permission.ACCESS_COARSE_LOCATION,
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                Manifest.permission.READ_PHONE_STATE)
                        .build(),
                new AcpListener() {
                    @Override
                    public void onGranted() {

                    }

                    @Override
                    public void onDenied(List<String> permissions) {
                        ToastUtil.show(MainActivity.this,"权限不够，应用可能会出现问题");
                    }
                });
    }

    private void initView() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View headerView = navigationView.getHeaderView(0);
        TextView user = (TextView) headerView.findViewById(R.id.user_textview);
        String userid = SpUtil.getString(ConstantValue.USER_LOGIN_INFO, "");
        user.append(userid+"");

    }

    private void sendMessage() {

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.SEND_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            showMissingPermissionDialog();

        }else{
            //
            startMessage();
        }
    }
    private void showMissingPermissionDialog() {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setTitle(R.string.notifyTitle);
        builder.setMessage(R.string.notifyMsg);

        // 拒绝, 退出应用
        builder.setNegativeButton(R.string.cancel,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                });

        builder.setPositiveButton(R.string.setting,

                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startAppSettings();
                    }
                });

        builder.setCancelable(false);

        builder.show();
    }
    private void startMessage() {
        handler.post(runnable);
    }
    private int sendMessageDelay = 60000;
    Runnable runnable=new Runnable() {
        @Override
        public void run() {
            // TODO Auto-generated method stub
            //要做的事情
            handler.postDelayed(this, 60000);
            send("定位成功 ");
        }
    };
    private void send(String message) {
        if(TextUtils.isEmpty(phone)) {
            ToastUtil.show(this,"您还没有设置手机号，请在侧滑菜单中设置手机号后重试！");
            return;
        }
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.SEND_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            ToastUtil.show(this,"您没有获得发送短信的权限");

        }else{
            SmsManager smsManager = SmsManager.getDefault();
            Log.e("tag",""+currentLocation.length());
            smsManager.sendTextMessage(phone,"",message+currentLocation+"",null,null);
            ToastUtil.show(this,"发送位置成功");
        }

    }
    /**
     *  启动应用的设置
     *
     * @since 2.5.0
     *
     */
    private void startAppSettings() {
        Intent intent = new Intent(
                Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.parse("package:" + getPackageName()));
        startActivity(intent);
    }
    private void initMap(Bundle savedInstanceState) {
        // 初始化地理围栏
        fenceClient = new GeoFenceClient(getApplicationContext());
        tvResult = (TextView)findViewById(R.id.tv_result);
        tvResult.setVisibility(View.GONE);
        showSetMapDialog = new Dialog(this);
        View view = LayoutInflater.from(this).inflate(R.layout.plugin_geofence_option, null);
        showSetMapDialog.setContentView(view);
        map_top = view.findViewById(R.id.map_top);
        lyOption = view.findViewById(R.id.ly_option);
        btAddFence = (Button) view.findViewById(R.id.bt_addFence);
        btOption = (Button) view.findViewById(R.id.bt_option);
        tvGuide = (TextView) view.findViewById(R.id.tv_guide);
        etRadius = (EditText) view.findViewById(R.id.et_radius);
        cbAlertIn = (CheckBox) view.findViewById(R.id.cb_alertIn);
        cbAlertOut = (CheckBox) view.findViewById(R.id.cb_alertOut);
        cbAldertStated = (CheckBox) view.findViewById(R.id.cb_alertStated);
        mMapView = (MapView) findViewById(R.id.map);
        mMapView.onCreate(savedInstanceState);
        markerOption = new MarkerOptions().draggable(true);
        init();
        String username = SpUtil.getString(ConstantValue.USER_LOGIN_INFO,"");
        List<User> users = DataSupport.where("username = ? ", username).find(User.class);
        if (users!=null && users.size()>0) {
            try{
                User user = users.get(0);
                phone = user.getSendMessagePhone();
                sendMessageDelay = Integer.parseInt(user.getSendMessageDelay());
            }catch (Exception e) {

            }
        }
    }
    private Dialog showSetMapDialog;
    private void showSetMapDialog() {
            if(showSetMapDialog.isShowing()) {
                showSetMapDialog.dismiss();
            }else {
                showSetMapDialog.show();
            }

    }
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_zhuxiao) {
            SpUtil.putInt(ConstantValue.USER_LOGIN_INFO,0);
            ToastUtil.show(this,"已注销当前帐号，请重新登录");
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return true;
        }
        if(id==R.id.action_exit) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_phone) {
            showPhoneDialog();
        }else if(id == R.id.nav_weilan) {
           showSetMapDialog();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private String phone;
    private void showPhoneDialog() {
        final EditText et = new EditText(this);
        et.setHint("11位手机号");
        if(!TextUtils.isEmpty(phone)) {
            et.setText(phone);
        }
        final EditText et1 = new EditText(this);
        et1.setHint("发送短信间隔（s）");
        et1.setInputType(InputType.TYPE_CLASS_NUMBER);
        LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.addView(et);
        linearLayout.addView(et1);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        new AlertDialog.Builder(this).setTitle("请输入手机号和短信间隔")
                .setView(linearLayout)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        String input = et.getText().toString();
                        if (input.equals("")) {
                            Toast.makeText(getApplicationContext(), "手机号不能为空！" + input, Toast.LENGTH_LONG).show();
                        }
                        else {
                            phone = input;
                            String number = et1.getText().toString();
                            try{
                                int delay = Integer.parseInt(number);
                                sendMessageDelay = delay * 1000;
                                ToastUtil.show(MainActivity.this,"设置成功");
                                String username = SpUtil.getString(ConstantValue.USER_LOGIN_INFO,"");
                                List<User> users = DataSupport.where("username = ? ", username).find(User.class);
                                if (users!=null && users.size()>0) {
                                    User user = users.get(0);
                                    user.setSendMessagePhone(phone);
                                    user.setSendMessageDelay(sendMessageDelay+"");
                                    user.update(user.getId());
                                }
                            }catch (Exception e) {
                                ToastUtil.show(MainActivity.this,"请输入正确的数字");
                            }

                        }
                    }
                })
                .setNegativeButton("取消", null)
                .show();
    }

    void init() {
        if (mAMap == null) {
            mAMap = mMapView.getMap();
            mAMap.getUiSettings().setRotateGesturesEnabled(false);
            mAMap.moveCamera(CameraUpdateFactory.zoomBy(6));
            setUpMap();
        }

        btOption.setVisibility(View.VISIBLE);
        btOption.setText(getString(R.string.hideOption));
        resetView_round();

        btAddFence.setOnClickListener(this);
        btOption.setOnClickListener(this);
        cbAlertIn.setOnCheckedChangeListener(this);
        cbAlertOut.setOnCheckedChangeListener(this);
        cbAldertStated.setOnCheckedChangeListener(this);

        IntentFilter filter = new IntentFilter(
                ConnectivityManager.CONNECTIVITY_ACTION);
        filter.addAction(GEOFENCE_BROADCAST_ACTION);
        registerReceiver(mGeoFenceReceiver, filter);

        /**
         * 创建pendingIntent
         */
        fenceClient.createPendingIntent(GEOFENCE_BROADCAST_ACTION);
        fenceClient.setGeoFenceListener(this);
        /**
         * 设置地理围栏的触发行为,默认为进入
         */
        fenceClient.setActivateAction(GeoFenceClient.GEOFENCE_IN);



    }

    /**
     * 设置一些amap的属性
     */
    private void setUpMap() {
        mAMap.setOnMapClickListener(this);
        mAMap.setLocationSource(this);// 设置定位监听
        mAMap.getUiSettings().setMyLocationButtonEnabled(true);// 设置默认定位按钮是否显示
        // 自定义系统定位蓝点
        MyLocationStyle myLocationStyle = new MyLocationStyle();
        // 自定义定位蓝点图标
        myLocationStyle.myLocationIcon(
                BitmapDescriptorFactory.fromResource(R.drawable.gps_point));
        // 自定义精度范围的圆形边框颜色
        myLocationStyle.strokeColor(Color.argb(0, 0, 0, 0));
        // 自定义精度范围的圆形边框宽度
        myLocationStyle.strokeWidth(0);
        // 设置圆形的填充颜色
        myLocationStyle.radiusFillColor(Color.argb(0, 0, 0, 0));
        // 将自定义的 myLocationStyle 对象添加到地图上
        mAMap.setMyLocationStyle(myLocationStyle);
        mAMap.setMyLocationEnabled(true);// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
        // 设置定位的类型为定位模式 ，可以由定位、跟随或地图根据面向方向旋转几种
        mAMap.setMyLocationType(AMap.LOCATION_TYPE_LOCATE);

    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mMapView.onSaveInstanceState(outState);
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
        handler.removeCallbacks(runnable);
        handler.removeMessages(1);
        handler.removeMessages(2);
        handler.removeMessages(0);
        handler = null;
        try {
            unregisterReceiver(mGeoFenceReceiver);
        } catch (Throwable e) {
        }

        if (null != fenceClient) {
            fenceClient.removeGeoFence();
        }
        if (null != mlocationClient) {
            mlocationClient.onDestroy();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_addFence :
                addFence();
                break;
            case R.id.bt_option :
                if (btOption.getText().toString()
                        .equals(getString(R.string.showOption))) {
                    lyOption.setVisibility(View.VISIBLE);
                    btOption.setText(getString(R.string.hideOption));
                } else {
                    lyOption.setVisibility(View.GONE);
                    btOption.setText(getString(R.string.showOption));
                }
                break;
            default :
                break;
        }
    }

    private void drawFence(GeoFence fence) {
        switch (fence.getType()) {
            case GeoFence.TYPE_ROUND :
            case GeoFence.TYPE_AMAPPOI :
                drawCircle(fence);
                break;
            case GeoFence.TYPE_POLYGON :
            case GeoFence.TYPE_DISTRICT :
                drawPolygon(fence);
                break;
            default :
                break;
        }
        removeMarkers();
    }

    private void drawCircle(GeoFence fence) {
        LatLng center = new LatLng(fence.getCenter().getLatitude(),
                fence.getCenter().getLongitude());
        // 绘制一个圆形
        mAMap.addCircle(new CircleOptions().center(center)
                .radius(fence.getRadius()).strokeColor(Const.STROKE_COLOR)
                .fillColor(Const.FILL_COLOR).strokeWidth(Const.STROKE_WIDTH));
        boundsBuilder.include(center);
    }

    private void drawPolygon(GeoFence fence) {
        final List<List<DPoint>> pointList = fence.getPointList();
        if (null == pointList || pointList.isEmpty()) {
            return;
        }
        for (List<DPoint> subList : pointList) {
            List<LatLng> lst = new ArrayList<LatLng>();

            PolygonOptions polygonOption = new PolygonOptions();
            for (DPoint point : subList) {
                lst.add(new LatLng(point.getLatitude(), point.getLongitude()));
                boundsBuilder.include(
                        new LatLng(point.getLatitude(), point.getLongitude()));
            }
            polygonOption.addAll(lst);

            polygonOption.strokeColor(Const.STROKE_COLOR)
                    .fillColor(Const.FILL_COLOR).strokeWidth(Const.STROKE_WIDTH);
            mAMap.addPolygon(polygonOption);
        }
    }

    Object lock = new Object();
    void drawFence2Map() {
        new Thread() {
            @Override
            public void run() {
                try {
                    synchronized (lock) {
                        if (null == fenceList || fenceList.isEmpty()) {
                            return;
                        }
                        for (GeoFence fence : fenceList) {
                            if (fenceMap.containsKey(fence.getFenceId())) {
                                continue;
                            }
                            drawFence(fence);
                            fenceMap.put(fence.getFenceId(), fence);
                        }
                    }
                } catch (Throwable e) {

                }
            }
        }.start();
    }

    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0 :
                    StringBuffer sb = new StringBuffer();
                    sb.append("添加围栏成功");

                    Toast.makeText(getApplicationContext(), sb.toString(),
                            Toast.LENGTH_SHORT).show();
                    showSetMapDialog();
                    drawFence2Map();
                    break;
                case 1 :
                    int errorCode = msg.arg1;
                    Toast.makeText(getApplicationContext(),
                            "添加围栏失败 " + errorCode, Toast.LENGTH_SHORT).show();
                    break;
                case 2 :
                    String statusStr = (String) msg.obj;
                    tvResult.setVisibility(View.VISIBLE);
                    tvResult.append(statusStr + "\n");
                    break;
                default :
                    break;
            }
        }
    };

    List<GeoFence> fenceList = new ArrayList<GeoFence>();
    @Override
    public void onGeoFenceCreateFinished(final List<GeoFence> geoFenceList,
                                         int errorCode, String customId) {
        Message msg = Message.obtain();
        if (errorCode == GeoFence.ADDGEOFENCE_SUCCESS) {
            fenceList = geoFenceList;
            msg.obj = customId;
            msg.what = 0;
        } else {
            msg.arg1 = errorCode;
            msg.what = 1;
        }
        handler.sendMessage(msg);
    }

    /**
     * 接收触发围栏后的广播,当添加围栏成功之后，会立即对所有围栏状态进行一次侦测，如果当前状态与用户设置的触发行为相符将会立即触发一次围栏广播；
     * 只有当触发围栏之后才会收到广播,对于同一触发行为只会发送一次广播不会重复发送，除非位置和围栏的关系再次发生了改变。
     */
    private BroadcastReceiver mGeoFenceReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // 接收广播
            if (intent.getAction().equals(GEOFENCE_BROADCAST_ACTION)) {
                Bundle bundle = intent.getExtras();

                String customId = bundle
                        .getString(GeoFence.BUNDLE_KEY_CUSTOMID);
                String fenceId = bundle.getString(GeoFence.BUNDLE_KEY_FENCEID);
                //status标识的是当前的围栏状态，不是围栏行为
                int status = bundle.getInt(GeoFence.BUNDLE_KEY_FENCESTATUS);
                StringBuffer sb = new StringBuffer();
                switch (status) {
                    case GeoFence.STATUS_LOCFAIL :
                        sb.append("定位失败");
                        break;
                    case GeoFence.STATUS_IN :
                        sb.append("进入围栏 ");
                        send("进入围栏 ");
                        break;
                    case GeoFence.STATUS_OUT :
                        sb.append("离开围栏 ");
                        send("离开围栏 ");
                        break;
                    case GeoFence.STATUS_STAYED :
                        sb.append("停留在围栏内 ");
                        break;
                    default :
                        break;
                }
                if(status != GeoFence.STATUS_LOCFAIL){
                    if(!TextUtils.isEmpty(customId)){
                        sb.append(" customId: " + customId);
                    }
                    sb.append(" fenceId: " + fenceId);
                }
                String str = sb.toString();
                Message msg = Message.obtain();
                msg.obj = str;
                msg.what = 2;
                handler.sendMessage(msg);

            }
        }
    };

    @Override
    public void onMapClick(LatLng latLng) {
        markerOption.icon(ICON_YELLOW);
        centerLatLng = latLng;
        addCenterMarker(centerLatLng);
        tvGuide.setBackgroundColor(getResources().getColor(R.color.gary));
        tvGuide.setText("选中的坐标：" + centerLatLng.longitude + ","
                + centerLatLng.latitude);
    }

    /**
     * 定位成功后回调函数
     */
    private String currentLocation = " ";
    @Override
    public void onLocationChanged(AMapLocation location) {
        if (mListener != null && location != null) {
            StringBuffer sb = new StringBuffer();
            //errCode等于0代表定位成功，其他的为定位失败，具体的可以参照官网定位错误码说明
            if(location.getErrorCode() == 0){
                mListener.onLocationChanged(location);// 显示系统小蓝点
                sb.append("定位成功" + "\n");
                sb.append("经    度    : " + location.getLongitude() );
                sb.append("纬    度    : " + location.getLatitude() + "\n");
                sb.append("地    址    : " + location.getAddress() + "\n");
            } else {
                //定位失败
                sb.append("定位失败" + "\n");
                sb.append("错误码:" + location.getErrorCode() + "\n");
                sb.append("错误信息:" + location.getErrorInfo() + "\n");
                sb.append("错误描述:" + location.getLocationDetail() + "\n");
            }
            //解析定位结果，
            String result = sb.toString();
            currentLocation = "经度："+location.getLatitude()+" 纬度："+location.getLatitude()+" 位置："+location.getAddress();
            tvResult.setText(result+"\n");
            currentLocation.replace("\n","");
            Log.e("tag","result: "+result);
        }

    }

    /**
     * 激活定位
     */
    @Override
    public void activate(OnLocationChangedListener listener) {
        mListener = listener;
        if (mlocationClient == null) {
            mlocationClient = new AMapLocationClient(this);
            mLocationOption =getDefaultOption();
            // 设置定位监听
            mlocationClient.setLocationListener(this);
          /*  // 设置为高精度定位模式
            mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
            // 只是为了获取当前位置，所以设置为单次定位
            mLocationOption.setOnceLocation(false);*/
            // 设置定位参数
            mlocationClient.setLocationOption(mLocationOption);
            mlocationClient.startLocation();
            Log.e("tag","startlocation");
        }
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent home = new Intent(Intent.ACTION_MAIN);
            home.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            home.addCategory(Intent.CATEGORY_HOME);
            startActivity(home);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
    private AMapLocationClientOption getDefaultOption(){
        AMapLocationClientOption mOption = new AMapLocationClientOption();
        mOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);//可选，设置定位模式，可选的模式有高精度、仅设备、仅网络。默认为高精度模式
        mOption.setGpsFirst(true);//可选，设置是否gps优先，只在高精度模式下有效。默认关闭
        mOption.setHttpTimeOut(30000);//可选，设置网络请求超时时间。默认为30秒。在仅设备模式下无效
        mOption.setInterval(5000);//可选，设置定位间隔。默认为2秒
        mOption.setNeedAddress(true);//可选，设置是否返回逆地理地址信息。默认是true
        mOption.setOnceLocation(false);//可选，设置是否单次定位。默认是false
        mOption.setOnceLocationLatest(false);//可选，设置是否等待wifi刷新，默认为false.如果设置为true,会自动变为单次定位，持续定位时不要使用
        AMapLocationClientOption.setLocationProtocol(AMapLocationClientOption.AMapLocationProtocol.HTTP);//可选， 设置网络请求的协议。可选HTTP或者HTTPS。默认为HTTP
        mOption.setSensorEnable(false);//可选，设置是否使用传感器。默认是false
        mOption.setWifiScan(true); //可选，设置是否开启wifi扫描。默认为true，如果设置为false会同时停止主动刷新，停止以后完全依赖于系统刷新，定位位置可能存在误差
        mOption.setLocationCacheEnable(true); //可选，设置是否使用缓存定位，默认为true
        return mOption;
    }
    /**
     * 停止定位
     */
    @Override
    public void deactivate() {
        mListener = null;
        if (mlocationClient != null) {
            mlocationClient.stopLocation();
            mlocationClient.onDestroy();
        }
        mlocationClient = null;
    }

    private void addCenterMarker(LatLng latlng) {
        if (null == centerMarker) {
            centerMarker = mAMap.addMarker(markerOption);
        }
        centerMarker.setPosition(latlng);
        markerList.add(centerMarker);
    }

    private void removeMarkers() {
        if(null != centerMarker){
            centerMarker.remove();
            centerMarker = null;
        }
        if (null != markerList && markerList.size() > 0) {
            for (Marker marker : markerList) {
                marker.remove();
            }
            markerList.clear();
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.cb_alertIn :
                if (isChecked) {
                    activatesAction |= GeoFenceClient.GEOFENCE_IN;
                } else {
                    activatesAction = activatesAction
                            & (GeoFenceClient.GEOFENCE_OUT
                            | GeoFenceClient.GEOFENCE_STAYED);
                }
                break;
            case R.id.cb_alertOut :
                if (isChecked) {
                    activatesAction |= GeoFenceClient.GEOFENCE_OUT;
                } else {
                    activatesAction = activatesAction
                            & (GeoFenceClient.GEOFENCE_IN
                            | GeoFenceClient.GEOFENCE_STAYED);
                }
                break;
            case R.id.cb_alertStated :
                if (isChecked) {
                    activatesAction |= GeoFenceClient.GEOFENCE_STAYED;
                } else {
                    activatesAction = activatesAction
                            & (GeoFenceClient.GEOFENCE_IN
                            | GeoFenceClient.GEOFENCE_OUT);
                }
                break;
            default :
                break;
        }
        if (null != fenceClient) {

            fenceClient.setActivateAction(activatesAction);
        }
    }

    private void resetView_round() {
        etRadius.setHint("围栏半径");
        etRadius.setVisibility(View.VISIBLE);
        tvGuide.setBackgroundColor(getResources().getColor(R.color.red));
        tvGuide.setText("请点击地图选择围栏的中心点");
        tvGuide.setVisibility(View.VISIBLE);
    }

    /**
     * 添加围栏
     *
     * @since 3.2.0
     * @author hongming.wang
     *
     */
    private void addFence() {
        addRoundFence();
    }

    /**
     * 添加圆形围栏
     *
     * @since 3.2.0
     * @author hongming.wang
     *
     */
    private void addRoundFence() {
        fenceClient.removeGeoFence();
        String radiusStr = etRadius.getText().toString();
        if (null == centerLatLng
                || TextUtils.isEmpty(radiusStr)) {
            Toast.makeText(getApplicationContext(), "参数不全", Toast.LENGTH_SHORT)
                    .show();
            return;
        }

        DPoint centerPoint = new DPoint(centerLatLng.latitude,
                centerLatLng.longitude);
        fenceRadius = Float.parseFloat(radiusStr);
        fenceClient.addGeoFence(centerPoint, fenceRadius, "1");

    }
}

package com.mzth.tangerinepoints_merchant.ui.activity.sub;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.mzth.tangerinepoints_merchant.R;
import com.mzth.tangerinepoints_merchant.common.Constans;
import com.mzth.tangerinepoints_merchant.common.SPName;
import com.mzth.tangerinepoints_merchant.common.ToastHintMsgUtil;
import com.mzth.tangerinepoints_merchant.ui.activity.base.BaseBussActivity;
import com.mzth.tangerinepoints_merchant.util.DialogThridUtils;
import com.mzth.tangerinepoints_merchant.util.GsonUtil;
import com.mzth.tangerinepoints_merchant.util.NetUtil;
import com.mzth.tangerinepoints_merchant.util.SharedPreferencesUtil;
import com.mzth.tangerinepoints_merchant.util.StringUtil;
import com.mzth.tangerinepoints_merchant.util.ToastUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Created by Administrator on 2017/4/15.
 */

public class BusinessesActivity extends BaseBussActivity {
    private EditText et_merchant_pin;
    private TextView tv_next;
    private Dialog LoadDialog;//加载动画的Dialog
    private double latitude;//纬度
    private double longitude;//经度
    private GoogleApiClient mGoogleApiClient;
    private LocationManager locationManager;
    private String locationProvider;
    //private WifiManager wifiManager;
    //声明AMapLocationClient类对象
    public AMapLocationClient mLocationClient = null;
    //声明AMapLocationClientOption对象
    public AMapLocationClientOption mLocationOption = null;
    //声明定位回调监听器
    public AMapLocationListener mLocationListener = new AMapLocationListener() {
        @Override
        public void onLocationChanged(AMapLocation aMapLocation) {
            if (aMapLocation != null) {
                if (aMapLocation.getErrorCode() == 0) {
                    //可在其中解析amapLocation获取相应内容。
                    latitude = aMapLocation.getLatitude();
                    longitude = aMapLocation.getLongitude();
                    SharedPreferencesUtil.setParam(_context, SPName.location, latitude + "," + longitude);
                    //String location = (String) SharedPreferencesUtil.getParam(_context, SPName.location, "");

                } else {
                    //定位失败时，可通过ErrCode（错误码）信息来确定失败的原因，errInfo是错误信息，详见错误码表。
                    Log.e("AmapError", "location Error, ErrCode:"
                            + aMapLocation.getErrorCode() + ", errInfo:"
                            + aMapLocation.getErrorInfo());
                }
            }
        }
    };
    @Override
    protected void setCustomLayout(Bundle savedInstanceState) {
        super.setCustomLayout(savedInstanceState);
        _context = BusinessesActivity.this;
        setContentView(R.layout.activity_businesses);
    }

    @Override
    protected void initView() {
        super.initView();
        //输入商户PIN
        et_merchant_pin = (EditText) findViewById(R.id.et_merchant_pin);
        //点击登录
        tv_next = (TextView) findViewById(R.id.tv_next);
    }

    @Override
    protected void initData() {
        super.initData();
        //初始化定位
        mLocationClient = new AMapLocationClient(getApplicationContext());
        //设置定位回调监听
        mLocationClient.setLocationListener(mLocationListener);
        //初始化AMapLocationClientOption对象
        mLocationOption = new AMapLocationClientOption();
        //设置定位模式为AMapLocationMode.Battery_Saving，低功耗模式。
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Battery_Saving);
        //设置定位模式为AMapLocationMode.Device_Sensors，仅设备模式。
        //mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Device_Sensors);
        //获取一次定位结果：
        //该方法默认为false。
        mLocationOption.setOnceLocation(true);
        //获取最近3s内精度最高的一次定位结果：
        //设置setOnceLocationLatest(boolean b)接口为true，启动定位时SDK会返回最近3s内精度最高的一次定位结果。如果设置其为true，setOnceLocation(boolean b)接口也会被设置为true，反之不会，默认为false。
        mLocationOption.setOnceLocationLatest(true);
        //设置定位间隔,单位毫秒,默认为2000ms，最低1000ms。
        mLocationOption.setInterval(1000);
        //设置是否返回地址信息（默认返回地址信息）
        mLocationOption.setNeedAddress(true);
        //设置是否强制刷新WIFI，默认为true，强制刷新。
        mLocationOption.setWifiActiveScan(false);
        //设置是否允许模拟位置,默认为false，不允许模拟位置
        mLocationOption.setMockEnable(true);
        //单位是毫秒，默认30000毫秒，建议超时时间不要低于8000毫秒。
        mLocationOption.setHttpTimeOut(20000);
        //关闭缓存机制
        mLocationOption.setLocationCacheEnable(false);
        //给定位客户端对象设置定位参数
        mLocationClient.setLocationOption(mLocationOption);
        //启动定位
        mLocationClient.startLocation();

/**

        //GetLocation();
//        //商户的PIN  测试数据
        //et_merchant_pin.setText(Constans.MERCHANT_PIN);
        //获得经纬度
        //获取地理位置管理器
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        //获取所有可用的位置提供器
        List<String> providers = locationManager.getProviders(true);
        //String pro = providers.get(0);
        if (providers.contains(LocationManager.GPS_PROVIDER)) {
            //如果是GPS
            locationProvider = LocationManager.GPS_PROVIDER;
        } else if (providers.contains(LocationManager.NETWORK_PROVIDER)) {
            //如果是Network
            locationProvider = LocationManager.NETWORK_PROVIDER;
        } else if (providers.contains(LocationManager.PASSIVE_PROVIDER)) {
            //一个特殊地点的供应商
            locationProvider = LocationManager.PASSIVE_PROVIDER;
        } else {
            ToastUtil.showShort(_context, "There is no location provider available");
            return;
        }
        //获取Location
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Location location = locationManager.getLastKnownLocation(locationProvider);
        if (location != null) {
            latitude = location.getLatitude();//经度
            longitude = location.getLongitude();//纬度
        }
 */
        if (!StringUtil.isEmpty((String) SharedPreferencesUtil.getParam(getApplicationContext(), "accessKey", ""))) {
            //Authorization = (String) SharedPreferencesUtil.getParam(getApplicationContext(), "accessKey", "");
            startActivity(HomeActivity.class, null);
            onBackPressed();
        }

    }

    @Override
    protected void BindComponentEvent() {
        super.BindComponentEvent();
        //点击下一步
        tv_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!StringUtil.isEmpty(et_merchant_pin.getText().toString())) {
                    StoreRequest();
                } else {
                    ToastUtil.showShort(_context, "Merchant PIN cannot be empty");
                }
            }
        });
    }

    //列出商户名下的所有商店
    private void StoreRequest() {
        LoadDialog = DialogThridUtils.showWaitDialog(_context, "Getting Your Store , Loading...", false, false);
        Map<String, Object> map = new HashMap<String, Object>();
        //获取用户输入的pin
        final String pin = et_merchant_pin.getText().toString();
        map.put("merchant_pin", pin);
        //获取设备的IMEI码
        TelephonyManager mTm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        String imei = mTm.getDeviceId();
        map.put("device_imei", imei);
        //map.put("device_imei", Constans.DEVICE_IMEI);
        NetUtil.Request(NetUtil.RequestMethod.GET, Constans.SH_BUSINESSES, map, null, null, new NetUtil.RequestCallBack() {
            @Override
            public void onSuccess(int statusCode, String json) {
                String businesses = GsonUtil.getJsonFromKey(json, "businesses");
                Bundle bundle = new Bundle();
                bundle.putString("businesses", businesses);
                bundle.putString("pin", pin);
                startActivity(LoginActivity.class, bundle);
                finish();
                DialogThridUtils.closeDialog(LoadDialog);//关闭动画
            }

            @Override
            public void onFailure(int statusCode, String errorMsg) {
                ToastUtil.showShort(_context, ToastHintMsgUtil.getToastMsg(errorMsg));
                DialogThridUtils.closeDialog(LoadDialog);//关闭动画
            }

            @Override
            public void onFailure(Exception e, String errorMsg) {
                ToastUtil.showShort(_context, errorMsg);
                DialogThridUtils.closeDialog(LoadDialog);//关闭动画
            }
        });
    }
    private  LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            Log.d("Location", "onLocationChanged Latitude"+ location.getLatitude());
            Log.d("Location", "onLocationChanged location"+ location.getLongitude());
        }
    };
    private void GetLocation() {
//        _context.registerReceiver(new WifiReceiver(), new IntentFilter(
//                WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        //wifiManager.startScan();
//        Map<String,Object> map = new HashMap<String,Object>();
//        map.put("version","1.1.0");
//        map.put("host","maps.google.com");
//        map.put("address_language","zh_CN");
//        map.put("request_address",true);
//        //List<ScanResult> wifiList = getWifiList();
//        JSONArray towerarray = new JSONArray();
//        for (int i = 0; i < wifiList.size(); i++) {
//            JSONObject tower = new JSONObject();
//            try {
//            tower.put("mac_address", wifiList.get(i).BSSID);
//            tower.put("ssid", wifiList.get(i).SSID);
//            tower.put("signal_strength", wifiList.get(i).level);
//            towerarray.put(tower);
//            }catch (Exception e){
//                e.printStackTrace();
//            }
//        }
//        map.put("wifi_towers", towerarray);
//        NetUtil.Request(NetUtil.RequestMethod.POST, Constans.GET_LOCATION, map, null, null, new NetUtil.RequestCallBack() {
//            @Override
//            public void onSuccess(int statusCode, String json) {
//                ToastUtil.showShort(_context,json);
//            }
//
//            @Override
//            public void onFailure(int statusCode, String errorMsg) {
//                ToastUtil.showShort(_context,errorMsg);
//            }
//
//            @Override
//            public void onFailure(Exception e, String errorMsg) {
//                ToastUtil.showShort(_context,errorMsg);
//            }
//        });
    }

//    public List<ScanResult> getWifiList(){
//
//        //wifiManager = (WifiManager) _context.getSystemService(Context.WIFI_SERVICE);
//        //return wifiManager.getScanResults();
//    }

}

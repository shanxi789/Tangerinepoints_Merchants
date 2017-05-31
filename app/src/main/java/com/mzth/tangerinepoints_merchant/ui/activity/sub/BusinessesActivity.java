package com.mzth.tangerinepoints_merchant.ui.activity.sub;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

        //商户的PIN  测试数据
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
        } else if(providers.contains(LocationManager.PASSIVE_PROVIDER)){
            //一个特殊地点的供应商
            locationProvider = LocationManager.PASSIVE_PROVIDER;
        }else {
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
        if(location!=null){
            latitude = location.getLatitude();//经度
            longitude = location.getLongitude();//纬度
            SharedPreferencesUtil.setParam(_context, SPName.location,latitude+","+longitude);
        }
        if(!StringUtil.isEmpty((String) SharedPreferencesUtil.getParam(getApplicationContext(), "accessKey", ""))) {
            //Authorization = (String) SharedPreferencesUtil.getParam(getApplicationContext(), "accessKey", "");
            startActivity(HomeActivity.class,null);
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
                if(!StringUtil.isEmpty(et_merchant_pin.getText().toString())){
                StoreRequest();
                }else{
                    ToastUtil.showShort(_context,"Merchant PIN cannot be empty");
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
        //TelephonyManager mTm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        //String imei = mTm.getDeviceId();
        //map.put("device_imei", imei);
        map.put("device_imei", Constans.DEVICE_IMEI);
        NetUtil.Request(NetUtil.RequestMethod.GET, Constans.SH_BUSINESSES, map, null, null, new NetUtil.RequestCallBack() {
            @Override
            public void onSuccess(int statusCode, String json) {
                String businesses = GsonUtil.getJsonFromKey(json, "businesses");
                Bundle bundle = new Bundle();
                bundle.putString("businesses", businesses);
                bundle.putString("pin",pin);
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


}

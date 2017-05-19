package com.mzth.tangerinepoints_merchant.common;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.IBinder;
import android.support.annotation.Nullable;


import com.mzth.tangerinepoints_merchant.util.NetUtil;
import com.mzth.tangerinepoints_merchant.util.SharedPreferencesUtil;
import com.mzth.tangerinepoints_merchant.util.StringUtil;
import com.mzth.tangerinepoints_merchant.util.ToastUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Administrator on 2017/5/9.
 * 后台同步未完成交易
 */

public class BackUpServices extends Service {
    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Timer timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                    String data = (String) SharedPreferencesUtil.getParam(getApplicationContext(),"MessageQueue","");
                    String redata = data.replace("\n","").trim();
                if(!StringUtil.isEmpty(redata)){//如果本地有待处理的交易数据
                        UnfinishedRequest(redata); //每隔10分钟向服务发起这个请求
                    }
            }
        };
        timer.schedule(task,1000*60*10,999999999);

        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    //同步一项未完成交易
    private void UnfinishedRequest(String data){
        Map<String,Object> map = new HashMap<String,Object>();
        String location = (String) SharedPreferencesUtil.getParam(getApplicationContext(),"location","");
        map.put("location",Constans.location);//当前的地理位置
        //待处理的交易数据
        map.put("txn_data",data);//待处理的交易数据
        NetUtil.Request(NetUtil.RequestMethod.POST, Constans.SYNC_PURCHASES, map, (String) SharedPreferencesUtil.getParam(getApplicationContext(),"accessKey",""), Constans.APP_INSTANCE_ID, new NetUtil.RequestCallBack() {
            @Override
            public void onSuccess(int statusCode, String json) {
                //ToastUtil.showShort(getApplicationContext(),json);
                //清空消息队列
                SharedPreferencesUtil.setParam(getApplicationContext(),"MessageQueue","");
            }

            @Override
            public void onFailure(int statusCode, String errorMsg) {
                ToastUtil.showShort(getApplicationContext(),errorMsg);
            }

            @Override
            public void onFailure(Exception e, String errorMsg) {
                ToastUtil.showShort(getApplicationContext(),errorMsg);
            }
        });
    }
}

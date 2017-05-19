package com.mzth.tangerinepoints_merchant.common;

import android.app.Activity;
import android.app.Application;
import android.content.Context;

import com.mzth.tangerinepoints_merchant.bean.OfflineIntegrationBean;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MainApplication extends Application {

    private static ArrayList<Activity> list = new ArrayList<Activity>();

    private static Context context;

    public static List<OfflineIntegrationBean> ListBean;

    public Context getContext() {
        return context;
    }
    // 用于存放倒计时时间
    public static Map<String, Long> map;
    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        ListBean= new ArrayList<OfflineIntegrationBean>();//离线消息队列
    }
    /**
     * 添加Activity到集合中
     */
    public void addActivity(Activity activity) {
        list.add(activity);
    }

    /**
     * 从集合中移除Activity
     */
    public void removeActivity(Activity activity) {
        list.remove(activity);
    }

    /**
     * 关闭所有的Activity
     */
    public static void closeActivity() {
        for (Activity activity : list) {
            if (null != activity) {
                activity.finish();
            }
        }
    }








}

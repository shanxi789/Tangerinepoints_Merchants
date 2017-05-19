package com.mzth.tangerinepoints_merchant.ui.activity.base;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.mzth.tangerinepoints_merchant.bean.OfflineIntegrationBean;
import com.mzth.tangerinepoints_merchant.common.MainApplication;
import com.mzth.tangerinepoints_merchant.util.SharedPreferencesUtil;
import com.mzth.tangerinepoints_merchant.util.StringUtil;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by leeandy007 on 2017/3/11.
 */

public abstract class BaseActivity extends AppCompatActivity {

    protected Activity _context;
    protected MainApplication application;
    protected String Authorization;
    protected int index;//起始item
    protected int n;//返回的条数
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        application = (MainApplication) this.getApplication();
        application.addActivity(this);
        if(!StringUtil.isEmpty((String) SharedPreferencesUtil.getParam(getApplicationContext(), "accessKey", ""))) {
            Authorization = (String) SharedPreferencesUtil.getParam(getApplicationContext(), "accessKey", "");
        }
        index = 0;
        n = 5;
        //透明状态栏
        //getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        //透明导航栏
        //getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        setCustomLayout(savedInstanceState);
        initView();
        initData();
        BindComponentEvent();

    }

    /**
     * 初始化布局
     * */
    protected abstract void setCustomLayout(Bundle savedInstanceState);

    /**
     * 初始化控件
     * */
    protected abstract void initView();

    /**
     * 绑定控件事件
     * */
    protected abstract void BindComponentEvent();

    /**
     * 初始化数据
     * */
    protected abstract void initData();

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (resultCode == Activity.RESULT_OK) {
            doActivityResult(requestCode, intent);
        }
    }

    /**
     * 带返回值跳转的数据的处理方法
     * */
    protected abstract void doActivityResult(int requestCode, Intent intent);

}

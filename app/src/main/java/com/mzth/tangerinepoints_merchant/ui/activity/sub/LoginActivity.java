package com.mzth.tangerinepoints_merchant.ui.activity.sub;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v4.widget.NestedScrollView;
import android.telephony.TelephonyManager;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.mzth.tangerinepoints_merchant.R;
import com.mzth.tangerinepoints_merchant.bean.BusinessesBean;
import com.mzth.tangerinepoints_merchant.common.Constans;
import com.mzth.tangerinepoints_merchant.common.MainApplication;
import com.mzth.tangerinepoints_merchant.common.SPName;
import com.mzth.tangerinepoints_merchant.common.ToastHintMsgUtil;
import com.mzth.tangerinepoints_merchant.ui.activity.base.BaseBussActivity;
import com.mzth.tangerinepoints_merchant.ui.adapter.sub.StoreListAdapter;
import com.mzth.tangerinepoints_merchant.util.DialogThridUtils;
import com.mzth.tangerinepoints_merchant.util.GsonUtil;
import com.mzth.tangerinepoints_merchant.util.NetUtil;
import com.mzth.tangerinepoints_merchant.util.SharedPreferencesUtil;
import com.mzth.tangerinepoints_merchant.util.StringUtil;
import com.mzth.tangerinepoints_merchant.util.ToastUtil;
import com.mzth.tangerinepoints_merchant.widget.MyListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2017/4/15.
 */

public class LoginActivity extends BaseBussActivity {
    private NestedScrollView scroll;
    private MyListView lv_business;
    private TextView tv_login,tv_ok,tv_marked;
    private LinearLayout ll_login_no;
    private BusinessesBean busbean;
    private Dialog LoadDialog;//加载动画的Dialog
    private RadioGroup radio_group;//容纳单选按钮的容器
    private RadioButton radioButton;//单选按钮
    private List<BusinessesBean> beans;//商店列表
    private String BusinessId;//商店id
    private String pin;//商户pin
    private HashMap<String,String> map;//用于根据商店名字找到对应的商店ID
    @Override
    protected void setCustomLayout(Bundle savedInstanceState) {
        super.setCustomLayout(savedInstanceState);
        _context=LoginActivity.this;
        setContentView(R.layout.activity_login);
    }

    @Override
    protected void initView() {
        super.initView();
        scroll = (NestedScrollView) findViewById(R.id.scroll);
        //显示商店
        //lv_business= (MyListView) findViewById(R.id.lv_business);
        //点击登录
        tv_login= (TextView) findViewById(R.id.tv_login);
        //没有发现商店的布局
        ll_login_no= (LinearLayout) findViewById(R.id.ll_login_no);
        //没有发现商店的布局的ok控件
        tv_ok= (TextView) findViewById(R.id.tv_ok);
        //没有发现商店的布局的提示语句控件
        tv_marked= (TextView) findViewById(R.id.tv_marked);
        //添加单选按钮的容器
        radio_group= (RadioGroup) findViewById(R.id.radio_group);
        scroll.setFocusable(true);
        //lv_business.setFocusable(false);
    }
    @Override
    protected void initData() {
        super.initData();
        //让 merchant portal website.变成蓝色并添加在tv_marked控件中
        String marked=tv_marked.getText()+" merchant portal website.";
        SpannableStringBuilder style=new SpannableStringBuilder(marked);
        style.setSpan(new ForegroundColorSpan(Color.BLUE),39,marked.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        tv_marked.setText(style);

        Intent intent=getIntent();
        String businesses=intent.getStringExtra("businesses");
        pin = intent.getStringExtra("pin");
        //判断该商户旗下是否有商店
        if(StringUtil.isEmpty(businesses)){
            //如果为空就显示 没有发现商店的布局
            scroll.setVisibility(View.GONE);
            ll_login_no.setVisibility(View.VISIBLE);
        }else{
        beans= GsonUtil.getListFromJson(businesses,new TypeToken<List<BusinessesBean>>(){});
            for (int j=0; j <beans.size();j++){
                busbean = beans.get(j);
            //给RedioGroup容器添加RedioButton
            map =new HashMap<String,String>();//把商店的名字和ID保存在MAP中
            map.put(busbean.getBusinessName(),busbean.getBusinessId());
            radioButton = new RadioButton(_context);
            radioButton.setText(busbean.getBusinessName());
            radioButton.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                radio_group.addView(radioButton);
        }
            //lv_business.setAdapter(new StoreListAdapter(_context,list,R.layout.store_list_item));
    }
    }
    @Override
    protected void BindComponentEvent() {
        super.BindComponentEvent();
        tv_login.setOnClickListener(myclick);
        tv_ok.setOnClickListener(myclick);
        //这个事件监听到选中哪个radioButton的值,并根据值拿到这个商店的ID发送请求
        radioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BusinessId = map.get(radioButton.getText().toString());
                radioButton.setTextColor(getResources().getColor(R.color.orange_red));
                //ToastUtil.showShort(_context,BusinessId);
            }
        });
//        lv_business.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                busbean = (BusinessesBean) adapterView.getItemAtPosition(i);
//            }
//        });
    }
    private View.OnClickListener myclick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.tv_login://点击登录
                    if(StringUtil.isEmpty(BusinessId)){
                        //请选择您的店铺
                        ToastUtil.showShort(_context,"Please choose your shop");
                       return;
                    }
                    LoginRequest();
                    break;
                case R.id.tv_ok://没有发现商店的布局的ok控件
                    finish();
                    break;
            }
        }
    };

    private void LoginRequest(){
        LoadDialog = DialogThridUtils.showWaitDialog(_context,"Loging...",false,false);
        Map<String,Object> map = new HashMap<String,Object>();
        map.put("merchant_pin",pin);//商户身份识别码
        map.put("business_id",BusinessId);//要登录的商店UUID
        map.put("app_instance_id", MainApplication.APP_INSTANCE_ID);//在APP安装时生成并保存的一个随机的UUID，用于唯一标识一个APP实例
        //map.put("device_imei", Constans.DEVICE_IMEI);//设备的IMEI码
        //获取设备的IMEI码
        TelephonyManager mTm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        String imel = mTm.getDeviceId();
        map.put("device_imei", imel);
        //得到当前位置的坐标
        String location = (String) SharedPreferencesUtil.getParam(_context, SPName.location,"");
        //map.put("location",Constans.location);//表示位置信息的字符串
        map.put("location",location);//表示位置信息的字符串
        NetUtil.Request(NetUtil.RequestMethod.POST, Constans.SH_LOGIN, map,null,null, new NetUtil.RequestCallBack() {
            @Override
            public void onSuccess(int statusCode, String json) {
                ToastUtil.showShort(_context,"Login Success");
                String accessKey = GsonUtil.getJsonFromKey(json,"accessKey");
                //用SharedPreferences将accessKey保存在本地
                SharedPreferencesUtil.setParam(_context,"accessKey",accessKey);
//                String accessValue= (String) SharedPreferencesUtil.getParam(_context,"accessKey","");
//                ToastUtil.showShort(_context,accessValue);
                startActivity(HomeActivity.class,null);
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
                ToastUtil.showShort(_context,errorMsg);
                DialogThridUtils.closeDialog(LoadDialog);//关闭动画
            }
        });
    }
}

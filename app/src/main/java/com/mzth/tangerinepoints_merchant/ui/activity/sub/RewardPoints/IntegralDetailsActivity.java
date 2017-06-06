package com.mzth.tangerinepoints_merchant.ui.activity.sub.RewardPoints;

import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.mzth.tangerinepoints_merchant.R;
import com.mzth.tangerinepoints_merchant.bean.OfflineIntegrationBean;
import com.mzth.tangerinepoints_merchant.common.Constans;
import com.mzth.tangerinepoints_merchant.common.MainApplication;
import com.mzth.tangerinepoints_merchant.common.SPName;
import com.mzth.tangerinepoints_merchant.common.ToastHintMsgUtil;
import com.mzth.tangerinepoints_merchant.ui.activity.base.BaseBussActivity;
import com.mzth.tangerinepoints_merchant.ui.activity.sub.Redeem.ESCUtil;
import com.mzth.tangerinepoints_merchant.util.CacheUtil;
import com.mzth.tangerinepoints_merchant.util.NetUtil;
import com.mzth.tangerinepoints_merchant.util.SharedPreferencesUtil;
import com.mzth.tangerinepoints_merchant.util.StringUtil;
import com.mzth.tangerinepoints_merchant.util.ToastUtil;
import com.mzth.tangerinepoints_merchant.util.WeiboDialogUtils;
import com.mzth.tangerinepoints_merchant.widget.BluetoothUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2017/4/17.
 * 积分转发详情页面
 */

public class IntegralDetailsActivity extends BaseBussActivity {
    private ImageView btn_confirm,btn_cancel_success;
    private TextView tv_integral,tv_rp_money;
    private String money,str;
    private int points;
    private Dialog dialog;//显示加载动画的对话框
    private MediaPlayer mp;//mediaPlayer对象
    @Override
    protected void setCustomLayout(Bundle savedInstanceState) {
        super.setCustomLayout(savedInstanceState);
        _context=IntegralDetailsActivity.this;
        setContentView(R.layout.activity_rewardroints_success);
    }

    @Override
    protected void initView() {
        super.initView();
        //成功后确认
        btn_confirm= (ImageView) findViewById(R.id.btn_confirm);
        //成功后取消
        btn_cancel_success= (ImageView) findViewById(R.id.btn_cancel_success);
        //成功后显示的money
        tv_rp_money = (TextView) findViewById(R.id.tv_rp_money);
        //成功后显示的积分
        tv_integral= (TextView) findViewById(R.id.tv_integral);

    }

    @Override
    protected void initData() {
        super.initData();
        Intent intent=getIntent();
        money=intent.getStringExtra("money");
        points=intent.getIntExtra("points",0);
        //设置金额
        tv_rp_money.setText("Purchase:$"+money);
        //设置积分
        tv_integral.setText(points+"");
    }
    @Override
    protected void BindComponentEvent() {
        super.BindComponentEvent();
        btn_confirm.setOnClickListener(myclick);
        btn_cancel_success.setOnClickListener(myclick);
    }

    private View.OnClickListener myclick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.btn_confirm://成功后的确认进入扫码页面
//                    Bundle bundle = new Bundle();
//                    bundle.putDouble("money",Double.valueOf(money));
//                    bundle.putInt("points",points);
//                    startActivity(RPScanCodeActivity.class,bundle);
//                    finish();
                    Intent intent = new Intent("com.summi.scan");
                    intent.setPackage("com.sunmi.sunmiqrcodescanner");
                    //扫码模块有一些功能选项，开发者可以通过传递参数控制这些参数，
                    //所有参数都有一个默认值，开发者只要在需要的时候添加这些配置就可以。
                    intent.putExtra("CURRENT_PPI", 0X0003);//当前分辨率
                    //M1和V1的最佳是800*480,PPI_1920_1080 = 0X0001;PPI_1280_720 =
                    //0X0002;PPI_BEST = 0X0003;
                    intent.putExtra("PLAY_SOUND", true);// 扫描完成声音提示  默认true
                    intent.putExtra("PLAY_VIBRATE", false);
                    //扫描完成震动,默认false，目前M1硬件支持震动可用该配置，V1不支持
                    intent.putExtra("IDENTIFY_INVERSE_QR_CODE", true);// 识别反色二维码，默认true
                    intent.putExtra("IDENTIFY_MORE_CODE", false);// 识别画面中多个二维码，默认false
                    intent.putExtra("IS_SHOW_SETTING", true);// 是否显示右上角设置按钮，默认true
                    intent.putExtra("IS_SHOW_ALBUM", false);// 是否显示从相册选择图片按钮，默认true
                    startActivityForResult(intent, 100);
                    break;
                case R.id.btn_cancel_success://成功后的取消
                    finish();
                    //OfflineIntegration(Constans.customerId);
                    break;
            }
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if(intent!=null){
            switch (requestCode){
                case 100://兑换扫码回调
                    Bundle bundle = intent.getExtras();
                    ArrayList<HashMap<String, String>> result = (ArrayList<HashMap<String, String>>)
                            bundle.getSerializable("data");
                    Iterator<HashMap<String, String>> it = result.iterator();
                    while (it.hasNext()) {
                        HashMap<String, String> hashMap = it.next();
//                Log.i("sunmi", hashMap.get("TYPE"));//这个是扫码的类型
//                Log.i("sunmi", hashMap.get("VALUE"));//这个是扫码的结果
                        str = hashMap.get("VALUE");
                    }
                    //扫码成功后跳转页面
                    if(str.indexOf("customer")!=-1){
                        //得到用户的ID
                        String customerId = str.substring(str.indexOf(",")+1,str.length());
                        ReleaseRointsRequest(customerId);//确认发放积分
                    }else{
                        ToastUtil.showLong(_context,"Please scan the QR code users");
                    }
                    break;
            }
        }

        super.onActivityResult(requestCode, resultCode, intent);
    }
    //确认发放积分请求
    private void ReleaseRointsRequest(final String customerId){
        dialog = WeiboDialogUtils.createLoadingDialog(_context,"Loading...");
        Map<String,Object> map = new HashMap<String,Object>();
        //客户的唯一ID，是从客户的二维码中解析出来的
        map.put("customer_id", customerId);
        //消费金额。2位小数点
        map.put("purchase_amount",money);
        //要发放的点数。由梯度计算公式得出。
        map.put("points",points);
        //得到的当前位置
        String location = (String) SharedPreferencesUtil.getParam(_context, SPName.location,"");
        //map.put("location", Constans.location);
        map.put("location", location);
        NetUtil.Request(NetUtil.RequestMethod.POST, Constans.SH_REWARD_POINTS, map,Authorization,MainApplication.APP_INSTANCE_ID, new NetUtil.RequestCallBack() {
            @Override
            public void onSuccess(int statusCode, String json) {
                ToastUtil.showShort(_context,"Integral Success");

                Bundle bundle = new Bundle();
                bundle.putInt("points",points);
                startActivity(RPScanCodeSuccessActivity.class,bundle);
                finish();
                WeiboDialogUtils.closeDialog(dialog);//关闭动画
            }

            @Override
            public void onFailure(int statusCode, String errorMsg) {
                ToastUtil.showShort(_context, ToastHintMsgUtil.getToastMsg(errorMsg));
                WeiboDialogUtils.closeDialog(dialog);//关闭动画
            }

            @Override
            public void onFailure(Exception e, String errorMsg) {
                OfflineIntegration(customerId);
               // ToastUtil.showShort(_context,errorMsg);
                WeiboDialogUtils.closeDialog(dialog);//关闭动画
            }
        });
    }
    //离线发放积分
    private void OfflineIntegration(String customerId){
        OfflineIntegrationBean bean = new OfflineIntegrationBean();
        bean.setId(MainApplication.APP_INSTANCE_ID);//UUID
        bean.setTxn_type("PURCHASE");//类型
        bean.setCustomer_id(customerId);//用户ID
        bean.setPoints(points);//点数
        bean.setPurchase_amount(Double.valueOf(money));//金额
        bean.setTxn_time(new Date().getTime());//时间
        MainApplication.ListBean.add(bean);

        //将这个对象转换成json字符串
        String json = new Gson().toJson(MainApplication.ListBean);
        //String json = new Gson().toJson(bean);
        //将这个json字符串进行base64编码
        String encode = Base64.encodeToString(json.getBytes(),Base64.DEFAULT);
        //将这个消息队列以文件的形式保存在本地sp
//        if(!StringUtil.isEmpty((String)SharedPreferencesUtil.getParam(_context,"MessageQueue",""))){
//            //如果sp有数据的话把数据取出来与 刚刚得到的数据进行相加然后再次保存到SP中
//            String message = (String)SharedPreferencesUtil.getParam(_context,"MessageQueue","");
//            SharedPreferencesUtil.setParam(_context,"MessageQueue",encode+message);
//            String messages = (String)SharedPreferencesUtil.getParam(_context,"MessageQueue","");
//            ToastUtil.showShort(_context,messages);
//        }else{
            SharedPreferencesUtil.setParam(_context,"MessageQueue",encode);
        //}
        ToastUtil.showLong(_context,"This transaction is queued and it will be completed when the device is online.");
    }


}

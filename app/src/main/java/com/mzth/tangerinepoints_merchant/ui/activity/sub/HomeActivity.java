package com.mzth.tangerinepoints_merchant.ui.activity.sub;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.widget.ImageView;

import com.mzth.tangerinepoints_merchant.R;
import com.mzth.tangerinepoints_merchant.bean.CouponBean;
import com.mzth.tangerinepoints_merchant.bean.InvoiceBean;
import com.mzth.tangerinepoints_merchant.bean.offer.OfferBean;
import com.mzth.tangerinepoints_merchant.common.BackUpServices;
import com.mzth.tangerinepoints_merchant.common.Constans;
import com.mzth.tangerinepoints_merchant.common.MainApplication;
import com.mzth.tangerinepoints_merchant.common.ToastHintMsgUtil;
import com.mzth.tangerinepoints_merchant.ui.activity.base.BaseBussActivity;
import com.mzth.tangerinepoints_merchant.ui.activity.sub.History.HistoryActivity;
import com.mzth.tangerinepoints_merchant.ui.activity.sub.Redeem.RedeemActivity;
import com.mzth.tangerinepoints_merchant.ui.activity.sub.Redeem.RedeemCouponSureActivity;
import com.mzth.tangerinepoints_merchant.ui.activity.sub.Redeem.RedeemForwardActivity;
import com.mzth.tangerinepoints_merchant.ui.activity.sub.Redeem.RedeemOfferActivity;
import com.mzth.tangerinepoints_merchant.ui.activity.sub.RewardPoints.RewardPointsActivity;
import com.mzth.tangerinepoints_merchant.util.GsonUtil;
import com.mzth.tangerinepoints_merchant.util.NetUtil;
import com.mzth.tangerinepoints_merchant.util.SharedPreferencesUtil;
import com.mzth.tangerinepoints_merchant.util.ToastUtil;
import com.mzth.tangerinepoints_merchant.util.WeiboDialogUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2017/4/15.
 * 首页
 */

public class HomeActivity extends BaseBussActivity {
    private ImageView tv_rewardpoints, tv_redeem, tv_history;
    private long mExitTime;
    private Dialog dialog;//加载动画的对话框
    private String str;//二维码扫码结果

    @Override
    protected void setCustomLayout(Bundle savedInstanceState) {
        super.setCustomLayout(savedInstanceState);
        _context = HomeActivity.this;
        setContentView(R.layout.activity_home);

    }

    @Override
    protected void initView() {
        super.initView();
        //积分奖励
        tv_rewardpoints= (ImageView) findViewById(R.id.tv_rewardpoints);
        //兑换
        tv_redeem= (ImageView) findViewById(R.id.tv_redeem);
        //历史纪录
        tv_history= (ImageView) findViewById(R.id.tv_history);
    }
    @Override
    protected void initData() {
        super.initData();
        //启动后台服务  完成未完成的交易
        Intent intent = new Intent(_context, BackUpServices.class);
        startService(intent);
    }
    @Override
    protected void BindComponentEvent() {
        super.BindComponentEvent();
        tv_rewardpoints.setOnClickListener(myonclick);
        tv_redeem.setOnClickListener(myonclick);
        tv_history.setOnClickListener(myonclick);
    }
    private View.OnClickListener myonclick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.tv_rewardpoints://积分奖励
                    startActivity(RewardPointsActivity.class,null);
                    break;
                case R.id.tv_redeem://兑换 调用商品设备自带的扫码功能
                    //startActivity(RedeemActivity.class,null);
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
                    intent.putExtra("IS_SHOW_ALBUM", true);// 是否显示从相册选择图片按钮，默认true
                    startActivityForResult(intent, 100);
                    break;
                case R.id.tv_history://历史纪录
                    startActivity(HistoryActivity.class,null);
                    break;
            }
        }
    };

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
                    //优惠券二维码信息
                    if(str.indexOf("coupon")!=-1){
                        //优惠券ID
                        String couponId = str.substring(str.indexOf(",")+1,str.lastIndexOf(","));
                        //用户ID
                        String customerId = str.substring(str.lastIndexOf(",")+1,str.length());
                        CouponRequest(couponId,customerId);//获取coupon信息请求
                    }else if(str.indexOf("offer")!=-1){
                        //兑换ID
                        String offerId = str.substring(str.indexOf(",")+1,str.lastIndexOf(","));
                        //用户ID
                        String customerId = str.substring(str.lastIndexOf(",")+1,str.length());
                        GetOfferRequest(offerId,customerId);//获取offer信息请求
                    }else{
                        ToastUtil.showLong(_context,"Please scan the QR code for coupons and prizes");
                    }
                    break;
            }
        }
        super.onActivityResult(requestCode, resultCode, intent);

    }
    //获取coupon信息
    private void CouponRequest(String couponId,String customerId){
        dialog = WeiboDialogUtils.createLoadingDialog(_context,"Loading...");
        //从coupon的二维码中解析出来的
        NetUtil.Request(NetUtil.RequestMethod.GET, Constans.SH_COUPON+couponId,null,Authorization,Constans.APP_INSTANCE_ID,new NetUtil.RequestCallBack(){

            @Override
            public void onSuccess(int statusCode, String json) {
                String coupon = GsonUtil.getJsonFromKey(json,"coupon");
                CouponBean bean =  GsonUtil.getBeanFromJson(coupon,CouponBean.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("bean",bean);
                startActivity(RedeemForwardActivity.class,bundle);
                WeiboDialogUtils.closeDialog(dialog);//关闭动画
            }

            @Override
            public void onFailure(int statusCode, String errorMsg) {
                ToastUtil.showShort(_context, ToastHintMsgUtil.getToastMsg(errorMsg));
                WeiboDialogUtils.closeDialog(dialog);//关闭动画
            }

            @Override
            public void onFailure(Exception e, String errorMsg) {
                ToastUtil.showShort(_context,errorMsg);
                WeiboDialogUtils.closeDialog(dialog);//关闭动画
            }
        });
    }
    //获取offer信息
    private void GetOfferRequest(String offerId, final String customerId){
        dialog = WeiboDialogUtils.createLoadingDialog(_context,"Loading...");
        //从offer的二维码中解析出来的
        NetUtil.Request(NetUtil.RequestMethod.GET,Constans.SH_OFFER+offerId,null,Authorization,Constans.APP_INSTANCE_ID,new NetUtil.RequestCallBack(){
            @Override
            public void onSuccess(int statusCode, String json) {
                String offer = GsonUtil.getJsonFromKey(json,"offer");
                OfferBean offerBean = GsonUtil.getBeanFromJson(offer,OfferBean.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("bean",offerBean);
                bundle.putString("customerId",customerId);
                startActivity(RedeemOfferActivity.class,bundle);
                WeiboDialogUtils.closeDialog(dialog);//关闭动画
            }

            @Override
            public void onFailure(int statusCode, String errorMsg) {
                ToastUtil.showShort(_context, ToastHintMsgUtil.getToastMsg(errorMsg));
                WeiboDialogUtils.closeDialog(dialog);//关闭动画
            }

            @Override
            public void onFailure(Exception e, String errorMsg) {
                ToastUtil.showShort(_context,errorMsg);
                WeiboDialogUtils.closeDialog(dialog);//关闭动画
            }
        });
    }
    @Override
    public void onBackPressed() {
        if ((System.currentTimeMillis() - mExitTime) > 2000) {
            ToastUtil.showShort(this, "Press logout again");
            mExitTime = System.currentTimeMillis();
        } else {
            MainApplication.closeActivity();
        }
    }
}

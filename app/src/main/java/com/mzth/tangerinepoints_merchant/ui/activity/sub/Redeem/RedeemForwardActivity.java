package com.mzth.tangerinepoints_merchant.ui.activity.sub.Redeem;

import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.mzth.tangerinepoints_merchant.R;
import com.mzth.tangerinepoints_merchant.bean.CouponBean;
import com.mzth.tangerinepoints_merchant.bean.InvoiceBean;
import com.mzth.tangerinepoints_merchant.common.Constans;
import com.mzth.tangerinepoints_merchant.common.SPName;
import com.mzth.tangerinepoints_merchant.common.ToastHintMsgUtil;
import com.mzth.tangerinepoints_merchant.ui.activity.base.BaseBussActivity;
import com.mzth.tangerinepoints_merchant.util.GsonUtil;
import com.mzth.tangerinepoints_merchant.util.NetUtil;
import com.mzth.tangerinepoints_merchant.util.SharedPreferencesUtil;
import com.mzth.tangerinepoints_merchant.util.ToastUtil;
import com.mzth.tangerinepoints_merchant.util.WeiboDialogUtils;
import com.mzth.tangerinepoints_merchant.widget.BluetoothUtil;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2017/4/18.
 * 确认兑换优惠券页面
 */

public class RedeemForwardActivity extends BaseBussActivity {
    private ImageView btn_redeem_confirm,btn_redeem_cancel;
    private CouponBean bean;//优惠券的对象
    private TextView tv_coupons_title,tv_coupons_points;
    private Dialog dialog;//加载时的动画
    private CheckBox cb_redeem;//打印兑换得优惠券
    @Override
    protected void setCustomLayout(Bundle savedInstanceState) {
        super.setCustomLayout(savedInstanceState);
        _context=RedeemForwardActivity.this;
        setContentView(R.layout.activity_redeem_coupon);
   }

    @Override
    protected void initView() {
        super.initView();
        //确认按钮
        btn_redeem_confirm = (ImageView) findViewById(R.id.btn_redeem_confirm);
        //取消按钮
        btn_redeem_cancel  = (ImageView) findViewById(R.id.btn_redeem_cancel);
        //兑换标题
        tv_coupons_title = (TextView) findViewById(R.id.tv_coupons_title);
        //兑换的点数
        tv_coupons_points = (TextView) findViewById(R.id.tv_coupons_points);
        //打印兑换得优惠券
        cb_redeem = (CheckBox) findViewById(R.id.cb_redeem);
    }

    @Override
    protected void initData() {
        super.initData();
        bean = (CouponBean) getIntent().getSerializableExtra("bean");
        //初始化兑换信息 优惠券的标题 和 内容
        tv_coupons_title.setText(bean.getCouponTermTitle());
        //tv_coupons_title.setTextSize(20);
        tv_coupons_points.setText(bean.getCouponTermDetail());
        //tv_coupons_points.setTextSize(18);
    }
    @Override
    protected void BindComponentEvent() {
        super.BindComponentEvent();
        btn_redeem_confirm.setOnClickListener(myclick);
        btn_redeem_cancel.setOnClickListener(myclick);
    }

    private View.OnClickListener myclick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.btn_redeem_confirm://确认按钮
                    SureCouponRequest();//确认接受coupon
                    break;
                case R.id.btn_redeem_cancel://取消按钮
                    AlertDialog.Builder builder=new AlertDialog.Builder(_context);
                    builder.setMessage("Are you sure you want to cancel the deal?");
                    builder.setTitle("Prompt");
                    //确认按钮
                    builder.setPositiveButton("confirm", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            //CancleRequest();//取消一个当天的交易
                            dialogInterface.dismiss();
                            finish();
                        }
                    });
                    //取消按钮
                    builder.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    });
                    builder.show();
                    break;
            }
        }
    };

    //确认接受coupon
    private void SureCouponRequest(){
        dialog = WeiboDialogUtils.createLoadingDialog(_context,"Loading...");
        Map<String,Object> map = new HashMap<String,Object>();
        //从coupon的二维码中解析出来的 测试数据
        map.put("customer_id",bean.getCustomerId());
        map.put("coupon_id", bean.getCouponId());
        //map.put("customer_id",Constans.couponId);
        //map.put("coupon_id", Constans.customerId);
        //当前位置
        String location = (String) SharedPreferencesUtil.getParam(_context, SPName.location,"");
        //map.put("location",Constans.location);
        map.put("location",location);
        NetUtil.Request(NetUtil.RequestMethod.POST, Constans.SH_REDEEM_COUPON,map,Authorization,Constans.APP_INSTANCE_ID,new NetUtil.RequestCallBack(){

            @Override
            public void onSuccess(int statusCode, String json) {
                ToastUtil.showShort(_context,"Exchange Success");
                if(cb_redeem.isChecked()){
                    String confirmation = GsonUtil.getJsonFromKey(json,"confirmation");
                    //发票上的数据
                    InvoiceBean invoicebean = GsonUtil.getBeanFromJson(confirmation,InvoiceBean.class);
                    PrintInfo(invoicebean);
                }
                startActivity(RedeemCouponSureActivity.class,null);
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
                ToastUtil.showShort(_context,errorMsg);
                WeiboDialogUtils.closeDialog(dialog);//关闭动画
            }
        });
    }

    //打印数据
    private void PrintInfo(InvoiceBean invoicebean){
        // 1: Get BluetoothAdapter
        BluetoothAdapter btAdapter = BluetoothUtil.getBTAdapter();
        if (btAdapter == null) {
            ToastUtil.showLong(_context, "Please Open Bluetooth!");
            return;
        }
        // 2: Get Sunmi's InnerPrinter BluetoothDevice
        BluetoothDevice device = BluetoothUtil.getDevice(btAdapter);
        if (device == null) {
            ToastUtil.showLong(_context, "Please Make Sure Bluetooth have InnterPrinter!");
            return;
        }
        //生成订单数据
        byte[] data = ESCUtil.generateMockData(invoicebean);
        // 4: Using InnerPrinter print data
        BluetoothSocket socket = null;
        try {
            socket = BluetoothUtil.getSocket(device);
            BluetoothUtil.sendData(data, socket);
        } catch (IOException e) {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }


}

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
import com.mzth.tangerinepoints_merchant.bean.InvoiceBean;
import com.mzth.tangerinepoints_merchant.bean.offer.OfferBean;
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
 * Created by Administrator on 2017/5/5.
 * 确认兑换商品页面
 */

public class RedeemOfferActivity extends BaseBussActivity {
    private ImageView btn_redeem_confirm,btn_redeem_cancel;
    private OfferBean bean;//商品的对象
    private TextView tv_coupons_title,tv_coupons_points;
    private String customerId;//用户ID
    private Dialog dialog;//加载时的动画
    private CheckBox cb_redeem;//打印兑换得优惠券
    @Override
    protected void setCustomLayout(Bundle savedInstanceState) {
        super.setCustomLayout(savedInstanceState);
        _context=RedeemOfferActivity.this;
        setContentView(R.layout.activity_redeem_forward);
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
        bean = (OfferBean) getIntent().getSerializableExtra("bean");
        customerId = getIntent().getStringExtra("customerId");
        //初始化兑换信息 标题和点数
        tv_coupons_title.setText(bean.getItemName());
        tv_coupons_points.setText(bean.getPoints()+"");
    }
    @Override
    protected void BindComponentEvent() {
        super.BindComponentEvent();
        btn_redeem_confirm.setOnClickListener(myonclick);
        btn_redeem_cancel.setOnClickListener(myonclick);
    }

    private View.OnClickListener myonclick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.btn_redeem_confirm://确认按钮
                    OfferSureRequest();
                    break;
                case R.id.btn_redeem_cancel://取消按钮
                    AlertDialog.Builder builder=new AlertDialog.Builder(_context);
                    builder.setMessage("Are you sure you want to cancel the deal?");
                    builder.setTitle("Prompt");
                    //确认按钮
                    builder.setPositiveButton("confirm", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            //CancleRequest();
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
    //确认兑换Offer
    private void OfferSureRequest(){
        dialog = WeiboDialogUtils.createLoadingDialog(_context,"Loading...");
        Map<String,Object> map = new HashMap<String,Object>();
        map.put("customer_id",customerId);//用户ID
        map.put("offer_id",bean.getOfferId());//商品ID
        String location = (String) SharedPreferencesUtil.getParam(_context, SPName.location,"");
        //map.put("location", Constans.location);//位置
        map.put("location", location);//位置
        NetUtil.Request(NetUtil.RequestMethod.POST, Constans.SH_REDEEM_OFFER, map, Authorization, Constans.APP_INSTANCE_ID, new NetUtil.RequestCallBack() {
            @Override
            public void onSuccess(int statusCode, String json) {
                ToastUtil.showShort(_context,"Exchange Success");
                if(cb_redeem.isChecked()){
                    //ToastUtil.showShort(_context,"选中了");
                    String confirmation = GsonUtil.getJsonFromKey(json,"confirmation");
                    //发票上的数据
                    InvoiceBean invoicebean = GsonUtil.getBeanFromJson(confirmation,InvoiceBean.class);
                    PrintInfo(invoicebean);
                }
                Bundle bundle = new Bundle();
                bundle.putSerializable("bean",bean);
                startActivity(RedeemSuccessActivity.class,bundle);
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

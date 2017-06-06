package com.mzth.tangerinepoints_merchant.ui.activity.sub.RewardPoints;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;

import com.mzth.tangerinepoints_merchant.R;
import com.mzth.tangerinepoints_merchant.bean.SpendingTiersBean;
import com.mzth.tangerinepoints_merchant.common.Constans;
import com.mzth.tangerinepoints_merchant.common.MainApplication;
import com.mzth.tangerinepoints_merchant.common.ToastHintMsgUtil;
import com.mzth.tangerinepoints_merchant.ui.activity.base.BaseBussActivity;
import com.mzth.tangerinepoints_merchant.util.CacheUtil;
import com.mzth.tangerinepoints_merchant.util.GsonUtil;
import com.mzth.tangerinepoints_merchant.util.NetUtil;
import com.mzth.tangerinepoints_merchant.util.StringUtil;
import com.mzth.tangerinepoints_merchant.util.ToastUtil;
import com.mzth.tangerinepoints_merchant.util.WeiboDialogUtils;

import java.text.DecimalFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import static java.lang.StrictMath.min;

/**
 * Created by Administrator on 2017/4/17.
 * 转积分页面
 */

public class RewardPointsActivity extends BaseBussActivity {
    private ImageView btn_next,btn_cancel;
    private EditText et_integral;
    //获取当前系统时间
    private long CurrentTime;
    //用户输入的金额
    private double S;
    private Dialog dialog;//加载时的动画弹框
    @Override
    protected void setCustomLayout(Bundle savedInstanceState) {
        super.setCustomLayout(savedInstanceState);
        _context=RewardPointsActivity.this;
        //积分奖励
        setContentView(R.layout.activity_home_rewardroints);
    }

    @Override
    protected void initView() {
        super.initView();
        //点击下一步
        btn_next= (ImageView) findViewById(R.id.btn_next);
        //取消
        btn_cancel= (ImageView) findViewById(R.id.btn_cancel);
        //输入的积分
        et_integral= (EditText) findViewById(R.id.et_integral);
        //显示软键盘
        et_integral.setFocusable(true);
        et_integral.setFocusableInTouchMode(true);
        et_integral.requestFocus();
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            public void run() {
                InputMethodManager inputManager =
                (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.showSoftInput(et_integral, 0);
            }
                       },
                200);
    }
    @Override
    protected void initData() {
        super.initData();
        //TiersRequest();

    }
    @Override
    protected void BindComponentEvent() {
        super.BindComponentEvent();
        btn_next.setOnClickListener(myclick);
        btn_cancel.setOnClickListener(myclick);

        //输入金额监听
        et_integral.addTextChangedListener(new TextWatcher() {
            private boolean isChanged = false;
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(isChanged){
                    return;
                }
                String str = editable.toString();
                isChanged = true;
                String cuttedStr = str;
                /* 删除字符串中的dot */
                for (int i = str.length() - 1; i >= 0; i--) {
                    char c = str.charAt(i);
                    if ('.' == c) {
                        cuttedStr = str.substring(0, i) + str.substring(i + 1);
                        break;
                    }
                }
                /* 删除前面多余的0 */
                int NUM = cuttedStr.length();
                int zeroIndex = -1;
                for (int i = 0; i < NUM - 2; i++) {
                    char c = cuttedStr.charAt(i);
                    if (c != '0') {
                        zeroIndex = i;
                        break;
                    }else if(i == NUM - 3){
                        zeroIndex = i;
                        break;
                    }
                }
                if(zeroIndex != -1){
                    cuttedStr = cuttedStr.substring(zeroIndex);
                }
                /* 不足3位补0 */
                if (cuttedStr.length() < 3) {
                    cuttedStr = "0" + cuttedStr;
                }
                /* 加上dot，以显示小数点后两位 */
                cuttedStr = cuttedStr.substring(0, cuttedStr.length() - 2)
                        + "." + cuttedStr.substring(cuttedStr.length() - 2);

                et_integral.setText(cuttedStr);

                et_integral.setSelection(et_integral.length());
                isChanged = false;
            }

        });
    }

    private View.OnClickListener myclick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.btn_next: //点击下一步
                    if(StringUtil.isEmpty(et_integral.getText().toString())){
                        ToastUtil.showShort(_context,"Please enter the number to be issued.");
                        return;
                    }
                    if(et_integral.getText().toString().equals(0.00)||et_integral.getText().toString().equals(.00)){
                        ToastUtil.showShort(_context,"You must issue an integral greater than 0.");
                        return;
                    }
                    Point();

                    break;
                case R.id.btn_cancel: //取消
                    finish();
                    break;
            }
        }
    };
    //确认发放积分请求
//    private void ReleaseRointsRequest(){
//        Map<String,Object> map = new HashMap<String,Object>();
//        //客户的唯一ID，是从客户的二维码中解析出来的
//        map.put("customer_id", Constans.customerId);
//        //消费金额。2位小数点
//        S=Double.parseDouble(et_integral.getText().toString());
//        map.put("purchase_amount",S);
//        //要发放的点数。由梯度计算公式得出。
//        //int points=(int) Point();
//        map.put("points",points);
//        map.put("location",Constans.location);
//        NetUtil.Request(NetUtil.RequestMethod.POST, Constans.SH_REWARD_POINTS, map,Authorization,Constans.APP_INSTANCE_ID, new NetUtil.RequestCallBack() {
//            @Override
//            public void onSuccess(int statusCode, String json) {
//                ToastUtil.showShort(_context,json);
//                Bundle bundle = new Bundle();
//                bundle.putString("money",S+"");
//                bundle.putString("points",(int)Point()+"");
//                startActivity(IntegralDetailsActivity.class,bundle);
//                finish();
//            }
//
//            @Override
//            public void onFailure(int statusCode, String errorMsg) {
//                ToastUtil.showShort(_context,statusCode+errorMsg);
//            }
//
//            @Override
//            public void onFailure(Exception e, String errorMsg) {
//                ToastUtil.showShort(_context,errorMsg);
//            }
//        });
//    }
    //点数梯度计算公式
    private void Point(){
        //当前时间的毫秒
        long time = new Date().getTime();
        //10分钟的毫秒
        long minutes=10*60*1000;
        //查看当前缓存是否有点数梯度公式
        if(!StringUtil.isEmpty(CacheUtil.getValue(_context, "SpendingTiersBean"))){
            //查看缓存中的点数梯度公式是否超过10分钟未更新
            if((time-(long)CacheUtil.getValue(_context,"CurrentTime"))>minutes) {
                //ToastUtil.showLong(_context,"Preparing gradient formulas for you...");
                TiersRequest();
                SpendingTiersBean bean2 = (SpendingTiersBean) CacheUtil.getValue(_context, "SpendingTiersBean");
                double tier_3_min = bean2.getTier3Min();
                double tier_2_min = bean2.getTier2Min();
                double tier_1_min = bean2.getTier1Min();
                //消费金额。2位小数点
                S=Double.parseDouble(et_integral.getText().toString());
                int p=calculatePoints(S,tier_1_min,tier_2_min,tier_3_min);//梯度公式
                Bundle bundle = new Bundle();
                bundle.putString("money",S+"");
                //要发放的点数。由梯度计算公式得出。
                bundle.putInt("points",p);
                startActivity(IntegralDetailsActivity.class,bundle);
                finish();
        }else{
            SpendingTiersBean bean2 = (SpendingTiersBean) CacheUtil.getValue(_context, "SpendingTiersBean");
            double tier_3_min = bean2.getTier3Min();
            double tier_2_min = bean2.getTier2Min();
            double tier_1_min = bean2.getTier1Min();
            //消费金额。2位小数点
            S=Double.parseDouble(et_integral.getText().toString());
            int p=calculatePoints(S,tier_1_min,tier_2_min,tier_3_min);//梯度公式
                Bundle bundle = new Bundle();
                bundle.putString("money",S+"");
                //要发放的点数。由梯度计算公式得出。
                bundle.putInt("points",p);
                startActivity(IntegralDetailsActivity.class,bundle);
                finish();
        }
        }else{
            ToastUtil.showLong(_context,"Internet connection is required to compelte this transaction.");
            TiersRequest();
        }
    }
    //梯度公式计算发放的点数
    private Integer calculatePoints(Double purchase, Double tier1Min, Double tier2Min, Double tier3Min) {
        double delta = 0.0;
        delta += Math.max(purchase - tier3Min, 0) * 3; //tier 3 earning 3 points for every dolloar
        delta += Math.min(tier3Min - tier2Min, Math.max(purchase - tier2Min, 0)) * 2;//tier 2 earning 2 points for every dolloar
        delta += Math.min(tier2Min - tier1Min, Math.max(purchase - tier1Min, 0)) * 1;//tier 1 earning 1 point for every dolloar
        //DecimalFormat df = new DecimalFormat("######0");//四舍五入
        return Double.valueOf(delta).intValue();
    }

    //获取消费等级划分
    private void TiersRequest(){
        dialog = WeiboDialogUtils.createLoadingDialog(_context,"Loading...");
        NetUtil.Request(NetUtil.RequestMethod.GET, Constans.SH_SENDING_TIERS, null, Authorization, MainApplication.APP_INSTANCE_ID,new NetUtil.RequestCallBack() {
            @Override
            public void onSuccess(int statusCode, String json) {
                //ToastUtil.showShort(_context,json);
                String spending_tiers=GsonUtil.getJsonFromKey(json,"spending_tiers");
                SpendingTiersBean bean=GsonUtil.getBeanFromJson(spending_tiers, SpendingTiersBean.class);
                //将这个实体对象保存在缓存中
                CacheUtil.putValue(_context,"SpendingTiersBean",bean);
                //获取当前时间毫秒
                CurrentTime = new Date().getTime();
                //将当前时间保存到缓存
                CacheUtil.putValue(_context,"CurrentTime",CurrentTime);
                //ToastUtil.showShort(_context,CacheUtil.getValue(_context,"CurrentTime")+"");
                WeiboDialogUtils.closeDialog(dialog);//关闭这个动画
            }

            @Override
            public void onFailure(int statusCode, String errorMsg) {
                ToastUtil.showShort(_context, ToastHintMsgUtil.getToastMsg(errorMsg));
                WeiboDialogUtils.closeDialog(dialog);//关闭这个动画
            }

            @Override
            public void onFailure(Exception e, String errorMsg) {
                ToastUtil.showShort(_context,errorMsg);
                WeiboDialogUtils.closeDialog(dialog);//关闭这个动画
            }
        });
    }



}

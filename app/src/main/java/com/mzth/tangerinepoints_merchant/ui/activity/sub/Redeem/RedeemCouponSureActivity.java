package com.mzth.tangerinepoints_merchant.ui.activity.sub.Redeem;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.mzth.tangerinepoints_merchant.R;
import com.mzth.tangerinepoints_merchant.ui.activity.base.BaseBussActivity;

/**
 * Created by Administrator on 2017/5/5.
 * 优惠券兑换成功页面
 */

public class RedeemCouponSureActivity extends BaseBussActivity {
    private TextView tv_redeem_info;
    private ImageView btn_redeem_ok;
    @Override
    protected void setCustomLayout(Bundle savedInstanceState) {
        super.setCustomLayout(savedInstanceState);
        _context = RedeemCouponSureActivity.this;
        setContentView(R.layout.activity_redeem_success);
    }

    @Override
    protected void initView() {
        super.initView();
        //兑换优惠券成功后的信息
        tv_redeem_info = (TextView) findViewById(R.id.tv_redeem_info);
        //兑换优惠券成功好的ok
        btn_redeem_ok = (ImageView) findViewById(R.id.btn_redeem_ok);
    }
    @Override
    protected void initData() {
        super.initData();
        tv_redeem_info.setText("The coupon is now redeemed.");
    }
    @Override
    protected void BindComponentEvent() {
        super.BindComponentEvent();
        btn_redeem_ok.setOnClickListener(myclick);
    }
    private View.OnClickListener myclick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.btn_redeem_ok://兑换成功好的ok
                    finish();
                    break;
            }
        }
    };

}

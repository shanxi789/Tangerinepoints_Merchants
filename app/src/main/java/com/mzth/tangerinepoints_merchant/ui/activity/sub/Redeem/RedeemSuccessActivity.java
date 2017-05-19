package com.mzth.tangerinepoints_merchant.ui.activity.sub.Redeem;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.mzth.tangerinepoints_merchant.R;
import com.mzth.tangerinepoints_merchant.bean.offer.OfferBean;
import com.mzth.tangerinepoints_merchant.ui.activity.base.BaseBussActivity;

/**
 * Created by Administrator on 2017/4/18.
 * 兑换商品成功页面
 */

public class RedeemSuccessActivity extends BaseBussActivity {
    private TextView tv_redeem_info;
    private ImageView btn_redeem_ok;
    private OfferBean bean;//商品的对象
    @Override
    protected void setCustomLayout(Bundle savedInstanceState) {
        super.setCustomLayout(savedInstanceState);
        _context = RedeemSuccessActivity.this;
        setContentView(R.layout.activity_redeem_success);
    }

    @Override
    protected void initView() {
        super.initView();
        //兑换成功后的信息
        tv_redeem_info = (TextView) findViewById(R.id.tv_redeem_info);
        //兑换成功好的ok
        btn_redeem_ok = (ImageView) findViewById(R.id.btn_redeem_ok);
    }

    @Override
    protected void initData() {
        super.initData();
        bean = (OfferBean) getIntent().getSerializableExtra("bean");
        tv_redeem_info.setText("The customer redeemed offer "+bean.getItemName()
                +" for "+bean.getPoints()+" points.");
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

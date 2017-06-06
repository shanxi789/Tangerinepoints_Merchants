package com.mzth.tangerinepoints_merchant.ui.activity.sub.RewardPoints;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.mzth.tangerinepoints_merchant.R;
import com.mzth.tangerinepoints_merchant.ui.activity.base.BaseBussActivity;

/**
 * Created by Administrator on 2017/4/17.
 */

public class RPScanCodeSuccessActivity extends BaseBussActivity {
    private TextView tv_RP_scan_info;
    private ImageView btn_RP_scancode_ok;
    private MediaPlayer mp;//mediaPlayer对象
    @Override
    protected void setCustomLayout(Bundle savedInstanceState) {
        super.setCustomLayout(savedInstanceState);
        _context=RPScanCodeSuccessActivity.this;
        setContentView(R.layout.activity_rd_scancode_success);
    }

    @Override
    protected void initView() {
        super.initView();
        //扫码成功之后的信息
        tv_RP_scan_info = (TextView) findViewById(R.id.tv_RP_scan_info);
        //OK
        btn_RP_scancode_ok = (ImageView) findViewById(R.id.btn_RP_scancode_ok);
        //创建mediaplayer对象
        //mp.reset();
        mp= MediaPlayer.create(_context, R.raw.reward);
    }
    @Override
    protected void initData() {
        super.initData();

        mp.start();//开始播放
        int points = getIntent().getIntExtra("points",0);
        //将传过来的点数设置进去
        tv_RP_scan_info.setText("You have rewarded the customer "+points+" points.");
    }
    @Override
    protected void BindComponentEvent() {
        super.BindComponentEvent();
        btn_RP_scancode_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    @Override
    protected void onDestroy() {
        mp.release();//释放资源
        super.onDestroy();
    }
}

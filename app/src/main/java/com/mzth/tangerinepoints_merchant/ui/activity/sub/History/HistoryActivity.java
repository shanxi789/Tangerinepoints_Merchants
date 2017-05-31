package com.mzth.tangerinepoints_merchant.ui.activity.sub.History;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.cjj.MaterialRefreshLayout;
import com.cjj.MaterialRefreshListener;
import com.google.gson.reflect.TypeToken;
import com.mzth.tangerinepoints_merchant.R;
import com.mzth.tangerinepoints_merchant.bean.HistoryAllBean;
import com.mzth.tangerinepoints_merchant.common.Constans;
import com.mzth.tangerinepoints_merchant.common.ToastHintMsgUtil;
import com.mzth.tangerinepoints_merchant.ui.activity.base.BaseBussActivity;
import com.mzth.tangerinepoints_merchant.ui.adapter.sub.HistoryAllListAdapter;
import com.mzth.tangerinepoints_merchant.util.GsonUtil;
import com.mzth.tangerinepoints_merchant.util.NetUtil;
import com.mzth.tangerinepoints_merchant.util.SharedPreferencesUtil;
import com.mzth.tangerinepoints_merchant.util.StringUtil;
import com.mzth.tangerinepoints_merchant.util.ToastUtil;
import com.mzth.tangerinepoints_merchant.util.WeiboDialogUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2017/4/17.
 */

public class HistoryActivity extends BaseBussActivity {
    private ImageView iv_back;
    private ListView lv_hisotry;
    private TextView tv_all,tv_points,tv_redeems,tv_coupons;
    private HistoryAllListAdapter adapter;
    private MaterialRefreshLayout refresh;
    private Dialog LoadDialog;//加载动画的Dialog
    private String type;//历史纪录的类型
    @Override
    protected void setCustomLayout(Bundle savedInstanceState) {
        super.setCustomLayout(savedInstanceState);
        _context=HistoryActivity.this;
        setContentView(R.layout.activity_history);
    }
    @Override
    protected void initView() {
        super.initView();
        //返回键监听
        iv_back= (ImageView) findViewById(R.id.iv_back);
        //all的listview
        lv_hisotry = (ListView) findViewById(R.id.lv_hisotry);
        //获取所有
        tv_all = (TextView) findViewById(R.id.tv_all);
        //获取
        tv_points = (TextView) findViewById(R.id.tv_points);
        //获取补偿
        tv_redeems = (TextView) findViewById(R.id.tv_redeems);
        //获取优惠券
        tv_coupons = (TextView) findViewById(R.id.tv_coupons);
        //刷新控件
        refresh = (MaterialRefreshLayout) findViewById(R.id.refresh);
    }

    @Override
    protected void initData() {
        super.initData();
        tv_all.setTextColor(_context.getResources().getColor(R.color.white));
        tv_all.setBackgroundResource(R.drawable.history_text_bg_no);
        type = "ALL";
        String history = (String) SharedPreferencesUtil.getParam(_context,"history","");
        if(StringUtil.isEmpty(history)){
            HistoryRequest();
        }else {
            List<HistoryAllBean> beanList = GsonUtil.getListFromJson(history, new TypeToken<List<HistoryAllBean>>() {
            });
            setDataToView(beanList);//分页加载
        }
    }

    @Override
    protected void BindComponentEvent() {
        super.BindComponentEvent();
        iv_back.setOnClickListener(myonclick);
        tv_all.setOnClickListener(myonclick);
        tv_points.setOnClickListener(myonclick);
        tv_redeems.setOnClickListener(myonclick);
        tv_coupons.setOnClickListener(myonclick);
        //设置刷新控件的刷新事件
        refresh.setMaterialRefreshListener(materialRefreshListener);
    }
    private MaterialRefreshListener materialRefreshListener = new MaterialRefreshListener() {
        //下拉刷新
        @Override
        public void onRefresh(MaterialRefreshLayout materialRefreshLayout) {
            //刷新
            index = 0;
            HistoryRequest();
        }

        @Override
        public void onRefreshLoadMore(MaterialRefreshLayout materialRefreshLayout) {
            index += n;
            HistoryRequest();

        }
    };
    //结束刷新
    private void finishRefresh(){
        //结束下拉刷新
        refresh.finishRefresh();
        //结束上拉加载
        refresh.finishRefreshLoadMore();
    }
    private View.OnClickListener myonclick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.iv_back://返回键监听
                    onBackPressed();
                    break;
                case R.id.tv_all://获取所有
                    tv_all.setTextColor(_context.getResources().getColor(R.color.white));
                    tv_all.setBackgroundResource(R.drawable.history_text_bg_no);
                    tv_points.setTextColor(_context.getResources().getColor(R.color.orange_red));
                    tv_points.setBackgroundResource(R.drawable.history_text_bg);
                    tv_redeems.setTextColor(_context.getResources().getColor(R.color.orange_red));
                    tv_redeems.setBackgroundResource(R.drawable.history_text_bg);
                    tv_coupons.setTextColor(_context.getResources().getColor(R.color.orange_red));
                    tv_coupons.setBackgroundResource(R.drawable.history_text_bg);
                    type = "ALL";
                    index = 0;
                    HistoryRequest();
                    break;
                case R.id.tv_points:
                    tv_points.setTextColor(_context.getResources().getColor(R.color.white));
                    tv_points.setBackgroundResource(R.drawable.history_text_bg_no);
                    tv_all.setBackgroundResource(R.drawable.history_text_bg);
                    tv_all.setTextColor(_context.getResources().getColor(R.color.orange_red));
                    tv_redeems.setBackgroundResource(R.drawable.history_text_bg);
                    tv_redeems.setTextColor(_context.getResources().getColor(R.color.orange_red));
                    tv_coupons.setBackgroundResource(R.drawable.history_text_bg);
                    tv_coupons.setTextColor(_context.getResources().getColor(R.color.orange_red));
                    type = "PURCHASE";
                    index = 0;
                    HistoryRequest();
                    break;
                case R.id.tv_redeems://获取补偿
                    tv_redeems.setTextColor(_context.getResources().getColor(R.color.white));
                    tv_redeems.setBackgroundResource(R.drawable.history_text_bg_no);
                    tv_points.setBackgroundResource(R.drawable.history_text_bg);
                    tv_points.setTextColor(_context.getResources().getColor(R.color.orange_red));
                    tv_all.setBackgroundResource(R.drawable.history_text_bg);
                    tv_all.setTextColor(_context.getResources().getColor(R.color.orange_red));
                    tv_coupons.setBackgroundResource(R.drawable.history_text_bg);
                    tv_coupons.setTextColor(_context.getResources().getColor(R.color.orange_red));
                    type = "OFFER_REDEEM";
                    index = 0;
                    HistoryRequest();
                    break;
                case R.id.tv_coupons://获取优惠券
                    tv_coupons.setTextColor(_context.getResources().getColor(R.color.white));
                    tv_coupons.setBackgroundResource(R.drawable.history_text_bg_no);
                    tv_points.setBackgroundResource(R.drawable.history_text_bg);
                    tv_points.setTextColor(_context.getResources().getColor(R.color.orange_red));
                    tv_redeems.setBackgroundResource(R.drawable.history_text_bg);
                    tv_redeems.setTextColor(_context.getResources().getColor(R.color.orange_red));
                    tv_all.setBackgroundResource(R.drawable.history_text_bg);
                    tv_all.setTextColor(_context.getResources().getColor(R.color.orange_red));
                    type = "COUPON_REDEEM";
                    index = 0;
                    HistoryRequest();
                    break;
            }
        }
    };
    //获取历史交易记录请求
    private void HistoryRequest(){
        //加载动画
        LoadDialog = WeiboDialogUtils.createLoadingDialog(_context,"Loading...");
        Map<String,Object> map = new HashMap<String,Object>();
        map.put("start_index",index);
        map.put("n",n);
        map.put("type",type);
        NetUtil.Request(NetUtil.RequestMethod.GET, Constans.SH_TRANSACTION_HISTORY, map, Authorization, Constans.APP_INSTANCE_ID, new NetUtil.RequestCallBack() {
            @Override
            public void onSuccess(int statusCode, String json) {
                //ToastUtil.showShort(_context,json);
                String history = GsonUtil.getJsonFromKey(json,"history");
                SharedPreferencesUtil.setParam(_context,"history",history);
                List<HistoryAllBean> beanList = GsonUtil.getListFromJson(history,new TypeToken<List<HistoryAllBean>>(){});
                setDataToView(beanList);//分页加载
                finishRefresh();
                WeiboDialogUtils.closeDialog(LoadDialog);//关闭动画
            }

            @Override
            public void onFailure(int statusCode, String errorMsg) {
                ToastUtil.showShort(_context, ToastHintMsgUtil.getToastMsg(errorMsg));
                finishRefresh();
                WeiboDialogUtils.closeDialog(LoadDialog);//关闭动画
            }

            @Override
            public void onFailure(Exception e, String errorMsg) {
                ToastUtil.showShort(_context,errorMsg);
                finishRefresh();
                WeiboDialogUtils.closeDialog(LoadDialog);//关闭动画
            }
        });
    }
    //加载
    private void setDataToView(List<HistoryAllBean> list){
        if(index==0){
            if(!StringUtil.isEmpty(list)){
                if(adapter==null){
                    adapter = new HistoryAllListAdapter(_context,list,R.layout.hisotry_all_item);
                    lv_hisotry.setAdapter(adapter);
                }else{
                    adapter.clearAll();
                    adapter.add(list);
                }
            }else{
                if(adapter != null){
                    adapter.clearAll();
                }
                ToastUtil.showShort(_context, "No Information");
            }
        }else{
            if(!StringUtil.isEmpty(list)){
                adapter.add(list);
                if(list.size()<n){//如果 list的大小小于n返回的条数说明数据加载完毕
                    ToastUtil.showShort(_context, "That's all.");
                }
            }else {
                ToastUtil.showShort(_context, "That's all.");
            }
        }
    }

}

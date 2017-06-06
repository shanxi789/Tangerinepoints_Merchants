package com.mzth.tangerinepoints_merchant.ui.adapter.sub;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.mzth.tangerinepoints_merchant.R;
import com.mzth.tangerinepoints_merchant.bean.HistoryAllBean;
import com.mzth.tangerinepoints_merchant.common.Constans;
import com.mzth.tangerinepoints_merchant.common.MainApplication;
import com.mzth.tangerinepoints_merchant.common.ToastHintMsgUtil;
import com.mzth.tangerinepoints_merchant.ui.adapter.base.BaseInfoAdapter;
import com.mzth.tangerinepoints_merchant.util.DialogUtil;
import com.mzth.tangerinepoints_merchant.util.NetUtil;
import com.mzth.tangerinepoints_merchant.util.SharedPreferencesUtil;
import com.mzth.tangerinepoints_merchant.util.ToastUtil;
import com.mzth.tangerinepoints_merchant.util.WeiboDialogUtils;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Created by Administrator on 2017/4/18.
 */

public class HistoryAllListAdapter extends BaseInfoAdapter<HistoryAllBean> {
    public HistoryAllListAdapter(Context context, List<HistoryAllBean> list, int resId) {
        super(context, list, resId);
    }

    @Override
    public View dealView(Context context, List<HistoryAllBean> list, int resId, int position, View convertView) {
        ViewHolder vh=null;
        if(convertView==null){
            convertView=View.inflate(context,resId,null);
            vh=new ViewHolder();
            vh.initView(convertView);
            convertView.setTag(vh);
        }else{
            vh= (ViewHolder) convertView.getTag();
        }
        vh.initData(list,position);
        return convertView;
    }

    class ViewHolder{
        TextView tv_history_type,tv_history_money,tv_history_name,
                tv_history_time,tv_canceled;
        ImageView iv_history_cancel;
        private void initView(View v){
            tv_history_type= (TextView) v.findViewById(R.id.tv_history_type);
            tv_history_money= (TextView) v.findViewById(R.id.tv_history_money);
            tv_history_name= (TextView) v.findViewById(R.id.tv_history_name);
            tv_history_time= (TextView) v.findViewById(R.id.tv_history_time);
            tv_canceled = (TextView)v.findViewById(R.id.tv_history_canceled);
            //void 取消一个当天的交易
            iv_history_cancel = (ImageView) v.findViewById(R.id.iv_history_cancel);
        }
        private void initData(final List<HistoryAllBean> list, final int position){
            HistoryAllBean bean = list.get(position);
            tv_history_type.setText(bean.getTitle());
            tv_history_money.setText(bean.getDescription());
            tv_history_name.setText("Customer: "+bean.getCustomerScreenName());
            if(bean.isCanceled()){//判断是否取消
                iv_history_cancel.setVisibility(View.GONE);//void隐藏
                tv_canceled.setVisibility(View.VISIBLE);//让取消的标记显示
            }else{
                iv_history_cancel.setVisibility(View.VISIBLE);//void隐藏
                tv_canceled.setVisibility(View.GONE);//让取消的标记显示
            }
            //转换时间
//            String times= DateFormat.getDateTimeInstance(DateFormat.LONG, 3).format(new Date(bean.getTime()));
//            String timesub = "";
//            if(times.indexOf("上午")!=-1){
//                timesub = times.substring(times.indexOf("午")+1,times.length())+"AM";
//            }else{
//                timesub = times.substring(times.indexOf("午")+1,times.length())+"PM";
//            }
            //String time= DateFormat.getDateInstance(DateFormat.LONG, Locale.US).format(new Date(bean.getTime()));
            String time1 = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.MEDIUM, Locale.US).format(new Date(bean.getTime()));
            tv_history_time.setText(time1);
            //取消一个当天的交易
            iv_history_cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DialogUtil.alertDialog(_context, "Confirmation", "Are you sure you want to exit?",
                            "Confirm", "Cancel", true, new DialogUtil.ReshActivity() {
                                @Override
                                public void reshActivity() {//确定按钮
                                    //将登录成功后返回的accesskey保存在sp中
                                    CancleRequest(list.get(position).getTxnId());
                                }
                            });

                }
            });

        }
        //取消一个当天的交易
        private void CancleRequest(String id){
            final Dialog dialog = WeiboDialogUtils.createLoadingDialog(_context,"Loading...");
            Map<String,Object> map = new HashMap<String,Object>();
            map.put("txn_id",id);
            NetUtil.Request(NetUtil.RequestMethod.POST, Constans.SH_TRANSACTION_CANCEL, map, (String) SharedPreferencesUtil.getParam(_context,"accessKey",""), MainApplication.APP_INSTANCE_ID, new NetUtil.RequestCallBack() {
                @Override
                public void onSuccess(int statusCode, String json) {
                    ToastUtil.showShort(_context,"Cancel Success");
                    iv_history_cancel.setVisibility(View.GONE);//void隐藏
                    tv_canceled.setVisibility(View.VISIBLE);//让取消的标记显示
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

    }
}

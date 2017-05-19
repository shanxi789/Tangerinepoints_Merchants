package com.mzth.tangerinepoints_merchant.ui.adapter.sub;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.mzth.tangerinepoints_merchant.R;
import com.mzth.tangerinepoints_merchant.bean.BusinessesBean;
import com.mzth.tangerinepoints_merchant.ui.adapter.base.BaseInfoAdapter;

import java.util.List;

/**
 * Created by Administrator on 2017/4/15.
 */

public class StoreListAdapter extends BaseInfoAdapter<BusinessesBean> {
    public StoreListAdapter(Context context, List<BusinessesBean> list, int resId) {
        super(context, list, resId);
    }

    @Override
    public View dealView(Context context, List<BusinessesBean> list, int resId, int position, View convertView) {
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
        TextView tv_store_name;
        private void initView(View v){
            tv_store_name= (TextView) v.findViewById(R.id.tv_store_name);
        }
        private void initData(List<BusinessesBean> list,int position){
            BusinessesBean bean=list.get(position);
            //设置商店的名称
            tv_store_name.setText(bean.getBusinessName());
        }
    }
}

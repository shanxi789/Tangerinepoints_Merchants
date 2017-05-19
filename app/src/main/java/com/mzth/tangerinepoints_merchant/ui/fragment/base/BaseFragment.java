package com.mzth.tangerinepoints_merchant.ui.fragment.base;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * @Describe 
 * @Author leeandy007
 * @Date 2016-9-2 下午2:05:15
 */
public abstract class BaseFragment extends Fragment {

	protected Context _context;
	protected int resId = -1;
	protected FragmentCallBack mCallBack;

	
	/**
	 * Activity取Fragment所传递的值时调用的回调接口
	 * */
	public interface FragmentCallBack{
		
		/** 
		 * @DESC Activity中调用取出Fragment中的值
		 * @param param Object...变参多个不固定个数不规定类型的返回结果
		 **/
		public void setResult(Object... param);
		
	}
	
	public BaseFragment(){
		super();
	}

	public BaseFragment(Context context) {
		this._context = context;
	}

	public BaseFragment(Context context, int resId) {
		this._context = context;
		this.resId = resId;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
		View v = null;
		if(resId != -1){
			v = inflater.inflate(resId, null);
			initView(v, savedInstanceState);
			BindComponentEvent();
			initData();
		}
		return v;
	}
	
	/**
	 * 初始化界面
	 * */
	protected abstract void initView(View v, Bundle savedInstanceState);
	
	/**
	 * 初始化界面
	 * */
	protected abstract void initData();
	
	/**
	 * 绑定控件事件
	 * */
	protected abstract void BindComponentEvent();
	
	/**
	 * 声明Fragment实例，所创建的回调接口必须要在Activity中实现
	 * */
	@Override
	public void onAttach(Activity activity) {
		try {
			mCallBack = (FragmentCallBack) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ " must implement FragmentCallBack");
		}
		super.onAttach(activity);
	}

	/**
	 * 声明Fragment实例，所创建的回调接口必须要在Activity中实现
	 * */
	@Override
	public void onAttach(Context context) {
		try {
			mCallBack = (FragmentCallBack) context;
		} catch (ClassCastException e) {
			throw new ClassCastException(context.toString()
					+ " must implement FragmentCallBack");
		}
		super.onAttach(context);
	}

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (resultCode == Activity.RESULT_OK) {
            doActivityResult(requestCode, intent);
        }
    }

    /**
     * 带返回值跳转的数据的处理方法
     * */
    protected abstract void doActivityResult(int requestCode, Intent intent);

	/**
	 * @Desc 正常页面跳转
	 * @param clszz 目标页面
	 * @param bundle 传值载体
	 * */
	protected void startActivity(Class clszz, Bundle bundle){
		Intent intent = new Intent(_context, clszz);
        if(bundle != null){
            intent.putExtras(bundle);
        }
		startActivity(intent);
	}

	/**
	 * @Desc 带返回值跳转
	 * @param clszz 目标页面
	 * @param bundle 传值
	 * @param requestCode 请求码
	 * */
	protected void startActivityForResult(Class clszz, Bundle bundle, int requestCode){
		Intent intent = new Intent(_context, clszz);
		if(bundle != null){
			intent.putExtras(bundle);
		}
        startActivityForResult(intent, requestCode);
	}


}

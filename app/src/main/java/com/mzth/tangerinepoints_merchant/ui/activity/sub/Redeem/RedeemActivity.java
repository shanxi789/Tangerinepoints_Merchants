package com.mzth.tangerinepoints_merchant.ui.activity.sub.Redeem;

import android.app.Dialog;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.hardware.Camera;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mzth.tangerinepoints_merchant.R;
import com.mzth.tangerinepoints_merchant.bean.CouponBean;
import com.mzth.tangerinepoints_merchant.bean.offer.OfferBean;
import com.mzth.tangerinepoints_merchant.common.Constans;
import com.mzth.tangerinepoints_merchant.common.MainApplication;
import com.mzth.tangerinepoints_merchant.ui.activity.base.BaseBussActivity;
import com.mzth.tangerinepoints_merchant.util.GsonUtil;
import com.mzth.tangerinepoints_merchant.util.NetUtil;
import com.mzth.tangerinepoints_merchant.util.SoundUtils;
import com.mzth.tangerinepoints_merchant.util.StringUtil;
import com.mzth.tangerinepoints_merchant.util.ToastUtil;
import com.mzth.tangerinepoints_merchant.util.WeiboDialogUtils;
import com.mzth.tangerinepoints_merchant.widget.FinderView;
import com.sunmi.scan.Config;
import com.sunmi.scan.Image;
import com.sunmi.scan.ImageScanner;
import com.sunmi.scan.Symbol;
import com.sunmi.scan.SymbolSet;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2017/4/17.
 */

public class RedeemActivity extends BaseBussActivity implements SurfaceHolder.Callback{
    private ImageView btn_cancel_scancode;
    private FinderView finder_view;
    private Camera mCamera;
    private ImageScanner scanner;// 声明扫描器
    private SurfaceHolder mHolder;
    private Handler autoFocusHandler;
    private AsyncDecode asyncDecode;
    SoundUtils soundUtils;
    private boolean vibrate;
    public int decode_count = 0;
    private TextView textview,tv_redeem;
    private SurfaceView surface_view;
    private ImageView capture_scan_line;//扫描线
    private RelativeLayout rl_redeem;
    private Dialog dialog;//加载动画的对话框
    private int width,height,scan_line_height;//获取二维码扫描布局的宽和高
    private String str = "";
    private ImageView iv_back;//返回键
    @Override
    protected void setCustomLayout(Bundle savedInstanceState) {
        super.setCustomLayout(savedInstanceState);
        _context=RedeemActivity.this;
        setContentView(R.layout.activity_home_redeem);
    }

    @Override
    protected void initView() {
        super.initView();
        //取消
        btn_cancel_scancode= (ImageView) findViewById(R.id.btn_cancel_redeem);
        //扫码的控件
        finder_view= (FinderView) findViewById(R.id.finder_view);
        surface_view = (SurfaceView) findViewById(R.id.surface_view);
        //获取扫码信息
        textview = (TextView) findViewById(R.id.tv_scancode_info);
        //用标题的点击事件测试一下请求
        tv_redeem= (TextView) findViewById(R.id.tv_redeem_title);
        //扫描线
        capture_scan_line= (ImageView) findViewById(R.id.capture_scan_line);
        //获取屏幕的高度
        WindowManager wm = this.getWindowManager();
        height=wm.getDefaultDisplay().getHeight();
        //返回键
        iv_back = (ImageView) findViewById(R.id.iv_back);
    }

    @Override
    protected void initData() {
        super.initData();
        //设置capture_scan_line的位置
        int scan_line_height=height/10;
        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) capture_scan_line.getLayoutParams();
        lp.setMargins(0,scan_line_height,0,0);
        capture_scan_line.setLayoutParams(lp);

        mHolder = surface_view.getHolder();
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        mHolder.addCallback(this);
        // sym0=new SymbolSet();
        scanner = new ImageScanner();// 创建扫描器
        scanner.setConfig(0, Config.X_DENSITY, 2);// 行扫描间隔
        scanner.setConfig(0, Config.Y_DENSITY, 2);// 列扫描间隔
        scanner.setConfig(0, Config.ENABLE_MULTILESYMS, 0);// 是否开启同一幅图一次解多个条码,0表示只解一个，1为多个
        scanner.setConfig(0, Config.ENABLE_INVERSE, 0);// 是否解反色的条码

        autoFocusHandler = new Handler();
        asyncDecode = new AsyncDecode();
        decode_count = 0;
        //扫描线
        //半屏的时候显示的扫描线
//        TranslateAnimation mAnimation = new TranslateAnimation(TranslateAnimation.ABSOLUTE, 0f, TranslateAnimation.ABSOLUTE, 0f,
//                TranslateAnimation.RELATIVE_TO_PARENT, 0f, TranslateAnimation.RELATIVE_TO_PARENT, 0.6f);
        //全屏的时候展示的扫描线
        TranslateAnimation mAnimation = new TranslateAnimation(TranslateAnimation.ABSOLUTE, 0f, TranslateAnimation.ABSOLUTE, 0f,
                TranslateAnimation.RELATIVE_TO_PARENT, 0.2f, TranslateAnimation.RELATIVE_TO_PARENT, 0.55f);
        mAnimation.setDuration(2000);
        mAnimation.setRepeatCount(-1);
        mAnimation.setRepeatMode(Animation.REVERSE);
        mAnimation.setInterpolator(new LinearInterpolator());
        capture_scan_line.setAnimation(mAnimation);
    }
    @Override
    protected void BindComponentEvent() {
        super.BindComponentEvent();
        btn_cancel_scancode.setOnClickListener(myonclick);
        tv_redeem.setOnClickListener(myonclick);
        iv_back.setOnClickListener(myonclick);
    }

    private View.OnClickListener myonclick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.btn_cancel_redeem://取消按钮
                    finish();
                    break;
                case R.id.tv_redeem_title://用标题的点击时间测试一下请求
                    break;
                case R.id.iv_back:
                    onBackPressed();
                    break;
            }
        }
    };



    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        try {
            mCamera = Camera.open();
        } catch (Exception e) {
            mCamera = null;
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        if (mHolder.getSurface() == null) {
            return;
        }
        try {
            mCamera.stopPreview();
        } catch (Exception e) {
        }
        try {
            // 摄像头预览分辨率设置和图像放大参数设置，非必须，根据实际解码效果可取舍
            Camera.Parameters parameters = mCamera.getParameters();
            //parameters.setPreviewSize(800, 480); // 设置预览分辨率
            // parameters.set("zoom", String.valueOf(27 / 10.0));//放大图像2.7倍
            mCamera.setParameters(parameters);
            // //////////////////////////////////////////
            mCamera.setDisplayOrientation(90);// 竖屏显示
            mCamera.setPreviewDisplay(mHolder);
            mCamera.setPreviewCallback(previewCallback);
            mCamera.startPreview();
            mCamera.autoFocus(autoFocusCallback);
        } catch (Exception e) {
            Log.d("DBG", "Error starting camera preview: " + e.getMessage());
        }
    }
    /**
     * 预览数据
     */
    Camera.PreviewCallback previewCallback = new Camera.PreviewCallback() {
        public void onPreviewFrame(byte[] data, Camera camera) {
            if (asyncDecode.isStoped()) {
                Camera.Parameters parameters = camera.getParameters();
                Camera.Size size = parameters.getPreviewSize();// 获取预览分辨率

                // 创建解码图像，并转换为原始灰度数据，注意图片是被旋转了90度的
                Image source = new Image(size.width, size.height, "Y800");
                Rect scanImageRect = finder_view.getScanImageRect(size.height,
                        size.width);
                // 图片旋转了90度，将扫描框的TOP作为left裁剪
                source.setCrop(scanImageRect.top, scanImageRect.left,
                        scanImageRect.height(), scanImageRect.width());
                source.setData(data);// 填充数据
                asyncDecode = new AsyncDecode();
                asyncDecode.execute(source);// 调用异步执行解码
                //优惠券二维码信息
                if(str.indexOf("coupon")!=-1){
                    //优惠券ID
                    String couponId = str.substring(str.indexOf(",")+1,str.lastIndexOf(","));
                    //用户ID
                    String customerId = str.substring(str.lastIndexOf(",")+1,str.length());
                    CouponRequest(couponId,customerId);//获取coupon信息请求
                }else if(str.indexOf("offer")!=-1){
                    //兑换ID
                    String offerId = str.substring(str.indexOf(",")+1,str.lastIndexOf(","));
                    //用户ID
                    String customerId = str.substring(str.lastIndexOf(",")+1,str.length());
                    GetOfferRequest(offerId,customerId);//获取offer信息请求
                }
            }
        }
    };

    /**
     * 自动对焦回调
     */
    Camera.AutoFocusCallback autoFocusCallback = new Camera.AutoFocusCallback() {
        public void onAutoFocus(boolean success, Camera camera) {
            autoFocusHandler.postDelayed(doAutoFocus, 1000);
        }
    };

    // 自动对焦
    private Runnable doAutoFocus = new Runnable() {
        public void run() {
            if (null == mCamera || null == autoFocusCallback) {
                return;
            }
            mCamera.autoFocus(autoFocusCallback);
        }
    };
    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        if (mCamera != null) {
            mCamera.setPreviewCallback(null);
            mCamera.release();
            mCamera = null;

        }
    }


    private class AsyncDecode extends AsyncTask<Image, Void, Void> {
        private boolean stoped = true;


        @Override
        protected Void doInBackground(Image... params) {
            stoped = false;
            StringBuilder sb = new StringBuilder();
            Image src_data = params[0];// 获取灰度数据

            long startTimeMillis = System.currentTimeMillis();

            // 解码，返回值为0代表失败，>0表示成功
            int nsyms = scanner.scanImage(src_data);

            long endTimeMillis = System.currentTimeMillis();
            long cost_time = endTimeMillis - startTimeMillis;

            if (nsyms != 0) {
                playBeepSoundAndVibrate();// 解码成功播放提示音

                decode_count++;
//                sb.append("计数: " + String.valueOf(decode_count) + ", 耗时: "
//                        + String.valueOf(cost_time) + " ms \n");

                SymbolSet syms = scanner.getResults();// 获取解码结果
                for (Symbol sym : syms) {
//                    sb.append("[ " + sym.getSymbolName() + " ]: "
//                            + sym.getResult() + "\n");
                    sb.append(sym.getResult());
                }
            }
            str = sb.toString();
            return null;
        }



        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            stoped = true;
            if (!StringUtil.isEmpty(str)) {
                    textview.setText(str);// 显示解码结果
                    //扫码成功后跳转页面
                    //优惠券二维码信息
//                    if(str.indexOf("coupon")!=-1){
//                        //优惠券ID
//                        String couponId = str.substring(str.indexOf(",")+1,str.lastIndexOf(","));
//                        //用户ID
//                        String customerId = str.substring(str.lastIndexOf(",")+1,str.length());
//                        CouponRequest(couponId,customerId);//获取coupon信息请求
//                    }else if(str.indexOf("offer")!=-1){
//                        //兑换ID
//                        String offerId = str.substring(str.indexOf(",")+1,str.lastIndexOf(","));
//                        //用户ID
//                        String customerId = str.substring(str.lastIndexOf(",")+1,str.length());
//                        GetOfferRequest(offerId,customerId);//获取offer信息请求
//                    }

            }
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);

        }

        public boolean isStoped() {
            return stoped;
        }
    }
    private void initBeepSound() {
        if (soundUtils == null) {
            soundUtils = new SoundUtils(this, SoundUtils.RING_SOUND);
            soundUtils.putSound(0, R.raw.beep);
        }
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        initBeepSound();
        vibrate = false;
    }
    private static final long VIBRATE_DURATION = 200L;

    private void playBeepSoundAndVibrate() {
        if (soundUtils != null) {
            soundUtils.playSound(0, SoundUtils.SINGLE_PLAY);
        }
        if (vibrate) {
            Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
            vibrator.vibrate(VIBRATE_DURATION);
        }
    }
    //获取coupon信息
    private void CouponRequest(String couponId,String customerId){
        dialog = WeiboDialogUtils.createLoadingDialog(_context,"Loading...");
        //从coupon的二维码中解析出来的
        NetUtil.Request(NetUtil.RequestMethod.GET,Constans.SH_COUPON+couponId,null,Authorization, MainApplication.APP_INSTANCE_ID,new NetUtil.RequestCallBack(){

            @Override
            public void onSuccess(int statusCode, String json) {
                //ToastUtil.showShort(_context,json);
                String coupon = GsonUtil.getJsonFromKey(json,"coupon");
                CouponBean bean =  GsonUtil.getBeanFromJson(coupon,CouponBean.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("bean",bean);
                startActivity(RedeemForwardActivity.class,bundle);
                WeiboDialogUtils.closeDialog(dialog);//关闭动画
            }

            @Override
            public void onFailure(int statusCode, String errorMsg) {
                ToastUtil.showShort(_context,errorMsg);
                WeiboDialogUtils.closeDialog(dialog);//关闭动画
            }

            @Override
            public void onFailure(Exception e, String errorMsg) {
                ToastUtil.showShort(_context,errorMsg);
                WeiboDialogUtils.closeDialog(dialog);//关闭动画
            }
        });
    }
    //获取offer信息
    private void GetOfferRequest(String offerId, final String customerId){
        dialog = WeiboDialogUtils.createLoadingDialog(_context,"Loading...");
        //从offer的二维码中解析出来的
        NetUtil.Request(NetUtil.RequestMethod.GET,Constans.SH_OFFER+offerId,null,Authorization,MainApplication.APP_INSTANCE_ID,new NetUtil.RequestCallBack(){
            @Override
            public void onSuccess(int statusCode, String json) {
                ToastUtil.showShort(_context,json);
                String offer = GsonUtil.getJsonFromKey(json,"offer");
                OfferBean offerBean = GsonUtil.getBeanFromJson(offer,OfferBean.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("bean",offerBean);
                bundle.putString("customerId",customerId);
                startActivity(RedeemOfferActivity.class,bundle);
                WeiboDialogUtils.closeDialog(dialog);//关闭动画
            }

            @Override
            public void onFailure(int statusCode, String errorMsg) {
                ToastUtil.showShort(_context,errorMsg);
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

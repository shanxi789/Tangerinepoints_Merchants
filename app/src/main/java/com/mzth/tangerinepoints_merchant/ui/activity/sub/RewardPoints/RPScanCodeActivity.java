package com.mzth.tangerinepoints_merchant.ui.activity.sub.RewardPoints;

import android.app.Dialog;
import android.content.Intent;
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
import com.mzth.tangerinepoints_merchant.common.Constans;
import com.mzth.tangerinepoints_merchant.ui.activity.base.BaseBussActivity;
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

public class RPScanCodeActivity extends BaseBussActivity implements SurfaceHolder.Callback{
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
    private TextView textview;
    private SurfaceView surface_view;
    private ImageView capture_scan_line;//扫描线
    private int height;
    private double money;//消费金额
    private int points;//点数
    private Dialog dialog;//加载是显示的动画
    private String str = "";
    private boolean stoped = true;
    @Override
    protected void setCustomLayout(Bundle savedInstanceState) {
        super.setCustomLayout(savedInstanceState);
        _context=RPScanCodeActivity.this;
        setContentView(R.layout.activity_rewardroints_scancode);
    }

    @Override
    protected void initView() {
        super.initView();
        //取消
        btn_cancel_scancode= (ImageView) findViewById(R.id.btn_cancel_scancode);
        //扫码的控件
        finder_view= (FinderView) findViewById(R.id.finder_view);
        surface_view = (SurfaceView) findViewById(R.id.surface_view);
        //获取扫码信息
        textview = (TextView) findViewById(R.id.tv_scancode_info);
        //扫描线
        capture_scan_line= (ImageView) findViewById(R.id.capture_scan_line);

        //获取屏幕的高度
        WindowManager wm = this.getWindowManager();
        height=wm.getDefaultDisplay().getHeight();
    }
    @Override
    protected void initData() {
        super.initData();
        Intent intent=getIntent();
        money=intent.getDoubleExtra("money",0.00);
        points=intent.getIntExtra("points",0);
        //设置capture_scan_line的位置
        int scan_line_height=height/13;
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
//        TranslateAnimation mAnimation = new TranslateAnimation(TranslateAnimation.ABSOLUTE, 0f, TranslateAnimation.ABSOLUTE, 0f,
//                TranslateAnimation.RELATIVE_TO_PARENT, 0f, TranslateAnimation.RELATIVE_TO_PARENT, 0.65f);
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
    }

    private View.OnClickListener myonclick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.btn_cancel_scancode:
                    finish();
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
            parameters.setPreviewSize(800, 480); // 设置预览分辨率
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
                //扫码成功后跳转页面
                if(str.indexOf("customer")!=-1){
                    //得到用户的ID
                    String customerId = str.substring(str.indexOf(",")+1,str.length());
                    ReleaseRointsRequest(customerId);//确认发放积分
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

            }
        }


        public boolean isStoped() {
            return stoped;
        }
    }
    //确认发放积分请求
    private void ReleaseRointsRequest(String customerId){
        dialog = WeiboDialogUtils.createLoadingDialog(_context,"Loading...");
        Map<String,Object> map = new HashMap<String,Object>();
        //客户的唯一ID，是从客户的二维码中解析出来的
        map.put("customer_id", customerId);
        //消费金额。2位小数点
        map.put("purchase_amount",money);
        //要发放的点数。由梯度计算公式得出。
        map.put("points",points);
        map.put("location",Constans.location);
        NetUtil.Request(NetUtil.RequestMethod.POST, Constans.SH_REWARD_POINTS, map,Authorization,Constans.APP_INSTANCE_ID, new NetUtil.RequestCallBack() {
            @Override
            public void onSuccess(int statusCode, String json) {
                ToastUtil.showShort(_context,json);
                Bundle bundle = new Bundle();
                bundle.putInt("points",points);
                startActivity(RPScanCodeSuccessActivity.class,bundle);
                finish();
                WeiboDialogUtils.closeDialog(dialog);//关闭动画
            }

            @Override
            public void onFailure(int statusCode, String errorMsg) {
                ToastUtil.showShort(_context,statusCode+errorMsg);
                WeiboDialogUtils.closeDialog(dialog);//关闭动画
            }

            @Override
            public void onFailure(Exception e, String errorMsg) {
                ToastUtil.showShort(_context,errorMsg);
                WeiboDialogUtils.closeDialog(dialog);//关闭动画
            }
        });
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
}

package com.mzth.tangerinepoints_merchant.common;

/**
 * Created by Administrator on 2017/4/14.
 */

public class Constans {

//----------------------------------测试数据----------------------------------

    //设备的IMEI码
    public static final String DEVICE_IMEI = "861299030777136";
    //在APP安装时生成并保存的一个随机的UUID，用于唯一标识一个APP实例
    public static final String APP_INSTANCE_ID = "90804fb4-79d0-4a6e-bafb-e7456cea79a6";
    //商户的PIN
    public static final String MERCHANT_PIN = "some_pwd";

    public static final String merchantId = "0bbcae96-b71d-4562-94a7-bcaa24d7d3de";

    public static final String businessId = "d6bcf726-17ef-4430-a8bb-97e4b77aa35b";
    //表示位置信息的字符串，格式为latitude, longitude。如果当前位置无法获得，则尝试使用Last Known Location
    public static final String location = "40.4792841,-79.9194674";

    public static final String offerId = "9cfb752d-cb56-4273-88da-22121e2ee09e";

    public static final String couponId = "c1cb0b7a-fcd7-4c31-ae6d-e172a8529b93";
    //客户的唯一ID，是从客户的二维码中解析出来的。
    public static final String customerId = "0bbcae96-b71d-4562-94a7-bcaa24d7d3df";

//-------------------------------------报错代码-出错提示信息----------------------------
    //登录失败
    public static final String LOGIN_FAILURE = "Login failed with provided PIN.";
    //无效的位置数据
    public static final String INVALID_LOCATION_DATA = "Invalid Location data.";
    //商户PIN设备不匹配
    public static final String MERCHANT_PIN_DEVICE_MISMATCH = "PIN and device does not match.";
    //商人商店不匹配
    public static final String MERCHANT_BUSINESS_MISMATCH = "The business you were trying to log in is not registered under your merchant account.";
    //未指定消费层
    public static final String SPENDING_TIERS_NOT_SPECIFIED = "Spending tiers have not been specified. Please specify spending tiers via merchant portal website.";

    public static final String INVALID_OFFER_ID = "Invalid Offer ID.";

    public static final String INVALID_TXN_ID = "Invalid transaction ID.";

    public static final String INVALID_USER_ID = "Invalid User ID.";

    public static final String POINTS_AMOUNT_EXCEED_LIMIT = "This transaction cannot be completed because the amount of points have exceeded a preset limit.";

    public static final String SUSPICIOUS_LOCATION = "This transaction cannot be completed because it is not originated from your business location.";

    public static final String COUPON_CODE_NOT_ACCEPTED_HERE = "The coupon is not accepted here.";

    public static final String COUPON_CODE_EXPIRED = "The coupon has expired.";

    public static final String COUPON_CODE_INVALID = "The coupon is invalid.";

    public static final String OFFER_NOT_FROM_HERE = "The requested item of redemption is not from here.";

    public static final String OFFER_RETIRED = "The requested item of redemption is no longer available.";

    public static final String CANNOT_CANCEL_TXN = "This transaction cannot be cancelled.";

    public static final String INSUFFICIENT_POINTS = "Not enough points to redeem this offer.";


//---------------------------------商户端app接口---------------------------------------



    //列出商户名下的所有商店
    public static final String SH_BUSINESSES = "businesses";
    //商户登录
    public static final String SH_LOGIN= "login";
    //获取消费等级划分
    public static final String SH_SENDING_TIERS= "spending-tiers";
    //确认发放积分
    public static final String SH_REWARD_POINTS= "reward/points";
    //获取coupon信息
    public static final String SH_COUPON= "coupon/";
    //确认接受coupon
    public static final String SH_REDEEM_COUPON= "redeem/coupon";
    //获取offer信息
    public static final String SH_OFFER= "offer/";
    //确认兑换Offer
    public static final String SH_REDEEM_OFFER= "redeem/offer";
    //获取历史交易记录
    public static final String SH_TRANSACTION_HISTORY= "transaction/history";
    //取消一个当天的交易
    public static final String SH_TRANSACTION_CANCEL= "transaction/cancel";
    //同步一项未完成交易
    public final static String SYNC_PURCHASES ="transaction/sync/purchases";


}

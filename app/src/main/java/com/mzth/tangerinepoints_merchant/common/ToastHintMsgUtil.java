package com.mzth.tangerinepoints_merchant.common;

/**
 * Created by Administrator on 2017/5/9.
 */

public class ToastHintMsgUtil {

    public static String getToastMsg(String msg){
        switch (msg){//打印错误信息
            case "MERCHANT_PIN_DEVICE_MISMATCH":
                return Constans.MERCHANT_PIN_DEVICE_MISMATCH;
            case "LOGIN_FAILURE":
                return Constans.LOGIN_FAILURE;
            case "SPENDING_TIERS_NOT_SPECIFIED":
                return  Constans.SPENDING_TIERS_NOT_SPECIFIED;
            case "POINTS_AMOUNT_EXCEED_LIMIT":
                return Constans.POINTS_AMOUNT_EXCEED_LIMIT;
            case "SUSPICIOUS_LOCATION":
                return Constans.SUSPICIOUS_LOCATION;
            case "COUPON_CODE_INVALID":
                return Constans.COUPON_CODE_INVALID;
            case "COUPON_CODE_EXPIRED":
                return Constans.COUPON_CODE_EXPIRED;
            case "COUPON_CODE_NOT_ACCEPTED_HERE":
                return Constans.COUPON_CODE_NOT_ACCEPTED_HERE;
            case "INVALID_USER_ID":
                return Constans.INVALID_USER_ID;
            case "INVALID_TXN_ID":
                return Constans.INVALID_TXN_ID;
            case "OFFER_NOT_FROM_HERE":
                return Constans.OFFER_NOT_FROM_HERE;
            case "OFFER_RETIRED":
                return Constans.OFFER_RETIRED;
            case "INSUFFICIENT_POINTS":
                return Constans.INSUFFICIENT_POINTS;
            case "INVALID_OFFER_ID":
                return Constans.INVALID_OFFER_ID;
            case "CANNOT_CANCEL_TXN":
                return Constans.CANNOT_CANCEL_TXN;
            default:
                return msg;
        }

    }


}

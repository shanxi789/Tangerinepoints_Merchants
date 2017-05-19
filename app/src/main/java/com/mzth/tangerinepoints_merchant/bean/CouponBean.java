package com.mzth.tangerinepoints_merchant.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Administrator on 2017/5/4.
 */

public class CouponBean implements Serializable {//优惠券对象
    private String couponId;//优惠券ID
    private String issuedBy;//商店网址
    private String customerId;//用户ID
    private long validTill;//时间
    private boolean redeemed;//是否可以兑换
    private int couponTermId;//券项ID
    private String couponTermTitle;//券项标题
    private String couponImgKey;//优惠券图片的key
    private String couponTermDetail;//优惠券图片的描述
    private int paybackValue;//回报价值
    private List<Object> limitByBusinessIds;//未知

    public String getCouponId() {
        return couponId;
    }

    public void setCouponId(String couponId) {
        this.couponId = couponId;
    }

    public String getIssuedBy() {
        return issuedBy;
    }

    public void setIssuedBy(String issuedBy) {
        this.issuedBy = issuedBy;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public long getValidTill() {
        return validTill;
    }

    public void setValidTill(long validTill) {
        this.validTill = validTill;
    }

    public boolean isRedeemed() {
        return redeemed;
    }

    public void setRedeemed(boolean redeemed) {
        this.redeemed = redeemed;
    }

    public int getCouponTermId() {
        return couponTermId;
    }

    public void setCouponTermId(int couponTermId) {
        this.couponTermId = couponTermId;
    }

    public String getCouponTermTitle() {
        return couponTermTitle;
    }

    public void setCouponTermTitle(String couponTermTitle) {
        this.couponTermTitle = couponTermTitle;
    }

    public String getCouponImgKey() {
        return couponImgKey;
    }

    public void setCouponImgKey(String couponImgKey) {
        this.couponImgKey = couponImgKey;
    }

    public String getCouponTermDetail() {
        return couponTermDetail;
    }

    public void setCouponTermDetail(String couponTermDetail) {
        this.couponTermDetail = couponTermDetail;
    }

    public int getPaybackValue() {
        return paybackValue;
    }

    public void setPaybackValue(int paybackValue) {
        this.paybackValue = paybackValue;
    }

    public List<Object> getLimitByBusinessIds() {
        return limitByBusinessIds;
    }

    public void setLimitByBusinessIds(List<Object> limitByBusinessIds) {
        this.limitByBusinessIds = limitByBusinessIds;
    }
}

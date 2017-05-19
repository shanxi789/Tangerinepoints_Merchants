package com.mzth.tangerinepoints_merchant.bean;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/5/12.
 * 发票信息
 */

public class InvoiceBean implements Serializable {
    private String txnType;//发票类型
    private String txnId;//交易ID
    private String item;//发票名称
    private long time;//发票时间
    private String businessName;//发票商店
    private String businessLocation;//发票地址

    public String getTxnType() {
        return txnType;
    }

    public void setTxnType(String txnType) {
        this.txnType = txnType;
    }

    public String getTxnId() {
        return txnId;
    }

    public void setTxnId(String txnId) {
        this.txnId = txnId;
    }

    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getBusinessName() {
        return businessName;
    }

    public void setBusinessName(String businessName) {
        this.businessName = businessName;
    }

    public String getBusinessLocation() {
        return businessLocation;
    }

    public void setBusinessLocation(String businessLocation) {
        this.businessLocation = businessLocation;
    }
}

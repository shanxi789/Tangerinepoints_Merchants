package com.mzth.tangerinepoints_merchant.bean;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/4/18.
 */

public class HistoryAllBean implements Serializable {
    private String txnId;//id
    private String title;//标题
    private String description;//描述
    private String customerScreenName;//商户昵称
    private long time;//时间
    private boolean canceled;//是否取消
    private long canceledAt;//取消id

    public String getTxnId() {
        return txnId;
    }

    public void setTxnId(String txnId) {
        this.txnId = txnId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCustomerScreenName() {
        return customerScreenName;
    }

    public void setCustomerScreenName(String customerScreenName) {
        this.customerScreenName = customerScreenName;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public boolean isCanceled() {
        return canceled;
    }

    public void setCanceled(boolean canceled) {
        this.canceled = canceled;
    }

    public long getCanceledAt() {
        return canceledAt;
    }

    public void setCanceledAt(long canceledAt) {
        this.canceledAt = canceledAt;
    }
}

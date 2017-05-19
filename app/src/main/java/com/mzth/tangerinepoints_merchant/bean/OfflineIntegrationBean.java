package com.mzth.tangerinepoints_merchant.bean;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/5/8.
 * 离线发放积分
 */

public class OfflineIntegrationBean implements Serializable {
    private String id;//UUID
    private String txn_type;//类型
    private String customer_id;//用户的ID
    private int points;//点数
    private double purchase_amount;//购买金额
    private long txn_time;//时间



    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTxn_type() {
        return txn_type;
    }

    public void setTxn_type(String txn_type) {
        this.txn_type = txn_type;
    }

    public String getCustomer_id() {
        return customer_id;
    }

    public void setCustomer_id(String customer_id) {
        this.customer_id = customer_id;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public double getPurchase_amount() {
        return purchase_amount;
    }

    public void setPurchase_amount(double purchase_amount) {
        this.purchase_amount = purchase_amount;
    }

    public long getTxn_time() {
        return txn_time;
    }

    public void setTxn_time(long txn_time) {
        this.txn_time = txn_time;
    }
}

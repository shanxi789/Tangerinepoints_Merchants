package com.mzth.tangerinepoints_merchant.bean.offer;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/4/28.
 */

public class LocationBean implements Serializable {//经纬度
    private double latitude;
    private double longitude;

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}

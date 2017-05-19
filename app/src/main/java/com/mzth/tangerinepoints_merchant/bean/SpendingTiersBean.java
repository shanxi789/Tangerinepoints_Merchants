package com.mzth.tangerinepoints_merchant.bean;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/4/18.
 * 点数梯度计算公式
 */

public class SpendingTiersBean implements Serializable {
    //
    private long validFrom;
    //
    private double tier1Min;
    //
    private double tier2Min;
    //
    private double tier3Min;

    public long getValidFrom() {
        return validFrom;
    }

    public void setValidFrom(long validFrom) {
        this.validFrom = validFrom;
    }

    public double getTier1Min() {
        return tier1Min;
    }

    public void setTier1Min(double tier1Min) {
        this.tier1Min = tier1Min;
    }

    public double getTier2Min() {
        return tier2Min;
    }

    public void setTier2Min(double tier2Min) {
        this.tier2Min = tier2Min;
    }

    public double getTier3Min() {
        return tier3Min;
    }

    public void setTier3Min(double tier3Min) {
        this.tier3Min = tier3Min;
    }
}

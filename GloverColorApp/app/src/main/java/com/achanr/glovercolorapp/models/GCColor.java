package com.achanr.glovercolorapp.models;

import com.achanr.glovercolorapp.utility.EGCColorEnum;
import com.achanr.glovercolorapp.utility.GCPowerLevelUtil;

import java.io.Serializable;

/**
 * @author Andrew Chanrasmi
 * @created 1/20/16 3:46 PM
 */
public class GCColor implements Serializable {

    private EGCColorEnum mColorEnum;
    private String mPowerLevel;

    public GCColor(EGCColorEnum colorEnum, String powerLevel) {
        mColorEnum = colorEnum;
        mPowerLevel = powerLevel;
    }

    public EGCColorEnum getColorEnum() {
        return mColorEnum;
    }

    public void setColorEnum(EGCColorEnum colorEnum) {
        mColorEnum = colorEnum;
    }

    public GCPowerLevel getPowerLevel() {
        return GCPowerLevelUtil.getPowerLevelUsingTitle(mPowerLevel);
    }

    public void setPowerLevel(String powerLevel) {
        mPowerLevel = powerLevel;
    }
}

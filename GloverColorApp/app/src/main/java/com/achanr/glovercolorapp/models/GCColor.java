package com.achanr.glovercolorapp.models;

import com.achanr.glovercolorapp.utility.EGCColorEnum;
import com.achanr.glovercolorapp.utility.EGCPowerLevelEnum;

import java.io.Serializable;

/**
 * @author Andrew Chanrasmi
 * @created 1/20/16 3:46 PM
 */
public class GCColor implements Serializable{

    private EGCColorEnum mColorEnum;
    private EGCPowerLevelEnum mPowerLevelEnum;

    public GCColor(EGCColorEnum colorEnum, EGCPowerLevelEnum powerLevelEnum) {
        mColorEnum = colorEnum;
        mPowerLevelEnum = powerLevelEnum;
    }

    public EGCColorEnum getColorEnum() {
        return mColorEnum;
    }

    public void setColorEnum(EGCColorEnum colorEnum) {
        mColorEnum = colorEnum;
    }

    public EGCPowerLevelEnum getPowerLevelEnum() {
        return mPowerLevelEnum;
    }

    public void setPowerLevelEnum(EGCPowerLevelEnum powerLevelEnum) {
        mPowerLevelEnum = powerLevelEnum;
    }
}

package com.achanr.glovercolorapp.models;

import com.achanr.glovercolorapp.utility.EGCColorEnum;

import java.io.Serializable;

/**
 * @author Andrew Chanrasmi
 * @created 1/20/16 3:46 PM
 */
public class GCColor implements Serializable{

    private EGCColorEnum mColorEnum;
    private GCPowerLevel mPowerLevel;

    public GCColor(EGCColorEnum colorEnum, GCPowerLevel powerLevel) {
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
        return mPowerLevel;
    }

    public void setPowerLevel(GCPowerLevel powerLevel) {
        mPowerLevel = powerLevel;
    }
}

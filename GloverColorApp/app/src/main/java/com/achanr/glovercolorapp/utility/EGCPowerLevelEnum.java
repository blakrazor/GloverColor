package com.achanr.glovercolorapp.utility;

/**
 * @author Andrew Chanrasmi
 * @created 1/20/16 3:43 PM
 */
public enum EGCPowerLevelEnum {
    HIGH("H", (float) 1.0),
    MEDIUM("M", (float) 0.7),
    LOW("L", (float) 0.4);

    EGCPowerLevelEnum(String powerAbbrev, float saturationValue){
        mPowerAbbrev = powerAbbrev;
        mSaturationValue = saturationValue;
    }

    private String mPowerAbbrev;
    private float mSaturationValue;

    public String getPowerAbbrev() {
        return mPowerAbbrev;
    }

    public float getSaturationValue() {
        return mSaturationValue;
    }
}

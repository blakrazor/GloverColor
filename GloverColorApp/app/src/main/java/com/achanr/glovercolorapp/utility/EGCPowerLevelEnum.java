package com.achanr.glovercolorapp.utility;

/**
 * @author Andrew Chanrasmi
 * @created 1/20/16 3:43 PM
 */
public enum EGCPowerLevelEnum {
    HIGH("H"),
    MEDIUM("M"),
    LOW("L");

    EGCPowerLevelEnum(String powerAbbrev){
        mPowerAbbrev = powerAbbrev;
    }

    private String mPowerAbbrev;

    public String getPowerAbbrev() {
        return mPowerAbbrev;
    }
}

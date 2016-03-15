package com.achanr.glovercolorapp.models;

import com.achanr.glovercolorapp.common.GCPowerLevelUtil;

import java.io.Serializable;

/**
 * @author Andrew Chanrasmi
 * @created 1/20/16 3:46 PM
 */
public class GCPoweredColor implements Serializable {

    private GCColor mColor;
    private String mPowerLevel;

    public GCPoweredColor(GCColor colorEnum, String powerLevel) {
        mColor = colorEnum;
        mPowerLevel = powerLevel;
    }

    public GCColor getColor() {
        return mColor;
    }

    public void setColor(GCColor color) {
        mColor = color;
    }

    public GCPowerLevel getPowerLevel() {
        return GCPowerLevelUtil.getPowerLevelUsingTitle(mPowerLevel);
    }

    public void setPowerLevel(String powerLevel) {
        mPowerLevel = powerLevel;
    }
}

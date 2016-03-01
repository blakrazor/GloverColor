package com.achanr.glovercolorapp.models;

import com.achanr.glovercolorapp.utility.GCConstants;

import java.io.Serializable;

/**
 * Glover Color App Project
 *
 * @author Andrew Chanrasmi
 * @created 3/1/16 10:48 AM
 */
public class GCPowerLevel implements Serializable {

    private String mTitle;
    private String mAbbreviation;
    private float mValue;

    public GCPowerLevel() {
        //default constructor
        mTitle = GCConstants.POWER_LEVEL_HIGH_TITLE;
        mAbbreviation = GCConstants.POWER_LEVEL_HIGH_ABBREV;
        mValue = GCConstants.POWER_LEVEL_HIGH_VALUE;
    }

    public GCPowerLevel(String title, String abbreviation, float value) {
        mTitle = title;
        mAbbreviation = abbreviation;
        mValue = value;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public void setAbbreviation(String abbreviation) {
        mAbbreviation = abbreviation;
    }

    public void setValue(float value) {
        mValue = value;
    }

    public String getAbbreviation() {
        return mAbbreviation;
    }

    public float getValue() {
        return mValue;
    }

    public int convertValueToInt() {
        return (int) (mValue * 100);
    }
}

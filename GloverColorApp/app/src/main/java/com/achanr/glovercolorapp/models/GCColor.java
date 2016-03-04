package com.achanr.glovercolorapp.models;

import java.io.Serializable;

/**
 * @author Andrew Chanrasmi
 * @created 3/3/16 2:08 PM
 */
public class GCColor implements Serializable {

    private String mTitle;
    private String mAbbreviation;
    private int[] mRGBValues;

    public GCColor(String title, String abbreviation, int[] RGBValues){
        mTitle = title;
        mAbbreviation = abbreviation;
        mRGBValues = RGBValues;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public String getAbbreviation() {
        return mAbbreviation;
    }

    public void setAbbreviation(String abbreviation) {
        mAbbreviation = abbreviation;
    }

    public int[] getRGBValues() {
        return mRGBValues;
    }

    public void setRGBValues(int[] RGBValues) {
        mRGBValues = RGBValues;
    }
}

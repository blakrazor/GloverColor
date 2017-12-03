package com.achanr.glovercolorapp.models;

import java.io.Serializable;
import java.util.Objects;

/**
 * @author Andrew Chanrasmi
 * @created 3/3/16 2:08 PM
 */
public class GCColor implements Serializable {

    private String mTitle;
    private String mAbbreviation;
    private int[] mRGBValues;

    public GCColor(String title, String abbreviation, int[] RGBValues) {
        mTitle = title;
        mAbbreviation = abbreviation;
        mRGBValues = RGBValues;
    }

    public static GCColor convertFromOnlineColor(GCOnlineColor onlineColor) {
        if (onlineColor != null
                && !"".equals(onlineColor.name)
                && (!"".equals(onlineColor.abbrev) || "NONE".equals(onlineColor.name))
                && !"".equals(onlineColor.rgb)) {
            String[] rgbParts = onlineColor.rgb.split(",");
            if (rgbParts.length == 3) {
                int[] rgbValues = new int[3];
                rgbValues[0] = Integer.parseInt(rgbParts[0]);
                rgbValues[1] = Integer.parseInt(rgbParts[1]);
                rgbValues[2] = Integer.parseInt(rgbParts[2]);
                return new GCColor(onlineColor.name, onlineColor.abbrev, rgbValues);
            }
        }
        return null;
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

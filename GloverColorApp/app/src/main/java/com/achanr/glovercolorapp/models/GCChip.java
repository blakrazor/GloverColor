package com.achanr.glovercolorapp.models;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * @author Andrew Chanrasmi
 * @created 3/3/16 3:32 PM
 */
public class GCChip implements Serializable {

    private String mTitle;
    private ArrayList<String> mColors;
    private ArrayList<String> mModes;

    public GCChip(String title, ArrayList<String> colors, ArrayList<String> modes) {
        mTitle = title;
        mColors = colors;
        mModes = modes;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public ArrayList<String> getColors() {
        return mColors;
    }

    public void setColors(ArrayList<String> colors) {
        mColors = colors;
    }

    public ArrayList<String> getModes() {
        return mModes;
    }

    public void setModes(ArrayList<String> modes) {
        mModes = modes;
    }
}

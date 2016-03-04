package com.achanr.glovercolorapp.models;

import com.achanr.glovercolorapp.utility.GCUtil;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Glover Color App Project
 *
 * @author Andrew Chanrasmi
 */
public class GCSavedSet implements Serializable {

    private String mTitle;
    private ArrayList<GCPoweredColor> mColors;
    private GCMode mMode;
    private GCChip mChipSet;

    public String getTitle() {
        return mTitle = mTitle.replace("_", " ");
    }

    public void setTitle(String title) {
        mTitle = title.replace(" ", "_");
    }

    public ArrayList<GCPoweredColor> getColors() {
        return mColors;
    }

    public void setColors(ArrayList<GCPoweredColor> colors) {
        mColors = colors;
    }

    public GCMode getMode() {
        return mMode;
    }

    public void setMode(GCMode mode) {
        mMode = mode;
    }

    public GCChip getChipSet() {
        return mChipSet;
    }

    public void setChipSet(GCChip chipSet) {
        mChipSet = chipSet;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof GCSavedSet)) {
            return false;
        }
        GCSavedSet other = (GCSavedSet) o;
        return mTitle.equalsIgnoreCase(other.getTitle());
    }

    @Override
    public int hashCode() {
        return mTitle.hashCode();
    }

    @Override
    public String toString() {
        return "Title:" + mTitle + " Colors:" + GCUtil.convertColorListToShortenedColorString(mColors) + " Mode:" + mMode;
    }
}

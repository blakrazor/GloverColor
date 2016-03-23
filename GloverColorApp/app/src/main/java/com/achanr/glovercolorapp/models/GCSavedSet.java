package com.achanr.glovercolorapp.models;

import com.achanr.glovercolorapp.common.GCConstants;
import com.achanr.glovercolorapp.common.GCUtil;

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
    private ArrayList<int[]> mCustomColors;

    public GCSavedSet() {
        initializeCustomColorArray();
    }

    private void initializeCustomColorArray() {
        mCustomColors = new ArrayList<>();
        for (int i = 0; i < GCConstants.MAX_COLORS; i++) {
            mCustomColors.add(new int[]{255, 255, 255});
        }
    }

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

    public ArrayList<int[]> getCustomColors() {
        if (mCustomColors == null || mCustomColors.isEmpty()) {
            initializeCustomColorArray();
        } else if (mCustomColors.size() < GCConstants.MAX_COLORS){
            int difference = GCConstants.MAX_COLORS - mCustomColors.size();
            for (int i = 0; i < difference; i++) {
                mCustomColors.add(new int[]{255, 255, 255});
            }
        }
        return mCustomColors;
    }

    public void setCustomColors(ArrayList<int[]> customColors) {
        mCustomColors = customColors;
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

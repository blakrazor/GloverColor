package com.achanr.glovercolorapp.models;

import android.content.Context;

import com.achanr.glovercolorapp.common.GCChipUtil;
import com.achanr.glovercolorapp.common.GCConstants;
import com.achanr.glovercolorapp.common.GCModeUtil;
import com.achanr.glovercolorapp.common.GCUtil;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Glover Color App Project
 *
 * @author Andrew Chanrasmi
 */
public class GCSavedSet implements Serializable {

    private int mId = -1;
    private String mTitle;
    private String mDescription;
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

    public int getId() {
        return mId;
    }

    public void setId(int id) {
        this.mId = id;
    }

    public String getTitle() {
        return mTitle = mTitle.replace("_", " ");
    }

    public void setTitle(String title) {
        mTitle = title.replace(" ", "_");
    }

    public String getDescription() {
        return mDescription != null ? mDescription : "";
    }

    public void setDescription(String description) {
        mDescription = description;
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
        } else if (mCustomColors.size() < GCConstants.MAX_COLORS) {
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

    public static GCSavedSet convertToSavedSet(Context context, GCOnlineDBSavedSet dbSavedSet) {
        GCSavedSet savedSet = new GCSavedSet();
        savedSet.setId(dbSavedSet.getId());
        savedSet.setTitle(dbSavedSet.getTitle());
        savedSet.setDescription(dbSavedSet.getDescription());
        savedSet.setColors(GCUtil.convertShortenedColorStringToColorList(dbSavedSet.getColors()));
        savedSet.setMode(GCModeUtil.getModeUsingTitle(context, dbSavedSet.getMode().toUpperCase()));
        if (dbSavedSet.getCustom_colors() != null) {
            savedSet.setCustomColors(GCUtil.convertStringToCustomColorArray(dbSavedSet.getCustom_colors()));
        } else {
            ArrayList<int[]> customColors = new ArrayList<>();
            for (int i = 0; i < GCConstants.MAX_COLORS; i++) {
                customColors.add(new int[]{255, 255, 255});
            }
            savedSet.setCustomColors(customColors);
        }
        if (dbSavedSet.getChip() != null) {
            savedSet.setChipSet(GCChipUtil.getChipUsingTitle(dbSavedSet.getChip().toUpperCase()));
        } else {
            savedSet.setChipSet(GCChipUtil.getChipUsingTitle("NONE"));
        }
        return savedSet;
    }
}

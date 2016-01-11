package com.achanr.glovercolorapp.models;

import com.achanr.glovercolorapp.utility.GloverColorApplication;
import com.achanr.glovercolorapp.utility.GCColorEnum;
import com.achanr.glovercolorapp.utility.GCModeEnum;
import com.achanr.glovercolorapp.utility.GCUtil;

import java.util.ArrayList;

/**
 * Glover Color App Project
 *
 * @author Andrew Chanrasmi
 */
public class GCSavedSetDataModel {

    private String mTitle;
    private ArrayList<GCColorEnum> mColors;
    private GCModeEnum mMode;

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public ArrayList<GCColorEnum> getColors() {
        return mColors;
    }

    public void setColors(ArrayList<GCColorEnum> colors) {
        mColors = colors;
    }

    public GCModeEnum getMode() {
        return mMode;
    }

    public void setMode(GCModeEnum mode) {
        mMode = mode;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof GCSavedSetDataModel)) {
            return false;
        }
        GCSavedSetDataModel other = (GCSavedSetDataModel) o;
        return mTitle.equalsIgnoreCase(other.getTitle());
    }

    @Override
    public int hashCode() {
        return mTitle.hashCode();
    }

    @Override
    public String toString() {
        return "Title:" + mTitle + " Colors:" + GCUtil.convertColorListToShortenedColorString(GloverColorApplication.getContext(), mColors) + " Mode:" + mMode;
    }
}

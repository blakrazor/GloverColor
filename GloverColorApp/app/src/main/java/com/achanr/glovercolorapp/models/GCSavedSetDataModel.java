package com.achanr.glovercolorapp.models;

import com.achanr.glovercolorapp.utility.EGCColorEnum;
import com.achanr.glovercolorapp.utility.EGCModeEnum;
import com.achanr.glovercolorapp.utility.GCUtil;
import com.achanr.glovercolorapp.utility.GloverColorApplication;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Glover Color App Project
 *
 * @author Andrew Chanrasmi
 */
public class GCSavedSetDataModel implements Serializable {

    private String mTitle;
    private ArrayList<EGCColorEnum> mColors;
    private EGCModeEnum mMode;

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public ArrayList<EGCColorEnum> getColors() {
        return mColors;
    }

    public void setColors(ArrayList<EGCColorEnum> colors) {
        mColors = colors;
    }

    public EGCModeEnum getMode() {
        return mMode;
    }

    public void setMode(EGCModeEnum mode) {
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

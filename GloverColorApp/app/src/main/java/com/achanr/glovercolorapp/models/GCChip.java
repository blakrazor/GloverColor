package com.achanr.glovercolorapp.models;

import com.achanr.glovercolorapp.common.GCUtil;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Map;

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

    public static GCChip convertFromOnlineChip(GCOnlineDefaultChip onlineChip) {
        if (onlineChip != null
                && !"".equals(onlineChip.name)
                && onlineChip.colors != null
                && onlineChip.modes != null) {
            ArrayList<String> modes = new ArrayList<>(GCUtil.removeUnderscoresFromList(onlineChip.modes));
            ArrayList<String> colors = new ArrayList<>(GCUtil.removeUnderscoresFromList(onlineChip.colors));
            return new GCChip(onlineChip.name, colors, modes);
        }
        return null;
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

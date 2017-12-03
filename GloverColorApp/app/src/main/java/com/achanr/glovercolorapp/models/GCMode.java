package com.achanr.glovercolorapp.models;

import java.io.Serializable;

/**
 * @author Andrew Chanrasmi
 * @created 3/3/16 4:54 PM
 */
public class GCMode implements Serializable {

    private String mTitle;

    public GCMode(String title) {
        mTitle = title;
    }

    public static GCMode convertFromOnlineMode(GCOnlineMode onlineMode) {
        if (onlineMode != null && !"".equals(onlineMode.name)) {
            return new GCMode(onlineMode.name);
        }
        return null;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }
}

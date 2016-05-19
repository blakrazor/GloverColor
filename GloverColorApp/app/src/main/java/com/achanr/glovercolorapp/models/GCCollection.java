package com.achanr.glovercolorapp.models;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Glover Color App Project
 *
 * @author Andrew Chanrasmi
 * @created 4/29/16 2:59 PM
 */
public class GCCollection implements Serializable {

    private String mTitle;
    private String mDescription;
    private ArrayList<GCSavedSet> mSavedSetList;

    public GCCollection() {

    }

    public GCCollection(String title, String description, ArrayList<GCSavedSet> savedSetList) {
        mTitle = title;
        mDescription = description;
        mSavedSetList = savedSetList;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public String getDescription() {
        return mDescription;
    }

    public void setDescription(String description) {
        mDescription = description;
    }

    public ArrayList<GCSavedSet> getSavedSetList() {
        return mSavedSetList;
    }

    public void setSavedSetList(ArrayList<GCSavedSet> savedSetList) {
        mSavedSetList = savedSetList;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof GCCollection)) {
            return false;
        }
        GCCollection other = (GCCollection) o;
        return mTitle.equalsIgnoreCase(other.getTitle());
    }
}

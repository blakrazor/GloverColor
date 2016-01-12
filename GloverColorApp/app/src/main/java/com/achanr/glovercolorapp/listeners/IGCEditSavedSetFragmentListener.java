package com.achanr.glovercolorapp.listeners;

import com.achanr.glovercolorapp.models.GCSavedSetDataModel;

/**
 * Glover Color App Project
 *
 * @author Andrew Chanrasmi
 * @created 1/11/16 3:41 PM
 */
public interface IGCEditSavedSetFragmentListener {
    void onSetSaved(GCSavedSetDataModel oldSet, GCSavedSetDataModel newSet);
    void onSetDeleted(GCSavedSetDataModel savedSet, boolean isNewSet);
    void onSetAdded(GCSavedSetDataModel newSet);
}

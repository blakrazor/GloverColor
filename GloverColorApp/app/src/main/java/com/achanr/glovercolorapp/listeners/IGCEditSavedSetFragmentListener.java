package com.achanr.glovercolorapp.listeners;

import com.achanr.glovercolorapp.models.GCSavedSet;

/**
 * Glover Color App Project
 *
 * @author Andrew Chanrasmi
 * @created 1/11/16 3:41 PM
 */
public interface IGCEditSavedSetFragmentListener {
    void onSetSaved(GCSavedSet oldSet, GCSavedSet newSet);
    void onSetDeleted(GCSavedSet savedSet, boolean isNewSet);
    void onSetAdded(GCSavedSet newSet);
    void onLeaveConfirmed();
}

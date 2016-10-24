package com.achanr.glovercolorapp.common;

import android.content.Context;
import android.widget.Toast;

import com.achanr.glovercolorapp.database.GCDatabaseHelper;
import com.achanr.glovercolorapp.database.GCSavedSetDatabase;
import com.achanr.glovercolorapp.models.GCRealtimeDBSavedSet;
import com.achanr.glovercolorapp.models.GCSavedSet;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Andrew Chanrasmi on 10/24/16
 */

public enum GCRealtimeDatabaseUtil {
    INSTANCE;

    private DatabaseReference currentReference;

    private final String USER_SAVED_SET_KEY = "user_saved_sets";

    private DatabaseReference getCurrentDatabaseReference() {
        if (currentReference == null) {
            currentReference = FirebaseDatabase.getInstance().getReference();
        }
        return currentReference;
    }

    public void syncToOnline(Context context) {
        if (GCAuthUtil.isCurrentUserLoggedIn()) {
            FirebaseUser user = GCAuthUtil.getCurrentUser();
            List<GCSavedSet> savedSets = GCDatabaseHelper.getInstance(context).SAVED_SET_DATABASE.getAllData();
            List<GCRealtimeDBSavedSet> dbSavedSets = new ArrayList<>();
            for (GCSavedSet savedSet : savedSets) {
                dbSavedSets.add(GCSavedSetDatabase.convertSavedSetToRealtimeDBSavedSet(savedSet));
            }
            getCurrentDatabaseReference()
                    .child(USER_SAVED_SET_KEY)
                    .child(user.getUid())
                    .setValue(dbSavedSets);
        } else {
            Toast.makeText(context, "Please login first to save data online.", Toast.LENGTH_SHORT).show();
        }
    }
}

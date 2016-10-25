package com.achanr.glovercolorapp.common;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.achanr.glovercolorapp.database.GCDatabaseHelper;
import com.achanr.glovercolorapp.models.GCOnlineDBSavedSet;
import com.achanr.glovercolorapp.models.GCSavedSet;
import com.achanr.glovercolorapp.ui.activities.GCBaseActivity;
import com.achanr.glovercolorapp.ui.activities.GCSyncConflictActivity;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Andrew Chanrasmi on 10/24/16
 */

public class GCOnlineDatabaseUtil {

    public interface CompletionHandler {
        void onComplete();
    }

    public static final int SYNC_CONFLICT_REQUEST_CODE = 7001;

    private static DatabaseReference currentReference;

    private static ProgressDialog progressDialog;

    private static final String USER_SAVED_SET_KEY = "user_saved_sets";

    private static DatabaseReference getCurrentDatabaseReference() {
        if (currentReference == null) {
            currentReference = FirebaseDatabase.getInstance().getReference();
        }
        return currentReference;
    }

    public static void syncToOnline(Context context, CompletionHandler handler) {
        if (GCAuthUtil.isCurrentUserLoggedIn()) {
            showProgressDialog(context);
            FirebaseUser user = GCAuthUtil.getCurrentUser();
            syncSavedSets(context, user.getUid(), handler);
        } else {
            Toast.makeText(context, "Please login first to save data online.", Toast.LENGTH_SHORT).show();
        }
    }

    private static void syncSavedSets(final Context context, final String userUID, final CompletionHandler handler) {

        final List<GCSavedSet> savedSets = GCDatabaseHelper.getInstance(context).SAVED_SET_DATABASE.getAllData();
        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //Get all the online sets
                List<GCOnlineDBSavedSet> dbSavedSets = new ArrayList<>();
                for (DataSnapshot userSavedSets : dataSnapshot.getChildren()) {
                    dbSavedSets.add(userSavedSets.getValue(GCOnlineDBSavedSet.class));
                }
                //Compare the local and only sets
                Quadruple<List<GCOnlineDBSavedSet>, Boolean, Boolean, Boolean> comparison = compareSavedSetLists(dbSavedSets, savedSets);
                if (comparison.getA().isEmpty()) {
                    Toast.makeText(context, "Data is already synced.", Toast.LENGTH_SHORT).show();
                    dismissProgressDialog();
                    handler.onComplete();
                    return;
                }
                if (comparison.getB() || comparison.getC()) {
                    //If true, difference in data only exists on one side
                    if (comparison.getD()) {
                        //if true, online database had more records, sync online to local
                        for (GCOnlineDBSavedSet onlineSavedSet : comparison.getA()) {
                            GCDatabaseHelper.getInstance(context).SAVED_SET_DATABASE.insertData(onlineSavedSet);
                        }
                    }
                    reSynchronizeWithOnline(context, userUID);
                    syncCollections(context, userUID, handler);
                } else {
                    //else, multiple differences exist
                    dismissProgressDialog();
                    Intent intent = new Intent(context, GCSyncConflictActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable(GCSyncConflictActivity.CONFLICT_SETS_KEY, (Serializable) comparison.getA());
                    intent.putExtra(GCSyncConflictActivity.BUNDLE_KEY, bundle);
                    ((GCBaseActivity) context).startActivityForResult(intent, SYNC_CONFLICT_REQUEST_CODE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(GCOnlineDatabaseUtil.class.getSimpleName(), "loadSavedSets:onCancelled", databaseError.toException());
                Toast.makeText(context, "Something went wrong Try again later.", Toast.LENGTH_SHORT).show();
            }
        };
        getCurrentDatabaseReference()
                .child(USER_SAVED_SET_KEY)
                .child(userUID)
                .addListenerForSingleValueEvent(postListener);
    }

    private static Quadruple<List<GCOnlineDBSavedSet>, Boolean, Boolean, Boolean> compareSavedSetLists(List<GCOnlineDBSavedSet> dbSavedSets, List<GCSavedSet> savedSetsLocal) {
        List<GCOnlineDBSavedSet> dbSavedSetsLocal = new ArrayList<>();
        List<GCOnlineDBSavedSet> dbSavedSetsOnline = new ArrayList<>(dbSavedSets);
        for (GCSavedSet savedSet : savedSetsLocal) {
            dbSavedSetsLocal.add(convertSavedSetToOnlineDBSavedSet(savedSet));
        }
        List<GCOnlineDBSavedSet> intersection = new ArrayList<>();
        dbSavedSetsOnline.removeAll(dbSavedSetsLocal);
        dbSavedSetsLocal.removeAll(dbSavedSets);
        intersection.addAll(dbSavedSetsLocal);
        intersection.addAll(dbSavedSetsOnline);
        return new Quadruple<>(
                intersection,
                dbSavedSetsOnline.isEmpty(),
                dbSavedSetsLocal.isEmpty(),
                dbSavedSetsOnline.size() > dbSavedSetsLocal.size());
    }

    public static void reSynchronizeWithOnline(Context context, String userUID) {
        //then sync local to online
        List<GCOnlineDBSavedSet> localSavedSets = new ArrayList<>();
        for (GCSavedSet savedSet : GCDatabaseHelper.getInstance(context).SAVED_SET_DATABASE.getAllData()) {
            localSavedSets.add(convertSavedSetToOnlineDBSavedSet(savedSet));
        }
        getCurrentDatabaseReference()
                .child(USER_SAVED_SET_KEY)
                .child(userUID)
                .setValue(localSavedSets);
    }

    public static void syncCollections(Context context, String userUID, CompletionHandler handler) {
        //TODO: complete this later
        Toast.makeText(context, "Data has been synced to your account.", Toast.LENGTH_SHORT).show();
        dismissProgressDialog();
        handler.onComplete();
    }

    private static GCOnlineDBSavedSet convertSavedSetToOnlineDBSavedSet(GCSavedSet savedSet) {
        GCOnlineDBSavedSet dbSavedSet = new GCOnlineDBSavedSet();
        dbSavedSet.setId(savedSet.getId());
        dbSavedSet.setTitle(savedSet.getTitle());
        dbSavedSet.setColors(GCUtil.convertColorListToShortenedColorString(savedSet.getColors()));
        dbSavedSet.setMode(savedSet.getMode().getTitle());
        dbSavedSet.setChip(savedSet.getChipSet().getTitle());
        dbSavedSet.setCustom_colors(GCUtil.convertCustomColorArrayToString(savedSet.getCustomColors()));
        return dbSavedSet;
    }

    public static GCSavedSet convertToSavedSet(Context context, GCOnlineDBSavedSet dbSavedSet) {
        GCSavedSet savedSet = new GCSavedSet();
        savedSet.setId(dbSavedSet.getId());
        savedSet.setTitle(dbSavedSet.getTitle());
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

    private static void showProgressDialog(Context context) {
        if (progressDialog == null || !progressDialog.isShowing()) {
            progressDialog = ProgressDialog.show(context, "Online Sync",
                    "Syncing...Please Wait...", true);
        }
    }

    private static void dismissProgressDialog() {
        if (progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }
}

package com.achanr.glovercolorapp.common;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.achanr.glovercolorapp.R;
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
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * @author Andrew Chanrasmi on 10/24/16
 */

public class GCOnlineDatabaseUtil {

    public interface OnCompletionHandler {
        void onComplete();
    }

    public enum SyncStatus {
        Unavailable,
        Synced,
        OutOfSync
    }

    public static final int SYNC_CONFLICT_REQUEST_CODE = 7001;
    public static SyncStatus CurrentSyncStatus = SyncStatus.Unavailable;

    private static DatabaseReference currentReference;

    private static ProgressDialog progressDialog;

    private static final String USER_SAVED_SET_KEY = "user_saved_sets";

    public static void initialize() {
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        if (currentReference == null) {
            currentReference = FirebaseDatabase.getInstance().getReference();
        }
    }

    private static DatabaseReference getCurrentDatabaseReference() {
        if (currentReference == null) {
            currentReference = FirebaseDatabase.getInstance().getReference();
        }
        return currentReference;
    }

    public static void syncToOnline(Context context, OnCompletionHandler handler) {
        if (GCAuthUtil.isCurrentUserLoggedIn()) {
            showProgressDialog(context, context.getString(R.string.online_sync), context.getString(R.string.syncing_wait_message));
            FirebaseUser user = GCAuthUtil.getCurrentUser();
            syncSavedSets(context, user.getUid(), handler);
        } else {
            Toast.makeText(context, R.string.please_login_first, Toast.LENGTH_SHORT).show();
        }
    }

    private static void syncSavedSets(final Context context, final String userUID, final OnCompletionHandler handler) {

        final List<GCSavedSet> savedSets = GCDatabaseHelper.getInstance(context).SAVED_SET_DATABASE.getAllData();
        DatabaseReference connection = getCurrentDatabaseReference()
                .child(USER_SAVED_SET_KEY)
                .child(userUID);
        connection.keepSynced(true);
        connection.addListenerForSingleValueEvent(new ValueEventListener() {
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
                    syncCompleted(context, handler);
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
                dismissProgressDialog();
                Log.w(GCOnlineDatabaseUtil.class.getSimpleName(), "loadSavedSets:onCancelled", databaseError.toException());
                Toast.makeText(context, R.string.something_went_wrong, Toast.LENGTH_SHORT).show();
                CurrentSyncStatus = SyncStatus.Unavailable;
                if (handler != null) handler.onComplete();
            }
        });
    }

    private static Quadruple<List<GCOnlineDBSavedSet>, Boolean, Boolean, Boolean> compareSavedSetLists(List<GCOnlineDBSavedSet> dbSavedSets, List<GCSavedSet> savedSetsLocal) {
        List<GCOnlineDBSavedSet> dbSavedSetsLocal = new ArrayList<>();
        List<GCOnlineDBSavedSet> dbSavedSetsOnline = new ArrayList<>(dbSavedSets);
        for (GCSavedSet savedSet : savedSetsLocal) {
            dbSavedSetsLocal.add(convertToOnlineDBSavedSet(savedSet));
        }
        List<GCOnlineDBSavedSet> intersection = new ArrayList<>();
        dbSavedSetsOnline.removeAll(dbSavedSetsLocal);
        dbSavedSetsLocal.removeAll(dbSavedSets);
        intersection.addAll(dbSavedSetsLocal);
        intersection.addAll(dbSavedSetsOnline);
        checkForTitleDuplicates(intersection);
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
            localSavedSets.add(convertToOnlineDBSavedSet(savedSet));
        }
        getCurrentDatabaseReference()
                .child(USER_SAVED_SET_KEY)
                .child(userUID)
                .setValue(localSavedSets);
    }

    public static void syncCollections(Context context, String userUID, OnCompletionHandler handler) {
        //TODO: complete this later
        syncCompleted(context, handler);
    }

    private static void syncCompleted(Context context, OnCompletionHandler handler) {
        CurrentSyncStatus = SyncStatus.Synced;
        Toast.makeText(context, R.string.data_synced_complete_message, Toast.LENGTH_SHORT).show();
        dismissProgressDialog();
        if (handler != null) handler.onComplete();
    }

    private static GCOnlineDBSavedSet convertToOnlineDBSavedSet(GCSavedSet savedSet) {
        GCOnlineDBSavedSet dbSavedSet = new GCOnlineDBSavedSet();
        dbSavedSet.setId(savedSet.getId());
        dbSavedSet.setTitle(savedSet.getTitle());
        dbSavedSet.setDescription(savedSet.getDescription());
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

    private static void showProgressDialog(Context context, String title, String message) {
        if (progressDialog == null || !progressDialog.isShowing()) {
            progressDialog = ProgressDialog.show(context, title, message, true);
        }
    }

    private static void dismissProgressDialog() {
        if (progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    public static void addToOnlineDB(Context context, GCSavedSet savedSet) {
        if (GCAuthUtil.isCurrentUserLoggedIn() && savedSet != null) {
            showProgressDialog(context, context.getString(R.string.online_sync), context.getString(R.string.syncing_wait_message));
            String key = getCurrentDatabaseReference()
                    .child(USER_SAVED_SET_KEY)
                    .child(GCAuthUtil.getCurrentUser().getUid())
                    .push()
                    .getKey();
            getCurrentDatabaseReference()
                    .child(USER_SAVED_SET_KEY)
                    .child(GCAuthUtil.getCurrentUser().getUid())
                    .child(key)
                    .setValue(convertToOnlineDBSavedSet(savedSet));
            dismissProgressDialog();
        }
    }

    public static void updateToOnlineDB(final Context context, final GCSavedSet savedSet) {
        if (GCAuthUtil.isCurrentUserLoggedIn() && savedSet != null) {
            showProgressDialog(context, context.getString(R.string.online_sync), context.getString(R.string.syncing_wait_message));
            getCurrentDatabaseReference()
                    .child(USER_SAVED_SET_KEY)
                    .child(GCAuthUtil.getCurrentUser().getUid())
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            String key = null;
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                GCOnlineDBSavedSet dbSavedSet = snapshot.getValue(GCOnlineDBSavedSet.class);
                                if (dbSavedSet.getId() == savedSet.getId()) {
                                    key = snapshot.getKey();
                                    break;
                                }
                            }
                            if (key != null) {
                                getCurrentDatabaseReference()
                                        .child(USER_SAVED_SET_KEY)
                                        .child(GCAuthUtil.getCurrentUser().getUid())
                                        .child(key)
                                        .setValue(convertToOnlineDBSavedSet(savedSet));
                            }
                            dismissProgressDialog();
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            dismissProgressDialog();
                            Log.w(GCOnlineDatabaseUtil.class.getSimpleName(), "updateSet:onCancelled", databaseError.toException());
                            Toast.makeText(context, R.string.something_went_wrong, Toast.LENGTH_SHORT).show();
                            CurrentSyncStatus = SyncStatus.Unavailable;
                        }
                    });
        }
    }

    public static void deleteFromOnlineDB(final Context context, final int id) {
        if (GCAuthUtil.isCurrentUserLoggedIn() && id >= 0) {
            showProgressDialog(context, context.getString(R.string.online_sync), context.getString(R.string.syncing_wait_message));
            getCurrentDatabaseReference()
                    .child(USER_SAVED_SET_KEY)
                    .child(GCAuthUtil.getCurrentUser().getUid())
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            String key = null;
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                GCOnlineDBSavedSet dbSavedSet = snapshot.getValue(GCOnlineDBSavedSet.class);
                                if (dbSavedSet.getId() == id) {
                                    key = snapshot.getKey();
                                    break;
                                }
                            }
                            if (key != null) {
                                getCurrentDatabaseReference()
                                        .child(USER_SAVED_SET_KEY)
                                        .child(GCAuthUtil.getCurrentUser().getUid())
                                        .child(key)
                                        .removeValue();
                            }
                            dismissProgressDialog();
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            dismissProgressDialog();
                            Log.w(GCOnlineDatabaseUtil.class.getSimpleName(), "deleteSet:onCancelled", databaseError.toException());
                            Toast.makeText(context, R.string.something_went_wrong, Toast.LENGTH_SHORT).show();
                            CurrentSyncStatus = SyncStatus.Unavailable;
                        }
                    });
        }
    }

    public static void checkSyncStatus(final Context context, final OnCompletionHandler handler) {
        if (GCAuthUtil.isCurrentUserLoggedIn()) {
            if (handler != null) {
                showProgressDialog(context, context.getString(R.string.online_sync), context.getString(R.string.checking_sync_status_message));
            }
            final List<GCSavedSet> savedSets = GCDatabaseHelper.getInstance(context).SAVED_SET_DATABASE.getAllData();
            getCurrentDatabaseReference()
                    .child(USER_SAVED_SET_KEY)
                    .child(GCAuthUtil.getCurrentUser().getUid())
                    .addListenerForSingleValueEvent(new ValueEventListener() {
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
                                CurrentSyncStatus = SyncStatus.Synced;
                            } else {
                                CurrentSyncStatus = SyncStatus.OutOfSync;
                            }
                            if (handler != null) {
                                dismissProgressDialog();
                                handler.onComplete();
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Log.w(GCOnlineDatabaseUtil.class.getSimpleName(), "checkSyncStatus:onCancelled", databaseError.toException());
                            Toast.makeText(context, R.string.something_went_wrong, Toast.LENGTH_SHORT).show();
                            CurrentSyncStatus = SyncStatus.Unavailable;
                            if (handler != null) {
                                dismissProgressDialog();
                                handler.onComplete();
                            }
                        }
                    });
        }
    }

    private static void checkForTitleDuplicates(List<GCOnlineDBSavedSet> mOnlineDBSavedSets) {
        Comparator<GCOnlineDBSavedSet> comparator = new Comparator<GCOnlineDBSavedSet>() {
            @Override
            public int compare(GCOnlineDBSavedSet o1, GCOnlineDBSavedSet o2) {
                return o1.getTitle().compareTo(o2.getTitle());
            }
        };

        Set<GCOnlineDBSavedSet> hashSet = new TreeSet<>(comparator);
        for (GCOnlineDBSavedSet onlineDBSavedSet : mOnlineDBSavedSets) {
            if (!hashSet.add(onlineDBSavedSet)) {
                onlineDBSavedSet.setTitle(onlineDBSavedSet.getTitle() + " copy");
            }
        }
    }
}

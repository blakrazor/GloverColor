package com.achanr.glovercolorapp.common;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.achanr.glovercolorapp.R;
import com.achanr.glovercolorapp.application.GloverColorApplication;
import com.achanr.glovercolorapp.database.GCDatabaseHelper;
import com.achanr.glovercolorapp.models.GCMode;
import com.achanr.glovercolorapp.models.GCOnlineColor;
import com.achanr.glovercolorapp.models.GCOnlineDBSavedSet;
import com.achanr.glovercolorapp.models.GCOnlineDefaultChip;
import com.achanr.glovercolorapp.models.GCOnlineMode;
import com.achanr.glovercolorapp.models.GCSavedSet;
import com.achanr.glovercolorapp.models.GCUser;
import com.achanr.glovercolorapp.ui.activities.GCBaseActivity;
import com.achanr.glovercolorapp.ui.activities.GCSyncConflictActivity;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import static com.achanr.glovercolorapp.models.GCOnlineDBSavedSet.convertToOnlineDBSavedSet;

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
    private static final String USERS_KEY = "users";
    private static final String CHIP_DATABASE = "chip_database";
    private static final String CHIP_DATABASE_VERSION = "database_version";
    private static final String COLOR_DATABASE = "color_database";
    private static final String MODE_DATABASE = "mode_database";
    private static final String DEFAULT_CHIP_DATABASE = "default_chips";
    private static final String DISCOVER_KEY = "discover";

    private static boolean defaultChipsSynced;
    private static boolean defaultModesSynced;
    private static boolean defaultColorsSynced;

    public static void initialize() {
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        if (currentReference == null) {
            currentReference = FirebaseDatabase.getInstance().getReference();
        }
        GCOnlineDatabaseUtil.saveUserOnline();
    }

    private static DatabaseReference getCurrentDatabaseReference() {
        if (currentReference == null) {
            currentReference = FirebaseDatabase.getInstance().getReference();
        }
        return currentReference;
    }

    public static void saveUserOnline() {
        if (GCAuthUtil.isCurrentUserLoggedIn()) {
            FirebaseUser currentUser = GCAuthUtil.getCurrentUser();
            if (currentUser != null) {
                GCUser user = GCUser.convertFromFirebaseUser(currentUser);
                getCurrentDatabaseReference()
                        .child(USERS_KEY)
                        .child(currentUser.getUid())
                        .setValue(user);
            }
        }
    }

    public static void syncToOnline(Activity context, OnCompletionHandler handler) {
        if (GCAuthUtil.isCurrentUserLoggedIn()) {
            showProgressDialog(context, context.getString(R.string.online_sync), context.getString(R.string.syncing_wait_message));
            FirebaseUser user = GCAuthUtil.getCurrentUser();
            syncSavedSets(context, user.getUid(), handler);
        } else {
            Toast.makeText(context, R.string.please_login_first, Toast.LENGTH_SHORT).show();
        }
    }

    private static void syncSavedSets(final Activity context, final String userUID, final OnCompletionHandler handler) {

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
                    dismissProgressDialog(context);
                    Intent intent = new Intent(context, GCSyncConflictActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable(GCSyncConflictActivity.CONFLICT_SETS_KEY, (Serializable) comparison.getA());
                    intent.putExtra(GCSyncConflictActivity.BUNDLE_KEY, bundle);
                    context.startActivityForResult(intent, SYNC_CONFLICT_REQUEST_CODE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                dismissProgressDialog(context);
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

    public static void syncCollections(Activity context, String userUID, OnCompletionHandler handler) {
        //TODO: complete this later
        syncCompleted(context, handler);
    }

    private static void syncCompleted(Activity context, OnCompletionHandler handler) {
        CurrentSyncStatus = SyncStatus.Synced;
        Toast.makeText(context, R.string.data_synced_complete_message, Toast.LENGTH_SHORT).show();
        dismissProgressDialog(context);
        if (handler != null) handler.onComplete();
    }

    private static void showProgressDialog(final Activity activity, final String title, final String message) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (progressDialog == null || !progressDialog.isShowing()) {
                    progressDialog = ProgressDialog.show(activity, title, message, true);
                }
            }
        });
    }

    private static void dismissProgressDialog(Activity activity) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
            }
        });
    }

    public static void addToOnlineDB(Activity context, GCSavedSet savedSet) {
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
            dismissProgressDialog(context);
        }
    }

    public static void updateToOnlineDB(final Activity context, final GCSavedSet savedSet) {
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
                                        .setValue(GCOnlineDBSavedSet.convertToOnlineDBSavedSet(savedSet));
                            }
                            dismissProgressDialog(context);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            dismissProgressDialog(context);
                            Log.w(GCOnlineDatabaseUtil.class.getSimpleName(), "updateSet:onCancelled", databaseError.toException());
                            Toast.makeText(context, R.string.something_went_wrong, Toast.LENGTH_SHORT).show();
                            CurrentSyncStatus = SyncStatus.Unavailable;
                        }
                    });
        }
    }

    public static void deleteFromOnlineDB(final Activity context, final int id) {
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
                            dismissProgressDialog(context);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            dismissProgressDialog(context);
                            Log.w(GCOnlineDatabaseUtil.class.getSimpleName(), "deleteSet:onCancelled", databaseError.toException());
                            Toast.makeText(context, R.string.something_went_wrong, Toast.LENGTH_SHORT).show();
                            CurrentSyncStatus = SyncStatus.Unavailable;
                        }
                    });
        }
    }

    public static void checkSyncStatus(final Activity context, final OnCompletionHandler handler) {
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
                                dismissProgressDialog(context);
                                handler.onComplete();
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Log.w(GCOnlineDatabaseUtil.class.getSimpleName(), "checkSyncStatus:onCancelled", databaseError.toException());
                            Toast.makeText(context, R.string.something_went_wrong, Toast.LENGTH_SHORT).show();
                            CurrentSyncStatus = SyncStatus.Unavailable;
                            if (handler != null) {
                                dismissProgressDialog(context);
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

    public static void removeUploadedSets(Context context, String userId, OnCompletionHandler onCompletionHandler) {
        Query connection = getCurrentDatabaseReference()
                .child(DISCOVER_KEY)
                .orderByChild("userUid")
                .equalTo(userId);
        connection.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public static void syncWithOnlineDatabase(final Activity context) {
        if (isNetworkAvailable(context)) {
            showProgressDialog(context, context.getString(R.string.online_sync), context.getString(R.string.syncing_wait_message));
            // check online database version
            final DatabaseReference connection = getCurrentDatabaseReference()
                    .child(CHIP_DATABASE);
            connection.keepSynced(true);
            connection.child(CHIP_DATABASE_VERSION).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    long onlineDatabaseVersion = dataSnapshot.getValue(long.class);
                    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
                    long storedOnlineDatabaseVersion = prefs.getLong(CHIP_DATABASE_VERSION, 0);
                    // check if database version greater than stored version
                    if (onlineDatabaseVersion > storedOnlineDatabaseVersion) {
                        defaultChipsSynced = false;
                        defaultColorsSynced = false;
                        defaultModesSynced = false;

                        //if so, update all databases
                        syncOnlineColorDatabase(context, connection);
                        syncOnlineModeDatabase(context, connection);
                        syncOnlineDefaultChipDatabase(context, connection);

                        // save the new database version
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putLong(CHIP_DATABASE_VERSION, onlineDatabaseVersion);
                        editor.apply();
                    } else {
                        dismissProgressDialog(context);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    dismissProgressDialog(context);
                    Log.w(GCOnlineDatabaseUtil.class.getSimpleName(), "syncWithOnlineDatabase:onCancelled", databaseError.toException());
                    Toast.makeText(context, R.string.something_went_wrong, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private static void syncOnlineColorDatabase(final Activity context, DatabaseReference connection) {
        connection.child(COLOR_DATABASE).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<GCOnlineColor> onlineColors = new ArrayList<>();
                for (DataSnapshot colorSnapshot : dataSnapshot.getChildren()) {
                    onlineColors.add(colorSnapshot.getValue(GCOnlineColor.class));
                }
                if (onlineColors.size() > 0) {
                    GCColorUtil.syncOnlineColorDatabase(context, onlineColors);
                }
                defaultColorsSynced = true;
                checkFinishedSyncing(context);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                defaultColorsSynced = true;
                checkFinishedSyncing(context);
                Log.w(GCOnlineDatabaseUtil.class.getSimpleName(), "syncOnlineColorDatabase:onCancelled", databaseError.toException());
            }
        });
    }

    private static void syncOnlineModeDatabase(final Activity context, DatabaseReference connection) {
        connection.child(MODE_DATABASE).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<GCOnlineMode> onlineModes = new ArrayList<>();
                for (DataSnapshot modeSnapshot : dataSnapshot.getChildren()) {
                    onlineModes.add(modeSnapshot.getValue(GCOnlineMode.class));
                }
                if (onlineModes.size() > 0) {
                    GCModeUtil.syncOnlineModeDatabase(context, onlineModes);
                }
                defaultModesSynced = true;
                checkFinishedSyncing(context);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                defaultModesSynced = true;
                checkFinishedSyncing(context);
                Log.w(GCOnlineDatabaseUtil.class.getSimpleName(), "syncOnlineModeDatabase:onCancelled", databaseError.toException());
            }
        });
    }

    private static void syncOnlineDefaultChipDatabase(final Activity context, DatabaseReference connection) {
        connection.child(DEFAULT_CHIP_DATABASE).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<GCOnlineDefaultChip> onlineChips = new ArrayList<>();
                for (DataSnapshot chipSnapshot : dataSnapshot.getChildren()) {
                    onlineChips.add(chipSnapshot.getValue(GCOnlineDefaultChip.class));
                }
                if (onlineChips.size() > 0) {
                    GCChipUtil.syncOnlineDefaultChipDatabase(context, onlineChips);
                }
                defaultChipsSynced = true;
                checkFinishedSyncing(context);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                defaultChipsSynced = true;
                checkFinishedSyncing(context);
                Log.w(GCOnlineDatabaseUtil.class.getSimpleName(), "syncOnlineDefaultChipDatabase:onCancelled", databaseError.toException());
            }
        });
    }

    private static void checkFinishedSyncing(Activity context) {
        if (defaultChipsSynced && defaultColorsSynced && defaultModesSynced) {
            dismissProgressDialog(context);
        }
    }

    private static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}

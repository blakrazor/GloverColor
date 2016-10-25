package com.achanr.glovercolorapp.ui.activities;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.achanr.glovercolorapp.R;
import com.achanr.glovercolorapp.common.CustomItemAnimator;
import com.achanr.glovercolorapp.common.GCAuthUtil;
import com.achanr.glovercolorapp.common.GCOnlineDatabaseUtil;
import com.achanr.glovercolorapp.database.GCDatabaseHelper;
import com.achanr.glovercolorapp.models.GCOnlineDBSavedSet;
import com.achanr.glovercolorapp.models.GCSavedSet;
import com.achanr.glovercolorapp.ui.adapters.GCSyncConflictsAdapter;
import com.achanr.glovercolorapp.ui.views.GridRecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * @author Andrew Chanrasmi on 10/25/16
 */

public class GCSyncConflictActivity extends GCBaseActivity {

    private List<GCOnlineDBSavedSet> mOnlineDBSavedSets;
    public static final String BUNDLE_KEY = "bundle_key";
    public static final String CONFLICT_SETS_KEY = "conflict_sets_key";

    private GCSyncConflictsAdapter mAdapter;

    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLayoutInflater().inflate(R.layout.activity_sync_conflicts, mFrameLayout);
        setupToolbar(getString(R.string.title_sync_conflicts));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        Intent intent = getIntent();
        if (intent != null && intent.getExtras() != null) {
            mOnlineDBSavedSets = (List<GCOnlineDBSavedSet>) intent.getBundleExtra(BUNDLE_KEY).getSerializable(CONFLICT_SETS_KEY);
            setupSavedSetList();
            if (mOnlineDBSavedSets == null) {
                onBackPressed();
            }
        } else {
            onBackPressed();
        }
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_CANCELED);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, 1, 1, "Done").setIcon(R.drawable.ic_done_white_48dp)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 1: //revert changes
                showConfirmationAlert();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void showConfirmationAlert() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.confirm_selection)
                .setMessage(String.format(
                        Locale.getDefault(),
                        getString(R.string.confirm_selection_body),
                        mAdapter.getSelectedSets() != null ? mAdapter.getSelectedSets().size() : 0))
                .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        resolveConflicts();
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })
                .setIcon(R.drawable.ic_warning_black_48dp)
                .show();
    }

    private void setupSavedSetList() {
        GridRecyclerView mRecyclerView = (GridRecyclerView) findViewById(R.id.sync_conflicts_recyclerview);
        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        GridLayoutManager mLayoutManager = new GridLayoutManager(this, 1);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setItemAnimator(new CustomItemAnimator());
        mAdapter = new GCSyncConflictsAdapter(this, mOnlineDBSavedSets);
        mRecyclerView.setAdapter(mAdapter);
    }

    private void resolveConflicts() {
        mProgressDialog = ProgressDialog.show(this, getString(R.string.sync_conflicts),
                getString(R.string.resolving_conflicts_message), true);

        //Get the selected positions
        List<Integer> selectedPositions = mAdapter.getSelectedSets();

        if (selectedPositions != null && selectedPositions.size() > 0) {
            //Use selected positions to get the selected sets and convert to GCSavedSets
            //Also get unselected sets
            List<GCSavedSet> unselectedSets = new ArrayList<>();
            List<GCSavedSet> selectedSets = new ArrayList<>();
            for (int x = 0; x < mOnlineDBSavedSets.size(); x++) {
                GCSavedSet set = GCOnlineDatabaseUtil.convertToSavedSet(this, mOnlineDBSavedSets.get(x));
                if (selectedPositions.contains(x)) {
                    selectedSets.add(set);
                } else {
                    unselectedSets.add(set);
                }
            }

            //And remove all unselected local sets
            for (GCSavedSet unselectedSet : unselectedSets) {
                GCDatabaseHelper.getInstance(this).SAVED_SET_DATABASE.deleteData(unselectedSet);
            }

            //Get all local sets and remove any copies of local sets from selected sets
            List<GCSavedSet> localSets = GCDatabaseHelper.getInstance(this).SAVED_SET_DATABASE.getAllData();
            selectedSets.removeAll(localSets);

            //Add remaining selected sets which is just online conflicts
            for (GCSavedSet selectedSet : selectedSets) {
                GCDatabaseHelper.getInstance(this).SAVED_SET_DATABASE.insertData(selectedSet);
            }
        }

        GCOnlineDatabaseUtil.reSynchronizeWithOnline(this, GCAuthUtil.getCurrentUser().getUid());
        GCOnlineDatabaseUtil.syncCollections(this, GCAuthUtil.getCurrentUser().getUid(), new GCOnlineDatabaseUtil.CompletionHandler() {
            @Override
            public void onComplete() {
                mProgressDialog.dismiss();
                setResult(RESULT_OK);
                finish();
            }
        });
    }
}

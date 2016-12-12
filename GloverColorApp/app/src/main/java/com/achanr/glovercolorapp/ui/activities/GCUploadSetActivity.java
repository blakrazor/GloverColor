package com.achanr.glovercolorapp.ui.activities;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.achanr.glovercolorapp.R;
import com.achanr.glovercolorapp.common.CustomItemAnimator;
import com.achanr.glovercolorapp.common.GCConstants;
import com.achanr.glovercolorapp.database.GCDatabaseHelper;
import com.achanr.glovercolorapp.models.GCOnlineDBSavedSet;
import com.achanr.glovercolorapp.models.GCSavedSet;
import com.achanr.glovercolorapp.ui.adapters.GCMultiSelectorAdapter;
import com.achanr.glovercolorapp.ui.viewHolders.GCUploadSetViewHolder;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * @author Andrew Chanrasmi on 11/21/16
 */

public class GCUploadSetActivity extends GCBaseActivity {

    private List<GCOnlineDBSavedSet> mLocalSavedSets;
    private GCMultiSelectorAdapter mAdapter;
    private GCUploadSetViewHolder mViewHolder;
    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupToolbar(getString(R.string.title_upload_set));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        retrieveLocalSets();
        setupSavedSetList();
    }

    @Override
    protected void setupContentLayout() {
        View view = getLayoutInflater().inflate(R.layout.activity_upload_set, mFrameLayout);
        mViewHolder = new GCUploadSetViewHolder(view);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
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
                verifySetsSelected();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void verifySetsSelected() {
        int countSelected = mAdapter.getSelectedSets() != null ? mAdapter.getSelectedSets().size() : 0;
        if (countSelected <= 0) {
            showSelectAtLeastOneSetAlert();
        } else if (countSelected > 0 && countSelected <= GCConstants.MAX_UPLOAD_COUNT) {
            showConfirmationAlert(countSelected);
        } else { //countSelected is greater than max upload count
            showTooManySetsAlert(countSelected);
        }
    }

    private void showSelectAtLeastOneSetAlert() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.confirm_selection)
                .setMessage(R.string.select_one_set)
                .setNeutralButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setIcon(R.drawable.ic_warning_black_48dp)
                .show();
    }

    private void showConfirmationAlert(int count) {
        new AlertDialog.Builder(this)
                .setTitle(R.string.confirm_selection)
                .setMessage(String.format(
                        Locale.getDefault(),
                        getString(R.string.you_selected_to_upload),
                        count))
                .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        uploadSetsOnline();
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

    private void showTooManySetsAlert(int count) {
        new AlertDialog.Builder(this)
                .setTitle(R.string.confirm_selection)
                .setMessage(String.format(
                        Locale.getDefault(),
                        getString(R.string.you_selected_too_many),
                        count,
                        GCConstants.MAX_UPLOAD_COUNT))
                .setNeutralButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setIcon(R.drawable.ic_warning_black_48dp)
                .show();
    }

    private void retrieveLocalSets() {
        mLocalSavedSets = new ArrayList<>();
        ArrayList<GCSavedSet> savedSets = GCDatabaseHelper.getInstance(this).SAVED_SET_DATABASE.getAllData();
        for (GCSavedSet savedSet : savedSets) {
            mLocalSavedSets.add(GCOnlineDBSavedSet.convertToOnlineDBSavedSet(savedSet));
        }
    }

    private void setupSavedSetList() {
        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mViewHolder.getGridRecyclerView().setHasFixedSize(true);

        // use a linear layout manager
        GridLayoutManager mLayoutManager = new GridLayoutManager(this, 1);
        mViewHolder.getGridRecyclerView().setLayoutManager(mLayoutManager);
        mViewHolder.getGridRecyclerView().setItemAnimator(new CustomItemAnimator());
        mAdapter = new GCMultiSelectorAdapter(this, mLocalSavedSets);
        mViewHolder.getGridRecyclerView().setAdapter(mAdapter);
    }

    private void uploadSetsOnline() {
        mProgressDialog = ProgressDialog.show(this, getString(R.string.upload),
                getString(R.string.uploading_sets_please_wait), true);

        //Get the selected positions
        List<Integer> selectedPositions = mAdapter.getSelectedSets();

        //TODO: Remove all old uploaded sets if any

        //TODO: Add new uploaded sets


        mProgressDialog.dismiss();

        //TODO: show success and navigate away
    }
}

package com.achanr.glovercolorapp.activities;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.achanr.glovercolorapp.R;
import com.achanr.glovercolorapp.database.GCSavedSetDatabase;
import com.achanr.glovercolorapp.fragments.GCEditSavedSetFragment;
import com.achanr.glovercolorapp.fragments.GCSavedSetListFragment;
import com.achanr.glovercolorapp.listeners.IGCEditSavedSetFragmentListener;
import com.achanr.glovercolorapp.listeners.IGCSavedSetListFragmentListener;
import com.achanr.glovercolorapp.models.GCSavedSetDataModel;

import java.util.ArrayList;

/**
 * Glover Color App Project
 *
 * @author Andrew Chanrasmi
 */

public class GCSavedSetListActivity extends GCBaseActivity implements IGCSavedSetListFragmentListener, IGCEditSavedSetFragmentListener {

    private Context mContext;
    private GCSavedSetDatabase mSavedSetDatabase;
    private ArrayList<GCSavedSetDataModel> mSavedSetList;
    private GCSavedSetListFragment mSavedSetListFragment;

    private enum TransactionEnum {ADD, REPLACE}

    ;

    private FragmentManager mFragmentManager;

    private boolean isBackButton = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLayoutInflater().inflate(R.layout.activity_saved_set_list, mFrameLayout);
        mContext = this;
        setupToolbar(getString(R.string.title_your_saved_sets));

        mSavedSetDatabase = new GCSavedSetDatabase(mContext);
        mSavedSetList = getSavedSetListFromDatabase();

        mFragmentManager = getSupportFragmentManager();
        mSavedSetListFragment = GCSavedSetListFragment.newInstance(mSavedSetList);
        doFragmentTransaction(mSavedSetListFragment, TransactionEnum.ADD);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setPosition(R.id.nav_saved_color_sets);
        refreshSavedSetList();
    }

    private ArrayList<GCSavedSetDataModel> getSavedSetListFromDatabase() {
        ArrayList<GCSavedSetDataModel> savedSetList = mSavedSetDatabase.readData();
        if (mSavedSetList == null || mSavedSetList.size() <= 0) {
            mSavedSetList = new ArrayList<>();
        }
        return savedSetList;
    }

    private void doFragmentTransaction(Fragment fragment, TransactionEnum transaction) {
        FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
        switch (transaction) {
            case ADD:
                fragmentTransaction.add(R.id.saved_set_list_fragment_container, fragment);
                break;
            case REPLACE:
                fragmentTransaction.replace(R.id.saved_set_list_fragment_container, fragment);
                break;
        }
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();

        if (fragment instanceof GCEditSavedSetFragment) {
            displayBackButton(true);
        }
    }

    @Override
    public void onSavedSetListItemClicked(int position) {
        GCSavedSetDataModel savedSet = mSavedSetList.get(position);
        GCEditSavedSetFragment editSavedSetFragment = GCEditSavedSetFragment.newInstance(savedSet, false);
        doFragmentTransaction(editSavedSetFragment, TransactionEnum.REPLACE);
    }

    @Override
    public void onAddSetListItemClicked() {
        GCEditSavedSetFragment editSavedSetFragment = GCEditSavedSetFragment.newInstance(null, true);
        doFragmentTransaction(editSavedSetFragment, TransactionEnum.REPLACE);
    }

    @Override
    public void onSetSaved(GCSavedSetDataModel oldSet, GCSavedSetDataModel newSet) {
        mSavedSetDatabase.updateData(oldSet, newSet);
        popBackStackAndRefreshWithMessage("Your set has been updated.");
    }

    @Override
    public void onSetDeleted(GCSavedSetDataModel savedSet, boolean isNewSet) {
        if (!isNewSet) {
            mSavedSetDatabase.deleteData(savedSet);
        }
        popBackStackAndRefreshWithMessage("Your set has been deleted.");
    }

    @Override
    public void onSetAdded(GCSavedSetDataModel newSet) {
        mSavedSetDatabase.insertData(newSet);
        popBackStackAndRefreshWithMessage("Your set has been added.");
    }

    private void popBackStackAndRefreshWithMessage(String message) {
        mSavedSetList = mSavedSetDatabase.readData();
        mFragmentManager.popBackStack();
        displayBackButton(false);
        refreshSavedSetList();
        Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
    }

    public void refreshSavedSetList() {
        mSavedSetList = getSavedSetListFromDatabase();
        mSavedSetListFragment.refreshList(mSavedSetList);
    }

    private void displayBackButton(boolean shouldDisplay) {
        if (shouldDisplay) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            isBackButton = true;
            mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isBackButton) {
                        onBackPressed();
                    }
                }
            });
        } else {
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            isBackButton = false;
            setupToolbar(getString(R.string.title_your_saved_sets));
        }
    }

    @Override
    public void onBackPressed() {
        displayBackButton(false);
        super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                super.onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}

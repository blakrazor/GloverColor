package com.achanr.glovercolorapp.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.achanr.glovercolorapp.R;
import com.achanr.glovercolorapp.database.GCSavedSetDatabase;
import com.achanr.glovercolorapp.fragments.GCEditSavedSetFragment;
import com.achanr.glovercolorapp.fragments.GCSavedSetListFragment;
import com.achanr.glovercolorapp.listeners.IGCEditSavedSetFragmentListener;
import com.achanr.glovercolorapp.listeners.IGCSavedSetListFragmentListener;
import com.achanr.glovercolorapp.models.GCSavedSet;

import java.util.ArrayList;

/**
 * Glover Color App Project
 *
 * @author Andrew Chanrasmi
 */

public class GCSavedSetListActivity extends GCBaseActivity implements IGCSavedSetListFragmentListener, IGCEditSavedSetFragmentListener {

    private Context mContext;
    private GCSavedSetDatabase mSavedSetDatabase;
    private ArrayList<GCSavedSet> mSavedSetList;
    private GCSavedSetListFragment mSavedSetListFragment;
    private GCEditSavedSetFragment mEditSavedSetFragment;

    private enum TransactionEnum {ADD, REPLACE};

    private boolean isNewSet = false;

    private FragmentManager mFragmentManager;

    private boolean isBackButton = false;

    public static final String FROM_NAVIGATION = "from_navigation";
    public static final String NEW_SET_KEY = "new_set_key";
    private String mFromNavigation;
    private GCSavedSet mNewSet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLayoutInflater().inflate(R.layout.activity_saved_set_list, mFrameLayout);
        mContext = this;
        setupToolbar(getString(R.string.title_your_saved_sets));

        Intent intent = getIntent();
        if(intent != null){
            mFromNavigation = intent.getStringExtra(FROM_NAVIGATION);
            mNewSet = (GCSavedSet) intent.getSerializableExtra(NEW_SET_KEY);
        }

        mSavedSetDatabase = new GCSavedSetDatabase(mContext);
        mSavedSetList = getSavedSetListFromDatabase();

        mFragmentManager = getSupportFragmentManager();
        mSavedSetListFragment = GCSavedSetListFragment.newInstance(mSavedSetList);
        doFragmentTransaction(mSavedSetListFragment, TransactionEnum.ADD);

        if(mFromNavigation != null && mFromNavigation.equalsIgnoreCase(GCEnterCodeActivity.class.getName())) {
            mEditSavedSetFragment = GCEditSavedSetFragment.newInstance(mNewSet, true);
            doFragmentTransaction(mEditSavedSetFragment, TransactionEnum.REPLACE);
            getSupportActionBar().setTitle(R.string.title_add_set);
            isNewSet = true;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        setPosition(R.id.nav_saved_color_sets);
        refreshSavedSetList();
    }

    private ArrayList<GCSavedSet> getSavedSetListFromDatabase() {
        ArrayList<GCSavedSet> savedSetList = mSavedSetDatabase.readData();
        if (mSavedSetList == null || mSavedSetList.size() <= 0) {
            mSavedSetList = new ArrayList<>();
        }
        return savedSetList;
    }

    private void doFragmentTransaction(Fragment fragment, TransactionEnum transaction) {
        FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out, android.R.anim.slide_in_left, android.R.anim.slide_out_right);
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
        GCSavedSet savedSet = mSavedSetList.get(position);
        mEditSavedSetFragment = GCEditSavedSetFragment.newInstance(savedSet, false);
        doFragmentTransaction(mEditSavedSetFragment, TransactionEnum.REPLACE);
        getSupportActionBar().setTitle(R.string.title_edit_set);
    }

    @Override
    public void onAddSetListItemClicked() {
        mEditSavedSetFragment = GCEditSavedSetFragment.newInstance(null, true);
        doFragmentTransaction(mEditSavedSetFragment, TransactionEnum.REPLACE);
        getSupportActionBar().setTitle(R.string.title_add_set);
        isNewSet = true;
    }

    @Override
    public void onSetSaved(GCSavedSet oldSet, GCSavedSet newSet) {
        mSavedSetDatabase.updateData(oldSet, newSet);
        popBackStackAndRefreshWithMessage(getString(R.string.set_updated_message));
    }

    @Override
    public void onSetDeleted(GCSavedSet savedSet, boolean isNewSet) {
        if (!isNewSet) {
            mSavedSetDatabase.deleteData(savedSet);
        }
        popBackStackAndRefreshWithMessage(getString(R.string.set_deleted_message));
    }

    @Override
    public void onSetAdded(GCSavedSet newSet) {
        mSavedSetDatabase.insertData(newSet);
        popBackStackAndRefreshWithMessage(getString(R.string.set_added_message));
    }

    @Override
    public void onLeaveConfirmed() {
        popBackStackAndRefreshWithMessage("");
    }

    private void popBackStackAndRefreshWithMessage(String message) {
        mSavedSetList = mSavedSetDatabase.readData();
        mFragmentManager.popBackStack();
        displayBackButton(false);
        refreshSavedSetList();
        if (!message.isEmpty()) {
            Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
        }
    }

    public void refreshSavedSetList() {
        mSavedSetList = getSavedSetListFromDatabase();
        mSavedSetListFragment.refreshList(mSavedSetList);
    }

    private void displayBackButton(boolean shouldDisplay) {
        if (shouldDisplay) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            mToolbar.findViewById(R.id.theme_spinner).setVisibility(View.GONE);
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
            mToolbar.findViewById(R.id.theme_spinner).setVisibility(View.VISIBLE);
            isBackButton = false;
            isNewSet = false;
            setupToolbar(getString(R.string.title_your_saved_sets));
            // Check if no view has focus:
            View view = this.getCurrentFocus();
            if (view != null) {
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (isBackButton) {
            if(!isNewSet) {
                if (mEditSavedSetFragment.validateFields()) {
                    if (mEditSavedSetFragment.madeChanges()) {
                        mEditSavedSetFragment.showLeavingDialog();
                    } else {
                        displayBackButton(false);
                        super.onBackPressed();
                    }
                }
            } else {
                mEditSavedSetFragment.showLeavingDialog();
            }
        } else {
            displayBackButton(false);
            super.onBackPressed();
        }
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

package com.achanr.glovercolorapp.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import com.achanr.glovercolorapp.R;
import com.achanr.glovercolorapp.adapters.GCSavedSetListAdapter;
import com.achanr.glovercolorapp.database.GCSavedSetDatabase;
import com.achanr.glovercolorapp.models.GCSavedSet;

import java.util.ArrayList;

/**
 * Glover Color App Project
 *
 * @author Andrew Chanrasmi
 */

public class GCSavedSetListActivity extends GCBaseActivity {

    private Context mContext;
    private GCSavedSetDatabase mSavedSetDatabase;
    private ArrayList<GCSavedSet> mSavedSetList;
    private RecyclerView mSavedSetListRecyclerView;
    private GCSavedSetListAdapter mSavedSetListAdapter;
    private RecyclerView.LayoutManager mSavedSetListLayoutManager;
    private FloatingActionButton mFab;

    public static final String FROM_NAVIGATION = "from_navigation";
    public static final String NEW_SET_KEY = "new_set_key";
    public static final String OLD_SET_KEY = "old_set_key";
    public static final String IS_DELETE_KEY = "is_delete_key";

    public static final int ADD_NEW_SET_REQUEST_CODE = 1;
    public static final int UPDATE_SET_REQUEST_CODE = 2;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ADD_NEW_SET_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            boolean isDeleted = data.getBooleanExtra(IS_DELETE_KEY, false);
            if (!isDeleted) {
                GCSavedSet newSet = (GCSavedSet) data.getSerializableExtra(NEW_SET_KEY);
                onSetAdded(newSet);
            }
        } else if (requestCode == UPDATE_SET_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            boolean isDeleted = data.getBooleanExtra(IS_DELETE_KEY, false);
            if (isDeleted) {
                GCSavedSet savedSet = (GCSavedSet) data.getSerializableExtra(OLD_SET_KEY);
                onSetDeleted(savedSet);
            } else {
                GCSavedSet oldSet = (GCSavedSet) data.getSerializableExtra(OLD_SET_KEY);
                GCSavedSet newSet = (GCSavedSet) data.getSerializableExtra(NEW_SET_KEY);
                onSetUpdated(oldSet, newSet);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLayoutInflater().inflate(R.layout.activity_saved_set_list, mFrameLayout);
        mContext = this;
        setupToolbar(getString(R.string.title_your_saved_sets));

        mSavedSetDatabase = new GCSavedSetDatabase(mContext);
        mSavedSetList = getSavedSetListFromDatabase();

        if (mSavedSetList != null && mSavedSetList.size() > 0) {
            findViewById(R.id.icon_background).setVisibility(View.GONE);
        }

        mSavedSetListRecyclerView = (RecyclerView) findViewById(R.id.saved_set_list_recyclerview);
        mFab = (FloatingActionButton) findViewById(R.id.fab);
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onAddSetListItemClicked();
            }
        });

        setupSavedSetList();

        Intent intent = getIntent();
        if (intent != null) {
            String fromNavigation = intent.getStringExtra(FROM_NAVIGATION);
            if (fromNavigation != null) {
                if (fromNavigation.equalsIgnoreCase(GCEnterCodeActivity.class.getName())) {
                    GCSavedSet newSet = (GCSavedSet) intent.getSerializableExtra(NEW_SET_KEY);
                    Intent newIntent = new Intent(mContext, GCEditSavedSetActivity.class);
                    newIntent.putExtra(GCEditSavedSetActivity.IS_NEW_SET_KEY, true);
                    newIntent.putExtra(GCEditSavedSetActivity.SAVED_SET_KEY, newSet);
                    startActivityForResult(newIntent, ADD_NEW_SET_REQUEST_CODE);
                    overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                }
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        setPosition(R.id.nav_saved_color_sets);
    }

    private ArrayList<GCSavedSet> getSavedSetListFromDatabase() {
        ArrayList<GCSavedSet> savedSetList = mSavedSetDatabase.readData();
        if (mSavedSetList == null || mSavedSetList.size() <= 0) {
            mSavedSetList = new ArrayList<>();
        }
        return savedSetList;
    }

    private void setupSavedSetList() {
        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mSavedSetListRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mSavedSetListLayoutManager = new LinearLayoutManager(mContext);
        mSavedSetListRecyclerView.setLayoutManager(mSavedSetListLayoutManager);
        mSavedSetListAdapter = new GCSavedSetListAdapter(mContext, mSavedSetList);
        mSavedSetListRecyclerView.setAdapter(mSavedSetListAdapter);

        /*mSavedSetListRecyclerView.addOnItemTouchListener(
                new GCSavedSetListItemClickListener(mContext,
                        new GCSavedSetListItemClickListener.OnSavedSetItemClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {
                                if (mListener != null) {
                                    //mListener.onSavedSetListItemClicked(position);
                                }
                            }
                        })
        );*/
    }

    public void onSavedSetListItemClicked(GCSavedSet savedSet) {
        Intent intent = new Intent(mContext, GCEditSavedSetActivity.class);
        intent.putExtra(GCEditSavedSetActivity.SAVED_SET_KEY, savedSet);
        startActivityForResult(intent, UPDATE_SET_REQUEST_CODE);
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
    }

    public void onAddSetListItemClicked() {
        Intent intent = new Intent(mContext, GCEditSavedSetActivity.class);
        intent.putExtra(GCEditSavedSetActivity.IS_NEW_SET_KEY, true);
        startActivityForResult(intent, ADD_NEW_SET_REQUEST_CODE);
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
    }

    public void onSetUpdated(GCSavedSet oldSet, GCSavedSet newSet) {
        mSavedSetDatabase.updateData(oldSet, newSet);
        mSavedSetList = getSavedSetListFromDatabase();
        mSavedSetListAdapter.update(oldSet, newSet);
        Toast.makeText(mContext, getString(R.string.set_updated_message), Toast.LENGTH_SHORT);
    }

    public void onSetDeleted(GCSavedSet savedSet) {
        mSavedSetDatabase.deleteData(savedSet);
        mSavedSetList = getSavedSetListFromDatabase();
        mSavedSetListAdapter.remove(savedSet);
        Toast.makeText(mContext, getString(R.string.set_deleted_message), Toast.LENGTH_SHORT);
    }

    public void onSetAdded(GCSavedSet newSet) {
        mSavedSetDatabase.insertData(newSet);
        mSavedSetList = getSavedSetListFromDatabase();
        mSavedSetListAdapter.add(mSavedSetList.indexOf(newSet), newSet);
        Toast.makeText(mContext, getString(R.string.set_added_message), Toast.LENGTH_SHORT);
    }
}

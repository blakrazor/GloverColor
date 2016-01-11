package com.achanr.glovercolorapp.activities;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.achanr.glovercolorapp.R;
import com.achanr.glovercolorapp.adapters.GCSavedSetListAdapter;
import com.achanr.glovercolorapp.database.GCSavedSetDatabase;
import com.achanr.glovercolorapp.models.GCSavedSetDataModel;
import com.achanr.glovercolorapp.utility.GCColorEnum;
import com.achanr.glovercolorapp.utility.GCModeEnum;

import java.util.ArrayList;

/**
 * Glover Color App Project
 *
 * @author Andrew Chanrasmi
 */

public class GCSavedSetListActivity extends GCBaseActivity implements View.OnClickListener {

    private ArrayList<GCSavedSetDataModel> mSavedSetList;
    private RecyclerView mSavedSetListRecyclerView;
    private GCSavedSetListAdapter mSavedSetListAdapter;
    private RecyclerView.LayoutManager mSavedSetListLayoutManager;
    private GCSavedSetDatabase mSavedSetDatabase;
    private FloatingActionButton mFab;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLayoutInflater().inflate(R.layout.activity_saved_set_list, mFrameLayout);
        mContext = this;
        mSavedSetListRecyclerView = (RecyclerView) findViewById(R.id.saved_set_list_recyclerview);
        setupToolbar(getString(R.string.title_your_saved_sets));

        mFab = (FloatingActionButton) findViewById(R.id.fab);
        mFab.setOnClickListener(this);

        setupSavedSetList();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setPosition(R.id.nav_saved_color_sets);
    }

    private void setupSavedSetList() {
        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mSavedSetListRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mSavedSetListLayoutManager = new LinearLayoutManager(this);
        mSavedSetListRecyclerView.setLayoutManager(mSavedSetListLayoutManager);

        //Read list from database
        mSavedSetDatabase = new GCSavedSetDatabase(mContext);
        mSavedSetList = mSavedSetDatabase.readData();
        if (mSavedSetList == null || mSavedSetList.size() <= 0) {
            mSavedSetList = new ArrayList<>();
        }

        mSavedSetListAdapter = new GCSavedSetListAdapter(mContext, mSavedSetList);
        mSavedSetListRecyclerView.setAdapter(mSavedSetListAdapter);
    }

    @Override
    public void onClick(View v) {
        if(v == mFab){
            addSavedSet();
        }
    }

    private void addSavedSet(){
        GCSavedSetDataModel savedSet = new GCSavedSetDataModel();
        savedSet.setTitle("Test Title");
        ArrayList<GCColorEnum> colorList = new ArrayList<>();
        colorList.add(GCColorEnum.BLUE);
        colorList.add(GCColorEnum.GREEN);
        colorList.add(GCColorEnum.RED);
        savedSet.setColors(colorList);
        savedSet.setMode(GCModeEnum.STROBE);
        mSavedSetListAdapter.add(0, savedSet);
        mSavedSetDatabase.insertData(savedSet);
    }
}

package com.achanr.glovercolorapp.ui.activities;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.achanr.glovercolorapp.R;

import java.util.Arrays;
import java.util.Collections;

/**
 * Copyright (c) 2014 Miami HEAT. All rights reserved
 *
 * @author SapientNitro
 */
public class GCVersionInfoActivity extends GCBaseActivity {

    public static final String CURRENT_VERSION = "current_version";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLayoutInflater().inflate(R.layout.activity_version_info, mFrameLayout);
        setupToolbar(getString(R.string.title_version_info));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        setupVersionInfoList();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    private void setupVersionInfoList() {
        RecyclerView versionRecyclerView = (RecyclerView) findViewById(R.id.version_listview);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        versionRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        GridLayoutManager versionLayoutManager = new GridLayoutManager(this, 1);
        versionRecyclerView.setLayoutManager(versionLayoutManager);
        String[] versionInfoArray = getResources().getStringArray(R.array.version_info_array);
        Collections.reverse(Arrays.asList(versionInfoArray));
        String[] versionNumberArray = getResources().getStringArray(R.array.version_number_array);
        Collections.reverse(Arrays.asList(versionNumberArray));
        String currentVersion = getIntent().getStringExtra(CURRENT_VERSION);
        VersionInfoAdapter versionInfoAdapter = new VersionInfoAdapter(this, versionInfoArray, versionNumberArray, currentVersion);
        versionRecyclerView.setAdapter(versionInfoAdapter);
    }

    private class VersionInfoAdapter extends RecyclerView.Adapter<VersionInfoItemViewHolder> {

        String[] versionInfoArray;
        String[] versionNumberArray;
        String currentVersion;
        Context mContext;

        public VersionInfoAdapter(Context context, String[] versionInfoList, String[] versionNumberList,
                                  String currentVersion) {
            mContext = context;
            versionInfoArray = versionInfoList;
            versionNumberArray = versionNumberList;
            this.currentVersion = currentVersion;
        }

        @Override
        public VersionInfoItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            // create a new view
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_version_info, parent, false);
            // set the view's size, margins, paddings and layout parameters
            VersionInfoItemViewHolder vh = new VersionInfoItemViewHolder(mContext, v);
            return vh;
        }

        @Override
        public void onBindViewHolder(VersionInfoItemViewHolder holder, int position) {
            if (versionNumberArray[position].equalsIgnoreCase(currentVersion)) {
                holder.txtTitle.setText("v" + versionNumberArray[position] + " (current)");
            } else {
                holder.txtTitle.setText("v" + versionNumberArray[position]);
            }
            holder.txtDesc.setText(versionInfoArray[position]);
        }

        @Override
        public int getItemCount() {
            return versionInfoArray.length;
        }
    }

    private class VersionInfoItemViewHolder extends RecyclerView.ViewHolder {

        public TextView txtTitle;
        public TextView txtDesc;

        public VersionInfoItemViewHolder(Context context, View itemView) {
            super(itemView);
            txtTitle = (TextView) itemView.findViewById(R.id.list_item_version_info_title);
            txtDesc = (TextView) itemView.findViewById(R.id.list_item_version_info_desc);
        }
    }
}

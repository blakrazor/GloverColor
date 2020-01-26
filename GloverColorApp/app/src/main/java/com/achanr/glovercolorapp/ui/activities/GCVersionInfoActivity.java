package com.achanr.glovercolorapp.ui.activities;

import android.content.Context;
import android.os.Bundle;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.achanr.glovercolorapp.R;
import com.achanr.glovercolorapp.ui.viewHolders.GCVersionInfoViewHolder;

import java.util.Arrays;
import java.util.Collections;

/**
 * Copyright (c) 2014 Miami HEAT. All rights reserved
 *
 * @author SapientNitro
 */
public class GCVersionInfoActivity extends GCBaseActivity {

    public static final String CURRENT_VERSION = "current_version";
    private GCVersionInfoViewHolder mViewHolder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
    protected void setupContentLayout() {
        View view = getLayoutInflater().inflate(R.layout.activity_version_info, mFrameLayout);
        mViewHolder = new GCVersionInfoViewHolder(view);
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    private void setupVersionInfoList() {

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mViewHolder.getRecyclerView().setHasFixedSize(true);

        // use a linear layout manager
        GridLayoutManager versionLayoutManager = new GridLayoutManager(this, 1);
        mViewHolder.getRecyclerView().setLayoutManager(versionLayoutManager);
        String[] versionInfoArray = getResources().getStringArray(R.array.version_info_array);
        Collections.reverse(Arrays.asList(versionInfoArray));
        String[] versionNumberArray = getResources().getStringArray(R.array.version_number_array);
        Collections.reverse(Arrays.asList(versionNumberArray));
        String currentVersion = getIntent().getStringExtra(CURRENT_VERSION);
        VersionInfoAdapter versionInfoAdapter = new VersionInfoAdapter(this, versionInfoArray, versionNumberArray, currentVersion);
        mViewHolder.getRecyclerView().setAdapter(versionInfoAdapter);
    }

    private class VersionInfoAdapter extends RecyclerView.Adapter<VersionInfoItemViewHolder> {

        final String[] versionInfoArray;
        final String[] versionNumberArray;
        final String currentVersion;
        final Context mContext;

        VersionInfoAdapter(Context context, String[] versionInfoList, String[] versionNumberList,
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
            return new VersionInfoItemViewHolder(v);
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

        final TextView txtTitle;
        final TextView txtDesc;

        VersionInfoItemViewHolder(View itemView) {
            super(itemView);
            txtTitle = (TextView) itemView.findViewById(R.id.list_item_version_info_title);
            txtDesc = (TextView) itemView.findViewById(R.id.list_item_version_info_desc);
        }
    }
}

package com.achanr.glovercolorapp.ui.viewHolders;

import android.view.View;

import com.achanr.glovercolorapp.R;
import com.achanr.glovercolorapp.ui.views.GridRecyclerView;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author Andrew Chanrasmi on 11/9/16
 */

public class GCSyncConflictViewHolder {

    @BindView(R.id.sync_conflicts_recyclerview)
    GridRecyclerView mGridRecyclerView;

    public GCSyncConflictViewHolder(View view) {
        ButterKnife.bind(this, view);
    }

    public GridRecyclerView getGridRecyclerView() {
        return mGridRecyclerView;
    }
}

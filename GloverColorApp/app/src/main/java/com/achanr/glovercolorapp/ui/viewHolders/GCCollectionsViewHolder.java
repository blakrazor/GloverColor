package com.achanr.glovercolorapp.ui.viewHolders;

import android.support.design.widget.FloatingActionButton;
import android.view.View;

import com.achanr.glovercolorapp.R;
import com.achanr.glovercolorapp.ui.views.GridRecyclerView;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author Andrew Chanrasmi on 11/7/16
 */

public class GCCollectionsViewHolder {

    @BindView(R.id.collections_recyclerview)
    GridRecyclerView mCollectionsListRecyclerView;

    @BindView(R.id.fab)
    FloatingActionButton mFab;

    public GCCollectionsViewHolder(View view){
        ButterKnife.bind(this, view);
    }

    public GridRecyclerView getCollectionsListRecyclerView() {
        return mCollectionsListRecyclerView;
    }

    public FloatingActionButton getFab() {
        return mFab;
    }
}

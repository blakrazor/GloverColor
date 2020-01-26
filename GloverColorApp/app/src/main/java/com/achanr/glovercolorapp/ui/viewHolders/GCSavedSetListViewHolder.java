package com.achanr.glovercolorapp.ui.viewHolders;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import android.view.View;

import com.achanr.glovercolorapp.R;
import com.achanr.glovercolorapp.ui.views.GridRecyclerView;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author Andrew Chanrasmi on 11/9/16
 */

public class GCSavedSetListViewHolder {

    @BindView(R.id.saved_set_list_recyclerview)
    GridRecyclerView mSavedSetListRecyclerView;

    @BindView(R.id.fab)
    FloatingActionButton mFab;

    public GCSavedSetListViewHolder(View view) {
        ButterKnife.bind(this, view);
    }

    public GridRecyclerView getSavedSetListRecyclerView() {
        return mSavedSetListRecyclerView;
    }

    public FloatingActionButton getFab() {
        return mFab;
    }
}

package com.achanr.glovercolorapp.ui.viewHolders;

import android.view.View;

import com.achanr.glovercolorapp.R;
import com.achanr.glovercolorapp.ui.views.GridRecyclerView;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author Andrew Chanrasmi on 11/22/16
 */

public class GCUploadSetViewHolder  {

    @BindView(R.id.upload_set_recyclerview)
    GridRecyclerView mGridRecyclerView;

    public GCUploadSetViewHolder(View view) {
        ButterKnife.bind(this, view);
    }

    public GridRecyclerView getGridRecyclerView() {
        return mGridRecyclerView;
    }
}

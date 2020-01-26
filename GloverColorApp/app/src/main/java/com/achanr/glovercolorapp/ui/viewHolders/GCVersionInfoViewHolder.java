package com.achanr.glovercolorapp.ui.viewHolders;

import androidx.recyclerview.widget.RecyclerView;
import android.view.View;

import com.achanr.glovercolorapp.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author Andrew Chanrasmi on 11/9/16
 */

public class GCVersionInfoViewHolder {

    @BindView(R.id.version_listview)
    RecyclerView mRecyclerView;

    public GCVersionInfoViewHolder(View view) {
        ButterKnife.bind(this, view);
    }

    public RecyclerView getRecyclerView() {
        return mRecyclerView;
    }
}

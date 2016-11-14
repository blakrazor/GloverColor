package com.achanr.glovercolorapp.ui.viewHolders;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;

import com.achanr.glovercolorapp.R;
import com.achanr.glovercolorapp.ui.views.GridRecyclerView;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author Andrew Chanrasmi on 11/7/16
 */

public class GCEditCollectionViewHolder {

    @BindView(R.id.edit_text_title)
    EditText mTitleEditText;

    @BindView(R.id.edit_text_description)
    EditText mDescEditText;

    @BindView(R.id.add_set_button)
    Button mAddSetButton;

    @BindView(R.id.collection_sets_list)
    GridRecyclerView mSetsListRecyclerView;

    @BindView(R.id.edit_set_layout)
    ScrollView mEditSetLayout;

    public GCEditCollectionViewHolder(View view) {
        ButterKnife.bind(this, view);
    }

    public EditText getTitleEditText() {
        return mTitleEditText;
    }

    public EditText getDescEditText() {
        return mDescEditText;
    }

    public Button getAddSetButton() {
        return mAddSetButton;
    }

    public GridRecyclerView getSetsListRecyclerView() {
        return mSetsListRecyclerView;
    }

    public ScrollView getEditSetLayout() {
        return mEditSetLayout;
    }
}

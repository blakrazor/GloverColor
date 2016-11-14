package com.achanr.glovercolorapp.ui.viewHolders;

import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;

import com.achanr.glovercolorapp.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author Andrew Chanrasmi on 11/7/16
 */

public class GCEditSavedSetViewHolder {

    @BindView(R.id.edit_text_title)
    EditText mTitleEditText;

    @BindView(R.id.edit_text_description)
    EditText mDescriptionEditText;

    @BindView(R.id.mode_spinner)
    Spinner mModeSpinner;

    @BindView(R.id.chip_preset_spinner)
    Spinner mChipSetSpinner;

    @BindView(R.id.edit_set_layout)
    ScrollView mEditSetLayout;

    @BindView(R.id.color_swatch_layout)
    LinearLayout mColorSwatchLayout;

    @BindView(R.id.more_color_swatch_layout)
    LinearLayout mMoreColorSwatchLayout;

    @BindView(R.id.color_layout)
    LinearLayout mColorLayout;

    public GCEditSavedSetViewHolder(View view) {
        ButterKnife.bind(this, view);
    }

    public EditText getTitleEditText() {
        return mTitleEditText;
    }

    public EditText getDescriptionEditText() {
        return mDescriptionEditText;
    }

    public Spinner getModeSpinner() {
        return mModeSpinner;
    }

    public Spinner getChipSetSpinner() {
        return mChipSetSpinner;
    }

    public ScrollView getEditSetLayout() {
        return mEditSetLayout;
    }

    public LinearLayout getMoreColorSwatchLayout() {
        return mMoreColorSwatchLayout;
    }

    public LinearLayout getColorLayout() {
        return mColorLayout;
    }

    public LinearLayout getColorSwatchLayout() {
        return mColorSwatchLayout;
    }
}

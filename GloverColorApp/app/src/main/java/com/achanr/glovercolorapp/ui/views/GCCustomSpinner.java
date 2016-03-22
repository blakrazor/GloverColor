package com.achanr.glovercolorapp.ui.views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Spinner;

/**
 * @author Andrew Chanrasmi
 * @created 3/22/16 4:06 PM
 */
public class GCCustomSpinner extends Spinner {

    public GCCustomSpinner(Context context) {
        super(context);
    }

    public GCCustomSpinner(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public GCCustomSpinner(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void setSelection(int position, boolean animate) {
        boolean sameSelected = position == getSelectedItemPosition();
        super.setSelection(position, animate);
        if (sameSelected && getOnItemSelectedListener() != null) {
            // Spinner does not call the OnItemSelectedListener if the same item is selected, so do it manually now
            getOnItemSelectedListener().onItemSelected(this, getSelectedView(), position, getSelectedItemId());
        }
    }

    @Override
    public void setSelection(int position) {
        boolean sameSelected = position == getSelectedItemPosition();
        super.setSelection(position);
        if (sameSelected && getOnItemSelectedListener() != null) {
            // Spinner does not call the OnItemSelectedListener if the same item is selected, so do it manually now
            getOnItemSelectedListener().onItemSelected(this, getSelectedView(), position, getSelectedItemId());
        }
    }
}
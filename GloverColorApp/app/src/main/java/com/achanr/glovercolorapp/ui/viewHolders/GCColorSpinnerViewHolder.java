package com.achanr.glovercolorapp.ui.viewHolders;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.achanr.glovercolorapp.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author Andrew Chanrasmi on 11/8/16
 */

public class GCColorSpinnerViewHolder {

    @BindView(R.id.color_1_layout)
    LinearLayout firstColorLayout;

    @BindView(R.id.spinner_color_1)
    Spinner firstColorSpinner;

    @BindView(R.id.color_2_layout)
    LinearLayout secondColorLayout;

    @BindView(R.id.spinner_color_2)
    Spinner secondColorSpinner;

    @BindView(R.id.textview_color_1)
    TextView firstColorTextView;

    @BindView(R.id.textview_color_2)
    TextView secondColorTextView;

    public GCColorSpinnerViewHolder(View view, String colorTitleOne, String colorTitleTwo) {
        ButterKnife.bind(this, view);
        firstColorTextView.setText(colorTitleOne);
        secondColorTextView.setText(colorTitleTwo);
    }

    public LinearLayout getFirstColorLayout() {
        return firstColorLayout;
    }

    public Spinner getFirstColorSpinner() {
        return firstColorSpinner;
    }

    public LinearLayout getSecondColorLayout() {
        return secondColorLayout;
    }

    public Spinner getSecondColorSpinner() {
        return secondColorSpinner;
    }
}

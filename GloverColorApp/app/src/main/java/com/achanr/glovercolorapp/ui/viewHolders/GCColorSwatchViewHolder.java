package com.achanr.glovercolorapp.ui.viewHolders;

import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.achanr.glovercolorapp.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author Andrew Chanrasmi on 11/8/16
 */

public class GCColorSwatchViewHolder {

    @BindView(R.id.color_swatch_1)
    RelativeLayout colorSwatch;

    @BindView(R.id.color_swatch_1_textview)
    TextView colorSwatchTextView;

    public GCColorSwatchViewHolder(View view) {
        ButterKnife.bind(this, view);
    }

    public RelativeLayout getColorSwatch() {
        return colorSwatch;
    }

    public TextView getColorSwatchTextView() {
        return colorSwatchTextView;
    }
}

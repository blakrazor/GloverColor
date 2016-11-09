package com.achanr.glovercolorapp.ui.viewHolders;

import android.view.View;
import android.widget.Button;

import com.achanr.glovercolorapp.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author Andrew Chanrasmi on 11/8/16
 */

public class GCHomeViewHolder {

    @BindView(R.id.create_new_set_button)
    Button createNewSetButton;

    @BindView(R.id.checkout_saved_set_button)
    Button checkoutSavedSetButton;

    @BindView(R.id.checkout_collection_button)
    Button checkoutCollectionButton;

    public GCHomeViewHolder(View view) {
        ButterKnife.bind(this, view);
    }

    public Button getCreateNewSetButton() {
        return createNewSetButton;
    }

    public Button getCheckoutSavedSetButton() {
        return checkoutSavedSetButton;
    }

    public Button getCheckoutCollectionButton() {
        return checkoutCollectionButton;
    }
}

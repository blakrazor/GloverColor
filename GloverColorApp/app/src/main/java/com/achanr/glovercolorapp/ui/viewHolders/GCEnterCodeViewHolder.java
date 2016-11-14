package com.achanr.glovercolorapp.ui.viewHolders;

import android.support.design.widget.TextInputLayout;
import android.view.View;
import android.widget.Button;

import com.achanr.glovercolorapp.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author Andrew Chanrasmi on 11/8/16
 */

public class GCEnterCodeViewHolder {

    @BindView(R.id.edit_text_enter_code)
    TextInputLayout mEnterCodeEditText;

    @BindView(R.id.submit_code_button)
    Button mSubmitCodeButton;

    @BindView(R.id.clear_text_button)
    Button mClearTextButton;

    public GCEnterCodeViewHolder(View view) {
        ButterKnife.bind(this, view);
    }

    public TextInputLayout getEnterCodeEditText() {
        return mEnterCodeEditText;
    }

    public Button getSubmitCodeButton() {
        return mSubmitCodeButton;
    }

    public Button getClearTextButton() {
        return mClearTextButton;
    }
}

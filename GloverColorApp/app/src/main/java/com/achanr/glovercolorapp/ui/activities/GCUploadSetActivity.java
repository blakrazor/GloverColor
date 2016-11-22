package com.achanr.glovercolorapp.ui.activities;

import android.os.Bundle;
import android.view.View;

import com.achanr.glovercolorapp.R;

/**
 * @author Andrew Chanrasmi on 11/21/16
 */

public class GCUploadSetActivity extends GCBaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupToolbar(getString(R.string.title_upload_set));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    @Override
    protected void setupContentLayout() {
        getLayoutInflater().inflate(R.layout.activity_upload_set, mFrameLayout);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}

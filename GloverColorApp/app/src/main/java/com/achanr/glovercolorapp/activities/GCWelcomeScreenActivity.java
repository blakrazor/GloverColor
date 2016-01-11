package com.achanr.glovercolorapp.activities;

import android.os.Bundle;

import com.achanr.glovercolorapp.R;

public class GCWelcomeScreenActivity extends GCBaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLayoutInflater().inflate(R.layout.app_bar_home_screen, mFrameLayout);
        setupToolbar(getString(R.string.title_activity_gcwelcome_screen));
    }

    @Override
    protected void onResume() {
        super.onResume();
        setPosition(R.id.nav_home);
    }
}

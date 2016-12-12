package com.achanr.glovercolorapp.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.achanr.glovercolorapp.R;

/**
 * @author Andrew Chanrasmi on 11/21/16
 */

public class GCDiscoverActivity extends GCBaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupToolbar(getString(R.string.title_discover));
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
        getLayoutInflater().inflate(R.layout.activity_discover, mFrameLayout);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //TODO: Disabling this for now in order to fix secret issue
//        setPosition(R.id.nav_discover);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, 1, 1, "Upload").setIcon(R.drawable.ic_cloud_upload_white_48dp)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 1:
                Intent intent = new Intent(this, GCUploadSetActivity.class);
                startActivityTransition(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}

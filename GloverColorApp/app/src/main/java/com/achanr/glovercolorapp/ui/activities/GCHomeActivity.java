package com.achanr.glovercolorapp.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.achanr.glovercolorapp.R;

public class GCHomeActivity extends GCBaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLayoutInflater().inflate(R.layout.activity_home, mFrameLayout);
        setupToolbar(getString(R.string.title_activity_gcwelcome_screen));
        setupClickListeners();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setPosition(R.id.nav_home);
    }

    private void setupClickListeners() {
        Button createNewSetButton = (Button) findViewById(R.id.create_new_set_button);
        Button checkoutSavedSetButton = (Button) findViewById(R.id.checkout_saved_set_button);
        Button checkoutCollectionButton = (Button) findViewById(R.id.checkout_collection_button);

        createNewSetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GCHomeActivity.this, GCSavedSetListActivity.class);
                intent.putExtra(GCSavedSetListActivity.FROM_NAVIGATION, GCHomeActivity.class.getName());
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });

        checkoutSavedSetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToSavedSetListActivity();
            }
        });

        checkoutCollectionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToCollectionListActivity();
            }
        });
    }

    private void goToSavedSetListActivity() {
        Intent intent = new Intent(GCHomeActivity.this, GCSavedSetListActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivityTransition(intent);
    }

    private void goToCollectionListActivity() {
        Intent intent = new Intent(GCHomeActivity.this, GCCollectionsActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivityTransition(intent);
    }
}

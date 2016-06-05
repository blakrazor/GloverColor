package com.achanr.glovercolorapp.ui.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
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
        checkIfNewVersion();
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

    private void checkIfNewVersion() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(GCHomeActivity.this);
        String oldVersion = prefs.getString("CHECK_UPDATE_VERSION", "0.0");
        String currentVersion;
        try {
            PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            currentVersion = pInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            currentVersion = prefs.getString(getString(R.string.version_number_preference), "0.0");
        }

        if (!currentVersion.equalsIgnoreCase(oldVersion)) {
            displayWhatsNewDialog(currentVersion);
        }

        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("CHECK_UPDATE_VERSION", currentVersion.toString());
        editor.apply();
    }

    private void displayWhatsNewDialog(String versionNumber) {
        String[] versionInfoArray = getResources().getStringArray(R.array.version_info_array);
        String latestMessage = "What\'s new in this version?" + versionInfoArray[versionInfoArray.length - 1];

        new AlertDialog.Builder(GCHomeActivity.this)
                .setTitle("GloverColor v" + versionNumber)
                .setMessage(latestMessage)
                .setNeutralButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .show();
    }
}

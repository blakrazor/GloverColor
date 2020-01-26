package com.achanr.glovercolorapp.ui.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import androidx.appcompat.app.AlertDialog;
import android.view.View;

import com.achanr.glovercolorapp.R;
import com.achanr.glovercolorapp.ui.viewHolders.GCHomeViewHolder;

public class GCHomeActivity extends GCBaseActivity {

    private GCHomeViewHolder mHomeViewHolder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupToolbar(getString(R.string.title_activity_gcwelcome_screen));
        setupClickListeners();
        checkIfNewVersion();
    }

    @Override
    protected void setupContentLayout() {
        View view = getLayoutInflater().inflate(R.layout.activity_home, mFrameLayout);
        mHomeViewHolder = new GCHomeViewHolder(view);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setPosition(R.id.nav_home);
    }

    private void setupClickListeners() {

        mHomeViewHolder.getCreateNewSetButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GCHomeActivity.this, GCSavedSetListActivity.class);
                intent.putExtra(GCSavedSetListActivity.FROM_NAVIGATION, GCHomeActivity.class.getName());
                startActivityTransition(intent);
            }
        });

        mHomeViewHolder.getCheckoutSavedSetButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToSavedSetListActivity();
            }
        });

        mHomeViewHolder.getCheckoutCollectionButton().setOnClickListener(new View.OnClickListener() {
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

        String[] versionNumberArray = getResources().getStringArray(R.array.version_number_array);
        String currentVersion = versionNumberArray[versionNumberArray.length - 1];

        if (!currentVersion.equalsIgnoreCase(oldVersion)) {
            displayWhatsNewDialog(currentVersion);
        }

        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("CHECK_UPDATE_VERSION", currentVersion);
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

package com.achanr.glovercolorapp.activities;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.view.View;

import com.achanr.glovercolorapp.R;
import com.achanr.glovercolorapp.models.GCPowerLevel;
import com.achanr.glovercolorapp.utility.EGCThemeEnum;
import com.achanr.glovercolorapp.utility.GCConstants;
import com.achanr.glovercolorapp.utility.GCPowerLevelUtil;
import com.achanr.glovercolorapp.utility.GCUtil;
import com.achanr.glovercolorapp.views.GCPowerLevelPreference;

import java.util.ArrayList;

/**
 * Glover Color App Project
 *
 * @author Andrew Chanrasmi
 * @created 2/16/16 3:28 PM
 */
public class GCSettingsActivity extends GCBaseActivity {

    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLayoutInflater().inflate(R.layout.activity_settings, mFrameLayout);
        setupToolbar(getString(R.string.title_settings));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        mContext = this;

        PrefsFragment prefsFragment = new PrefsFragment();
        prefsFragment.setContext(this);

        getFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, prefsFragment).commit();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setPosition(R.id.nav_settings);
    }

    @Override
    public void onBackPressed() {
        finish();
        //overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    public static class PrefsFragment extends PreferenceFragment {

        private ListPreference mListPreference;
        private Activity mContext;

        public void setContext(Activity context) {
            mContext = context;
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            // Load the preferences from an XML resource
            addPreferencesFromResource(R.xml.preferences);

            //Setup preferences
            setupThemePreference();
            setupVersionNumberPreference();
            setupPowerLevelPreference();
        }

        private void setupThemePreference() {
            mListPreference = (ListPreference) findPreference("THEME_PREFERENCE");
            String oldThemeString = GCUtil.getCurrentTheme();
            String shortThemeString = oldThemeString.substring(0, oldThemeString.indexOf("_THEME"));
            mListPreference.setSummary(GCUtil.convertToCamelcase(shortThemeString));
            mListPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    String newThemeString = (String) newValue;
                    EGCThemeEnum themeEnum = EGCThemeEnum.valueOf(newThemeString.replace(" ", "_").toUpperCase());
                    GCUtil.changeToTheme(mContext, themeEnum);
                    return true;
                }
            });
        }

        private void setupVersionNumberPreference() {
            Preference versionPreference = findPreference("VERSION_NUMBER_PREFERENCE");
            String version;
            try {
                PackageInfo pInfo = mContext.getPackageManager().getPackageInfo(mContext.getPackageName(), 0);
                version = pInfo.versionName;
            } catch (PackageManager.NameNotFoundException e) {
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
                version = prefs.getString("VERSION_NUMBER_PREFERENCE", "0.0");
            }
            versionPreference.setSummary(version);
        }

        private void setupPowerLevelPreference() {
            final GCPowerLevelPreference powerLevelPreference = (GCPowerLevelPreference) findPreference("POWER_LEVEL_PREFERENCE");
            powerLevelPreference.setPowerLevelCallback(new GCPowerLevelPreference.PowerLevelCallback() {
                @Override
                public void onPowerLevelChanged() {
                    ArrayList<GCPowerLevel> powerLevelArrayList = GCPowerLevelUtil.getPowerLevelArrayList();
                    String powerLevelString = "";
                    for (GCPowerLevel powerLevel : powerLevelArrayList) {
                        powerLevelString += powerLevel.convertValueToInt();
                        if (!powerLevel.getTitle().equalsIgnoreCase(GCConstants.POWER_LEVEL_LOW_TITLE)) {
                            powerLevelString += " - ";
                        }
                    }
                    powerLevelPreference.setSummary(powerLevelString);

                    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putBoolean(GCConstants.WAS_POWER_LEVELS_CHANGED_KEY, true);
                    editor.apply();
                }
            });

            ArrayList<GCPowerLevel> powerLevelArrayList = GCPowerLevelUtil.getPowerLevelArrayList();
            String powerLevelString = "";
            for (GCPowerLevel powerLevel : powerLevelArrayList) {
                powerLevelString += powerLevel.convertValueToInt();
                if (!powerLevel.getTitle().equalsIgnoreCase(GCConstants.POWER_LEVEL_LOW_TITLE)) {
                    powerLevelString += " - ";
                }
            }
            powerLevelPreference.setSummary(powerLevelString);
        }
    }
}
package com.achanr.glovercolorapp.ui.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.view.View;

import com.achanr.glovercolorapp.R;
import com.achanr.glovercolorapp.common.EGCThemeEnum;
import com.achanr.glovercolorapp.common.GCConstants;
import com.achanr.glovercolorapp.common.GCPowerLevelUtil;
import com.achanr.glovercolorapp.common.GCUtil;
import com.achanr.glovercolorapp.models.GCPowerLevel;
import com.achanr.glovercolorapp.ui.views.GCPowerLevelPreference;

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
            setupRateAppPreference();
            setupReportBugsPreference();
        }

        private void setupThemePreference() {
            mListPreference = (ListPreference) findPreference(mContext.getString(R.string.theme_preference));
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
            Preference versionPreference = findPreference(mContext.getString(R.string.version_number_preference));
            String version;
            try {
                PackageInfo pInfo = mContext.getPackageManager().getPackageInfo(mContext.getPackageName(), 0);
                version = pInfo.versionName;
            } catch (PackageManager.NameNotFoundException e) {
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
                version = prefs.getString(mContext.getString(R.string.version_number_preference), "0.0");
            }
            versionPreference.setSummary(version);
        }

        private void setupPowerLevelPreference() {
            final GCPowerLevelPreference powerLevelPreference = (GCPowerLevelPreference) findPreference(mContext.getString(R.string.power_level_preference));
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

        private void setupRateAppPreference() {
            Preference rateAppPreference = findPreference(mContext.getString(R.string.rate_app_preference));
            rateAppPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    final String appPackageName = mContext.getPackageName(); // getPackageName() from Context or Activity object
                    try {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                    } catch (android.content.ActivityNotFoundException anfe) {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                    }
                    return true;
                }
            });
        }

        private void setupReportBugsPreference() {
            Preference reportBugsPreference = findPreference(mContext.getString(R.string.report_bugs_preference));
            reportBugsPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    String version;
                    try {
                        PackageInfo pInfo = mContext.getPackageManager().getPackageInfo(mContext.getPackageName(), 0);
                        version = pInfo.versionName;
                    } catch (PackageManager.NameNotFoundException e) {
                        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
                        version = prefs.getString(mContext.getString(R.string.version_number_preference), "0.0");
                    }

                    Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                            "mailto","andrew.chanrasmi.developer+GloverColorApp@gmail.com", null));
                    emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{"andrew.chanrasmi.developer+GloverColorApp@gmail.com"}); // String[] addresses
                    emailIntent.putExtra(Intent.EXTRA_SUBJECT, "[GloverColorApp] Bug or Feedback Report");
                    emailIntent.putExtra(Intent.EXTRA_TEXT,
                            "App Name: GloverColor" + "\n" +
                            "Version: " + version + "\n" +
                            "Contact (optional): " + "\n" +
                            "Details: ");
                    startActivity(Intent.createChooser(emailIntent, "Send email..."));
                    return true;
                }
            });
        }
    }
}
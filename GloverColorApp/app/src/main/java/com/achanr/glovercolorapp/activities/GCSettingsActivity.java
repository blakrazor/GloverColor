package com.achanr.glovercolorapp.activities;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.view.View;

import com.achanr.glovercolorapp.R;
import com.achanr.glovercolorapp.utility.EGCThemeEnum;
import com.achanr.glovercolorapp.utility.GCUtil;

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
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
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
    }
}
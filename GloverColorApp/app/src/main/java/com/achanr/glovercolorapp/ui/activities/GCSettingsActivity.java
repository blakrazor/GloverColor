package com.achanr.glovercolorapp.ui.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import androidx.appcompat.app.AlertDialog;
import android.view.View;
import android.widget.Toast;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupToolbar(getString(R.string.title_settings));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        PrefsFragment prefsFragment = new PrefsFragment();

        getFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, prefsFragment).commit();
    }

    @Override
    protected void setupContentLayout() {
        getLayoutInflater().inflate(R.layout.activity_settings, mFrameLayout);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setPosition(R.id.nav_settings);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    public static class PrefsFragment extends PreferenceFragment {

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
            setupDonatePreference();
            setupResetDialogPreference();
            setupDefaultChipPreference();
            setupFollowFacebookPreference();
            setupAcknowledgementsPreference();
            setupOptinBetaPreference();
        }

        private void setupThemePreference() {
            ListPreference listPreference = (ListPreference) findPreference(getActivity().getString(R.string.theme_preference));
            String oldThemeString = GCUtil.getCurrentTheme(getActivity());
            String shortThemeString = oldThemeString.substring(0, oldThemeString.indexOf("_THEME"));
            listPreference.setSummary(GCUtil.convertToTitleCase(getActivity(), shortThemeString));
            listPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    String newThemeString = (String) newValue;
                    EGCThemeEnum themeEnum = EGCThemeEnum.valueOf(newThemeString.replace(" ", "_").toUpperCase());
                    GCUtil.changeToTheme(getActivity(), themeEnum);
                    return true;
                }
            });
        }

        private void setupVersionNumberPreference() {
            Preference versionPreference = findPreference(getActivity().getString(R.string.version_number_preference));
            String version;
            try {
                PackageInfo pInfo = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0);
                version = pInfo.versionName;
            } catch (PackageManager.NameNotFoundException e) {
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
                version = prefs.getString(getActivity().getString(R.string.version_number_preference), "0.0");
            }
            final String versionFinal = version;
            versionPreference.setSummary(version);
            versionPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    Intent intent = new Intent(getActivity(), GCVersionInfoActivity.class);
                    intent.putExtra(GCVersionInfoActivity.CURRENT_VERSION, versionFinal);
                    startActivity(intent);
                    return true;
                }
            });
        }

        private void setupPowerLevelPreference() {
            final GCPowerLevelPreference powerLevelPreference = (GCPowerLevelPreference) findPreference(getActivity().getString(R.string.power_level_preference));
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

                    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
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
            Preference rateAppPreference = findPreference(getActivity().getString(R.string.rate_app_preference));
            rateAppPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    final String appPackageName = getActivity().getPackageName(); // getPackageName() from Context or Activity object
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
            Preference reportBugsPreference = findPreference(getActivity().getString(R.string.report_bugs_preference));
            reportBugsPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    /*String version;
                    try {
                        PackageInfo pInfo = mContext.getPackageManager().getPackageInfo(mContext.getPackageName(), 0);
                        version = pInfo.versionName;
                    } catch (PackageManager.NameNotFoundException e) {
                        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
                        version = prefs.getString(mContext.getString(R.string.version_number_preference), "0.0");
                    }

                    Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                            "mailto", mContext.getString(R.string.developer_email_address), null));
                    emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{mContext.getString(R.string.developer_email_address)}); // String[] addresses
                    emailIntent.putExtra(Intent.EXTRA_SUBJECT, "[GloverColorApp] Bug or Feedback Report");
                    emailIntent.putExtra(Intent.EXTRA_TEXT,
                            "App Name: GloverColor" + "\n" +
                                    "Version: " + version + "\n" +
                                    "Contact (optional): " + "\n" +
                                    "Details: ");
                    startActivity(Intent.createChooser(emailIntent, "Send email..."));*/

                    String url = "http://goo.gl/forms/Ge690vaYDQ";
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(url));
                    startActivity(intent);

                    return true;
                }
            });
        }

        private void setupDonatePreference() {
            Preference donatePreference = findPreference(getActivity().getString(R.string.donate_preference));
            donatePreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(getActivity().getString(R.string.paypal_donate_link)));
                    startActivity(browserIntent);
                    return true;
                }
            });
        }

        private void setupResetDialogPreference() {
            Preference rateAppPreference = findPreference(getActivity().getString(R.string.reset_dialog_preference));
            rateAppPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {

                    //Reset chip dialog
                    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putBoolean(GCConstants.DONT_SHOW_CHIP_PRESET_DIALOG_KEY, false);
                    editor.apply();

                    //Display toast
                    Toast.makeText(getActivity(), "Succesfully reset dialogs", Toast.LENGTH_SHORT).show();

                    return true;
                }
            });
        }

        private void setupDefaultChipPreference() {
            ListPreference listPreference = (ListPreference) findPreference(getActivity().getString(R.string.default_chip_preference));
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
            String oldDefaultChip = prefs.getString(getString(R.string.default_chip_preference), getString(R.string.NO_CHIP));
            listPreference.setSummary(GCUtil.convertToTitleCase(getActivity(), oldDefaultChip));
            listPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    String newDefaultChipString = (String) newValue;
                    preference.setSummary(GCUtil.convertToTitleCase(getActivity(), newDefaultChipString));
                    return true;
                }
            });
        }

        private void setupFollowFacebookPreference() {
            Preference facebookPreference = findPreference(getActivity().getString(R.string.follow_facebook_preference));
            facebookPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    Uri uri = Uri.parse(getActivity().getString(R.string.facebook_page_url));
                    try {
                        ApplicationInfo applicationInfo = getActivity().getPackageManager().getApplicationInfo("com.facebook.katana", 0);
                        if (applicationInfo.enabled) {
                            uri = Uri.parse("fb://facewebmodal/f?href=" + getActivity().getString(R.string.facebook_page_url));
                        }
                    } catch (PackageManager.NameNotFoundException ignored) {
                    }
                    startActivity(new Intent(Intent.ACTION_VIEW, uri));
                    return true;
                }
            });
        }

        private void setupAcknowledgementsPreference() {
            Preference acknowledgementsPreference = findPreference(getActivity().getString(R.string.acknowledgements_preference));
            acknowledgementsPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    new AlertDialog.Builder(getActivity())
                            .setTitle("Acknowledgements")
                            .setMessage(getString(R.string.acknowledgements_list))
                            .setNeutralButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            })
                            .show();
                    return true;
                }
            });
        }

        private void setupOptinBetaPreference() {
            Preference optinBetaPreference = findPreference(getActivity().getString(R.string.opt_in_beta_preference));
            optinBetaPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(getActivity().getString(R.string.beta_opt_in_link)));
                    startActivity(browserIntent);
                    return true;
                }
            });
        }
    }
}
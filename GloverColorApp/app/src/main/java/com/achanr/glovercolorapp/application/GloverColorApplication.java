package com.achanr.glovercolorapp.application;

import android.app.Application;

import com.achanr.glovercolorapp.common.GCChipUtil;
import com.achanr.glovercolorapp.common.GCColorUtil;
import com.achanr.glovercolorapp.common.GCModeUtil;
import com.achanr.glovercolorapp.common.GCOnlineDatabaseUtil;
import com.achanr.glovercolorapp.common.GCPowerLevelUtil;
import com.achanr.glovercolorapp.database.GCDatabaseHelper;
import com.crashlytics.android.Crashlytics;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;

import io.fabric.sdk.android.BuildConfig;
import io.fabric.sdk.android.Fabric;

/**
 * Glover Color App Project
 *
 * @author Andrew Chanrasmi
 * @created 1/8/16 4:37 PM
 */
public class GloverColorApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);
        if (!BuildConfig.DEBUG) {
            Fabric.with(this, new Crashlytics());
        }
        GCDatabaseHelper.getInstance(getApplicationContext());
        GCColorUtil.initColorArrayList(this);
        GCPowerLevelUtil.initPowerLevelArrayList(this);
        GCChipUtil.initChipArrayList(this);
        GCModeUtil.initModeArrayList(this);
        GCOnlineDatabaseUtil.initialize();
        GCOnlineDatabaseUtil.checkSyncStatus(this, null);
    }
}

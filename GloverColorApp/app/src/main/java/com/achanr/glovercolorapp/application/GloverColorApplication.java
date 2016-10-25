package com.achanr.glovercolorapp.application;

import android.app.Application;

import com.achanr.glovercolorapp.common.GCChipUtil;
import com.achanr.glovercolorapp.common.GCColorUtil;
import com.achanr.glovercolorapp.common.GCModeUtil;
import com.achanr.glovercolorapp.common.GCPowerLevelUtil;
import com.achanr.glovercolorapp.database.GCDatabaseHelper;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;

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
        //Fabric.with(this, new Crashlytics());
        GCDatabaseHelper.getInstance(getApplicationContext());
        GCColorUtil.initColorArrayList(this);
        GCPowerLevelUtil.initPowerLevelArrayList(this);
        GCChipUtil.initChipArrayList(this);
        GCModeUtil.initModeArrayList(this);
    }
}

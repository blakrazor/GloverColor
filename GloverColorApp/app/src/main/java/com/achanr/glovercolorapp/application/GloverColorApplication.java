package com.achanr.glovercolorapp.application;

import android.app.Application;
import android.content.Context;

import com.achanr.glovercolorapp.database.GCDatabaseAdapter;
import com.achanr.glovercolorapp.common.GCChipUtil;
import com.achanr.glovercolorapp.common.GCColorUtil;
import com.achanr.glovercolorapp.common.GCModeUtil;
import com.achanr.glovercolorapp.common.GCPowerLevelUtil;

/**
 * Glover Color App Project
 *
 * @author Andrew Chanrasmi
 * @created 1/8/16 4:37 PM
 */
public class GloverColorApplication extends Application {

    private static Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
        GCDatabaseAdapter.getInstance(getApplicationContext());
        GCColorUtil.initColorArrayList();
        GCPowerLevelUtil.initPowerLevelArrayList();
        GCChipUtil.initChipArrayList();
        GCModeUtil.initModeArrayList();
    }

    public static Context getContext(){
        return mContext;
    }
}

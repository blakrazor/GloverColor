package com.achanr.glovercolorapp.utility;

import android.util.Log;

import com.achanr.glovercolorapp.database.GCDatabaseHelper;
import com.achanr.glovercolorapp.models.GCPowerLevel;

import java.util.ArrayList;

/**
 * Glover Color App Project
 *
 * @author Andrew Chanrasmi
 * @created 3/1/16 10:46 AM
 */
public class GCPowerLevelUtil {

    private static ArrayList<GCPowerLevel> mPowerLevelArrayList;

    public static void initPowerLevelArrayList() {
        mPowerLevelArrayList = GCDatabaseHelper.POWER_LEVEL_DATABASE.readData();
        if (mPowerLevelArrayList == null || mPowerLevelArrayList.isEmpty()) {
            createDefaultPowerLevels();
        }
    }

    private static void createDefaultPowerLevels() {
        //Setup default power level values
        mPowerLevelArrayList = new ArrayList<>();
        GCPowerLevel powerLevelHigh = new GCPowerLevel(
                GCConstants.POWER_LEVEL_HIGH_TITLE,
                GCConstants.POWER_LEVEL_HIGH_ABBREV,
                GCConstants.POWER_LEVEL_HIGH_VALUE);
        GCPowerLevel powerLevelMedium = new GCPowerLevel(
                GCConstants.POWER_LEVEL_MEDIUM_TITLE,
                GCConstants.POWER_LEVEL_MEDIUM_ABBREV,
                GCConstants.POWER_LEVEL_MEDIUM_VALUE
        );
        GCPowerLevel powerLevelLow = new GCPowerLevel(
                GCConstants.POWER_LEVEL_LOW_TITLE,
                GCConstants.POWER_LEVEL_LOW_ABBREV,
                GCConstants.POWER_LEVEL_LOW_VALUE
        );
        mPowerLevelArrayList.add(powerLevelHigh);
        mPowerLevelArrayList.add(powerLevelMedium);
        mPowerLevelArrayList.add(powerLevelLow);

        //Clear database and save default values
        GCDatabaseHelper.POWER_LEVEL_DATABASE.clearTable();
        for (GCPowerLevel powerLevel : mPowerLevelArrayList) {
            GCDatabaseHelper.POWER_LEVEL_DATABASE.insertData(powerLevel);
        }
    }

    public static void updatePowerLevelValue(GCPowerLevel newPowerLevel) {
        GCPowerLevel oldPowerLevel = getPowerLevelUsingTitle(newPowerLevel.getTitle());
        if (oldPowerLevel != null) {
            GCDatabaseHelper.POWER_LEVEL_DATABASE.updateData(oldPowerLevel, newPowerLevel);
        } else {
            Log.e(GCPowerLevelUtil.class.getSimpleName(), "power level does not exist in array");
        }
    }

    public static GCPowerLevel getPowerLevelUsingTitle(String title) {
        GCPowerLevel powerLevel = null;
        for (GCPowerLevel powerLevelItem : mPowerLevelArrayList) {
            if (title.equalsIgnoreCase(powerLevelItem.getTitle())) {
                powerLevel = powerLevelItem;
                break;
            }
        }
        return powerLevel;
    }

    public static GCPowerLevel getPowerLevelUsingAbbrev(String abbrev) {
        GCPowerLevel powerLevel = null;
        for (GCPowerLevel powerLevelItem : mPowerLevelArrayList) {
            if (abbrev.equalsIgnoreCase(powerLevelItem.getAbbreviation())) {
                powerLevel = powerLevelItem;
                break;
            }
        }
        return powerLevel;
    }
}

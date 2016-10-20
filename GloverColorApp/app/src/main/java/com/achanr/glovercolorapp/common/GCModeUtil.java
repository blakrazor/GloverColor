package com.achanr.glovercolorapp.common;

import android.content.Context;

import com.achanr.glovercolorapp.R;
import com.achanr.glovercolorapp.database.GCDatabaseHelper;
import com.achanr.glovercolorapp.models.GCMode;

import java.util.ArrayList;

/**
 * @author Andrew Chanrasmi
 * @created 3/3/16 4:58 PM
 */
public class GCModeUtil {

    private static ArrayList<GCMode> mModeArrayList;

    public static void initModeArrayList(Context context) {
        mModeArrayList = GCDatabaseHelper.getInstance(context).MODE_DATABASE.getAllData();
        if (mModeArrayList == null || mModeArrayList.isEmpty()) {
            createDefaultModes(context);
        }
    }

    private static void createDefaultModes(Context context) {
        mModeArrayList = new ArrayList<>();

        String[] modesStringArray = context.getResources().getStringArray(R.array.default_modes);

        for (String modeString : modesStringArray) {
            mModeArrayList.add(new GCMode(modeString));
        }

        //Clear database and save default values
        GCDatabaseHelper.getInstance(context).MODE_DATABASE.clearTable();
        for (GCMode mode : mModeArrayList) {
            GCDatabaseHelper.getInstance(context).MODE_DATABASE.insertData(mode);
        }
    }

    public static GCMode getModeUsingTitle(Context context, String title) {
        GCMode mode = null;

        if (title.equalsIgnoreCase("SLOW_FADE")) {
            return new GCMode(context.getResources().getString(R.string.SLOW_FADE));
        }

        for (GCMode modeItem : mModeArrayList) {
            if (title.equalsIgnoreCase(modeItem.getTitle())) {
                mode = modeItem;
                break;
            }
        }

        return mode;
    }
}

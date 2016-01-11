package com.achanr.glovercolorapp.utility;

import android.app.Application;
import android.content.Context;

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
    }

    public static Context getContext(){
        return mContext;
    }
}

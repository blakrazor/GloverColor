package com.achanr.glovercolorapp.database;

import android.provider.BaseColumns;

/**
 * Glover Color App Project
 *
 * @author Andrew Chanrasmi
 * @created 1/8/16 1:52 PM
 */
public abstract class GCSavedSetEntry implements BaseColumns {
    public static final String TABLE_NAME = "savedsets";
    public static final String COLUMN_NAME_TITLE = "title";
    public static final String COLUMN_NAME_COLORS = "colors";
    public static final String COLUMN_NAME_MODE = "mode";
}

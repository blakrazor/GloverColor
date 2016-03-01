package com.achanr.glovercolorapp.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

/**
 * Glover Color App Project
 *
 * @author Andrew Chanrasmi
 * @created 3/1/16 11:21 AM
 */
public abstract class GCAbstractDatabase {
    static GCDatabaseAdapter db_adapter;
    protected Context mContext;

    public abstract void createTable(SQLiteDatabase db);
}

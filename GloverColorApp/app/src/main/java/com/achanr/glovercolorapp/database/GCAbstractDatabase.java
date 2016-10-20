package com.achanr.glovercolorapp.database;

import android.database.sqlite.SQLiteDatabase;

/**
 * Glover Color App Project
 *
 * @author Andrew Chanrasmi
 * @created 3/1/16 11:21 AM
 */
abstract class GCAbstractDatabase {
    GCDatabaseAdapter db_adapter;

    public abstract void createTable(SQLiteDatabase db);
}

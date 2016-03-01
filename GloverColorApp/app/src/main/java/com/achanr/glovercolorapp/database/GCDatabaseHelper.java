package com.achanr.glovercolorapp.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Glover Color App Project
 *
 * @author Andrew Chanrasmi
 * @created 1/8/16 1:42 PM
 */
public class GCDatabaseHelper extends SQLiteOpenHelper {

    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 3;
    public static final String DATABASE_NAME = "SavedSet.db";
    public static final String TEXT_TYPE = " TEXT";
    public static final String INT_TYPE = " INT";
    public static final String COMMA_SEP = ",";
    private static Context mContext;
    private static GCDatabaseAdapter mDbAdapter;

    public static GCSavedSetDatabase SAVED_SET_DATABASE;
    public static GCPowerLevelDatabase POWER_LEVEL_DATABASE;

    public GCDatabaseHelper(Context context, String name,
                            SQLiteDatabase.CursorFactory factory, int version, GCDatabaseAdapter dbAdapter) {
        super(context, name, factory, version);
        mContext = context;
        mDbAdapter = dbAdapter;
        initializeTable(mDbAdapter);
    }

    private void initializeTable(GCDatabaseAdapter db_adapter) {
        SAVED_SET_DATABASE = new GCSavedSetDatabase(mContext, db_adapter);
        POWER_LEVEL_DATABASE = new GCPowerLevelDatabase(mContext, db_adapter);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //db.execSQL(SQL_CREATE_ENTRIES);
        SAVED_SET_DATABASE.createTable(db);
        POWER_LEVEL_DATABASE.createTable(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        int upgradeTo = oldVersion + 1;
        while (upgradeTo <= newVersion) {
            switch (upgradeTo) {
                case 2: //upgrade from 1 to 2
                    db.execSQL("ALTER TABLE " + SAVED_SET_DATABASE.TABLE_NAME +
                            " ADD COLUMN " + GCSavedSetDatabase.GCSavedSetEntry.SAVED_SET_CHIP + " " + TEXT_TYPE + ";");
                    break;
                case 3: //upgrade from 2 to 3
                    POWER_LEVEL_DATABASE.createTable(db);
                    break;
                default: //if case not shown, no changes made
                    break;
            }
            upgradeTo++;
        }
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}
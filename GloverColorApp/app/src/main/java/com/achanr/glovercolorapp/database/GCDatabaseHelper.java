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
    private static final int DATABASE_VERSION = 35;
    private static final String DATABASE_NAME = "SavedSet.db";
    static final String TEXT_TYPE = " TEXT";
    static final String INT_TYPE = " INT";
    static final String COMMA_SEP = ",";

    public GCSavedSetDatabase SAVED_SET_DATABASE;
    public GCPowerLevelDatabase POWER_LEVEL_DATABASE;
    public GCColorDatabase COLOR_DATABASE;
    public GCChipDatabase CHIP_DATABASE;
    public GCModeDatabase MODE_DATABASE;
    public GCCollectionDatabase COLLECTION_DATABASE;

    private static GCDatabaseHelper dbHelper;

    public static synchronized GCDatabaseHelper getInstance(Context context) {
        if (dbHelper == null) {
            dbHelper = new GCDatabaseHelper(context);
        }
        return dbHelper;
    }

    private GCDatabaseHelper(Context context) {
        super(context, GCDatabaseHelper.DATABASE_NAME, null, GCDatabaseHelper.DATABASE_VERSION);
        initializeTable(context, new GCDatabaseAdapter(this));
    }

    private void initializeTable(Context context, GCDatabaseAdapter db_adapter) {
        SAVED_SET_DATABASE = new GCSavedSetDatabase(context, db_adapter);
        POWER_LEVEL_DATABASE = new GCPowerLevelDatabase(db_adapter);
        COLOR_DATABASE = new GCColorDatabase(db_adapter);
        CHIP_DATABASE = new GCChipDatabase(db_adapter);
        MODE_DATABASE = new GCModeDatabase(db_adapter);
        COLLECTION_DATABASE = new GCCollectionDatabase(context, db_adapter);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //db.execSQL(SQL_CREATE_ENTRIES);
        SAVED_SET_DATABASE.createTable(db);
        POWER_LEVEL_DATABASE.createTable(db);
        COLOR_DATABASE.createTable(db);
        CHIP_DATABASE.createTable(db);
        MODE_DATABASE.createTable(db);
        COLLECTION_DATABASE.createTable(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        int upgradeTo = oldVersion + 1;
        while (upgradeTo <= newVersion) {
            switch (upgradeTo) {
                case 2: //upgrade from 1 to 2
                    db.execSQL("ALTER TABLE " + GCSavedSetDatabase.TABLE_NAME +
                            " ADD COLUMN " + GCSavedSetDatabase.GCSavedSetEntry.SAVED_SET_CHIP + " " + TEXT_TYPE + ";");
                    break;
                case 3: //upgrade from 2 to 3
                    POWER_LEVEL_DATABASE.createTable(db);
                    break;
                case 4: //upgrade from 3 to 4
                    COLOR_DATABASE.createTable(db);
                    CHIP_DATABASE.createTable(db);
                    MODE_DATABASE.createTable(db);
                    break;
                case 10: //upgrade from 9 to 10
                    db.execSQL("ALTER TABLE " + GCSavedSetDatabase.TABLE_NAME +
                            " ADD COLUMN " + GCSavedSetDatabase.GCSavedSetEntry.SAVED_SET_CUSTOM_COLORS + " " + TEXT_TYPE + ";");
                    break;
                case 21:
                    String[] ids = {"SP2_REV1", "SP2_REV2", "SP3_REV1", "SP3_REV2"};
                    db.delete(GCSavedSetDatabase.TABLE_NAME, GCSavedSetDatabase.GCSavedSetEntry.SAVED_SET_CHIP + " IN (?, ?, ?, ?)", ids);
                case 22:
                    db.execSQL("delete from " + GCSavedSetDatabase.TABLE_NAME + ";");
                    break;
                case 23:
                    COLLECTION_DATABASE.createTable(db);
                    break;
                case 32:
                    db.execSQL("ALTER TABLE " + GCSavedSetDatabase.TABLE_NAME +
                            " ADD COLUMN " + GCSavedSetDatabase.GCSavedSetEntry.SAVED_SET_DESCRIPTION + " " + TEXT_TYPE + ";");
                    break;
                case 34:
                    db.execSQL("CREATE TABLE " +
                            "backupTable" +
                            " (" + GCCollectionDatabase.GCCollectionEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                            GCCollectionDatabase.GCCollectionEntry.COLLECTION_TITLE_KEY + GCDatabaseHelper.TEXT_TYPE + GCDatabaseHelper.COMMA_SEP +
                            GCCollectionDatabase.GCCollectionEntry.COLLECTION_DESC_KEY + GCDatabaseHelper.TEXT_TYPE + GCDatabaseHelper.COMMA_SEP +
                            GCCollectionDatabase.GCCollectionEntry.COLLECTION_SETS_KEY + GCDatabaseHelper.TEXT_TYPE +
                            " );");
                    db.execSQL("INSERT INTO backupTable(" +
                            GCCollectionDatabase.GCCollectionEntry.COLLECTION_TITLE_KEY + ", " +
                            GCCollectionDatabase.GCCollectionEntry.COLLECTION_DESC_KEY + ", " +
                            GCCollectionDatabase.GCCollectionEntry.COLLECTION_SETS_KEY +
                            ") SELECT " +
                            GCCollectionDatabase.GCCollectionEntry.COLLECTION_TITLE_KEY + ", " +
                            GCCollectionDatabase.GCCollectionEntry.COLLECTION_DESC_KEY + ", " +
                            GCCollectionDatabase.GCCollectionEntry.COLLECTION_SETS_KEY + " FROM " + GCCollectionDatabase.TABLE_NAME + ";");
                    db.execSQL("DROP TABLE " + GCCollectionDatabase.TABLE_NAME + ";");
                    db.execSQL("ALTER TABLE backupTable RENAME TO " + GCCollectionDatabase.TABLE_NAME + ";");
                    break;
                default: //if case not shown, no changes made
                    break;
            }
            upgradeTo++;
        }

        //for each update, refresh the color, chip, and mode databases
        db.execSQL("delete from " + GCColorDatabase.TABLE_NAME + ";");
        db.execSQL("delete from " + GCChipDatabase.TABLE_NAME + ";");
        db.execSQL("delete from " + GCModeDatabase.TABLE_NAME + ";");
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}

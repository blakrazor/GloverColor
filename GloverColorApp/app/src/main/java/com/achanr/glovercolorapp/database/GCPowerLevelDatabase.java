package com.achanr.glovercolorapp.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;
import android.util.Log;

import com.achanr.glovercolorapp.models.GCPowerLevel;

import java.util.ArrayList;

/**
 * Glover Color App Project
 *
 * @author Andrew Chanrasmi
 * @created 3/1/16 11:05 AM
 */
public class GCPowerLevelDatabase extends GCAbstractDatabase {

    static String TABLE_NAME = "POWER_LEVEL_TBL";
    static final String CREATE_POWER_LEVEL_DATABASE =
            "CREATE TABLE IF NOT EXISTS " +
                    TABLE_NAME +
                    " (" + GCPowerLevelEntry._ID + " INTEGER PRIMARY KEY," +
                    GCPowerLevelEntry.POWER_LEVEL_TITLE_KEY + GCDatabaseHelper.TEXT_TYPE + GCDatabaseHelper.COMMA_SEP +
                    GCPowerLevelEntry.POWER_LEVEL_ABBREV_KEY + GCDatabaseHelper.TEXT_TYPE + GCDatabaseHelper.COMMA_SEP +
                    GCPowerLevelEntry.POWER_LEVEL_VALUE_KEY + GCDatabaseHelper.INT_TYPE +
                    " );";

    private Context mContext;

    public static class GCPowerLevelEntry implements BaseColumns {
        public static final String POWER_LEVEL_TITLE_KEY = "TITLE";
        public static final String POWER_LEVEL_ABBREV_KEY = "ABBREV";
        public static final String POWER_LEVEL_VALUE_KEY = "VALUE";
    }

    public GCPowerLevelDatabase(Context context, GCDatabaseAdapter databaseAdapter) {
        mContext = context;
        GCPowerLevelDatabase.db_adapter = databaseAdapter;
    }

    @Override
    public void createTable(SQLiteDatabase db) {
        synchronized (GCDatabaseAdapter.Lock) {
            db.execSQL(CREATE_POWER_LEVEL_DATABASE);
        }
    }

    public long insertData(GCPowerLevel powerLevel) {
        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(GCPowerLevelEntry.POWER_LEVEL_TITLE_KEY, powerLevel.getTitle());
        values.put(GCPowerLevelEntry.POWER_LEVEL_ABBREV_KEY, powerLevel.getAbbreviation());
        values.put(GCPowerLevelEntry.POWER_LEVEL_VALUE_KEY, powerLevel.convertValueToInt());

        return db_adapter.insertEntryInDB(TABLE_NAME, values);
    }

    public void clearTable() {
        db_adapter.clearTable(TABLE_NAME);
    }

    public ArrayList<GCPowerLevel> readData() {
        ArrayList<GCPowerLevel> powerLevelArrayList = new ArrayList<>();

        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        String[] projection = {
                GCPowerLevelEntry._ID,
                GCPowerLevelEntry.POWER_LEVEL_TITLE_KEY,
                GCPowerLevelEntry.POWER_LEVEL_ABBREV_KEY,
                GCPowerLevelEntry.POWER_LEVEL_VALUE_KEY
        };

        // How you want the results sorted in the resulting Cursor
        String sortOrder =
                GCPowerLevelEntry.POWER_LEVEL_VALUE_KEY + " DESC";

        Cursor mCursor = null;
        try {
            mCursor = db_adapter.getEntryFromDB(
                    TABLE_NAME,  // The table to query
                    projection,                               // The columns to return
                    null,                                // The columns for the WHERE clause
                    null,                            // The values for the WHERE clause
                    null,                                     // don't group the rows
                    null,                                     // don't filter by row groups
                    sortOrder                                 // The sort order
            );

            if (mCursor != null) {
                if (mCursor.moveToFirst()) {
                    do {
                        GCPowerLevel powerLevel = new GCPowerLevel();

                        String title = mCursor.getString(mCursor.getColumnIndex(GCPowerLevelEntry.POWER_LEVEL_TITLE_KEY));
                        String abbrev = mCursor.getString(mCursor.getColumnIndex(GCPowerLevelEntry.POWER_LEVEL_ABBREV_KEY));
                        int value = mCursor.getInt(mCursor.getColumnIndex(GCPowerLevelEntry.POWER_LEVEL_VALUE_KEY));

                        powerLevel.setTitle(title);
                        powerLevel.setAbbreviation(abbrev);
                        powerLevel.setValue((float) value / 100);

                        powerLevelArrayList.add(powerLevel);
                    } while (mCursor.moveToNext());
                }
            }
        } catch (Exception e) {
            Log.e(GCPowerLevelDatabase.class.getSimpleName(), e.getMessage());
        } finally {
            if (mCursor != null) {
                mCursor.close();
            }
            if (db_adapter.isOpen())
                db_adapter.close();
        }

        return powerLevelArrayList;
    }

    public boolean deleteData(GCPowerLevel powerLevel) {
        // Define 'where' part of query.
        String selection = GCPowerLevelEntry.POWER_LEVEL_TITLE_KEY + "=?";
        // Specify arguments in placeholder order.
        String[] selectionArgs = {String.valueOf(powerLevel.getTitle())};
        // Issue SQL statement.
        return db_adapter.deleteRow(TABLE_NAME, selection, selectionArgs) > 0;
    }

    public int updateData(GCPowerLevel oldPowerLevel, float newPowerValue) {

        // New value for one column
        ContentValues values = new ContentValues();
        values.put(GCPowerLevelEntry.POWER_LEVEL_TITLE_KEY, oldPowerLevel.getTitle());
        values.put(GCPowerLevelEntry.POWER_LEVEL_ABBREV_KEY, oldPowerLevel.getAbbreviation());
        values.put(GCPowerLevelEntry.POWER_LEVEL_VALUE_KEY, (int) (newPowerValue * 100));

        // Which row to update, based on the ID
        String selection = GCPowerLevelEntry.POWER_LEVEL_TITLE_KEY + "=?";
        String[] selectionArgs = {String.valueOf(oldPowerLevel.getTitle())};

        return db_adapter.updateEntryInDB(TABLE_NAME, values, selection, selectionArgs);
    }
}

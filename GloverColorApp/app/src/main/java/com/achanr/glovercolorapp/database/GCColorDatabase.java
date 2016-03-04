package com.achanr.glovercolorapp.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;
import android.util.Log;

import com.achanr.glovercolorapp.models.GCColor;

import java.util.ArrayList;

/**
 * @author Andrew Chanrasmi
 * @created 3/3/16 1:58 PM
 */
public class GCColorDatabase extends GCAbstractDatabase {

    static String TABLE_NAME = "COLOR_TBL";
    static final String CREATE_COLOR_DATABASE =
            "CREATE TABLE IF NOT EXISTS " +
                    TABLE_NAME +
                    " (" + GCColorEntry._ID + " INTEGER PRIMARY KEY," +
                    GCColorEntry.COLOR_TITLE_KEY + GCDatabaseHelper.TEXT_TYPE + GCDatabaseHelper.COMMA_SEP +
                    GCColorEntry.COLOR_ABBREV_KEY + GCDatabaseHelper.TEXT_TYPE + GCDatabaseHelper.COMMA_SEP +
                    GCColorEntry.RED_VALUE_KEY + GCDatabaseHelper.INT_TYPE + GCDatabaseHelper.COMMA_SEP +
                    GCColorEntry.GREEN_VALUE_KEY + GCDatabaseHelper.INT_TYPE + GCDatabaseHelper.COMMA_SEP +
                    GCColorEntry.BLUE_VALUE_KEY + GCDatabaseHelper.INT_TYPE +
                    " );";

    private Context mContext;

    public static class GCColorEntry implements BaseColumns {
        public static final String COLOR_TITLE_KEY = "COLOR_TITLE";
        public static final String COLOR_ABBREV_KEY = "COLOR_ABBREV";
        public static final String RED_VALUE_KEY = "RED_VALUE";
        public static final String GREEN_VALUE_KEY = "GREEN_VALUE";
        public static final String BLUE_VALUE_KEY = "BLUE_VALUE";
    }

    public GCColorDatabase(Context context, GCDatabaseAdapter databaseAdapter) {
        mContext = context;
        GCColorDatabase.db_adapter = databaseAdapter;
    }

    @Override
    public void createTable(SQLiteDatabase db) {
        synchronized (GCDatabaseAdapter.Lock) {
            db.execSQL(CREATE_COLOR_DATABASE);
        }
    }

    public long insertData(GCColor color) {
        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(GCColorEntry.COLOR_TITLE_KEY, color.getTitle());
        values.put(GCColorEntry.COLOR_ABBREV_KEY, color.getAbbreviation());
        values.put(GCColorEntry.RED_VALUE_KEY, color.getRGBValues()[0]);
        values.put(GCColorEntry.GREEN_VALUE_KEY, color.getRGBValues()[1]);
        values.put(GCColorEntry.BLUE_VALUE_KEY, color.getRGBValues()[2]);

        return db_adapter.insertEntryInDB(TABLE_NAME, values);
    }

    public void clearTable() {
        db_adapter.clearTable(TABLE_NAME);
    }

    public ArrayList<GCColor> getAllData() {
        ArrayList<GCColor> colorArrayList = new ArrayList<>();

        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        String[] projection = {
                GCColorEntry._ID,
                GCColorEntry.COLOR_TITLE_KEY,
                GCColorEntry.COLOR_ABBREV_KEY,
                GCColorEntry.RED_VALUE_KEY,
                GCColorEntry.GREEN_VALUE_KEY,
                GCColorEntry.BLUE_VALUE_KEY
        };

        // How you want the results sorted in the resulting Cursor
        String sortOrder =
                GCColorEntry._ID + " DESC";

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
                        String title = mCursor.getString(mCursor.getColumnIndex(GCColorEntry.COLOR_TITLE_KEY));
                        String abbrev = mCursor.getString(mCursor.getColumnIndex(GCColorEntry.COLOR_ABBREV_KEY));
                        int redValue = mCursor.getInt(mCursor.getColumnIndex(GCColorEntry.RED_VALUE_KEY));
                        int greenValue = mCursor.getInt(mCursor.getColumnIndex(GCColorEntry.GREEN_VALUE_KEY));
                        int blueValue = mCursor.getInt(mCursor.getColumnIndex(GCColorEntry.BLUE_VALUE_KEY));

                        colorArrayList.add(new GCColor(title, abbrev, new int[]{redValue, greenValue, blueValue}));
                    } while (mCursor.moveToNext());
                }
            }
        } catch (Exception e) {
            Log.e(GCColorDatabase.class.getSimpleName(), e.getMessage());
        } finally {
            if (mCursor != null) {
                mCursor.close();
            }
            if (db_adapter.isOpen())
                db_adapter.close();
        }

        return colorArrayList;
    }

    public boolean deleteData(GCColor color) {
        // Define 'where' part of query.
        String selection = GCColorEntry.COLOR_TITLE_KEY + "=?";
        // Specify arguments in placeholder order.
        String[] selectionArgs = {String.valueOf(color.getTitle())};
        // Issue SQL statement.
        return db_adapter.deleteRow(TABLE_NAME, selection, selectionArgs) > 0;
    }

    public int updateData(GCColor oldColor, GCColor newColor) {

        // New value for one column
        ContentValues values = new ContentValues();
        values.put(GCColorEntry.COLOR_TITLE_KEY, newColor.getTitle());
        values.put(GCColorEntry.COLOR_ABBREV_KEY, newColor.getAbbreviation());
        values.put(GCColorEntry.RED_VALUE_KEY, newColor.getRGBValues()[0]);
        values.put(GCColorEntry.GREEN_VALUE_KEY, newColor.getRGBValues()[1]);
        values.put(GCColorEntry.BLUE_VALUE_KEY, newColor.getRGBValues()[2]);

        // Which row to update, based on the ID
        String selection = GCColorEntry.COLOR_TITLE_KEY + "=?";
        String[] selectionArgs = {String.valueOf(oldColor.getTitle())};

        return db_adapter.updateEntryInDB(TABLE_NAME, values, selection, selectionArgs);
    }
}
